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
import org.fest.swing.core.NameMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.vecmath.Point2d;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * These tests check the menu bar operations.
 *
 * @author John Mayfield
 */
public class MenuTest extends AbstractUITest {

    @Test
    public void testMenuChargePlus1() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        int oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue();
        int oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue();
        frame.menuItem("plus").target.doClick();
        Assert.assertEquals(oldcharge + 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        Assert.assertEquals("plus", panel.get2DHub().getActiveDrawModule().getID());
        Point2d moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        panel.get2DHub().mouseClickedDown((int) moveto.x, (int) moveto.y, 0);
        Assert.assertEquals(oldhcount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue());
        Assert.assertEquals(oldcharge + 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        panel.get2DHub().mouseClickedDown((int) moveto.x, (int) moveto.y, 0);
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
        frame.menuItem("minus").target.doClick();
        Assert.assertEquals(oldcharge - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getImplicitHydrogenCount().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setFormalCharge(0);
        Assert.assertEquals("minus", panel.get2DHub().getActiveDrawModule().getID());
        Point2d moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        panel.get2DHub().mouseClickedDown((int) moveto.x, (int) moveto.y, 0);
        Assert.assertEquals(oldcharge - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(oldhcount - 1 , panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue());
        oldhcount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getImplicitHydrogenCount().intValue();
        oldcharge = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getFormalCharge().intValue();
        panel.get2DHub().mouseClickedDown((int) moveto.x, (int) moveto.y, 0);
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
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setValency(1);
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).setValency(2);
        genericValenceTest(-1);
    }

    private void genericValenceTest(int valence) {
        //we go to select mode
        restoreModelWithBasicmol();
        frame.button("select").target.doClick();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        frame.menuItem("isotopeChange").target.doClick();
        frame.menuItem("valence" + (valence == -1 ? "Off" : valence)).target.doClick();
        if (valence == -1)
            Assert.assertNull(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getValency());
        else
            Assert.assertEquals(valence, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getValency().intValue());
        //the mode should have changed now
        Assert.assertEquals("valence", panel.get2DHub().getActiveDrawModule().getID());
        //if we click somewhere, we should get a new atom with specified properties
        Point2d moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int) moveto.x, (int) moveto.y), MouseButton.LEFT_BUTTON, 1);
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

