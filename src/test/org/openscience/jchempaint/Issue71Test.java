package org.openscience.jchempaint;

import java.awt.AWTException;
import java.awt.Point;
import javax.vecmath.Point2d;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import org.openscience.jchempaint.AbstractAppletTest;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue71Test extends AbstractAppletTest {

    @Test public void testIssue71() throws AWTException {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("C").click();
        applet.button("bondTool").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.button("select").click();
		IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
		Point2d p1 = ethane.getAtom(0).getPoint2d();
		Renderer r = panel.getRenderPanel().getRenderer();
		p1 = r.toScreenCoordinates(p1.x, p1.y);
    	panel.get2DHub().mouseClickedDown((int)(p1.x), (int)(p1.y));
        applet.panel("renderpanel").robot.waitForIdle();
    	panel.get2DHub().mouseClickedUp((int)p1.x, (int)p1.y);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        IChemObjectSelection sel = r.getRenderer2DModel().getSelection();
        Assert.assertEquals(true, sel.contains(ethane.getAtom(0)));

		double d = r.getRenderer2DModel().getSelectionRadius() / r.getRenderer2DModel().getScale();
    	panel.get2DHub().mouseClickedDown((int)(p1.x+d/2), (int)(p1.y+d/2));
        applet.panel("renderpanel").robot.waitForIdle();
    	panel.get2DHub().mouseDrag((int)(p1.x+d/2), (int)(p1.y+d/2),(int)p1.x+100, (int)p1.y+100);
    	panel.get2DHub().mouseClickedUp((int)p1.x+100, (int)p1.y+100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        sel = panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();
        Point2d p2 = ethane.getAtom(0).getPoint2d();
        p2 = r.toScreenCoordinates(p2.x, p2.y);
        Assert.assertEquals((int)(p1.x+100), (int)p2.x);
    }

}
