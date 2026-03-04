/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn, Christoph Steinbeck
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

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.layout.AtomPlacer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.HydrogenState;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.controller.undoredo.AddAtomsAndBondsEdit;
import org.openscience.jchempaint.controller.undoredo.CompoundEdit;
import org.openscience.jchempaint.controller.undoredo.RemoveAtomsAndBondsEdit;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * An action triggering everything related to hydrogens
 *
 */
public class AddHydrogenAction extends JCPAction {

    private static final long serialVersionUID = 7696756423842199080L;

    public void actionPerformed(ActionEvent event) {
        logger.debug("Trying to add hydrogen in mode: ", type);
        if (jcpPanel.getChemModel() != null) {
            /* JWM: Mar 2026 hydrogen/hydrooff is not really about adding/setting
            *                the display style but just if we show them on atoms or not */
            if (type.equals("hydroon")) {
                jcpPanel.get2DHub().getRenderer().getRenderer2DModel()
                        .setShowImplicitHydrogens(true);
                jcpPanel.get2DHub().getRenderer().getRenderer2DModel()
                        .setKekuleStructure(true);
            } else if (type.equals("hydrooff")) {
                jcpPanel.get2DHub().getRenderer().getRenderer2DModel()
                        .setShowImplicitHydrogens(false);
                jcpPanel.get2DHub().getRenderer().getRenderer2DModel()
                        .setKekuleStructure(false);
            } else if (type.equals("minimal")) {
                setHydrogens(HydrogenState.Minimal);
            } else if (type.equals("stereo")) {
                setHydrogens(HydrogenState.Stereo);
            } else if (type.equals("depiction")) {
                setHydrogens(HydrogenState.Depiction);
            } else if (type.equals("all")) {
                setHydrogens(HydrogenState.Explicit);
            }
            jcpPanel.get2DHub().updateView();
        }
    }

    private void setHydrogens(HydrogenState state) {

        Double bondLengthMedian = null;
        AtomBondSet removed = new AtomBondSet();
        AtomBondSet added = new AtomBondSet();

        IChemObjectSelection selection = jcpPanel.getRenderPanel().getRendererModel().getSelection();

        // make sure stereo is in sync for these normalisation mode
        IChemModel chemModel = jcpPanel.getChemModel();

        boolean syncNeeded = false;
        List<IAtomContainer> containers;
        if (selection.isFilled()) {
            IAtomContainer selectedPart = selection.getConnectedAtomContainer();
            containers = Collections.singletonList(selectedPart);
            syncNeeded = true;

            // add in any adjacent hydrogens as needed
            for (IAtomContainer container : ChemModelManipulator.getAllAtomContainers(chemModel)) {
                for (IBond bond : container.bonds()) {
                    if (selectedPart.contains(bond.getBegin()) && bond.getEnd().getAtomicNumber() == IElement.H) {
                        selectedPart.addAtom(bond.getEnd());
                        selectedPart.addBond(bond);
                    } else if (selectedPart.contains(bond.getEnd()) && bond.getBegin().getAtomicNumber() == IElement.H) {
                        selectedPart.addAtom(bond.getBegin());
                        selectedPart.addBond(bond);
                    }
                }
            }
        } else {
            containers = ChemModelManipulator.getAllAtomContainers(chemModel);
        }

        for (IAtomContainer container : containers) {

            // ensure stereo is in sync, not perfect since a use may not have
            // selected the stereocenter with neighbours but should be OK
            if (state == HydrogenState.Stereo || state == HydrogenState.Depiction) {
                container.setStereoElements(StereoElementFactory.using2DCoordinates(container).createAll());
            }

            if (bondLengthMedian == null)
                bondLengthMedian = GeometryUtil.getBondLengthMedian(container, 1.5);

            for (IAtom atom : container.atoms())
                removed.add(atom);
            for (IBond bond : container.bonds())
                removed.add(bond);

            // make the change!!
            AtomContainerManipulator.normalizeHydrogens(container, state);

            for (IAtom atom : container.atoms()) {
                if (removed.contains(atom))
                    removed.remove(atom);
                else
                    added.add(atom);
            }
            for (IBond bond : container.bonds()) {
                if (removed.contains(bond))
                    removed.remove(bond);
                else
                    added.add(bond);
            }
        }

        if (syncNeeded) {
            // potentially v. slow
            for (IAtom atom : removed.atoms()) {
                IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom);
                if (container != null)
                    container.removeAtom(atom);
            }
            for (IAtom atom : added.atoms()) {
                IBond bond = atom.bonds().iterator().next();
                IAtom nbor = bond.getOther(atom);
                IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, nbor);
                if (container != null) {
                    container.addAtom(atom);
                    container.addBond(bond);
                }
            }
        }

        // set the coordinates for the new atoms
        Set<IAtom> visit = new HashSet<>();
        for (IAtom newHAtom : added.atoms()) {
            if (visit.contains(newHAtom))
                continue;

            IBond nborBond = newHAtom.bonds().iterator().next();
            IAtom atom = nborBond.getOther(newHAtom);

            IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom);
            AtomPlacer atomPlacer = new AtomPlacer(container);

            IAtomContainer placed = newHAtom.getBuilder().newAtomContainer();
            IAtomContainer unplaced = newHAtom.getBuilder().newAtomContainer();

            for (IBond bond : container.getConnectedBondsList(atom)) {
                IAtom nbor = bond.getOther(atom);
                if (added.contains(nbor)) {
                    unplaced.addAtom(nbor);
                } else {
                    placed.addAtom(nbor);
                    visit.add(nbor);
                }
            }

            if (bondLengthMedian == null)
                bondLengthMedian = 1.5;

            atomPlacer.distributePartners(atom, placed, atom.getPoint2d(), unplaced,
                                          bondLengthMedian);
        }

        UndoRedoHandler undoRedo = jcpPanel.get2DHub().getUndoRedoHandler();
        if (undoRedo != null && (!removed.isEmpty() || !added.isEmpty())) {
            undoRedo.postEdit(new CompoundEdit("Set Hydrogen Display",
                                               new AddAtomsAndBondsEdit(chemModel,
                                                                        added, null, "add",
                                                                        jcpPanel.get2DHub()),
                                               new RemoveAtomsAndBondsEdit(chemModel,
                                                                           removed, "remove",
                                                                           jcpPanel.get2DHub())));
        }
    }
}

