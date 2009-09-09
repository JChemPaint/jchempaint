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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.EventObject;

import javax.swing.JComponent;

import org.openscience.cdk.controller.AddAtomModule;
import org.openscience.cdk.controller.AddBondDragModule;
import org.openscience.cdk.controller.AddRingModule;
import org.openscience.cdk.controller.AlterBondStereoModule;
import org.openscience.cdk.controller.AtomAtomMappingModule;
import org.openscience.cdk.controller.ChangeFormalChargeModule;
import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.CycleSymbolModule;
import org.openscience.cdk.controller.IControllerModel;
import org.openscience.cdk.controller.IControllerModule;
import org.openscience.cdk.controller.MoveModule;
import org.openscience.cdk.controller.RemoveModule;
import org.openscience.cdk.controller.RotateModule;
import org.openscience.cdk.controller.SelectLassoModule;
import org.openscience.cdk.controller.SelectSquareModule;
import org.openscience.cdk.controller.IChemModelRelay.Direction;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.exception.CDKException;
import org.openscience.jchempaint.JCPToolBar;
import org.openscience.jchempaint.dialog.EnterElementSwingModule;
import org.openscience.jchempaint.dialog.PeriodicTableDialog;
import org.openscience.jchempaint.dialog.PeriodicTablePanel;

/**
 * JChemPaint menu actions
 * 
 */
public class ChangeModeAction extends JCPAction {

    private static final long serialVersionUID = -4056416630614934238L;

    public void actionPerformed(ActionEvent e) {
        ControllerHub hub = jcpPanel.get2DHub();
        IControllerModule newActiveModule=null;
        if (type.equals("move")) {
        	newActiveModule=new MoveModule(hub);
        } else if (type.equals("eraser")) {
        	newActiveModule=new RemoveModule(hub);
        } else if (type.equals("plus")) {
        	newActiveModule=new ChangeFormalChargeModule(hub, 1);
        } else if (type.equals("minus")) {
        	newActiveModule=new ChangeFormalChargeModule(hub, -1);
        } else if (type.equals("bond")) {
        	newActiveModule=new AddBondDragModule(hub);
        } else if (type.equals("periodictable")) {
        	newActiveModule=new AddAtomModule(hub);
            // open PeriodicTable panel
            PeriodicTableDialog dialog = new PeriodicTableDialog();
            dialog.setName("periodictabledialog");
            hub.getController2DModel().setDrawElement(dialog.getChoosenSymbol());
        } else if (type.equals("enterelement")) {
        	newActiveModule=new EnterElementSwingModule(hub);
        } else if (type.equals("lasso")) {
        	newActiveModule=new SelectLassoModule(hub);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("select")) {
        	newActiveModule=new SelectSquareModule(hub);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("up_bond")) {
        	newActiveModule=new AlterBondStereoModule(
                    hub, Direction.UP);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("down_bond")) {
        	newActiveModule=new AlterBondStereoModule(
                    hub, Direction.DOWN);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("undefined_bond")) {
        	newActiveModule=new AlterBondStereoModule(
                    hub, Direction.UNDEFINED);
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("undefined_stereo_bond")) {
        	newActiveModule=new AlterBondStereoModule(
                    hub, Direction.EZ_UNDEFINED);
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
        } else if (type.length() == 1 || type.length() == 2) {
            // I assume something with length of 1 is an atom name
            // (C/H/O/N/etc.)
        	newActiveModule=new AddAtomModule(hub);
            hub.getController2DModel().setDrawElement(type);
        } else if (type.equals("rotate")) {
        	newActiveModule=new RotateModule(hub);
        }
        if(newActiveModule!=null){
	        newActiveModule.setID(type);
	        hub.setActiveDrawModule(newActiveModule);
        }
    }
}
