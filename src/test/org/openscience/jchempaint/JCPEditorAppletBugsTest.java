package org.openscience.jchempaint;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;

import org.fest.swing.applet.AppletViewer;
import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.launcher.AppletLauncher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;

public class JCPEditorAppletBugsTest {
	private static AppletViewer viewer;
	private static FrameFixture applet;
	private static JChemPaintEditorApplet jcpApplet;


	@BeforeClass public static void setUp() {
	    jcpApplet = new JChemPaintEditorApplet();
		Map<String, String> parameters = new HashMap<String, String>();
		viewer = AppletLauncher.applet(jcpApplet)
			.withParameters(parameters)
			.start();
		applet = new FrameFixture(viewer);
		applet.show();
	}

	@Test public void testSquareSelectSingleAtom()  {
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		applet.button("C").click();
		Point movetopint=new Point(100,100);	
		applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), movetopint, MouseButton.LEFT_BUTTON,1);
		Assert.assertEquals(1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtomCount());
		applet.button("select").click();
		movetopint=new Point(110,110);	
		applet.panel("renderpanel").robot.moveMouse(applet.panel("renderpanel").component(),movetopint);
		applet.panel("renderpanel").robot.pressMouse(MouseButton.LEFT_BUTTON);
		movetopint=new Point(90,90);	
		applet.panel("renderpanel").robot.moveMouse(applet.panel("renderpanel").component(),movetopint);
		applet.panel("renderpanel").robot.releaseMouse(MouseButton.LEFT_BUTTON);
		Assert.assertEquals(1, panel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().getAtomCount());
		restoreModel();
	}
	
	@Test public void testGetMolFile() throws CDKException{
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        applet.button("hexagon").click();
        Point movetopint=new Point(100,100);    
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), movetopint, MouseButton.LEFT_BUTTON,1);
	    Assert.assertTrue(jcpApplet.getMolFile().indexOf("6  6  0  0  0  0  0  0  0  0999 V2000")>0);
        restoreModel();	    
	}
	
	
	private void restoreModel(){
		JPanelFixture jcppanel=applet.panel("appletframe");
		JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
		IChemModel basic = DefaultChemObjectBuilder.getInstance().newChemModel();
		basic.setMoleculeSet(basic.getBuilder().newMoleculeSet());
		basic.getMoleculeSet().addAtomContainer(
				basic.getBuilder().newMolecule());
		panel.setChemModel(basic);
		panel.getRenderPanel().getRenderer().getRenderer2DModel().setZoomFactor(1);
		panel.get2DHub().updateView();
	}
	
	/*
	 * @cdk.bug 2859344 /6
	 */
	@Test public void overwriteStereo(){
	    JPanelFixture jcppanel=applet.panel("appletframe");
	    JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
	    //we draw a hexagon
	    applet.button("hexagon").click();
	    applet.click();
	    //one of its bonds becomes an up bond
	    applet.button("up_bond").click();
        Point2d moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //if we make that down, it must be down (and not donw_inv)
        applet.button("down_bond").click();
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.STEREO_BOND_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        //and up again
        applet.button("up_bond").click();
        moveto=panel.getRenderPanel().getRenderer().toScreenCoordinates((panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().x+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().x)/2,(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(2).getPoint2d().y+panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(3).getPoint2d().y)/2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
	    restoreModel();     
	}
	
	@Test public void testUpBond(){
	    genericStereoBondTest(CDKConstants.STEREO_BOND_UP);
	}
	
    @Test public void testDownBond(){
        genericStereoBondTest(CDKConstants.STEREO_BOND_DOWN);
    }
    
    @Test public void testUndefinedBond(){
        genericStereoBondTest(CDKConstants.STEREO_BOND_UNDEFINED);
    }

    /*
     * @cdk.bug 2859344 /7
     */
    @Test public void testUndefinedEzBond(){
        genericStereoBondTest(CDKConstants.EZ_BOND_UNDEFINED);
    }
    
    @Test public void testNoneBond(){
        genericStereoBondTest(CDKConstants.STEREO_BOND_NONE);
    }
    
    /**
	 * This is a test for overwriting of stereo bonds. Any stereo bond
	 * must overwrite all others and flip itself. 
	 * 
	 * @param directionToTest
	 */
	private void genericStereoBondTest(int directionToTest){
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        //we draw a hexagon
        applet.button("hexagon").click();
        applet.click();
        //we make all bond types in there
        applet.button("up_bond").click();
        Point2d moveto=getBondPoint(panel,1);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UP, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(1).getStereo());
        applet.button("down_bond").click();
        moveto=getBondPoint(panel,2);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.STEREO_BOND_DOWN, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(2).getStereo());
        applet.button("undefined_bond").click();
        moveto=getBondPoint(panel,3);   
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(3).getStereo());
        applet.button("undefined_stereo_bond").click();
        moveto=getBondPoint(panel,4);
        applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
        Assert.assertEquals(CDKConstants.EZ_BOND_UNDEFINED, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(4).getStereo());
        //now we click on all of them with disired bond
        if(directionToTest==CDKConstants.STEREO_BOND_UP)
            applet.button("up_bond").click();
        if(directionToTest==CDKConstants.STEREO_BOND_DOWN)
            applet.button("down_bond").click();
        if(directionToTest==CDKConstants.STEREO_BOND_UNDEFINED)
            applet.button("undefined_bond").click();
        if(directionToTest==CDKConstants.EZ_BOND_UNDEFINED)
            applet.button("undefined_stereo_bond").click();
        if(directionToTest==CDKConstants.STEREO_BOND_NONE)
            applet.button("bond").click();
        for(int i=0;i<5;i++){
            boolean self=false;
            if(panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(i).getStereo()==directionToTest)
                self=true;
            moveto=getBondPoint(panel,i);
            applet.panel("renderpanel").robot.click(applet.panel("renderpanel").component(), new Point((int)moveto.x, (int)moveto.y), MouseButton.LEFT_BUTTON,1);
            if(self){
                Assert.assertEquals(directionToTest==CDKConstants.EZ_BOND_UNDEFINED || directionToTest==CDKConstants.STEREO_BOND_NONE ? directionToTest : directionToTest==CDKConstants.STEREO_BOND_DOWN ? directionToTest-1 : directionToTest+1, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(i).getStereo());
            }else{
                Assert.assertEquals(directionToTest, panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(i).getStereo());
            }
        }
        restoreModel();     
	}
	

	private Point2d getBondPoint(JChemPaintPanel panel, int bondnumber) {
	    IBond bond = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(bondnumber);
	    return panel.getRenderPanel().getRenderer().toScreenCoordinates((bond.getAtom(0).getPoint2d().x+bond.getAtom(1).getPoint2d().x)/2,(bond.getAtom(0).getPoint2d().y+bond.getAtom(1).getPoint2d().y)/2);
    }

    @AfterClass public static void tearDown() {
	  viewer.unloadApplet();
	  applet.cleanUp();
	}
}
