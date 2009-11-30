/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 * Copyright (C) 2008  Stefan Kuhn (undo redo)
 * Copyright (C) 2009 Arvid Berg <goglepox@users.sf.net>
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
import org.openscience.jchempaint.controller.IChemModelRelay.Direction;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.selection.SingleSelection;
import org.openscience.jchempaint.GT;

/**
 * Adds an atom on the given location on mouseclick
 * 
 * @author Niels Out
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.module controlbasic
 */
public class AddAtomModule extends ControllerModuleAdapter {

    Point2d start;
    Point2d dest;
    IAtom source = null;// either atom at mouse down or new atom
    IAtom merge = null;
    boolean newSource = false;
    private String ID;

    boolean isBond = false;
    private double bondLength;
    private IBond.Stereo stereoForNewBond;

    public AddAtomModule(IChemModelRelay chemModelRelay, IBond.Stereo stereoForNewBond) {
		super(chemModelRelay);
		this.stereoForNewBond = stereoForNewBond;
	}

	public void mouseClickedDown(Point2d worldCoord) {
        start = null;
        dest = null;
        source = null;
        merge = null;
        isBond = false;
        newSource = false;
        bondLength = Renderer.calculateAverageBondLength( chemModelRelay.getIChemModel() );
        // in case we are starting on an empty canvas
        if(bondLength==0 || Double.isNaN(bondLength))
            bondLength=1.5;
        start = new Point2d(worldCoord);
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
        IBond closestBond = chemModelRelay.getClosestBond( worldCoord );

        IChemObject singleSelection = getHighlighted( worldCoord,
                                                      closestAtom,
                                                      closestBond );

        if(singleSelection == null || singleSelection instanceof IAtom ) {
            isBond = false;
	        source =  (IAtom) getHighlighted(worldCoord, closestAtom);
	
	        if(source == null) {
	            source = chemModelRelay.getIChemModel().getBuilder().
	            	newAtom( chemModelRelay.getController2DModel().
	            	getDrawElement(), start );
	            if(chemModelRelay.getController2DModel().getDrawIsotopeNumber()!=0)
	                source.setMassNumber(chemModelRelay.getController2DModel().getDrawIsotopeNumber());
	            newSource = true;
	        }
        }
        else if (singleSelection instanceof IBond) {
            if(stereoForNewBond==IBond.Stereo.NONE){
                chemModelRelay.cycleBondValence((IBond) singleSelection);
            }else{
                IChemModelRelay.Direction direction = Direction.DOWN;
                if(stereoForNewBond==IBond.Stereo.UP)
                    direction=Direction.UP;
                else if(stereoForNewBond== IBond.Stereo.UP_OR_DOWN)
                    direction=Direction.UNDEFINED;
                else if(stereoForNewBond== IBond.Stereo.E_OR_Z)
                    direction=Direction.EZ_UNDEFINED;
                chemModelRelay.makeBondStereo((IBond)singleSelection, direction);
            }
            setSelection(new SingleSelection<IChemObject>(singleSelection));
            isBond = true;
        }
	}
	
    public void mouseDrag( Point2d worldCoordFrom, Point2d worldCoordTo ) {
        if(isBond) return;
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoordTo);

        merge =  (IAtom) getHighlighted(worldCoordTo, closestAtom);


