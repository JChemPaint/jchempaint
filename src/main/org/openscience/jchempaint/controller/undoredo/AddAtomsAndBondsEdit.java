/* $RCSfile$
 * $Author: gilleain $
 * $Date: 2008-11-26 16:01:05 +0000 (Wed, 26 Nov 2008) $
 * $Revision: 13311 $
 *
 * Copyright (C) 2005-2008 Tobias Helmus, Stefan Kuhn
 *           (C) 2010      Mark Rynbeek
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * Undo/Redoable Object that contains all information to reconstruct
 * the next/previous model state in the undo chain. Condition: between
 * state A and B may only happen additions of bonds and additions of atoms.
 * In case of mergers following from these additions better use another handler.
 */
public class AddAtomsAndBondsEdit implements IUndoRedoable {

	private static final long serialVersionUID = -7667903450980188402L;
	private IChemModel chemModel;
	private IAtomContainer undoRedoContainer;
	private String type;
	private IChemModelRelay chemModelRelay;
	private IAtomContainer containerToAddTo;		// set in undo()
	private IAtomContainer removedAtomContainer;

	/**
	 * Called from:
		ControllerHub.addAtom (urc, null, Add Atom) 1A
		ControllerHub.addNewBond (urc, null, Add Bond) 2A1B
		ControllerHub.makeNewStereoBond (urc, null, Add Stereo Bond) 1A1B
		ControllerHub.makeAllImplicitExplicit (urc, null, Make implicit Hs explicit) NANB
		ControllerHub.addPhenyl (new ring, null, Benzene) R
		ControllerHub.addRing (new ring, null, Ring N) R
		AddRingModule.mouseClickedUp (urc, null, Ring N) R
		ControllerHub.createAttachRing (new ring, null, Ring N) R
		JChemPaint.generateModel (molecule, null, Paste) M
		EnterElementSwingModule (new AC, null, Add Functional Group) AC
	 * This means that removedAtomContainer is never used.
	 *
	 * @param chemModel
	 * @param undoRedoContainer
	 * @param removedAtomContainer atomContainer which has been removed from
	 * setOfAtomContainers as part of the edit, if none, null.
	 * @param chemModelRelay
	 */
	public AddAtomsAndBondsEdit(IChemModel chemModel,
			IAtomContainer undoRedoContainer, IAtomContainer removedAtomContainer, String type, IChemModelRelay chemModelRelay) {
		this.chemModel = chemModel;
		this.undoRedoContainer = undoRedoContainer;
		this.type = type;
		this.chemModelRelay = chemModelRelay;
		this.removedAtomContainer = removedAtomContainer;
	}

	public void redo() {

		if (containerToAddTo != null && chemModel.getMoleculeSet().getMultiplier(containerToAddTo) == -1) {
			chemModel.getMoleculeSet().addAtomContainer(containerToAddTo);
		}

		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			IBond bond = undoRedoContainer.getBond(i);
			containerToAddTo.addBond(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom = undoRedoContainer.getAtom(i);
			containerToAddTo.addAtom(atom);
			chemModelRelay.updateAtom(atom);
		}
	}

	public void undo() {

		IBond[] bonds = new IBond[undoRedoContainer.getBondCount()];
		int idx = 0;
		for (IBond bond : undoRedoContainer.bonds()) {
			bonds[idx++] = bond;
		}
		for (IBond bond : bonds) {
			containerToAddTo = ChemModelManipulator.getRelevantAtomContainer(chemModel, bond);
			if (containerToAddTo != null) {
				containerToAddTo.removeBond(bond);
			}
		}

		IAtom[] atoms = new IAtom[undoRedoContainer.getAtomCount()];
		idx = 0;
		for (IAtom atom : undoRedoContainer.atoms()) {
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
	public String getPresentationName() {
		return type;
	}
}
