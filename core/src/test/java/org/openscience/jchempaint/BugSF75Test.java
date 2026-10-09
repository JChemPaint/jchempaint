package org.openscience.jchempaint;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;

import javax.vecmath.Point2d;
import java.awt.Point;

public class BugSF75Test extends AbstractAppletTest {

    // JWM - note the eraser behaviour changes, it is no longer a 'mode' which
    //       but an action on a selected object
    @Test
    public void testBug75() {
        restoreModelToEmpty();

        // add one hexagon
        applet.button("hexagon").click();
        applet.click();

        // add another hexagon
        JPanelFixture renderPanel = applet.panel("renderpanel");
        Robot robot = renderPanel.robot;
        robot.click(renderPanel.component(), new Point(100, 100));

        // now we will delete the atoms one by one, select > erase > (select) > erase > etc
        JButtonFixture selectButton = applet.button("select");
        JButtonFixture eraserButton = applet.button("eraser");

        selectButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();

        Assert.assertEquals("C1CCCCC1", panel.getSmiles());

        restoreModelToEmpty();
    }

}
