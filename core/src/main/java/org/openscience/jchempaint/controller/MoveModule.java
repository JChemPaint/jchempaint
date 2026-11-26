/* 
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 * Copyright (C) 2008-2009  Arvid Berg <goglepox@users.sf.net>
 * Copyright (C) 2008  Stefan Kuhn (undo redo)
 * Copyright (C) 2009  Mark Rijnbeek (markr@ebi.ac.uk)
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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.selection.AbstractSelection;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.openscience.jchempaint.controller.AbstractSelectModule.getSelectionControlType;

/**
 * Module to move around a selection of atoms and bonds.
 * Handles merging of atoms.
 *
 * @author Niels Out
 * @author John Mayfield
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.module controlbasic
 */
public class MoveModule extends ControllerModuleAdapter {

    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(MoveModule.class);
    
    private Vector2d offset;
    protected Set<IAtom> visible;
    
    private Set<IAtom> atomsToMove;
    
    private Point2d startPoint;

    private String ID;

    private ControllerModuleAdapter switchtowhenoutside;

    private enum Transform {
        Move,
        Rotate,
        Scale
    }

    private Transform transform = Transform.Move;

    private double transformAmmount;
    protected Point2d transformOrigin;
    protected IChemModelRelay.Scale scaleDir = IChemModelRelay.Scale.Both;
    protected Map<IAtom, Point2d> relativeAtomCoords;
    protected Map<IAtom, Point2d[]> atomCoordsMap;
    protected boolean transformMade;

    /**
     * Constructor for the MoveModule. Gets passed an instance of another module
     * to which mode switches if clicked outside the selection.
     *
     * @param chemObjectRelay     The current ChemObjectRelay.
     * @param switchtowhenoutside The module to switch to if clicked outside selection.
     */
    public MoveModule(IChemModelRelay chemObjectRelay, 
            ControllerModuleAdapter switchtowhenoutside) {
        super(chemObjectRelay);
        this.switchtowhenoutside = switchtowhenoutside;
    }

