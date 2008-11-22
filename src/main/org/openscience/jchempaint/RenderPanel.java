package org.openscience.jchempaint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import org.openscience.cdk.controller.Controller2DHub;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.controller.IViewEventRelay;
import org.openscience.cdk.controller.SwingMouseEventRelay;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.IntermediateRenderer;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

public class RenderPanel extends JPanel implements IViewEventRelay {
	
	private IntermediateRenderer renderer;
	private IChemModel chemModel;
	
	private Controller2DHub hub;
	public Controller2DHub getHub() {
		return hub;
	}
	

	private Controller2DModel controllerModel;
	private SwingMouseEventRelay mouseEventRelay;
	
	
	public RenderPanel(IChemModel chemModel) {
		this.chemModel = chemModel;
		this.setupMachinery();
		this.setupPanel();
	}
	
	private void setupMachinery() {
		// setup the Renderer and the controller 'model'
		this.renderer = new IntermediateRenderer();
		this.controllerModel = new Controller2DModel();
		
		// connect the Renderer to the Hub
		this.hub = new Controller2DHub(this.controllerModel, this.renderer, chemModel,this);
		
		// connect mouse events from Panel to the Hub
		this.mouseEventRelay = new SwingMouseEventRelay(this.hub);
		this.addMouseListener(mouseEventRelay);
		this.addMouseMotionListener(mouseEventRelay);
	}
	
	private void setupPanel() {
		this.setBackground(Color.WHITE);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		if (this.chemModel != null) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			//TODO render the chemodel, not atomcontainer
			if(this.chemModel.getMoleculeSet()!=null && this.chemModel.getMoleculeSet().getAtomContainerCount()>0)
				this.renderer.paintMolecule(this.chemModel.getMoleculeSet().getAtomContainer(0), g2, this.getBounds());
		}
	}

	public void updateView() {
		this.repaint();
	}

}
