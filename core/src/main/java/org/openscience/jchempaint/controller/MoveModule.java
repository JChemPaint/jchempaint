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

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.selection.AbstractSelection;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openscience.jchempaint.controller.AbstractSelectModule.getSelectionControlType;

/**
 * Module to move around a selection of atoms and bonds.
 * Handles merging of atoms.
 *
 * @author Niels Out
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.module controlbasic
 */
public class MoveModule extends ControllerModuleAdapter {

    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(MoveModule.class);
    
    private Vector2d offset;
    
    private Set<IAtom> atomsToMove;
    
    private Point2d start2DCenter;
    private Point2d end2DCenter;
    
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
    public void mouseClickedDown(Point2d worldCoord, int modifiers) {

        JChemPaintRendererModel jcpModel = chemModelRelay.getRenderer().getRenderer2DModel();

        // move/rotate/scales works on atom coords, a bond may be selected
        // without its end points so we expand the selection to include
        // those atoms
        selection = getExpandedSelection(jcpModel.getSelection());

        IChemModelRelay.CursorType cursor = getSelectionControlType(worldCoord, chemModelRelay);
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
                atomCoordsMap.put(atom, new Point2d[]{null, atom.getPoint2d()});
            }

            transform = Transform.Rotate;
            jcpModel.setRotating(true);
            return;
        } else if (cursor.isResize()) {

            transformMade = false;
            transformOrigin = RotateModule.getRotationCenter(selection);
            transformAmmount = worldCoord.distance(transformOrigin);

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
        }


        // if we are outside bounding box, we deselect, else
        // we actually start a move.
        AtomBondSet selectedAC = getSelectAtomBondSet(worldCoord);
        if (selectedAC != null && !selectedAC.isEmpty()) {

            // It could be that only a  selected bond is going to be moved. 
            // So make sure that the attached atoms are included, otherwise
            // the undo will fail to place the atoms back where they were
           atomsToMove = new HashSet<IAtom>();

            for (IAtom atom : selectedAC.atoms()) {
                atomsToMove.add(atom);
            }
            for (IBond bond : selectedAC.bonds()) {
                for (IAtom atom : bond.atoms()){
                    atomsToMove.add(atom);
                    selectedAC.add(atom);
                }
            }

            Point2d current = GeometryUtil.get2DCenter(selectedAC.atoms());
            start2DCenter = current;
            offset = new Vector2d();
            offset.sub(current, worldCoord);

         } else if(switchtowhenoutside!=null){
            jcpModel
                .setSelection(AbstractSelection.EMPTY_SELECTION);
            this.chemModelRelay
                    .setActiveDrawModule(switchtowhenoutside);
            chemModelRelay.updateView();
            endMove();
            switchtowhenoutside.mouseClickedDown(worldCoord);
            chemModelRelay.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    private IChemObjectSelection getExpandedSelection(IChemObjectSelection selection) {
        LogicalSelection expandSelection = new LogicalSelection(LogicalSelection.Type.ALL);
        for (IAtom atom : selection.elements(IAtom.class)) {
            expandSelection.select(atom);
        }
        for (IBond bond : selection.elements(IBond.class)) {
            expandSelection.select(bond.getBegin());
            expandSelection.select(bond.getEnd());
        }
        return expandSelection;
    }

    public void mouseMove(Point2d p){
        AbstractSelectModule.showMouseCursor(p, this.chemModelRelay);
    }


    public void mouseClickedUp(Point2d worldCoord) {

        if (transform == Transform.Rotate ||
            transform == Transform.Scale) {

            if (transformMade && atomCoordsMap != null && selection.isFilled()) {

                /* Keep new coordinates for the sake of possible undo/redo */
                for (IAtom atom : selection.getConnectedAtomContainer().atoms()) {
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

    	if (start2DCenter != null) {

            // take 2d center of end point to ensure correct positional undo
            Vector2d total = new Vector2d();
            Point2d end2DCenter = GeometryUtil.get2DCenter(atomsToMove);
            total.sub(end2DCenter, start2DCenter);

            Map<IAtom, IAtom> mergeMap = calculateMerge(atomsToMove);
            JChemPaintRendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
            model.getMerge().clear();
            model.getMerge().putAll(mergeMap);

            // Do the merge of atoms
            if (!mergeMap.isEmpty()) {
                try {
					chemModelRelay.mergeMolecules(total);
				} catch (RuntimeException e) {
					//move things back, merge is not allowed
		            Vector2d d = new Vector2d();
		            d.sub(start2DCenter,end2DCenter);
					chemModelRelay.moveBy(atomsToMove, d, total);
		            chemModelRelay.updateView();
				}
            } else {
                // single atom will snap to sensible angles (moveTo(atom,from,to)
                if (atomsToMove.size() == 1) {
                    chemModelRelay.moveTo(atomsToMove.iterator().next(), start2DCenter, end2DCenter, true);
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
        start2DCenter = null;
        selection = null;
        offset = null;
    }

    /**
     * Most move mode calculations are done in this routine.
     */
    public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo) {

        if (transform == Transform.Rotate) {
            transformMade = true;
            transformAmmount += RotateModule.getRotationAmount(transformOrigin, worldCoordFrom, worldCoordTo);
            chemModelRelay.rotate(relativeAtomCoords,
                                  transformOrigin,
                                  transformAmmount);
            chemModelRelay.updateView();
            return;
        } else if (transform == Transform.Scale) {
            transformMade = true;
            double dist = worldCoordTo.distance(transformOrigin) / transformAmmount;
            chemModelRelay.scale(relativeAtomCoords,
                                 transformOrigin,
                                 dist,
                                 scaleDir);
            chemModelRelay.updateView();
            return;
        }

    	end2DCenter = worldCoordTo;

        Vector2d d = new Vector2d();
        d.sub(worldCoordTo, worldCoordFrom);

        // single atom will snap to sensible angles (moveTo(atom,from,to)
        if (atomsToMove.size() == 1) {
            chemModelRelay.moveTo(atomsToMove.iterator().next(), start2DCenter, worldCoordTo, false);
        } else {
            chemModelRelay.moveBy(atomsToMove, d, null);
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
                    mergers.put( atom, candidate.atom );
                }
            }
        }
        return mergers;
    }
    
    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.ControllerModuleAdapter#mouseClickedDouble(javax.vecmath.Point2d)
     */
    public void mouseClickedDouble(Point2d p){
        IAtom closestAtom = chemModelRelay.getClosestAtom(p);
        IBond closestBond = chemModelRelay.getClosestBond(p);
        IChemObject singleSelection = getHighlighted( p,
                closestAtom,
                closestBond );
        if(singleSelection!=null){
            for (IAtomContainer isolatedSystem : ConnectivityChecker.partitionIntoMolecules(this.chemModelRelay.getIChemModel().getMoleculeSet().getAtomContainer(0)).atomContainers()) {
                if(isolatedSystem.contains(closestAtom) || isolatedSystem.contains(closestBond)){
                    this.chemModelRelay.select(new SingleSelection<IAtomContainer>(isolatedSystem));
                }
            }
        }
        chemModelRelay.updateView();
    }

    private List<DistAtom> findMergeCandidates(IAtomContainer set, IAtom atom ) {
        List<DistAtom> candidates = new ArrayList<DistAtom>();
        for(IAtom candidate:set.atoms()) {
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
