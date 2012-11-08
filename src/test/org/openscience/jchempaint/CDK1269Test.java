package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 *
 * Tests chiral SMILES input with following cleanup. 
 * See also #165.
 */
public class CDK1269Test extends AbstractAppletTest {

    @Test (expected=CDKException.class)
    public void testCDK1269() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;

		jcpApplet.setSmiles("O=C(O)[C@H](N)C"); // L-alanine
		panel.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
		panel.get2DHub().cleanup();
		panel.get2DHub().updateView();
		applet.panel("renderpanel").robot.waitForIdle();
			
		int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(7, atomCount);
        Assert.assertEquals(6, bondCount);
        Assert.assertEquals(6, implicitHCount);
    }

}
