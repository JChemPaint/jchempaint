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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.matchers.RGroup;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.controller.undoredo.*;
import org.openscience.jchempaint.rgroups.RGroupHandler;

/**
 * A class returning Swing-Implementations of all the undo-redo edits
 */
public class SwingUndoRedoFactory implements IUndoRedoFactory {

    public IUndoRedoable getAddAtomsAndBondsEdit(IChemModel chemModel,
                                                 AtomBondSet undoRedoSet,
                                                 IAtomContainer removedAtomContainer,
                                                 String type,
                                                 IChemModelRelay c2dm) {
        return new SwingUndoableEdit(new AddAtomsAndBondsEdit(chemModel, undoRedoSet, removedAtomContainer, type, c2dm));
    }

    public IUndoRedoable getAdjustBondOrdersEdit(Map<IBond, IBond.Order[]> orderChanges, Map<IBond, IBond.Display[]> displayChanges, String type, IChemModelRelay chemModelRelay) {
        return new SwingUndoableEdit(new AdjustBondOrdersEdit(orderChanges, Collections.emptyMap(), displayChanges, type, chemModelRelay));
    }

    public IUndoRedoable getChangeAtomSymbolEdit(IAtom atom, String formerSymbol,
                                                 String symbol, String type, IChemModelRelay chemModelRelay) {
        return new SwingUndoableEdit(new ChangeAtomSymbolEdit(atom, formerSymbol, symbol, type, chemModelRelay));
    }

    public IUndoRedoable getChangeChargeEdit(IAtom atomInRange,
                                             int formerCharge, int newCharge, String type, IChemModelRelay chemModelRelay) {
        return new SwingUndoableEdit(new ChangeChargeEdit(atomInRange, formerCharge, newCharge, type, chemModelRelay));
    }

    public IUndoRedoable getMoveAtomEdit(IAtomContainer undoRedoSet,
                                         Vector2d offset, String type) {
        return new SwingUndoableEdit(new MoveAtomEdit(undoRedoSet, offset, type));
    }

    public IUndoRedoable getRemoveAtomsAndBondsEdit(IChemModel chemModel,
                                                    AtomBondSet undoRedoSet, String type, IChemModelRelay chemModelRelay) {
        return new SwingUndoableEdit(new RemoveAtomsAndBondsEdit(chemModel, undoRedoSet, type, chemModelRelay));
    }

    public IUndoRedoable getReplaceAtomEdit(IChemModel chemModel,
                                            IAtom oldAtom, IAtom newAtom, String type) {
        return new SwingUndoableEdit(new ReplaceAtomEdit(chemModel, oldAtom, newAtom, type));
    }

    public IUndoRedoable getSingleElectronEdit(
            IAtomContainer relevantContainer,
            IElectronContainer electronContainer, boolean addSingleElectron,
            IChemModelRelay chemModelRelay, IAtom atom, String type) {
        return new SwingUndoableEdit(new AddSingleElectronEdit(relevantContainer, electronContainer,
                                                               addSingleElectron, chemModelRelay, atom, type));
    }

    public IUndoRedoable getChangeIsotopeEdit(IAtom atom,
                                              Integer formerIsotopeNumber, Integer newIstopeNumber, String type) {
        return new SwingUndoableEdit(new ChangeIsotopeEdit(atom, formerIsotopeNumber, newIstopeNumber, type));
    }

    public IUndoRedoable getClearAllEdit(IChemModel chemModel,
                                         IAtomContainerSet som, IReactionSet sor, String type) {
        return new SwingUndoableEdit(new ClearAllEdit(chemModel, som, sor, type));
    }

    public IUndoRedoable getChangeCoordsEdit(Map<IAtom, Point2d[]> atomCoordsMap,
                                             Map<IBond, IBond.Stereo> bondStereo,
                                             String type) {
        return new SwingUndoableEdit(new ChangeCoordsEdit(atomCoordsMap, bondStereo, type));
    }

    public IUndoRedoable getMakeReactantOrProductInNewReactionEdit(IChemModel chemModel,
                                                                   IAtomContainer ac, IAtomContainer oldcontainer, boolean reactantOrProduct, String type) {
        return new SwingUndoableEdit(new MakeReactantOrProductInNewReactionEdit(chemModel, ac, oldcontainer, reactantOrProduct, type));
    }

    public IUndoRedoable getMakeReactantOrProductInExistingReactionEdit(
            IChemModel chemModel, IAtomContainer newContainer,
            IAtomContainer oldcontainer, String s, boolean reactantOrProduct,
            String type) {
        return new SwingUndoableEdit(new MakeReactantOrProductInExistingReactionEdit(chemModel, newContainer, oldcontainer, s, reactantOrProduct, type));
    }

    public IUndoRedoable getMergeMoleculesEdit(List<IAtom> deletedAtom, List<IAtomContainer> containers, List<IAtomContainer> droppedContainers,
                                               List<List<IBond>> deletedBonds, List<Map<IBond, Integer>> bondsWithReplacedAtom, Vector2d offset, List<IAtom> atomwhichwasmoved,
                                               IUndoRedoable moveundoredo, Map<Integer, Map<Integer, Integer>> oldRgrpHash, Map<Integer, Map<Integer, Integer>> newRgrpHash, String type, IChemModelRelay c2dm) {
        return new SwingUndoableEdit(new MergeMoleculesEdit(deletedAtom, containers, droppedContainers, deletedBonds, bondsWithReplacedAtom, offset, atomwhichwasmoved, moveundoredo, oldRgrpHash, newRgrpHash, type, c2dm));
    }

    public IUndoRedoable getChangeHydrogenCountEdit(
            Map<IAtom, Integer[]> atomHydrogenCountsMap, String type) {
        return new SwingUndoableEdit(new ChangeHydrogenCountEdit(atomHydrogenCountsMap, type));
    }

    public IUndoRedoable getLoadNewModelEdit(IChemModel chemModel, IChemModelRelay relay,
                                             IAtomContainerSet oldsom, IReactionSet oldsor, IAtomContainerSet newsom,
                                             IReactionSet newsor, String type) {
        return new SwingUndoableEdit(new LoadNewModelEdit(chemModel, relay, oldsom, oldsor, newsom, newsor, type));
    }

    public IUndoRedoable getChangeValenceEdit(IAtom atomInRange,
                                              Integer formerValence, Integer valence, String text,
                                              IChemModelRelay chemModelRelay) {
        return new SwingUndoableEdit(new ChangeValenceEdit(atomInRange, formerValence, valence,
                                                           text, chemModelRelay));
    }

    public IUndoRedoable getRGroupEdit(String type, boolean isNewRGroup, IChemModelRelay hub, RGroupHandler rgrpHandler
            , Map<IAtom, IAtomContainer> existingAtomDistr, Map<IBond, IAtomContainer> existingBondDistr
            , IAtomContainer existingRoot, Map<IAtom, Map<Integer, IBond>> oldRootApos,
                                       Map<IAtom, Map<Integer, IBond>> newRootApos,
                                       Map<RGroup, Map<Integer, IAtom>> existingRGroupApo,
                                       Map<Integer, RGroupList> rgroupLists, IAtomContainer userSelection) {
        return new SwingUndoableEdit(new RGroupEdit(type, isNewRGroup, hub, rgrpHandler
                , existingAtomDistr, existingBondDistr, existingRoot, oldRootApos, newRootApos
                , existingRGroupApo, rgroupLists, userSelection));

    }
}
