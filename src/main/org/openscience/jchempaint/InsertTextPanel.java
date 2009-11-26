/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Rajarshi Guha, Stefan Kuhn
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
package org.openscience.jchempaint;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.jchempaint.applet.JChemPaintAbstractApplet;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;

/**
 * A panel containing a text field and button to directly insert SMILES or InChI's
 *
 */
public class InsertTextPanel extends JPanel implements ActionListener {

    private AbstractJChemPaintPanel jChemPaintPanel;
    private JComboBox textCombo;
    private JTextComponent editor;
    private JFrame closeafter = null;
    private JButton button = new JButton(GT._("Insert"));

    public InsertTextPanel(AbstractJChemPaintPanel jChemPaintPanel, JFrame closeafter) {
        super();
        this.closeafter = closeafter;
        setLayout(new GridBagLayout());

        List<String> oldText = new ArrayList<String>();
        oldText.add("");

        textCombo = new JComboBox(oldText.toArray());
        textCombo.setEditable(true);
        textCombo.setToolTipText(GT._("Enter a CAS, SMILES or InChI string"));

        textCombo.addActionListener(this);
        editor = (JTextComponent) textCombo.getEditor().getEditorComponent();

        button.addActionListener(this);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        add(textCombo, gridBagConstraints);

        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridx = 1;
        add(button, gridBagConstraints);

        this.jChemPaintPanel = jChemPaintPanel;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        if (actionCommand.equals("comboBoxEdited")
                || actionCommand.equals(GT._("Insert"))) {
            IMolecule molecule = getMolecule();
            if (molecule == null)
                return;
            JChemPaintAbstractApplet.generateModel(jChemPaintPanel, molecule, true, false);
            if (closeafter != null)
                closeafter.setVisible(false);
        }
    }

    private IMolecule getMolecule() {

        IMolecule molecule;
        String text = (String) textCombo.getSelectedItem();
        text = text.trim(); // clean up extra white space

        if (text.equals("")) return null;

        if (text.startsWith("InChI")) { // handle it as an InChI
            try {
                InChIGeneratorFactory inchiFactory = InChIGeneratorFactory.getInstance();
                InChIToStructure inchiToStructure = inchiFactory.getInChIToStructure(text,jChemPaintPanel.getChemModel().getBuilder());
                INCHI_RET status = inchiToStructure.getReturnStatus();
                if (status != INCHI_RET.OKAY) {
                  JOptionPane.showMessageDialog(jChemPaintPanel, GT._("Could not process InChI"));
                  return null;
                }
                IAtomContainer atomContainer = inchiToStructure.getAtomContainer();
                molecule = atomContainer.getBuilder().newMolecule(atomContainer);
            } catch (CDKException e2) {
                JOptionPane.showMessageDialog(jChemPaintPanel, GT._("Could not load InChI subsystem"));
                return null;
            }
        } else if (isCASNumber(text)) { // is it a CAS number?
            try {
                molecule = getMoleculeFromCAS(text);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(jChemPaintPanel, GT._("Error in reading data from PubChem"));
                return null;
            }
        } else { // OK, it must be a SMILES
            SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            try {
                molecule = smilesParser.parseSmiles(text);
                //for some reason, smilesparser sets valencies, which we don't want in jcp
                for(int i=0;i<molecule.getAtomCount();i++){
                	molecule.getAtom(i).setValency(null);
                }

            } catch (InvalidSmilesException e1) {
                JOptionPane.showMessageDialog(jChemPaintPanel, GT._("Invalid SMILES specified"));
                return null;
            }
        }

        // OK, we have a valid molecule, save it and show it
        String tmp = (String) textCombo.getItemAt(0);
        if (tmp.equals("")) textCombo.removeItemAt(0);
        textCombo.addItem(text);
        editor.setText("");

        return molecule;
    }

    private boolean isCASNumber(String text) {
        String[] chars = text.split("-");
        if (chars.length != 3) return false;
        for (int i = 0; i < 3; i++) {
            if (i == 2 && chars[i].length() != 1) return false;
            if (i == 1 && chars[i].length() != 2) return false;
            if (i == 0 && chars[i].length() > 6) return false;
            try {
                Integer.parseInt(chars[i]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private IMolecule getMoleculeFromCAS(String cas) throws IOException {
        String firstURL = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=pccompound&term=" + cas;
        String data = getDataFromURL(firstURL);

        Pattern pattern = Pattern.compile("http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi\\?cid=(\\d*)");
        Matcher matcher = pattern.matcher(data);

        String cid = null;
        boolean found = false;
        while (matcher.find()) {
            cid = matcher.group(1);
            try { // should be an integer
                Integer.parseInt(cid);
                found = true;
                break;
            } catch (NumberFormatException e) {
                continue;
            }
        }
        if (!found) return null;

        String secondURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?tool=jcppubchem&db=pccompound&id=" + cid;
        data = getDataFromURL(secondURL);

        pattern = Pattern.compile("<Item Name=\"CanonicalSmile\" Type=\"String\">([^\\s]*?)</Item>");
        matcher = pattern.matcher(data);
        String smiles = "";
        found = false;
        while (matcher.find()) {
            smiles = matcher.group(1);
            if (!smiles.equals("")) {
                found = true;
                break;
            }
        }
        if (!found) return null;

        // got the canonical SMILES, lets get the molecule
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        try {
        	IMolecule mol=smilesParser.parseSmiles(smiles);
            //for some reason, smilesparser sets valencies, which we don't want in jcp
            for(int i=0;i<mol.getAtomCount();i++){
            	mol.getAtom(i).setValency(null);
            }
            return mol;
        } catch (InvalidSmilesException e1) {
            JOptionPane.showMessageDialog(jChemPaintPanel, "Couldn't process data from PubChem");
            return null;
        }
    }

    private String getDataFromURL(String url) throws IOException {
        URL theURL = new URL(url);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(theURL.openStream()));
        String data = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) data += line;
        bufferedReader.close();
        return data;
    }

    public void updateLanguage() {
        textCombo.setToolTipText(GT._("Enter a CAS, SMILES or InChI string"));
        button.setText(GT._("Insert"));
    }

}
