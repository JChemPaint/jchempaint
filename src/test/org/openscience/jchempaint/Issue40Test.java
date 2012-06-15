package org.openscience.jchempaint;

import javax.vecmath.Point2d;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue40Test extends AbstractAppletTest {

    @Test public void testIssue40() {
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
        try {
	    	panel.get2DHub().mouseClickedDown((int)p.x, (int)p.y);
	    	panel.get2DHub().mouseClickedUp((int)p.x, (int)p.y);
        } catch(Exception e) {
        	Assert.fail();
        }	
    }

}
