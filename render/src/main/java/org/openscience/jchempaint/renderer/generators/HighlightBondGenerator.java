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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.path.Close;
import org.openscience.cdk.renderer.elements.path.LineTo;
import org.openscience.cdk.renderer.elements.path.MoveTo;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.jchempaint.renderer.RenderingParameters;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.Color;
import java.util.Arrays;

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
            r /= 1.5;
            Color hColor = jcpModel.getHoverOverColor();
            Point2d p = bond.get2DCenter();
            boolean filled = jcpModel.getHighlightShapeFilled();


            if (jcpModel.getHighlightBondShape() == RenderingParameters.AtomShape.OVAL) {
                return new OvalElement(p.x, p.y, r, filled, hColor);
            } else if (jcpModel.getHighlightBondShape() == RenderingParameters.AtomShape.SQUARE)  {
                Point2d p1 = bond.getBegin().getPoint2d();
                Point2d p2 = bond.getEnd().getPoint2d();
                Vector2d v = new Vector2d(p1.x - p2.x, p1.y - p2.y);
                v.normalize();
                v.scale(r);

                p1 = new Point2d(p.x - v.x, p.y - v.y);
                p2 = new Point2d(p.x + v.x, p.y + v.y);

                Vector2d o = new Vector2d(-v.y, v.x);

                GeneralPath rectangle = new GeneralPath(Arrays.asList(
                        new MoveTo(p1.x - o.x, p1.y - o.y),
                        new LineTo(p1.x + o.x, p1.y + o.y),
                        new LineTo(p2.x + o.x, p2.y + o.y),
                        new LineTo(p2.x - o.x, p2.y - o.y),
                        new Close()), hColor);
                if (filled)
                    return rectangle;
                else
                    return rectangle.outline(1 / ((JChemPaintRendererModel) model).getScale());
            }
        }
        return new ElementGroup();
    }
}
