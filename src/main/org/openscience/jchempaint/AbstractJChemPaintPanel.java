package org.openscience.jchempaint;

import javax.swing.JPanel;

import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.interfaces.IChemModel;

public abstract class AbstractJChemPaintPanel extends JPanel{

	protected RenderPanel renderPanel;

	/**
	 * Return the ControllerHub of this JCPPanel
	 * 
	 * @return The ControllerHub
	 */
	public ControllerHub get2DHub() {
		return renderPanel.getHub();
	}
	
	/**
	 * Return the chemmodel of this JCPPanel
	 * 
	 * @return
	 */
	public IChemModel getChemModel(){
		return renderPanel.getChemModel();
	}

	/**
	 * Return the chemmodel of this JCPPanel
	 * 
	 * @return
	 */
	public void setChemModel(IChemModel model){
		renderPanel.setChemModel(model);
	}
}
