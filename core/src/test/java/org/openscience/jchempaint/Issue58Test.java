package org.openscience.jchempaint;

import java.awt.AWTException;
import java.awt.Point;
import javax.vecmath.Point2d;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import org.openscience.cdk.renderer.selection.IChemObjectSelection;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue58Test extends AbstractAppletTest {

    @Test public void testIssue58() throws AWTException {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("C").target.doClick();
        applet.button("bondTool").target.doClick();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.button("select").target.doClick();
		IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
		Point2d p1 = ethane.getAtom(0).getPoint2d();
		p1 = panel.getRenderPanel().getRenderer().toScreenCoordinates(p1.x, p1.y);
    	panel.get2DHub().mouseClickedDown((int)p1.x, (int)p1.y);
        applet.panel("renderpanel").robot.waitForIdle();
    	panel.get2DHub().mouseClickedUp((int)p1.x, (int)p1.y);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        IChemObjectSelection sel = panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();
        Assert.assertEquals(true, sel.contains(ethane.getAtom(0)));
        applet.menuItem("formalCharge").requireEnabled();

        Point2d p2 = ethane.getAtom(1).getPoint2d();
		p2 = panel.getRenderPanel().getRenderer().toScreenCoordinates(p2.x, p2.y);
    	panel.get2DHub().mouseClickedDown((int)p2.x, (int)p2.y);
        applet.panel("renderpanel").robot.waitForIdle();
    	panel.get2DHub().mouseClickedUp((int)p2.x, (int)p2.y);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        sel = panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();
        Assert.assertEquals(true, sel.contains(ethane.getAtom(1)));
        applet.menuItem("formalCharge").requireEnabled();
    }

}
