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

import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.renderer.BoundsCalculator;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.selection.AbstractSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

/**
 * Module to move around a selection of atoms and bonds
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
    
    private String ID;

    private ControllerModuleAdapter switchtowhenoutside;
    
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

    /**
     * Constructor for the MoveModule.
     *
     * @param chemObjectRelay The current ChemObjectRelay.
     */
    public MoveModule(IChemModelRelay chemObjectRelay) {
        super(chemObjectRelay);
    }

    public void mouseClickedDown(Point2d worldCoord) {

        //if we are outside bounding box, we deselect, else
        //we actually start a move.
        IAtomContainer selectedAC = getSelectedAtomContainer(worldCoord );
        if (selectedAC != null) {
            
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
                    selectedAC.addAtom(atom);
                }

            }

            Point2d current = GeometryTools.get2DCenter(selectedAC);
            start2DCenter = current;
            offset = new Vector2d();
            offset.sub(current, worldCoord);

         } else if(switchtowhenoutside!=null){
            this.chemModelRelay.getRenderer().getRenderer2DModel()
                .setSelection(AbstractSelection.EMPTY_SELECTION);
            this.chemModelRelay
                    .setActiveDrawModule(switchtowhenoutside);
            chemModelRelay.updateView();
            endMove();
            switchtowhenoutside.mouseClickedDown(worldCoord);
            chemModelRelay.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }
    
    public void mouseMove(Point2d p){
        AbstractSelectModule.showMouseCursor(p, this.chemModelRelay);
    }


    public void mouseClickedUp(Point2d worldCoord) {
    	if (start2DCenter != null) {
            Vector2d end = new Vector2d();

            // take 2d center of end point to ensure correct positional undo
            Point2d end2DCenter = GeometryTools.get2DCenter(atomsToMove);
            end.sub(end2DCenter, start2DCenter);

            Map<IAtom, IAtom> mergeMap = chemModelRelay.getRenderer()
                                          .getRenderer2DModel().getMerge();
            // Do the merge of atoms
            if (!mergeMap.isEmpty()) {
                chemModelRelay.mergeMolecules(end);
            }else {
                chemModelRelay.moveBy(atomsToMove, null, end);
            }
    	}
    	endMove();
    }

    private void endMove() {
        start2DCenter = null;
        selection = null;
        offset = null;
    }

    public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo) {
        if (chemModelRelay != null && offset != null) {

            Point2d atomCoord = new Point2d();
            atomCoord.add(worldCoordTo, offset);

            Vector2d d = new Vector2d();
            d.sub(worldCoordTo, worldCoordFrom);

            // check for possible merges
            RendererModel model = 
                chemModelRelay.getRenderer().getRenderer2DModel();
            model.getMerge().clear();

            model.getMerge().putAll( calculateMerge(atomsToMove) );

            chemModelRelay.moveBy(atomsToMove, d, null);
            chemModelRelay.updateView();

        } else {
            if (chemModelRelay == null) {
                logger.debug("chemObjectRelay is NULL!");
            }
        }
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
        RendererModel rModel = chemModelRelay.getRenderer().getRenderer2DModel();
        double maxDistance = rModel.getHighlightDistance()/ rModel.getScale();
        maxDistance *= maxDistance; // maxDistance squared
        Map<IAtom,IAtom> mergers = new HashMap<IAtom, IAtom>();
        Iterator<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(chemModelRelay.getIChemModel()).iterator();
        while (containers.hasNext()) {
            IAtomContainer ac = (IAtomContainer)containers.next();
            for(IAtom atom:mergeAtoms) {
                List<DistAtom> candidates = findMergeCandidates(ac,atom);
                Collections.sort( candidates);
                for(DistAtom candiate:candidates) {
                    if(candiate.distSquared>maxDistance)
                        break;
                    if(mergeAtoms.contains( candiate.atom ))
                        continue;
                    mergers.put( atom, candiate.atom );
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