    // if anything is selected, set offset, start2DCenter, and atomsToMove.
    // if not, switch to other mode and reset mouse cursor.
    @Override
    public void mouseClickedDown(Point2d p, int modifiers) {

        JChemPaintRendererModel jcpModel = chemModelRelay.getRenderer().getRenderer2DModel();

        // move/rotate/scales works on atom coords, a bond may be selected
        // without its end points so we expand the selection to include
        // those atoms
        selection = jcpModel.getSelection();
        visible = selection.elements(IAtom.class)
                           .stream()
                           .filter(a -> !a.is(IChemObject.HIDDEN))
                           .collect(Collectors.toSet());

        IChemModelRelay.CursorType cursor = getSelectionControlType(p, chemModelRelay);
        if (cursor == IChemModelRelay.CursorType.ROTATE) {

            transformMade = false;
            transformAmmount = 0;
            transformOrigin = RotateModule.getRotationCenter(selection);

            /* Keep original coordinates for possible undo/redo */
            atomCoordsMap = new HashMap<>();
            /* Store the original coordinates relative to the rotation center.
             * These are necessary to rotate around the center of the
             * selection rather than the draw center. */
            relativeAtomCoords = new HashMap<>();
            for (IAtom atom : selection.elements(IAtom.class)) {
                Point2d relPoint = new Point2d();
                relPoint.x = atom.getPoint2d().x - transformOrigin.x;
                relPoint.y = atom.getPoint2d().y - transformOrigin.y;
                relativeAtomCoords.put(atom, relPoint);
                atomCoordsMap.put(atom, new Point2d[]{null, new Point2d(atom.getPoint2d())});
            }

            transform = Transform.Rotate;
            jcpModel.setRotating(true);
            return;
        } else if (cursor.isResize()) {

            transformMade = false;
            transformOrigin = RotateModule.getRotationCenter(selection);
            transformAmmount = p.distance(transformOrigin);

            /* Keep original coordinates for possible undo/redo */
            atomCoordsMap = new HashMap<>();
            /* Store the original coordinates relative to the rotation center.
             * These are necessary to rotate around the center of the
             * selection rather than the draw center. */
            relativeAtomCoords = new HashMap<>();
            for (IAtom atom : selection.elements(IAtom.class)) {
                Point2d relPoint = new Point2d();
                relPoint.x = atom.getPoint2d().x - transformOrigin.x;
                relPoint.y = atom.getPoint2d().y - transformOrigin.y;
                relativeAtomCoords.put(atom, relPoint);
                atomCoordsMap.put(atom, new Point2d[]{null, atom.getPoint2d()});
            }

            transform = Transform.Scale;
            scaleDir = cursor.getScaleDirection();
            return;
        } else {
            transform = Transform.Move;
            if (visible.size() == 1) {
                transformOrigin = visible.iterator()
                                         .next()
                                         .getPoint2d();
            } else {
                transformOrigin = startPoint;
            }
            atomCoordsMap = new HashMap<>();
            for (IAtom atom : selection.elements(IAtom.class)) {
                atomCoordsMap.put(atom, new Point2d[]{null, atom.getPoint2d()});
            }
            chemModelRelay.initSnapGuides();
        }

        // if we are outside bounding box, we deselect, else
        // we actually start a move.
        if (selection.isFilled() && isInSelectionBox(jcpModel, p)) {
            atomsToMove = new HashSet<>();
            atomsToMove.addAll(selection.elements(IAtom.class));
            startPoint = p;

         } else if(switchtowhenoutside!=null){
            jcpModel
                .setSelection(AbstractSelection.EMPTY_SELECTION);
            this.chemModelRelay
                    .setActiveDrawModule(switchtowhenoutside);
            chemModelRelay.updateView();
            endMove();
            switchtowhenoutside.mouseClickedDown(p, modifiers);
            chemModelRelay.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    private static boolean isInSelectionBox(JChemPaintRendererModel jcpModel, Point2d p) {
        RectangleElement rect = jcpModel.getSelectionBounds();
        return rect.xCoord <= p.x && p.x <= rect.xCoord + rect.width &&
               (rect.yCoord + rect.height) <= p.y && p.y <= rect.yCoord;
    }

    @Override
    public void mouseMove(Point2d p, int modifiers){
        AbstractSelectModule.showMouseCursor(p, this.chemModelRelay);
    }

    @Override
    public void mouseClickedUp(Point2d p, int modifiers) {

        chemModelRelay.clearSnapGuides();

        if (transform == Transform.Rotate ||
            transform == Transform.Scale) {

            if (transformMade && atomCoordsMap != null && selection.isFilled()) {

                /* Keep new coordinates for the sake of possible undo/redo */
                for (IAtom atom : selection.elements(IAtom.class)) {
                    atomCoordsMap.get(atom)[0] = atom.getPoint2d();
                }

                /* Post the rotation/scale */
                IUndoRedoFactory factory = chemModelRelay.getUndoRedoFactory();
                UndoRedoHandler handler = chemModelRelay.getUndoRedoHandler();
                if (factory != null && handler != null) {
                    handler.postEdit(factory.getChangeCoordsEdit(atomCoordsMap, new HashMap<>(), transform.name()));
                }
            }

            chemModelRelay.setCursor(Cursor.DEFAULT_CURSOR);
            chemModelRelay.getRenderer().getRenderer2DModel().setRotating(false);
            chemModelRelay.updateView();

            return;
        }

    	if (startPoint != null) {

            // take 2d center of end point to ensure correct positional undo
            Vector2d total = new Vector2d();
            total.sub(p, startPoint);

            Map<IAtom, IAtom> mergeMap = calculateMerge(atomsToMove);
            JChemPaintRendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
            model.getMerge().clear();
            model.getMerge().putAll(mergeMap);

            // Do the merge of atoms
            boolean altMode = (modifiers & KeyEvent.ALT_DOWN_MASK) != 0;
            if (!mergeMap.isEmpty() && !altMode) {
                try {
                    chemModelRelay.mergeMolecules(atomCoordsMap);
				} catch (RuntimeException e) {
					//move things back, merge is not allowed
		            Vector2d d = new Vector2d();
		            d.sub(startPoint, p);
					chemModelRelay.moveBy(atomsToMove, d, total);
		            chemModelRelay.updateView();
				}
            } else {
                // single atom will snap to sensible angles (moveTo(atom,from,to)
                if (visible.size() == 1) {
                    chemModelRelay.moveSingleAtom(visible.iterator().next(),
                                                  transformOrigin, p, selection, true);
                } else {
                    chemModelRelay.moveBy(atomsToMove, null, total);
                }
            }
    	}
        IControllerModule newActiveModule = new SelectSquareModule(this.chemModelRelay);
        newActiveModule.setID("select");
        this.chemModelRelay.setActiveDrawModule(newActiveModule);
    
    	endMove();
    }

    private void endMove() {
        startPoint = null;
        selection = null;
    }

    /**
     * Most move mode calculations are done in this routine.
     */
    @Override
    public void mouseDrag(Point2d pFrom, Point2d pTo, int modifiers) {
        switch (transform) {
            case Rotate:
                transformMade = true;
                transformAmmount += RotateModule.getRotationAmount(transformOrigin, pFrom, pTo);
                double snapAngle = transformAmmount;
                // by default snap to 10 degrees (PI/18 radians)
                // unless in alt mode we allow free rotation
                if ((modifiers & KeyEvent.ALT_DOWN_MASK) == 0) {
                    snapAngle = (Math.PI / 18) * Math.round(snapAngle / (Math.PI / 18));
                }
                chemModelRelay.rotate(relativeAtomCoords,
                                      transformOrigin,
                                      snapAngle);
                chemModelRelay.updateView();
                return;
            case Scale:
                transformMade = true;
                double dist = pTo.distance(transformOrigin) / transformAmmount;
                chemModelRelay.scale(relativeAtomCoords,
                                     transformOrigin,
                                     dist,
                                     scaleDir);
                chemModelRelay.updateView();
                return;
            case Move:
                Point2d endPoint = pTo;

                // single atom will snap to sensible angles (moveTo(atom,from,to)
                if (visible.size() == 1) {
                    chemModelRelay.moveSingleAtom(visible.iterator().next(),
                                                  transformOrigin, endPoint, selection,
                                                  false);
                } else {
                    // do an absolute move by rolling back any changed coordinates
                    for (Map.Entry<IAtom, Point2d[]> e : atomCoordsMap.entrySet())
                        e.getKey().setPoint2d(e.getValue()[1]);
                    Vector2d moveVector = new Vector2d(endPoint);
                    moveVector.sub(startPoint);
                    chemModelRelay.moveBy(atomsToMove, moveVector, null);

                    // if alt is not held we snap the selection and show
                    // which atoms will be merged
                    if ((modifiers&KeyEvent.ALT_DOWN_MASK) == 0) {
                        chemModelRelay.snapAtomsToGuides(atomsToMove);

                        JChemPaintRendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
                        model.getMerge().clear();
                        model.getMerge().putAll(calculateMerge(atomsToMove));
                    }
                }
                break;
        }
        chemModelRelay.updateView();
    }

    public static class DistAtom implements Comparable<DistAtom>{
        IAtom atom;
        double distSquared;

        public DistAtom(IAtom atom, double distSquared) {
            this.atom = atom;
            this.distSquared = distSquared;
        }

        public int compareTo( DistAtom o ) {
            if(this.distSquared < o.distSquared) return -1;
            if(this.distSquared > o.distSquared) return 1;
            return 0;
        }
    }

    private Map<IAtom, IAtom> calculateMerge( Set<IAtom> mergeAtoms ) {
        JChemPaintRendererModel rModel = chemModelRelay.getRenderer().getRenderer2DModel();
        double maxDistance = rModel.getHighlightDistance()/ rModel.getScale();
        maxDistance *= maxDistance; // maxDistance squared
        Map<IAtom,IAtom> mergers = new HashMap<IAtom, IAtom>();
        Iterator<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(chemModelRelay.getIChemModel()).iterator();
        while (containers.hasNext()) {
            IAtomContainer ac = (IAtomContainer)containers.next();
            for(IAtom atom:mergeAtoms) {
                List<DistAtom> candidates = findMergeCandidates(ac,atom);
                Collections.sort( candidates);
                for(DistAtom candidate:candidates) {
                    if(candidate.distSquared>maxDistance)
                        break;
                    if(mergeAtoms.contains( candidate.atom ))
                        continue;
                    if (hasPriority(candidate.atom, atom)) {
                        mergers.put(atom, candidate.atom);
                    } else {
                        mergers.put(candidate.atom, atom);
                    }
                }
            }
        }
        return mergers;
    }

    private boolean hasPriority(IAtom atom, IAtom other) {
        return atom.getAtomicNumber() != IElement.C &&
               other.getAtomicNumber() == IElement.C;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.ControllerModuleAdapter#mouseClickedDouble(javax.vecmath.Point2d)
     */
    @Override
    public void mouseClickedDouble(Point2d p, int modifiers) {
        IAtom closestAtom = chemModelRelay.getClosestAtom(p);
        IBond closestBond = chemModelRelay.getClosestBond(p);
        IChemObject singleSelection = getHighlighted(p, closestAtom, closestBond);
        if (singleSelection instanceof IAtom)
            chemModelRelay.selectFragment((IAtom)singleSelection);
        else if (singleSelection instanceof IBond)
            chemModelRelay.selectFragment((IBond)singleSelection);
        chemModelRelay.updateView();
    }

    private List<DistAtom> findMergeCandidates(IAtomContainer set, IAtom atom ) {
        List<DistAtom> candidates = new ArrayList<DistAtom>();
        for(IAtom candidate:set.atoms()) {
            if (candidate.is(IChemObject.HIDDEN))
                continue;
            double disSquare = candidate.getPoint2d().distanceSquared( atom.getPoint2d() );
            candidates.add(new DistAtom(candidate,disSquare));
        }
        return candidates;
    }

    public String getDrawModeString() {
		return "Move";
	}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
