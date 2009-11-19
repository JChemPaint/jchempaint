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

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;
import org.openscience.jchempaint.controller.IControllerModel;

/**
 * @cdk.module controlextra
 * @cdk.svnrev  $Revision: 13311 $
 */
public class MakeReactantOrProductInNewReactionEdit implements IUndoRedoable {

    private static final long serialVersionUID = -7667903450980188402L;

	private IAtomContainer movedContainer;

	private IAtomContainer oldContainer;

	private String type;
	
	private IChemModel chemModel;
	
	private String reactionID;
	
	private boolean reactantOrProduct;
	

	/**
	 * @param chemModel
	 * @param undoRedoContainer
	 * @param c2dm The controller model; if none, set to null
	 */
	public MakeReactantOrProductInNewReactionEdit(IChemModel chemModel, IAtomContainer ac, IAtomContainer oldcontainer, boolean reactantOrProduct, String type) {
		this.type = type;
		this.movedContainer = ac;
		this.oldContainer = oldcontainer;
		this.chemModel = chemModel;
		this.reactionID = ReactionSetManipulator.getReactionByAtomContainerID(chemModel.getReactionSet(), movedContainer.getID()).getID();
		this.reactantOrProduct = reactantOrProduct;
	}

	public void redo() {
		chemModel.getMoleculeSet().removeAtomContainer(movedContainer);
		IReaction reaction = chemModel.getBuilder().newReaction();
		reaction.setID(reactionID);
		IMolecule mol=chemModel.getBuilder().newMolecule(movedContainer);
		mol.setID(movedContainer.getID());
		if(reactantOrProduct)
			reaction.addReactant(mol);
		else
			reaction.addProduct(mol);
		if(chemModel.getReactionSet()==null)
			chemModel.setReactionSet(chemModel.getBuilder().newReactionSet());
		chemModel.getReactionSet().addReaction(reaction);
		chemModel.getMoleculeSet().removeAtomContainer(oldContainer);
	}

	public void undo() {
		if(chemModel.getMoleculeSet()==null)
			chemModel.setMoleculeSet(chemModel.getBuilder().newMoleculeSet());
		chemModel.getMoleculeSet().addAtomContainer(oldContainer);
		chemModel.getReactionSet().removeReaction(ReactionSetManipulator.getReactionByAtomContainerID(chemModel.getReactionSet(), movedContainer.getID()));
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
