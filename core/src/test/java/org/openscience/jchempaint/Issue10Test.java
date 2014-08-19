/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 *
 * Tests Undo after SMILES input. See also Issue81Test
 */
public class Issue10Test extends AbstractAppletTest {

    @Test public void testIssue10() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel pane = (JChemPaintPanel)jcppanel.target;
        try {
        	jcpApplet.setSmiles("CCCC");
        	pane.get2DHub().updateView();
		} catch (Exception e) {
			Assert.fail();
		}
	applet.button("undo").click();
	pane.get2DHub().updateView();

        int atomCount=0, bondCount=0;
		for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
	Assert.assertEquals(0, atomCount);
        Assert.assertEquals(0, bondCount);

	applet.button("redo").click();
	pane.get2DHub().updateView();

	atomCount=0; bondCount=0;
		for(IAtomContainer atc : pane.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
	Assert.assertEquals(4, atomCount);
        Assert.assertEquals(3, bondCount);
    }

}
