/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *  Copyright (C) 2009 Arvid Berg
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
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
 *
 */
package org.openscience.jchempaint.controller.undoredo;

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

/**
 * This interface needs to be implemented in an application to return instances of classes 
 * extending the implementations of IUndoRedoable suitable for the respective gui framework 
 * (e. g. swing or swt).
 * 
 * @cdk.module control
 */
public interface IUndoRedoFactory {
	public IUndoRedoable getAddAtomsAndBondsEdit(IChemModel chemModel, IAtomContainer undoRedoContainer, IAtomContainer removedAtomContainer, String type, IChemModelRelay c2dm);
	public IUndoRedoable getAdjustBondOrdersEdit(Map<IBond,
		IBond.Order[]> changedBonds,
		Map<IBond, IBond.Stereo[]> changedBondsStereo, String type,
		IChemModelRelay chemModelRelay
	);
	public IUndoRedoable getChangeAtomSymbolEdit(IAtom atom, String formerSymbol, String symbol, String type, IChemModelRelay chemModelRelay);
	public IUndoRedoable getChangeChargeEdit(IAtom atomInRange, int formerCharge, int newCharge, String type, IChemModelRelay chemModelRelay);
	public IUndoRedoable getMoveAtomEdit(IAtomContainer undoRedoContainer, Vector2d offset, String type);
	public IUndoRedoable getRemoveAtomsAndBondsEdit(IChemModel chemModel, IAtomContainer undoRedoContainer, String type, IChemModelRelay chemModelRelay);
	public IUndoRedoable getReplaceAtomEdit(IChemModel chemModel, IAtom oldAtom, IAtom newAtom, String type);
	public IUndoRedoable getSingleElectronEdit(IAtomContainer relevantContainer, IElectronContainer electronContainer, boolean add, IChemModelRelay chemModelRelay, IAtom atom, String type);
	public IUndoRedoable getChangeIsotopeEdit(IAtom atom, Integer formerIsotopeNumber, Integer newIstopeNumber, String type);
	public IUndoRedoable getClearAllEdit(IChemModel chemModel, IMoleculeSet som, IReactionSet sor, String type);
	public IUndoRedoable getChangeCoordsEdit(Map<IAtom, Point2d[]> atomCoordsMap, String type);
	public IUndoRedoable getMakeReactantOrProductInNewReactionEdit(IChemModel chemModel, IAtomContainer ac, IAtomContainer oldcontainer, boolean reactantOrProduct, String type);
	public IUndoRedoable getMakeReactantOrProductInExistingReactionEdit(
			IChemModel chemModel, IAtomContainer newContainer,
			IAtomContainer oldcontainer, String s, boolean reactantOrProduct, String string);
	public IUndoRedoable getMergeMoleculesEdit(List<IAtom> deletedAtom, List<IAtomContainer> containerWhereAtomWasIn, List<List<IBond>> deletedBonds, List<Map<IBond, Integer>> bondsWithReplacedAtom, Vector2d offset, List<IAtom> atomwhichwasmoved, IUndoRedoable moveundoredo, String type, IChemModelRelay c2dm);
	public IUndoRedoable getChangeHydrogenCountEdit(Map<IAtom, Integer[]> atomHydrogenCountsMap, String type);
	public IUndoRedoable getLoadNewModelEdit(IChemModel chemModel, IMoleculeSet oldsom, IReactionSet oldsor, IMoleculeSet newsom, IReactionSet newsor, String type);
	public IUndoRedoable getChangeValenceEdit(IAtom atomInRange, Integer formerValence,
			Integer valence, String text, IChemModelRelay chemModelRelay);
}
