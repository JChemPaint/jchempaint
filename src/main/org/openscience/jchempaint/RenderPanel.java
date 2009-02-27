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
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.ControllerModel;
import org.openscience.cdk.controller.IViewEventRelay;
import org.openscience.cdk.controller.SwingMouseEventRelay;
import org.openscience.cdk.controller.undoredo.IUndoListener;
import org.openscience.cdk.controller.undoredo.IUndoRedoable;
import org.openscience.cdk.controller.undoredo.UndoRedoHandler;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.AtomContainerBoundsGenerator;
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
import org.openscience.jchempaint.undoredo.SwingUndoRedoFactory;

public class RenderPanel extends JPanel implements IViewEventRelay, IUndoListener {

	private Renderer renderer;

	private boolean isNewChemModel;

	private ControllerHub hub;

	private ControllerModel controllerModel;

	private SwingMouseEventRelay mouseEventRelay;

	private boolean fitToScreen;

	private boolean shouldPaintFromCache = false;

	private UndoManager undoManager=new UndoManager();

	public RenderPanel(IChemModel chemModel, int width, int height,
            boolean fitToScreen) {
		this.setupMachinery(chemModel, fitToScreen);
		this.setupPanel(width, height);
		this.fitToScreen = fitToScreen;
		undoManager.setLimit(Integer.parseInt(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UndoStackSize")));
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

		UndoRedoHandler undoredohandler = new UndoRedoHandler();
		undoredohandler.addIUndoListener(this);
		// connect the Renderer to the Hub
		this.hub =
		    new ControllerHub(controllerModel, renderer, chemModel, this, undoredohandler, new SwingUndoRedoFactory());

		// connect mouse events from Panel to the Hub
		this.mouseEventRelay = new SwingMouseEventRelay(this.hub);
		this.addMouseListener(mouseEventRelay);
		this.addMouseMotionListener(mouseEventRelay);
		this.addMouseWheelListener(mouseEventRelay);
		this.isNewChemModel = true;
	}

	private List<IGenerator> makeGenerators() {
	    List<IGenerator> generators = new ArrayList<IGenerator>();
	    generators.add(new AtomContainerBoundsGenerator());
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

        IChemModel chemModel = hub.getIChemModel();
        if (isValidChemModel(chemModel)) {
            this.paintChemModel(chemModel, g, bounds);
        }
    }

	private boolean isValidChemModel(IChemModel chemModel) {
	    return chemModel != null
	            && (chemModel.getMoleculeSet() != null
	                    || chemModel.getReactionSet() != null);
	}

    private void shift(
            Graphics2D g, Rectangle screenBounds, Rectangle diagramBounds) {
        int screenMaxX  = screenBounds.x + screenBounds.width;
        int screenMaxY  = screenBounds.y + screenBounds.height;
        int diagramMaxX = diagramBounds.x + diagramBounds.width;
        int diagramMaxY = diagramBounds.y + diagramBounds.height;

        int leftOverlap   = screenBounds.x - diagramBounds.x;
        int rightOverlap  = diagramMaxX - screenMaxX;
        int topOverlap    = screenBounds.y - diagramBounds.y;
        int bottomOverlap = diagramMaxY - screenMaxY;

        boolean overlapsLeft   = leftOverlap   > 0;
        boolean overlapsRight  = rightOverlap  > 0;
        boolean overlapsTop    = topOverlap    > 0;
        boolean overlapsBottom = bottomOverlap > 0;

        int dx = 0;
        int dy = 0;
        int w = screenBounds.width;
        int h = screenBounds.height;

        if (overlapsLeft) {
            dx = leftOverlap;
        }

        if (overlapsRight) {
            w += rightOverlap;
        }

        if (overlapsTop) {
            dy = topOverlap;
        }

        if (overlapsBottom) {
            h += bottomOverlap;
        }

        if (dx != 0 || dy != 0) {
            renderer.shiftDrawCenter(dx, dy);
            this.setPreferredSize(new Dimension(w, h));
            this.revalidate();
            super.paint(g);
            this.renderer.paintChemModel(
                    this.hub.getIChemModel(),new AWTDrawVisitor(g));
        }
    }

    public void paint(Graphics g) {
        this.setBackground(renderer.getRenderer2DModel().getBackColor());
        super.paint(g);

        Graphics2D g2 = (Graphics2D)g;

    	if (this.shouldPaintFromCache) {
    	    renderer.repaint(new AWTDrawVisitor(g2));
    	} else {
    	    IChemModel chemModel = this.hub.getIChemModel();

    	    if (!isValidChemModel(chemModel)) return;

    		/*
    		 * It is more correct to use a rectangle starting at (0,0)
    		 * than to use getBounds() as the RenderPanel may be a child
    		 * of some container window, and its Graphics will be
    		 * translated relative to its parent.
    		 */
    	    Rectangle screen = new Rectangle(0, 0, getWidth(), getHeight());

    	    if (renderer.getRenderer2DModel().isFitToScreen()) {
    	        this.paintChemModelFitToScreen(chemModel, g2, screen);
    	    } else {
    	        this.paintChemModel(chemModel, g2, screen);
    	    }
    	}
    }

    /**
     * Paint the chem model not fit-to-screen
     *
     * @param chemModel
     * @param g
     * @param screen
     */
    private void paintChemModel(
            IChemModel chemModel,Graphics2D g, Rectangle screen) {

        if (isNewChemModel) {
            renderer.setScale(chemModel);
        }

        Rectangle diagram = renderer.paintChemModel(
                chemModel, new AWTDrawVisitor(g));

        isNewChemModel = false;

        /*
         * This is dangerous, but necessary to allow fast
         * repainting when scrolling the canvas
         */
        this.shouldPaintFromCache = true;

        // determine the size the canvas needs to be to fit the model
        shift(g, screen, diagram);
    }

    private void paintChemModelFitToScreen(
            IChemModel chemModel, Graphics2D g, Rectangle screen) {

        renderer.paintChemModel(chemModel, new AWTDrawVisitor(g), screen, true);

    }

    public void setIsNewChemModel(boolean isNewChemModel) {
	    this.isNewChemModel = isNewChemModel;
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
			status = JCPMenuTextMaker.getInstance("applet").getText(hub.getActiveDrawModule().getDrawModeString());
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
	    	status = GT._("Zoomfactor")
                    + ": "
                    + NumberFormat.getPercentInstance().format(
                            renderer.getRenderer2DModel().getZoomFactor());
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

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public void doUndo(IUndoRedoable undoredo) {
		undoManager.addEdit((UndoableEdit)undoredo);
		if(this.getParent().getParent().getParent() instanceof JChemPaintPanel)
			((JChemPaintPanel)this.getParent().getParent().getParent()).updateUndoRedoControls();
	}
}
