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

import org.assertj.swing.core.MouseButton;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.renderer.Renderer;

import javax.vecmath.Point2d;
import java.awt.Point;

public class AbbreviationTest extends AbstractUITest {

    private void setupAbbreiviationMol() {
        restoreModelToEmpty();
        String molfile = "OP(OPh)3\n" +
               "  CDK     11212619342D\n" +
               "\n" +
               " 23 25  0  0  0  0  0  0  0  0999 V2000\n" +
               "    2.8346   -2.7512    0.0000 C   0  0\n" +
               "    5.0846   -5.1485    0.0000 O   0  0\n" +
               "    6.5845   -6.6483    0.0000 O   0  0\n" +
               "    9.5846   -4.0502    0.0000 C   0  0\n" +
               "    3.5845   -4.0502    0.0000 C   0  0\n" +
               "    1.1875   -4.3983    0.0000 C   0  0\n" +
               "    8.8346   -5.3493    0.0000 C   0  0\n" +
               "    2.4865   -5.1483    0.0000 C   0  0\n" +
               "    8.8345   -2.7512    0.0000 C   0  0\n" +
               "    1.3346   -2.7512    0.0000 C   0  0\n" +
               "    7.3345   -5.3492    0.0000 C   0  0\n" +
               "    5.0846   -8.1484    0.0000 O   0  0\n" +
               "    3.7855   -4.3985    0.0000 C   0  0\n" +
               "    3.7855   -2.8984    0.0000 C   0  0\n" +
               "    6.5846   -4.0503    0.0000 C   0  0\n" +
               "    1.3346   -5.3493    0.0000 C   0  0\n" +
               "    2.8345   -5.3492    0.0000 C   0  0\n" +
               "    0.5846   -4.0502    0.0000 C   0  0\n" +
               "    7.3346   -2.7512    0.0000 C   0  0\n" +
               "    3.5846   -6.6483    0.0000 O   0  0\n" +
               "    2.4865   -2.1484    0.0000 C   0  0\n" +
               "    5.0846   -6.6483    0.0000 P   0  0\n" +
               "    1.1874   -2.8985    0.0000 C   0  0\n" +
               " 22 12  2  0\n" +
               " 20 22  1  0\n" +
               " 11  7  2  0\n" +
               "  6  8  2  0\n" +
               "  1  5  1  0\n" +
               "  3 11  1  0\n" +
               " 14 13  2  0\n" +
               "  8 13  1  0\n" +
               " 21 23  2  0\n" +
               "  9 19  1  0\n" +
               " 17  5  2  0\n" +
               " 14 21  1  0\n" +
               " 18 10  1  0\n" +
               " 17 16  1  0\n" +
               " 20 17  1  0\n" +
               " 22  3  1  0\n" +
               " 23  6  1  0\n" +
               " 16 18  2  0\n" +
               " 10  1  2  0\n" +
               "  2 13  1  0\n" +
               "  7  4  1  0\n" +
               "  9  4  2  0\n" +
               " 15 11  1  0\n" +
               " 22  2  1  0\n" +
               " 19 15  2  0\n" +
               "M  STY  3   1 SUP   2 SUP   3 SUP\n" +
               "M  SAL   1  6  13  14  21  23   6   8\n" +
               "M  SBL   1  1  20\n" +
               "M  SMT   1 Ph\n" +
               "M  SAL   2  6  11   9  19  15   7   4\n" +
               "M  SBL   2  1   6\n" +
               "M  SMT   2 Ph\n" +
               "M  SAL   3  6  17  16  18  10   1   5\n" +
               "M  SBL   3  1  15\n" +
               "M  SMT   3 Ph\n" +
               "M  END\n" +
               "$$$$\n";
        GuiActionRunner.execute(() -> {
            panel.setMolfile(molfile);
        });
        panel.get2DHub().updateView();
        robot.waitForIdle();
    }

