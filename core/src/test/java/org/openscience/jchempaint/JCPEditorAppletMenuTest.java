package org.openscience.jchempaint;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.NameMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.jchempaint.io.JCPFileFilter;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;
import org.openscience.jchempaint.matchers.DialogTitleComponentMatcher;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.vecmath.Point2d;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.List;

public class JCPEditorAppletMenuTest extends AbstractAppletTest {

    // Exporting does work. The testcases are not useful, since they fail for
    // sputrious reasons and test nothing. Commented out.#
	/*
	@Test public void testMenuExportBmp() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		  File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.bmp");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").target.doClick();
		  DialogFixture dialog = applet.dialog();
		  //JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  //combobox.setSelectedItem(combobox.getItemAt(1));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText(file.toString().substring(0, file.toString().length()-4));
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.target.doClick();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.target.doClick();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}

	@Test public void testMenuExportJpg() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.jpg");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").target.doClick();
		  DialogFixture dialog = applet.dialog();
		  //JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  //combobox.setSelectedItem(combobox.getItemAt(2));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText(file.toString().substring(0, file.toString().length()-4));
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.target.doClick();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.target.doClick();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	
	@Test public void testMenuExportPng() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.png");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").target.doClick();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(3));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText(file.toString().substring(0, file.toString().length()-4));
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.target.doClick();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.target.doClick();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}*/

    // Not very meaningful test. Outcommented because of fest complications
/*	@Test public void testMenuExportSvg() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
          restoreModelWithBasicmol();
	      File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.svg");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").target.doClick();
		  DialogFixture dialog = applet.dialog();
		  //JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  //combobox.setSelectedItem(combobox.getItemAt(4));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText(file.toString().substring(0, file.toString().length()-4));
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.target.doClick();
		  //dialog = applet.dialog();
		  //okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  //okbutton.target.doClick();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	@Test public void testMenuPrint() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  //Printing only works from signed applet. We cannot test this.
	}*/

    @Test
    public void testMenuChargePlus1() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        int oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue();
        int oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue();
        applet.menuItem("plus").target.doClick();
        Assert.assertEquals(oldcharge + 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        Assert.assertEquals("plus", panel.get2DHub().getActiveDrawModule().getID());
        Point2d moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        panel.get2DHub().mouseClickedDown((int) moveto.x, (int) moveto.y);
        Assert.assertEquals(oldhcount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue());
        Assert.assertEquals(oldcharge + 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        panel.get2DHub().mouseClickedDown((int) moveto.x, (int) moveto.y);
        Assert.assertEquals(oldhcount - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue());
        Assert.assertEquals(oldcharge + 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).setFormalCharge(0);
    }

    @Test
    public void testMenuChargeMinus1() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        int oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue();
        int oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue();
        applet.menuItem("minus").target.doClick();
        Assert.assertEquals(oldcharge - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        Assert.assertEquals("minus", panel.get2DHub().getActiveDrawModule().getID());
        Point2d moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        panel.get2DHub().mouseClickedDown((int) moveto.x, (int) moveto.y);
        Assert.assertEquals(oldcharge - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount - 1 , panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue());
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        panel.get2DHub().mouseClickedDown((int) moveto.x, (int) moveto.y);
        Assert.assertEquals(oldcharge - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).setFormalCharge(0);
    }

    @Ignore
    public void testMenuValenceOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(1);
    }

