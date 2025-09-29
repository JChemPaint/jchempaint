/*
 * Copyright (C) 2010  Conni Wagner <conni75@users.sourceforge.net>
 *               2012  Ralf Stephan <ralf@ark.in-berlin.de>
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

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.jchempaint.renderer.Renderer;

import javax.vecmath.Point2d;

/**
 * Adds a bond at direction that is dragged.
 *
 */
public class ChainModule extends ControllerModuleAdapter {

    Point2d start;
    Point2d dest;
    IAtom source = null;// either atom at mouse down or new atom
    IAtom merge = null;
    private double bondLength;
    private String ID;


    /**
     * Constructor for the ChainModule.
     * 
     * @param chemModelRelay   The current chemModelRelay.
     */
    public ChainModule(IChemModelRelay chemModelRelay) {
        super( chemModelRelay );
    }

    private IChemObjectBuilder getBuilder() {
        return chemModelRelay.getIChemModel().getBuilder();
    }

    @Override
    public void mouseClickedDown( Point2d worldCoord, int modifiers) {

        start = null;
        dest = null;
        source = null;
        bondLength = Renderer.calculateBondLength(chemModelRelay.getIChemModel());
        
        // in case we are starting on an empty canvas
        if (bondLength == 0 || Double.isNaN(bondLength))
            bondLength = 1.5;
        
        start = new Point2d(worldCoord);
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
        IBond closestBond = chemModelRelay.getClosestBond( worldCoord );

        IChemObject singleSelection = getHighlighted( worldCoord,
                                                      closestAtom,
                                                      closestBond );

        if(singleSelection instanceof IAtom ) {
            source =  (IAtom) getHighlighted(worldCoord, closestAtom);

            if(source == null) {
                source = getBuilder().newInstance(IAtom.class, chemModelRelay.getController2DModel().getDrawElement(), start );
            }
            else {
                // Take the true (x,y) of the atom, not the click point
                // otherwise it's very hard to draw a regular ring
                start = closestAtom.getPoint2d();
            }

        }
    }

