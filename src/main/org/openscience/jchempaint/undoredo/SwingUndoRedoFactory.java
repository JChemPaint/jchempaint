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

import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.controller.IControllerModel;
import org.openscience.cdk.controller.undoredo.IUndoRedoFactory;
import org.openscience.cdk.controller.undoredo.IUndoRedoable;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * A class returning Swing-Implementations of all the undo-redo edits
 *
 */
public class SwingUndoRedoFactory implements IUndoRedoFactory {

	public IUndoRedoable getAddAtomsAndBondsEdit(IChemModel chemModel,
			IAtomContainer undoRedoContainer, String type, IControllerModel c2dm) {
		return new SwingAddAtomsAndBondsEdit(chemModel, undoRedoContainer, type, c2dm);
	}
	
	public IUndoRedoable getAdjustBondOrdersEdit(Map<IBond, IBond.Order[]> changedBonds, Map<IBond, Integer[]> changedBondsStereo, String type){
		return new SwingAdjustBondOrdersEdit(changedBonds, changedBondsStereo, type);
	}

	public IUndoRedoable getChangeAtomSymbolEdit(IAtom atom, String formerSymbol,
			String symbol, String type) {
		return new SwingChangeAtomSymbolEdit(atom, formerSymbol, symbol, type);
	}

	public IUndoRedoable getChangeChargeEdit(IAtom atomInRange,
			int formerCharge, int newCharge, String type) {
		return new SwingChangeChargeEdit(atomInRange, formerCharge, newCharge, type);
	}

	public IUndoRedoable getMoveAtomEdit(IAtomContainer undoRedoContainer,
			Vector2d offset, String type) {
		return new SwingMoveAtomEdit(undoRedoContainer, offset, type);
	}

	public IUndoRedoable getRemoveAtomsAndBondsEdit(IChemModel chemModel,
			IAtomContainer undoRedoContainer, String type) {
		return new SwingRemoveAtomsAndBondsEdit(chemModel, undoRedoContainer, type);
	}

	public IUndoRedoable getCleanUpEdit(
			Map<IAtom, Point2d[]> atomCoordsMap, String type) {
		return new SwingCleanUpEdit(atomCoordsMap, type);
	}

	public IUndoRedoable getReplaceAtomEdit(IChemModel chemModel,
			IAtom oldAtom, IAtom newAtom, String type) {
		return new SwingReplaceAtomEdit(chemModel, oldAtom, newAtom, type);
	}

	public IUndoRedoable getConvertToRadicalEdit(
			IAtomContainer relevantContainer,
			IElectronContainer electronContainer, String type) {
		return new SwingConvertToRadicalEdit(relevantContainer, electronContainer, type);
	}

	public IUndoRedoable getChangeIsotopeEdit(IAtom atom,
			Integer formerIsotopeNumber, Integer newIstopeNumber, String type) {
		return new SwingChangeIsotopeEdit(atom, formerIsotopeNumber, newIstopeNumber, type);
	}

	public IUndoRedoable getClearAllEdit(IChemModel chemModel,
			IMoleculeSet som, IReactionSet sor, String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
