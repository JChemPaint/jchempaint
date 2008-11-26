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
public class JCPActionChangeMode extends JCPAction
{

	private static final long serialVersionUID = -4056416630614934238L;
	private PTDialog dialog;

	public void actionPerformed(ActionEvent e) {
		System.out.println("the key: " + type);
		if (type.equals("move")) {
			jcpPanel.get2DHub().setActiveDrawModule(new MoveModule(jcpPanel.get2DHub()));
		}
		else if (type.equals("eraser")) {
			jcpPanel.get2DHub().setActiveDrawModule(new RemoveModule(jcpPanel.get2DHub()));
		}
		else if (type.equals("plus")) {
			jcpPanel.get2DHub().setActiveDrawModule(new ChangeFormalChargeModule(jcpPanel.get2DHub(),1));
		}
		else if (type.equals("minus")) {
			jcpPanel.get2DHub().setActiveDrawModule(new ChangeFormalChargeModule(jcpPanel.get2DHub(),-1));
		}
		else if (type.equals("bond")) {
			jcpPanel.get2DHub().setActiveDrawModule(new AddBondModule(jcpPanel.get2DHub()));
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("cyclesymbol")) {
			jcpPanel.get2DHub().setActiveDrawModule(new CycleSymbolModule(jcpPanel.get2DHub()));
		}
		else if (type.equals("periodictable")) {
			jcpPanel.get2DHub().setActiveDrawModule(new AddAtomModule(jcpPanel.get2DHub()));
			if (dialog == null)
			{
				// open PeriodicTable panel
				dialog = new PTDialog(
						new PTDialogChangeListener(jcpPanel.get2DHub().getController2DModel())
						);
			}
			dialog.pack();
			dialog.setVisible(true);
		}
		else if (type.equals("enterelement")) {
			jcpPanel.get2DHub().setActiveDrawModule(new EnterElementSwingModule(jcpPanel.get2DHub()));
		}
		else if (type.equals("up_bond")) {
			//TODO not yet a module
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("down_bond")) {
			//TODO not yet a module
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("triangle")) {
			jcpPanel.get2DHub().setActiveDrawModule(new AddRingModule(jcpPanel.get2DHub(),3));
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("square")) {
			jcpPanel.get2DHub().setActiveDrawModule(new AddRingModule(jcpPanel.get2DHub(),4));
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("pentagon")) {
			jcpPanel.get2DHub().setActiveDrawModule(new AddRingModule(jcpPanel.get2DHub(),5));
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("hexagon")) {
			jcpPanel.get2DHub().setActiveDrawModule(new AddRingModule(jcpPanel.get2DHub(),6));
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("heptagon")) {
			jcpPanel.get2DHub().setActiveDrawModule(new AddRingModule(jcpPanel.get2DHub(),7));
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("octagon")) {
			jcpPanel.get2DHub().setActiveDrawModule(new AddRingModule(jcpPanel.get2DHub(),8));
			jcpPanel.get2DHub().getController2DModel().setDrawElement("C");
		}
		else if (type.equals("benzene")) {
			//TODO not yet a module
		}
		else if (type.length() == 1 || type.length() == 2) {
			//I assume something with length of 1 is an atom name (C/H/O/N/etc.)
			jcpPanel.get2DHub().setActiveDrawModule(new AddAtomModule(jcpPanel.get2DHub()));
			jcpPanel.get2DHub().getController2DModel().setDrawElement(type);
		}
	 	if (this.jcpPanel.getActionButton() != null)
	 		this.jcpPanel.getActionButton().setBackground(Color.LIGHT_GRAY);
	 	this.jcpPanel.setActionButton((JComponent) e.getSource());
		((JComponent) e.getSource()).setBackground(Color.GRAY);
    }

	class PTDialogChangeListener implements ICDKChangeListener
	{

		IController2DModel model;


		/**
		 *  Constructor for the PTDialogChangeListener object
		 *
		 *@param  model  Description of the Parameter
		 */
		public PTDialogChangeListener(IController2DModel model)
		{
			this.model = model;
		}

		public void stateChanged(EventObject event)
		{
			logger.debug("Element change signaled...");
			if (event.getSource() instanceof PeriodicTablePanel)
			{
				PeriodicTablePanel source = (PeriodicTablePanel) event.getSource();
				String symbol = source.getSelectedElement().getSymbol();
				logger.debug("Setting drawing element to: ", symbol);
				model.setDrawElement(symbol);
				dialog.setVisible(false);
				dialog = null;
			} else
			{
				logger.warn("Unkown source for event: ", event.getSource().getClass().getName());
			}
		}
	}
}

