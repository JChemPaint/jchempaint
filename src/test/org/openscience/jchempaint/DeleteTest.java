package org.openscience.jchempaint;

import java.awt.Point;
import java.io.IOException;

import javax.vecmath.Point2d;

import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;

public class DeleteTest extends AbstractAppletTest {
	@Test public void testDelete() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
        int oldBondCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount();
        //we delete an atom
        applet.button("eraser").click();
        Point2d moveto=getAtomPoint(panel, 0);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(oldAtomCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(oldBondCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
        //and a (terminal) bond
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(2, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(oldAtomCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        Assert.assertEquals(oldBondCount-2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getBondCount());
        restoreModelWithBasicmol();
    }
}
