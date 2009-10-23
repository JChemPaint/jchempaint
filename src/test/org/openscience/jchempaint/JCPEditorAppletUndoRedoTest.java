package org.openscience.jchempaint;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import junit.framework.Assert;

import org.fest.swing.core.MouseButton;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

public class JCPEditorAppletUndoRedoTest extends AbstractAppletTest {

    private static List<IAtomContainer> models = new ArrayList<IAtomContainer>();
    
    @BeforeClass public static void setUp() {
        AbstractAppletTest.setUp();
    }
    
    @Test public void testUndo() throws CloneNotSupportedException{
        models.add((IAtomContainer)panel.getChemModel().getMoleculeSet().getAtomContainer(0).clone());
        System.err.println(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        drawRing();
        models.add((IAtomContainer)panel.getChemModel().getMoleculeSet().getAtomContainer(0).clone());
        System.err.println(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        attachRing();
        models.add((IAtomContainer)panel.getChemModel().getMoleculeSet().getAtomContainer(0).clone());
        System.err.println(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        panel.get2DHub().getRenderer().getRenderer2DModel().setHighlightedBond(null);
        deleteAtom();
        models.add((IAtomContainer)panel.getChemModel().getMoleculeSet().getAtomContainer(0).clone());
        System.err.println(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        applet.button("undo").click();
        Assert.assertEquals(models.get(2).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(models.get(2).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
        applet.button("undo").click();
        Assert.assertEquals(models.get(1).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(models.get(1).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
        applet.button("undo").click();
        Assert.assertEquals(models.get(0).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(models.get(0).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
    }

    private void deleteAtom() {
        applet.button("eraser").click();
        Point2d moveto=getAtomPoint(panel, 0);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);        
    }

    @Test public void testRedo(){
        applet.button("redo").click();
        Assert.assertEquals(models.get(1).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(models.get(1).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
        applet.button("redo").click();
        Assert.assertEquals(models.get(2).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(models.get(2).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
        applet.button("redo").click();
        Assert.assertEquals(models.get(3).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(models.get(3).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
    }
    
    private void attachRing() {
        applet.button("hexagon").click();
        Point2d moveto=getBondPoint(panel, 0);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);        
    }

    public void drawRing(){
        applet.button("hexagon").click();
        Point2d moveto=new Point2d(100,100);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
    }
}
