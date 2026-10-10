package org.openscience.jchempaint;

import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;

public class SetSmilesTest extends AbstractAppletTest {
	@Test
	public void testSetSmiles() throws CDKException {
		JPanelFixture jcppanel = applet.panel("JChemPaintPanel");
		JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
		panel.setSmiles("CCCC");
		panel.get2DHub().updateView();
		Assert.assertEquals(4, panel.getChemModel().getMoleculeSet()
				.getAtomContainer(0).getAtomCount());
		restoreModelToEmpty();
	}

}
