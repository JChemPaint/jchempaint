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

import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.IChemModelRelay.Direction;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

/**
 * Adds a bond at direction that is draged.
 *
 * @cdk.module controlbasic
 */
public class AddBondDragModule extends ControllerModuleAdapter {

    Point2d start;
    Point2d dest;
    IAtom source = null;// either atom at mouse down or new atom
    IAtom merge = null;
    boolean newSource = false;
    boolean isBond = false;
    private double bondLength;
    private String ID;
    IBond.Stereo stereoForNewBond;
    IBond.Order orderForNewBond;

    //if this is true, initally a bond will be drawn, if not, just an atom
    boolean makeInitialBond;

    /**
     * Constructor for the AddBondDragModule.
     * 
     * @param chemModelRelay   The current chemModelRelay.
     * @param stereoForNewBond If a new bond is formed, which stereo specification should it have?
     * @param makeInitialBond  true=click on empty place gives bond, false=gives atom.
     */

    public AddBondDragModule(IChemModelRelay chemModelRelay, IBond.Stereo stereoForNewBond,boolean makeInitialBond) {
        super( chemModelRelay );
        this.stereoForNewBond = stereoForNewBond;
        this.orderForNewBond = IBond.Order.SINGLE;
        this.makeInitialBond = makeInitialBond;
    }

    public AddBondDragModule(IChemModelRelay chemModelRelay, IBond.Order orderForNewBond,boolean makeInitialBond) {
        super( chemModelRelay );
        this.stereoForNewBond = IBond.Stereo.NONE;
        this.orderForNewBond = orderForNewBond;
        this.makeInitialBond = makeInitialBond;
    }

    private IChemObjectBuilder getBuilder() {
        return chemModelRelay.getIChemModel().getBuilder();
    }

