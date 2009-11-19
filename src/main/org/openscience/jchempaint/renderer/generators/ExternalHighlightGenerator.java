/* $Revision: $ $Author:  $ $Date$
 *
 * Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.LineElement;
import org.openscience.jchempaint.renderer.elements.OvalElement;

/**
 * @cdk.module rendercontrol
 */
public class ExternalHighlightGenerator implements IGenerator {

    public ExternalHighlightGenerator() {}

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        ElementGroup group = new ElementGroup();
        ac = model.getExternalSelectedPart();
        if (ac == null)
            return group;
        for (IAtom atom : ac.atoms()) {
            group.add(generate(atom, model));
        }
        for (IBond bond : ac.bonds()) {
            group.add(generate(bond, model));
        }
        return group;
    }

    public IRenderingElement generate(IAtom atom, RendererModel model) {
        Point2d p = atom.getPoint2d();
        double r = model.getHighlightDistance() / model.getScale();
        return new OvalElement(p.x, p.y, r, model.getExternalHighlightColor());
    }

    public IRenderingElement generate(IBond bond, RendererModel model) {
        Point2d p1 = bond.getAtom(0).getPoint2d();
        Point2d p2 = bond.getAtom(1).getPoint2d();
        double w = model.getBondWidth() / model.getScale();
        return new LineElement(
                p1.x, p1.y, p2.x, p2.y, w, model.getExternalHighlightColor());
    }

    public List<IGeneratorParameter> getParameters() {
        // TODO Auto-generated method stub
        return null;
    }

}
