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

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.TextElement;

/**
 * @cdk.module rendercontrol
 */
public class AtomContainerTitleGenerator implements IGenerator {

    public AtomContainerTitleGenerator() {}

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        if(ac.getProperty(CDKConstants.TITLE)==null)
            return null;
        double d = model.getBondLength() / model.getScale()/2;
        Rectangle2D totalBounds = Renderer.calculateBounds(ac);
        
        ElementGroup diagram = new ElementGroup();
        double minX = totalBounds.getMinX();
        double minY = totalBounds.getMinY();
        double maxX = totalBounds.getMaxX();
        double maxY = totalBounds.getMaxY();
        Color c = model.getForeColor();
        diagram.add(new TextElement(
                        (minX+maxX)/2, maxY+d, (String)ac.getProperty(CDKConstants.TITLE), c));
        return diagram;
    }
    
    public List<IGeneratorParameter> getParameters() {
        // TODO Auto-generated method stub
        return null;
    }
}
