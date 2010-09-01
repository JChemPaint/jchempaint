/*
 * Copyright (C) 2010  Conni Wagner <conni75@users.sourceforge.net>
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
import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.renderer.Renderer;

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
    public void mouseClickedDown( Point2d worldCoord ) {

        start = null;
        dest = null;
        source = null;
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
    public void mouseDrag( Point2d worldCoordFrom, Point2d worldCoordTo ) {
        chemModelRelay.clearPhantoms();
        //how many bonds do we want?
        double distance = start.distance(worldCoordTo);
        int numberofbonds = (int)(distance / (bondLength*.9));
        if(numberofbonds>0){
        	//add start atom
        	IAtomContainer phantoms = getBuilder().newInstance(IAtomContainer.class);
        	IAtom startAtom;
        	if(source==null){
        		startAtom = getBuilder().newInstance(IAtom.class, chemModelRelay.getController2DModel().getDrawElement(), start );
        	}else{
        		startAtom=source;
        	}   
        	phantoms.addAtom(startAtom);
        	//make atoms and bonds as needed
        	for(int i=0;i<numberofbonds;i++){
        		IAtom nextAtom = getBuilder().newInstance(IAtom.class, chemModelRelay.getController2DModel().getDrawElement(), new Point2d(startAtom.getPoint2d().x+bondLength, startAtom.getPoint2d().y));
        		phantoms.addAtom(nextAtom);
        		phantoms.addBond(getBuilder().newInstance(IBond.class, startAtom, nextAtom, IBond.Order.SINGLE));
        		startAtom = nextAtom;
        	}
            //calculate a second point on the perpendicular through start point
            double angle = Math.PI / 2;
            double costheta = Math.cos(angle);
            double sintheta = Math.sin(angle);
            Point2d point = new Point2d(worldCoordTo);
            Point2d center = new Point2d(start);
            double relativex = point.x - center.x;
            double relativey = point.y - center.y;
            Point2d start2 = new Point2d();
            start2.x = relativex * costheta - relativey * sintheta + center.x;
            start2.y = relativex * sintheta + relativey * costheta + center.y;
            //scale that
            start2.x = start.x + (start2.x-start.x)/numberofbonds;
            start2.y = start.y + (start2.y-start.y)/numberofbonds;
            Point2d worldCoordTo2 = new Point2d();
            worldCoordTo2.x = relativex * costheta - relativey * sintheta + point.x;
            worldCoordTo2.y = relativex * sintheta + relativey * costheta + point.y;
            //scale that
            worldCoordTo2.x = worldCoordTo.x + (worldCoordTo2.x-worldCoordTo.x)/numberofbonds;
            worldCoordTo2.y = worldCoordTo.y + (worldCoordTo2.y-worldCoordTo.y)/numberofbonds;
            for(int i=1;i<phantoms.getAtomCount();i++){
        		Point2d p1 = new Point2d();
            	if(i % 2 == 1){
            		//atoms on line2
                    p1.interpolate(start2, worldCoordTo2, 1.0/phantoms.getAtomCount()*i);
            	}else{
            		//atoms on line
                    p1.interpolate(start, worldCoordTo, 1.0/phantoms.getAtomCount()*i);
            	}
        		phantoms.getAtom(i).setPoint2d(p1);
            }
            chemModelRelay.setPhantoms(phantoms);
            chemModelRelay.setPhantomText(""+phantoms.getAtomCount(), worldCoordTo);
            IAtom closestAtom = chemModelRelay.getClosestAtom(phantoms.getAtom(phantoms.getAtomCount()-1));
            chemModelRelay.getRenderer().getRenderer2DModel().getMerge().remove(merge);
            merge =  (IAtom) getHighlighted(phantoms.getAtom(phantoms.getAtomCount()-1).getPoint2d(), closestAtom);
            if(merge!=null){
            	chemModelRelay.getRenderer().getRenderer2DModel().getMerge().put(merge,phantoms.getAtom(phantoms.getAtomCount()-1));
            	chemModelRelay.getPhantoms().getConnectedBondsList(phantoms.getAtom(phantoms.getAtomCount()-1)).get(0).setAtom(merge,1);
            	phantoms.removeAtom(phantoms.getAtomCount()-1);
            }
        }
        chemModelRelay.updateView();
    }

    @Override
    public void mouseClickedUp( Point2d worldCoord ) {
		IAtomContainer toContainer = ChemModelManipulator.getRelevantAtomContainer(chemModelRelay.getChemModel(), merge);
		IAtomContainer fromContainer = ChemModelManipulator.getRelevantAtomContainer(chemModelRelay.getChemModel(), source);
    	if(source!=null){
    		chemModelRelay.getPhantoms().removeAtom(0);
    		chemModelRelay.addFragment(getBuilder().newInstance(IMolecule.class,chemModelRelay.getPhantoms()), fromContainer, toContainer==fromContainer ? null : toContainer);
    	}else{
    		chemModelRelay.addFragment(getBuilder().newInstance(IMolecule.class,chemModelRelay.getPhantoms()), toContainer, null);
    	}
        if(merge!=null){
        	chemModelRelay.updateAtom(merge);
        }
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
