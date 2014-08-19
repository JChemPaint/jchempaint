package org.openscience.jchempaint;

import java.awt.Point;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;

public class Issue4Test extends AbstractAppletTest {

    @Test public void testIssue4() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("C").click();
        applet.button("C").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.button("select").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.panel("renderpanel").robot.waitForIdle();
        try {
	        applet.button("H").click();
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("select").click();
	        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("N").click();
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("select").click();
	        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("I").click();
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("select").click();
	        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("S").click();
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("select").click();
	        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("C").click();
	        applet.panel("renderpanel").robot.waitForIdle();
        } catch(Exception e) {
        	Assert.fail();
        }
    }

}
