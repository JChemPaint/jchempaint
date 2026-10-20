/* $RCSfile$
 * $Author: gilleain $
 * $Date: 2008-11-26 16:01:05 +0000 (Wed, 26 Nov 2008) $
 * $Revision: 13311 $
 *
 * Copyright (C) 2005-2008 Tobias Helmus, Stefan Kuhn
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.controller.undoredo;

import java.util.List;
import java.util.Map;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.vecmath.Vector2d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * @cdk.module controlextra
 * @cdk.svnrev $Revision: 10979 $
 */
public class MergeMoleculesEdit implements IUndoRedoable {

    private static final long serialVersionUID = -4093867960954400453L;

    private List<IAtom> deletedAtoms;
    private List<List<IBond>> deletedBondss;
    private List<Map<IBond, Integer>> bondsWithReplacedAtoms;
    private IChemModelRelay chemModelRelay;
    private String type;
    private List<IAtomContainer> containers;
    private List<IAtomContainer> droppedContainers;

    private List<IAtom> mergedPartnerAtoms;

    Map<Integer, Map<Integer, Integer>> oldRgrpHash;
    Map<Integer, Map<Integer, Integer>> newRgrpHash;

    /**
     * @param deletedAtoms           The atoms which were deleted during the merge.
     * @param containers             containers participating in the merge
     * @param droppedContainers      containers dropped on merge of 2 separate containers
     * @param deletedBondss          The bonds which were deleted to merge each individual atom.
     * @param bondsWithReplacedAtoms For each atom merge, for each bond in deletedBondss, which of the atoms (0 or 1) in that bond was deletedAtom.
     * @param mergedPartnerAtoms     The atoms which replace deletedAtoms.
     * @param type                   The string returned as presentation name.
     * @param chemModelRelay         The current chemModelRelay.
     */
    public MergeMoleculesEdit(List<IAtom> deletedAtoms,
                              List<IAtomContainer> containers,
                              List<IAtomContainer> droppedContainers,
                              List<List<IBond>> deletedBondss,
                              List<Map<IBond, Integer>> bondsWithReplacedAtoms,
                              List<IAtom> mergedPartnerAtoms,
                              Map<Integer, Map<Integer, Integer>> oldRgrpHash, Map<Integer, Map<Integer, Integer>> newRgrpHash, String type, IChemModelRelay chemModelRelay) {
        this.deletedAtoms = deletedAtoms;
        this.deletedBondss = deletedBondss;
        this.bondsWithReplacedAtoms = bondsWithReplacedAtoms;
        this.chemModelRelay = chemModelRelay;
        this.mergedPartnerAtoms = mergedPartnerAtoms;
        this.type = type;
        this.containers = containers;
        this.droppedContainers = droppedContainers;
        this.oldRgrpHash = oldRgrpHash;
        this.newRgrpHash = newRgrpHash;
    }

    public void redo() throws CannotRedoException {
        // put everything back together
        int n = deletedAtoms.size();
        for (int i = 0; i < n; i++) {
            IAtomContainer containerWithMerge = containers.get(i);
            IAtomContainer droppedContainer = droppedContainers.get(i); // can be null
            if (droppedContainer != null) {
                containerWithMerge.add(droppedContainer);
                chemModelRelay.getChemModel().getMoleculeSet().removeAtomContainer(droppedContainer);
            }
        }

        // now remove the bonds
        for (int i = 0; i < n; i++) {
            IAtomContainer containerWithMerge = containers.get(i);
            for (IBond bond : deletedBondss.get(i)) {
                containerWithMerge.removeBond(bond);
            }
        }

        // update connectivity
        for (int i = 0; i < n; i++) {
            IAtomContainer container = containers.get(i);
            for (IBond bond : bondsWithReplacedAtoms.get(i).keySet()) {
                bond = container.getBond(container.indexOf(bond)); // resync
                bond.setAtom(mergedPartnerAtoms.get(i),
                             bondsWithReplacedAtoms.get(i).get(bond));
            }
        }

        // now delete the atoms
        for (int i = 0; i < n; i++) {
            IAtomContainer containerWithMerge = containers.get(i);
            containerWithMerge.removeAtomOnly(deletedAtoms.get(i));
        }

        // update any atoms that had changed bonding
        for (int i=0; i<n; i++) {
            for (IBond bond : bondsWithReplacedAtoms.get(i).keySet())
                chemModelRelay.updateAtoms(bond);
            for (IBond bond : deletedBondss.get(i)) {
                chemModelRelay.updateAtom(bond.getBegin());
                chemModelRelay.updateAtom(bond.getEnd());
            }
            chemModelRelay.updateAtom(mergedPartnerAtoms.get(i));
        }

        if (chemModelRelay.getRGroupHandler() != null) {
            chemModelRelay.getRGroupHandler().restoreFromHash(newRgrpHash, chemModelRelay.getChemModel().getMoleculeSet());
        }
    }

    public void undo() throws CannotUndoException {
        int n = deletedAtoms.size();
        assert n == mergedPartnerAtoms.size();

        // add all atoms back on first
        for (int i = 0; i < n; i++) {
            IAtomContainer container = containers.get(i);
            container.addAtom(deletedAtoms.get(i));
        }

        // add all the bonds back
        for (int i = 0; i < n; i++) {
            IAtomContainer containerWithMerge = containers.get(i);
            for (IBond bond : deletedBondss.get(i)) {
                containerWithMerge.addBond(bond);
            }
        }

        // update connectivity
        for (int i = 0; i < n; i++) {
            //Put dropped atom and bond back into atc1
            IAtomContainer container = containers.get(i);
            for (Map.Entry<IBond, Integer> e : bondsWithReplacedAtoms.get(i).entrySet()) {
                IBond bond = e.getKey();
                int idx = e.getValue();
                bond = container.getBond(container.indexOf(bond)); // resync
                bond.setAtom(deletedAtoms.get(i), idx);
            }
        }

        for (int i = 0; i < n; i++) {
            IAtomContainer containerWithMerge = containers.get(i);
            IAtomContainer droppedContainer = droppedContainers.get(i); // can be null

            if (droppedContainer != null) {
                //remove from atc1 what was merged in from atc2
                for (IAtom mAt : droppedContainer.atoms()) {
                    if (containerWithMerge.contains(mAt)) {
                        containerWithMerge.removeAtomOnly(mAt);
                    }
                }
                for (IBond mBond : droppedContainer.bonds()) {
                    if (containerWithMerge.contains(mBond)) {
                        containerWithMerge.removeBond(mBond);
                    }
                }
                //restore removed container atc2
                chemModelRelay.getChemModel().getMoleculeSet().addAtomContainer(droppedContainer);
            }
        }

        // update any atoms that had changed bonding
        for (int i=0; i<n; i++) {
            for (IBond bond : bondsWithReplacedAtoms.get(i).keySet())
                chemModelRelay.updateAtoms(bond);
            for (IBond bond : deletedBondss.get(i))
                chemModelRelay.updateAtoms(bond);
            chemModelRelay.updateAtom(deletedAtoms.get(i));
            chemModelRelay.updateAtom(mergedPartnerAtoms.get(i));
        }

        if (chemModelRelay.getRGroupHandler() != null) {
            chemModelRelay.getRGroupHandler().restoreFromHash(oldRgrpHash, chemModelRelay.getChemModel().getMoleculeSet());
        }
    }

    public boolean canRedo() {
        return true;
    }

    public boolean canUndo() {
        return true;
    }

    public String description() {
        return type;
    }
}
