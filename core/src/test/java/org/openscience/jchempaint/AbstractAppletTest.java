package org.openscience.jchempaint;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;

import org.fest.swing.applet.AppletViewer;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.launcher.AppletLauncher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;

/**
 * An abstract base class for applet tests. It sets up and tears down
 * and offers some convenience methods.
 */
public class AbstractAppletTest {
    private static AppletViewer viewer;
    protected static FrameFixture applet;
    protected static JChemPaintEditorApplet jcpApplet;
    protected static JChemPaintPanel panel;
    


    @BeforeClass public static void setUp() {
        jcpApplet = new JChemPaintEditorApplet();
        Map<String, String> parameters = new HashMap<String, String>();
        viewer = AppletLauncher.applet(jcpApplet)
            .withParameters(parameters)
            .start();
        applet = new FrameFixture(viewer);
        applet.show();
        JPanelFixture jcppanel=applet.panel("appletframe");
        panel = (JChemPaintPanel)jcppanel.target;
        viewer.setSize(700,700);
    }
    
    protected Point2d getBondPoint(JChemPaintPanel panel, int bondnumber) {
        IBond bond = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getBond(bondnumber);
        return panel.getRenderPanel().getRenderer().toScreenCoordinates((bond.getAtom(0).getPoint2d().x+bond.getAtom(1).getPoint2d().x)/2,(bond.getAtom(0).getPoint2d().y+bond.getAtom(1).getPoint2d().y)/2);
    }
    
    protected Point2d getAtomPoint(JChemPaintPanel panel, int atomnumber){
        IAtom atom = panel.getChemModel().getMoleculeSet().getAtomContainer(0).getAtom(atomnumber);
        return panel.getRenderPanel().getRenderer().toScreenCoordinates(atom.getPoint2d().x,atom.getPoint2d().y);
    }    
    
    protected Point2d getAtomPoint(JChemPaintPanel panel, int atomnumber, int acnumber){
        IAtom atom = panel.getChemModel().getMoleculeSet().getAtomContainer(acnumber).getAtom(atomnumber);
        return panel.getRenderPanel().getRenderer().toScreenCoordinates(atom.getPoint2d().x,atom.getPoint2d().y);
    }    

    protected void restoreModelToEmpty(){
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        IChemModel basic = DefaultChemObjectBuilder.getInstance().newInstance(IChemModel.class);
        basic.setMoleculeSet(basic.getBuilder().newInstance(IAtomContainerSet.class));
        basic.getMoleculeSet().addAtomContainer(
                basic.getBuilder().newInstance(IAtomContainer.class));
        panel.setChemModel(basic);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setZoomFactor(1);
        panel.getRenderPanel().getRenderer().getRenderer2DModel().setBondLength(10.4);
        panel.get2DHub().updateView();
    }
    
    protected void restoreModelWithBasicmol(){
        JPanelFixture jcppanel=applet.panel("appletframe");
        JChemPaintPanel panel = (JChemPaintPanel)jcppanel.target;
        panel.get2DHub().getController2DModel().setAutoUpdateImplicitHydrogens(true);
        String filename = "data/basic.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemModel basic;
        try {
            basic = (IChemModel) reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemModel.class));
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(basic.getMoleculeSet().getAtomContainer(0));
            CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(basic.getMoleculeSet().getAtomContainer(0)
                    .getBuilder());
            hAdder.addImplicitHydrogens(basic.getMoleculeSet().getAtomContainer(0));
            //valencies are set when doing atom typing, which we don't want in jcp
            for(int i=0;i<basic.getMoleculeSet().getAtomContainer(0).getAtomCount();i++){
                basic.getMoleculeSet().getAtomContainer(0).getAtom(i).setValency(null);
            }
            panel.setChemModel(basic);
            panel.getRenderPanel().getRenderer().getRenderer2DModel().setZoomFactor(1);

            panel.get2DHub().updateView();
            reader.close();
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @AfterClass public static void tearDown() {
      viewer.unloadApplet();
      applet.cleanUp();
    }
}
