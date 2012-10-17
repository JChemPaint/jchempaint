/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.jchempaint.action.JCPAction;

/**
 * Draw chain, replace middle atom with H, clean up.
 * Should not throw anything.
 * @author <ralf@ark.in-berlin.de>
 */
public class Issue8Test extends AbstractAppletTest {

	@Test public void testIssue8() {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel pane = (JChemPaintPanel)jcppanel.target;
		applet.button("chain").click();
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		pane.get2DHub().mouseClickedDown(100, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		pane.get2DHub().mouseDrag(100, 100, 300, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		pane.get2DHub().mouseClickedUp(300, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		
		applet.button("H").click();
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		pane.get2DHub().mouseClickedDown(180, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		pane.get2DHub().mouseClickedUp(180, 100);
		pane.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		
        try {
            // For some reason this does not work
            // applet.button("cleanup").click();
            // panel.get2DHub().updateView();
            // so we crank the lever manually
            JCPAction act = new JCPAction().getAction(panel, "org.openscience.jchempaint.action.CleanupAction");
            act.actionPerformed(null);
		} catch (Exception e) {
			Assert.fail();
		}
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
	}
}