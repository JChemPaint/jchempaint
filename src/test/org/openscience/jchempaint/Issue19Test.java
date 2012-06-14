package org.openscience.jchempaint;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue19Test extends AbstractAppletTest {

    @Test public void test() throws AWTException {
    	Robot robot = new Robot();
    	JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("C").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.button("select").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.panel("renderpanel").robot.moveMouse(100,100);
        try {
            robot.keyPress(KeyEvent.VK_DELETE);
            robot.delay(500);
            robot.keyRelease(KeyEvent.VK_DELETE);
            robot.delay(500);
            robot.keyPress(KeyEvent.VK_DELETE);
            robot.delay(500);
            robot.keyRelease(KeyEvent.VK_DELETE);
            robot.delay(500);
		} catch (Exception e) {
			Assert.fail();
		}
        
    }

}
