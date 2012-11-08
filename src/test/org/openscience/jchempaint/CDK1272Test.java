package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 *
 * Tests SMILES input with spaces to simulate 
 * multiline cut&paste
 */
public class CDK1272Test extends AbstractAppletTest {

    @Test public void testCDK1272() {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
    	jcpApplet.setSmiles(" CC1=C(N=C(N=C1N)C(CC(=O)N)NCC(C(=O)N)N)C(=O)NC(C(C2=CN=CN2)OC3C(C(C(C (O3)CO)O)O)OC4C(C(C(C(O4)CO)O)OC(=O)N)O)C(=O)NC(C)C(C(C)C(=O)NC(C(C)O)C (=O)NCCC5=NC(=CS5)C6=NC(=CS6)C(=O)NCCCS(=O)C)O");
    	panel.get2DHub().updateView();
        
        int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(96, atomCount);
        Assert.assertEquals(101, bondCount);
        Assert.assertEquals(81, implicitHCount);
    }

}
