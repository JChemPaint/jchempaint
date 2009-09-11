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
import org.fest.swing.exception.WaitTimedOutError;
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
import org.openscience.cdk.config.IsotopeFactory;
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

public class JCPEditorAppletMenuTest {
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

	@Test public void testMenuExportBmp() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  File file=new File("/tmp/test.bmp");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(1));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText("/tmp/test");
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}

	@Test public void testMenuExportJpg() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  File file=new File("/tmp/test.jpg");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(2));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText("/tmp/test");
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	
	@Test public void testMenuExportPng() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  File file=new File("/tmp/test.png");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(3));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText("/tmp/test");
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	
	@Test public void testMenuExportSvg() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  File file=new File("/tmp/test.svg");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(4));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText("/tmp/test");
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	@Test public void testMenuExportTiff() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  File file=new File("/tmp/test.tiff");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(5));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText("/tmp/test");
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	@Test public void testMenuPrint() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  applet.menuItem("print").click();
		  //TODO in linux, the java print dialog is not working
		  //TODO printing as a such can not be tested, I suppose
	}
	
	@Test public void testMenuChargePlus2() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("chargePlus2").click();
		Assert.assertEquals(2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
	}
	
	@Test public void testMenuChargePlus1() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("chargePlus1").click();
		Assert.assertEquals(1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
	}

	@Test public void testMenuChargeZero() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("chargeZero").click();
		Assert.assertEquals(0, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
	}

	@Test public void testMenuChargeMinus1() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("chargeMinus1").click();
		Assert.assertEquals(-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
	}
	
	@Test public void testMenuChargeMinus2() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("chargeMinus2").click();
		Assert.assertEquals(-2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
	}

	@Test public void testMenuIsotopeMajorPlusThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("isotopeChange").click();
		applet.menuItem("C15").click();
		Assert.assertEquals(15, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}

	@Test public void testMenuIsotopeMajorPlusTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("isotopeChange").click();
		applet.menuItem("C14").click();
		Assert.assertEquals(14, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajorPlusOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("isotopeChange").click();
		applet.menuItem("C13").click();
		Assert.assertEquals(13, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajor() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("isotopeChange").click();
		applet.menuItem("C12").click();
		Assert.assertEquals(12, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajorMinusOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("isotopeChange").click();
		applet.menuItem("C11").click();
		Assert.assertEquals(11, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajorMinusTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("isotopeChange").click();
		applet.menuItem("C10").click();
		Assert.assertEquals(10, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajorMinusThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("isotopeChange").click();
		applet.menuItem("C9").click();
		Assert.assertEquals(9, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuConvertToRadical() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		//applet.menuItem("convertToRadical").click();
		//TODO miguel needs to look at this
		//Assert...
		//reset to non-radical
	}
	
	@Test public void testMenuPseudoStar() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("pseudoStar").click();
		Assert.assertEquals("*", ((IPseudoAtom)panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)).getLabel());
        IAtom normal = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getBuilder().newAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
        normal.setSymbol("C");
        panel.get2DHub().replaceAtom(normal,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
	}

	@Test public void testMenuPseudoR() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.selectionChanged();
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		applet.menuItem("pseudoR").click();
		Assert.assertEquals("R", ((IPseudoAtom)panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)).getLabel());
        IAtom normal = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getBuilder().newAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
        normal.setSymbol("C");
        panel.get2DHub().replaceAtom(normal,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
	}
	
	@Test public void testMenuPeriodictable() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.selectionChanged();
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("periodictablemenu").click();
		DialogFixture dialog = applet.dialog();
		dialog.button("Li").click();
		Assert.assertEquals("Li", panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		panel.get2DHub().setSymbol(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0),"C");
	}
		
	@Test public void testMenuBondSingle() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setOrder(IBond.Order.DOUBLE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("singleBond").click();
		Assert.assertEquals(IBond.Order.SINGLE, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getOrder());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setOrder(IBond.Order.SINGLE);
	}
	
	@Test public void testMenuBondStereoDown() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(CDKConstants.STEREO_BOND_NONE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("stereoDownBond").click();
		Assert.assertEquals(CDKConstants.STEREO_BOND_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(CDKConstants.STEREO_BOND_NONE);
	}

	@Test public void testMenuBondStereoUp() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(CDKConstants.STEREO_BOND_NONE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("stereoUpBond").click();
		Assert.assertEquals(CDKConstants.STEREO_BOND_UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(CDKConstants.STEREO_BOND_NONE);
	}

	@Test public void testMenuBondUndefinedStereo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(CDKConstants.STEREO_BOND_NONE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("undefinedStereoBond").click();
		Assert.assertEquals(CDKConstants.STEREO_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(CDKConstants.STEREO_BOND_NONE);
	}

	@Test public void testMenuBondUndefinedEZ() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(CDKConstants.STEREO_BOND_NONE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("undefinedEZBond").click();
		Assert.assertEquals(CDKConstants.EZ_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(CDKConstants.STEREO_BOND_NONE);
	}
	
	@Test public void testMenuReportSmiles() {
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
		
		@Test public void testMenuSaveAsMol() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
			  applet.menuItem("save").click();
			  DialogFixture dialog = applet.dialog();
			  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
			  combobox.setSelectedItem(combobox.getItemAt(1));
			  JTextComponentFixture text = dialog.textBox();
			  String file = "/tmp/test.mol";
			  if(new File(file).exists())
				  new File(file).delete();
			  text.setText(file);
			  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
			  okbutton.click();
			  MDLReader reader = new MDLReader(new FileInputStream(file));
			  IAtomContainer mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
			  JPanelFixture jcppanel=applet.panel("appletframe");
			  JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
			  Assert.assertEquals(panel.getChemModel().getMoleculeSet().getMolecule(0).getAtomCount(), mol.getAtomCount());
			  Assert.assertEquals(panel.getChemModel().getMoleculeSet().getMolecule(0).getBondCount(), mol.getBondCount());
		}
		//TODO do this for all formats
		
		@Test public void testMenuOpenMol() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
			String filename = "data/chebi/ChEBI_26120.mol";
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			String file = "/tmp/test.mol";
			if(new File(file).exists())
				  new File(file).delete();
	        FileOutputStream fos = new FileOutputStream(new File(file));
	        while(ins.available()>0)
	        	fos.write(ins.read());
			applet.menuItem("open").click();
			DialogFixture dialog = applet.dialog();
			JTextComponentFixture text = dialog.textBox();
			text.setText(file);
			JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
			okbutton.click();
	        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
	        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        Assert.assertNotNull(chemFile);
	        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
	        JPanelFixture jcppanel=applet.panel("appletframe");
	        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
	        Assert.assertEquals(1, containersList.size());
	        Assert.assertEquals((containersList.get(0)).getAtomCount(),panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
	        Assert.assertEquals((containersList.get(0)).getBondCount(),panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
	        restoreModel();
		}
		
		@Test public void testMenuOpenCml() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
			String filename = "data/a-pinen.cml";
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			String file = "/tmp/test.cml";
			if(new File(file).exists())
				  new File(file).delete();
	        FileOutputStream fos = new FileOutputStream(new File(file));
	        while(ins.available()>0)
	        	fos.write(ins.read());
			applet.menuItem("open").click();
			DialogFixture dialog = applet.dialog();
			JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
	        combobox.setSelectedItem(combobox.getItemAt(1));
			JTextComponentFixture text = dialog.textBox();
			text.setText(file);
			JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
			okbutton.click();
	        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
	        CMLReader reader = new CMLReader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        Assert.assertNotNull(chemFile);
	        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
	        JPanelFixture jcppanel=applet.panel("appletframe");
	        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
	        Assert.assertEquals(containersList.size(), containersList.size());
	        Assert.assertEquals((containersList.get(0)).getAtomCount(),panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
	        Assert.assertEquals((containersList.get(0)).getBondCount(),panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
	        restoreModel();
		}

		/*@Test public void testMenuOpenSmiles() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
			String filename = "data/smiles.smi";
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			String file = "/tmp/test.smi";
			if(new File(file).exists())
				  new File(file).delete();
	        FileOutputStream fos = new FileOutputStream(new File(file));
	        while(ins.available()>0)
	        	fos.write(ins.read());
			applet.menuItem("open").click();
			DialogFixture dialog = applet.dialog();
			JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
	        combobox.setSelectedItem(combobox.getItemAt(2));
			JTextComponentFixture text = dialog.textBox();
			text.setText(file);
			JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
			okbutton.click();
			DialogFixture coordsdialog = new DialogFixture(applet.robot, applet.robot.finder().find(new DialogTitleComponentMatcher("No 2D coordinates")));
			JButtonFixture okbuttoncoordsdialog = new JButtonFixture(coordsdialog.robot, coordsdialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
			okbuttoncoordsdialog.click();
	        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
	        SMILESReader reader = new SMILESReader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        Assert.assertNotNull(chemFile);
	        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
	        JPanelFixture jcppanel=applet.panel("appletframe");
	        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
	        Assert.assertEquals(containersList.size(), containersList.size());
	        Assert.assertEquals((containersList.get(0)).getAtomCount(),panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
	        Assert.assertEquals((containersList.get(0)).getBondCount(),panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
	        restoreModel();
		}*/
		//TODO do this for all formats

		
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
