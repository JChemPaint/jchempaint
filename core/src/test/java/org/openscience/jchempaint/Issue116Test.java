package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.action.JCPAction;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue116Test extends AbstractAppletTest {

    @Test public void testIssue116() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("H").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        
		int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(1, atomCount);
		Assert.assertEquals(0, bondCount);
		Assert.assertEquals(1, implicitHCount);
        
        applet.button("O").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();

		atomCount=0; bondCount=0; implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(1, atomCount);
		Assert.assertEquals(0, bondCount);
		Assert.assertEquals(2, implicitHCount);

        try {
            // For some reason this does not work
            // applet.button("undo").click();
            // panel.get2DHub().updateView();
            // so we crank the lever manually
            JCPAction act = new JCPAction().getAction(panel, "org.openscience.jchempaint.action.UndoAction");
            act.actionPerformed(null);
		} catch (Exception e) {
			Assert.fail();
		}
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        
		atomCount=0; bondCount=0; implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(1, atomCount);
		Assert.assertEquals(0, bondCount);
		Assert.assertEquals(1, implicitHCount);
    }
}
