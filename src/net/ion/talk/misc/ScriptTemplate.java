package net.ion.talk.misc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.ion.craken.node.ReadNode;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;

import com.google.common.base.Function;

public class ScriptTemplate implements Function<ReadNode, String> {

	private Engine engine;
	private List<String> fnNames;
	private String tplName;

	public ScriptTemplate(Engine engine, List<String> fnNames, String tplName) {
		this.engine = engine;
		this.fnNames = fnNames ;
		this.tplName = tplName ;
	}
	
	public static ScriptTemplate test(Engine engine, List<String> fnNames, String tplName){
		return new ScriptTemplate(engine, fnNames, tplName) ;
	}

	@Override
	public String apply(ReadNode node) {

        try {
			InputStream input = ScriptTemplate.class.getResourceAsStream(tplName);
			if (input == null) throw new FileNotFoundException(tplName) ;
			return engine.transform(IOUtil.toStringWithClose(input), MapUtil.<String, Object> chainMap().put("self", node).put("fnNames", fnNames).toMap());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
