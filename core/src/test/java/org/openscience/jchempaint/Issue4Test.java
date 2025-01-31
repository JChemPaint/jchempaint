package org.openscience.jchempaint;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

public class Issue4Test extends AbstractAppletTest {

    @Test public void testIssue4() {
        applet.button("C").target.doClick();
        applet.button("C").target.doClick();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.button("select").target.doClick();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.panel("renderpanel").robot.waitForIdle();
        try {
	        applet.button("H").target.doClick();
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("select").target.doClick();
	        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("N").target.doClick();
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("select").target.doClick();
	        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("I").target.doClick();
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("select").target.doClick();
	        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("S").target.doClick();
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("select").target.doClick();
	        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
	        applet.panel("renderpanel").robot.waitForIdle();
	        applet.button("C").target.doClick();
	        applet.panel("renderpanel").robot.waitForIdle();
        } catch(Exception e) {
        	Assert.fail();
        }
    }

}
