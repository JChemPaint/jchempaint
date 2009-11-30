package org.openscience.jchempaint;

import java.awt.Point;
import java.io.IOException;

import javax.vecmath.Point2d;

import org.fest.swing.core.ComponentDragAndDrop;
import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

public class JCPEditorAppletDrawingTest extends AbstractAppletTest{
	
	@Test public void testAddBond() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//using bond button
		applet.button("bond").click();
		//can we add a new bond?
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//can we convert bond order?
		int oldhydrogencount0 = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getHydrogenCount().intValue();
		int oldhydrogencount1 = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getHydrogenCount().intValue();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(IBond.Order.DOUBLE, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getOrder());
		Assert.assertEquals(oldhydrogencount0-2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getHydrogenCount().intValue());
		Assert.assertEquals(oldhydrogencount1-2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getHydrogenCount().intValue());
		//using up bond button,
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4)));
		applet.button("up_bond").click();
		//was the selected bond changed
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
		//can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
		//can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(IBond.Stereo.UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using down bond button,
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4)));
		applet.button("down_bond").click();
        //was the selected bond changed
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(IBond.Stereo.DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using undefined bond button,
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4)));
		applet.button("undefined_bond").click();
        //was the selected bond changed
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+4, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //using undefined stereo button,		
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(new SingleSelection<IBond>(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4)));
		applet.button("undefined_stereo_bond").click();
	    //was the selected bond changed
        Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
        //can we add a new bond?
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()-1).getStereo());
        //can we convert an existing one?
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(IBond.Stereo.E_OR_Z, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
	}
	
	@Test public void testElement() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    //we go into bond_down mode to see that hitting O button actually changes activeDrawModule
	    applet.button("down_bond").click();
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		applet.button("O").click();
        Assert.assertEquals("O",panel.get2DHub().getActiveDrawModule().getID());
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals("O",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		applet.button("bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("O",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
		Assert.assertEquals("O",panel.get2DHub().getController2DModel().getDrawElement());
	}
	
	@Test public void testPeriodictable() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		applet.button("periodictable").click();
		DialogFixture dialog = applet.dialog();
		dialog.button("Li").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals("Li",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		applet.button("bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("Li",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
	}

	@Test public void testEnterelement() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		applet.button("enterelement").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		DialogFixture dialog = applet.dialog();
		dialog.textBox().setText("U");
		dialog.button("ok").click();
		Assert.assertEquals("U",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getSymbol());
		applet.button("bond").click();
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		Assert.assertEquals("U",panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()-1).getSymbol());
	}

	@Test public void testTriangle() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a triangle to an atom
		applet.button("triangle").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModelWithBasicmol();
	}

	@Test public void testPentagon() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a pentagon to an atom
		applet.button("pentagon").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+4, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+7, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModelWithBasicmol();
	}

	@Test public void testHexagon() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a hexagon to an atom
		applet.button("hexagon").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+9, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModelWithBasicmol();
	}

	@Test public void testOctagon() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a octagon to an atom
		applet.button("octagon").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+7, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+13, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModelWithBasicmol();
	}

	@Test public void testBenzene() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a benzene to an atom
		applet.button("benzene").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+9, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModelWithBasicmol();
	}

	@Test public void testSquare() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
		//we add a square to an atom
		applet.button("square").click();
		Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+3, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		//and a bond
		moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(oldAtomCount+5, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		restoreModelWithBasicmol();
	}
	
	@Test public void testDelete() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
	    restoreModelWithBasicmol();
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
        int oldBondCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount();
        //we delete an atom
        applet.button("eraser").click();
        Point2d moveto=getAtomPoint(panel, 0);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(oldAtomCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(oldBondCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount());
        //and a (terminal) bond
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(1).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(2, panel.getChemModel().getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(oldAtomCount-2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        Assert.assertEquals(oldBondCount-2, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBondCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        restoreModelWithBasicmol();
    }

	@Test public void selectByDoubleClick(){
	    restoreModelWithBasicmol();
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
        //we add a hexagon
        applet.button("hexagon").click();
        Point2d moveto=new Point2d(100,100);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(oldAtomCount+6, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount()+panel.getChemModel().getMoleculeSet().getAtomContainer(1).getAtomCount());
        applet.button("select").click();
        //double click on atom
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().x,panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(0).getPoint2d().y);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,2);
        Assert.assertEquals(oldAtomCount, panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().getAtomCount());
	}
	
	@Test public void mergeAndUndoRedo(){
        restoreModelWithBasicmol();
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        int oldAtomCount=panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount();
        applet.button("select").click();
        Point2d startpoint=getAtomPoint(panel,0);
        ComponentDragAndDrop dandd = new ComponentDragAndDrop(applet.panel("renderpanel").robot);
        dandd.drag(applet.panel("renderpanel").component(), new Point((int)startpoint.x, (int)startpoint.y));
        Point2d movetopoint=getAtomPoint(panel,1);
        dandd.drop(applet.panel("renderpanel").component(), new Point((int)movetopoint.x, (int)movetopoint.y));
        Assert.assertEquals(oldAtomCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        movetopoint=getAtomPoint(panel,2);
        applet.panel("renderpanel").robot.moveMouse(applet.panel("renderpanel").component(),new Point((int)movetopoint.x, (int)movetopoint.y));
        applet.button("undo").click();
        Assert.assertEquals(oldAtomCount, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
        applet.button("redo").click();
        Assert.assertEquals(oldAtomCount-1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
	}
}
