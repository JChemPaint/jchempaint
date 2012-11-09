package org.openscience.jchempaint;

import javax.vecmath.Vector2d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 *
 * Tests SMILES input. Depends on cdk SF#3531612.
 */
public class Issue27Test extends AbstractAppletTest {

    @Test //(expected=CDKException.class)
    public void testIssue27() throws Exception, InvalidSmilesException {
        
        SmilesParser sp = 
                new SmilesParser(DefaultChemObjectBuilder.getInstance());
            String smiles = 
                "C1C1";
            		
            IMolecule mol = sp.parseSmiles(smiles);

            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setMolecule(mol);
            sdg.generateCoordinates(new Vector2d(0, 1));
            mol = sdg.getMolecule();
            int nullpos = 0;
            for (IAtom atom: mol.atoms()) {
            	nullpos += (atom.getPoint2d() == null)? 1:0;
            }
            Assert.assertTrue(nullpos == 0);
    }

}

