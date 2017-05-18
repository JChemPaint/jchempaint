package org.openscience.jchempaint;


import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.selection.RectangleSelection;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue139Test extends AbstractAppletTest {

    @Test public void testIssue139() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        panel.get2DHub().mouseClickedUp(100, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
        
        applet.button("select").click();
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
		IAtomContainer ethane = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
        RectangleSelection sel = new RectangleSelection();
        sel.addAtom (ethane.getAtom(0));
        sel.addBond(ethane.getBond(0));
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(sel);
        panel.selectionChanged();
        try {
			applet.button("cut").click();
	        panel.get2DHub().updateView();
	        applet.panel("renderpanel").robot.waitForIdle();
		} catch (Exception e) {
	        Assert.fail();
		}

		int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(1, atomCount);
		Assert.assertEquals(0, bondCount);
		Assert.assertEquals(4, implicitHCount);

        try {
			applet.button("paste").click();
		} catch (Exception e) {
	        Assert.fail();
		}
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();

		atomCount=0; bondCount=0; implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(2, atomCount);
		Assert.assertEquals(0, bondCount);
		Assert.assertEquals(8, implicitHCount);

    }

}
