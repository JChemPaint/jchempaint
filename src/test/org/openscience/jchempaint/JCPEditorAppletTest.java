package org.openscience.jchempaint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.fest.swing.applet.AppletViewer;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.launcher.AppletLauncher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;

public class JCPEditorAppletTest {
	private static AppletViewer viewer;
	private static FrameFixture applet;
	private static IChemModel originalModel;

	
    @BeforeClass public static void setUp() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("smiles", "CCCCCCCC");
		viewer = AppletLauncher.applet(new JChemPaintEditorApplet())
			.withParameters(parameters)
			.start();
		applet = new FrameFixture(viewer);
		applet.show();
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		originalModel=panel.getChemModel();
	}

	@Test public void testReportSmiles() {
	  applet.menuItem("createSMILES").click();
	  String text = applet.dialog("smilestextdialog").textBox("textviewdialogtextarea").text();
	  Assert.assertTrue(text.indexOf("CCCCCCCC")>-1);
	  Assert.assertTrue(text.indexOf("[H]C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])[H]")>-1);
	  applet.dialog("smilestextdialog").close();
	}
	
	@Test public void testMenuNew() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	  applet.menuItem("new").click();
	  JPanelFixture jcppanel=applet.panel("appletframe");
	  JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
	  Assert.assertEquals("",panel.getSmiles());
	  restoreModel();
	}
	
	private void restoreModel(){
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.setChemModel(originalModel);
	}
	

	@AfterClass public static void tearDown() {
	  viewer.unloadApplet();
	  applet.cleanUp();
	}
}
