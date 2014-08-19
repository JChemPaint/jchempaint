package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 *
 * Tests SMILES input of a salt, the correct model and
 * error handling (#11). Depends on cdk SF#3531612.
 */
public class Issue11Test extends AbstractAppletTest {

    @Test public void testIssue11() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        try {
        	jcpApplet.setSmiles("[NH4+].CP(=O)(O)CCC(N)C(=O)[O-]");
        	panel.get2DHub().updateView();
		} catch (Exception e) {
			Assert.fail();
		}
        
        int atomCount=0, bondCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(16, atomCount);
        Assert.assertEquals(14, bondCount);
    }

}