	@Override
    public void mouseDrag( Point2d worldCoordFrom, Point2d worldCoordTo, int modifiers) {
        chemModelRelay.clearPhantoms();
        //how many bonds do we want?
        double distance = start.distance(worldCoordTo);
        int numberofbonds = (int)(distance / (bondLength*.8660254)); // constant is sqrt(3)/4
        if(numberofbonds>0){
        	//add start atom
        	IAtomContainer phantoms = getBuilder().newInstance(IAtomContainer.class);
        	IAtom startAtom;
            if (source == null) {
                if (Elements.ofString(chemModelRelay.getController2DModel().getDrawElement()) == Elements.Unknown)
                    startAtom = getBuilder().newInstance(IAtom.class, "C", start);
                else
                    startAtom = getBuilder().newInstance(IAtom.class, chemModelRelay.getController2DModel().getDrawElement(), start);
            } else {
                startAtom = source;
            }
        	phantoms.addAtom(startAtom);
        	//make atoms and bonds as needed
        	for(int i=0;i<numberofbonds;i++){
        		IAtom nextAtom;
                if (Elements.ofString(chemModelRelay.getController2DModel().getDrawElement()) == Elements.Unknown)
                    nextAtom = getBuilder().newInstance(IAtom.class, "C", new Point2d(startAtom.getPoint2d().x+bondLength, startAtom.getPoint2d().y));
                else
                    nextAtom = getBuilder().newInstance(IAtom.class, chemModelRelay.getController2DModel().getDrawElement(), new Point2d(startAtom.getPoint2d().x+bondLength, startAtom.getPoint2d().y));
        		phantoms.addAtom(nextAtom);
        		phantoms.addBond(getBuilder().newInstance(IBond.class, startAtom, nextAtom, IBond.Order.SINGLE));
        		startAtom = nextAtom;
        	}
        	
        	// The algorithm is 1. calc point a bondlength away in the mouse direction
        	// 2. calc point rotating the above 30° down, then the same 30° up
        	// 3. chain the points alternatively
            Point2d point = new Point2d(worldCoordTo);
            Point2d center = new Point2d(start);
            double dx = (point.x - center.x)/numberofbonds;
            double dy = (point.y - center.y)/numberofbonds;
            double angle = Math.PI / 6;
            double cosangle = Math.cos(angle);
            double sinangle = Math.sin(angle);
            double firstx = dx*cosangle - dy*sinangle;
            double firsty = dx*sinangle + dy*cosangle;
            double secx = dx*cosangle + dy*sinangle;
            double secy = -dx*sinangle + dy*cosangle;
    		Point2d p1 = new Point2d(start);
    		phantoms.getAtom(0).setPoint2d(p1);
    		double currx = p1.x;
    		double curry = p1.y;
            for(int i=1; i<phantoms.getAtomCount(); i++){
        		Point2d p = new Point2d(currx,curry);
            	if(i % 2 == 1){
            		p.x += firstx;
            		p.y += firsty;
            	}else{
            		p.x += secx;
            		p.y += secy;
                }
            	currx = p.x;
            	curry = p.y;
        		phantoms.getAtom(i).setPoint2d(p);
            }
            chemModelRelay.setPhantoms(phantoms);
            chemModelRelay.setPhantomText(""+phantoms.getAtomCount(), worldCoordTo);
            IAtom closestAtom = chemModelRelay.getClosestAtom(phantoms.getAtom(phantoms.getAtomCount()-1));
            chemModelRelay.getRenderer().getRenderer2DModel().getMerge().remove(merge);
            merge =  (IAtom) getHighlighted(phantoms.getAtom(phantoms.getAtomCount()-1).getPoint2d(), closestAtom);
            if(merge!=null){
            	chemModelRelay.getRenderer().getRenderer2DModel().getMerge().put(merge,phantoms.getAtom(phantoms.getAtomCount()-1));
            	chemModelRelay.getPhantoms().getConnectedBondsList(phantoms.getAtom(phantoms.getAtomCount()-1)).get(0).setAtom(merge,1);
            	phantoms.removeAtomOnly(phantoms.getAtomCount()-1);
            }
        }
        chemModelRelay.updateView();
    }

    @Override
    public void mouseClickedUp( Point2d worldCoord, int modifiers) {
        JChemPaintRendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
        double d = model.getSelectionRadius() / model.getScale();
    	if (start.distance(worldCoord) < 4*d)
    		return;
    	IAtomContainer fromContainer = null, toContainer = null;
    	IChemModel chemModel = chemModelRelay.getChemModel();
    	if(source != null){
			fromContainer = ChemModelManipulator.getRelevantAtomContainer(chemModel, source);
			if (chemModelRelay.getPhantoms().getAtomCount() > 0)
				chemModelRelay.getPhantoms().removeAtomOnly(0);
			if (merge != null)
		    	toContainer = ChemModelManipulator.getRelevantAtomContainer(chemModel, merge);
            AtomBondSet fragment = new AtomBondSet(chemModelRelay.getPhantoms());
            chemModelRelay.addFragment(fragment, fromContainer, toContainer==fromContainer ? null : toContainer);
    	} else {
			if (merge != null)
		    		toContainer = ChemModelManipulator.getRelevantAtomContainer(chemModel, merge);
	    	chemModelRelay.addFragment(new AtomBondSet(chemModelRelay.getPhantoms()), null, toContainer);
    	}
        if (merge != null)
        	chemModelRelay.updateAtom(merge);
		if (source != null)
			chemModelRelay.updateAtom(source);
	        chemModelRelay.clearPhantoms();
	        chemModelRelay.setPhantomText(null, null);
	        chemModelRelay.getRenderer().getRenderer2DModel().getMerge().clear();
    }

    public String getDrawModeString() {
        return "Draw Bond Chain";
    }
    
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }
}
