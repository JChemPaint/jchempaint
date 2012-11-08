package org.openscience.jchempaint;

import javax.vecmath.Point2d;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.action.JCPAction;
import org.openscience.jchempaint.renderer.Renderer;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 *
 * 129: Input SMILES C=C (ethylene); (with single bond active) click on
 *      double bond: NOT ethyne; click on triple bond; click on quadruple
 *      bond: H2C-CH2, should be H3C-CH3
 */
public class Issue129Test extends AbstractAppletTest {
	
    @Test public void testIssue129() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        
    	jcpApplet.setSmiles("C=C");
    	panel.get2DHub().updateView();
        
        int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(2, atomCount);
        Assert.assertEquals(1, bondCount);
		Assert.assertEquals(4, implicitHCount);
		
        IAtomContainer ethylene = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
		Renderer r = panel.getRenderPanel().getRenderer();
		Point2d atompos0=ethylene.getAtom(0).getPoint2d();
		Point2d atompos1=ethylene.getAtom(1).getPoint2d();
		Point2d bondpos = r.toScreenCoordinates((atompos0.x + atompos1.x)/2, (atompos0.y + atompos1.y)/2);
		panel.get2DHub().mouseClickedDown((int)bondpos.x, (int)bondpos.y);
		panel.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		panel.get2DHub().mouseClickedUp((int)bondpos.x, (int)bondpos.y);
		panel.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();

        atomCount=0; bondCount=0; implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(2, atomCount);
        Assert.assertEquals(1, bondCount);
		Assert.assertEquals(2, implicitHCount);
    }

}
