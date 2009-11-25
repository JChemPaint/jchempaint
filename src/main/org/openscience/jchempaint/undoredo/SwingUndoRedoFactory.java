/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 2008 Stefan Kuhn
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
package org.openscience.jchempaint.undoredo;

import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;

/**
 * A class returning Swing-Implementations of all the undo-redo edits
 *
 */
public class SwingUndoRedoFactory implements IUndoRedoFactory {

	public IUndoRedoable getAddAtomsAndBondsEdit(IChemModel chemModel,
			IAtomContainer undoRedoContainer, IAtomContainer removedAtomContainer, String type, IChemModelRelay c2dm) {
		return new SwingAddAtomsAndBondsEdit(chemModel, undoRedoContainer, removedAtomContainer, type, c2dm);
	}
	
	public IUndoRedoable getAdjustBondOrdersEdit(Map<IBond, IBond.Order[]> changedBonds, Map<IBond, IBond.Stereo[]> changedBondsStereo, String type, IChemModelRelay chemModelRelay){
		return new SwingAdjustBondOrdersEdit(changedBonds, changedBondsStereo, type, chemModelRelay);
	}

	public IUndoRedoable getChangeAtomSymbolEdit(IAtom atom, String formerSymbol,
			String symbol, String type, IChemModelRelay chemModelRelay) {
		return new SwingChangeAtomSymbolEdit(atom, formerSymbol, symbol, type, chemModelRelay);
	}

	public IUndoRedoable getChangeChargeEdit(IAtom atomInRange,
			int formerCharge, int newCharge, String type, IChemModelRelay chemModelRelay) {
		return new SwingChangeChargeEdit(atomInRange, formerCharge, newCharge, type, chemModelRelay);
	}

	public IUndoRedoable getMoveAtomEdit(IAtomContainer undoRedoContainer,
			Vector2d offset, String type) {
		return new SwingMoveAtomEdit(undoRedoContainer, offset, type);
	}

	public IUndoRedoable getRemoveAtomsAndBondsEdit(IChemModel chemModel,
			IAtomContainer undoRedoContainer, String type, IChemModelRelay chemModelRelay) {
		return new SwingRemoveAtomsAndBondsEdit(chemModel, undoRedoContainer, type, chemModelRelay);
	}

	public IUndoRedoable getReplaceAtomEdit(IChemModel chemModel,
			IAtom oldAtom, IAtom newAtom, String type) {
		return new SwingReplaceAtomEdit(chemModel, oldAtom, newAtom, type);
	}

	public IUndoRedoable getSingleElectronEdit(
			IAtomContainer relevantContainer,
			IElectronContainer electronContainer, boolean addSingleElectron,
			IChemModelRelay chemModelRelay, IAtom atom, String type) {
		return new SwingConvertToRadicalEdit(relevantContainer, electronContainer, 
		        addSingleElectron, chemModelRelay, atom, type);
	}

	public IUndoRedoable getChangeIsotopeEdit(IAtom atom,
			Integer formerIsotopeNumber, Integer newIstopeNumber, String type) {
		return new SwingChangeIsotopeEdit(atom, formerIsotopeNumber, newIstopeNumber, type);
	}

	public IUndoRedoable getClearAllEdit(IChemModel chemModel,
			IMoleculeSet som, IReactionSet sor, String type) {
		return new SwingClearAllEdit(chemModel, som, sor, type);
	}

	public IUndoRedoable getChangeCoordsEdit(Map<IAtom, Point2d[]> atomCoordsMap,
			String type) {
		return new SwingChangeCoordsEdit(atomCoordsMap, type);
	}

	public IUndoRedoable getMakeReactantOrProductInNewReactionEdit(IChemModel chemModel,
			IAtomContainer ac, IAtomContainer oldcontainer, boolean reactantOrProduct, String type) {
		return new SwingMakeReactantOrProductInNewReactionEdit(chemModel, ac, oldcontainer, reactantOrProduct, type);
	}

	public IUndoRedoable getMakeReactantOrProductInExistingReactionEdit(
			IChemModel chemModel, IAtomContainer newContainer,
			IAtomContainer oldcontainer, String s, boolean reactantOrProduct,
			String type) {
		return new SwingMakeReactantInExistingReactionEdit(chemModel, newContainer, oldcontainer, s, reactantOrProduct, type);
	}

	public IUndoRedoable getMergeMoleculesEdit(List<IAtom> deletedAtom, List<IAtomContainer> containerWhereAtomWasIn, List<List<IBond>> deletedBonds, List<Map<IBond, Integer>> bondsWithReplacedAtom, Vector2d offset, List<IAtom> atomwhichwasmoved, IUndoRedoable moveundoredo, String type, IChemModelRelay c2dm) {
		return new SwingMergeMoleculesEdit( deletedAtom, containerWhereAtomWasIn, deletedBonds, bondsWithReplacedAtom, offset, atomwhichwasmoved, moveundoredo, type, c2dm);
	}

	public IUndoRedoable getChangeHydrogenCountEdit(
			Map<IAtom, Integer[]> atomHydrogenCountsMap, String type) {
		return new SwingChangeHydrogenCountEdit(atomHydrogenCountsMap, type);
	}

	public IUndoRedoable getLoadNewModelEdit(IChemModel chemModel,
			IMoleculeSet oldsom, IReactionSet oldsor, IMoleculeSet newsom,
			IReactionSet newsor, String type) {
		return new SwingLoadNewModelEdit(chemModel, oldsom, oldsor, newsom, newsor, type);
	}

	public IUndoRedoable getChangeValenceEdit(IAtom atomInRange,
			Integer formerValence, Integer valence, String text,
			IChemModelRelay chemModelRelay) {
		return new SwingChangeValenceEdit(atomInRange, formerValence, valence, 
				text, chemModelRelay);
	}
}
