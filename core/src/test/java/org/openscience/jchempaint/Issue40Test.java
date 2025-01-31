package org.openscience.jchempaint;

import javax.vecmath.Point2d;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.action.JCPAction;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 */
public class Issue40Test extends AbstractAppletTest {

    @Test public void testIssue40() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("C").target.doClick();
        applet.button("chain").target.doClick();
        panel.get2DHub().mouseClickedDown(100, 100);
        panel.get2DHub().mouseDrag(100, 100, 300, 100, 0);
        panel.get2DHub().mouseClickedUp(300, 100);
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();
		Point2d p = getAtomPoint(panel,0,1);
        try {
	    	panel.get2DHub().mouseClickedDown((int)p.x, (int)p.y);
	    	panel.get2DHub().mouseClickedUp((int)p.x, (int)p.y);
        } catch(Exception e) {
        	Assert.fail();
        }	
		int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(6, atomCount);
		Assert.assertEquals(5, bondCount);
		Assert.assertEquals(14, implicitHCount);
	
		try {
            JCPAction act = new JCPAction().getAction(panel, "org.openscience.jchempaint.action.UndoAction");
            act.actionPerformed(null);
		} catch (NullPointerException e) {
        	Assert.fail();
		}

        atomCount=0; bondCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(0, atomCount);
        Assert.assertEquals(0, bondCount);
    }

}
