package org.openscience.jchempaint;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;

import org.fest.swing.applet.AppletViewer;
import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.launcher.AppletLauncher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;

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
		//using bond button
		applet.button("bond").click();
		//can we add a new bond?
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//can we convert bond order?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(IBond.Order.DOUBLE, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getOrder());
		//using up bond button,
		applet.button("up_bond").click();
		//can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(CDKConstants.STEREO_BOND_UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
		//can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using down bond button,
		applet.button("down_bond").click();
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(CDKConstants.STEREO_BOND_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        //note it must be STEREO_BOND_DOWN_INV, since it was up before
        Assert.assertEquals(CDKConstants.STEREO_BOND_DOWN_INV, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using undefined bond button,
		applet.button("undefined_bond").click();
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+4, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(CDKConstants.STEREO_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using undefined stereo button,		
		applet.button("undefined_stereo_bond").click();
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(CDKConstants.EZ_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.EZ_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
	}
	
	@Test public void testElement() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    //we go into bond_down mode to see that hitting O button actually changes activeDrawModule
	    applet.button("down_bond").click();
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		applet.button("O").click();
        Assert.assertEquals("O",panel.get2DHub().getActiveDrawModule().getID());
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals("O",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		applet.button("bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("O",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
		Assert.assertEquals("O",panel.get2DHub().getController2DModel().getDrawElement());
	}
	
	@Test public void testPeriodictable() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		applet.button("periodictable").click();
		DialogFixture dialog = applet.dialog();
		dialog.button("Li").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals("Li",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		applet.button("bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("Li",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
	}

	@Test public void testEnterelement() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		applet.button("enterelement").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		DialogFixture dialog = applet.dialog();
		dialog.textBox().setText("U");
		dialog.button("ok").click();
		Assert.assertEquals("U",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		applet.button("bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("U",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
	}

	@Test public void testTriangle() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a triangle to an atom
		applet.button("triangle").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModel();
	}

	@Test public void testPentagon() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a pentagon to an atom
		applet.button("pentagon").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+4, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+7, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModel();
	}

	@Test public void testHexagon() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a hexagon to an atom
		applet.button("hexagon").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+9, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModel();
	}

	@Test public void testOctagon() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a octagon to an atom
		applet.button("octagon").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+7, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+13, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModel();
	}

	@Test public void testBenzene() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a benzene to an atom
		applet.button("benzene").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+9, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModel();
	}

	@Test public void testSquare() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a square to an atom
		applet.button("square").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModel();
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