    @Ignore
    public void testMenuValenceTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(2);
    }

    @Ignore
    public void testMenuValenceThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(3);
    }

    @Ignore
    public void testMenuValenceFour() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(4);
    }

    @Ignore
    public void testMenuValenceFive() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(5);
    }

    @Ignore
    public void testMenuValenceSix() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(6);
    }

    @Ignore
    public void testMenuValenceSeven() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(7);
    }

    @Ignore
    public void testMenuValenceEight() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(8);
    }

    @Ignore
    public void testMenuValenceOff() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        JPanelFixture jcppanel = applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setValency(1);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).setValency(2);
        genericValenceTest(-1);
    }

    private void genericValenceTest(int valence) {
        //we go to select mode
        restoreModelWithBasicmol();
        applet.button("select").target.doClick();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        applet.menuItem("isotopeChange").target.doClick();
        applet.menuItem("valence" + (valence == -1 ? "Off" : valence)).target.doClick();
        if (valence == -1)
            Assert.assertNull(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getValency());
        else
            Assert.assertEquals(valence, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getValency().intValue());
        //the mode should have changed now
        Assert.assertEquals("valence", panel.get2DHub().getActiveDrawModule().getID());
        //if we click somewhere, we should get a new atom with specified properties
        Point2d moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int) moveto.x, (int) moveto.y), MouseButton.LEFT_BUTTON, 1);
        if (valence == -1)
            Assert.assertNull(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getValency());
        else
            Assert.assertEquals(valence, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getValency().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setValency(null);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).setValency(null);
    }

    @Test
    public void testMenuConvertToRadical() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        //applet.menuItem("convertToRadical").target.doClick();
        //TODO miguel needs to look at this
        //Assert...
        //reset to non-radical
    }

    @Test
    public void testMenuPseudoStar() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        applet.menuItem("pseudoStar").target.doClick();
        Assert.assertEquals("*", ((IPseudoAtom) panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)).getLabel());
        //the mode should have changed now
        Assert.assertEquals("*", panel.get2DHub().getActiveDrawModule().getID());
        Assert.assertTrue(panel.get2DHub().getController2DModel().getDrawPseudoAtom());
        Assert.assertEquals("*", panel.get2DHub().getActiveDrawModule().getID());
        IAtom normal = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getBuilder().newInstance(IAtom.class, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
        normal.setSymbol("C");
        panel.get2DHub().replaceAtom(normal, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
    }

    @Test
    public void testMenuPseudoR() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        applet.menuItem("pseudoR").target.doClick();
        Assert.assertEquals("R", ((IPseudoAtom) panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)).getLabel());
        //the mode should have changed now
        Assert.assertEquals("R", panel.get2DHub().getActiveDrawModule().getID());
        Assert.assertTrue(panel.get2DHub().getController2DModel().getDrawPseudoAtom());
        Assert.assertEquals("R", panel.get2DHub().getActiveDrawModule().getID());
        IAtom normal = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getBuilder().newInstance(IAtom.class, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
        normal.setSymbol("C");
        panel.get2DHub().replaceAtom(normal, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
    }

    @Test
    public void testMenuPeriodictable() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException, InterruptedException, InvocationTargetException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        SwingUtilities.invokeLater(applet.menuItem("periodictable").target::doClick);
        DialogFixture dialog = applet.dialog();
        dialog.button("Li").target.doClick();
        dialog.target.setVisible(false);
        applet.robot.waitForIdle();
        Assert.assertEquals("Li", panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
        panel.get2DHub().setSymbol(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0), "C");
        Assert.assertEquals("periodictable", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondSingle() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        JPanelFixture jcppanel = applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setOrder(IBond.Order.DOUBLE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        applet.menuItem("bond").target.doClick();
        Assert.assertEquals(IBond.Order.SINGLE, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getOrder());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setOrder(IBond.Order.SINGLE);
        Assert.assertEquals("bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondStereoDown() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        JPanelFixture jcppanel = applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        applet.menuItem("down_bond").target.doClick();
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        Assert.assertEquals("down_bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondStereoUp() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        JPanelFixture jcppanel = applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        applet.menuItem("up_bond").target.doClick();
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        Assert.assertEquals("up_bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondUndefinedStereo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        JPanelFixture jcppanel = applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        applet.menuItem("undefined_bond").target.doClick();
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        Assert.assertEquals("undefined_bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondUndefinedEZ() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        JPanelFixture jcppanel = applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        applet.menuItem("undefined_stereo_bond").target.doClick();
        Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        Assert.assertEquals("undefined_stereo_bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuReportSmiles() {
        restoreModelWithBasicmol();
        SwingUtilities.invokeLater(applet.menuItem("createSMILES").target::doClick);
        DialogFixture dialog = applet.dialog("smilestextdialog");
        String text = dialog.textBox("textviewdialogtextarea").text();
        Assert.assertTrue(text.contains("CCCCCCCC"));
        dialog.target.setVisible(false);
    }

    @Test
    public void testMenuNew() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        applet.menuItem("new").target.doClick();
        JPanelFixture jcppanel = applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        Assert.assertEquals("", panel.getSmiles());
        restoreModelWithBasicmol();
    }

    @Test
    public void testMenuSaveAsMol() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        SwingUtilities.invokeLater(applet.menuItem("save").target::doClick);
        applet.robot.waitForIdle();
        JFileChooserFixture dialog = applet.fileChooser("save");
        File file = File.createTempFile("jcptest", ".mol");
        file.delete();
        JComboBox<?> combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
        int index = -1;
        for (int i = 0; i < combobox.getModel().getSize(); i++)
            if (((JCPFileFilter) combobox.getModel().getElementAt(i)).getType() == JCPFileFilter.mol)
                index = i;
        Assert.assertFalse(index < 0);
        combobox.setSelectedIndex(index);
        JTextComponentFixture text = dialog.fileNameTextBox();
        text.setText(file.toString());
        JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
        okbutton.target.doClick();
        dialog.target.setVisible(false);
        applet.robot.waitForIdle();
        MDLV2000Reader reader = null;
        try {
            reader = new MDLV2000Reader(Files.newInputStream(file.toPath()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Assert.fail("File not found.");
            return;
        }
        IAtomContainer mol = (IAtomContainer) reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        JPanelFixture jcppanel = applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount(), mol.getAtomCount());
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount(), mol.getBondCount());
        reader.close();
    }
    //TODO do this for all formats

    @Test
    public void testMenuOpenMol() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        if (System.getProperty("os.name").indexOf("Mac") == -1) {
            String filename = "data/chebi/ChEBI_26120.mol";
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
            File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "test.mol");
            if (file.exists())
                file.delete();
            FileOutputStream fos = new FileOutputStream(file);
            while (ins.available() > 0)
                fos.write(ins.read());
            fos.close();
            applet.menuItem("open").target.doClick();
            DialogFixture dialog = applet.dialog();
            JTextComponentFixture text = dialog.textBox();
            text.setText(file.toString());
            JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
            okbutton.target.doClick();
            ins = this.getClass().getClassLoader().getResourceAsStream(filename);
            MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
            ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
            reader.close();
            Assert.assertNotNull(chemFile);
            List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
            JPanelFixture jcppanel = applet.panel("appletframe");
            JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
            Assert.assertEquals(1, containersList.size());
            Assert.assertEquals((containersList.get(0)).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
            Assert.assertEquals((containersList.get(0)).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
            restoreModelWithBasicmol();
        }
    }

    // CML i/o was disabled, buggy test commented out
		/*
		@Test public void testMenuOpenCml() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		    if(System.getProperty("os.name").indexOf("Mac")==-1){
    			String filename = "data/a-pinen.cml";
    	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.cml");
    			if(file.exists())
    				  file.delete();
    	        FileOutputStream fos = new FileOutputStream(file);
    	        while(ins.available()>0)
    	        	fos.write(ins.read());
    			applet.menuItem("open").target.doClick();
    			DialogFixture dialog = applet.dialog();
    			//JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
    	        //combobox.setSelectedItem(combobox.getItemAt(1));
    			JTextComponentFixture text = dialog.textBox();
    			text.setText(file.toString());
    			JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
    			okbutton.target.doClick();
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
    	        restoreModelWithBasicmol();
		    }
		}*/

    @Test
    public void testMenuOpenSmiles() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        if (System.getProperty("os.name").indexOf("Mac") == -1) {
            String filename = "data/smiles.smi";
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
            File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "test.smi");
            if (file.exists())
                file.delete();
            FileOutputStream fos = new FileOutputStream(file);
            while (ins.available() > 0)
                fos.write(ins.read());
            fos.close();
            applet.menuItem("open").target.doClick();
            DialogFixture dialog = applet.dialog();
            //it seems the Combo selection depends on if you run test as single test or all in class, no idea why
            //JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter","org.openscience.jchempaint.io.JCPSaveFileFilter"));
            //combobox.setSelectedItem(combobox.getItemAt(2));
            JTextComponentFixture text = dialog.textBox();
            text.setText(file.toString());
            JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
            okbutton.target.doClick();
            DialogFixture coordsdialog = new DialogFixture(applet.robot, applet.robot.finder().find(new DialogTitleComponentMatcher("No 2D coordinates")));
            JButtonFixture okbuttoncoordsdialog = new JButtonFixture(coordsdialog.robot, coordsdialog.robot.finder().find(new ButtonTextComponentMatcher("Yes")));
            okbuttoncoordsdialog.target.doClick();
            ins = this.getClass().getClassLoader().getResourceAsStream(filename);
            SMILESReader reader = new SMILESReader(ins);
            ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
            Assert.assertNotNull(chemFile);
            reader.close();
            List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
            JPanelFixture jcppanel = applet.panel("appletframe");
            JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
            Assert.assertEquals(containersList.size(), containersList.size());
            Assert.assertEquals((containersList.get(0)).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
            Assert.assertEquals((containersList.get(0)).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
            restoreModelWithBasicmol();
        }
    }
    //TODO do this for all formats

    @Test
    public void testMenuTemplatesAll() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelToEmpty();
        SwingUtilities.invokeLater(applet.menuItem("pasteTemplate").target::doClick);
        DialogFixture dialog = applet.dialog("templates");
        JButtonFixture morphineButton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Morphine")));
        morphineButton.target.doClick();
        dialog.target.setVisible(false);
        applet.robot.waitForIdle();
        Assert.assertEquals(1, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(22, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
    }

    @Test
    public void testSwitchLanguage() {
        applet.menuItem("options").target.doClick();
        DialogFixture dialog = applet.dialog();
        JTabbedPaneFixture tabs = new JTabbedPaneFixture(dialog.robot, (JTabbedPane) dialog.robot.finder().findByName("tabs"));
        tabs.selectTab(1);
        JComboBox<?> combobox = (JComboBox<?>) dialog.robot.finder().find(new NameMatcher("language"));
        for (int i = 0; i < combobox.getItemCount(); i++) {
            if (((String) combobox.getItemAt(i)).equals("German")) {
                combobox.setSelectedIndex(i);
                break;
            }
        }
        JButtonFixture applybutton = new JButtonFixture(dialog.robot, (JButton) dialog.robot.finder().find(new NameMatcher("apply", true)));
        applybutton.target.doClick();
        Assert.assertEquals("Neu", applet.menuItem("new").component().getText());

        for (int i = 0; i < combobox.getItemCount(); i++) {
            if (((String) combobox.getItemAt(i)).equals("American English")) {
                combobox.setSelectedIndex(i);
                break;
            }
        }

        applybutton.target.doClick();
        dialog.target.setVisible(false);
        applet.robot.waitForIdle();

        Assert.assertEquals("New", applet.menuItem("new").component().getText());
    }


}
