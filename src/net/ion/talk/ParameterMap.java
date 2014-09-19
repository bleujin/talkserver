package net.ion.talk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;

import org.apache.commons.fileupload.FileItem;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpRequest;

public class ParameterMap {

	public static final ParameterMap BLANK = ParameterMap.create(MapUtil.EMPTY);
	private MultivaluedMap<String, String> inner;
	private ParameterMap(MultivaluedMap<String, String> inner) {
		this.inner = inner ;
	}

	public static ParameterMap create(){
		return new ParameterMap(new MultivaluedMapImpl()) ;
	}
	public static ParameterMap create(Map<String, ? extends Object> map){
		MultivaluedMapImpl<String, String> mmap = new MultivaluedMapImpl<String, String>() ; 
		for (String key : map.keySet()) {
			mmap.add(key, ObjectUtil.toString(map.get(key))) ;
		}
		
		return new ParameterMap(mmap) ;
	}

	public static ParameterMap create(HttpRequest request) {
		return new ParameterMap(request.getDecodedFormParameters());
	}
	
	public String asString(String name){
		return ObjectUtil.toString(inner.getFirst(name)) ;
	}
	
	public int asInt(String name){
		return NumberUtil.toInt(asString(name), 0) ;
	}

	public long asLong(String name){
		return NumberUtil.toLong(asString(name), 0L) ;
	}
	
	public FileItem asFileItem(String name){
		final Object item = inner.get(name);
		if (item instanceof FileItem){
			FileItem fitem = (FileItem) item;
			return fitem ;
		} else {
			throw new IllegalArgumentException(name + " : not fileitem") ;
		}
		
	}

	public ParameterMap reset(String name, String... value){
		inner.remove(name) ;
		return set(name, value) ;
	}

	public ParameterMap set(String name, String... values){
		for (String value : values) {
			inner.add(name, value) ;
		}
		return this ;
	}
	

	public InputStream asStream(String name) throws IOException{
		final Object item = inner.get(name);
		if (item instanceof FileItem){
			FileItem fitem = (FileItem) item;
			return fitem.getInputStream() ;
		} else {
			return new ByteArrayInputStream(asString(name).getBytes("UTF-8")) ;
		}
	}
	
	public JsonObject asJson(){
		return JsonParser.fromMap(inner) ;
	}

	
	public String asString(String name, int index){
		
		return ObjectUtil.toString(inner.get(name).get(index)) ;
	}
	
	public int asInt(String name, int index){
		return NumberUtil.toInt(asString(name, index), 0) ;
	}

	public long asLong(String name, int index){
		return NumberUtil.toLong(asString(name, index), 0L) ;
	}
	

	public String[] asStrings(String name){
		List<String> list = inner.get(name);
		if (list == null) return new String[0] ;
		return (String[]) list.toArray(new String[0]) ;
	}

	public ParameterMap addValue(String name, String value) {
		inner.add(name, value);
		return this ;
	}


	
	

}
