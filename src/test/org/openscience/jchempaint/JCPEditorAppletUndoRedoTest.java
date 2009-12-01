package org.openscience.jchempaint;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import junit.framework.Assert;

import org.fest.swing.core.MouseButton;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IChemModel;

public class JCPEditorAppletUndoRedoTest extends AbstractAppletTest {

    private static List<IChemModel> models = new ArrayList<IChemModel>();
    
    @BeforeClass public static void setUp() {
        AbstractAppletTest.setUp();
    }
    
    @Test public void testUndo() throws CloneNotSupportedException{
        //These should be models.add((IChemModel)panel.getChemModel());
        //but due to a bug in cdk, clone changes the model
        //without the clone, the test is not of much use, since it tests
        //(for my understanding) the model against itself.
        models.add((IChemModel)panel.getChemModel());
        drawRing(100,100);
        models.add((IChemModel)panel.getChemModel());
        attachRing();
        models.add((IChemModel)panel.getChemModel());
        panel.get2DHub().getRenderer().getRenderer2DModel().setHighlightedBond(null);
        deleteAtom();
        models.add((IChemModel)panel.getChemModel());
        drawRing(300,200);
        models.add((IChemModel)panel.getChemModel());
        applet.button("undo").click();
        compare(3);
        applet.button("undo").click();
        compare(2);
        applet.button("undo").click();
        compare(1);
        applet.button("undo").click();
        compare(0);
    }

    private void deleteAtom() {
        applet.button("eraser").click();
        Point2d moveto=getAtomPoint(panel, 0);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);        
    }

    @Test public void testRedo(){
        applet.button("redo").click();
        compare(1);
        applet.button("redo").click();
        compare(2);
        applet.button("redo").click();
        compare(3);
        applet.button("redo").click();
        compare(4);
    }
    
    private void attachRing() {
        applet.button("hexagon").click();
        Point2d moveto=getBondPoint(panel, 0);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);        
    }

    public void drawRing(int x, int y){
        applet.button("hexagon").click();
        Point2d moveto=new Point2d(x, y);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
    }
    
    
    private void compare(int whichmodel) {
        Assert.assertEquals(models.get(whichmodel).getMoleculeSet().getAtomContainerCount(), panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        for(int i=0;i<models.get(whichmodel).getMoleculeSet().getAtomContainerCount();i++){
            Assert.assertEquals(models.get(whichmodel).getMoleculeSet().getAtomContainer(i).getAtomCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(i).getAtomCount());
            Assert.assertEquals(models.get(whichmodel).getMoleculeSet().getAtomContainer(i).getBondCount(), panel.getChemModel().getMoleculeSet().getAtomContainer(i).getBondCount());
        }
    }

}
