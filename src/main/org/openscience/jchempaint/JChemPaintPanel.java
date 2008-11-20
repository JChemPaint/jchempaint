package org.openscience.jchempaint;

import javax.swing.JPanel;

import org.openscience.cdk.interfaces.IAtomContainer;

public class JChemPaintPanel extends JPanel {

	public JChemPaintPanel(IAtomContainer ac){
		RenderPanel p = new RenderPanel(ac);
		this.add(p);
	}
}
