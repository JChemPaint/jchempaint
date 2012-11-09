package org.openscience.jchempaint;

import javax.vecmath.Vector2d;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 *
 * Tests SMILES input of a salt, the correct model and
 * error handling (#11). Depends on cdk SF#3531612.
 */
public class Issue11Test extends AbstractAppletTest {

    @Test //(expected=CDKException.class)
    public void testIssue11() throws Exception, InvalidSmilesException {
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        
        SmilesParser sp = 
                new SmilesParser(DefaultChemObjectBuilder.getInstance());
            String smiles = 
                "[NH4+].CP(=O)(O)CCC(N)C(=O)[O-]";
            		
            IMolecule mol = sp.parseSmiles(smiles);

            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setMolecule(mol);
            sdg.generateCoordinates(new Vector2d(0, 1));
            mol = sdg.getMolecule();
            System.out.print(mol);
/*

        jcpApplet.setSmiles("[NH4+].CP(=O)(O)CCC(N)C(=O)[O-]");
        panel.get2DHub().updateView();
        
        int atomCount=0, bondCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(16, atomCount);
        Assert.assertEquals(14, bondCount);
*/    }

}
