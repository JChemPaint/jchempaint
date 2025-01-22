/* $RCSfile$
 * $Author: egonw $
 * $Date: 2008-05-12 07:29:49 +0100 (Mon, 12 May 2008) $
 * $Revision: 10979 $
 *
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 10979 $
 */
public class RemoveAtomsAndBondsEdit implements IUndoRedoable {

    private static final long serialVersionUID = -143712173063846054L;

    private String type;

	private AtomBondSet undoRedoSet;

	private IChemModel chemModel;

	private IAtomContainer container;
	
	private IChemModelRelay chemModelRelay=null;

	public RemoveAtomsAndBondsEdit(IChemModel chemModel,
								   AtomBondSet undoRedoSet, String type, IChemModelRelay chemModelRelay) {
		this.chemModel = chemModel;
		this.undoRedoSet = undoRedoSet;
		this.container = chemModel.getBuilder().newInstance(IAtomContainer.class);
    	Iterator<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
    	while (containers.hasNext()) {
    		container.add((IAtomContainer)containers.next());
    	}
		this.type = type;
		this.chemModelRelay=chemModelRelay;
	}

	public void redo() {
		for (IBond bond : undoRedoSet.bonds()) {
			container.removeBond(bond);
		}
		for (IAtom atom : undoRedoSet.atoms()) {
			container.removeAtom(atom);
		}
		chemModelRelay.updateAtoms(container, container.atoms());
		IAtomContainer molecule = container.getBuilder().newInstance(IAtomContainer.class,container);
		IAtomContainerSet moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setMoleculeSet(moleculeSet);
		if (chemModelRelay.getRGroupHandler()!=null) {
			try {
				chemModelRelay.getRGroupHandler().adjustAtomContainers(moleculeSet);
			} catch (CDKException e) {
				e.printStackTrace();
				chemModelRelay.unsetRGroupHandler();
			}
		}
	}

	public void undo() {
		for (IAtom atom : undoRedoSet.atoms()) {
			container.addAtom(atom);
		}
		for (IBond bond : undoRedoSet.bonds()) {
			container.addBond(bond);
		}
		chemModelRelay.updateAtoms(container, container.atoms());
		IAtomContainer molecule = container.getBuilder().newInstance(IAtomContainer.class,container);
		IAtomContainerSet moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setMoleculeSet(moleculeSet);
		if (chemModelRelay.getRGroupHandler()!=null) {
			try {
				chemModelRelay.getRGroupHandler().adjustAtomContainers(moleculeSet);
			} catch (CDKException e) {
				chemModelRelay.unsetRGroupHandler();
				e.printStackTrace();
			}
		}
	}

	public boolean canRedo() {
		return true;
	}

	public boolean canUndo() {
		return true;
	}

	public String getPresentationName() {
		return type;
	}

}
