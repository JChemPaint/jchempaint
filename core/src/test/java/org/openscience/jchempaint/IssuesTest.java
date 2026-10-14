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

import org.fest.swing.core.ComponentDragAndDrop;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.selection.RectangleSelection;

import javax.vecmath.Point2d;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * These tests check reported issues on SourceForge / GitHub. Unfortunately
 * the SourceForge tickets are now lost so we can't alway be sure what was
 * being tested.
 *
 * @author Ralf Stephan
 * @author John Mayfield
 */
public class IssuesTest extends AbstractUITest {

    @Test public void testBug65() {
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        frame.button("hexagon").click();
        frame.click();
        frame.button("select").click();
        Point2d point = getBondPoint(panel,0);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.LEFT_BUTTON, 1);
        frame.button("eraser").click();
        point = getBondPoint(panel,2);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)point.x, (int)point.y), MouseButton.LEFT_BUTTON, 1);
        frame.button("eraser").click();

        int atomCount=0, bondCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(6, atomCount);
        Assert.assertEquals(4, bondCount);
        restoreModelToEmpty();
    }

    // JWM - note the eraser behaviour changes, it is no longer a 'mode' which
    //       but an action on a selected object
    @Test
    public void testBug75() {
        restoreModelToEmpty();

        // add one hexagon
        frame.button("hexagon").click();
        frame.click();

        // add another hexagon
        JPanelFixture renderPanel = frame.panel("renderpanel");
        Robot robot = renderPanel.robot;
        robot.click(renderPanel.component(), new Point(100, 100));

        // now we will delete the atoms one by one, select > erase > (select) > erase > etc
        JButtonFixture selectButton = frame.button("select");
        JButtonFixture eraserButton = frame.button("eraser");

        selectButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();
        robot.click(renderPanel.component(), toAwtPoint(getAtomPoint(panel, 0, 1)));
        eraserButton.click();

        Assert.assertEquals("C1CCCCC1", panel.getSmiles());

        restoreModelToEmpty();
    }

    //Test for Trac ticket # 80 : deletion of the double bonds that bind the oxygens in caffeine led to
    //                            incorrect cascading removal of the bound carbons too.
    @Test
    public void testRemoveBond() throws CDKException, ClassNotFoundException,
                                        IOException, CloneNotSupportedException {
        // Clean the panel:
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        panel.setSmiles("Cn1cnc2c1c(=O)n(C)c(=O)n2C");

/*		// Paste caffeine template:
        applet.menuItem("pasteTemplate").click();
		DialogFixture dialog = applet.dialog("templates");
		JButtonFixture templateButton = new JButtonFixture(dialog.robot,
				dialog.robot.finder().find(
						new ButtonTextComponentMatcher("Caffeine")));
		templateButton.click();
		applet.button("select").target.doClick();
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),
				new Point(0,0), MouseButton.LEFT_BUTTON,1);
*/

        //Delete the double bonds connecting the two oxygen atoms:
        frame.button("select").target.doClick();
        for (IBond bond : panel.getChemModel().getMoleculeSet().getAtomContainer(0).bonds())
            if (bond.getOrder().equals(IBond.Order.DOUBLE) &&
                (bond.getAtom(0).getSymbol().equals("O")||bond.getAtom(1).getSymbol().equals("O"))) {
                double xAvg = (bond.getAtom(0).getPoint2d().x +bond.getAtom(1).getPoint2d().x)/2;
                double yAvg = (bond.getAtom(0).getPoint2d().y +bond.getAtom(1).getPoint2d().y)/2;
                Point2d moveTo=panel.getRenderPanel().getRenderer().toScreenCoordinates(xAvg,yAvg);
                Point p = new Point((int)moveTo.x, (int)moveTo.y);
                frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(),
                                                       p, MouseButton.LEFT_BUTTON, 1);
                frame.button("eraser").target.doClick();
            }

        //Establish that only the double bonds plus the oxygens are gone, rest is still in place:
        //Issue 1, 129: make sure implicit Hs are added
        int atomCount=0, bondCount=0, implicitHCount=0;
        for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount+=atc.getAtomCount();
            bondCount+=atc.getBondCount();
        }
        Assert.assertEquals(14, atomCount); // the atoms! are not delete
        Assert.assertEquals(13, bondCount);
        Assert.assertEquals(10, implicitHCount); // should hydrogens be added in which case 14?

    }

    @Test public void testIssue4() {
        frame.button("C").target.doClick();
        frame.button("C").target.doClick();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
        frame.button("select").target.doClick();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
        frame.panel("renderpanel").robot.waitForIdle();
        try {
            frame.button("H").target.doClick();
            frame.panel("renderpanel").robot.waitForIdle();
            frame.button("select").target.doClick();
            frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
            frame.panel("renderpanel").robot.waitForIdle();
            frame.button("N").target.doClick();
            frame.panel("renderpanel").robot.waitForIdle();
            frame.button("select").target.doClick();
            frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
            frame.panel("renderpanel").robot.waitForIdle();
            frame.button("I").target.doClick();
            frame.panel("renderpanel").robot.waitForIdle();
            frame.button("select").target.doClick();
            frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
            frame.panel("renderpanel").robot.waitForIdle();
            frame.button("S").target.doClick();
            frame.panel("renderpanel").robot.waitForIdle();
            frame.button("select").target.doClick();
            frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
            frame.panel("renderpanel").robot.waitForIdle();
            frame.button("C").target.doClick();
            frame.panel("renderpanel").robot.waitForIdle();
        } catch(Exception e) {
            Assert.fail();
        }
    }

    @Test public void testIssue8() {
        restoreModelToEmpty();
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel pane = (JChemPaintPanel)jcppanel.target;
        frame.button("chain").target.doClick();
        pane.get2DHub().updateView();
        JPanelFixture renderpanel = frame.panel("renderpanel");
        renderpanel.robot.click(renderpanel.component(), new Point(100, 100));
        renderpanel.robot.pressMouse(renderpanel.component(), new Point(100, 100));
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(300, 100));
        renderpanel.robot.releaseMouseButtons();

        frame.button("H").target.doClick();
        pane.get2DHub().updateView();
        renderpanel.robot.waitForIdle();
        pane.get2DHub().mouseClickedDown(180, 100);
        renderpanel.robot.waitForIdle();
        pane.get2DHub().mouseClickedUp(180, 100);
        pane.get2DHub().updateView();
        frame.button("cleanup").click();
    }

    @Test public void testIssue10() {
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel pane = (JChemPaintPanel)jcppanel.target;
        try {
            pane.setSmiles("CCCC");
            pane.get2DHub().updateView();
        } catch (Exception e) {
            Assert.fail();
        }
        frame.button("undo").target.doClick();
        pane.get2DHub().updateView();

        int atomCount=0, bondCount=0;
        for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
            atomCount+=atc.getAtomCount();
            bondCount+=atc.getBondCount();
        }
        Assert.assertEquals(0, atomCount);
        Assert.assertEquals(0, bondCount);

        frame.button("redo").target.doClick();
        pane.get2DHub().updateView();

        atomCount=0; bondCount=0;
        for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
            atomCount+=atc.getAtomCount();
            bondCount+=atc.getBondCount();
        }
        Assert.assertEquals(4, atomCount);
        Assert.assertEquals(3, bondCount);
    }

    /**
     * @author Ralf Stephan <ralf@ark.in-berlin.de>
     * <p>
     * Tests SMILES input of a salt, the correct model and
     * error handling (#11). Depends on cdk SF#3531612.
     */
    @Test
    public void testIssue11() {
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        try {
            panel.setSmiles("[NH4+].CP(=O)(O)CCC(N)C(=O)[O-]");
            panel.get2DHub().updateView();
        } catch (Exception e) {
            Assert.fail();
        }

        int atomCount = 0, bondCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }

        // the hydrogens here are not explicit
        Assert.assertEquals(12, atomCount);
        Assert.assertEquals(10, bondCount);
    }

    @Test public void testIssue19() throws AWTException {
        java.awt.Robot robot = new java.awt.Robot();
        frame.button("C").target.doClick();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
        frame.button("select").target.doClick();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
        frame.panel("renderpanel").robot.moveMouse(100, 100);
        try {
            robot.keyPress(KeyEvent.VK_DELETE);
            robot.delay(500);
            robot.keyRelease(KeyEvent.VK_DELETE);
            robot.delay(500);
            robot.keyPress(KeyEvent.VK_DELETE);
            robot.delay(500);
            robot.keyRelease(KeyEvent.VK_DELETE);
            robot.delay(500);
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test public void testIssue32() throws AWTException {
        restoreModelToEmpty();
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        frame.button("C").click();
        frame.button("bondTool").click();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
        IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
        IAtomContainer selected = ethane.getBuilder().newInstance(IAtomContainer.class);
        selected.addAtom(ethane.getAtom(0));
        panel.getRenderPanel().getHub().deleteFragment(new AtomBondSet(selected));

        /*
        applet.button("C").click();
        applet.button("bondTool").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),new Point(100,100));
        applet.button("select").click();
		Point2d p1 = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d();
		Point2d p2 = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d();
		double d = p2.x - p1.x;
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((p1.x+p2.x)/2, p1.y+d);
        applet.panel("renderpanel").robot.moveMouse((int)moveto.x, (int)moveto.y);
        applet.panel("renderpanel").robot.pressMouse(MouseButton.LEFT_BUTTON);
        robot.delay(1000);
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(p1.x-d/2, p1.y-d);
        applet.panel("renderpanel").robot.moveMouse((int)moveto.x, (int)moveto.y);
        robot.delay(1000);
        applet.panel("renderpanel").robot.releaseMouseButtons();
*/

        int atomCount=0, hCount=0;
        for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            atomCount+=atc.getAtomCount();
            hCount+=atc.getAtom(0).getImplicitHydrogenCount().intValue();
        }
        Assert.assertEquals(1, atomCount);
        Assert.assertEquals(4, hCount);
    }

    @Test public void testIssue40() {
        restoreModelToEmpty();
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        JPanelFixture renderpanel = frame.panel("renderpanel");
        frame.button("C").target.doClick();
        frame.button("chain").target.doClick();
        robot.pressMouse(renderpanel.component(), new Point(100, 100));
        robot.moveMouse(renderpanel.component(), new Point(300, 100));
        robot.releaseMouseButtons();
        panel.get2DHub().updateView();
        Assert.assertEquals("CCCCCCCCC", panel.getSmiles());
        frame.menuItem("undo").click();
        Assert.assertEquals("", panel.getSmiles());
    }

    @Test public void testIssue58() throws AWTException {
        restoreModelToEmpty();
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        frame.button("C").target.doClick();
        frame.button("bondTool").target.doClick();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
        frame.button("select").target.doClick();
        IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
        Point2d p1 = ethane.getAtom(0).getPoint2d();
        p1 = panel.getRenderPanel().getRenderer().toScreenCoordinates(p1.x, p1.y);
        panel.get2DHub().mouseClickedDown((int)p1.x, (int)p1.y);
        frame.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp((int)p1.x, (int)p1.y);
        panel.get2DHub().updateView();
        frame.panel("renderpanel").robot.waitForIdle();
        IChemObjectSelection sel = panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();
        Assert.assertEquals(true, sel.contains(ethane.getAtom(0)));
        frame.menuItem("formalCharge").requireEnabled();

        Point2d p2 = ethane.getAtom(1).getPoint2d();
        p2 = panel.getRenderPanel().getRenderer().toScreenCoordinates(p2.x, p2.y);
        panel.get2DHub().mouseClickedDown((int)p2.x, (int)p2.y);
        frame.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp((int)p2.x, (int)p2.y);
        panel.get2DHub().updateView();
        frame.panel("renderpanel").robot.waitForIdle();
        sel = panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();
        Assert.assertEquals(true, sel.contains(ethane.getAtom(1)));
        frame.menuItem("formalCharge").requireEnabled();
    }

    @Test public void testIssue71() throws AWTException {
        restoreModelToEmpty();
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        frame.button("C").target.doClick();
        frame.button("bondTool").target.doClick();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(100, 100));
        frame.button("select").target.doClick();
        robot.waitForIdle();
        IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
        Point2d p1 = new Point2d(ethane.getAtom(0).getPoint2d());
        Renderer r = panel.getRenderPanel().getRenderer();
        p1 = r.toScreenCoordinates(p1.x, p1.y);
        panel.get2DHub().mouseClickedDown((int)(p1.x), (int)(p1.y));
        frame.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp((int)p1.x, (int)p1.y);
        panel.get2DHub().updateView();
        frame.panel("renderpanel").robot.waitForIdle();
        IChemObjectSelection sel = r.getRenderer2DModel().getSelection();
        Assert.assertTrue(sel.contains(ethane.getAtom(0)));

        double d = r.getRenderer2DModel().getSelectionRadius() / r.getRenderer2DModel().getScale();
        panel.get2DHub().mouseClickedDown((int)(p1.x+d/2), (int)(p1.y+d/2));
        frame.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().setAltInputMode(true);
        panel.get2DHub().mouseDrag((int)(p1.x+d/2), (int)(p1.y+d/2),(int)p1.x+100, (int)p1.y+100,
                                   0);
        panel.get2DHub().mouseClickedUp((int)p1.x+100, (int)p1.y+100);
        panel.get2DHub().updateView();
        panel.get2DHub().setAltInputMode(false);
        frame.panel("renderpanel").robot.waitForIdle();
        sel = panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();
        Point2d p2 = ethane.getAtom(0).getPoint2d();
        p2 = r.toScreenCoordinates(p2.x, p2.y);
        Assert.assertEquals((int)(p1.x+100), (int)p2.x);
    }

    @Test public void testIssue73() {
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        frame.button("C").target.doClick();
        frame.button("chain").target.doClick();
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().mouseDrag(100, 100, 300, 100, 0);
        panel.get2DHub().mouseClickedUp(300, 100);
        panel.get2DHub().updateView();
        frame.panel("renderpanel").robot.waitForIdle();
        Point2d p = getAtomPoint(panel,0,1);
        Assert.assertEquals(new Point2d(100,100), p);
    }

    /**
     * #76: synopsis: place a benzene; draw chain from a benzene C-atom
     * should not change this atom's position; first undo should give unaltered
     * benzene; second back to zero, without NullPointerException
     * #154: merging with chain garbles implicit Hs
     * #158: with chain, single click on atom creates undo slot, should do nothing
     *
     * @author Ralf Stephan <ralf@ark.in-berlin.de>
     */
    @Test
    public void testIssue76() {
        restoreModelToEmpty();
        frame.button("benzene").target.doClick();
        JPanelFixture renderPanel = frame.panel("renderpanel");
        robot.click(renderPanel.component(), new Point(100, 100));
        panel.get2DHub().updateView();
        renderPanel.robot.waitForIdle();

        Assert.assertEquals(6, getAtomCount(panel));
        Assert.assertEquals(6, getBondCount(panel));

        Point2d atompos = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d();
        atompos = panel.getRenderPanel().getRenderer().toScreenCoordinates(atompos.x, atompos.y);
        // C on a C will not cause a valence change so clear the highlight atom
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setHighlightedAtom(null);
        frame.button("C").target.doClick();
        frame.button("chain").target.doClick();
        robot.click(renderPanel.component(), toAwtPoint(atompos));
        renderPanel.robot.waitForIdle();
        robot.click(renderPanel.component(), toAwtPoint(atompos));
        renderPanel.robot.waitForIdle();
        robot.click(renderPanel.component(), toAwtPoint(atompos));
        renderPanel.robot.waitForIdle();
        int x = (int) atompos.x;
        int y = (int) atompos.y;
        panel.get2DHub().mouseDrag(x, y, x + 200, y, 0);
        panel.get2DHub().updateView();
        renderPanel.robot.waitForIdle();
        panel.get2DHub().mouseClickedUp(x + 200, y);
        panel.get2DHub().updateView();
        renderPanel.robot.waitForIdle();

        Assert.assertTrue(getAtomCount(panel) > 6);
        Assert.assertTrue(getBondCount(panel) > 6);

        frame.button("undo").click();
        panel.get2DHub().updateView();

        Assert.assertEquals(6, getAtomCount(panel));
        Assert.assertEquals(6, getBondCount(panel));

        try {
            frame.button("undo").click();
            panel.get2DHub().updateView();
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(0, getAtomCount(panel));
        Assert.assertEquals(0, getBondCount(panel));
        restoreModelToEmpty();
    }

    /* Tests SMILES input in general. */
    @Test public void testIssue81() {
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        try {
            panel.setSmiles("CCCC");
            panel.get2DHub().updateView();
        } catch (Exception e) {
            Assert.fail();
        }

        int atomCount=0, bondCount=0;
        for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            atomCount+=atc.getAtomCount();
            bondCount+=atc.getBondCount();
        }
        Assert.assertEquals(4, atomCount);
        Assert.assertEquals(3, bondCount);
    }

    /**
     * 1. place a C-C bond; 2. click draw chain; 3. start dragging from C and
     * notice one H too much in status bar; notice also the
     * explicit C of the merged methyl stays explicit, unexpectedly, see #103);
     * 4. clean mol will bomb with NullPointerException
     *
     * @author <ralf@ark.in-berlin.de>
     */
    // this test dragging a chain to an existing atom merges the chain on
    @Test
    public void testIssue82() throws InterruptedException {
        restoreModelToEmpty();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        JPanelFixture renderpanel = frame.panel("renderpanel");

        // we need the chain tool to line up, to do that we need to
        // draw a very particular shape by clicking and dragging the bonds
        // to snap to the correct location
        frame.button("hexagon").click();
        renderpanel.robot.click(renderpanel.component(), new Point(300, 100));
        renderpanel.robot.waitForIdle();
        frame.button("bondTool").target.doClick();
        // we want the atom at the bottom which is currently idx=3 but may change in future
        renderpanel.robot.click(renderpanel.component(), toAwtPoint(getAtomPoint(panel, 3)));
        renderpanel.robot.waitForIdle();
        Point p6 = toAwtPoint(getAtomPoint(panel, 6));
        renderpanel.robot.pressMouse(renderpanel.component(), p6);
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(p6.x - 70, p6.y + 40));
        renderpanel.robot.releaseMouseButtons();
        renderpanel.robot.waitForIdle();
        Point p7 = toAwtPoint(getAtomPoint(panel, 7));
        renderpanel.robot.pressMouse(renderpanel.component(), p7);
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(p7.x - 70, p7.y - 40));
        renderpanel.robot.releaseMouseButtons();
        renderpanel.robot.waitForIdle();

        frame.button("chain").target.doClick();
        Point startPoint = toAwtPoint(getAtomPoint(panel, 8));
        Point endPoint = toAwtPoint(getAtomPoint(panel, 4));
        renderpanel.robot.pressMouse(renderpanel.component(), startPoint);
        renderpanel.robot.waitForIdle();
        // drag up first
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(startPoint.x, startPoint.y - 50));
        // then across slightly overshooting
        renderpanel.robot.moveMouse(renderpanel.component(), new Point(endPoint.x + 2, endPoint.y));
        renderpanel.robot.releaseMouseButtons();
        renderpanel.robot.waitForIdle();

        Assert.assertEquals("C1CCC2C(C1)CCCC2", panel.getSmiles());
    }

    @Test public void testIssue116() {
        restoreModelToEmpty();
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        frame.button("H").target.doClick();
        JPanelFixture renderpanel = frame.panel("renderpanel");
        renderpanel.robot.click(renderpanel.component(), new Point(100, 100));
        Assert.assertEquals("[H][H]", panel.getSmiles());
        frame.button("O").target.doClick();
        renderpanel.robot.click(renderpanel.component(), new Point(100, 100));
        Assert.assertEquals("O", panel.getSmiles());
        frame.menuItem("undo").click();
        Assert.assertEquals("[H][H]", panel.getSmiles());
    }

    /**
     * @author Ralf Stephan <ralf@ark.in-berlin.de>
     * <p>
     * 129: Input SMILES C=C (ethylene); (with single bond active) click on
     * double bond: NOT ethyne; click on triple bond; click on quadruple
     * bond: H2C-CH2, should be H3C-CH3
     */
    @Test
    public void testIssue129() {
        restoreModelToEmpty();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        try {
            panel.setSmiles("C=C");
            panel.get2DHub().updateView();
        } catch (Exception e) {
            Assert.fail();
        }

        int atomCount = 0, bondCount = 0, implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }
        Assert.assertEquals(2, atomCount);
        Assert.assertEquals(1, bondCount);
        Assert.assertEquals(4, implicitHCount);

        IAtomContainer mol = panel.getChemModel().getMoleculeSet().getAtomContainer(0);

        frame.button("bondTool").click();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(),
                                               getBondAwtPoint(mol.getBond(0)));
        panel.get2DHub().updateView();
        frame.panel("renderpanel").robot.waitForIdle();

        frame.button("triple_bondTool").click();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(),
                                               getBondAwtPoint(mol.getBond(0)));
        panel.get2DHub().updateView();
        frame.panel("renderpanel").robot.waitForIdle();

        atomCount = 0;
        bondCount = 0;
        implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }
        Assert.assertEquals(2, atomCount);
        Assert.assertEquals(1, bondCount);
        Assert.assertEquals(2, implicitHCount);
    }

    /**
     * @author Ralf Stephan <ralf@ark.in-berlin.de>
     * <p>
     * #137: move C in ethane over the other gives CH3
     * #153: merging ethane internally does not delete bond from model
     */
    @Test
    public void testIssue137() {
        restoreModelToEmpty();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;

        JPanelFixture renderpanel = frame.panel("renderpanel");
        Robot robot = renderpanel.robot;
        frame.button("C").target.doClick();
        frame.button("bondTool").click();
        robot.click(renderpanel.component(), new Point(100, 100));
        frame.button("select").click();
        robot.waitForIdle();
        Point2d atom0p = getAtomPoint(panel, 0);
        Point2d atom1p = getAtomPoint(panel, 1);
        Assert.assertEquals("CC", panel.getSmiles());

        // select the atom
        robot.click(renderpanel.component(), toAwtPoint(atom0p));

        panel.get2DHub().setAltInputMode(true); // alt-mode needed (free move)
        robot.pressMouse(renderpanel.component(), toAwtPoint(atom0p));
        robot.moveMouse(renderpanel.component(), toAwtPoint(atom1p));
        robot.releaseMouseButtons();
        panel.get2DHub().setAltInputMode(false);
        panel.get2DHub().updateView();
        robot.waitForIdle();
        robot.waitForIdle();

        int atomCount = 0, bondCount = 0, implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }
        Assert.assertEquals(1, atomCount);
        Assert.assertEquals(0, bondCount);
        Assert.assertEquals(4, implicitHCount);
        restoreModelToEmpty();
    }

    @Test public void testIssue139() {
        restoreModelToEmpty();
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        JPanelFixture renderpanel = frame.panel("renderpanel");
        robot.click(renderpanel.component(), new Point(100,100));
        frame.button("select").click();
        IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
        RectangleSelection sel = new RectangleSelection();
        sel.addAtom(ethane.getAtom(0));
        sel.addBond(ethane.getBond(0));
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(sel);
        panel.selectionChanged();

        frame.button("cut").click();

        Assert.assertEquals(1, getAtomCount(panel));
        Assert.assertEquals(0, getBondCount(panel));
        Assert.assertEquals(4, getHydrogenCount(panel));

        frame.button("paste").click();

        Assert.assertEquals(2, getAtomCount(panel));
        Assert.assertEquals(0, getBondCount(panel));
        Assert.assertEquals(7, getHydrogenCount(panel));
        // part of the structure was copied so CH3-CH3 + CH3
        // this is because we go via MOLfile - in future via JCP objects
        // things will update as needed
    }

    @Test
    public void testSquareSelectSingleAtom() {
        restoreModelToEmpty();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        frame.button("C").target.doClick();
        Point movetopint = new Point(100, 100);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), movetopint, MouseButton.LEFT_BUTTON, 1);
        Assert.assertEquals(1, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getAtomCount());
        frame.button("select").target.doClick();
        movetopint = new Point(80, 80);
        frame.panel("renderpanel").robot.moveMouse(frame.panel("renderpanel")
                                                        .component(), movetopint);
        frame.panel("renderpanel").robot.pressMouse(MouseButton.LEFT_BUTTON);
        movetopint = new Point(85, 85); // little fix by MR, do small initial
        // drag first
        frame.panel("renderpanel").robot.moveMouse(frame.panel("renderpanel")
                                                        .component(), movetopint);
        movetopint = new Point(120, 120);
        frame.panel("renderpanel").robot.moveMouse(frame.panel("renderpanel")
                                                        .component(), movetopint);
        frame.panel("renderpanel").robot.releaseMouse(MouseButton.LEFT_BUTTON);
        Assert.assertEquals(1, panel.getRenderPanel().getRenderer()
                                    .getRenderer2DModel().getSelection()
                                    .getConnectedAtomContainer().getAtomCount());
        restoreModelToEmpty();
    }

    @Test
    public void testMove() throws InterruptedException {
        restoreModelToEmpty();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        // we draw a hexagon
        frame.button("hexagon").target.doClick();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point(100, 100), MouseButton.LEFT_BUTTON, 1);
        // select the whole molecule
        select(panel.getChemModel().getMoleculeSet().getAtomContainer(0));

        Point2d oldPoint = new Point2d(panel.getChemModel()
                                .getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d());

        frame.button("select").click();
        // switch to move mode should happen automatically
        frame.panel("renderpanel").robot.moveMouse(
                frame.panel("renderpanel").target, new Point(100, 100));
        frame.panel("renderpanel").robot.pressMouse(MouseButton.LEFT_BUTTON);
        frame.panel("renderpanel").robot.moveMouse(
                frame.panel("renderpanel").target, new Point(150, 150));
        frame.panel("renderpanel").robot.releaseMouseButtons();
        Point2d newPoint = panel.getChemModel()
                               .getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d();
        Assert.assertFalse(oldPoint.equals(newPoint));
        restoreModelToEmpty();
    }

    @Test
    public void testSetMolFile() throws CDKException {
        restoreModelToEmpty();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        panel.setMolfile("\n  CDK    1/19/07,10:3\n\n  2  1  0  0  0  0  0  0  0  0999 V2000 \n  2.520000 10.220000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n  2.270000 10.470000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n  2  1  1  0  0  0  0 \nM  END");
        Assert.assertEquals(2, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getAtomCount());
        restoreModelToEmpty();
    }

    @Test
    public void testGetMolFile() throws CDKException {
        restoreModelToEmpty();
        frame.button("hexagon").target.doClick();
        Point movetopint = new Point(100, 100);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), movetopint, MouseButton.LEFT_BUTTON, 1);
        Assert.assertTrue(panel.getMolfile().indexOf(
                "6  6  0  0  0  0  0  0  0  0999 V2000") > 0);
        restoreModelToEmpty();
    }

    @Test
    public void testBug2858663() {
        restoreModelToEmpty();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        frame.button("bondTool").target.doClick();
        frame.click();
        Assert.assertEquals(2, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getAtomCount());
        Assert.assertEquals(1, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getBondCount());
        restoreModelToEmpty();
    }

    // @cdk.bug 2859344 /6
    @Test
    public void overwriteStereo() {
        restoreModelToEmpty();
        // we draw a hexagon
        frame.button("hexagon").target.doClick();
        frame.click();
        // one of its bonds becomes an up bond
        frame.button("up_bond").target.doClick();
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
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
                                                  .getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        // if we make that down, it must be down (and not donw_inv)
        frame.button("down_bond").target.doClick();
        moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(
                (panel.getChemModel().getMoleculeSet().getAtomContainer(0)
                      .getAtom(2).getPoint2d().x + panel.getChemModel()
                                                        .getMoleculeSet().getAtomContainer(0).getAtom(3)
                                                        .getPoint2d().x) / 2,
                (panel.getChemModel().getMoleculeSet().getAtomContainer(0)
                      .getAtom(2).getPoint2d().y + panel.getChemModel()
                                                        .getMoleculeSet().getAtomContainer(0).getAtom(3)
                                                        .getPoint2d().y) / 2);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
                                                    .getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        // and up again
        frame.button("up_bond").target.doClick();
        moveto = panel.getRenderPanel().getRenderer().toScreenCoordinates(
                (panel.getChemModel().getMoleculeSet().getAtomContainer(0)
                      .getAtom(2).getPoint2d().x + panel.getChemModel()
                                                        .getMoleculeSet().getAtomContainer(0).getAtom(3)
                                                        .getPoint2d().x) / 2,
                (panel.getChemModel().getMoleculeSet().getAtomContainer(0)
                      .getAtom(2).getPoint2d().y + panel.getChemModel()
                                                        .getMoleculeSet().getAtomContainer(0).getAtom(3)
                                                        .getPoint2d().y) / 2);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
                                                  .getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
    }

    @Test
    public void testUpBond() {
        restoreModelToEmpty();
        genericStereoBondTest(IBond.Stereo.UP);
    }

    @Test
    public void testDownBond() {
        restoreModelToEmpty();
        genericStereoBondTest(IBond.Stereo.DOWN);
    }

    @Test
    public void testUndefinedBond() {
        restoreModelToEmpty();
        genericStereoBondTest(IBond.Stereo.UP_OR_DOWN);
    }

    // @cdk.bug 2859344 /7
    @Test
    public void testUndefinedEzBond() {
        restoreModelToEmpty();
        genericStereoBondTest(IBond.Stereo.E_OR_Z);
    }

    @Test
    public void testNoneBond() {
        restoreModelToEmpty();
        genericStereoBondTest(IBond.Stereo.NONE);
    }

    // @cdk.bug 2860015
    @Test
    public void testBug2860015() {
        restoreModelToEmpty();
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        frame.button("bondTool").click();
        frame.click();
        frame.click();
        frame.click();
        frame.click();
        Point2d moveto = panel.getRenderPanel().getRenderer()
                              .toScreenCoordinates(
                                      panel.getChemModel().getMoleculeSet().getAtomContainer(
                                              0).getAtom(3).getPoint2d().x,
                                      panel.getChemModel().getMoleculeSet().getAtomContainer(
                                              0).getAtom(3).getPoint2d().y);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);

        Assert.assertEquals("C(C)(C)(CC)C", panel.getSmiles());

        // now delete 2 bonds

        Assert.assertEquals(6, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getAtomCount());
        Assert.assertEquals(5, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getBondCount());
        moveto = getBondPoint(panel, 0);
        panel.getRenderPanel().getRenderer().getRenderer2DModel()
             .setHighlightedAtom(null);

        // applet.moveTo(new Point(100, 100));
        frame.button("select").target.doClick();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);
        frame.button("eraser").target.doClick();

        Assert.assertEquals(5, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getAtomCount());
        Assert.assertEquals(4, panel.getChemModel().getMoleculeSet()
                                    .getAtomContainer(0).getBondCount());
        moveto = getBondPoint(panel, 1);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);
        frame.button("eraser").target.doClick();

        Assert.assertEquals("[CH3].[C](C)C.[CH2]C", panel.getSmiles());
        restoreModelToEmpty();
    }

    // This is a test for overwriting of stereo bonds. Any stereo bond
    // must overwrite all others and flip itself.
    private void genericStereoBondTest(IBond.Stereo directionToTest) {
        // we draw a hexagon
        frame.button("hexagon").click();
        frame.click();
        // we make all bond types in there
        frame.button("up_bond").target.doClick();
        JPanelFixture renderpanel = frame.panel("renderpanel");
        renderpanel.robot.click(renderpanel.component(), toAwtPoint(getBondPoint(panel, 1)));
        IAtomContainer atomContainer = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(IBond.Stereo.UP, atomContainer.getBond(1).getStereo());
        frame.button("down_bond").target.doClick();
        renderpanel.robot.click(renderpanel.component(), toAwtPoint(getBondPoint(panel, 2)));
        Assert.assertEquals(IBond.Stereo.DOWN_INVERTED, atomContainer.getBond(2).getStereo());
        frame.button("undefined_bond").target.doClick();
        renderpanel.robot.click(renderpanel.component(), toAwtPoint(getBondPoint(panel, 3)));
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, atomContainer.getBond(3).getStereo());
        frame.button("undefined_stereo_bond").target.doClick();
        renderpanel.robot.click(renderpanel.component(), toAwtPoint(getBondPoint(panel, 4)));
        Assert.assertEquals(IBond.Stereo.E_OR_Z, atomContainer.getBond(4).getStereo());
        // now we click on all of them with disired bond
        if (directionToTest == IBond.Stereo.UP)
            frame.button("up_bond").target.doClick();
        if (directionToTest == IBond.Stereo.DOWN)
            frame.button("down_bond").target.doClick();
        if (directionToTest == IBond.Stereo.UP_OR_DOWN)
            frame.button("undefined_bond").target.doClick();
        if (directionToTest == IBond.Stereo.E_OR_Z)
            frame.button("undefined_stereo_bond").target.doClick();
        if (directionToTest == IBond.Stereo.NONE)
            frame.button("bondTool").target.doClick();
        for (int i = 0; i < 5; i++) {
            boolean self = false;
            if (atomContainer.getBond(i).getStereo() == directionToTest)
                self = true;
            renderpanel.robot.click(renderpanel.component(), toAwtPoint(getBondPoint(panel, i)));
            if (self) {
                IBond.Stereo desiredDirection = null;
                if (directionToTest == IBond.Stereo.E_OR_Z)
                    desiredDirection = IBond.Stereo.E_OR_Z;
                else if (directionToTest == IBond.Stereo.NONE)
                    desiredDirection = IBond.Stereo.E_Z_BY_COORDINATES;
                else if (directionToTest == IBond.Stereo.DOWN)
                    desiredDirection = IBond.Stereo.DOWN_INVERTED;
                else if (directionToTest == IBond.Stereo.UP)
                    desiredDirection = IBond.Stereo.UP_INVERTED;
                else if (directionToTest == IBond.Stereo.UP_OR_DOWN)
                    desiredDirection = IBond.Stereo.UP_OR_DOWN;
                Assert.assertEquals(desiredDirection, atomContainer.getBond(i)
                                                                   .getStereo());
            } else {
                Assert.assertEquals(directionToTest, atomContainer.getBond(i)
                                                                  .getStereo());
            }
        }
        restoreModelToEmpty();
    }

    @Test
    public void testFlipWithStereo() {
        JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        frame.button("hexagon").target.doClick();
        frame.click();
        frame.button("up_bond").target.doClick();
        Point2d moveto = getAtomPoint(panel, 0);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);
        moveto = getAtomPoint(panel, 1);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);
        frame.button("down_bond").target.doClick();
        moveto = getAtomPoint(panel, 2);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel")
                                                    .component(), new Point((int) moveto.x, (int) moveto.y),
                                               MouseButton.LEFT_BUTTON, 1);
        frame.button("flipHorizontal").target.doClick();
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
                                                    .getMoleculeSet().getAtomContainer(0).getBond(6).getStereo());
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
                                                    .getMoleculeSet().getAtomContainer(0).getBond(7).getStereo());
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
                                                  .getMoleculeSet().getAtomContainer(0).getBond(8).getStereo());
        frame.button("flipVertical").target.doClick();
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
                                                  .getMoleculeSet().getAtomContainer(0).getBond(6).getStereo());
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel()
                                                  .getMoleculeSet().getAtomContainer(0).getBond(7).getStereo());
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel()
                                                    .getMoleculeSet().getAtomContainer(0).getBond(8).getStereo());
        restoreModelToEmpty();
    }


}
