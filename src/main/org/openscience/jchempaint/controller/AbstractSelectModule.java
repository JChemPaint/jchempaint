/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.controller;

import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Point2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.jchempaint.renderer.BoundsCalculator;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.ShapeSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

/**
 * An abstract base class for select modules. Classes extending this must 
 * implement getDrawModeString and set selection to the appropriate type in 
 * their constructor.
 * 
 * @cdk.module controlbasic
 */
public abstract class AbstractSelectModule extends ControllerModuleAdapter {
    
    protected ShapeSelection selection;
    private Point2d startPoint;
    private String ID;
    
    public AbstractSelectModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
    }
    
    public void mouseClickedDown(Point2d p) {
        Rectangle2D bounds=null;
        if(this.chemModelRelay.getRenderer().getRenderer2DModel().getSelection()!=null 
                    && this.chemModelRelay.getRenderer().getRenderer2DModel()
                    .getSelection().isFilled())
                bounds = BoundsCalculator.calculateBounds(this.chemModelRelay.getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer());
        IAtom closestAtom = chemModelRelay.getClosestAtom(p);
        IChemObject highlightedAtom = getHighlighted( p, closestAtom);
        IBond closestBond = chemModelRelay.getClosestBond(p);
        IChemObject highlightedBond = getHighlighted( p, closestBond);
        //in case the user either starts dragging inside the currrent selection
        //or in highlight distance to an atom, we switch to move mode, else
        //we start a new selection
        if((bounds !=null && bounds.contains(new Point2D.Double(p.x, p.y))) 
            || highlightedAtom!=null || highlightedBond!=null){
                IControllerModule newActiveModule = new MoveModule(this.chemModelRelay, this);
                newActiveModule.setID("move");
                this.chemModelRelay
                        .setActiveDrawModule(newActiveModule);
                ((IMouseEventRelay)this.chemModelRelay).mouseClickedDown(
                		(int)this.chemModelRelay.getRenderer().toScreenCoordinates(p.x, p.y).x,
                		(int)this.chemModelRelay.getRenderer().toScreenCoordinates(p.x, p.y).y);
        }else{
	        this.selection.clear();
	        this.chemModelRelay.getRenderer()
	                           .getRenderer2DModel()
	                           .setSelection(this.selection);
	        startPoint=p;
        }
    }
    
    public void mouseDrag(Point2d from, Point2d to) {
        this.selection.addPoint(to);
        this.chemModelRelay.select(selection);
        this.chemModelRelay.updateView();
    }
    
    public void mouseMove(Point2d p){
        showMouseCursor(p, this.chemModelRelay);
    }

    public static void showMouseCursor(Point2d p, IChemModelRelay chemModelRelay){
        //We look if we the user would move when clicking and show arrows then
        Rectangle2D bounds=null;
        if(chemModelRelay.getRenderer().getRenderer2DModel().getSelection()!=null 
                    && chemModelRelay.getRenderer().getRenderer2DModel()
                    .getSelection().isFilled())
                bounds = BoundsCalculator.calculateBounds(chemModelRelay
                        .getRenderer().getRenderer2DModel().getSelection()
                        .getConnectedAtomContainer());
        IChemObject highlightedAtom = chemModelRelay.getRenderer().getRenderer2DModel().getHighlightedAtom();
        IChemObject highlightedBond = chemModelRelay.getRenderer().getRenderer2DModel().getHighlightedBond();
        if((bounds !=null && bounds.contains(new Point2D.Double(p.x, p.y))) 
                || highlightedAtom!=null || highlightedBond!=null){
            chemModelRelay.setCursor(Cursor.HAND_CURSOR);
        }else{
            chemModelRelay.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    public void mouseClickedUp(Point2d p) {
        this.chemModelRelay.select(selection);
        this.selection.reset();
        if(p.equals(startPoint)){
            IAtom closestAtom = chemModelRelay.getClosestAtom(p);
            IBond closestBond = chemModelRelay.getClosestBond(p);
        	IChemObject singleSelection = getHighlighted( p,
                    closestAtom,
                    closestBond );
        	if(singleSelection instanceof IAtom){
        		IChemObjectSelection selection = new SingleSelection<IAtom>((IAtom)singleSelection);
        		this.chemModelRelay.select(selection);
        	}else if(singleSelection instanceof IBond){
        		IChemObjectSelection selection = new SingleSelection<IBond>((IBond)singleSelection);
            	this.chemModelRelay.select(selection);
        	}
        }
        this.chemModelRelay.updateView();
    }
    
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
