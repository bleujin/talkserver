package net.ion.talk.let;

import com.google.common.base.Function;

import net.ion.craken.node.ReadNode;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ScriptTemplate implements Function<ReadNode, String> {

	private Engine engine;
	private List<String> fnNames;

	ScriptTemplate(Engine engine, List<String> fnNames) {
		this.engine = engine;
		this.fnNames = fnNames ;
	}
	
	public static ScriptTemplate test(Engine engine, List<String> fnNames){
		return new ScriptTemplate(engine, fnNames) ;
	}

	@Override
	public String apply(ReadNode node) {

        try {
			InputStream input = ScriptTemplate.class.getResourceAsStream("viewscript.tpl");
			if (input == null) throw new FileNotFoundException("viewscript.tpl") ;
			return engine.transform(IOUtil.toStringWithClose(input), MapUtil.<String, Object> chainMap().put("self", node).put("fnNames", fnNames).toMap());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
