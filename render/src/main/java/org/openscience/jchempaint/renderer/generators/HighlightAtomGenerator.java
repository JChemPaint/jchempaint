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
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;

/**
 * @cdk.module rendercontrol
 */
public class HighlightAtomGenerator extends BasicAtomGenerator {

    public HighlightAtomGenerator() {}
    
    private boolean shouldHighlight(IAtom atom, JChemPaintRendererModel model) {
        return !super.isHydrogen(atom) || model.getShowExplicitHydrogens();
    }

    @Override
    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        JChemPaintRendererModel jcpModel = (JChemPaintRendererModel) model;
        IAtom atom = model.getHighlightedAtom();
        if (atom != null && shouldHighlight(atom, jcpModel)) {
            Point2d p = atom.getPoint2d();
            
            // the element size has to be scaled to model space 
            // so that it can be scaled back to screen space...
            double radius = jcpModel.getHighlightDistance() / jcpModel.getScale();
            radius /= 1.5;
            boolean filled = jcpModel.getHighlightShapeFilled();
            Color highlightColor = jcpModel.getHoverOverColor(); 
            return new OvalElement(p.x, p.y, radius, filled, highlightColor);
        }
        
        return new ElementGroup();
    }
}
