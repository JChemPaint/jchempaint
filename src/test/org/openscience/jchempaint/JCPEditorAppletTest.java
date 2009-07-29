package org.openscience.jchempaint;

import java.util.HashMap;
import java.util.Map;

import org.fest.swing.applet.AppletViewer;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.launcher.AppletLauncher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;

public class JCPEditorAppletTest {
	private static AppletViewer viewer;
	private static FrameFixture applet;

	
    @BeforeClass public static void setUp() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("smiles", "CCCCCCCC");
		viewer = AppletLauncher.applet(new JChemPaintEditorApplet())
			.withParameters(parameters)
			.start();
		applet = new FrameFixture(viewer);
		applet.show();
	}

	@Test public void testReportSmiles() {
	  applet.menuItem("createSMILES").click();
	  String text = applet.dialog("smilestextdialog").textBox("textviewdialogtextarea").text();
	  Assert.assertTrue(text.indexOf("CCCCCCCC")>-1);
	  Assert.assertTrue(text.indexOf("[H]C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])[H]")>-1);
	}

	@AfterClass public static void tearDown() {
	  viewer.unloadApplet();
	  applet.cleanUp();
	}
}
