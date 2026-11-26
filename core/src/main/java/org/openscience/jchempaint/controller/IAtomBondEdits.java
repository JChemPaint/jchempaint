/*
 * Copyright (C) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.jchempaint.controller;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.controller.IChemModelRelay.Direction;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.Collection;
import java.util.Map;

/**
 *  Interface for edit methods currently used by edit modules but should
 *  be replacereplaced by IEdit commands.
 * @author Arvid
 * @cdk.module control
 */
public interface IAtomBondEdits {
    /* Editing actions for atoms */
    AtomBondSet removeAtom(IAtom atom);
    AtomBondSet removeAtomWithoutUndo(IAtom atom);
    IAtom addAtom(String element, Point2d worldcoord, boolean makePseudoAtom);
    IAtom addAtomWithoutUndo(String element, Point2d worldcoord, boolean makePseudoAtom);
    IAtom addAtom(String element, IAtom atom, boolean makePseudoAtom);
    IAtom addAtomWithoutUndo(String element, IAtom atom, boolean makePseudoAtom);
    void moveToWithoutUndo(IAtom atom, Point2d point);
    /** Move a single atom, the selection include any other atoms which should
     *  also move, for example part of a contraction. */
    void moveSingleAtom(IAtom atom, Point2d from, Point2d to,
                        IChemObjectSelection selection,
                        boolean undoable);
    void moveBy(Collection<IAtom> atoms, Vector2d move, Vector2d totalmove);
    void setSymbol(IAtom atom, String symbol);
    void setCharge(IAtom atom, int charge);
    void setMassNumber(IAtom atom, int charge);
    void setImplicitHydrogenCount(IAtom atom, int intValue);
    void replaceAtom(IAtom atomnew, IAtom atomold);
    void replaceAtomWithoutUndo(IAtom atomnew, IAtom atomold);
    void addSingleElectron(IAtom atom);
    void removeSingleElectron(IAtom atom);
    void updateAtoms(IAtomContainer container, Iterable<IAtom> atoms);
    void updateAtom(IAtom atom);
    void updateAtoms(IBond bond);
    void mergeMolecules(Map<IAtom, Point2d[]> movedAtoms);

    /* Editing actions for bonds */
    IBond addBond(IAtom fromAtom, IAtom toAtom);
    void removeBondWithoutUndo(IBond bond);
    void removeBond(IBond bond);
    void moveToWithoutUndo(IBond bond, Point2d point);
    void moveSingleAtom(IBond bond, Point2d point);
    void changeBond(IBond bond, IBond.Order order, IBond.Display stereo);
    void addNewBond(Point2d worldCoordinate, boolean makePseudoAtom);
    void cycleBondValence(IBond bond);
    void cycleBondValence(IBond bond, IBond.Order order);
    boolean alternateBonds(IBond bond);
    void makeBondStereo(IBond bond, Direction desiredDirection);
    IBond makeNewStereoBond(IAtom atom, Direction desiredDirection);
}
