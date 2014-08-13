package org.openscience.jchempaint;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;
import javax.vecmath.Point2d;

import org.fest.swing.core.ComponentDragAndDrop;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLReader;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;

public class BugSF65Test extends AbstractAppletTest {

    @Test public void testBug65() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("hexagon").click();
        applet.click();
        applet.button("eraser").click();
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
