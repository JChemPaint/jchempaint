package org.openscience.jchempaint;

import javax.vecmath.Point2d;

import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.Renderer;

import java.awt.Point;

/**
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 * <p>
 * 129: Input SMILES C=C (ethylene); (with single bond active) click on
 * double bond: NOT ethyne; click on triple bond; click on quadruple
 * bond: H2C-CH2, should be H3C-CH3
 */
public class Issue129Test extends AbstractAppletTest {

    @Test
    public void testIssue129() {
        JPanelFixture jcppanel = applet.panel("JChemPaintPanel");
        JChemPaintPanel panel = (JChemPaintPanel) jcppanel.target;
        try {
            panel.setSmiles("C=C");
            panel.get2DHub().updateView();
        } catch (Exception e) {
            Assert.fail();
        }

        int atomCount = 0, bondCount = 0, implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }
        Assert.assertEquals(2, atomCount);
        Assert.assertEquals(1, bondCount);
        Assert.assertEquals(4, implicitHCount);

        IAtomContainer mol = panel.getChemModel().getMoleculeSet().getAtomContainer(0);
        System.err.println(mol.getBond(0).getOrder());

        applet.button("bondTool").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),
                                                getBondAwtPoint(mol.getBond(0)));
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();

        applet.button("triple_bondTool").click();
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(),
                                                getBondAwtPoint(mol.getBond(0)));
        panel.get2DHub().updateView();
        applet.panel("renderpanel").robot.waitForIdle();

        atomCount = 0;
        bondCount = 0;
        implicitHCount = 0;
        for (IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
            for (IAtom a : atc.atoms())
                implicitHCount += a.getImplicitHydrogenCount();
            atomCount += atc.getAtomCount();
            bondCount += atc.getBondCount();
        }
        System.err.println(panel.getSmiles());
        Assert.assertEquals(2, atomCount);
        Assert.assertEquals(1, bondCount);
        Assert.assertEquals(2, implicitHCount);
    }

}
