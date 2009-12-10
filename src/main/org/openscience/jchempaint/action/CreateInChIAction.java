/*
 *  Copyright (C) 1997-2009 Christoph Steinbeck, Stefan Kuhn, Mark Rijnbeek
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

import javax.swing.JFrame;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.dialog.TextViewDialog;
import org.openscience.jchempaint.dialog.WaitDialog;
import org.openscience.jchempaint.inchi.InChI;
import org.openscience.jchempaint.inchi.StdInChIGenerator;

/**
 * Creates InChI(s) from the current model, to be displayed to user
 * in pop out window.
 * 
 * @cdk.module jchempaint
 * @author Mark Rijnbeek
 */
public class CreateInChIAction extends JCPAction {

    private static final long serialVersionUID = -4886982931009753347L;

    TextViewDialog dialog = null;
    JFrame frame = null;

    public void actionPerformed(ActionEvent e) {
        logger.debug("Trying to create InChI: ", type);
        WaitDialog.showDialog();


        if (dialog == null) {
            dialog = new TextViewDialog(frame, "InChI", null, false, 40, 2);
        }

        ChemModel model = (ChemModel) jcpPanel.getChemModel();
        if (model.getReactionSet() != null
                && model.getReactionSet().getReactionCount() > 0) {
            dialog.setMessage(
                    GT._("Problem"),
                    GT._("You have reactions in JCP. Reactions cannot be shown as InChI!"));
        } else {
            StringBuffer dialogText = new StringBuffer();
            int i = 0;
            String eol = System.getProperty("line.separator");
            for (IAtomContainer container : model.getMoleculeSet()
                    .molecules()) {
                if (model.getMoleculeSet().getAtomContainerCount() > 1) {
                    dialogText.append(GT._("Structure") + " #" + (i + 1)
                            + eol);
                }
                try {
                    InChI inchi = new StdInChIGenerator().generateInchi(container);
                    dialogText.append(inchi.getInChI() + eol);
                    dialogText.append(inchi.getAuxInfo()+ eol);
                    dialogText.append(inchi.getKey() + eol);
                    
                } catch (Exception cdke) {
                    dialogText.append(GT._("InChI generation failed")
                            + ": " + cdke.getMessage() + eol + eol);
                }
                i++;
            }
            dialog.setMessage(GT._("InChI generation") + ":", dialogText
                    .toString());
        }

        WaitDialog.hideDialog();
        dialog.setVisible(true);

    }

}
