package org.openscience.jchempaint;

import javax.vecmath.Point2d;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.action.JCPAction;

import java.awt.Point;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue40Test extends AbstractAppletTest {

    @Test public void testIssue40() {
		restoreModelToEmpty();
        JPanelFixture jcppanel=applet.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		JPanelFixture renderpanel = applet.panel("renderpanel");
        applet.button("C").target.doClick();
        applet.button("chain").target.doClick();
		robot.pressMouse(renderpanel.component(), new Point(100, 100));
		robot.moveMouse(renderpanel.component(), new Point(300, 100));
		robot.releaseMouseButtons();
        panel.get2DHub().updateView();

		renderpanel.robot.waitForIdle();
		Point2d p = getAtomPoint(panel,0,1);
        try {
	    	panel.get2DHub().mouseClickedDown((int)p.x, (int)p.y);
	    	panel.get2DHub().mouseClickedUp((int)p.x, (int)p.y);
        } catch(Exception e) {
        	Assert.fail();
        }	

		// JWM I think the number of atoms of the chain depends on the
		//     display/rendering settings!
		Assert.assertEquals(9, getAtomCount(panel));
		Assert.assertEquals(8, getBondCount(panel));

		try {
            JCPAction act = new JCPAction().getAction(panel, "org.openscience.jchempaint.action.UndoAction");
            act.actionPerformed(null);
		} catch (NullPointerException e) {
        	Assert.fail();
		}

		Assert.assertEquals(0, getAtomCount(panel));
        Assert.assertEquals(0, getBondCount(panel));
		restoreModelToEmpty();
    }

}
