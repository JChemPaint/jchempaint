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
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

import javax.swing.SwingUtilities;
import javax.vecmath.Point2d;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;

/**
 * These tests check expected drawing behaviour when interacting withe
 * canvas/render panel.
 *
 * @author John Mayfield
 */
public class DrawingTest extends AbstractUITest {

	@Test public void testAddBond() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//using bond button
		frame.button("bondTool").click();
		//can we add a new bond?
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//can we convert bond order?
		int oldhydrogencount0 = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getImplicitHydrogenCount().intValue();
		int oldhydrogencount1 = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getImplicitHydrogenCount().intValue();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(IBond.Order.DOUBLE, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getOrder());
		Assert.assertEquals(oldhydrogencount0-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getImplicitHydrogenCount().intValue());
		Assert.assertEquals(oldhydrogencount1-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getImplicitHydrogenCount().intValue());
		//using up bond button,
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4)));
		frame.button("up_bond").click();
		//was the selected bond changed
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
		//can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
		//can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using down bond button,
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4)));
		frame.button("down_bond").click();
        //was the selected bond changed
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using undefined bond button,
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4)));
		frame.button("undefined_bond").click();
        //was the selected bond changed
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+4, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using undefined stereo button,		
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4)));
		frame.button("undefined_stereo_bond").click();
	    //was the selected bond changed
        Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
        Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
	}
	
	@Test public void testElement() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    //we go into bond_down mode to see that hitting O button actually changes activeDrawModule
	    restoreModelWithBasicmol();
	    frame.button("down_bond").target.doClick();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		robot.waitForIdle();
		frame.button("O").target.doClick(); // click() doesn't work?
		robot.waitForIdle();
		Assert.assertEquals("O",panel.get2DHub().getActiveDrawModule().getID());

		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals("O",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());

		frame.button("bondTool").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("O",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
		Assert.assertEquals("O",panel.get2DHub().getController2DModel().getDrawElement());
	}

	@Test public void testPeriodictable() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		SwingUtilities.invokeLater(frame.button("periodictable").target::doClick);
		DialogFixture dialog = frame.dialog();
		dialog.button("Li").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals("Li",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		frame.button("bondTool").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("Li",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
		frame.button("C").target.doClick();
		restoreModelToEmpty();
	}

	@Test public void testEnterElementOnSelection() {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		frame.button("select").click();
		Point2d atom0 = getAtomPoint(panel, 0);
		JPanelFixture renderpanel = frame.panel("renderpanel");
		renderpanel.robot.click(renderpanel.component(),
								toAwtPoint(atom0));
		SwingUtilities.invokeLater(frame.button("enterelement").target::doClick);
		DialogFixture dialog = frame.dialog();
		dialog.textBox().setText("U");
		dialog.button("ok").click();
		robot.waitForIdle();
		Assert.assertEquals("U",
							panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		frame.button("bondTool").click();
		renderpanel.robot.click(renderpanel.component(),
								toAwtPoint(atom0));
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("C",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
	}

	@Test public void testEnterElementNoSelection() {
		restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		Point2d atom0 = getAtomPoint(panel, 0);
		JPanelFixture renderpanel = frame.panel("renderpanel");
		SwingUtilities.invokeLater(frame.button("enterelement").target::doClick);
		renderpanel.robot.click(renderpanel.component(),
								toAwtPoint(atom0));
		DialogFixture dialog = frame.dialog();
		dialog.textBox().setText("U");
		dialog.button("ok").click();
		robot.waitForIdle();
		// the draw element should have been updated
		Assert.assertEquals("U",
							panel.get2DHub().getController2DModel().getDrawElement());
		Assert.assertEquals("U",
							panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		frame.button("bondTool").click();
		renderpanel.robot.click(renderpanel.component(),
								toAwtPoint(atom0));
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("U",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
	}

	@Test public void testTriangleAltMode() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a triangle to an atom
		frame.button("triangle").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);
		panel.get2DHub().setAltInputMode(true);
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		panel.get2DHub().setAltInputMode(false);
		restoreModelWithBasicmol();
	}

	@Test public void testPentagonAltMode() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a pentagon to an atom
		frame.button("pentagon").click();
		panel.get2DHub().setAltInputMode(true);
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+4, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+7, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		panel.get2DHub().setAltInputMode(false);
		restoreModelWithBasicmol();
	}

	// to make a spiro we need alt-mode
	@Test public void testHexagonAltMode() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a hexagon to an atom
		frame.button("hexagon").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);
		panel.get2DHub().setAltInputMode(true);
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+9, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		panel.get2DHub().setAltInputMode(false);
		restoreModelWithBasicmol();
	}

	@Test public void testHexagon() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a hexagon to an atom
		frame.button("hexagon").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+6, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+10, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModelWithBasicmol();
	}


	@Test public void testOctagonAltMode() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a octagon to an atom
		frame.button("octagon").click();
		panel.get2DHub().setAltInputMode(true);
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+7, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+13, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		panel.get2DHub().setAltInputMode(false);
		restoreModelWithBasicmol();
	}

	@Test public void testBenzeneAltMode() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a benzene to an atom
		panel.get2DHub().setAltInputMode(true);
		frame.button("benzene").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+9, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		panel.get2DHub().setAltInputMode(false);
		restoreModelWithBasicmol();
	}

	@Test public void testBenzene() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a benzene to an atom
		frame.button("benzene").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+6, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+10, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModelWithBasicmol();
	}


	@Test public void testSquareAltMode() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a square to an atom
		frame.button("square").click();
		panel.get2DHub().setAltInputMode(true);
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		panel.get2DHub().setAltInputMode(false);
		restoreModelWithBasicmol();
	}
	
	@Test public void testDelete() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
        JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
        int oldBondCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount();
        //we delete an atom
        frame.button("select").click();
        Point2d moveto=getAtomPoint(panel, 0);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		frame.button("eraser").click();
        Assert.assertEquals(oldAtomCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(oldBondCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
        //and a (terminal) bond
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y)/2);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
		frame.button("eraser").click();
        Assert.assertEquals(2, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(oldAtomCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        Assert.assertEquals(oldBondCount-2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getBondCount());
        restoreModelWithBasicmol();
    }

	// The double-click functionality was removed. Commented out
	/*
	@Test public void selectByDoubleClick(){
	    restoreModelWithBasicmol();
        JPanelFixture jcppanel=applet.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
        //we add a hexagon
        applet.button("hexagon").click();
        Point2d moveto=new Point2d(200,100);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        
        Assert.assertEquals(oldAtomCount+6, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        applet.button("select").click();
        //double click on atom
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,2);
        Assert.assertEquals(oldAtomCount, panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().getAtomCount());
	}*/

	@Test
	public void mergeAndUndoRedo() {
		restoreModelWithBasicmol();
		JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		int oldAtomCount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		frame.button("select").click();
		Point2d atom0 = getAtomPoint(panel, 0);
		Point2d atom2 = getAtomPoint(panel, 2);
		JPanelFixture renderpanel = frame.panel("renderpanel");
		renderpanel.robot.click(renderpanel.component(), toAwtPoint(atom0));
		renderpanel.robot.pressMouse(renderpanel.component(), toAwtPoint(atom0));
		renderpanel.robot.moveMouse(toAwtPoint(atom2));
		renderpanel.robot.releaseMouseButtons();
		Assert.assertEquals(oldAtomCount - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		frame.button("undo").click();
		Assert.assertEquals(oldAtomCount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		frame.button("redo").click();
		Assert.assertEquals(oldAtomCount - 1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
	}

	@Test
	public void mergeAndUndoRedoAlt() {
		restoreModelWithBasicmol();
		JPanelFixture jcppanel = frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		int oldAtomCount = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		frame.button("select").click();
		Point2d atom0 = getAtomPoint(panel, 0);
		Point2d atom1 = getAtomPoint(panel, 2);
		JPanelFixture renderpanel = frame.panel("renderpanel");
		renderpanel.robot.click(renderpanel.component(), toAwtPoint(atom0));
		renderpanel.robot.pressMouse(renderpanel.component(), toAwtPoint(atom0));
		renderpanel.robot.moveMouse(toAwtPoint(atom1));
		renderpanel.robot.pressKey(KeyEvent.VK_ALT);
		renderpanel.robot.releaseMouseButtons();
		renderpanel.robot.releaseKey(KeyEvent.VK_ALT);
		Assert.assertEquals(oldAtomCount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		frame.button("undo").click();
		Assert.assertEquals(oldAtomCount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		frame.button("redo").click();
		Assert.assertEquals(oldAtomCount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
	}


    @Test public void drawReactions(){
		restoreModelToEmpty();
		JPanelFixture jcppanel= frame.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		frame.button("hexagon").click();
		JPanelFixture renderpanel = frame.panel("renderpanel");
		renderpanel.robot.click(renderpanel.component(), new Point(100, 50), MouseButton.LEFT_BUTTON, 1);
		renderpanel.robot.click(renderpanel.component(), new Point(400, 50), MouseButton.LEFT_BUTTON, 1);
		renderpanel.robot.click(renderpanel.component(), new Point(0, 0), MouseButton.RIGHT_BUTTON, 1);
		frame.button("reactionArrow").click();
		renderpanel.robot.pressMouse(renderpanel.component(), new Point(150, 50));
		renderpanel.robot.moveMouse(renderpanel.component(), new Point(350, 50));
		renderpanel.robot.releaseMouseButtons();
		Assert.assertEquals(1, panel.getChemModel().getReactionSet().getReactionCount());
		Assert.assertEquals(1, panel.getChemModel().getReactionSet().getReaction(0).getReactantCount());
		Assert.assertEquals(1, panel.getChemModel().getReactionSet().getReaction(0).getProductCount());
		frame.button("hexagon").click();
		renderpanel.robot.click(renderpanel.component(), new Point(500, 50), MouseButton.LEFT_BUTTON, 1);
		renderpanel.robot.click(renderpanel.component(), new Point(0, 0), MouseButton.RIGHT_BUTTON, 1);
		Point2d point = getAtomPoint(panel,0);
		renderpanel.robot.click(renderpanel.component(), new Point((int)point.x, (int)point.y), MouseButton.RIGHT_BUTTON, 1);

		// we do target.doClick on the parent menu as otherwise FlatLAF gets stuck
		frame.menuItem("reaction").component().menuSelectionChanged(true);
		frame.menuItem("addProductToExistingReaction").target.doClick();

		Assert.assertEquals(1, panel.getChemModel().getReactionSet().getReactionCount());
		Assert.assertEquals(1, panel.getChemModel().getReactionSet().getReaction(0).getReactantCount());
		Assert.assertEquals(2, panel.getChemModel().getReactionSet().getReaction(0).getProductCount());

		frame.button("hexagon").click();
		renderpanel.robot.click(renderpanel.component(), new Point(100, 300), MouseButton.LEFT_BUTTON, 1);
		renderpanel.robot.click(renderpanel.component(), new Point(400, 300), MouseButton.LEFT_BUTTON, 1);
		frame.button("reactionArrow").click();
		renderpanel.robot.pressMouse(renderpanel.component(), new Point(150, 300));
		renderpanel.robot.moveMouse(renderpanel.component(), new Point(350, 300));
		renderpanel.robot.releaseMouseButtons();
		Assert.assertEquals(2, panel.getChemModel().getReactionSet().getReactionCount());
		Assert.assertEquals(1, panel.getChemModel().getReactionSet().getReaction(1).getReactantCount());
		Assert.assertEquals(1, panel.getChemModel().getReactionSet().getReaction(1).getProductCount());
    }


	/**
	 * October 2026 - Erase on a heteroatom changes the atom to carbon rather
	 *                than remove it and it's bonds. If a connected bond is
	 *                select it still gets deleted.
	 */
	@Test
	public void testEraseHighlightAtom() {
		restoreModelWithBasicmol();
		JPanelFixture renderpanel = frame.panel("renderpanel");
		Point atom0 = toAwtPoint(getAtomPoint(panel, 0));
		frame.button("N").click();
		renderpanel.robot.click(renderpanel.component(), atom0);
		Assert.assertEquals("NCCCCCCC", panel.getSmiles());
		renderpanel.robot.moveMouse(renderpanel.component(), atom0);
		frame.button("eraser").click();
		Assert.assertEquals("CCCCCCCC", panel.getSmiles()); // N => C
		renderpanel.robot.click(renderpanel.component(), atom0);
		Assert.assertEquals("CCCCCCC", panel.getSmiles()); // C => remove
	}

	@Test
	public void testEraseSelection() {
		restoreModelWithBasicmol();
		JPanelFixture renderpanel = frame.panel("renderpanel");
		frame.button("N").click();
		Point atom0 = toAwtPoint(getAtomPoint(panel, 0));
		renderpanel.robot.click(renderpanel.component(), atom0);
		Point atom2 = toAwtPoint(getAtomPoint(panel, 2));
		renderpanel.robot.click(renderpanel.component(), atom2);
		Point atom4 = toAwtPoint(getAtomPoint(panel, 4));
		renderpanel.robot.click(renderpanel.component(), atom4);
		Assert.assertEquals("NCNCNCCC", panel.getSmiles());

		// select multiple, they will change to carbon
		frame.button("select").click();
		renderpanel.robot.pressKey(KeyEvent.VK_SHIFT);
		renderpanel.robot.click(renderpanel.component(), atom2);
		renderpanel.robot.click(renderpanel.component(), atom4);
		renderpanel.robot.releaseKey(KeyEvent.VK_SHIFT);
		frame.button("eraser").click();
		Assert.assertEquals("NCCCCCCC", panel.getSmiles()); // N,N => C,C

		// select multiple
		frame.button("select").click();
		renderpanel.robot.pressKey(KeyEvent.VK_SHIFT);
		renderpanel.robot.click(renderpanel.component(), atom2);
		renderpanel.robot.click(renderpanel.component(), atom4);
		renderpanel.robot.releaseKey(KeyEvent.VK_SHIFT);
		frame.button("eraser").click();
		// cansmi will fix this!
		String[] parts = panel.getSmiles().split("\\.");
		Arrays.sort(parts);
		Assert.assertEquals("C.CCC.NC", String.join(".", parts)); // N,N => C,C
	}

	/**
	 * Here even though the atom is N we delete it right away because the bond
	 * is also selected.
	 */
	@Test
	public void testEraseSelectionWithBond() {
		restoreModelWithBasicmol();
		JPanelFixture renderpanel = frame.panel("renderpanel");
		Point atom0 = toAwtPoint(getAtomPoint(panel, 0));
		Point bond0 = toAwtPoint(getBondPoint(panel, 0));

		frame.button("N").click();
		renderpanel.robot.click(renderpanel.component(), atom0);
		Assert.assertEquals("NCCCCCCC", panel.getSmiles());

		frame.button("select").click();
		renderpanel.robot.pressKey(KeyEvent.VK_SHIFT);
		renderpanel.robot.click(renderpanel.component(), atom0);
		renderpanel.robot.click(renderpanel.component(), bond0);
		renderpanel.robot.releaseKey(KeyEvent.VK_SHIFT);
		frame.button("eraser").click();
		Assert.assertEquals("CCCCCCC", panel.getSmiles());
	}

	/**
	 * Here even though the atom is N we delete it right away because the bond
	 * is also deleted when it's neighbour is deleted.
	 */
	@Test
	public void testEraseSelectionWithImplBond() {
		restoreModelWithBasicmol();
		JPanelFixture renderpanel = frame.panel("renderpanel");
		Point atom0 = toAwtPoint(getAtomPoint(panel, 0));
		Point atom1 = toAwtPoint(getAtomPoint(panel, 1));
		Point atom2 = toAwtPoint(getAtomPoint(panel, 2));

		frame.button("N").click();
		renderpanel.robot.click(renderpanel.component(), atom0);
		renderpanel.robot.click(renderpanel.component(), atom2);
		Assert.assertEquals("NCNCCCCC", panel.getSmiles());

		frame.button("select").click();
		renderpanel.robot.pressKey(KeyEvent.VK_SHIFT);
		renderpanel.robot.click(renderpanel.component(), atom0);
		renderpanel.robot.click(renderpanel.component(), atom1);
		renderpanel.robot.click(renderpanel.component(), atom2);
		renderpanel.robot.releaseKey(KeyEvent.VK_SHIFT);
		frame.button("eraser").click();

		// one nitrogen is removed (because all it's bonds were removed),
		// the other is turned into a carbon
		Assert.assertEquals("CCCCCC", panel.getSmiles());
	}

	/**
	 * When moving and merging, hetero atoms should take priority.
	 */
	@Test
	public void testMergePreference() {
		restoreModelToEmpty();
		frame.button("hexagon").click();
		JPanelFixture renderpanel = frame.panel("renderpanel");
		renderpanel.robot.click(renderpanel.component(), new Point(100, 150));
		renderpanel.robot.click(renderpanel.component(), new Point(300, 150));
		panel.getRenderPanel().getRendererModel().setHighlightedAtom(null);
		frame.button("N").click();
		Point mol1atom1 = toAwtPoint(getAtomPoint(panel, 1, 0));
		Point mol2atom4 = toAwtPoint(getAtomPoint(panel, 4, 1));
		renderpanel.robot.click(renderpanel.component(), mol1atom1);
		renderpanel.robot.click(renderpanel.component(), mol2atom4);

		Assert.assertEquals("C1NCCCC1.C1CCCNC1", panel.getSmiles());

		int yShiftAmount = mol2atom4.x - mol1atom1.x;

		// select the second ring
		frame.button("select").click();
		select(panel.getChemModel().getMoleculeSet().getAtomContainer(1));

		// move it
		renderpanel.robot.pressMouse(renderpanel.component(), new Point(300, 150));
		renderpanel.robot.moveMouse(renderpanel.component(), new Point(300 - 10, 150));
		renderpanel.robot.moveMouse(renderpanel.component(), new Point(300 - yShiftAmount + 10, 150));
		renderpanel.robot.moveMouse(renderpanel.component(), new Point(300 - yShiftAmount, 150));
		robot.waitForIdle();
		renderpanel.robot.releaseMouseButtons();
		robot.waitForIdle();

		// would be easier with canonical SMILES
		Assert.assertEquals(10, getAtomCount(panel));
		Assert.assertEquals(11, getBondCount(panel));
		Assert.assertEquals(2, getElemCount(panel, IElement.N));

		// check the undo works
		frame.menuItem("undo").click();

		// would be easier with canonical SMILES
		Assert.assertEquals(12, getAtomCount(panel));
		Assert.assertEquals(12, getBondCount(panel));
		Assert.assertEquals(2, getElemCount(panel, IElement.N));

		// check the redo works
		frame.menuItem("redo").click();

		// would be easier with canonical SMILES
		Assert.assertEquals(10, getAtomCount(panel));
		Assert.assertEquals(11, getBondCount(panel));
		Assert.assertEquals(2, getElemCount(panel, IElement.N));
	}

	/**
	 * When moving and merging, hetero atoms should take priority.
	 */
	@Test
	public void testMergePreferenceAlt() {
		restoreModelToEmpty();
		frame.button("hexagon").click();
		JPanelFixture renderpanel = frame.panel("renderpanel");
		renderpanel.robot.click(renderpanel.component(), new Point(100, 150));
		renderpanel.robot.click(renderpanel.component(), new Point(300, 150));
		panel.getRenderPanel().getRendererModel().setHighlightedAtom(null);

		Assert.assertEquals("C1CCCCC1.C1CCCCC1", panel.getSmiles());

		Point mol1atom1 = toAwtPoint(getAtomPoint(panel, 1, 0));
		Point mol2atom4 = toAwtPoint(getAtomPoint(panel, 4, 1));
		int yShiftAmount = mol2atom4.x - mol1atom1.x;

		// select the second ring
		frame.button("select").click();
		select(panel.getChemModel().getMoleculeSet().getAtomContainer(1));

		// move it
		renderpanel.robot.pressKey(KeyEvent.VK_ALT);
		renderpanel.robot.pressMouse(renderpanel.component(), new Point(300, 150));
		renderpanel.robot.moveMouse(renderpanel.component(), new Point(300 - yShiftAmount, 150));
		renderpanel.robot.releaseMouseButtons();
		renderpanel.robot.releaseKey(KeyEvent.VK_ALT);

		// moved but did not merge
		Assert.assertEquals("C1CCCCC1.C1CCCCC1", panel.getSmiles());
	}

	// JWM if setting the multiple atom symbols these should all be under
	//     one 'change' event for undo/redo
	@Test
	public void testSetMultipleSymbols() {
		restoreModelToEmpty();
		frame.button("hexagon").click();
		JPanelFixture renderpanel = frame.panel("renderpanel");
		renderpanel.robot.click(renderpanel.component(), new Point(100, 150));
		frame.button("select").click();

		Assert.assertEquals("C1CCCCC1", panel.getSmiles());

		select(panel.getChemModel().getMoleculeSet().getAtomContainer(0));
		frame.button("N").click();
		Assert.assertEquals("N1NNNNN1", panel.getSmiles());
		frame.button("undo").click();
		Assert.assertEquals("C1CCCCC1", panel.getSmiles());
	}
}
