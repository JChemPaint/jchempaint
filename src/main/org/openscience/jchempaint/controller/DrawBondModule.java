/* $Revision: $ $Author:  $ $Date$
 *
 * Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.BondManipulator;


/**
 * @cdk.module controlbasic
 */
public class DrawBondModule extends ControllerModuleAdapter {

    private IAtom atomToBondFrom;
    private IAtom newAtom;
    private String ID;

    public DrawBondModule(IChemModelRelay relay) {
        super(relay);
    }

    private void cycleBondValence(IBond bond) {
        // special case : reset stereo bonds
        if (bond.getStereo() != IBond.Stereo.NONE) {
            bond.setStereo(IBond.Stereo.NONE);
            chemModelRelay.updateView();
            return;
        }

        // cycle the bond order up to maxOrder
        IBond.Order maxOrder = super.chemModelRelay.getController2DModel().getMaxOrder();
        if (BondManipulator.isLowerOrder(bond.getOrder(), maxOrder)) {
            BondManipulator.increaseBondOrder(bond);
        } else {
            bond.setOrder(IBond.Order.SINGLE);
        }
        chemModelRelay.updateView();
    }

    public void mouseClickedDown(Point2d worldCoordinate) {
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoordinate);
        IBond closestBond = chemModelRelay.getClosestBond(worldCoordinate);
        atomToBondFrom = null;
        newAtom = null;
        double dA = super.distanceToAtom(closestAtom, worldCoordinate);
        double dB = super.distanceToBond(closestBond, worldCoordinate);
        double dH = super.getHighlightDistance();
        
        if (noSelection(dA, dB, dH)) {
            atomToBondFrom = null;
            newAtom = addAtomTo(worldCoordinate);
        } else if (isBondOnlyInHighlightDistance(dA, dB, dH)) {
            this.cycleBondValence(closestBond);
        } else if (isAtomOnlyInHighlightDistance(dA, dB, dH)) {
            atomToBondFrom = closestAtom;
        } else {
            if (dA <= dB) {
                atomToBondFrom = closestAtom;
            } else {
                this.cycleBondValence(closestBond);
            }
        }

    }

    @Override
    public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo) {
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoordTo);
        // check distance from bondFrom and closestAtom to make it snap
        if (atomToBondFrom == null) {
            showBondTo(worldCoordTo);
        } else if (closestAtom != null 
                && closestAtom != atomToBondFrom
                && closestAtom != newAtom) {
            showBondTo(closestAtom.getPoint2d());
        } else {
            showBondTo(worldCoordTo);
        }

    }

    @Override
    public void mouseClickedUp(Point2d worldCoord) {
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
        if (atomToBondFrom == null) {
            showBondTo(worldCoord);
        } else if (closestAtom != null && closestAtom != atomToBondFrom
                && closestAtom != newAtom) {
            chemModelRelay.addBond(atomToBondFrom, closestAtom);
            chemModelRelay.removeAtomWithoutUndo(newAtom);
            chemModelRelay.updateView();
        } else {
            showBondTo(worldCoord);
        }
        atomToBondFrom = null;
        newAtom = null;
    }

    private void showBondTo(Point2d to) {
        if (newAtom == null && atomToBondFrom != null) {
            addAtomTo(to);
            newAtom = chemModelRelay.getClosestAtom(to);
            chemModelRelay.addBond(atomToBondFrom, newAtom);
        } else {
            chemModelRelay.moveToWithoutUndo(newAtom, to);
        }
        chemModelRelay.updateView();
    }

    private IAtom addAtomTo(Point2d to) {
        String atomType = chemModelRelay.getController2DModel()
                .getDrawElement();
        IAtom newAtom = chemModelRelay.addAtomWithoutUndo(atomType, to);
        chemModelRelay.updateView();
        return newAtom;
    }

    public String getDrawModeString() {
        return "DrawAtomBond";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
