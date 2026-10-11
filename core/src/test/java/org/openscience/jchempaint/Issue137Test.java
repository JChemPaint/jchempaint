package org.openscience.jchempaint;

import javax.vecmath.Point2d;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.action.JCPAction;
import org.openscience.jchempaint.renderer.Renderer;

import java.awt.Point;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 * <p>
 * #137: move C in ethane over the other gives CH3
 * #153: merging ethane internally does not delete bond from model
 */
public class Issue137Test extends AbstractAppletTest {

    @Test
    public void testIssue137() {
        restoreModelToEmpty();
        JPanelFixture jcppanel = applet.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;

        JPanelFixture renderpanel = applet.panel("renderpanel");
        Robot robot = renderpanel.robot;

        robot.click(renderpanel.component(), new Point(100, 100));
        panel.get2DHub().updateView();
        robot.waitForIdle();

        applet.button("select").click();
        robot.waitForIdle();
        Point2d atom0p = getAtomPoint(panel, 0);
        Point2d atom1p = getAtomPoint(panel, 1);

        // select the atom
        robot.click(renderpanel.component(), toAwtPoint(atom0p));

        panel.get2DHub().setAltInputMode(true); // alt-mode needed (free move)
        robot.pressMouse(renderpanel.component(), toAwtPoint(atom0p));
        robot.moveMouse(renderpanel.component(), toAwtPoint(atom1p));
        robot.releaseMouseButtons();
        panel.get2DHub().setAltInputMode(false);
        panel.get2DHub().updateView();
        robot.waitForIdle();
        robot.waitForIdle();

        int atomCount = 0, bondCount = 0, implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }
        Assert.assertEquals(1, atomCount);
        Assert.assertEquals(0, bondCount);
        Assert.assertEquals(4, implicitHCount);
        restoreModelToEmpty();
    }
}
