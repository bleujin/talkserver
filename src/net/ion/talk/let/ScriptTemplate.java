package net.ion.talk.let;

import com.google.common.base.Function;
import net.ion.craken.node.ReadNode;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ScriptTemplate implements Function<ReadNode, String> {

	private Engine engine;

	ScriptTemplate(Engine engine) {
		this.engine = engine;
	}
	
	public static ScriptTemplate test(Engine engine){
		return new ScriptTemplate(engine) ;
	}

	@Override
	public String apply(ReadNode node) {

        try {
			InputStream input = ScriptTemplate.class.getResourceAsStream("viewscript.tpl");
			if (input == null) throw new FileNotFoundException("viewscript.tpl") ;
			return engine.transform(IOUtil.toStringWithClose(input), MapUtil.<String, Object> create("self", node));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
