package org.openscience.jchempaint;


import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * #168: Create two atoms, link them with a bond. Fine so far. Undo all three actions.
 * Redo the add atom. You get two new atom objects. Redo the add bond.
 * It adds a bond between the old atoms, this is incorrect.
 * 
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue168Test extends AbstractAppletTest {

    @Test public void testIssue168() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        
        applet.button("C").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedDown(160, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp(160, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        
        applet.button("bondTool").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseDrag(100, 100, 160, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp(160, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        
		int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(2, atomCount);
		Assert.assertEquals(1, bondCount);
		Assert.assertEquals(6, implicitHCount);

		applet.button("undo").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
		applet.button("undo").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
		applet.button("undo").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();

		applet.button("redo").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
		applet.button("redo").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
		applet.button("redo").click();
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
		Assert.assertEquals(6, implicitHCount);

    }

}
