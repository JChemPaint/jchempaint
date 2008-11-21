package org.openscience.jchempaint;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.openscience.cdk.controller.Controller2DHub;
import org.openscience.cdk.interfaces.IAtomContainer;

public class JChemPaintPanel extends JPanel {

	RenderPanel p;
	
	private JComponent lastActionButton;
	public JComponent getActionButton() {
		return lastActionButton;
	}
	public void setActionButton(JComponent actionButton) {
		lastActionButton = actionButton;
	}

	
	public JChemPaintPanel(IAtomContainer ac){
		this.setLayout(new BorderLayout());
		p = new RenderPanel(ac);
		this.add(p,BorderLayout.CENTER);
		JToolBar toolbar = SomeToolBar.getToolbar(this, 1);
		this.add(toolbar,BorderLayout.NORTH);
	}

	public Controller2DHub get2DHub() {
		return p.getHub();
	}
}
