package org.openscience.jchempaint;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;

import org.openscience.jchempaint.AbstractAppletTest;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue32Test extends AbstractAppletTest {

    @Test public void testIssue32() throws AWTException {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("C").click();
        applet.button("bondTool").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
		IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
		IAtomContainer selected = ethane.getBuilder().newInstance(IAtomContainer.class);
		selected.addAtom(ethane.getAtom(0));
		panel.getRenderPanel().getHub().deleteFragment(selected);
        
        /*
        applet.button("C").click();
        applet.button("bondTool").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.button("select").click();
		Point2d p1 = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d();
		Point2d p2 = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d();
		double d = p2.x - p1.x;
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((p1.x+p2.x)/2, p1.y+d);
        applet.panel("renderpanel").robot.moveMouse((int)moveto.x, (int)moveto.y);
        applet.panel("renderpanel").robot.pressMouse(MouseButton.LEFT_BUTTON);
        robot.delay(1000);
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(p1.x-d/2, p1.y-d);
        applet.panel("renderpanel").robot.moveMouse((int)moveto.x, (int)moveto.y);
        robot.delay(1000);
        applet.panel("renderpanel").robot.releaseMouseButtons();
*/

        int atomCount=0, hCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			hCount+=atc.getAtom(0).getImplicitHydrogenCount().intValue();
		}
		Assert.assertEquals(1, atomCount);
		Assert.assertEquals(4, hCount);
    }

}
