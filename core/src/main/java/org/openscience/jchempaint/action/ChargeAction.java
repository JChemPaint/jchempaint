/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 2009 Stefan Kuhn
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
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.jchempaint.controller.ChangeFormalChargeModule;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import javax.swing.JButton;
import javax.swing.JMenuItem;


/**
 * Changes the charge
 */
public class ChargeAction extends JCPAction {

    private static final long serialVersionUID = -8502905723573311893L;

    public void actionPerformed(ActionEvent event) {

        String s = event.getActionCommand();
        String action = s.substring(s.indexOf("@") + 1);
        int charge = 0;
        if (action.equals("plus2")) {
            charge = 2;
        } else if (action.equals("plus")) {
            charge = 1;
        } else if (action.equals("minus")) {
            charge = -1;
        } else if (action.equals("minus2")) {
            charge = -2;
        }

        logger.debug("About to change atom type of relevant atom!");
        List<IAtom> atoms = new ArrayList<>();

        JChemPaintRendererModel model = jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel();
        IAtom highlightedAtom = model.getHighlightedAtom();
        if (model.getSelection() != null && model.getSelection().isFilled())
            atoms.addAll(model.getSelection().elements(IAtom.class));
        else if (highlightedAtom != null)
            atoms.add(highlightedAtom);
        else
            return;


        // only change to the charge tool if the button was pressed
        if (event.getSource() instanceof JButton ||
            event.getSource() instanceof JMenuItem  ||
            atoms.isEmpty()) {
            ChangeFormalChargeModule newActiveModule = new ChangeFormalChargeModule(jcpPanel.get2DHub(), charge);
            newActiveModule.setID(action);
            jcpPanel.get2DHub().setActiveDrawModule(newActiveModule);
        }

        for (IAtom atom : atoms) {
            int newCharge = charge;
            if (atom.getFormalCharge() != null)
                newCharge += atom.getFormalCharge();
            jcpPanel.get2DHub().setCharge(atom, newCharge);
        }
        jcpPanel.get2DHub().updateView();
    }
}

