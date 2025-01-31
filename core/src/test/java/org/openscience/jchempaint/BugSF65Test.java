package org.openscience.jchempaint;

import java.awt.Point;

import javax.vecmath.Point2d;

import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

public class BugSF65Test extends AbstractAppletTest {

    @Test public void testBug65() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("hexagon").target.doClick();
        applet.click();
        applet.button("eraser").target.doClick();
        Point2d point = getBondPoint(panel,0);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.LEFT_BUTTON,1);
        point = getBondPoint(panel,2);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.LEFT_BUTTON,1);

        int atomCount=0, bondCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(6, atomCount);
        Assert.assertEquals(4, bondCount);
        restoreModelToEmpty();
    }

}
