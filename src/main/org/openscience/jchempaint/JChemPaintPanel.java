package org.openscience.jchempaint;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
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
		JMenuBar menu = new JChemPaintMenuBar(this, "stable");
		JPanel topContainer = new JPanel(new BorderLayout());
		topContainer.setLayout(new BorderLayout());
		this.add(topContainer,BorderLayout.NORTH);
		topContainer.add(menu,BorderLayout.NORTH);
		p = new RenderPanel(ac);
		this.add(p,BorderLayout.CENTER);
		JToolBar toolbar = JCPToolBar.getToolbar(this, 1);
		topContainer.add(toolbar,BorderLayout.CENTER);
	}

	public Controller2DHub get2DHub() {
		return p.getHub();
	}
}
