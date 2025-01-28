/* 
 * Copyright (C) 2008  Gilleain Torrance <gilleain.torrance@gmail.com>
 *               2009  Mark Rijnbeek <mark_rynbeek@users.sourceforge.net>
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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ISetting;
import org.openscience.cdk.renderer.selection.AbstractSelection;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.BoundsCalculator;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

/**
 * Module to rotate a selection of atoms (and their bonds).
 *
 * @cdk.module controlbasic
 */
public class RotateModule extends ControllerModuleAdapter {

    protected static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(RotateModule.class);

    private static final double SNAP_ANGLE = (Math.PI/6);
    private double rotationAngle;
    protected boolean selectionMade = false;
    protected IChemObjectSelection selection;
    protected Point2d rotationCenter;
    protected Map<IAtom, Point2d>  startCoordsRelativeToRotationCenter;
    protected Map<IAtom, Point2d[]> atomCoordsMap;
    protected boolean rotationPerformed;
    protected String ID;

    /**
     * Constructor 
     * @param chemModelRelay
     */
    public RotateModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
        logger.debug("constructor");
    }

    /**
     * Initializes possible rotation. Determines rotation center and stores 
     * coordinates of atoms to be rotated. These stored coordinates are relative 
     * to the rotation center.
     */
    public void mouseClickedDown(Point2d worldCoord) {
        logger.debug("rotate mouseClickedDown, initializing rotation");
        rotationCenter = null;
        selection = super.chemModelRelay.getRenderer().getRenderer2DModel()
                .getSelection();

        if (   selection == null 
            ||!selection.isFilled()
            || selection.getConnectedAtomContainer() == null
            || selection.getConnectedAtomContainer().getAtomCount()==0) {

            /*
             * Nothing selected- return. Dragging the mouse will not result in
             * any rotation logic.
             */
            logger.debug("Nothing selected for rotation");
            selectionMade = false;
            return;
        
        } else {
            rotationPerformed = false;
            //if we are outside bounding box, we deselect, else
            //we actually start a rotation.
            Rectangle2D bounds = BoundsCalculator.calculateBounds(this.chemModelRelay.getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer());
                
            rotationAngle = 0.0;
            selectionMade = true;

            /* Keep original coordinates for possible undo/redo */
            atomCoordsMap = new HashMap<IAtom, Point2d[]>();
            for (IAtom atom : selection.getConnectedAtomContainer().atoms()) {
                Point2d[] coordsforatom = new Point2d[2];
                coordsforatom[1] = atom.getPoint2d();
                atomCoordsMap.put(atom, coordsforatom);
            }

            /*
             * Determine rotationCenter as the middle of a region defined by
             * min(x,y) and max(x,y) of coordinates of the selected atoms.
             */
            Set<IAtom> anchors = new HashSet<>();

            Double upperX = null, lowerX = null, upperY = null, lowerY = null;
            Collection<IAtom> selectedAtoms = selection.elements(IAtom.class);
            for (IAtom atom : selectedAtoms) {
                if (upperX == null) {
                    upperX = atom.getPoint2d().x;
                    lowerX = upperX;
                    upperY = atom.getPoint2d().y;
                    lowerY = atom.getPoint2d().y;
                } else {
                    double currX = atom.getPoint2d().x;
                    if (currX > upperX)
                        upperX = currX;
                    if (currX < lowerX)
                        lowerX = currX;

                    double currY = atom.getPoint2d().y;
                    if (currY > upperY)
                        upperY = currY;
                    if (currY < lowerY)
                        lowerY = currY;
                }

                for (IBond bond : atom.bonds()) {
                    if (!selection.contains(bond.getOther(atom)))
                        anchors.add(atom);
                }
            }

            if (anchors.size() == 1) {
                rotationCenter = new Point2d(anchors.iterator().next().getPoint2d());
            } else {
                rotationCenter = new Point2d();
                rotationCenter.x = (upperX + lowerX) / 2;
                rotationCenter.y = (upperY + lowerY) / 2;
            }
            logger.debug("rotationCenter " 
                    + rotationCenter.x + " "
                    + rotationCenter.y);

            /* Store the original coordinates relative to the rotation center.
             * These are necessary to rotate around the center of the
             * selection rather than the draw center. */
            startCoordsRelativeToRotationCenter = new HashMap<>();

            int i = 0;
            for (IAtom atom : selectedAtoms) {
                Point2d relPoint = new Point2d();
                relPoint.x = atom.getPoint2d().x - rotationCenter.x;
                relPoint.y = atom.getPoint2d().y - rotationCenter.y;
                startCoordsRelativeToRotationCenter.put(atom, relPoint);
            }
        }
    }

    /**
     * On mouse drag, actual rotation around the center is done
     */
    public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo) {
        if (selectionMade) {
            rotationPerformed=true;
            /*
             * Determine the quadrant the user is currently in, relative to the
             * rotation center.
             */
            int quadrant = 0;
            if ((worldCoordFrom.x >= rotationCenter.x))
                if ((worldCoordFrom.y <= rotationCenter.y))
                    quadrant = 1; // 12 to 3 o'clock
                else
                    quadrant = 2; // 3 to 6 o'clock
            else if ((worldCoordFrom.y <= rotationCenter.y))
                quadrant = 4; // 9 to 12 o'clock
            else
                quadrant = 3; // 6 to 9 o'clock

            /*
             * The quadrant and the drag combined determine in which direction
             * the rotation will be done. For example, dragging in direction
             * left/down in quadrant 4 means rotating counter clockwise.
             */
            final int SLOW_DOWN_FACTOR=4;
            switch (quadrant) {
            case 1:
                rotationAngle += (worldCoordTo.x - worldCoordFrom.x)/SLOW_DOWN_FACTOR
                        + (worldCoordTo.y - worldCoordFrom.y)/SLOW_DOWN_FACTOR;
                break;
            case 2:
                rotationAngle += (worldCoordFrom.x - worldCoordTo.x)/SLOW_DOWN_FACTOR
                        + (worldCoordTo.y - worldCoordFrom.y)/SLOW_DOWN_FACTOR;
                break;
            case 3:
                rotationAngle += (worldCoordFrom.x - worldCoordTo.x)/SLOW_DOWN_FACTOR
                        + (worldCoordFrom.y - worldCoordTo.y)/SLOW_DOWN_FACTOR;
                break;
            case 4:
                rotationAngle += (worldCoordTo.x - worldCoordFrom.x)/SLOW_DOWN_FACTOR
                        + (worldCoordFrom.y - worldCoordTo.y)/SLOW_DOWN_FACTOR;
                break;
            }

            chemModelRelay.rotate(startCoordsRelativeToRotationCenter,
                                  rotationCenter,
                                  rotationAngle);
        }
        chemModelRelay.updateView();
    }

    /**
     * After the rotation (=mouse up after drag), post the undo/redo information
     * with the old and the new coordinates
     */
    public void mouseClickedUp(Point2d worldCoord) {
        if(rotationPerformed && atomCoordsMap!=null && selection.isFilled()) {
            logger.debug("posting undo/redo for rotation");

            /* Keep new coordinates for the sake of possible undo/redo */
            for (IAtom atom : selection.getConnectedAtomContainer().atoms()) {
                Point2d[] coords = atomCoordsMap.get(atom);
                coords[0] = atom.getPoint2d();
            }

            /* Post the rotation */
            IUndoRedoFactory factory = chemModelRelay.getUndoRedoFactory();
            UndoRedoHandler handler = chemModelRelay.getUndoRedoHandler();
            if (factory != null && handler != null) {
                IUndoRedoable undoredo = factory.getChangeCoordsEdit(
                        atomCoordsMap, new HashMap<IBond, IBond.Stereo>(), "Rotation");
                handler.postEdit(undoredo);
            }
        } else {
            chemModelRelay.select(AbstractSelection.EMPTY_SELECTION);
            chemModelRelay.updateView();
        }
    }

    
    public void setChemModelRelay(IChemModelRelay relay) {
        this.chemModelRelay = relay;
    }

    public String getDrawModeString() {
        return "Rotate";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
