package org.openscience.jchempaint.renderer.generators;

import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;

public interface IReactionSetGenerator {
	
    public IRenderingElement generate(IReactionSet reactionSet, RendererModel model);


}