    @Test
    public void testExpandContract() {
        setupAbbreiviationMol();
        assertHiddenAtoms(3*5);
        JPanelFixture renderpanel = frame.panel("renderpanel");
        Point p10 = toAwtPoint(getAtomPoint(panel, 10));
        renderpanel.showPopupMenuAt(p10).menuItem("expand").click();
        assertHiddenAtoms(2*5);
        Point p12 = toAwtPoint(getAtomPoint(panel, 12));
        renderpanel.showPopupMenuAt(p12).menuItem("expand").click();
        assertHiddenAtoms(5);
        Point p16 = toAwtPoint(getAtomPoint(panel, 16));
        renderpanel.showPopupMenuAt(p16).menuItem("expand").click();
        assertHiddenAtoms(0);

        // recontract
        renderpanel.showPopupMenuAt(p10).menuItem("contract").click();
        assertHiddenAtoms(5);
        renderpanel.showPopupMenuAt(p12).menuItem("contract").click();
        assertHiddenAtoms(2*5);
        renderpanel.showPopupMenuAt(p16).menuItem("contract").click();
        assertHiddenAtoms(3*5);
    }

    @Test
    public void testExpandContractUndoRedo() {
        setupAbbreiviationMol();
        assertHiddenAtoms(3*5);
        JPanelFixture renderpanel = frame.panel("renderpanel");
        renderpanel.showPopupMenuAt(toAwtPoint(getAtomPoint(panel, 10)))
                   .menuItem("expand").click();
        assertHiddenAtoms(2*5);
        frame.menuItem("undo").click();
        assertHiddenAtoms(3*5);
        frame.menuItem("redo").click();
        assertHiddenAtoms(2*5);
    }

    @Test
    public void testMoveContractedAtom() {
        setupAbbreiviationMol();
        assertHiddenAtoms(3*5);
        frame.button("select").click();
        Point p = toAwtPoint(getAtomPoint(panel, 10));
        // atom 3,6,8,14,18 are a contracted phenyl group
        Point2d p6 = getAtomPoint(panel, 6);
        Point2d p14 = getAtomPoint(panel, 14);
        robot.click(panel.renderPanel, p);
        

        // only one atom clicked... but we picked up 6 atoms/6 bonds because
        // they are hidden
        IChemObjectSelection selection = panel.getRenderPanel().getRendererModel().getSelection();
        Assert.assertEquals(6, selection.elements(IAtom.class).size());
        Assert.assertEquals(6, selection.elements(IBond.class).size());

        robot.pressMouse(panel.renderPanel, p, MouseButton.LEFT_BUTTON);
        robot.moveMouse(panel.renderPanel, p.x + 15, p.y + 5);
        robot.releaseMouseButtons();
        assertDifferent(p6, getAtomPoint(panel, 6));
        assertDifferent(p14, getAtomPoint(panel, 14));
        // show the person running the test
        JPanelFixture renderpanel = frame.panel("renderpanel");
        renderpanel.showPopupMenuAt(toAwtPoint(getAtomPoint(panel, 10)))
                   .menuItem("expand").click();
    }

    @Test
    public void testMoveContractedAtomAlt() {
        setupAbbreiviationMol();
        assertHiddenAtoms(3*5);
        frame.button("select").click();
        Point p = toAwtPoint(getAtomPoint(panel, 10));
        // atom 3,6,8,14,18 are a contracted phenyl group
        Point2d p6 = getAtomPoint(panel, 6);
        Point2d p14 = getAtomPoint(panel, 14);
        robot.click(panel.renderPanel, p);
        

        // only one atom clicked... but we picked up 6 atoms/6 bonds because
        // they are hidden
        IChemObjectSelection selection = panel.getRenderPanel().getRendererModel().getSelection();
        Assert.assertEquals(6, selection.elements(IAtom.class).size());
        Assert.assertEquals(6, selection.elements(IBond.class).size());

        //robot().pressKey(KeyEvent.VK_ALT);
        panel.get2DHub().setAltInputMode(true); // to remove
        robot.pressMouse(panel.renderPanel, p, MouseButton.LEFT_BUTTON);
        robot.moveMouse(panel.renderPanel, p.x + 40, p.y + 5);
        robot.releaseMouseButtons();
        //robot().releaseKey(KeyEvent.VK_ALT);
        panel.get2DHub().setAltInputMode(false); // to remove
        assertDifferent(p6, getAtomPoint(panel, 6));
        assertDifferent(p14, getAtomPoint(panel, 14));
        // show the person running the test
        JPanelFixture renderpanel = frame.panel("renderpanel");
        renderpanel.showPopupMenuAt(toAwtPoint(getAtomPoint(panel, 10)))
                   .menuItem("expand").click();
    }

