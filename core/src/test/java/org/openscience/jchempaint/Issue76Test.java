/*
 */
package org.openscience.jchempaint;

import javax.vecmath.Point2d;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * #76: synopsis: place a benzene; draw chain from a benzene C-atom
 * should not change this atom's position; first undo should give unaltered
 * benzene; second back to zero, without NullPointerException
 * #154: merging with chain garbles implicit Hs
 * #158: with chain, single click on atom creates undo slot, should do nothing
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue76Test extends AbstractAppletTest {

    @Test public void testIssue76() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel pane = (JChemPaintPanel)jcppanel.target;
        applet.button("benzene").click();
        pane.get2DHub().mouseClickedDown(100, 100);
        pane.get2DHub().mouseClickedUp(100, 100);
        pane.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();

		Point2d atompos=pane.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d();
		atompos = pane.getRenderPanel().getRenderer().toScreenCoordinates(atompos.x, atompos.y);
		int x = (int) atompos.x;
		int y = (int) atompos.y;
		applet.button("C").click();
	    applet.button("chain").click();
	    pane.get2DHub().mouseClickedDown(x, y);
	    pane.get2DHub().updateView();
	    applet.panel("renderpanel").robot.waitForIdle();
	    pane.get2DHub().mouseClickedUp(x, y);
	    pane.get2DHub().updateView();
	    applet.panel("renderpanel").robot.waitForIdle();
	    pane.get2DHub().mouseClickedDown(x, y);
	    pane.get2DHub().updateView();
	    applet.panel("renderpanel").robot.waitForIdle();
	    pane.get2DHub().mouseDrag(x, y, x+200, y);
	    pane.get2DHub().updateView();
	    applet.panel("renderpanel").robot.waitForIdle();
	    pane.get2DHub().mouseClickedUp(x+200, y);
	    pane.get2DHub().updateView();
	    applet.panel("renderpanel").robot.waitForIdle();

		int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(11, atomCount);
		Assert.assertEquals(11, bondCount);
		Assert.assertEquals(16, implicitHCount);
	
		applet.button("undo").click();
		pane.get2DHub().updateView();

        atomCount=0; bondCount=0;
		for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(6, atomCount);
        Assert.assertEquals(6, bondCount);

        try {
			applet.button("undo").click();
			pane.get2DHub().updateView();
		} catch (Exception e) {
			Assert.fail();
		}
        atomCount=0; bondCount=0;
		for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(0, atomCount);
        Assert.assertEquals(0, bondCount);

	}
}
