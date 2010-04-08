/* $Revision: $ $Author:  $ $Date$
 *
 * Copyright (C) 2009  Stefan Kuhn <stefan.kuhn@ebi.ac.uk>
 *
 * Contact: cdk-jchempaint@lists.sourceforge.net
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
package org.openscience.jchempaint.controller;

import java.awt.Color;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ArrowElement;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.generators.IGenerator;
import org.openscience.jchempaint.renderer.generators.IGeneratorParameter;
import org.openscience.jchempaint.renderer.generators.ReactionArrowGenerator;

/**
 * Draws a phantom arrow in ControllerHub
 */
public class PhantomArrowGenerator implements IGenerator{

    ControllerHub hub;


    public PhantomArrowGenerator(){
        
    }
    
    public void setControllerHub(ControllerHub hub) {
        this.hub = hub;
    }

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        if(hub.getPhantomArrow()[0]==null || hub.getPhantomArrow()[1]==null)
            return new ElementGroup();
        else
            return new ArrowElement(hub.getPhantomArrow()[0].x,
                    hub.getPhantomArrow()[0].y,
                    hub.getPhantomArrow()[1].x,
                    hub.getPhantomArrow()[1].y,
                    1 / model.getScale(),true,
                    Color.GRAY);
    }

    public List<IGeneratorParameter> getParameters() {
        // TODO Auto-generated method stub
        return null;
    }
}
