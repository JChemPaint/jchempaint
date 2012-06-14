package org.openscience.jchempaint;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;
import javax.vecmath.Point2d;

import org.fest.swing.core.ComponentDragAndDrop;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;

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