    // JWM Sept 2026 - pseudoStar is going to be used for VarAttach so pseudoR
    //                 is more correct
    @Test
    public void testMenuPseudoStar() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        frame.menuItem("pseudoR").target.doClick();
        IAtom atom = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0);
        Assert.assertEquals("R", ((IPseudoAtom)AtomRef.deref(atom)).getLabel());
        //the mode should have changed now
        Assert.assertEquals("R", panel.get2DHub().getActiveDrawModule().getID());
        Assert.assertTrue(panel.get2DHub().getController2DModel().getDrawPseudoAtom());
        Assert.assertEquals("R", panel.get2DHub().getActiveDrawModule().getID());
        IAtom normal = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getBuilder().newInstance(IAtom.class, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
        normal.setSymbol("C");
        panel.get2DHub().replaceAtom(normal, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0));
    }

    @Test
    public void testMenuPseudoR() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        frame.menuItem("pseudoR").target.doClick();
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
        SwingUtilities.invokeLater(frame.menuItem("periodictable").target::doClick);
        DialogFixture dialog = frame.dialog();
        dialog.button("Li").target.doClick();
        dialog.target.setVisible(false);
        frame.robot.waitForIdle();
        Assert.assertEquals("Li", panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
        panel.get2DHub().setSymbol(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0), "C");
        Assert.assertEquals("periodictable", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondSingle() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setOrder(IBond.Order.DOUBLE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        frame.menuItem("bond").target.doClick();
        Assert.assertEquals(IBond.Order.SINGLE, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getOrder());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setOrder(IBond.Order.SINGLE);
        Assert.assertEquals("bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondStereoDown() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        frame.menuItem("down_bond").target.doClick();
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        Assert.assertEquals("down_bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondStereoUp() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        frame.menuItem("up_bond").target.doClick();
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        Assert.assertEquals("up_bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondUndefinedStereo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        frame.menuItem("undefined_bond").target.doClick();
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        Assert.assertEquals("undefined_bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuBondUndefinedEZ() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0)));
        panel.selectionChanged();
        frame.menuItem("undefined_stereo_bond").target.doClick();
        Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).getStereo());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(0).setStereo(IBond.Stereo.NONE);
        Assert.assertEquals("undefined_stereo_bond", panel.get2DHub().getActiveDrawModule().getID());
    }

    @Test
    public void testMenuReportSmiles() {
        restoreModelWithBasicmol();
        SwingUtilities.invokeLater(frame.menuItem("createSMILES").target::doClick);
        DialogFixture dialog = frame.dialog("smilestextdialog");
        String text = dialog.textBox("textviewdialogtextarea").text();
        Assert.assertTrue(text.contains("CCCCCCCC"));
        dialog.target.setVisible(false);
    }

    // in application mode "new" makes a new frame and does not clear
    @Ignore
    public void testMenuNew() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelWithBasicmol();
        frame.menuItem("new").target.doClick();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        Assert.assertEquals("", panel.getSmiles());
        restoreModelWithBasicmol();
    }


    @Test
    public void testMenuTemplatesAll() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        restoreModelToEmpty();
        SwingUtilities.invokeLater(frame.menuItem("pasteTemplate").target::doClick);
        DialogFixture dialog = frame.dialog("templates");
        JButtonFixture morphineButton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Morphine")));
        morphineButton.target.doClick();
        dialog.target.setVisible(false);
        frame.robot.waitForIdle();
        Assert.assertEquals(1, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(22, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
    }

    // This test seems to be problematic, the preferences are non-modal and
    // doesn't get closed - likely an issue with dialog being redisplayed
    @Test
    public void testSwitchLanguage() {
        restoreModelToEmpty();
        frame.menuItem("options").target.doClick();
        DialogFixture dialog = frame.dialog();
        JTabbedPaneFixture tabs = new JTabbedPaneFixture(dialog.robot, (JTabbedPane) dialog.robot.finder().findByName("tabs"));
        tabs.selectTab(1);
        JComboBox<?> combobox = (JComboBox<?>) dialog.robot.finder().find(new NameMatcher("language"));
        for (int i = 0; i < combobox.getItemCount(); i++) {
            if ((combobox.getItemAt(i)).equals("German")) {
                combobox.setSelectedIndex(i);
                break;
            }
        }
        JButtonFixture applybutton = new JButtonFixture(dialog.robot, (JButton) dialog.robot.finder().find(new NameMatcher("apply", true)));
        applybutton.target.doClick();

        Assert.assertEquals("Neu", frame.menuItem("new").component().getText());

        // setting the language resets the whole tree so we do some
        for (int i = 0; i < combobox.getItemCount(); i++) {
            if (((String) combobox.getItemAt(i)).equals("American English")) {
                combobox.setSelectedIndex(i);
                break;
            }
        }
        applybutton.target.doClick();
        dialog.target.setVisible(false);
        frame.robot.waitForIdle();

        Assert.assertEquals("New", frame.menuItem("new").component().getText());
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
        frame.button("select").target.doClick();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        int oldAtomCount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();

        // the C12, C13, C14 etc does appear until it is selected, so manually select it first
        frame.menuItemWithPath("Atom", "Isotopes").target.menuSelectionChanged(true);
        frame.menuItemWithPath("Atom", "Isotopes", "C" + isotopeNumber).target.doClick();

        Assert.assertEquals((Integer) isotopeNumber,
                            panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber());
        //the mode should have changed now
        Assert.assertEquals("C", panel.get2DHub().getActiveDrawModule().getID());
        Assert.assertEquals(isotopeNumber, panel.get2DHub().getController2DModel().getDrawIsotopeNumber());
        //if we click somewhere, we should get a new atom with specified properties
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(150, 100));
        Assert.assertEquals(oldAtomCount+1,
                            panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount() +
                            panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
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


    @Test public void testMenuCut(){
        restoreModelWithBasicmol();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        Assert.assertEquals(8,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        frame.menuItem("cut").click();
        Assert.assertEquals(7,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        frame.menuItem("paste").click();
        Assert.assertEquals(2,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(7,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(1,panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(4).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(4).getPoint2d().y);
        JPopupMenuFixture popup = frame.panel("renderpanel").showPopupMenuAt(new Point((int)moveto.x, (int)moveto.y));
        //try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
        popup.menuItem("cut").click();
        Assert.assertEquals(3,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        //try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
        Assert.assertEquals(1,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(4,panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        Assert.assertEquals(2,panel.getChemModel().getMoleculeSet().getAtomContainer(2).getAtomCount());
    }

    @Test
    public void testHydrogenDisplay() {
        restoreModelToEmpty();
        String smiles = "C=1(C[C@]2([C@]([H])(CC([2H])C([C@H](C)O)C2)CC1)[H])N";
        panel.setSmiles(smiles);
        frame.menuItemWithPath("Tools", "Set Hydrogen Display", "All").target.doClick();
        Assert.assertEquals("C=1(C([C@]2([C@]([H])(C(C([2H])(C([C@@](C([H])([H])[H])(O[H])[H])(C2([H])[H])[H])[H])([H])[H])C(C1[H])([H])[H])[H])([H])[H])N([H])[H]",
                            panel.getSmiles());
        frame.menuItemWithPath("Tools", "Set Hydrogen Display", "Stereocenters").target.doClick();
        Assert.assertEquals("C=1(C[C@]2([C@]([H])(CC([2H])C([C@@](C)(O)[H])C2)CC1)[H])N",
                            panel.getSmiles());
        frame.menuItemWithPath("Tools", "Set Hydrogen Display", "Preferred").target.doClick();
        Assert.assertEquals("C=1(C[C@]2([C@]([H])(CC([2H])C([C@H](C)O)C2)CC1)[H])N",
                            panel.getSmiles());
        frame.menuItemWithPath("Tools", "Set Hydrogen Display", "Minimal").target.doClick();
        Assert.assertEquals("C=1(CC2C(CC([2H])C([C@H](C)O)C2)CC1)N",
                            panel.getSmiles());
    }

    @Test
    public void testHydrogenDisplaySelectAll() {
        restoreModelToEmpty();
        String smiles = "C=1(C[C@]2([C@]([H])(CC([2H])C([C@H](C)O)C2)CC1)[H])N";
        panel.setSmiles(smiles);
        frame.menuItemWithPath("Tools", "Set Hydrogen Display", "All").target.doClick();
        Assert.assertEquals("C=1(C([C@]2([C@]([H])(C(C([2H])(C([C@@](C([H])([H])[H])(O[H])[H])(C2([H])[H])[H])[H])([H])[H])C(C1[H])([H])[H])[H])([H])[H])N([H])[H]",
                            panel.getSmiles());
        frame.menuItemWithPath("Edit", "Select All").click();
        frame.menuItemWithPath("Tools", "Set Hydrogen Display", "Minimal").target.doClick();
        Assert.assertEquals("C=1(CC2C(CC([2H])C([C@H](C)O)C2)CC1)N",
                            panel.getSmiles());
    }

    @Test
    public void testCopySmilesStereochemistry() throws IOException, UnsupportedFlavorException {
        restoreModelToEmpty();
        String smiles = "C=1(C[C@]2([C@]([H])(CC([2H])C([C@H](C)O)C2)CC1)[H])N";
        panel.setSmiles(smiles);
        frame.menuItemWithPath("Edit", "Select All").click();
        frame.menuItem("copy").target.doClick();
        Clipboard clipboard = panel.getToolkit().getSystemClipboard();
        MatcherAssert.assertThat(clipboard.getData(DataFlavor.stringFlavor).toString(),
                                 CoreMatchers.containsString("@"));
    }

}
