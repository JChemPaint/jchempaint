/*
 */
package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;

import java.awt.Point;

/**
 * 1. place a C-C bond; 2. click draw chain; 3. start dragging from C and
 * notice one H too much in status bar; notice also the
 * explicit C of the merged methyl stays explicit, unexpectedly, see #103);
 * 4. clean mol will bomb with NullPointerException
 *
 * @author <ralf@ark.in-berlin.de>
 */
public class Issue82Test extends AbstractAppletTest {

    // this test dragging a chain to an existing atom merges the chain on
    @Test
    public void testIssue82() throws InterruptedException {
        restoreModelToEmpty();
        JPanelFixture jcppanel = applet.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        JPanelFixture renderpanel = applet.panel("renderpanel");

        // we need the chain tool to line up, to do that we need to
        // draw a very particular shape by clicking and dragging the bonds
        // to snap to the correct location
        applet.button("hexagon").click();
        renderpanel.robot.click(renderpanel.component(), new Point(300, 100));
        renderpanel.robot.waitForIdle();
        applet.button("bondTool").target.doClick();
        // we want the atom at the bottom which is currently idx=3 but may change in future
        renderpanel.robot.click(renderpanel.component(), toAwtPoint(getAtomPoint(panel, 3)));
        renderpanel.robot.waitForIdle();
        Point p6 = toAwtPoint(getAtomPoint(panel, 6));
        renderpanel.robot.pressMouse(renderpanel.component(), p6);
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(p6.x - 70, p6.y + 40));
        renderpanel.robot.releaseMouseButtons();
        renderpanel.robot.waitForIdle();
        Point p7 = toAwtPoint(getAtomPoint(panel, 7));
        renderpanel.robot.pressMouse(renderpanel.component(), p7);
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(p7.x - 70, p7.y - 40));
        renderpanel.robot.releaseMouseButtons();
        renderpanel.robot.waitForIdle();

        applet.button("chain").target.doClick();
        Point startPoint = toAwtPoint(getAtomPoint(panel, 8));
        Point endPoint = toAwtPoint(getAtomPoint(panel, 4));
        renderpanel.robot.pressMouse(renderpanel.component(), startPoint);
        renderpanel.robot.waitForIdle();
        // drag up first
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(startPoint.x, startPoint.y - 50));
        // then across slightly overshooting
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(endPoint.x + 2, endPoint.y));
        renderpanel.robot.releaseMouseButtons();
        renderpanel.robot.waitForIdle();

        Assert.assertEquals("C1CCC2C(C1)CCCC2", panel.getSmiles());
    }
}