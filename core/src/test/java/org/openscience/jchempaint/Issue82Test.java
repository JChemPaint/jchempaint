/*
 */
package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * 1. place a C-C bond; 2. click draw chain; 3. start dragging from C and
 * notice one H too much in status bar; notice also the
 * explicit C of the merged methyl stays explicit, unexpectedly, see #103);
 * 4. clean mol will bomb with NullPointerException
 * @author <ralf@ark.in-berlin.de>
 */
public class Issue82Test extends AbstractAppletTest {

	@Test public void testIssue82() {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel pane = (JChemPaintPanel)jcppanel.target;
		applet.button("C").click();
		applet.button("bondTool").click();
		pane.get2DHub().mouseClickedDown(300, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		pane.get2DHub().mouseClickedUp(300, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		try { applet.panel("renderpanel").robot.wait(100);}
		catch (Exception e) {}

		applet.button("chain").click();
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		pane.get2DHub().mouseClickedDown(200, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		pane.get2DHub().mouseDrag(200, 100, 300, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		try {
			pane.get2DHub().mouseClickedUp(300, 100);
			pane.get2DHub().updateView();
			applet.panel("renderpanel").robot.waitForIdle();
		} catch (Exception e) 
		{
			Assert.fail();
		}

		int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(4, atomCount);
		Assert.assertEquals(3, bondCount);
		Assert.assertEquals(10, implicitHCount);
		
		applet.button("undo").click();
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		atomCount=0; bondCount=0; implicitHCount=0;
		for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(2, atomCount);
		Assert.assertEquals(1, bondCount);
		Assert.assertEquals(6, implicitHCount);
		
		try {
			applet.button("undo").click();
			pane.get2DHub().updateView();
			applet.panel("renderpanel").robot.waitForIdle();
		} catch (Exception e) 
		{
			Assert.fail();
		}
		atomCount=0; bondCount=0; implicitHCount=0;
		for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(0, atomCount);
		Assert.assertEquals(0, bondCount);
	}
}