    @Override
    public void mouseClickedDown( Point2d worldCoord ) {

        start = null;
        dest = null;
        source = null;
        merge = null;
        isBond = false;
        newSource = false;
        bondLength = Renderer.calculateAverageBondLength( chemModelRelay.getIChemModel() );

        // in case we are starting on an empty canvas
        if(bondLength==0|| Double.isNaN(bondLength))
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
                source = getBuilder().newInstance(IAtom.class, chemModelRelay.getController2DModel().getDrawElement(), start );
                newSource = true;
            }
            else {
                // Take the true (x,y) of the atom, not the click point
                // otherwise it's very hard to draw a regular ring
                start = closestAtom.getPoint2d();
            }

        }
        else if (singleSelection instanceof IBond) {
            if(stereoForNewBond==IBond.Stereo.NONE){
                chemModelRelay.cycleBondValence((IBond) singleSelection, orderForNewBond);
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

	@Override
    public void mouseDrag( Point2d worldCoordFrom, Point2d worldCoordTo ) {
        if(isBond) return;
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoordTo);

        chemModelRelay.getRenderer().getRenderer2DModel().getMerge().remove(merge);
        merge =  (IAtom) getHighlighted(worldCoordTo, closestAtom);


        chemModelRelay.clearPhantoms();
        if(start.distance( worldCoordTo )<getHighlightDistance()) {
            // clear phantom
            merge = null;
            dest = null;
        }else if (merge != null) {
            // set bond
            chemModelRelay.addPhantomBond( getBuilder().newInstance(IBond.class,source,merge, orderForNewBond, stereoForNewBond) );
            dest = null;
            //we also remember the merge atom in the merges in the rendererModel,
            //in case an application uses these.
            chemModelRelay.getRenderer().getRenderer2DModel().getMerge().put(merge,merge);
        }else {
            dest = roundAngle( start, worldCoordTo, bondLength );
            IAtom atom = getBuilder().newInstance(IAtom.class, chemModelRelay.getController2DModel().getDrawElement(), dest );
            IBond bond = getBuilder().newInstance(IBond.class, source,atom, orderForNewBond, stereoForNewBond );
            chemModelRelay.addPhantomBond( bond );
            // update phantom
        }
        chemModelRelay.updateView();
    }

	public static Point2d roundAngle(Point2d s,Point2d d, double bondLength) {

        Vector2d v = new Vector2d();
        v.sub( d, s );
        double rad = Math.atan2(v.y,v.x);
        double deg = Math.toDegrees( rad );
        deg = Math.round( deg/15)*15;
        rad = Math.toRadians( deg );
        v.x = bondLength*Math.cos( rad );
        v.y = bondLength*Math.sin( rad );
        Point2d result = new Point2d();
        result.add( s, v );
        return result;
    }

    @Override
    public void mouseClickedUp( Point2d worldCoord ) {
        chemModelRelay.clearPhantoms();
        if(isBond) return;

        IUndoRedoFactory factory = chemModelRelay.getUndoRedoFactory();
        UndoRedoHandler handler = chemModelRelay.getUndoRedoHandler();
        IAtomContainer containerForUndoRedo = chemModelRelay.getIChemModel().getBuilder().newInstance(IAtomContainer.class);
        
        IAtom newAtom;
        if(newSource) {
            newAtom = chemModelRelay.addAtomWithoutUndo( chemModelRelay.getController2DModel().getDrawElement(), start, chemModelRelay.getController2DModel().getDrawPseudoAtom() );
            containerForUndoRedo.addAtom(newAtom);
        }
        else
            newAtom = source;

        // if merge is set either form a bond or add and form
        IAtomContainer removedContainer=null;
        if(merge!=null) {
                chemModelRelay.getRenderer().getRenderer2DModel().getMerge().remove(merge);
                removedContainer = ChemModelManipulator.getRelevantAtomContainer(chemModelRelay.getIChemModel(), merge);
                IBond newBond = chemModelRelay.addBond( newAtom , merge, stereoForNewBond, orderForNewBond );
                containerForUndoRedo.addBond(newBond);
        } else {
            if(start.distance( worldCoord )<getHighlightDistance()) {
                if(!newSource) {
                    IAtom undoRedoAtom=chemModelRelay.addAtomWithoutUndo(chemModelRelay.getController2DModel().getDrawElement(), newAtom, stereoForNewBond, orderForNewBond, chemModelRelay.getController2DModel().getDrawPseudoAtom() );
                    containerForUndoRedo.addAtom(undoRedoAtom);
                    IAtomContainer atomCon =
                        ChemModelManipulator.getRelevantAtomContainer(chemModelRelay.getIChemModel(), undoRedoAtom);
                    containerForUndoRedo.addElectronContainer(atomCon.getConnectedElectronContainersList(undoRedoAtom).get(0));
                } else if(makeInitialBond){
                    IAtom undoRedoAtom=chemModelRelay.addAtomWithoutUndo(
                            chemModelRelay.getController2DModel().getDrawElement(),
                            new Point2d(newAtom.getPoint2d().x+1.5,newAtom.getPoint2d().y),
                            chemModelRelay.getController2DModel().getDrawPseudoAtom());
                    containerForUndoRedo.addAtom(undoRedoAtom);
                    containerForUndoRedo.addBond(chemModelRelay.addBond(newAtom, undoRedoAtom, stereoForNewBond, orderForNewBond));
                }
            }else {
                IAtom atom = chemModelRelay.addAtomWithoutUndo(chemModelRelay.getController2DModel().getDrawElement(), dest, chemModelRelay.getController2DModel().getDrawPseudoAtom() );
                containerForUndoRedo.addAtom(atom);
                IBond newBond = chemModelRelay.addBond( newAtom, atom, stereoForNewBond, orderForNewBond );
                containerForUndoRedo.addBond(newBond);
            }
        }

        if (factory != null && handler != null) {
            IUndoRedoable undoredo = chemModelRelay.getUndoRedoFactory().getAddAtomsAndBondsEdit
            (chemModelRelay.getIChemModel(), containerForUndoRedo, removedContainer, "Add Bond",chemModelRelay);
            handler.postEdit(undoredo);
        }
        chemModelRelay.updateView();
    
    }

    public String getDrawModeString() {
        if (orderForNewBond == IBond.Order.DOUBLE)
            return "Draw Double Bond";
        else if (orderForNewBond == IBond.Order.TRIPLE)
            return "Draw Triple Bond";
        else
            return "Draw Bond";
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
