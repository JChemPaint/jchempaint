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
import java.util.EventObject;

import javax.swing.JComponent;

import org.openscience.cdk.controller.AddAtomModule;
import org.openscience.cdk.controller.AddBondModule;
import org.openscience.cdk.controller.AddRingModule;
import org.openscience.cdk.controller.ChangeFormalChargeModule;
import org.openscience.cdk.controller.Controller2DHub;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.controller.CycleSymbolModule;
import org.openscience.cdk.controller.IController2DModel;
import org.openscience.cdk.controller.MoveModule;
import org.openscience.cdk.controller.RemoveModule;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.jchempaint.dialog.EnterElementSwingModule;
import org.openscience.jchempaint.dialog.PTDialog;
import org.openscience.jchempaint.dialog.PeriodicTablePanel;

/**
 * JChemPaint menu actions
 * 
 */
public class JCPChangeModeAction extends JCPAction {

    private static final long serialVersionUID = -4056416630614934238L;
    private PTDialog dialog;

    public void actionPerformed(ActionEvent e) {
        System.out.println("the key: " + type);
        Controller2DHub hub = jcpPanel.get2DHub();
        if (type.equals("move")) {
            hub.setActiveDrawModule(new MoveModule(hub));
        } else if (type.equals("eraser")) {
            hub.setActiveDrawModule(new RemoveModule(hub));
        } else if (type.equals("plus")) {
            hub.setActiveDrawModule(new ChangeFormalChargeModule(hub, 1));
        } else if (type.equals("minus")) {
            hub.setActiveDrawModule(new ChangeFormalChargeModule(hub, -1));
        } else if (type.equals("bond")) {
            hub.setActiveDrawModule(new AddBondModule(hub));
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("cyclesymbol")) {
            hub.setActiveDrawModule(new CycleSymbolModule(hub));
        } else if (type.equals("periodictable")) {
            hub.setActiveDrawModule(new AddAtomModule(hub));
            if (dialog == null) {
                // open PeriodicTable panel
                dialog = new PTDialog(
                        new PTDialogChangeListener(hub.getController2DModel()));
            }
            dialog.pack();
            dialog.setVisible(true);
        } else if (type.equals("enterelement")) {
            hub.setActiveDrawModule(new EnterElementSwingModule(hub));
        } else if (type.equals("up_bond")) {
            // TODO not yet a module
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("down_bond")) {
            // TODO not yet a module
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("triangle")) {
            hub.setActiveDrawModule(new AddRingModule(hub, 3, false));
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("square")) {
            hub.setActiveDrawModule(new AddRingModule(hub, 4, false));
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("pentagon")) {
            hub.setActiveDrawModule(new AddRingModule(hub, 5, false));
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("hexagon")) {
            hub.setActiveDrawModule(new AddRingModule(hub, 6, false));
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("heptagon")) {
            hub.setActiveDrawModule(new AddRingModule(hub, 7, false));
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("octagon")) {
            hub.setActiveDrawModule(new AddRingModule(hub, 8, false));
            hub.getController2DModel().setDrawElement("C");
        } else if (type.equals("benzene")) {
            hub.setActiveDrawModule(new AddRingModule(hub, 6, true));
            hub.getController2DModel().setDrawElement("C");
        } else if (type.length() == 1 || type.length() == 2) {
            // I assume something with length of 1 is an atom name
            // (C/H/O/N/etc.)
            hub.setActiveDrawModule(new AddAtomModule(hub));
            hub.getController2DModel().setDrawElement(type);
        }
        if (this.jcpPanel.getActionButton() != null)
            this.jcpPanel.getActionButton().setBackground(Color.LIGHT_GRAY);
        this.jcpPanel.setActionButton((JComponent) e.getSource());
        ((JComponent) e.getSource()).setBackground(Color.GRAY);
    }

    class PTDialogChangeListener implements ICDKChangeListener {

        IController2DModel model;

        /**
         * Constructor for the PTDialogChangeListener object
         * 
         *@param model
         *            Description of the Parameter
         */
        public PTDialogChangeListener(IController2DModel model) {
            this.model = model;
        }

        public void stateChanged(EventObject event) {
            logger.debug("Element change signaled...");
            if (event.getSource() instanceof PeriodicTablePanel) {
                PeriodicTablePanel source = (PeriodicTablePanel) event
                        .getSource();
                String symbol = source.getSelectedElement().getSymbol();
                logger.debug("Setting drawing element to: ", symbol);
                model.setDrawElement(symbol);
                dialog.setVisible(false);
                dialog = null;
            } else {
                logger.warn("Unkown source for event: ", event.getSource()
                        .getClass().getName());
            }
        }
    }
}