    @Test
    public void testNestedAbbreviations() {
        setupAbbreiviationMol();
        assertHiddenAtoms(3*5);
        frame.button("select").click();
        Point carbon = toAwtPoint(getAtomPoint(panel, 10)); // -C≤
        Point oxygen = toAwtPoint(getAtomPoint(panel, 2)); // -O-
        robot.pressMouse(panel.renderPanel,
                         new Point(carbon.x - 16, carbon.y - 16),
                         MouseButton.LEFT_BUTTON);
        robot.moveMouse(panel.renderPanel,
                        new Point(oxygen.x + 15, oxygen.y + 15));
        robot.releaseMouseButtons();
        

        IChemObjectSelection selection = panel.getRenderPanel().getRendererModel().getSelection();
        Assert.assertEquals(7, selection.elements(IAtom.class).size());
        Assert.assertEquals(7, selection.elements(IBond.class).size());

        // click off some bit to get the correct menu
        JPanelFixture renderpanel = frame.panel("renderpanel");
        robot.click(renderpanel.target(), new Point(carbon.x - 16, carbon.y), MouseButton.RIGHT_BUTTON, 1);
        frame.menuItem("abbreviate").click();
        DialogFixture dialog = frame.dialog();
        dialog.textBox().setText("OPh");
        dialog.button("ok").click();

        robot.waitForIdle();

        // -O-Ph -> -OPh
        assertHiddenAtoms(3*5+1);
        robot.click(renderpanel.target(), oxygen, MouseButton.RIGHT_BUTTON, 1);
        frame.menuItem("expand").click();
        assertHiddenAtoms(3*5); // -OPh -> -O-Ph
        robot.click(renderpanel.target(), carbon, MouseButton.RIGHT_BUTTON, 1);
        frame.menuItem("expand").click();
        assertHiddenAtoms(2*5); // -O-Ph -> -O-c1ccccc1
        robot.click(renderpanel.target(), oxygen, MouseButton.RIGHT_BUTTON, 1);
        frame.menuItem("contract").click();
        assertHiddenAtoms(3*5+1); // -O-c1ccccc1 -> -OPh
        robot.click(renderpanel.target(), oxygen, MouseButton.RIGHT_BUTTON, 1);
        frame.menuItem("expand").click();
        assertHiddenAtoms(2*5); // -OPh -> -O-c1ccccc1
    }

