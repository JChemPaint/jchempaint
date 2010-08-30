/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.action;

import java.awt.event.ActionEvent;

import org.openscience.jchempaint.controller.AddRingModule;
import org.openscience.jchempaint.controller.AtomAtomMappingModule;
import org.openscience.jchempaint.controller.ChainModule;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IControllerModule;
import org.openscience.jchempaint.controller.ReactionArrowModule;
import org.openscience.jchempaint.controller.Rotate3DModule;
import org.openscience.jchempaint.controller.RotateModule;
import org.openscience.jchempaint.controller.SelectLassoModule;
import org.openscience.jchempaint.controller.SelectSquareModule;

/**
 * JChemPaint menu actions
 * 
 */
public class ChangeModeAction extends JCPAction {

    private static final long serialVersionUID = -4056416630614934238L;

    public void actionPerformed(ActionEvent e) {
        ControllerHub hub = jcpPanel.get2DHub();
        IControllerModule newActiveModule=null;
        if (type.equals("lasso")) {
        	newActiveModule=new SelectLassoModule(hub);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("select")) {
        	newActiveModule=new SelectSquareModule(hub);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("triangle")) {
        	newActiveModule=new AddRingModule(hub, 3, false);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("square")) {
        	newActiveModule=new AddRingModule(hub, 4, false);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("pentagon")) {
        	newActiveModule=new AddRingModule(hub, 5, false);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("hexagon")) {
        	newActiveModule=new AddRingModule(hub, 6, false);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("heptagon")) {
        	newActiveModule=new AddRingModule(hub, 7, false);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("octagon")) {
        	newActiveModule=new AddRingModule(hub, 8, false);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("benzene")) {
        	newActiveModule=new AddRingModule(hub, 6, true);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("atomatommapping")) {
        	newActiveModule=new AtomAtomMappingModule(hub);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("rotate")) {
        	newActiveModule=new RotateModule(hub);
        } else if (type.equals("rotate3d")) {
        	newActiveModule=new Rotate3DModule(hub);
        } else if (type.equals("reactionArrow")) {
            newActiveModule=new ReactionArrowModule(hub);
        } else if (type.equals("chain")) {
            newActiveModule=new ChainModule(hub);
        }
        if(newActiveModule!=null){
	        newActiveModule.setID(type);
	        hub.setActiveDrawModule(newActiveModule);
        }
    }
}
