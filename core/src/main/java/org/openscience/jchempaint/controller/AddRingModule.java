/* $Revision: $ $Author:  $ $Date: $
 *
 * Copyright (C) 2007  Gilleain Torrance
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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.layout.RingPlacer;
import org.openscience.cdk.renderer.selection.AbstractSelection;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

import javax.vecmath.Point2d;
import java.util.Map;

/**
 * Adds an atom on the given location on mouseclick
 *
 * @author maclean
 * @cdk.module controlbasic
 */
public class AddRingModule extends ControllerModuleAdapter {

    private int ringSize;
    private boolean addingBenzene = false;
    private String ID;
    private RingPlacer ringPlacer = new RingPlacer();
    private static long drawTime = 0;
    private Point2d mouseLastMoved = null;

    public AddRingModule(IChemModelRelay chemModelRelay, int ringSize,
                         boolean addingBenzene) {
        super(chemModelRelay);
        this.ringSize = ringSize;
        this.addingBenzene = addingBenzene;
    }

    private IRing addRingToEmptyCanvas(Point2d p, boolean phantom) {
        if (addingBenzene) {
            return chemModelRelay.addPhenyl(p, phantom);
        } else {
            return chemModelRelay.addRing(ringSize, p, phantom);
        }
    }

    private IRing addRingToAtom(IAtom closestAtom, boolean phantom) {
        IRing newring;
        if (addingBenzene) {
            newring = chemModelRelay.addPhenyl(closestAtom, phantom);
        } else {
            newring = chemModelRelay.addRing(closestAtom, ringSize, phantom);
        }
        newring.removeAtom(closestAtom);
        return newring;
    }

    private IRing addRingToBond(IBond bond, boolean phantom) {
        IRing newring;
        if (addingBenzene) {
            newring = chemModelRelay.addPhenyl(bond, phantom);
        } else {
            newring = chemModelRelay.addRing(bond, ringSize, phantom);
        }
        newring.removeAtom(bond.getAtom(0));
        newring.removeAtom(bond.getAtom(1));
        newring.removeBond(bond);
        return newring;
    }

    public void mouseClickedDown(Point2d worldCoord, int modifiers) {
        mouseLastMoved = null;
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
        IBond closestBond = chemModelRelay.getClosestBond(worldCoord);

        IChemObject singleSelection = getHighlighted(worldCoord,
                                                     closestAtom, closestBond);

        if (singleSelection == null) {
            //we add the ring
            IRing newRing = this.addRingToEmptyCanvas(worldCoord, false);

            Map<IAtom, IAtom> mergeSet = chemModelRelay.getRenderer().getRenderer2DModel().getMerge();
            mergeSet.clear();

            //we look if it would merge
            for (IAtom atom : newRing.atoms()) {
                IAtom closestAtomInRing = this.chemModelRelay.getClosestAtom(atom);
                if (closestAtomInRing != null) {
                    mergeSet.put(atom, closestAtomInRing);
                }
            }

            // if we need to merge, we first move the ring so that the merge atoms
            // are exactly on top of each other - if not doing this, rings get distorted.
            for (Map.Entry<IAtom, IAtom> e : mergeSet.entrySet()) {
                IAtom atomOut = e.getKey();
                IAtom atomRep = e.getValue();
                atomOut.getPoint2d().sub(atomRep.getPoint2d());
                Point2d pointSub = new Point2d(atomOut.getPoint2d().x, atomOut.getPoint2d().y);
                for (IAtom atom : newRing.atoms()) {
                    atom.getPoint2d().sub(pointSub);
                }
            }

            AtomBondSet abset = new AtomBondSet(newRing);
            for (IAtom atom : mergeSet.keySet()) {
                abset.remove(atom);
                for (IBond bond : newRing.getConnectedBondsList(atom)) {
                    if (mergeSet.containsKey(bond.getOther(atom)))
                        abset.remove(bond);
                }
            }

            if (chemModelRelay.getUndoRedoFactory() != null && chemModelRelay.getUndoRedoHandler() != null) {
                IUndoRedoable undoredo = chemModelRelay.getUndoRedoFactory()
                                                       .getAddAtomsAndBondsEdit(chemModelRelay.getIChemModel(), abset, null, "Ring" + " " + ringSize, chemModelRelay);
                chemModelRelay.getUndoRedoHandler().postEdit(undoredo);
            }

            //and perform the merge
            chemModelRelay.mergeMolecules(null);
            chemModelRelay.getRenderer().getRenderer2DModel().getMerge().clear();
        } else if (singleSelection instanceof IAtom) {
            this.addRingToAtom((IAtom) singleSelection, false);
        } else if (singleSelection instanceof IBond) {
            this.addRingToBond((IBond) singleSelection, false);
        }

        if (singleSelection == null)
            setSelection(AbstractSelection.EMPTY_SELECTION);
        else
            setSelection(new SingleSelection<IChemObject>(singleSelection));

        chemModelRelay.updateView();
    }

    public void mouseClickedDownRight(Point2d worldCoord) {
        this.chemModelRelay.clearPhantoms();
        this.setSelection(AbstractSelection.EMPTY_SELECTION);
        chemModelRelay.getRenderer().getRenderer2DModel().getMerge().clear();
        this.chemModelRelay.updateView();
        this.escapeTheMode();
    }

    public void mouseMove(Point2d worldCoord) {
        if ((System.nanoTime() - drawTime) < 4000000) {
            return;
        }
        this.mouseLastMoved = worldCoord;
        this.chemModelRelay.clearPhantoms();
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
        IBond closestBond = chemModelRelay.getClosestBond(worldCoord);
        IChemObject singleSelection = getHighlighted(worldCoord,
                                                     closestAtom, closestBond);

        if (singleSelection == null) {
            this.addRingToEmptyCanvas(worldCoord, true);
        } else if (singleSelection instanceof IAtom) {
            this.addRingToAtom((IAtom) singleSelection, true);
        } else if (singleSelection instanceof IBond) {
            this.addRingToBond((IBond) singleSelection, true);
        }
        drawTime = System.nanoTime();
        this.chemModelRelay.updateView();
    }

    @Override
    public void updateView() {
        if (mouseLastMoved != null)
            mouseMove(mouseLastMoved);
    }

    public String getDrawModeString() {
        if (addingBenzene) {
            return "Benzene";
        } else {
            return "Ring" + " " + ringSize;
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

}
