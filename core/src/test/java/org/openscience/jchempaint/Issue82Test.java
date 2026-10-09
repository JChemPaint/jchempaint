/*
 */
package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

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

    @Test
    public void testIssue82() {
        JPanelFixture jcppanel = applet.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        JPanelFixture renderpanel = applet.panel("renderpanel");

        applet.button("C").target.doClick();
        applet.button("bondTool").target.doClick();
        renderpanel.robot.click(renderpanel.component(), new Point(300, 100));
        renderpanel.robot.waitForIdle();

        // CH3-CH3
        Assert.assertEquals(2, getAtomCount(AbstractAppletTest.panel));
        Assert.assertEquals(1, getBondCount(AbstractAppletTest.panel));
        Assert.assertEquals(6, getHydrogenCount(AbstractAppletTest.panel));

        applet.button("chain").target.doClick();
        panel.get2DHub().updateView();
        renderpanel.robot.waitForIdle();
        panel.get2DHub().mouseClickedDown(200, 100);
        panel.get2DHub().updateView();
        renderpanel.robot.waitForIdle();
        panel.get2DHub().mouseDrag(200, 100, 300, 100, 0);
        panel.get2DHub().updateView();
        renderpanel.robot.waitForIdle();
        try {
            panel.get2DHub().mouseClickedUp(300, 100);
            panel.get2DHub().updateView();
            renderpanel.robot.waitForIdle();
        } catch (Exception e) {
            Assert.fail();
        }

        int atomCount = 0, bondCount = 0, implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }

        // JWM - note chain tool main depend on rendering settings, however
        //       for an alkane we can compute it
        int acount = getAtomCount(AbstractAppletTest.panel);
        int bcount = getBondCount(AbstractAppletTest.panel);
        int hcount = getHydrogenCount(AbstractAppletTest.panel);
        Assert.assertTrue(acount > 2);
        Assert.assertTrue(bcount > 1);
        System.err.println(panel.getSmiles());
        int expectHcount = 2 + 2 * acount;
        Assert.assertEquals(expectHcount, hcount);

        applet.button("undo").target.doClick();
        panel.get2DHub().updateView();
        renderpanel.robot.waitForIdle();
        atomCount = 0;
        bondCount = 0;
        implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }
        Assert.assertEquals(2, atomCount);
        Assert.assertEquals(1, bondCount);
        Assert.assertEquals(6, implicitHCount);

        try {
            applet.button("undo").target.doClick();
            panel.get2DHub().updateView();
            renderpanel.robot.waitForIdle();
        } catch (Exception e) {
            Assert.fail();
        }
        atomCount = 0;
        bondCount = 0;
        implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }
        Assert.assertEquals(0, atomCount);
        Assert.assertEquals(0, bondCount);
    }
}