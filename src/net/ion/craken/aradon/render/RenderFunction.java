package net.ion.craken.aradon.render;

import net.ion.craken.node.ReadNode;

import org.restlet.representation.Representation;

import com.google.common.base.Function;

public interface RenderFunction {

	public Function<ReadNode, ? extends Representation> getTransformer(final RenderRequest renderRequest);
	
}