    @Test
    public void testMerge() {
        restoreModelToEmpty();
        GuiActionRunner.execute(() -> panel.setMolfile("\n" +
                                                       "  CDK     11242610012D\n" +
                                                       "\n" +
                                                       "  8  8  0  0  0  0  0  0  0  0999 V2000\n" +
                                                       "   11.5517   -9.7644    0.0000 C   0  0\n" +
                                                       "   12.8508  -12.0144    0.0000 C   0  0\n" +
                                                       "   10.2527  -12.0144    0.0000 C   0  0\n" +
                                                       "   10.2527  -10.5144    0.0000 C   0  0\n" +
                                                       "   11.5517  -12.7644    0.0000 C   0  0\n" +
                                                       "    7.6546  -10.5144    0.0000 C   0  0\n" +
                                                       "    8.9536   -9.7644    0.0000 O   0  0\n" +
                                                       "   12.8508  -10.5144    0.0000 C   0  0\n" +
                                                       "  8  2  1  0\n" +
                                                       "  4  1  1  0\n" +
                                                       "  5  3  1  0\n" +
                                                       "  3  4  2  0\n" +
                                                       "  1  8  2  0\n" +
                                                       "  4  7  1  0\n" +
                                                       "  7  6  1  0\n" +
                                                       "  2  5  2  0\n" +
                                                       "M  STY  1   1 SUP\n" +
                                                       "M  SAL   1  2   6   7\n" +
                                                       "M  SBL   1  1   6\n" +
                                                       "M  SMT   1 OMe\n" +
                                                       "M  END\n" +
                                                       "$$$$\n"));
        GuiActionRunner.execute(() -> panel.addMolfile("\n" +
                                                      "  CDK     11242610012D\n" +
                                                      "\n" +
                                                      "  9  9  0  0  0  0  0  0  0  0999 V2000\n" +
                                                      "   21.0810  -10.6293    0.0000 C   0  0\n" +
                                                      "   17.1839   -9.8793    0.0000 C   0  0\n" +
                                                      "   15.8849  -10.6293    0.0000 C   0  0\n" +
                                                      "   15.8849  -12.1293    0.0000 C   0  0\n" +
                                                      "   22.3801   -9.8793    0.0000 C   0  0\n" +
                                                      "   18.4829  -10.6293    0.0000 C   0  0\n" +
                                                      "   17.1839  -12.8793    0.0000 C   0  0\n" +
                                                      "   19.7820   -9.8793    0.0000 O   0  0\n" +
                                                      "   18.4829  -12.1293    0.0000 C   0  0\n" +
                                                      "  9  7  2  0\n" +
                                                      "  4  3  2  0\n" +
                                                      "  8  1  1  0\n" +
                                                      "  1  5  1  0\n" +
                                                      "  7  4  1  0\n" +
                                                      "  2  6  2  0\n" +
                                                      "  6  9  1  0\n" +
                                                      "  6  8  1  0\n" +
                                                      "  3  2  1  0\n" +
                                                      "M  STY  1   1 SUP\n" +
                                                      "M  SAL   1  3   1   8   5\n" +
                                                      "M  SBL   1  1   8\n" +
                                                      "M  SMT   1 OEt\n" +
                                                      "M  END\n" +
                                                      "$$$$\n"));
        Assert.assertEquals(2, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        panel.get2DHub().updateView();


        frame.button("select").click();
        JPanelFixture renderpanel = frame.panel("renderpanel");

        // double click to select whole fragment
        robot.click(renderpanel.target(), toAwtPoint(getAtomPoint(panel, 0)),
                    MouseButton.LEFT_BUTTON, 2);

        // move on structure to merge with the other, we use min/max bounds
        // to work out how far to move
        Renderer renderer = panel.getRenderPanel().getRenderer();
        double[] minMaxA = GeometryUtil.getMinMax(panel.getChemModel().getMoleculeSet().getAtomContainer(0));
        double[] minMaxB = GeometryUtil.getMinMax(panel.getChemModel().getMoleculeSet().getAtomContainer(1));
        double centerX = (minMaxA[2] + minMaxA[0]) / 2;
        double centerY = (minMaxA[3] + minMaxA[1]) / 2;
        robot.pressMouse(renderpanel.target(), toAwtPoint(renderer.toScreenCoordinates(centerX, centerY)),
                         MouseButton.LEFT_BUTTON);
        robot.moveMouse(renderpanel.target(), toAwtPoint(renderer.toScreenCoordinates(centerX - (minMaxA[2] - minMaxB[0]), centerY)));
        robot.releaseMouseButtons();

        Assert.assertEquals(1, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(2, Sgroup.get(panel.getChemModel().getMoleculeSet().getAtomContainer(0)).size());
        frame.menuItem("undo").click();
        Assert.assertEquals(2, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(1, Sgroup.get(panel.getChemModel().getMoleculeSet().getAtomContainer(1)).size());
        Assert.assertEquals(1, Sgroup.get(panel.getChemModel().getMoleculeSet().getAtomContainer(1)).size());
        frame.menuItem("redo").click();
        Assert.assertEquals(1, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(2, Sgroup.get(panel.getChemModel().getMoleculeSet().getAtomContainer(0)).size());
        frame.menuItem("undo").click();
        Assert.assertEquals(2, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(1, Sgroup.get(panel.getChemModel().getMoleculeSet().getAtomContainer(1)).size());
        Assert.assertEquals(1, Sgroup.get(panel.getChemModel().getMoleculeSet().getAtomContainer(1)).size());
    }

    static void assertHiddenAtoms(int expected) {
        int actual = 0;
        for (IAtomContainer container : ChemModelManipulator.getAllAtomContainers(panel.getChemModel())) {
            for (IAtom atom : container.atoms()) {
                if (atom.is(IChemObject.HIDDEN))
                    actual++;
            }
        }
        Assert.assertEquals(expected, actual);
    }

    static void assertDifferent(Point2d a, Point2d b) {
        String mesg = String.format("actual (%.4f,%.4f) != expected (%.4f,%.4f)",
                                    a.x, a.y, b.x, b.y);
        Assert.assertNotEquals(mesg,
                               b.x, a.x, 0.1);
        Assert.assertNotEquals(mesg,
                               b.y, a.y, 0.1);
    }
}
