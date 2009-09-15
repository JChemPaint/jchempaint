package org.openscience.jchempaint;

import java.awt.Frame;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import org.fest.swing.applet.AppletViewer;
import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.launcher.AppletLauncher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;

public class JCPEditorAppletBugsTest {
	private static AppletViewer viewer;
	private static FrameFixture applet;
	private static JChemPaintEditorApplet jcpApplet;


	@BeforeClass public static void setUp() {
	    jcpApplet = new JChemPaintEditorApplet();
		Map<String, String> parameters = new HashMap<String, String>();
		viewer = AppletLauncher.applet(jcpApplet)
			.withParameters(parameters)
			.start();
		applet = new FrameFixture(viewer);
		applet.show();
	}

	@Test public void testSquareSelectSingleAtom()  {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		applet.button("C").click();
		Point movetopint=new Point(100,100);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), movetopint, MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		applet.button("select").click();
		movetopint=new Point(110,110);	
		applet.panel("renderpanel").robot.moveMouse(applet.panel("renderpanel").component(),movetopint);
		applet.panel("renderpanel").robot.pressMouse(MouseButton.LEFT_BUTTON);
		movetopint=new Point(90,90);	
		applet.panel("renderpanel").robot.moveMouse(applet.panel("renderpanel").component(),movetopint);
		applet.panel("renderpanel").robot.releaseMouse(MouseButton.LEFT_BUTTON);
		Assert.assertEquals(1, panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().getAtomCount());
		restoreModel();
	}
	
	@Test public void testGetMolFile() throws CDKException{
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("hexagon").click();
        Point movetopint=new Point(100,100);    
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), movetopint, MouseButton.LEFT_BUTTON,1);
	    Assert.assertTrue(jcpApplet.getMolFile().indexOf("6  6  0  0  0  0  0  0  0  0999 V2000")>0);
        restoreModel();	    
	}
	
	
	private void restoreModel(){
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		IChemModel basic = DefaultChemObjectBuilder.getInstance().newChemModel();
		basic.setMoleculeSet(basic.getBuilder().newMoleculeSet());
		basic.getMoleculeSet().addAtomContainer(
				basic.getBuilder().newMolecule());
		panel.setChemModel(basic);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setZoomFactor(1);
		panel.get2DHub().updateView();
	}
	

	@AfterClass public static void tearDown() {
	  viewer.unloadApplet();
	  applet.cleanUp();
	}
}
