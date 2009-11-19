/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
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

import java.awt.Color;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.OvalElement;

/**
 * @cdk.module rendercontrol
 */
public class HighlightAtomGenerator extends BasicAtomGenerator 
                                implements IGenerator {

    public HighlightAtomGenerator() {}
    
    private boolean shouldHighlight(IAtom atom, RendererModel model) {
        return !super.isHydrogen(atom) || model.getShowExplicitHydrogens();
    }

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        IAtom atom = model.getHighlightedAtom();
        if (atom != null && shouldHighlight(atom, model)) {
            Point2d p = atom.getPoint2d();
            
            // the element size has to be scaled to model space 
            // so that it can be scaled back to screen space...
            double radius = model.getHighlightDistance() / model.getScale();
            radius /= 2.0;
            boolean filled = model.getHighlightShapeFilled();
            Color highlightColor = model.getHoverOverColor(); 
            return new OvalElement(p.x, p.y, radius, filled, highlightColor);
        }
        
        return new ElementGroup();
    }
}
