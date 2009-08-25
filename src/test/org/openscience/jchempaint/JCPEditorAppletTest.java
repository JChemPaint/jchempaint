package org.openscience.jchempaint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.fest.swing.applet.AppletViewer;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.launcher.AppletLauncher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.selection.SingleSelection;
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
		try {
			originalModel=(IChemModel)panel.getChemModel().clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	@Test public void testMenuSave() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  applet.menuItem("save").click();
		  DialogFixture dialog = applet.dialog();
		  JTextComponentFixture text = dialog.textBox();
		  text.setText("/tmp/test.cml");
		  //TODO hit the ok button
		  dialog.close();
	}
	
	@Test public void testMenuOpen() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  applet.menuItem("open").click();
		  DialogFixture dialog = applet.dialog();
		  JTextComponentFixture text = dialog.textBox();
		  text.setText("/tmp/test.cml");
		  //TODO hit the ok button
		  dialog.close();
	}

	@Test public void testMenuExport() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JTextComponentFixture text = dialog.textBox();
		  text.setText("/tmp/test.cml");
		  //TODO hit the ok button
		  dialog.close();
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
		applet.menuItem("majorPlusThree").click();
		Assert.assertEquals(15, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}

	@Test public void testMenuIsotopeMajorPlusTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("majorPlusTwo").click();
		Assert.assertEquals(14, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajorPlusOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("majorPlusOne").click();
		Assert.assertEquals(13, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajor() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("major").click();
		Assert.assertEquals(12, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajorMinusOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("majorMinusOne").click();
		Assert.assertEquals(11, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajorMinusTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("majorMinusTwo").click();
		Assert.assertEquals(10, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
	}
	
	@Test public void testMenuIsotopeMajorMinusThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("majorMinusThree").click();
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
	}

	private void restoreModel(){
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.setChemModel(originalModel);
		panel.get2DHub().updateView();
	}
	

	@AfterClass public static void tearDown() {
	  viewer.unloadApplet();
	  applet.cleanUp();
	}
}
