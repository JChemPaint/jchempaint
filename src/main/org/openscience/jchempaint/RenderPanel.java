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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.ExternalHighlightGenerator;
import org.openscience.cdk.renderer.generators.HighlightAtomGenerator;
import org.openscience.cdk.renderer.generators.HighlightBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.LonePairGenerator;
import org.openscience.cdk.renderer.generators.RadicalGenerator;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.generators.SelectionGenerator;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.renderer.visitor.SVGGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class RenderPanel extends JPanel implements IViewEventRelay {

	private Renderer renderer;

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
	    this.renderer.getRenderer2DModel().setFitToScreen(fitToScreen);
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
	    
		this.renderer = new Renderer(makeGenerators(), new AWTFontManager());
		this.setFitToScreen(fitToScreen);
		this.controllerModel = new ControllerModel();

		// connect the Renderer to the Hub
		this.hub = 
		    new ControllerHub(controllerModel, renderer, chemModel, this);

		// connect mouse events from Panel to the Hub
		this.mouseEventRelay = new SwingMouseEventRelay(this.hub);
		this.addMouseListener(mouseEventRelay);
		this.addMouseMotionListener(mouseEventRelay);
		this.isNewChemModel = true;
	}
	
	private List<IGenerator> makeGenerators() {
	    List<IGenerator> generators = new ArrayList<IGenerator>();
	    generators.add(new RingGenerator());
        generators.add(new BasicAtomGenerator());
        generators.add(new LonePairGenerator());
        generators.add(new RadicalGenerator());
        generators.add(new ExternalHighlightGenerator());
        generators.add(new HighlightAtomGenerator());
        generators.add(new HighlightBondGenerator());
        generators.add(new SelectionGenerator());
        return generators;
	}

	private void setupPanel(int width, int height) {
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(width, height));
	}

	public String toSVG() {
	    IChemModel chemModel = this.hub.getIChemModel();
	    if (chemModel != null && chemModel.getMoleculeSet() != null) {
	    	SVGGenerator svgGenerator = new SVGGenerator();
	    	this.renderer.paintChemModel(
	    	        chemModel, svgGenerator, this.getBounds(), true);
	    	return svgGenerator.getResult();
	    } else {
	    	return "<svg></svg>";
	    }
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
        takeSnapshot(g, bounds);
        return image;
	}
	
	public void takeSnapshot(Graphics2D g, Rectangle bounds){
        super.paint(g);
        this.paintChemModel(g, bounds);
    }
	
	private boolean isValidChemModel(IChemModel chemModel) {
	    return chemModel != null 
	            && (chemModel.getMoleculeSet() != null
	                    || chemModel.getReactionSet() != null);
	}

	public void paintChemModel(Graphics2D g, Rectangle screenBounds) {

	    IChemModel chemModel = this.hub.getIChemModel();
	    if (isValidChemModel(chemModel)) {

	        // paint first, so that the transform is set correctly
	        this.paintChemModel(chemModel, g, screenBounds);
	        
	        // don't calculate screen size as it is equal to screen size!
	        if (renderer.getRenderer2DModel().isFitToScreen()) {
	            return;
	        }
	        
	        // determine the size the canvas needs to be to fit the model
	        Rectangle diagramBounds = renderer.calculateScreenBounds(chemModel);
	        if (this.overlaps(screenBounds, diagramBounds)) {
	            Rectangle union = screenBounds.union(diagramBounds);
	            this.setPreferredSize(union.getSize());
	            this.revalidate();
            }
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

	private void paintChemModel(
	        IChemModel chemModel, Graphics2D g, Rectangle bounds) {

        // paint the chem model, and record that it is no longer new
	    
        renderer.paintChemModel(chemModel, 
                                new AWTDrawVisitor(g),
                                bounds,
                                isNewChemModel);
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
	    
        Graphics2D g2 = (Graphics2D)g;
       
		if (this.shouldPaintFromCache) {
		    this.paintFromCache(g2);
		} else {

			/*
			 * It is more correct to use a rectangle starting at (0,0)
			 * than to use getBounds() as the RenderPanel may be a child
			 * of some container window, and its Graphics will be
			 * translated relative to its parent.
			 */
			this.paintChemModel(g2,
					new Rectangle(0, 0, this.getWidth(), this.getHeight()));
		}
	}

	private void paintFromCache(Graphics2D g) {
	    renderer.repaint(new AWTDrawVisitor(g));
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
		String status = "";
		if (position == 0) {
			// depict editing mode
			status = JCPMenuTextMaker.getInstance().getText(hub.getActiveDrawModule().getDrawModeString());
		} else if (position == 1) {
			// depict bruto formula
		    IChemModel chemModel = hub.getIChemModel();

		    if (chemModel.getMoleculeSet() != null
                    && chemModel.getMoleculeSet().getAtomContainerCount() > 0) {
		        IMolecularFormula wholeModel = NoNotificationChemObjectBuilder.getInstance().newMolecularFormula();
		        Iterator<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
		        int implicitHs = 0;
	        	while (containers.hasNext()) {
	        		for(IAtom atom : containers.next().atoms()){
	        			wholeModel.addIsotope(atom);
	                   	if (atom.getHydrogenCount() != null) {
	                    	implicitHs += atom.getHydrogenCount();
	                	}	        		
	                }
	        	}
		        String formula
		        = MolecularFormulaManipulator.getHTML(
		                        wholeModel,
		                        true,
		                        false);
		        status = makeStatusBarString(formula, implicitHs, MolecularFormulaManipulator.getNaturalExactMass(wholeModel));
		    }
	    } else if (position == 2) {
	        // depict brutto formula of the selected molecule or part of molecule
	        IChemObjectSelection selection =
	            renderer.getRenderer2DModel().getSelection();
	        
	        if (selection != null) {
	            IAtomContainer ac = selection.getConnectedAtomContainer();
	            if (ac != null) {
			        int implicitHs = 0;
	        		for(IAtom atom : ac.atoms()){
	        			if (atom.getHydrogenCount() != null) {
	                    	implicitHs += atom.getHydrogenCount();
	                	}	        		
	                }
	                String formula = MolecularFormulaManipulator
	                .getHTML(MolecularFormulaManipulator
	                        .getMolecularFormula(ac), true, false);
	                status = makeStatusBarString(formula, implicitHs, AtomContainerManipulator.getNaturalExactMass(ac));
	            }
	        }
	    } else if (position == 3) {
	    	status= GT._("Zoomfactor")+": "+NumberFormat.getPercentInstance().format(renderer.getZoom());
	    }
		return status;
	}
	
	private String makeStatusBarString(String formula, int implicitHs, double mass){
		DecimalFormat df1 = new DecimalFormat("####.0000");
        return "<html>"
            + formula
            + (implicitHs == 0 ? "" : " ("+GT._("of these")+ " "
                + implicitHs + " "+GT._("Hs implicit")+")")+" ("+GT._("mass")+" "+df1.format(mass)+")</html>";
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
