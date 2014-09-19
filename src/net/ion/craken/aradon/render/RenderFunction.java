package net.ion.craken.aradon.render;

import net.ion.craken.node.ReadNode;

import com.google.common.base.Function;

public interface RenderFunction {

	public Function<ReadNode, ? extends Object> getTransformer(final RenderRequest renderRequest);
	
}
