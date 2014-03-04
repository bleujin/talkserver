package net.ion.craken.aradon.render;

import com.google.common.base.Function;
import net.ion.craken.node.ReadNode;
import org.restlet.representation.Representation;

public interface RenderFunction {

	public Function<ReadNode, ? extends Representation> getTransformer(final RenderRequest renderRequest);
	
}
