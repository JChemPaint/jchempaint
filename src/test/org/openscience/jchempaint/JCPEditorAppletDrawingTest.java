package org.openscience.jchempaint;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.vecmath.Point2d;

import org.fest.swing.applet.AppletViewer;
import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.launcher.AppletLauncher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.renderer.selection.SingleSelection;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;

public class JCPEditorAppletDrawingTest {
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

	@Test public void testAddBond() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		applet.button("bond").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		applet.button("up_bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(CDKConstants.STEREO_BOND_UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
		applet.button("down_bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(CDKConstants.STEREO_BOND_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
		applet.button("undefined_bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+4, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(CDKConstants.STEREO_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
		applet.button("undefined_stereo_bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(CDKConstants.EZ_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
	}


	private void restoreModel(){
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		String filename = "data/basic.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IChemModel basic;
		try {
			basic = (IChemModel) reader.read(DefaultChemObjectBuilder.getInstance().newChemModel());
			panel.setChemModel(basic);
			panel.getRenderPanel().getRenderer().getRenderer2DModel().setZoomFactor(1);
			panel.get2DHub().updateView();
		} catch (CDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@AfterClass public static void tearDown() {
	  viewer.unloadApplet();
	  applet.cleanUp();
	}
}
