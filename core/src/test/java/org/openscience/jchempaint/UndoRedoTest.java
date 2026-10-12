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
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;

import javax.vecmath.Point2d;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * These tests check the functioning of the undo/redo handling.
 *
 * @author John Mayfield
 */
public class UndoRedoTest extends AbstractUITest {

    private static List<IChemModel> models = new ArrayList<IChemModel>();
    
    @Test public void testUndo() throws CloneNotSupportedException{
        //These should be models.add((IChemModel)panel.getChemModel().clone());
        //but due to a bug in cdk, clone changes the model
        //without the clone, the test is not of much use, since it tests
        //(for my understanding) the model against itself.
        restoreModelToEmpty();
        models.clear();
        models.add((IChemModel)panel.getChemModel());
        drawRing(100,100);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(0, 0), MouseButton.RIGHT_BUTTON, 1);
        models.add((IChemModel)panel.getChemModel());
        attachRing();
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(0, 0), MouseButton.RIGHT_BUTTON, 1);
        models.add((IChemModel)panel.getChemModel());
        panel.get2DHub().getRenderer().getRenderer2DModel().setHighlightedBond(null);
        deleteAtom();
        models.add((IChemModel)panel.getChemModel());
        drawRing(300,200);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(0, 0), MouseButton.RIGHT_BUTTON, 1);
        models.add((IChemModel)panel.getChemModel());
        frame.button("undo").target.doClick();
        compare(3);
        frame.button("undo").target.doClick();
        compare(2);
        frame.button("undo").target.doClick();
        compare(1);
        frame.button("undo").target.doClick();
        compare(0);
    }

    private void deleteAtom() {
        frame.button("eraser").target.doClick();
        Point2d moveto=getAtomPoint(panel, 0);
        frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
    }

    @Test public void testRedo(){
        try {
            restoreModelToEmpty();
            models.clear();
            models.add((IChemModel)panel.getChemModel());
            drawRing(100,100);
            frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(0, 0), MouseButton.RIGHT_BUTTON, 1);
            models.add((IChemModel)panel.getChemModel());
            attachRing();
            frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(0, 0), MouseButton.RIGHT_BUTTON, 1);
            models.add((IChemModel)panel.getChemModel());
            panel.get2DHub().getRenderer().getRenderer2DModel().setHighlightedBond(null);
            deleteAtom();
            models.add((IChemModel)panel.getChemModel());
            drawRing(300,200);
            frame.panel("renderpanel").robot.click(frame.panel("renderpanel").component(), new Point(0, 0), MouseButton.RIGHT_BUTTON, 1);
            models.add((IChemModel)panel.getChemModel());
            frame.button("undo").target.doClick();
            compare(3);
            frame.button("undo").target.doClick();
            compare(2);
            frame.button("undo").target.doClick();
            compare(1);
            frame.button("undo").target.doClick();
            compare(0);
			frame.button("redo").target.doClick();
			compare(1);
			frame.button("redo").target.doClick();
			compare(2);
			frame.button("redo").target.doClick();
			compare(3);
			frame.button("redo").target.doClick();
			compare(4);
		} catch (IllegalStateException e) {
			Assert.fail("Redo button disabled");
		}
    }
    
    private void attachRing() {
        frame.button("hexagon").target.doClick();
        Point2d moveto=getBondPoint(panel, 0);
        robot.click(frame.panel("renderpanel").component(),
                    new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
    }

    public void drawRing(int x, int y){
        frame.button("hexagon").target.doClick();
        Point2d moveto=new Point2d(x, y);
        robot.click(frame.panel("renderpanel").component(),
                    new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON, 1);
    }
    
    
    private void compare(int whichmodel) {
        Assert.assertEquals(models.get(whichmodel).getMoleculeSet().getAtomContainerCount(),
                            panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        for(int i=0;i<models.get(whichmodel).getMoleculeSet().getAtomContainerCount();i++){
            Assert.assertEquals(models.get(whichmodel).getMoleculeSet().getAtomContainer(i).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(i).getAtomCount());
            Assert.assertEquals(models.get(whichmodel).getMoleculeSet().getAtomContainer(i).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(i).getBondCount());
        }
    }

    /**
     * Tests bug fixed for JChempaint trac Ticket #79 (new defect):
     * Undo/redo when pasting templates works partly.
     */
    @Test public void testUndoTemplates() throws CloneNotSupportedException{
        models.clear();
        restoreModelToEmpty();
    	frame.menuItem("new").click();
        models.add((IChemModel)panel.getChemModel());
        frame.menuItem("pasteTemplate").click();
        DialogFixture dialog = frame.dialog("templates");
        JButtonFixture templateButton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Cysteine")));
        templateButton.click();
        Assert.assertEquals(7,totalAtomCount());
        Assert.assertEquals(1,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        
        frame.menuItem("pasteTemplate").click();
        dialog = frame.dialog("templates");
        templateButton = new JButtonFixture(dialog.robot, dialog.robot.finder().find(new ButtonTextComponentMatcher("Leucine")));
        templateButton.click();
        Assert.assertEquals(16,totalAtomCount());
        Assert.assertEquals(2,panel.getChemModel().getMoleculeSet().getAtomContainerCount());

        frame.button("undo").target.doClick();
        Assert.assertEquals(7,totalAtomCount());
        Assert.assertEquals(1,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
    }

	private int totalAtomCount () {
        int totalAtomCount=0;
        for(IAtomContainer atc: panel.getChemModel().getMoleculeSet().atomContainers()) {
        	totalAtomCount+=atc.getAtomCount();
        }
        return totalAtomCount;
	}
}
