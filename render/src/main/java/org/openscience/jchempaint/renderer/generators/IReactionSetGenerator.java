package org.openscience.jchempaint.renderer.generators;

import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;

public interface IReactionSetGenerator {
	
    public IRenderingElement generate(IReactionSet reactionSet, JChemPaintRendererModel model);


}
