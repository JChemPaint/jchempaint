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

import javax.swing.JComponent;

import org.openscience.cdk.controller.Controller2DModel;

/**
 * JChemPaint menu actions
 *
 */
public class JCPActionChangeMode extends JCPAction
{

	private static final long serialVersionUID = -4056416630614934238L;

	public void actionPerformed(ActionEvent e) {
		System.out.println("the key: " + type);
		Controller2DModel.DrawMode drawMode = null;
		String drawElement="";
		if (type.equals("move")) {
			drawMode = Controller2DModel.DrawMode.MOVE;
		}
		else if (type.equals("eraser")) {
			drawMode = Controller2DModel.DrawMode.ERASER;
		}
		else if (type.equals("plus")) {
			//module = new Controller2DModuleChangeFormalC(1);
			drawMode = Controller2DModel.DrawMode.INCCHARGE;
		}
		else if (type.equals("minus")) {
			drawMode = Controller2DModel.DrawMode.DECCHARGE;
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("bond")) {
			drawMode = Controller2DModel.DrawMode.DRAWBOND;
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("symbol")) {
			drawMode = Controller2DModel.DrawMode.SYMBOL;
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("element")) {
			drawMode = Controller2DModel.DrawMode.ELEMENT;
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("enterelement")) {
			drawMode = Controller2DModel.DrawMode.ENTERELEMENT;
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("up_bond")) {
			drawMode = Controller2DModel.DrawMode.UP_BOND;
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("down_bond")) {
			drawMode = Controller2DModel.DrawMode.DOWN_BOND;
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("triangle")) {
			drawMode = Controller2DModel.DrawMode.RING;
			jcpPanel.get2DHub().getController2DModel().setRingSize(3);
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("square")) {
			drawMode = Controller2DModel.DrawMode.RING;
			jcpPanel.get2DHub().getController2DModel().setRingSize(4);
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("pentagon")) {
			drawMode = Controller2DModel.DrawMode.RING;
			jcpPanel.get2DHub().getController2DModel().setRingSize(5);
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("hexagon")) {
			drawMode = Controller2DModel.DrawMode.RING;
			jcpPanel.get2DHub().getController2DModel().setRingSize(6);
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("octagon")) {
			drawMode = Controller2DModel.DrawMode.RING;
			jcpPanel.get2DHub().getController2DModel().setRingSize(7);
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("heptagon")) {
			drawMode = Controller2DModel.DrawMode.RING;
			jcpPanel.get2DHub().getController2DModel().setRingSize(7);
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.equals("benzene")) {
			drawMode = Controller2DModel.DrawMode.BENZENERING;
			//module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (type.length() == 1) {
			//I assume something with length of 1 is an atom name (C/H/O/N/etc.)
			//	module = new Controller2DModuleAddAtom(key);
			drawMode = Controller2DModel.DrawMode.ELEMENT;
			drawElement = type;
		}
	 	if (this.jcpPanel.getActionButton() != null)
	 		this.jcpPanel.getActionButton().setBackground(Color.LIGHT_GRAY);
	 	this.jcpPanel.setActionButton((JComponent) e.getSource());
		((JComponent) e.getSource()).setBackground(Color.GRAY);
		jcpPanel.get2DHub().getController2DModel().setDrawMode(drawMode);
		if (drawMode != null) {
			System.out.println("Drawmode activated: " + drawMode);
		}
		else {
			System.out.println("This drawmode is unavailable atm :/ ");
		}
		if (drawElement != "")
			jcpPanel.get2DHub().getController2DModel().setDrawElement(drawElement);
    }
}

