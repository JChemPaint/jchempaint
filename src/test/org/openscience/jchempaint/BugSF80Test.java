package org.openscience.jchempaint;

import java.awt.Point;
import java.io.IOException;

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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.matchers.ButtonTextComponentMatcher;
import org.openscience.jchempaint.matchers.ComboBoxTextComponentMatcher;

public class BugSF80Test extends AbstractAppletTest {

	//Test for Trac ticket # 80 : deletion of the double bonds that bind the oxygens in caffeine led to               
	//                            incorrect cascading removal of the bound carbons too.
	@Test
	public void testRemoveBond() throws CDKException, ClassNotFoundException,
			IOException, CloneNotSupportedException {
		// Clean the panel:
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		jcpApplet.setSmiles("Cn1cnc2c1c(=O)n(C)c(=O)n2C");

/*		// Paste caffeine template:
        applet.menuItem("pasteTemplate").click();
		DialogFixture dialog = applet.dialog("templates");
		JButtonFixture templateButton = new JButtonFixture(dialog.robot,
				dialog.robot.finder().find(
						new ButtonTextComponentMatcher("Caffeine")));
		templateButton.click();
		applet.button("select").click();
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), 
				new Point(0,0), MouseButton.LEFT_BUTTON,1);
*/
        //Delete the double bonds connecting the two oxygen atoms:
		applet.button("eraser").click();
		for (IBond bond : panel.getChemModel().getMoleculeSet().getAtomContainer(0).bonds()) 
			if (bond.getOrder().equals(IBond.Order.DOUBLE) && 
					(bond.getAtom(0).getSymbol().equals("O")||bond.getAtom(1).getSymbol().equals("O"))) {
				double xAvg= (bond.getAtom(0).getPoint2d().x +bond.getAtom(1).getPoint2d().x)/2; 
				double yAvg= (bond.getAtom(0).getPoint2d().y +bond.getAtom(1).getPoint2d().y)/2; 
				Point2d moveTo=panel.getRenderPanel().getRenderer().toScreenCoordinates(xAvg,yAvg);
				Point p = new Point((int)moveTo.x, (int)moveTo.y);
				applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), 
						p, MouseButton.LEFT_BUTTON,1);
			}
        
        //Establish that only the double bonds plus the oxygens are gone, rest is still in place:
		//Issue 1, 129: make sure implicit Hs are added
		int atomCount=0, bondCount=0, implicitHCount=0;
		for(IAtomContainer atc : panel.getChemModel().getMoleculeSet().atomContainers()) {
			for (IAtom a : atc.atoms())
				implicitHCount += a.getImplicitHydrogenCount();
			atomCount+=atc.getAtomCount();
			bondCount+=atc.getBondCount();
		}
		Assert.assertEquals(12, atomCount);
		Assert.assertEquals(13, bondCount);
		Assert.assertEquals(14, implicitHCount);

	}
}
