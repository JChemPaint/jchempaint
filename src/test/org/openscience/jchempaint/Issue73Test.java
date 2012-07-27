package org.openscience.jchempaint;

import javax.vecmath.Point2d;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue73Test extends AbstractAppletTest {

    @Test public void testIssue73() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("C").click();
        applet.button("chain").click();
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().mouseDrag(100, 100, 300, 100);
        panel.get2DHub().mouseClickedUp(300, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
		Point2d p = getAtomPoint(panel,0,1);
        Assert.assertEquals(new Point2d(100,100), p);
    }

}
