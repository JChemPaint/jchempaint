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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.selection.AbstractSelection;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;

/**
 * Module to rotate a selection of atoms (and their bonds).
 *
 * @cdk.module controlbasic
 */
public class RotateModule extends ControllerModuleAdapter {

    protected static ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(RotateModule.class);

    private static final double SNAP_ANGLE = (Math.PI / 6);
    private double rotationAngle;
    protected boolean selectionMade = false;
    protected IChemObjectSelection selection;
    protected Point2d rotationCenter;
    protected Map<IAtom, Point2d> startCoordsRelativeToRotationCenter;
    protected Map<IAtom, Point2d[]> atomCoordsMap;
    protected boolean rotationPerformed;
    protected String ID;

    /**
     * Constructor
     *
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
    public void mouseClickedDown(Point2d worldCoord, int modifiers) {
        logger.debug("rotate mouseClickedDown, initializing rotation");
        rotationCenter = null;
        selection = super.chemModelRelay.getRenderer().getRenderer2DModel()
                                        .getSelection();

        if (selection == null
            || !selection.isFilled()
            || selection.getConnectedAtomContainer() == null
            || selection.getConnectedAtomContainer().getAtomCount() == 0) {

            /*
             * Nothing selected- return. Dragging the mouse will not result in
             * any rotation logic.
             */
            logger.debug("Nothing selected for rotation");
            selectionMade = false;
            return;

        } else {
            rotationPerformed = false;
            rotationAngle = 0.0;
            selectionMade = true;
            chemModelRelay.getRenderer().getRenderer2DModel().setRotating(true);


            Collection<IAtom> selectedAtoms = selection.elements(IAtom.class);
            rotationCenter = getRotationCenter(selection);
            logger.debug("rotationCenter "
                         + rotationCenter.x + " "
                         + rotationCenter.y);

            /* Keep original coordinates for possible undo/redo */
            atomCoordsMap = new HashMap<>();
            /* Store the original coordinates relative to the rotation center.
             * These are necessary to rotate around the center of the
             * selection rather than the draw center. */
            startCoordsRelativeToRotationCenter = new HashMap<>();
            for (IAtom atom : selectedAtoms) {
                Point2d relPoint = new Point2d();
                relPoint.x = atom.getPoint2d().x - rotationCenter.x;
                relPoint.y = atom.getPoint2d().y - rotationCenter.y;
                startCoordsRelativeToRotationCenter.put(atom, relPoint);
                atomCoordsMap.put(atom, new Point2d[]{null, atom.getPoint2d()});
            }
        }
    }

    static Point2d getRotationCenter(IChemObjectSelection selection) {

        /*
         * Determine rotationCenter as the middle of a region defined by
         * min(x,y) and max(x,y) of coordinates of the selected atoms.
         */
        Set<IAtom> anchors = new HashSet<>();

        Double upperX = null, lowerX = null, upperY = null, lowerY = null;

        for (IAtom atom : selection.elements(IAtom.class)) {
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
            return new Point2d(anchors.iterator().next().getPoint2d());
        } else {
            return new Point2d((upperX + lowerX) / 2, (upperY + lowerY) / 2);
        }
    }

    /**
     * On mouse drag, actual rotation around the center is done
     */
    public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo, int modifiers) {
        if (selectionMade) {
            rotationPerformed = true;
            rotationAngle += getRotationAmount(rotationCenter, worldCoordFrom, worldCoordTo);

            chemModelRelay.rotate(startCoordsRelativeToRotationCenter,
                                  rotationCenter,
                                  rotationAngle);
        }
        chemModelRelay.updateView();
    }

    static double getRotationAmount(Point2d rotationCenter,
                                    Point2d from,
                                    Point2d to) {
        /*
         * Determine the quadrant the user is currently in, relative to the
         * rotation center.
         */
        int quadrant = 0;
        if ((from.x >= rotationCenter.x))
            if ((from.y <= rotationCenter.y))
                quadrant = 1; // 12 to 3 o'clock
            else
                quadrant = 2; // 3 to 6 o'clock
        else if ((from.y <= rotationCenter.y))
            quadrant = 4; // 9 to 12 o'clock
        else
            quadrant = 3; // 6 to 9 o'clock

        /*
         * The quadrant and the drag combined determine in which direction
         * the rotation will be done. For example, dragging in direction
         * left/down in quadrant 4 means rotating counter clockwise.
         */
        final int SLOW_DOWN_FACTOR = 4;
        switch (quadrant) {
            case 1:
                return (to.x - from.x) / SLOW_DOWN_FACTOR
                       + (to.y - from.y) / SLOW_DOWN_FACTOR;
            case 2:
                return (from.x - to.x) / SLOW_DOWN_FACTOR
                       + (to.y - from.y) / SLOW_DOWN_FACTOR;
            case 3:
                return (from.x - to.x) / SLOW_DOWN_FACTOR
                       + (from.y - to.y) / SLOW_DOWN_FACTOR;
            case 4:
                return (to.x - from.x) / SLOW_DOWN_FACTOR
                       + (from.y - to.y) / SLOW_DOWN_FACTOR;
        }

        return 0;
    }

    /**
     * After the rotation (=mouse up after drag), post the undo/redo information
     * with the old and the new coordinates
     */
    public void mouseClickedUp(Point2d worldCoord, int modifiers) {
        if (rotationPerformed && atomCoordsMap != null && selection.isFilled()) {
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
            chemModelRelay.getRenderer().getRenderer2DModel().setRotating(false);
            chemModelRelay.updateView();
        } else {
            chemModelRelay.select(AbstractSelection.EMPTY_SELECTION);
            chemModelRelay.getRenderer().getRenderer2DModel().setRotating(false);
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
        this.ID = ID;
    }

}
