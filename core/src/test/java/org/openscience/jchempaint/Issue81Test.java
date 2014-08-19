package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 *
 * Tests SMILES input in general.
 */
public class Issue81Test extends AbstractAppletTest {

    @Test public void testIssue81() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        try {
        	jcpApplet.setSmiles("CCCC");
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

}
