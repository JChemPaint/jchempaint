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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.OvalElement;

/**
 * @cdk.module rendercontrol
 */
public class HighlightBondGenerator extends BasicBondGenerator 
                                    implements IGenerator {

    public HighlightBondGenerator() {}
    
    private boolean shouldHighlight(IBond bond, RendererModel model) {
        return !super.bindsHydrogen(bond) || model.getShowExplicitHydrogens();
    }

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        IBond bond = model.getHighlightedBond();
        if (bond != null && shouldHighlight(bond, model)) {
            super.ringSet = super.getRingSet(ac);
            
            double r = model.getHighlightDistance() / model.getScale();
            r /= 2.0;
            Color hColor = model.getHoverOverColor();
            Point2d p = bond.get2DCenter();
            boolean filled = model.getHighlightShapeFilled();
            return new OvalElement(p.x, p.y, r, filled, hColor);
        }
        return new ElementGroup();
    }
}
