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
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;

/**
 * @cdk.module rendercontrol
 */
public class HighlightBondGenerator extends BasicBondGenerator {

    public HighlightBondGenerator() {}
    
    private boolean shouldHighlight(IBond bond, JChemPaintRendererModel model) {
        return !super.bindsHydrogen(bond) || model.getShowExplicitHydrogens();
    }

    @Override
    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        JChemPaintRendererModel jcpModel = (JChemPaintRendererModel) model;
        IBond bond = model.getHighlightedBond();
        if (bond != null && shouldHighlight(bond, jcpModel)) {
            super.ringSet = super.getRingSet(ac);
            
            double r = jcpModel.getHighlightDistance() / jcpModel.getScale();
            r /= 2.0;
            Color hColor = jcpModel.getHoverOverColor();
            Point2d p = bond.get2DCenter();
            boolean filled = jcpModel.getHighlightShapeFilled();
            return new OvalElement(p.x, p.y, r, filled, hColor);
        }
        return new ElementGroup();
    }
}
