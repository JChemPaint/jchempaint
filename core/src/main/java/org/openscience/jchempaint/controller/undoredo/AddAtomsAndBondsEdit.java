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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @cdk.module controlbasic @cdk.svnrev $Revision: 13311 $
 */
public class AddAtomsAndBondsEdit implements IUndoRedoable {

	private static final long serialVersionUID = -7667903450980188402L;
	private IChemModel chemModel;
	private AtomBondSet undoRedoSet;
	private String type;
	private IChemModelRelay chemModelRelay = null;
	private IAtomContainer containerToAddTo;
	private IAtomContainer removedAtomContainer;   // TODO - remove this, also from constructor. see markr's comments

	/**
	 * @param chemModel
	 * @param undoRedoSet
	 * @param removedAtomContainer atomContainer which has been removed from
	 * setOfAtomContainers as part of the edit, if none, null.
	 * @param chemModelRelay
	 */
	public AddAtomsAndBondsEdit(IChemModel chemModel,
								AtomBondSet undoRedoSet,
								IAtomContainer removedAtomContainer,
								String type,
								IChemModelRelay chemModelRelay) {
		this.chemModel = chemModel;
		this.undoRedoSet = undoRedoSet;
		this.type = type;
		this.chemModelRelay = chemModelRelay;
		this.removedAtomContainer = removedAtomContainer;
	}

	public void redo() {

        IAtomContainer connectedContainer = isConnectedToExistingFragment(undoRedoSet);

        if (connectedContainer == null &&
            containerToAddTo != null &&
            chemModel.getMoleculeSet().getMultiplier(containerToAddTo) == -1) {
			chemModel.getMoleculeSet().addAtomContainer(containerToAddTo);
            connectedContainer = containerToAddTo;
		}

        if (connectedContainer == null) {
            connectedContainer = DefaultChemObjectBuilder.getInstance()
                                                         .newAtomContainer();
            chemModel.getMoleculeSet().addAtomContainer(connectedContainer);
        }

		//markr: this code creates problems when dragging a bond across a structure, so that it merges with itself.. 
		//if(removedAtomContainer!=null){
		//    containerToAddTo.add(removedAtomContainer);
		//    chemModel.getMoleculeSet().removeAtomContainer(removedAtomContainer);
		//}

		for (IAtom atom : undoRedoSet.atoms()) {
            connectedContainer.addAtom(atom);
		}

		for (IBond bond : undoRedoSet.bonds()) {
            connectedContainer.addBond(bond);
		}

		for (IBond bond : undoRedoSet.bonds()) {
			chemModelRelay.updateAtoms(bond);
		}
        for (IAtom atom : undoRedoSet.atoms()) {
            chemModelRelay.updateAtom(atom);
        }
	}

    // JWM: this is really dodgey, we need to ditch ChemModel!
    private IAtomContainer isConnectedToExistingFragment(AtomBondSet undoRedoSet) {
        for (IBond bond : undoRedoSet.bonds()) {
            if (!undoRedoSet.contains(bond.getBegin()))
                return ChemModelManipulator.getRelevantAtomContainer(chemModel, bond.getBegin());
            if (!undoRedoSet.contains(bond.getEnd()))
                return ChemModelManipulator.getRelevantAtomContainer(chemModel, bond.getEnd());
        }
        return null;
    }

    public void undo() {

		//markr: this code creates problems when dragging a bond across a structure, so that it merges with itself.. 
		//if(removedAtomContainer!=null){
		//    ChemModelManipulator.getRelevantAtomContainer(chemModel, removedAtomContainer.getAtom(0)).remove(removedAtomContainer);
		//    chemModel.getMoleculeSet().addAtomContainer(removedAtomContainer);
		//}

		IBond[] bonds = new IBond[undoRedoSet.getBondCount()];
		int idx = 0;
		for (IBond bond : undoRedoSet.bonds()) {
			bonds[idx++] = bond;
		}

		List<IAtom> leftOver = new ArrayList<>();

		for (IBond bond : bonds) {
			containerToAddTo = ChemModelManipulator.getRelevantAtomContainer(chemModel, bond);
			if (containerToAddTo != null) {
				containerToAddTo.removeBond(bond);

				if (undoRedoSet.contains(bond.getBegin()) &&
						!undoRedoSet.contains(bond.getEnd())) {
					leftOver.add(bond.getEnd());
				} else if (undoRedoSet.contains(bond.getEnd()) &&
							     !undoRedoSet.contains(bond.getBegin())) {
					leftOver.add(bond.getBegin());
				}
			}
		}

		IAtom[] atoms = new IAtom[undoRedoSet.getAtomCount()];
		idx = 0;
		for (IAtom atom : undoRedoSet.atoms()) {
			atoms[idx++] = atom;
		}
		for (IAtom atom : atoms) {
			containerToAddTo = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom);
			if (containerToAddTo != null) {
				containerToAddTo.removeAtom(atom);
			}
		}

		if (chemModelRelay.getIChemModel().getMoleculeSet().getAtomContainerCount() > 1) {
			ControllerHub.removeEmptyContainers(chemModelRelay.getIChemModel());
		}

		for (IAtomContainer container : ChemModelManipulator.getAllAtomContainers(chemModel)) {
			chemModelRelay.updateAtoms(container, container.atoms());
		}


		// select the last atom if the hotspot was deleted by this undo
		JChemPaintRendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();

		if (!leftOver.isEmpty()) {
			leftOver.sort(Comparator.comparing(IAtom::getIndex));
			IAtom hgAtom = leftOver.get(leftOver.size() - 1);
			containerToAddTo = ChemModelManipulator.getRelevantAtomContainer(chemModel, hgAtom);
			model.setHighlightedAtom(containerToAddTo.getAtom(containerToAddTo.indexOf(hgAtom)));
		} else {
			if (undoRedoSet.contains(model.getHighlightedAtom())) {
				for (IAtomContainer container : ChemModelManipulator.getAllAtomContainers(chemModelRelay.getIChemModel())) {
					if (!container.isEmpty()) {
						chemModelRelay.getRenderer().getRenderer2DModel()
													.setHighlightedAtom(container.getAtom(container.getAtomCount() - 1));
					}
				}
			}
		}

	}

	public boolean canRedo() {
		return true;
	}

	public boolean canUndo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#getPresentationName()
	 */
	public String description() {
		return type;
	}
}
