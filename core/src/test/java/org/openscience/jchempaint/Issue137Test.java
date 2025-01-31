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
 * #137: move C in ethane over the other gives CH3
 * #153: merging ethane internally does not delete bond from model
 */
public class Issue137Test extends AbstractAppletTest {

    @Test public void testIssue137() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().mouseClickedUp(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();

        // For some reason this does not work
        // applet.button("select").click();
        // panel.get2DHub().updateView();
        // so we crank the lever manually
        JCPAction act = new JCPAction().getAction(panel, "org.openscience.jchempaint.action.ChangeModeAction@select");
        act.actionPerformed(null);
        applet.panel("renderpanel").robot.waitForIdle();
		IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
		Renderer r = panel.getRenderPanel().getRenderer();
		Point2d atompos0=ethane.getAtom(0).getPoint2d();
		Point2d atompos1=ethane.getAtom(1).getPoint2d();
		atompos0 = r.toScreenCoordinates(atompos0.x, atompos0.y);
		atompos1 = r.toScreenCoordinates(atompos1.x, atompos1.y);
		panel.get2DHub().mouseClickedDown((int)atompos0.x, (int)atompos0.y);
		panel.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		panel.get2DHub().mouseClickedUp((int)atompos0.x, (int)atompos0.y);
		panel.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		panel.get2DHub().mouseClickedDown((int)atompos0.x, (int)atompos0.y);
		panel.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		panel.get2DHub().mouseDrag((int)atompos0.x, (int)atompos0.y, (int)atompos1.x, (int)atompos1.y, 0);
		panel.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		panel.get2DHub().mouseClickedUp((int)atompos1.x, (int)atompos1.y);
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
		Assert.assertEquals(4, implicitHCount);
    }
}
