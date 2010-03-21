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
import org.openscience.cdk.io.MDLReader;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;

public class JCPEditorAppletBugsTest extends AbstractAppletTest {

	@Test
	public void testSquareSelectSingleAtom() {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		applet.button("C").click();
		Point movetopint = new Point(100, 100);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), movetopint, MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(1, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtomCount());
		applet.button("select").click();
		movetopint = new Point(80, 80);
		applet.panel("renderpanel").robot.moveMouse(applet.panel("renderpanel")
				.component(), movetopint);
		applet.panel("renderpanel").robot.pressMouse(MouseButton.LEFT_BUTTON);
		movetopint = new Point(85, 85); // little fix by MR, do small initial
										// drag first
		applet.panel("renderpanel").robot.moveMouse(applet.panel("renderpanel")
				.component(), movetopint);
		movetopint = new Point(120, 120);
		applet.panel("renderpanel").robot.moveMouse(applet.panel("renderpanel")
				.component(), movetopint);
		applet.panel("renderpanel").robot.releaseMouse(MouseButton.LEFT_BUTTON);
		Assert.assertEquals(1, panel.getRenderPanel().getRenderer()
				.getRenderer2DModel().getSelection()
				.getConnectedAtomContainer().getAtomCount());
		restoreModelToEmpty();
	}

	@Test
	public void testMove() throws InterruptedException {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		// we draw a hexagon
		applet.button("hexagon").click();
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point(100, 100), MouseButton.LEFT_BUTTON, 1);
		// select this
		applet.button("select").click();
		Point movetopint = new Point(50, 50);
		ComponentDragAndDrop dandd = new ComponentDragAndDrop(applet
				.panel("renderpanel").robot);
		dandd.drag(applet.panel("renderpanel").component(), movetopint);
		movetopint = new Point(300, 300);
		dandd.drop(applet.panel("renderpanel").component(), movetopint);
		Point2d oldcoord = new Point2d(panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtom(0).getPoint2d().x, panel
				.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)
				.getPoint2d().y);
		// switch to move mode should happen automatically
		applet.panel("renderpanel").robot.moveMouse(
				applet.panel("renderpanel").target, new Point(100, 100));
		applet.panel("renderpanel").robot.pressMouse(MouseButton.LEFT_BUTTON);
		applet.panel("renderpanel").robot.moveMouse(
				applet.panel("renderpanel").target, new Point(150, 150));
		applet.panel("renderpanel").robot.releaseMouseButtons();
		Assert.assertFalse(oldcoord.equals(panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d()));
	}

	@Test
	public void testSetSmiles() throws CDKException {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		jcpApplet.setSmiles("CCCC");
		panel.get2DHub().updateView();
		Assert.assertEquals(4, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtomCount());
		restoreModelToEmpty();
	}

	@Test
	public void testSetMolFile() throws CDKException {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		jcpApplet
				.setMolFile("\n  CDK    1/19/07,10:3\n\n  2  1  0  0  0  0  0  0  0  0999 V2000 \n  2.520000 10.220000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n  2.270000 10.470000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n  2  1  1  0  0  0  0 \nM  END");
		Assert.assertEquals(2, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtomCount());
		restoreModelToEmpty();
	}

	@Test
	public void testGetMolFile() throws CDKException {
		applet.button("hexagon").click();
		Point movetopint = new Point(100, 100);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), movetopint, MouseButton.LEFT_BUTTON, 1);
		Assert.assertTrue(jcpApplet.getMolFile().indexOf(
				"6  6  0  0  0  0  0  0  0  0999 V2000") > 0);
		restoreModelToEmpty();
	}

	@Test
	public void testBug2858663() {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		applet.button("bondTool").click();
		applet.click();
		Assert.assertEquals(2, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtomCount());
		Assert.assertEquals(1, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getBondCount());
		restoreModelToEmpty();
	}

	// @cdk.bug 2859344 /6
	@Test
	public void overwriteStereo() {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		// we draw a hexagon
		applet.button("hexagon").click();
		applet.click();
		// one of its bonds becomes an up bond
		applet.button("up_bond").click();
		Point2d moveto = panel
				.getRenderPanel()
				.getRenderer()
				.toScreenCoordinates(
						(panel.getChemModel().getMoleculeSet()
								.getAtomContainer(0).getAtom(2).getPoint2d().x + panel
								.getChemModel().getMoleculeSet()
								.getAtomContainer(0).getAtom(3).getPoint2d().x) / 2,
						(panel.getChemModel().getMoleculeSet()
								.getAtomContainer(0).getAtom(2).getPoint2d().y + panel
								.getChemModel().getMoleculeSet()
								.getAtomContainer(0).getAtom(3).getPoint2d().y) / 2);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
		// if we make that down, it must be down (and not donw_inv)
		applet.button("down_bond").click();
		moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(
				(panel.getChemModel().getMoleculeSet().getAtomContainer(0)
						.getAtom(2).getPoint2d().x + panel.getChemModel()
						.getMoleculeSet().getAtomContainer(0).getAtom(3)
						.getPoint2d().x) / 2,
				(panel.getChemModel().getMoleculeSet().getAtomContainer(0)
						.getAtom(2).getPoint2d().y + panel.getChemModel()
						.getMoleculeSet().getAtomContainer(0).getAtom(3)
						.getPoint2d().y) / 2);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
		// and up again
		applet.button("up_bond").click();
		moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(
				(panel.getChemModel().getMoleculeSet().getAtomContainer(0)
						.getAtom(2).getPoint2d().x + panel.getChemModel()
						.getMoleculeSet().getAtomContainer(0).getAtom(3)
						.getPoint2d().x) / 2,
				(panel.getChemModel().getMoleculeSet().getAtomContainer(0)
						.getAtom(2).getPoint2d().y + panel.getChemModel()
						.getMoleculeSet().getAtomContainer(0).getAtom(3)
						.getPoint2d().y) / 2);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
		restoreModelToEmpty();
	}

	@Test
	public void testUpBond() {
		genericStereoBondTest(IBond.Stereo.UP);
	}

	@Test
	public void testDownBond() {
		genericStereoBondTest(IBond.Stereo.DOWN);
	}

	@Test
	public void testUndefinedBond() {
		genericStereoBondTest(IBond.Stereo.UP_OR_DOWN);
	}

	// @cdk.bug 2859344 /7
	@Test
	public void testUndefinedEzBond() {
		genericStereoBondTest(IBond.Stereo.E_OR_Z);
	}

	@Test
	public void testNoneBond() {
		genericStereoBondTest(IBond.Stereo.NONE);
	}

	// @cdk.bug 2860015
	@Test
	public void testBug2860015() {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		applet.button("bondTool").click();
		applet.click();
		applet.click();
		applet.click();
		applet.click();
		Point2d moveto = panel.getRenderPanel().getRenderer()
				.toScreenCoordinates(
						panel.getChemModel().getMoleculeSet().getAtomContainer(
								0).getAtom(3).getPoint2d().x,
						panel.getChemModel().getMoleculeSet().getAtomContainer(
								0).getAtom(3).getPoint2d().y);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(6, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtomCount());
		Assert.assertEquals(5, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getBondCount());
		moveto = getBondPoint(panel, 0);
		panel.getRenderPanel().getRenderer().getRenderer2DModel()
				.setHighlightedAtom(null);
		applet.moveTo(new Point(100, 100));
		applet.button("eraser").click();
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(5, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtomCount());
		Assert.assertEquals(4, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getBondCount());
		moveto = getBondPoint(panel, 1);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);

		Assert.assertEquals(2, panel.getChemModel().getMoleculeSet()
				.getAtomContainerCount());
		Assert.assertEquals(3, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtomCount());
		Assert.assertEquals(2, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getBondCount());
		Assert.assertEquals(2, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(1).getAtomCount());
		Assert.assertEquals(1, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(1).getBondCount());
		restoreModelToEmpty();
	}

	// This is a test for overwriting of stereo bonds. Any stereo bond
	// must overwrite all others and flip itself.
	private void genericStereoBondTest(IBond.Stereo directionToTest) {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		// we draw a hexagon
		applet.button("hexagon").click();
		applet.click();
		// we make all bond types in there
		applet.button("up_bond").click();
		Point2d moveto = getBondPoint(panel, 1);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(1).getStereo());
		applet.button("down_bond").click();
		moveto = getBondPoint(panel, 2);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
		applet.button("undefined_bond").click();
		moveto = getBondPoint(panel, 3);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(3).getStereo());
		applet.button("undefined_stereo_bond").click();
		moveto = getBondPoint(panel, 4);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
		// now we click on all of them with disired bond
		if (directionToTest == IBond.Stereo.UP)
			applet.button("up_bond").click();
		if (directionToTest == IBond.Stereo.DOWN)
			applet.button("down_bond").click();
		if (directionToTest == IBond.Stereo.UP_OR_DOWN)
			applet.button("undefined_bond").click();
		if (directionToTest == IBond.Stereo.E_OR_Z)
			applet.button("undefined_stereo_bond").click();
		if (directionToTest == IBond.Stereo.NONE)
			applet.button("bondTool").click();
		for (int i = 0; i < 5; i++) {
			boolean self = false;
			if (panel.getChemModel().getMoleculeSet().getAtomContainer(0)
					.getBond(i).getStereo() == directionToTest)
				self = true;
			moveto = getBondPoint(panel, i);
			applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
					.component(), new Point((int) moveto.x, (int) moveto.y),
					MouseButton.LEFT_BUTTON, 1);
			if (self) {
				IBond.Stereo desiredDirection = null;
				if (directionToTest == IBond.Stereo.E_OR_Z)
					desiredDirection = IBond.Stereo.E_OR_Z;
				else if (directionToTest == IBond.Stereo.NONE)
					desiredDirection = IBond.Stereo.NONE;
				else if (directionToTest == IBond.Stereo.DOWN)
					desiredDirection = IBond.Stereo.DOWN_INVERTED;
				else if (directionToTest == IBond.Stereo.UP)
					desiredDirection = IBond.Stereo.UP_INVERTED;
				else if (directionToTest == IBond.Stereo.UP_OR_DOWN)
					desiredDirection = IBond.Stereo.UP_OR_DOWN_INVERTED;
				Assert.assertEquals(desiredDirection, panel.getChemModel()
						.getMoleculeSet().getAtomContainer(0).getBond(i)
						.getStereo());
			} else {
				Assert.assertEquals(directionToTest, panel.getChemModel()
						.getMoleculeSet().getAtomContainer(0).getBond(i)
						.getStereo());
			}
		}
		restoreModelToEmpty();
	}

	@Test
	public void testFlipWithStereo() {
		JPanelFixture jcppanel = applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		applet.button("hexagon").click();
		applet.click();
		applet.button("up_bond").click();
		Point2d moveto = getAtomPoint(panel, 0);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		moveto = getAtomPoint(panel, 1);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		applet.button("down_bond").click();
		moveto = getAtomPoint(panel, 2);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel")
				.component(), new Point((int) moveto.x, (int) moveto.y),
				MouseButton.LEFT_BUTTON, 1);
		applet.button("flipHorizontal").click();
		Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(6).getStereo());
		Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(7).getStereo());
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(8).getStereo());
		applet.button("flipVertical").click();
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(6).getStereo());
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(7).getStereo());
		Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
				.getMoleculeSet().getAtomContainer(0).getBond(8).getStereo());
		restoreModelToEmpty();
	}

	//Test for Trac ticket # 80 : deletion of the double bonds that bind the oxygens in caffeine led to               
	//                            incorrect cascading removal of the bound carbons too.
	@Test
	public void testRemoveBond() throws CDKException, ClassNotFoundException,
			IOException, CloneNotSupportedException {
		// Clean the panel:
    	applet.menuItem("new").click();
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;

		// Paste caffeine template:
        applet.menuItem("pasteTemplate").click();
		DialogFixture dialog = applet.dialog("templates");
		JButtonFixture templateButton = new JButtonFixture(dialog.robot,
				dialog.robot.finder().find(
						new ButtonTextComponentMatcher("Caffeine")));
		templateButton.click();
		applet.button("select").click();
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), 
				new Point(0,0), MouseButton.LEFT_BUTTON,1);

        //Delete the double bonds connecting the two oxygen atoms:
		applet.button("eraser").click();
		for (IBond bond : panel.getChemModel().getMoleculeSet().getAtomContainer(0).bonds()) 
			if (bond.getOrder().equals(IBond.Order.DOUBLE) && 
					(bond.getAtom(0).getSymbol().equals("O")||bond.getAtom(1).getSymbol().equals("O"))) {
				double xAvg= (bond.getAtom(0).getPoint2d().x +bond.getAtom(1).getPoint2d().x)/2; 
				double yAvg= (bond.getAtom(0).getPoint2d().y +bond.getAtom(1).getPoint2d().y)/2; 
				Point2d moveTo=panel.getRenderPanel().getRenderer().toScreenCoordinates(xAvg,yAvg);
				Point p = new Point((int)moveTo.x, (int)moveTo.y);
				applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), 
						p, MouseButton.LEFT_BUTTON,1);
			}
        
        //Establish that only the double bonds plus the oxygens are gone, rest is still in place:
		Assert.assertEquals(12, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount() );

	}
	
	@Test public void testBug70() throws FileNotFoundException, CDKException{
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("hexagon").click();
        applet.click();
        Point2d point = getAtomPoint(panel,0);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.RIGHT_BUTTON,1);
        applet.menuItem("showACProperties2").click();
        DialogFixture dialog = applet.dialog();
        JTextComponent textfield = dialog.robot.finder().find(JTextComponentMatcher.withName("Title"));
        textfield.setText("aaa");
        JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
        okbutton.click();
        applet.menuItem("save").click();
        dialog = applet.dialog();
        JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
        combobox.setSelectedItem(combobox.getItemAt(5));
        JTextComponentFixture text = dialog.textBox();
        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.mol");
        if(file.exists())
            file.delete();
        text.setText(file.toString());
        JButtonFixture savebutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
        savebutton.click();
        MDLReader reader = new MDLReader(new FileInputStream(file));
        IAtomContainer mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
        Assert.assertEquals("aaa",(String)mol.getProperty(CDKConstants.TITLE));
        restoreModelToEmpty();
	}

	@Test public void testBug77() throws FileNotFoundException, CDKException{
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("hexagon").click();
        applet.click();
        applet.menuItem("saveAs").click();
        DialogFixture dialog = applet.dialog();
        JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
        combobox.setSelectedItem(combobox.getItemAt(5));
        JTextComponentFixture text = dialog.textBox();
        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test1.mol");
        if(file.exists())
            file.delete();
        text.setText(file.toString());
        JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
        okbutton.click();
        //not the bug, but still worth testing
        MDLReader reader = new MDLReader(new FileInputStream(file));
        IAtomContainer mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getMolecule(0).getAtomCount(), mol.getAtomCount());
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getMolecule(0).getBondCount(), mol.getBondCount());
        applet.menuItem("new").click();
        applet.button("hexagon").click();
        applet.click();
        applet.button("bond").click();
        Point2d moveto=getAtomPoint(panel,0);    
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x,(int)moveto.y), MouseButton.LEFT_BUTTON,1);
        applet.menuItem("saveAs").click();
        dialog = applet.dialog();
        combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
        combobox.setSelectedItem(combobox.getItemAt(5));
        text = dialog.textBox();
        file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test2.mol");
        if(file.exists())
            file.delete();
        text.setText(file.toString());
        okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
        okbutton.click();
        //not the bug, but still worth testing
        reader = new MDLReader(new FileInputStream(file));
        mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getMolecule(0).getAtomCount(), mol.getAtomCount());
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getMolecule(0).getBondCount(), mol.getBondCount());
        //ok, now the critical bits - open mol1
        file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test1.mol");
        applet.menuItem("open").click();
        dialog = applet.dialog();
        text = dialog.textBox();
        text.setText(file.toString());
        okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
        okbutton.click();
        //"save as" mol1
        file.delete();
        applet.menuItem("saveAs").click();
        dialog = applet.dialog();
        combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
        combobox.setSelectedItem(combobox.getItemAt(5));
        text = dialog.textBox();
        text.setText(file.toString());
        okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
        okbutton.click();
        //open mol2
        file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test2.mol");
        applet.menuItem("open").click();
        dialog = applet.dialog();
        text = dialog.textBox();
        text.setText(file.toString());
        okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
        okbutton.click();
        //save should write to mol2, ie mol1=6 atoms, mol2=7atoms
        applet.menuItem("save").click();
        file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test1.mol");
        reader = new MDLReader(new FileInputStream(file));
        mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
        Assert.assertEquals(6, mol.getAtomCount());
        file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test2.mol");
        reader = new MDLReader(new FileInputStream(file));
        mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
        Assert.assertEquals(7, mol.getAtomCount());
        restoreModelToEmpty();
	}

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
        Assert.assertEquals(6, panel.getChemModel().getMoleculeSet().getMolecule(0).getAtomCount());
        Assert.assertEquals(4, panel.getChemModel().getMoleculeSet().getMolecule(0).getBondCount());
        restoreModelToEmpty();
    }

    @Test public void testBug75() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException{
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("hexagon").click();
        applet.click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point(100,100), MouseButton.LEFT_BUTTON,1);
        applet.button("eraser").click();
        Point2d moveto=getAtomPoint(panel,0,1);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        moveto=getAtomPoint(panel,0,1);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        moveto=getAtomPoint(panel,0,1);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        moveto=getAtomPoint(panel,0,1);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        moveto=getAtomPoint(panel,0,1);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        moveto=getAtomPoint(panel,0,1);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals("C1CCCCC1", panel.getSmiles());
        restoreModelToEmpty();
    }
}
