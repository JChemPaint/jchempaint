package org.openscience.jchempaint;

import java.awt.Point;

import javax.vecmath.Point2d;

import org.fest.swing.fixture.JPopupMenuFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

public class MenuCutTest extends AbstractAppletTest {

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
        //try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
        popup.menuItem("cut").click();
        Assert.assertEquals(3,panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        //try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
        Assert.assertEquals(1,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(4,panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        Assert.assertEquals(2,panel.getChemModel().getMoleculeSet().getAtomContainer(2).getAtomCount());
	}
}

