/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
package org.openscience.jchempaint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Iterator;

import javax.swing.JPanel;

import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.ControllerModel;
import org.openscience.cdk.controller.IViewEventRelay;
import org.openscience.cdk.controller.SwingMouseEventRelay;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.renderer.ISelection;
import org.openscience.cdk.renderer.IntermediateRenderer;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class RenderPanel extends JPanel implements IViewEventRelay {
	
	private IntermediateRenderer renderer;

	private boolean isNewChemModel;
	
	private ControllerHub hub;
	
	private ControllerModel controllerModel;
	
	private SwingMouseEventRelay mouseEventRelay;
	
	private boolean fitToScreen;
	
	private boolean shouldPaintFromCache = false;
	
	public RenderPanel(IChemModel chemModel, int width, int height,
            boolean fitToScreen) {
		this.setupMachinery(chemModel, fitToScreen);
		this.setupPanel(width, height);
		this.fitToScreen = fitToScreen;
	}
	
	public void setFitToScreen(boolean fitToScreen) {
	    this.renderer.setFitToScreen(fitToScreen);
	}
	
	public IChemModel getChemModel() {
	    return this.hub.getIChemModel();
	}

	public void setChemModel(IChemModel model) {
	    this.setupMachinery(model, this.fitToScreen); 
	}
	
	public ControllerHub getHub() {
	    return hub;
	}
	
	private void setupMachinery(IChemModel chemModel, boolean fitToScreen) {
		// setup the Renderer and the controller 'model'
		this.renderer = new IntermediateRenderer();
		this.renderer.setFitToScreen(fitToScreen);
		this.controllerModel = new ControllerModel();
		
		// connect the Renderer to the Hub
		this.hub = new ControllerHub(controllerModel, renderer, chemModel, this);
		
		// connect mouse events from Panel to the Hub
		this.mouseEventRelay = new SwingMouseEventRelay(this.hub);
		this.addMouseListener(mouseEventRelay);
		this.addMouseMotionListener(mouseEventRelay);
		this.isNewChemModel = true;
	}
	
	private void setupPanel(int width, int height) {
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(width, height));
	}
	
	public Image takeSnapshot() {
	    return this.takeSnapshot(this.getBounds());
	}
	
	public Image takeSnapshot(Rectangle bounds) {
        Image image = GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getScreenDevices()[0]
                        .getDefaultConfiguration()
                        .createCompatibleImage(bounds.width, bounds.height);
        Graphics2D g = (Graphics2D)image.getGraphics();
        super.paint(g);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        this.paintChemModel(g, bounds);
        return image;
    }
	
	public void paintChemModel(Graphics2D g, Rectangle screenBounds) {
	    
	    IChemModel chemModel = this.hub.getIChemModel();
	    if (chemModel != null && chemModel.getMoleculeSet() != null) {
	        
	        // determine the size the canvas needs to be in order to fit the model
	        Rectangle diagramBounds = renderer.calculateScreenBounds(chemModel);
	        
	        if (this.overlaps(screenBounds, diagramBounds)) {
	            Rectangle union = screenBounds.union(diagramBounds); 
	            this.setPreferredSize(union.getSize());
	            this.revalidate();
            }
	        this.paintChemModel(chemModel, g, screenBounds);
        }    
	}
	
	/**
	 * Check to see if the molecule bounding box has overlapped a screen edge. 
	 * 
	 * @param screenBounds the bounding box of the screen
	 * @param diagramBounds the bounding box of the molecule on the screen
	 * @return
	 */
	private boolean overlaps(Rectangle screenBounds, Rectangle diagramBounds) {
	    return screenBounds.getMinX() > diagramBounds.getMinX()
	        || screenBounds.getMinY() > diagramBounds.getMinY()
	        || screenBounds.getMaxX() < diagramBounds.getMaxX()
	        || screenBounds.getMaxY() < diagramBounds.getMaxY();
	}
	
	private void paintChemModel(IChemModel chemModel, Graphics2D g, Rectangle bounds) {
      
        // paint the chem model, and record that it is no longer new
        renderer.paintChemModel(chemModel, g, bounds, isNewChemModel);
        isNewChemModel = false;
        
        /*
         * This is dangerous, but necessary to allow fast 
         * repainting when scrolling the canvas 
         */
        this.shouldPaintFromCache = true;
	}
	
	public void setIsNewChemModel(boolean isNewChemModel) {
	    this.isNewChemModel = isNewChemModel;
	}
	
	public void paint(Graphics g) {
	    this.setBackground(renderer.getRenderer2DModel().getBackColor());
	    super.paint(g);
		  // set the graphics to antialias
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        
		if (this.shouldPaintFromCache) {
		    this.paintFromCache(g2);
		} else {
		    this.paintChemModel(g2, this.getBounds());
		}
	}
	
	private void paintFromCache(Graphics2D g) {
	    renderer.repaint(g);
	}

	public void updateView() {
	    /*
         * updateView should only be called in a ControllerModule where 
         * we assume that things have changed so we can't use the cache
         */ 
	    this.shouldPaintFromCache = false;
	    
		this.repaint();
	}
	
	/**
	 *  Returns one of the status strings at the given position
	 *
	 * @param  position
	 * @return the current status
	 */
	public String getStatus(int position) {
		// return this.status[position];
		String status = "";
		// logger.debug("Getting status");
		if (position == 0) {
			// depict editing mode
			status = hub.getActiveDrawModule().getDrawModeString();
		}
		else if (position == 1) {
			// depict bruto formula
		    IChemModel chemModel = hub.getIChemModel();
		    if(chemModel.getMoleculeSet()!=null && chemModel.getMoleculeSet().getAtomContainerCount()>0){
		    //TODO should be for all atomcontainers
			/*IMolecularFormula wholeModel = NoNotificationChemObjectBuilder.getInstance().newMolecularFormula();
        	Iterator<IAtomContainer> containers 
        	    = ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
        	while (containers.hasNext()) {
        		for(IAtom atom : containers.next().atoms()){
        			wholeModel.addIsotope(atom);
        		}
        	}*/
        	String formula 
        	        = MolecularFormulaManipulator.getHTML(        	        		
        	                MolecularFormulaManipulator.getMolecularFormula(chemModel.getMoleculeSet().getAtomContainer(0)),
        	                true,
        	                false);
			int implicitHs = 0;
			/*for (int i = 0; i < wholeModel.getAtomCount(); i++) {
			    IAtom a = wholeModel.getAtom(i);  
                if (a.getHydrogenCount() != null) {
                    implicitHs += a.getHydrogenCount();
                }
            }*/
			status = "<html>"
                    + formula
                    + (implicitHs == 0 ? "" : " (of these "
                    + implicitHs + " Hs implicit)") + "</html>";
		    }
		}
		else if (position == 2) {
			// depict brutto formula of the selected molecule or part of molecule
		    ISelection selection = renderer.getRenderer2DModel().getSelection(); 
			if (selection != null) {
			    IAtomContainer ac = selection.getConnectedAtomContainer();
			    if(ac!=null){
					String formula = MolecularFormulaManipulator.getHTML(
					        MolecularFormulaManipulator.getMolecularFormula(ac),
					        true,
					        false);
					status = "<html>" + formula + "</html>";
			    }
			}
		}
		return status;
	}

	
	public IntermediateRenderer getRenderer() {
		return renderer;
	}
}
