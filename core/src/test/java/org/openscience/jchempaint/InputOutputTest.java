/*
 *  Copyright (C) 2026 John Mayfield
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.DialogTitleComponentMatcher;

import javax.swing.text.JTextComponent;
import javax.vecmath.Point2d;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * These tests check expected behaviour when loading/saving.
 *
 * @author John Mayfield
 */
public class InputOutputTest extends AbstractUITest {

    @Test
    public void testSetSmiles() throws CDKException {
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.setSmiles("CCCC");
        panel.get2DHub().updateView();
        Assert.assertEquals(4, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getAtomCount());
        restoreModelToEmpty();
    }


    @Test
    public void testMenuOpenMol() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelToEmpty();

        File file = File.createTempFile("jcp", ".mol");
        try (InputStream in = this.getClass().getResourceAsStream("/data/chebi/ChEBI_26120.mol");
             FileOutputStream out = new FileOutputStream(file)) {
            Assert.assertNotNull("Input stream was null", in);
            while (in.available() > 0)
                out.write(in.read());
        }

        frame.menuItem("open").click();
        JFileChooserFixture chooser = frame.fileChooser("open");
        chooser.selectFile(file);
        robot.waitForIdle();
        chooser.approveButton().click();
        robot.waitForIdle();

        Assert.assertEquals("C(CCC=C(CCC=C(C)C)C)(=CC=CC(=CC=CC=C(CCC=C(CCC=C(CCC=C(C)C)C)C)C)C)C",
                            panel.getSmiles());
    }

    @Test
    public void testMenuSaveAsMol() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        robot.waitForIdle();
        frame.menuItem("saveAs").click();
        robot.waitForIdle();
        JFileChooserFixture dialog = frame.fileChooser("save");
        File file = File.createTempFile("jcptest", ".mol");
        file.delete();
        dialog.selectFile(file);
        dialog.approveButton().click();
        frame.robot.waitForIdle();
        MDLV2000Reader reader = null;
        try {
            reader = new MDLV2000Reader(Files.newInputStream(file.toPath()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Assert.fail("File not found.");
            return;
        }
        IAtomContainer mol = (IAtomContainer) reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount(), mol.getAtomCount());
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount(), mol.getBondCount());
        reader.close();
    }
    //TODO do this for all formats

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
    	        JPanelFixture jcppanel=applet.panel("JChemPaintPanel");
    	        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
    	        Assert.assertEquals(containersList.size(), containersList.size());
    	        Assert.assertEquals((containersList.get(0)).getAtomCount(),panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
    	        Assert.assertEquals((containersList.get(0)).getBondCount(),panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
    	        restoreModelWithBasicmol();
		    }
		}*/

    @Test
    public void testMenuOpenSmiles() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelToEmpty();
        File file = File.createTempFile("jcpTest", ".smi");
        try (InputStream in = this.getClass().getResourceAsStream("/data/smiles.smi");
             FileOutputStream out = new FileOutputStream(file)) {
            Assert.assertNotNull("Input stream was null", in);
            while (in.available() > 0)
                out.write(in.read());
        }

        frame.menuItem("open").click();
        JFileChooserFixture chooser = frame.fileChooser("open");
        chooser.selectFile(file);
        chooser.approveButton().click();
        robot.waitForIdle();

        DialogFixture coordsdialog = new DialogFixture(frame.robot, frame.robot.finder().find(new DialogTitleComponentMatcher("No 2D coordinates")));
        JButtonFixture okbuttoncoordsdialog = new JButtonFixture(coordsdialog.robot, coordsdialog.robot.finder().find(new ButtonTextComponentMatcher("Yes")));
        okbuttoncoordsdialog.target.doClick();
        robot.waitForIdle();

        Assert.assertEquals("C1=CC=CC=C1.CNNC.OC(=O)C(CO)N.CC(=O)O.C1CCNC1.CC(C)C.CCCC.COC", panel.getSmiles());
    }
    //TODO do this for all formats

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

    @Test public void testBug70() throws CDKException, IOException {
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        frame.button("hexagon").target.doClick();
        frame.click();
        Point2d point = getAtomPoint(panel, 0);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.RIGHT_BUTTON, 1);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.RIGHT_BUTTON, 1);
        frame.menuItem("showACProperties").click();
        DialogFixture dialog = frame.dialog();
        JTextComponent textfield = dialog.robot.finder().find(JTextComponentMatcher.withName("Title"));
        textfield.setText("aaa");
        JButtonFixture okbutton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("OK")));
        okbutton.click();
        frame.menuItem("saveAs").click();
        JFileChooserFixture fileChooser = frame.fileChooser("save");
        File file=new File(System.getProperty("java.io.tmpdir") + File.separator + "test.mol");
        if(file.exists())
            file.delete();
        fileChooser.selectFile(file);
        fileChooser.approveButton().click();
        MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(file));
        IAtomContainer mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        Assert.assertEquals("aaa",(String)mol.getProperty(CDKConstants.TITLE));
        reader.close();
    }

    @Test public void testBug77() throws CDKException, IOException{
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        frame.button("hexagon").target.doClick();
        frame.click();
        frame.menuItem("saveAs").click();

        JPanelFixture renderpanel = frame.panel("renderpanel");
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File file1 = new File(tmpDir, "test1.mol");
        File file2 = new File(tmpDir, "test2.mol");
        file1.delete();
        file2.delete();

        JFileChooserFixture saveAs = frame.fileChooser("save");
        saveAs.selectFile(file1);
        saveAs.approveButton().click();
        //not the bug, but still worth testing
        MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(file1));
        IAtomContainer mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount(), mol.getAtomCount());
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount(), mol.getBondCount());

        restoreModelToEmpty(); // rather than "new"
        frame.button("hexagon").click();
        frame.click();
        frame.button("bondTool").click();
        robot.click(renderpanel.component(), toAwtPoint(getAtomPoint(panel, 0)));
        robot.waitForIdle();
        panel.get2DHub().updateView();

        frame.menuItem("saveAs").click();
        saveAs = frame.fileChooser("save");
        saveAs.selectFile(file2);
        saveAs.approveButton().click();
        reader.close();

        //not the bug, but still worth testing
        reader = new MDLV2000Reader(new FileInputStream(file2));
        mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount(), mol.getAtomCount());
        Assert.assertEquals(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount(), mol.getBondCount());

        //ok, now the critical bits - open mol1
        restoreModelToEmpty(); // rather than "new"
        panel.setModified(false); // avoid "unsaved" warning
        frame.menuItem("open").click();
        robot.waitForIdle();
        JFileChooserFixture open = frame.fileChooser("open");
        open.selectFile(file1);
        open.approveButton().click();
        file1.delete();

        //"save as" mol1
        frame.menuItem("saveAs").click();
        saveAs = frame.fileChooser("save");
        saveAs.selectFile(file1);
        saveAs.approveButton().click();
        reader.close();

        //save should write to mol2, ie mol1=6 atoms, mol2=7atoms
        reader = new MDLV2000Reader(new FileInputStream(file1));
        mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        Assert.assertEquals(6, mol.getAtomCount());
        reader.close();

        reader = new MDLV2000Reader(new FileInputStream(file2));
        mol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        Assert.assertEquals(7, mol.getAtomCount());
        reader.close();
        restoreModelToEmpty();
    }

}
