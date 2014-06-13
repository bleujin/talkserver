package net.ion.talk.monitor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import net.ion.craken.listener.AsyncCDDHandler;
import net.ion.craken.listener.CDDHandler;
import net.ion.craken.listener.CDDModifiedEvent;
import net.ion.craken.listener.CDDRemovedEvent;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.Workspace;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.TreeNodeKey;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.AbstractEventSourceResource;
import net.ion.nradon.EventSourceConnection;
import net.ion.nradon.EventSourceMessage;

public class TalkMonitor extends AbstractEventSourceResource{

	private CopyOnWriteArrayList<Element> elements = new CopyOnWriteArrayList<Element>() ;
	private Workspace workspace; 

	private TalkMonitor(Workspace workspace) {
		this.workspace = workspace ;
	}


	public static TalkMonitor create(ReadSession session) {
		return new TalkMonitor(session.workspace());
	}

	@Override
	public void onClose(EventSourceConnection econn) throws Exception {
		for (Element ele : elements) {
			if (ele.econn() == econn) {
				workspace.cddm().remove(ele.handler()) ;
			}
		}
		
	}

	@Override
	public void onOpen(EventSourceConnection econn) throws Exception {
		String pathPattern = StringUtil.substringAfter(econn.httpRequest().uri(), "/event/") ; // /event/room/@roomId
		
		Element element = new Element(econn, "/" + pathPattern.replaceAll("\\$(\\w*)\\$", "{$1}")) ; //   StringUtil.replaceEach(pathPattern, new String[]{"$", "$"}, new String[]{"{", "}"})
		elements.add(element) ;

		workspace.cddm().add(element.handler()) ;
	}


}

class Element {
	private EventSourceConnection econn ;
	private CDDHandler chandler ;
	
	Element(final EventSourceConnection econn, final String pathPattern){
		this.econn= econn ;
		this.chandler = new AsyncCDDHandler() {
			@Override
			public String pathPattern() {
				return pathPattern;
			}
			
			@Override
			public TransactionJob<Void> modified(final Map<String, String> datas, final CDDModifiedEvent event) {
				return new TransactionJob<Void>() {
					@Override
					public Void handle(WriteSession wsession) throws Exception {
						econn.send(createMessage(datas, event)) ;
						return null;
					}
				};
			}
			
			@Override
			public TransactionJob<Void> deleted(final Map<String, String> datas, final CDDRemovedEvent event) {
				return new TransactionJob<Void>() {
					@Override
					public Void handle(WriteSession wsession) throws Exception {
						econn.send(createMessage(datas, event)) ;
						return null;
					}

				};
			}
		};
	}
	
	private EventSourceMessage createMessage(final Map<String, String> datas, final CDDModifiedEvent event){
		JsonObject result = new JsonObject() ;
		result.add("pattern", JsonObject.fromObject(datas) );
		result.add("key", new JsonObject().put("fqn", event.getKey().fqnString()).put("action", event.getKey().action().toString()));
		JsonObject values = new JsonObject() ;
		for (Entry<PropertyId, PropertyValue> entry : event.getValue().entrySet()) {
			values.addProperty(entry.getKey().idString(), entry.getValue().asObject().toString());
		}
		result.add("values", values);
		
		return new EventSourceMessage(result.toString()) ;
	}
	
	private EventSourceMessage createMessage(Map<String, String> datas, CDDRemovedEvent event) {
		TreeNodeKey key = event.getKey() ;
		
		return new EventSourceMessage(datas.toString()) ;
	}
	
	EventSourceConnection econn(){
		return econn ;
	}
	
	CDDHandler handler(){
		return chandler ;
	}
}