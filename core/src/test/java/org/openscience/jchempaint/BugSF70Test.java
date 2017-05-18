package org.openscience.jchempaint;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;
import javax.vecmath.Point2d;

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
import org.openscience.cdk.io.MDLReader;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;

public class BugSF70Test extends AbstractAppletTest {

	private static int SAVE_AS_MOL_COMBOBOX_POS=6;

	@Test public void testBug70() throws CDKException, IOException{
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("hexagon").click();
        applet.click();
        Point2d point = getAtomPoint(panel,0);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.RIGHT_BUTTON,1);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.RIGHT_BUTTON,1);
        applet.menuItem("showACProperties").click();
        DialogFixture dialog = applet.dialog();
        JTextComponent textfield = dialog.robot.finder().find(JTextComponentMatcher.withName("Title"));
        textfield.setText("aaa");
        JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
        okbutton.click();
        applet.menuItem("save").click();
        dialog = applet.dialog();
        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.mol");
        if(file.exists())
            file.delete();
        JComboBox<?> combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
        combobox.setSelectedItem(combobox.getItemAt(SAVE_AS_MOL_COMBOBOX_POS));
        JTextComponentFixture text = dialog.textBox();
        text.setText(file.toString());
        JButtonFixture savebutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
        savebutton.click();
        MDLReader reader = new MDLReader(new FileInputStream(file));
        IAtomContainer mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        Assert.assertEquals("aaa",(String)mol.getProperty(CDKConstants.TITLE));
        reader.close();
	}

}
