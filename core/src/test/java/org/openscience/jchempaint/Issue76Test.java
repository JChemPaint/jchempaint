/*
 */
package org.openscience.jchempaint;

import javax.vecmath.Point2d;

import org.apache.logging.log4j.core.Core;
import org.fest.swing.fixture.JPanelFixture;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.awt.Point;

/**
 * #76: synopsis: place a benzene; draw chain from a benzene C-atom
 * should not change this atom's position; first undo should give unaltered
 * benzene; second back to zero, without NullPointerException
 * #154: merging with chain garbles implicit Hs
 * #158: with chain, single click on atom creates undo slot, should do nothing
 *
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue76Test extends AbstractAppletTest {

    @Test
    public void testIssue76() {
        restoreModelToEmpty();
        applet.button("benzene").target.doClick();
        JPanelFixture renderPanel = applet.panel("renderpanel");
        robot.click(renderPanel.component(), new Point(100, 100));
        panel.get2DHub().updateView();
        renderPanel.robot.waitForIdle();

        Assert.assertEquals(6, getAtomCount(panel));
        Assert.assertEquals(6, getBondCount(panel));

        Point2d atompos = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d();
        atompos = panel.getRenderPanel().getRenderer().toScreenCoordinates(atompos.x, atompos.y);
        applet.button("C").target.doClick();
        applet.button("chain").target.doClick();
        robot.click(renderPanel.component(), toAwtPoint(atompos));
        renderPanel.robot.waitForIdle();
        robot.click(renderPanel.component(), toAwtPoint(atompos));
        renderPanel.robot.waitForIdle();
        robot.click(renderPanel.component(), toAwtPoint(atompos));
        renderPanel.robot.waitForIdle();
        int x = (int) atompos.x;
        int y = (int) atompos.y;
        panel.get2DHub().mouseDrag(x, y, x + 200, y, 0);
        panel.get2DHub().updateView();
        renderPanel.robot.waitForIdle();
        panel.get2DHub().mouseClickedUp(x + 200, y);
        panel.get2DHub().updateView();
        renderPanel.robot.waitForIdle();

        Assert.assertTrue(getAtomCount(panel) > 6);
        Assert.assertTrue(getBondCount(panel) > 6);

        applet.button("undo").click();
        panel.get2DHub().updateView();

        Assert.assertEquals(6, getAtomCount(panel));
        Assert.assertEquals(6, getBondCount(panel));

        try {
            applet.button("undo").click();
            panel.get2DHub().updateView();
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(0, getAtomCount(panel));
        Assert.assertEquals(0, getBondCount(panel));
        restoreModelToEmpty();
    }


}
