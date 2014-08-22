/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *               2009  Stefan Kuhn <shk3@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.renderer.generators;


import java.awt.geom.Rectangle2D;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.RectangleElement;


/**
 * Produce a bounding rectangle for various chem objects.
 * 
 * @author maclean
 * @cdk.module renderextra
 */
public class BoundsGenerator implements IReactionGenerator {
    
    public BoundsGenerator() {}
    
    public IRenderingElement generate(IReaction reaction, JChemPaintRendererModel model) {
        ElementGroup elementGroup = new ElementGroup();
        IAtomContainerSet reactants = reaction.getReactants();
        if (reactants != null) {
            elementGroup.add(this.generate(reactants, model));
        }
        
        IAtomContainerSet products = reaction.getProducts();
        if (products != null) {
            elementGroup.add(this.generate(products, model));
        }
        
        return elementGroup;
    }
    
    public IRenderingElement generate(IAtomContainer molecule, JChemPaintRendererModel model) {
        Rectangle2D bounds = Renderer.calculateBounds(molecule);
        return new RectangleElement(bounds.getMinX(),
                bounds.getMaxY(),
                bounds.getMaxX(),
                bounds.getMinY(),
                model.getBoundsColor());
    }
    
    public IRenderingElement generate(
            IAtomContainerSet moleculeSet, JChemPaintRendererModel model) {
        Rectangle2D totalBounds = Renderer.calculateBounds(moleculeSet);
        
        return new RectangleElement(totalBounds.getMinX(),
                                    totalBounds.getMaxY(),
                                    totalBounds.getMaxX(),
                                    totalBounds.getMinY(),
                                    model.getBoundsColor());
    }

}
