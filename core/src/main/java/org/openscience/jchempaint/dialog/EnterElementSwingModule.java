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
package org.openscience.jchempaint.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.vecmath.Point2d;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.XMLIsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.layout.RingPlacer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.controller.ControllerModuleAdapter;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.controller.undoredo.CompoundEdit;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;

public class EnterElementSwingModule extends ControllerModuleAdapter {

    private final SmilesParser smipar = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    private List<String> funcgroupnames = new ArrayList<>();
    private HashMap<String, String> funcgroupsmap = new HashMap<>();
    private final static RingPlacer ringPlacer = new RingPlacer();
    private String ID;

    public EnterElementSwingModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
        String filename = "org/openscience/jchempaint/resources/funcgroups.txt";

        Set<String> uniq = new HashSet<>();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
             InputStreamReader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader brdr = new BufferedReader(rdr)) {
            String line;
            while ((line = brdr.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#')
                    continue;
                String[] fields = line.split("\t", 2);
                if (fields.length != 2) {
                    System.err.println("Bad line in funcgroups.txt: " + line);
                    continue;
                }

                String abbr = fields[0];
                String value = fields[1];
                if (uniq.add(value))
                    funcgroupnames.add(abbr);
                funcgroupsmap.put(abbr.toLowerCase(Locale.ROOT), value);
            }
        } catch (IOException ex) {
            // ignored
        }
    }

    private boolean isSmilesFrag(String str) {
        if (str.isEmpty() || str.charAt(0) != '*')
            return false;
        try {
            smipar.parseSmiles(str);
            return true;
        } catch (InvalidSmilesException e) {
            return false;
        }
    }

    public void mouseClickedDown(Point2d worldCoord, int modifiers) {

        IAtom atom = chemModelRelay.getRenderer()
                                          .getRenderer2DModel()
                                          .getHighlightedAtom();

        String[] funcGroupsKeys = new String[funcgroupnames.size() + 1];
        funcGroupsKeys[0] = "";

        // fow now presume only terminal atoms can have functional groups, so
        // we populate the selection box
        if (atom.getBondCount() == 1) {
            int h = 1;
            for (String name : funcgroupnames) {
                funcGroupsKeys[h++] = name;
            }
        }

        String label = EnterElementOrGroupDialog.showDialog(null, null,
                                                        "Enter an element symbol or choose/enter a functional group abbreviation:",
                                                        "Enter element",
                                                        funcGroupsKeys,
                                                        "", "");
        try {

            String smiles = funcgroupsmap.get(label.toLowerCase(Locale.ROOT));

            //this means a functional group was entered
            IChemModel chemModel = chemModelRelay.getIChemModel();
            if (smiles != null && !smiles.isEmpty()) {
                if (addSmiles(smiles, chemModel, atom, label))
                    return;
            } else if (isSmilesFrag(label)) {
                if (addSmiles(label, chemModel, atom, label))
                    return;
            } else if (!label.isEmpty()) {
                if (Character.isLowerCase(label.toCharArray()[0]))
                    label = Character.toUpperCase(label.charAt(0)) + label.substring(1);
                IsotopeFactory ifa = XMLIsotopeFactory.getInstance(chemModel.getBuilder());
                IIsotope iso = ifa.getMajorIsotope(label);
                if (iso != null) {
                    if (atom == null) {
                        AtomBondSet addatom = new AtomBondSet();
                        addatom.add(chemModelRelay.addAtomWithoutUndo(label, worldCoord, false));
                        if (chemModelRelay.getUndoRedoFactory() != null && chemModelRelay.getUndoRedoHandler() != null) {
                            IUndoRedoable undoredo = chemModelRelay.getUndoRedoFactory().getAddAtomsAndBondsEdit(chemModel, addatom, null, GT.get("Add Atom"), chemModelRelay);
                            chemModelRelay.getUndoRedoHandler().postEdit(undoredo);
                        }
                    } else {
                        chemModelRelay.setSymbol(atom, label);
                    }
                    chemModelRelay.getController2DModel().setDrawElement(label);
                } else {
                    JOptionPane.showMessageDialog(null, GT.get("{0} is not a valid element symbol or functional group.", label), GT.get("No valid input"), JOptionPane.WARNING_MESSAGE);
                }
            }
            chemModelRelay.updateView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean addSmiles(String smiles, IChemModel chemModel, IAtom highlightAtom, String label) throws CDKException {
        IAtomContainer funcgroup = smipar.parseSmiles(smiles);
        IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel,
                                                                                 highlightAtom);

        AtomBondSet added = new AtomBondSet();
        AtomBondSet deleted = new AtomBondSet();
        Set<IAtom> afix = new HashSet<>();
        Set<IBond> bfix = new HashSet<>();
        IUndoRedoable delEdit = null;

        IAtom lastplaced = null;
        if (container == null) {
            if (chemModel.getMoleculeSet() == null)
                chemModel.setMoleculeSet(funcgroup.getBuilder().newInstance(IAtomContainerSet.class));
            chemModel.getMoleculeSet().addAtomContainer(funcgroup);
            funcgroup.getAtom(0).setPoint2d(new Point2d(0, 0));
            lastplaced = funcgroup.getAtom(0);
            container = funcgroup;
        } else {

            if (highlightAtom.getBondCount() != 1) {
                JOptionPane.showMessageDialog(null,
                                              GT.get("Incorrect number of bonds to function group {1}", label),
                                              GT.get("No valid input"), JOptionPane.WARNING_MESSAGE);
                return true;
            }

            for (IAtom atom : container.atoms())
                afix.add(atom);
            for (IBond bond : container.bonds())
                bfix.add(bond);

            IBond bondToDelete = highlightAtom.bonds().iterator().next();
            deleted.add(bondToDelete);
            deleted.add(highlightAtom);
            container.removeAtom(highlightAtom);

            // JWM: we need to capture the delete NOW before adding
            // anything else otherwise we get in an inconsistent state
            IUndoRedoFactory undoRedoFactory = chemModelRelay.getUndoRedoFactory();
            if (undoRedoFactory != null && chemModelRelay.getUndoRedoHandler() != null) {
                delEdit = undoRedoFactory.getRemoveAtomsAndBondsEdit(chemModel, deleted, "", chemModelRelay);
            }

            container.add(funcgroup);
            IAtom attachmentStar = funcgroup.getAtom(0);
            IAtom attachmentAtom = attachmentStar.bonds().iterator().next().getOther(attachmentStar);

            container.removeAtom(attachmentStar);
            IBond newBond = null;
            if (bondToDelete.getBegin().equals(highlightAtom))
                newBond = container.newBond(attachmentAtom, bondToDelete.getOther(highlightAtom), bondToDelete.getOrder());
            else if (bondToDelete.getEnd().equals(highlightAtom))
                newBond = container.newBond(bondToDelete.getOther(highlightAtom), attachmentAtom, bondToDelete.getOrder());
            else
                throw new IllegalStateException("attachmentAtom is not at either end of one if it's bonds");
            newBond.setStereo(bondToDelete.getStereo());
            newBond.setDisplay(bondToDelete.getDisplay());

            for (IAtom atom : container.atoms()) {
                if (!afix.contains(atom))
                    added.add(atom);
            }
            for (IBond bond : container.bonds()) {
                if (!bfix.contains(bond))
                    added.add(bond);
            }
        }

        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(container, false, afix, bfix);
        sdg.generateCoordinates();

        IUndoRedoFactory undoRedoFactory = chemModelRelay.getUndoRedoFactory();
        if (undoRedoFactory != null && chemModelRelay.getUndoRedoHandler() != null) {
            IUndoRedoable undoredo = undoRedoFactory.getAddAtomsAndBondsEdit(chemModel, added, null, GT.get("Add Functional Group"), chemModelRelay);
            if (delEdit != null)
                undoredo = new CompoundEdit(GT.get("Add Functional Group"), delEdit, undoredo);
            chemModelRelay.getUndoRedoHandler().postEdit(undoredo);
        }
        return false;
    }

    public String getDrawModeString() {
        return "Enter Element or Group";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}