        chemModelRelay.clearPhantoms();
        if(start.distance( worldCoordTo )<getHighlightDistance()) {
            // clear phantom
            merge = null;
            dest = null;
        }else if (merge != null) {
            // set bond
            chemModelRelay.addPhantomBond( chemModelRelay.getIChemModel()
            		.getBuilder().newBond(source,merge) );
            dest = null;

        }else {
            dest = AddBondDragModule.roundAngle(start, worldCoordTo, bondLength );
            IAtom atom = chemModelRelay.getIChemModel().getBuilder().
            	newAtom( chemModelRelay.getController2DModel().getDrawElement(), dest );
            IBond bond = chemModelRelay.getIChemModel().getBuilder().
            	newBond( source, atom, IBond.Order.SINGLE, stereoForNewBond );
            chemModelRelay.addPhantomBond( bond );
            // update phantom
        }
        chemModelRelay.updateView();
    }

	public void mouseClickedUp(Point2d worldCoord){
        chemModelRelay.clearPhantoms();
        if(isBond) return;
        //There are four cases we handle
        //-Clicked on an existing atom and not dragged more than highligt distance:
        // Change element
        //-Clicked on an existing atom and dragged more than hightligh distance: 
        // Make a bond of DrawBondType from the atom to a new atom of DrawElement
        //-Clicked in empty and not dragged more than highligt distance: 
        // Make new atom
        //-Clicked in empty and dragged more than hightligh distance: 
        // Make a bond of DrawBondType with two new atoms of DrawElement
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
        RendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
        double dH = model.getHighlightDistance() / model.getScale();
        IAtom newAtom;
        if(newSource){
            newAtom = chemModelRelay.addAtom( chemModelRelay.getController2DModel().getDrawElement(), chemModelRelay.getController2DModel().getDrawIsotopeNumber(), start, chemModelRelay.getController2DModel().getDrawPseudoAtom() );
        }else{
            newAtom = source;
        }
        if(start.distance(worldCoord)>dH){
            if(dest==null)
                dest=worldCoord;

            IAtom atom = chemModelRelay.addAtom( chemModelRelay.getController2DModel().getDrawElement(), chemModelRelay.getController2DModel().getDrawIsotopeNumber(), dest, chemModelRelay.getController2DModel().getDrawPseudoAtom() );
            if(chemModelRelay.getController2DModel().getDrawIsotopeNumber()!=0)
                atom.setMassNumber(chemModelRelay.getController2DModel().getDrawIsotopeNumber());

            IUndoRedoFactory factory = chemModelRelay.getUndoRedoFactory();
            UndoRedoHandler handler = chemModelRelay.getUndoRedoHandler();
            IAtomContainer containerForUndoRedo = chemModelRelay.getIChemModel().getBuilder().newAtomContainer();
            IBond bond = chemModelRelay.addBond( newAtom, atom, stereoForNewBond);
            containerForUndoRedo.addBond(bond);
            if (factory != null && handler != null) {
                IUndoRedoable undoredo = chemModelRelay.getUndoRedoFactory().getAddAtomsAndBondsEdit
                (chemModelRelay.getIChemModel(), containerForUndoRedo, null, "Add Bond",chemModelRelay);
                handler.postEdit(undoredo);
            }

            
		}else{	
            if(!newSource){
                if(chemModelRelay.getController2DModel().getDrawPseudoAtom()){
                    chemModelRelay.convertToPseudoAtom(closestAtom, chemModelRelay.getController2DModel().getDrawElement());
                }else{
                    chemModelRelay.setSymbol(closestAtom, chemModelRelay.getController2DModel().getDrawElement());
                    if(chemModelRelay.getController2DModel().getDrawIsotopeNumber()!=0)
                        chemModelRelay.setMassNumber(closestAtom, chemModelRelay.getController2DModel().getDrawIsotopeNumber());
                }
            }
		}
		chemModelRelay.updateView();
		
	}


	public void setChemModelRelay(IChemModelRelay relay) {
		this.chemModelRelay = relay;
	}

	public String getDrawModeString() {
		return GT._("Add Atom Or Change Element") +" ("+
		    (chemModelRelay.getController2DModel().getDrawIsotopeNumber()==0 ? "" 
		        : chemModelRelay.getController2DModel().getDrawIsotopeNumber())+
		    chemModelRelay.getController2DModel().getDrawElement()+")";
	}
	
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }


    /**
     * Tells which stereo info new bonds will get.
     * 
     * @return The stereo which new bonds will have.
     */
    public IBond.Stereo getStereoForNewBond() {
        return stereoForNewBond;
    }
}
