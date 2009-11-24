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

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.layout.RingPlacer;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.renderer.selection.AbstractSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

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
    
    public AddRingModule(IChemModelRelay chemModelRelay, int ringSize,
            boolean addingBenzene) {
        super(chemModelRelay);
        this.ringSize = ringSize;
        this.addingBenzene = addingBenzene;
    }

    private IRing addRingToEmptyCanvas(Point2d p) {
        if (addingBenzene) {
            return chemModelRelay.addPhenyl(p, false);
        } else {
            return chemModelRelay.addRing(ringSize, p, false);
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

    public void mouseClickedDown(Point2d worldCoord) {
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
        IBond closestBond = chemModelRelay.getClosestBond(worldCoord);

        IChemObject singleSelection = getHighlighted( worldCoord,
                                                      closestAtom,closestBond );

        if (singleSelection == null) {
            //we add the ring
    		IRing newRing = this.addRingToEmptyCanvas(worldCoord);
            chemModelRelay.getRenderer().getRenderer2DModel().getMerge().clear();
            //we look if it would merge
            for(IAtom atom : newRing.atoms()){
                IAtom closestAtomInRing = this.chemModelRelay.getClosestAtom(atom);
                if( closestAtomInRing != null) {
                        chemModelRelay.getRenderer().getRenderer2DModel().getMerge().put(atom, closestAtomInRing);
                }
            }
            //if we need to merge, we first move the ring so that the merge atoms
            //are exactly on top of each other - if not doing this, rings get distorted.
            if(chemModelRelay.getRenderer().getRenderer2DModel().getMerge().size()>0){
                try {
                    IAtom toleave = chemModelRelay.getRenderer().getRenderer2DModel().getMerge().keySet().iterator().next();
                    IAtom toshift = (IAtom)chemModelRelay.getRenderer().getRenderer2DModel().getMerge().get(chemModelRelay.getRenderer().getRenderer2DModel().getMerge().keySet().iterator().next()).clone();
                    toleave.getPoint2d().sub(toshift.getPoint2d());
                    Point2d pointSub = new Point2d(toleave.getPoint2d().x, toleave.getPoint2d().y);
                    for(IAtom atom: newRing.atoms()){
                        atom.getPoint2d().sub(pointSub);
                    }
                } catch (CloneNotSupportedException e) {
                    //should not happen
                }
            }
            
            IAtomContainer undoredocontainer = newRing;
            for(IAtom atom : chemModelRelay.getRenderer().getRenderer2DModel().getMerge().keySet()){
                undoredocontainer.removeAtom(atom);
                for(IAtom innerAtom : chemModelRelay.getRenderer().getRenderer2DModel().getMerge().keySet()){
                    if(undoredocontainer.getBondNumber(atom, innerAtom)>-1)
                        undoredocontainer.removeBond(undoredocontainer.getBondNumber(atom, innerAtom));
                }
            }
            if(chemModelRelay.getUndoRedoFactory()!=null && chemModelRelay.getUndoRedoHandler()!=null){
                IUndoRedoable undoredo = chemModelRelay.getUndoRedoFactory().getAddAtomsAndBondsEdit(chemModelRelay.getIChemModel(), newRing, null, "Ring" + " " + ringSize, chemModelRelay);
                chemModelRelay.getUndoRedoHandler().postEdit(undoredo);
            }
            //and perform the merge
            chemModelRelay.mergeMolecules(null);
            
            chemModelRelay.getRenderer().getRenderer2DModel().getMerge().clear();
        } else if (singleSelection instanceof IAtom) {
            this.addRingToAtom((IAtom) singleSelection,false);
        } else if (singleSelection instanceof IBond) {
            this.addRingToBond((IBond) singleSelection,false);
        }            
		
		if (singleSelection == null)
			setSelection(AbstractSelection.EMPTY_SELECTION);
		else
			setSelection(new SingleSelection<IChemObject>(singleSelection));

		chemModelRelay.updateView();
	}
    
    public void mouseMove(Point2d worldCoord) {
        this.chemModelRelay.clearPhantoms();
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
        IBond closestBond = chemModelRelay.getClosestBond(worldCoord);
        IChemObject singleSelection = getHighlighted( worldCoord,
                closestAtom,closestBond );

        if (singleSelection == null) {
            //we build a phantom ring
            IRing ring = this.chemModelRelay.getIChemModel().getBuilder().newRing(ringSize, "C");
            if (addingBenzene) {
                ring.getBond(0).setOrder(IBond.Order.DOUBLE);
                ring.getBond(2).setOrder(IBond.Order.DOUBLE);
                ring.getBond(4).setOrder(IBond.Order.DOUBLE);
            }
            double bondLength = ((ControllerHub)this.chemModelRelay).calculateAverageBondLength(this.chemModelRelay.getIChemModel().getMoleculeSet());
            ringPlacer.placeRing(ring, worldCoord, bondLength);
            for(IAtom atom : ring.atoms())
                this.chemModelRelay.addPhantomAtom(atom);
            for(IBond atom : ring.bonds())
                this.chemModelRelay.addPhantomBond(atom);
            //and look if it would merge somewhere
            chemModelRelay.getRenderer().getRenderer2DModel().getMerge().clear();
            for(IAtom atom : ring.atoms()){
                IAtom closestAtomInRing = this.chemModelRelay.getClosestAtom(atom);
                if( closestAtomInRing != null) {
                        chemModelRelay.getRenderer().getRenderer2DModel().getMerge().put(closestAtomInRing, atom);
                }
            }
        } else if (singleSelection instanceof IAtom) {
            this.addRingToAtom((IAtom) singleSelection,true);
        } else if (singleSelection instanceof IBond) {
            this.addRingToBond((IBond) singleSelection,true);
        }
        this.chemModelRelay.updateView();        
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
        this.ID=ID;
    }

}
