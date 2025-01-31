package org.openscience.jchempaint;

import java.awt.Point;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

public class MenuIsotopeTest extends AbstractAppletTest {


	@Test public void testMenuIsotopeMajorPlusThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(15);
	}

	@Test public void testMenuIsotopeMajorPlusTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(14);
	}
	
	@Test public void testMenuIsotopeMajorPlusOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(13);
	}
	
	@Test public void testMenuIsotopeMajor() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(12);
	}
	
	@Test public void testMenuIsotopeMajorMinusOne() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(11);
	}
	
	private void genericIsotopeTest(int isotopeNumber){
	    //we go to select mode
	    restoreModelWithBasicmol();
        applet.button("select").target.doClick();
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IAtom>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0)));
        panel.selectionChanged();
        int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
        applet.menuItem("isotopeChange").click();
        applet.menuItem("C"+isotopeNumber).click();
        Assert.assertEquals(isotopeNumber, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getMassNumber().intValue());
        //the mode should have changed now
        Assert.assertEquals("C", panel.get2DHub().getActiveDrawModule().getID());
        Assert.assertEquals(isotopeNumber, panel.get2DHub().getController2DModel().getDrawIsotopeNumber());
        //if we click somewhere, we should get a new atom with specified properties
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point(100,100));
        Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        Assert.assertEquals("C", panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtom(0).getSymbol());
        Assert.assertEquals(isotopeNumber, panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtom(0).getMassNumber().intValue());
        panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).setMassNumber(12);
    }
	
	@Test public void testMenuIsotopeMajorMinusTwo() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(10);
	}
	
	@Test public void testMenuIsotopeMajorMinusThree() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    genericIsotopeTest(9);
	}
}


