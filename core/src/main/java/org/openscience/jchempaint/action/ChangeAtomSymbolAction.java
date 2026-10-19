/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egon Willighagen, Stefan Kuhn
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.controller.AddAtomModule;
import org.openscience.jchempaint.controller.AddBondDragModule;
import org.openscience.jchempaint.controller.IControllerModule;
import org.openscience.jchempaint.dialog.EnterElementOrGroupDialog;
import org.openscience.jchempaint.dialog.EnterElementSwingModule;
import org.openscience.jchempaint.dialog.PeriodicTableDialog;
import org.openscience.jchempaint.GT;

/**
 * Change the symbol of an atom.
 * Three modes:
 *  - Periodic Table
 *  - Enter Element
 *  - A standard symbol
 *
 * @author John Mayfield
 */
public class ChangeAtomSymbolAction extends JCPAction {

    public void actionPerformed(ActionEvent event) {
        logger.debug("About to change atom type of relevant atom!");
        IChemObject object = getSource(event);
        logger.debug("Source of call: ", object);
        List<IAtom> atoms = new ArrayList<>();

        // apply to the selection or the highlighed atom
        IChemObjectSelection selection = jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();
        if (selection != null && selection.isFilled()) {
            atoms.addAll(selection.elements(IAtom.class));
        } else if (jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getHighlightedAtom() != null) {
            atoms.add(jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getHighlightedAtom());
        }

        String s = event.getActionCommand();
        String symbol = s.substring(s.indexOf("@") + 1);
        boolean switchMode = event.getSource() instanceof JButton || atoms.isEmpty();
        IControllerModule newActiveModule;
        if (symbol.equals("periodictable")) {
            switchMode = true;
            if (jcpPanel.get2DHub().getActiveDrawModule() instanceof AddBondDragModule)
                newActiveModule = new AddAtomModule(jcpPanel.get2DHub(), ((AddBondDragModule) jcpPanel.get2DHub().getActiveDrawModule()).getStereoForNewBond());
            else if (jcpPanel.get2DHub().getActiveDrawModule() instanceof AddAtomModule)
                newActiveModule = new AddAtomModule(jcpPanel.get2DHub(), ((AddAtomModule) jcpPanel.get2DHub().getActiveDrawModule()).getStereoForNewBond());
            else
                newActiveModule = new AddAtomModule(jcpPanel.get2DHub(), IBond.Display.Solid);
            newActiveModule.setID(symbol);

            // open PeriodicTable panel
            PeriodicTableDialog dialog = new PeriodicTableDialog((JFrame) SwingUtilities.getWindowAncestor(jcpPanel));
            dialog.setName("periodictabledialog");
            dialog.setVisible(true);
            symbol = dialog.getChosenSymbol();
            if (symbol == null || symbol.isEmpty())
                return;
            if (switchMode) {
                jcpPanel.get2DHub().getController2DModel().setDrawElement(symbol);
                jcpPanel.get2DHub().getController2DModel().setDrawIsotopeNumber(0);
                jcpPanel.get2DHub().getController2DModel().setDrawPseudoAtom(false);
            }
        } else if (symbol.equals("enterelement")) {
            switchMode = true;
            newActiveModule = new EnterElementSwingModule(jcpPanel.get2DHub());
            newActiveModule.setID(symbol);
            if (!atoms.isEmpty()) {
                String[] funcGroupsKeys = new String[0];
                symbol = EnterElementOrGroupDialog.showDialog(null, null, GT.get("Enter an element symbol:"), GT.get("Enter element"), funcGroupsKeys, "", "");
                if (symbol != null && !symbol.isEmpty()) {
                    if (Character.isLowerCase(symbol.toCharArray()[0]))
                        symbol = Character.toUpperCase(symbol.charAt(0)) + symbol.substring(1);
                    IsotopeFactory ifa;
                    try {
                        ifa = Isotopes.getInstance();
                        IIsotope iso = ifa.getMajorIsotope(symbol);
                        if (iso == null) {
                            JOptionPane.showMessageDialog(jcpPanel, GT.get("No valid element symbol entered"), GT.get("Invalid symbol"), JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } catch (IOException e) {
                        return;
                    }
                }
            }
        } else {
            // it must be a symbol
            if (jcpPanel.get2DHub().getActiveDrawModule() instanceof AddBondDragModule)
                newActiveModule = new AddAtomModule(jcpPanel.get2DHub(), ((AddBondDragModule) jcpPanel.get2DHub().getActiveDrawModule()).getStereoForNewBond());
            else if (jcpPanel.get2DHub().getActiveDrawModule() instanceof AddAtomModule)
                newActiveModule = new AddAtomModule(jcpPanel.get2DHub(), ((AddAtomModule) jcpPanel.get2DHub().getActiveDrawModule()).getStereoForNewBond());
            else
                newActiveModule = new AddAtomModule(jcpPanel.get2DHub(), IBond.Display.Solid);

            if (switchMode) {
                jcpPanel.get2DHub().getController2DModel().setDrawPseudoAtom(false);
                newActiveModule.setID(symbol);
                // D/2H - deuterium special case
                if (symbol.equals("D")) {
                    jcpPanel.get2DHub().getController2DModel().setDrawElement("H");
                    jcpPanel.get2DHub().getController2DModel().setDrawIsotopeNumber(2);
                } else {
                    jcpPanel.get2DHub().getController2DModel().setDrawElement(symbol);
                    jcpPanel.get2DHub().getController2DModel().setDrawIsotopeNumber(0);
                }
            }
        }

        // only set the active draw module if we have pressed the button
        if (switchMode) {
            jcpPanel.get2DHub().setActiveDrawModule(newActiveModule);
        }

        if (symbol != null && !symbol.isEmpty()) {
            if (!atoms.isEmpty()) {

                Integer isotope = null;
                if (symbol.equals("D")) {
                    isotope = 2;
                    symbol = "H";
                }

                jcpPanel.get2DHub().setSymbol(atoms, symbol, isotope);
                jcpPanel.get2DHub().updateView();
            }
        }
    }


}

