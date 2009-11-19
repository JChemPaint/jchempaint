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

import java.util.Iterator;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 13311 $
 */
public class AddAtomsAndBondsEdit implements IUndoRedoable {

    private static final long serialVersionUID = -7667903450980188402L;

    private IChemModel chemModel;

	private IAtomContainer undoRedoContainer;

	private String type;
	
	private IChemModelRelay chemModelRelay=null;

	/**
	 * @param chemModel
	 * @param undoRedoContainer
	 * @param chemModelRelay The controller model; if none, set to null
	 */
	public AddAtomsAndBondsEdit(IChemModel chemModel,
			IAtomContainer undoRedoContainer, String type, IChemModelRelay chemModelRelay) {
		this.chemModel = chemModel;
		this.undoRedoContainer = undoRedoContainer;
		this.type = type;
		this.chemModelRelay=chemModelRelay;
	}

	public void redo() {
		IAtomContainer container = chemModel.getMoleculeSet().getMolecule(0);
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			IBond bond = undoRedoContainer.getBond(i);
			container.addBond(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom = undoRedoContainer.getAtom(i);
			container.addAtom(atom);
		}
		chemModelRelay.updateAtoms(container, container.atoms());
	}

	public void undo() {
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			IBond bond = undoRedoContainer.getBond(i);
			ChemModelManipulator.getRelevantAtomContainer(chemModel, bond).removeBond(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom = undoRedoContainer.getAtom(i);
			ChemModelManipulator.getRelevantAtomContainer(chemModel, atom).removeAtom(atom);
		}
		Iterator<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
    	while (containers.hasNext()) {
    		IAtomContainer container = (IAtomContainer)containers.next();
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
