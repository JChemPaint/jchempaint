package org.openscience.jchempaint;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.JComboBox;
import javax.vecmath.Point2d;

import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;
import org.openscience.jchempaint.matchers.DialogTitleComponentMatcher;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

public class JCPEditorAppletMenuTest extends AbstractAppletTest{

	@Test public void testMenuExportBmp() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		  File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.bmp");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(1));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText(file.toString().substring(0, file.toString().length()-4));
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}

	@Test public void testMenuExportJpg() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.jpg");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(2));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText(file.toString().substring(0, file.toString().length()-4));
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	
	@Test public void testMenuExportPng() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.png");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(3));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText(file.toString().substring(0, file.toString().length()-4));
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	
	@Test public void testMenuExportSvg() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	      File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.svg");
		  if(file.exists())
			  file.delete();
		  applet.menuItem("export").click();
		  DialogFixture dialog = applet.dialog();
		  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPExportFileFilter"));
		  combobox.setSelectedItem(combobox.getItemAt(4));
		  JTextComponentFixture text = dialog.textBox();
		  text.setText(file.toString().substring(0, file.toString().length()-4));
		  JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Save")));
		  okbutton.click();
		  dialog = applet.dialog();
		  okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
		  okbutton.click();
		  //we only check the existence of file for now
		  Assert.assertTrue(file.exists());
	}
	@Test public void testMenuPrint() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		  //Printing only works from signed applet. We cannot test this.
	}
	
	@Test public void testMenuChargePlus1() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		int oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getHydrogenCount().intValue();
        int oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue();
		applet.menuItem("plus").click();
		Assert.assertEquals(oldcharge+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
		Assert.assertEquals(oldhcount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getHydrogenCount().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        Assert.assertEquals("plus",panel.get2DHub().getActiveDrawModule().getID());
        Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(oldhcount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getHydrogenCount().intValue());
        Assert.assertEquals(oldcharge+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(oldhcount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getHydrogenCount().intValue());
        Assert.assertEquals(oldcharge+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).setFormalCharge(0);
	}

	@Test public void testMenuChargeMinus1() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
        int oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getHydrogenCount().intValue();
        int oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue();
		applet.menuItem("minus").click();
		Assert.assertEquals(oldcharge-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getHydrogenCount().intValue());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        Assert.assertEquals("minus",panel.get2DHub().getActiveDrawModule().getID());
        Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(oldcharge-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getHydrogenCount().intValue());
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(oldcharge-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getHydrogenCount().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).setFormalCharge(0);
    }
	
	@Test public void testMenuIsotopeMajorPlusThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(15);
	}

	@Test public void testMenuIsotopeMajorPlusTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(14);
	}
	
	@Test public void testMenuIsotopeMajorPlusOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(13);
	}
	
	@Test public void testMenuIsotopeMajor() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(12);
	}
	
	@Test public void testMenuIsotopeMajorMinusOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(11);
	}
	
	private void genericIsotopeTest(int isotopeNumber){
	    //we go to select mode
	    restoreModelWithBasicmol();
        applet.button("select").click();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
        applet.menuItem("isotopeChange").click();
        applet.menuItem("C"+isotopeNumber).click();
        Assert.assertEquals(isotopeNumber, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
        //the mode should have changed now
        Assert.assertEquals("C", panel.get2DHub().getActiveDrawModule().getID());
        Assert.assertEquals(isotopeNumber, panel.get2DHub().getController2DModel().getDrawIsotopeNumber());
        //if we click somewhere, we should get a new atom with specified properties
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point(100,100));
        Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        Assert.assertEquals("C", panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtom(0).getSymbol());
        Assert.assertEquals(isotopeNumber, panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtom(0).getMassNumber().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
    }
	
	@Test public void testMenuIsotopeMajorMinusTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(10);
	}
	
	@Test public void testMenuIsotopeMajorMinusThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(9);
	}
	
	@Test public void testMenuValenceOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(1);
    }
	
    @Test public void testMenuValenceTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(2);
    }
    
    @Test public void testMenuValenceThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(3);
    }
    
    @Test public void testMenuValenceFour() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(4);
    }
    
    @Test public void testMenuValenceFive() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(5);
    }
    
    @Test public void testMenuValenceSix() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(6);
    }

    @Test public void testMenuValenceSeven() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(7);
    }

    @Test public void testMenuValenceEight() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        genericValenceTest(8);
    }

    @Test public void testMenuValenceOff() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setValency(1);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).setValency(2);
        genericValenceTest(-1);
    }
    
    private void genericValenceTest(int valence){
        //we go to select mode
        applet.button("select").click();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        applet.menuItem("isotopeChange").click();
        applet.menuItem("valence"+(valence==-1 ? "Off" : valence)).click();
        if(valence==-1)
            Assert.assertNull(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getValency());
        else
            Assert.assertEquals(valence, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getValency().intValue());
        //the mode should have changed now
        Assert.assertEquals("valence", panel.get2DHub().getActiveDrawModule().getID());
        //if we click somewhere, we should get a new atom with specified properties
        Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        if(valence==-1)
            Assert.assertNull(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getValency());
        else
            Assert.assertEquals(valence, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getValency().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setValency(null);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).setValency(null);
    }

    @Test public void testMenuConvertToRadical() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		//applet.menuItem("convertToRadical").click();
		//TODO miguel needs to look at this
		//Assert...
		//reset to non-radical
	}
	
	@Test public void testMenuPseudoStar() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("pseudoStar").click();
		Assert.assertEquals("*", ((IPseudoAtom)panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)).getLabel());
        //the mode should have changed now
        Assert.assertEquals("*", panel.get2DHub().getActiveDrawModule().getID());
        Assert.assertTrue(panel.get2DHub().getController2DModel().getDrawPseudoAtom());
        Assert.assertEquals("*",panel.get2DHub().getActiveDrawModule().getID());
        IAtom normal = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getBuilder().newAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
        normal.setSymbol("C");
        panel.get2DHub().replaceAtom(normal,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
	}

	@Test public void testMenuPseudoR() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("pseudoR").click();
		Assert.assertEquals("R", ((IPseudoAtom)panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)).getLabel());
        //the mode should have changed now
        Assert.assertEquals("R", panel.get2DHub().getActiveDrawModule().getID());
        Assert.assertTrue(panel.get2DHub().getController2DModel().getDrawPseudoAtom());
        Assert.assertEquals("R",panel.get2DHub().getActiveDrawModule().getID());
        IAtom normal = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getBuilder().newAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
        normal.setSymbol("C");
        panel.get2DHub().replaceAtom(normal,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
	}
	
	@Test public void testMenuPeriodictable() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
		panel.selectionChanged();
		applet.menuItem("periodictable").click();
		DialogFixture dialog = applet.dialog();
		dialog.button("Li").click();
		Assert.assertEquals("Li", panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		panel.get2DHub().setSymbol(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0),"C");
        Assert.assertEquals("periodictable",panel.get2DHub().getActiveDrawModule().getID());
	}
		
	@Test public void testMenuBondSingle() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setOrder(IBond.Order.DOUBLE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("bond").click();
		Assert.assertEquals(IBond.Order.SINGLE, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getOrder());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setOrder(IBond.Order.SINGLE);
		Assert.assertEquals("bond", panel.get2DHub().getActiveDrawModule().getID());
	}
	
	@Test public void testMenuBondStereoDown() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("down_bond").click();
		Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
		Assert.assertEquals("down_bond", panel.get2DHub().getActiveDrawModule().getID());
	}

	@Test public void testMenuBondStereoUp() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("up_bond").click();
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
		Assert.assertEquals("up_bond", panel.get2DHub().getActiveDrawModule().getID());
	}

	@Test public void testMenuBondUndefinedStereo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("undefined_bond").click();
		Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
		Assert.assertEquals("undefined_bond", panel.get2DHub().getActiveDrawModule().getID());
	}

	@Test public void testMenuBondUndefinedEZ() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
		panel.selectionChanged();
		applet.menuItem("undefined_stereo_bond").click();
		Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
		panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
		Assert.assertEquals("undefined_stereo_bond", panel.get2DHub().getActiveDrawModule().getID());
	}
	
	@Test public void testMenuReportSmiles() {
	    restoreModelWithBasicmol();
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
		  restoreModelWithBasicmol();
    }
		
		@Test public void testMenuSaveAsMol() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
			  applet.menuItem("save").click();
			  DialogFixture dialog = applet.dialog();
			  JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
			  combobox.setSelectedItem(combobox.getItemAt(5));
			  JTextComponentFixture text = dialog.textBox();
			  File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.mol");
			  if(file.exists())
				  file.delete();
			  text.setText(file.toString());
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
            if(System.getProperty("os.name").indexOf("Mac")==-1){
    			String filename = "data/chebi/ChEBI_26120.mol";
    	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.mol");
    			if(file.exists())
    				  file.delete();
    	        FileOutputStream fos = new FileOutputStream(file);
    	        while(ins.available()>0)
    	        	fos.write(ins.read());
    			applet.menuItem("open").click();
    			DialogFixture dialog = applet.dialog();
    			JTextComponentFixture text = dialog.textBox();
    			text.setText(file.toString());
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
    	        restoreModelWithBasicmol();
            }
		}
		
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
    			applet.menuItem("open").click();
    			DialogFixture dialog = applet.dialog();
    			JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter"));
    	        combobox.setSelectedItem(combobox.getItemAt(1));
    			JTextComponentFixture text = dialog.textBox();
    			text.setText(file.toString());
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
    	        restoreModelWithBasicmol();
		    }
		}

		@Test public void testMenuOpenSmiles() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	          if(System.getProperty("os.name").indexOf("Mac")==-1){
        			String filename = "data/smiles.smi";
        	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        	        File file=new File(System.getProperty("java.io.tmpdir")+File.separator+"test.smi");
        			if(file.exists())
        				  file.delete();
        	        FileOutputStream fos = new FileOutputStream(file);
        	        while(ins.available()>0)
        	        	fos.write(ins.read());
        			applet.menuItem("open").click();
        			DialogFixture dialog = applet.dialog();
        			//it seems the Combo selection depends on if you run test as single test or all in class, no idea why
        			JComboBox combobox = dialog.robot.finder().find(new ComboBoxTextComponentMatcher("org.openscience.jchempaint.io.JCPFileFilter","org.openscience.jchempaint.io.JCPSaveFileFilter"));
        	        combobox.setSelectedItem(combobox.getItemAt(2));
        			JTextComponentFixture text = dialog.textBox();
        			text.setText(file.toString());
        			JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Open")));
        			okbutton.click();
        			DialogFixture coordsdialog = new DialogFixture(applet.robot, applet.robot.finder().find(new DialogTitleComponentMatcher("No 2D coordinates")));
        			JButtonFixture okbuttoncoordsdialog = new JButtonFixture(coordsdialog.robot, coordsdialog.robot.finder().find(new ButtonTextComponentMatcher("Yes")));
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
        	        restoreModelWithBasicmol();
	          }
		}
		//TODO do this for all formats
		
		@Test public void testMenuCut(){
		    restoreModelWithBasicmol();
	        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
	        panel.selectionChanged();
	        Assert.assertEquals(8,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
	        applet.menuItem("cut").click();
            Assert.assertEquals(7,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
            applet.menuItem("paste").click();
            Assert.assertEquals(2,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
            Assert.assertEquals(7,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
            Assert.assertEquals(1,panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
            Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(4).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(4).getPoint2d().y);
            JPopupMenuFixture popup = applet.panel("renderpanel").showPopupMenuAt(new Point((int)moveto.x,(int)moveto.y));
            popup.menuItem("cut2").click();
            Assert.assertEquals(1,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
            Assert.assertEquals(7,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		}
	
	    @Test public void testMenuTemplatesAll() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
            restoreModelWithBasicmol();
            applet.menuItem("pasteTemplate").click();
            DialogFixture dialog = applet.dialog("templates");
            JButtonFixture penicillinbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Penicillin")));
            penicillinbutton.click();
            Assert.assertEquals(2,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
            Assert.assertEquals(18,panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
	    }

        @Test public void testMenuTemplatesAlkaloids() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
            restoreModelWithBasicmol();
            applet.menuItem("alkaloids").click();
            DialogFixture dialog = applet.dialog("templates");
            JButtonFixture morphinebutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Morphine")));
            morphinebutton.click();
            Assert.assertEquals(2,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
            Assert.assertEquals(22,panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
            applet.menuItem("alkaloids").click();
            dialog = applet.dialog("templates");
            morphinebutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Morphine")));
            morphinebutton.click();
            Assert.assertEquals(3,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
            Assert.assertEquals(22,panel.getChemModel().getMoleculeSet().getAtomContainer(2).getAtomCount());
        }
}
