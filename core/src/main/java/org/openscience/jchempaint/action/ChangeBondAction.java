/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egon Willighagen, Stefan Kuhn
 *  Some portions Copyright (C) 2009 Konstantin Tokarev
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.jchempaint.controller.AddBondDragModule;


/**
 * changes the atom symbol
 */
public class ChangeBondAction extends JCPAction {

    private static final long serialVersionUID = -8502905723573311893L;

    public void actionPerformed(ActionEvent event) {

        String s = event.getActionCommand();
        String type = s.substring(s.indexOf("@") + 1);

        //first switch mode
        AddBondDragModule newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.Solid, true);
        switch (type) {
            case "down_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.Down, true);
                break;
            case "up_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.Up, true);
                break;
            case "hollow_wedge_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.HollowWedgeBegin, true);
                break;
            case "hash_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.Hash, true);
                break;
            case "bold_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.Bold, true);
                break;
            case "dash_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.Dash, true);
                break;
            case "coordination_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.ArrowEnd, true);
                break;
            case "undefined_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Display.Wavy, true);
                break;
            case "undefined_stereo_bond":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Order.DOUBLE, IBond.Display.Crossed, true, "undefined_stereo_bond");
                break;
            case "bondTool":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Order.SINGLE, true);
                break;
            case "double_bondTool":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Order.DOUBLE, true);
                break;
            case "triple_bondTool":
                newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(), IBond.Order.TRIPLE, true);
                break;
        }

        if (newActiveModule != null) { // null means that menu was used => don't change module            
            newActiveModule.setID(type);
            jcpPanel.get2DHub().setActiveDrawModule(newActiveModule);
        }

        // xxxTool -> xxx
        int l = type.length();
        if (type.substring(l - 4, l).equals("Tool"))
            type = type.substring(0, l - 4);

        //then handle selection or highlight if there is one
        IChemObject object = getSource(event);
        Collection<IBond> bondsInRange = null;

        if (object == null) {
            //this means the main menu or toolbar was used
            if (jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection() != null
                && jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().isFilled())
                bondsInRange = jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().elements(IBond.class);
        } else if (object instanceof IBond) {
            List<IBond> bonds = new ArrayList<IBond>();
            bonds.add((IBond) object);
            bondsInRange = bonds;
        } else {
            List<IBond> bonds = new ArrayList<IBond>();
            bonds.add(jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getHighlightedBond());
            bondsInRange = bonds;
        }

        if (bondsInRange == null)
            return;

        IBond.Display display = null;
        IBond.Order order = null;

        switch (type) {
            case "bond":
                display = IBond.Display.Solid;
                order = IBond.Order.SINGLE;
                break;
            case "double_bond":
                display = IBond.Display.Solid;
                order = IBond.Order.DOUBLE;
                break;
            case "triple_bond":
                display = IBond.Display.Solid;
                order = IBond.Order.TRIPLE;
                break;
            case "quad_bond":
                display = IBond.Display.Solid;
                order = IBond.Order.QUADRUPLE;
                break;
            case "down_bond":
                display = IBond.Display.Down;
                order = IBond.Order.SINGLE;
                break;
            case "up_bond":
                display = IBond.Display.Up;
                order = IBond.Order.SINGLE;
                break;
            case "hollow_wedge_bond":
                display = IBond.Display.HollowWedgeBegin;
                order = IBond.Order.SINGLE;
                break;
            case "hash_bond":
                display = IBond.Display.Hash;
                order = IBond.Order.SINGLE;
                break;
            case "bold_bond":
                display = IBond.Display.Bold;
                order = IBond.Order.SINGLE;
                break;
            case "dash_bond":
                display = IBond.Display.Dash;
                order = IBond.Order.SINGLE;
                break;
            case "coordination_bond":
                display = IBond.Display.ArrowEnd;
                order = IBond.Order.UNSET;
                break;
            case "undefined_bond":
                display = IBond.Display.Wavy;
                order = IBond.Order.SINGLE;
                break;
            case "undefined_stereo_bond":
                display = IBond.Display.Crossed;
                order = IBond.Order.DOUBLE;
                break;
        }
        jcpPanel.get2DHub().changeBonds(bondsInRange, order, display);
        jcpPanel.get2DHub().updateView();

    }
}

