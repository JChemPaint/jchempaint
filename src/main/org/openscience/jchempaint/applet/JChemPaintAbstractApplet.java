/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.jchempaint.applet;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JApplet;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.InsertTextPanel;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.JExternalFrame;
import org.openscience.jchempaint.action.CreateSmilesAction;
import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IControllerModel;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;

/**
 * An abstract class for JCP applets, doing parameter parsing
 * 
 */
public abstract class JChemPaintAbstractApplet extends JApplet {
    private AbstractJChemPaintPanel theJcpp = null;
    private JExternalFrame jexf;
    protected boolean debug = false;

    private static String appletInfo = "JChemPaint Applet. See http://cdk.sourceforge.net "
            + "for more information";

    private static String[][] paramInfo = {
            { "background", "color",
                    "Background color as integer or hex starting with #" },
            { "atomNumbersVisible", "true or false",
                    "should atom numbers be shown" },
            { "load", "url", "URL of the chemical data" },
            { "compact", "true or false",
                    "compact means elements shown as dots, no figures etc. (default false)" },
            {
                    "tooltips",
                    "string like 'atomumber|test|atomnumber|text'",
                    "the texts will be used as tooltips for the respective atoms (leave out if none required" },
            { "impliciths", "true or false",
                    "the implicit hs will be added from start (default true)" },
            {
                    "spectrumRenderer",
                    "string",
                    "TODO name of a spectrum applet (see subproject in NMRShiftDB) where peaks should be highlighted when hovering over atom" },
            {
                    "hightlightTable",
                    "true or false",
                    "TODO if true peaks in a table will be highlighted when hovering over atom, ids are assumed to be tableid$atomnumber (default false)" },
            { "smiles", "string", "a structure to load as smiles" },
            {
                    "scrollbars",
                    "true or false",
                    "if the molecule is too big to be displayed in normal size, shall scrollbars be used (default) or the molecule be resized - only for viewer applet" },
            {
                    "dotranslate",
                    "true or false",
                    "should user interface be translated (default) or not (e. g. if you want an English-only webpage)" },
            { "detachable", "true or false",
                    "should the applet be detacheable by a double click (default false)" },
            { "detachableeditor", "true or false",
                    "should the applet be detacheable as an editor by a double click (default false), only for viewer" },
            { "debug", "true or false",
                    "switches on debug output (default false)" } };

    @Override
    public String getAppletInfo() {
        return appletInfo;
    }

    @Override
    public String[][] getParameterInfo() {
        return paramInfo;
    }

    /**
     * loads a molecule from url or smiles
     */
    protected void loadModelFromParam() {
        URL fileURL = null;
        String smiles = null;
        try {
            URL documentBase = getDocumentBase();
            String load = getParameter("load");
            if (load != null)
                fileURL = new URL(documentBase, load);
            smiles = getParameter("smiles");
        } catch (Exception exception) {
            theJcpp.announceError(exception);
        }
        if (fileURL != null)
            loadModelFromUrl(fileURL);
        if (smiles != null)
            loadModelFromSmiles(smiles);
    }

    /**
     * Loads a molecule from a smiles into jcp
     * 
     * @param fileURL
     */
    public void loadModelFromSmiles(String smiles) {
        if (smiles != null) {
            try {
                SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder
                        .getInstance());
                IMolecule mol = sp.parseSmiles(smiles);
                StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                sdg.setMolecule(mol);
                sdg.generateCoordinates(new Vector2d(0, 1));
                mol = sdg.getMolecule();
                //for some reason, smilesparser sets valencies, which we don't want in jcp
                for(int i=0;i<mol.getAtomCount();i++){
                	mol.getAtom(i).setValency(null);
                }
                IChemModel chemModel = DefaultChemObjectBuilder.getInstance()
                        .newChemModel();
                chemModel.setMoleculeSet(DefaultChemObjectBuilder.getInstance()
                        .newMoleculeSet());
                chemModel.getMoleculeSet().addAtomContainer(mol);
                theJcpp.setChemModel(chemModel);
            } catch (Exception exception) {
                theJcpp.announceError(exception);
            }
        } else {
            theJcpp.setChemModel(new ChemModel());
        }
    }

    public void setSmiles(String smiles) {
        loadModelFromSmiles(smiles);
        theJcpp.get2DHub().updateView();
        repaint();
    }

    /**
     * Loads a molecule from a url into jcp
     * 
     * @param fileURL
     */
    public void loadModelFromUrl(URL fileURL) {
        try {
            IChemModel chemModel = JChemPaint.readFromFileReader(fileURL,
                    fileURL.toString(), null);
            theJcpp.setChemModel(chemModel);
        } catch (Exception exception) {
            theJcpp.announceError(exception);
        }
    }

    @Override
    public void start() {
        RendererModel rendererModel = theJcpp.get2DHub().getRenderer()
                .getRenderer2DModel();
        IChemModel chemModel = theJcpp.getChemModel();
        IControllerModel controllerModel = theJcpp.get2DHub()
                .getController2DModel();

        // Parameter parsing goes here
        loadModelFromParam();
        String atomNumbers = getParameter("atomNumbersVisible");
        if (atomNumbers != null) {
            if (atomNumbers.equals("true"))
                rendererModel.setDrawNumbers(true);
        }

        String background = getParameter("background");
        if (background != null) {
            if (background.indexOf("#") == 0)
                rendererModel.setBackColor(Color.decode(background));
            else
                rendererModel.setBackColor(new Color(Integer
                        .parseInt(background)));
            theJcpp.getRenderPanel()
                    .setBackground(rendererModel.getBackColor());
        }

        if (getParameter("compact") != null
                && getParameter("compact").equals("true")) {
            rendererModel.setIsCompact(true);
        }

        if (getParameter("tooltips") != null) {
            StringTokenizer st = new StringTokenizer(getParameter("tooltips"),
                    "|");
            IAtomContainer container = theJcpp.getChemModel().getBuilder()
                    .newAtomContainer();
            Iterator<IAtomContainer> containers = ChemModelManipulator
                    .getAllAtomContainers(chemModel).iterator();

            while (containers.hasNext()) {
                container.add(containers.next());
            }

            while (st.hasMoreTokens()) {
                IAtom atom = container
                        .getAtom(Integer.parseInt(st.nextToken()) - 1);
                rendererModel.getToolTipTextMap().put(atom, st.nextToken());
            }
            rendererModel.setShowTooltip(true);
        }

        if (getParameter("dotranslate") != null
                && getParameter("dotranslate").equals("false")) {
            GT.setDoTranslate(false);
        }

        if (getParameter("debug") != null
                && getParameter("debug").equals("true")) {
            this.debug = true;
        }

        if (getParameter("impliciths") != null
                && getParameter("impliciths").equals("true")) {
            controllerModel.setAutoUpdateImplicitHydrogens(true);
            rendererModel.setShowImplicitHydrogens(true);
            rendererModel.setShowEndCarbons(true);
        } else {
            controllerModel.setAutoUpdateImplicitHydrogens(false);
            rendererModel.setShowImplicitHydrogens(false);
            rendererModel.setShowEndCarbons(false);

            if (chemModel != null) {
                List<IAtomContainer> atomContainers = ChemModelManipulator
                        .getAllAtomContainers(chemModel);
                for (int i = 0; i < atomContainers.size(); i++) {
                    try {
                        CDKHydrogenAdder.getInstance(
                                atomContainers.get(i).getBuilder())
                                .addImplicitHydrogens(atomContainers.get(i));
                    } catch (CDKException e) {
                        // do nothing
                    }
                }
            }
        }
    }

    @Override
    public void init() {
        prepareExternalFrame();
    }

    @Override
    public void stop() {
    }

    /**
     * @return Returns the theJcpp.
     */
    public AbstractJChemPaintPanel getTheJcpp() {
        return theJcpp;
    }

    /**
     * @param theJcpp
     *            The theJcpp to set.
     */
    public void setTheJcpp(AbstractJChemPaintPanel theJcpp) {
        this.theJcpp = theJcpp;
    }

    /**
     * Gives a mol file of the current molecules in the editor (not reactions)
     * 
     * @return The mol file
     * @throws CDKException
     */
    public String getMolFile() throws CDKException {
        StringWriter sw = new StringWriter();
        MDLWriter mdlwriter = new MDLWriter(sw);
        // mdlwriter.dontWriteAromatic();
        org.openscience.cdk.interfaces.IChemModel som = theJcpp
                .getChemModel();
        mdlwriter.write(som);
        return (sw.toString());
    }

    /**
     * Gives a smiles of the current editor content
     * 
     * @return The smiles
     * @throws CloneNotSupportedException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    public String getSmiles() throws CDKException, ClassNotFoundException,
            IOException, CloneNotSupportedException {
        return CreateSmilesAction.getSmiles(theJcpp.getChemModel());
    }

    /**
     * Gives a chiral smiles of the current editor content
     * 
     * @return The smiles
     * @throws CloneNotSupportedException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    public String getSmilesChiral() throws CDKException,
            ClassNotFoundException, IOException, CloneNotSupportedException {
        return CreateSmilesAction.getChiralSmiles(theJcpp.getChemModel());
    }

    /**
     * This method sets a structure in the editor and leaves the old one. This
     * method replaces all \n characters with the system line separator. This
     * can be used when setting a mol file in an applet without knowing which
     * platform the applet is running on.
     * 
     * @param mol
     *            The mol file to set (V2000)
     * @throws Exception
     */
    public void addMolFileWithReplace(String mol) throws Exception {
        StringBuffer newmol = new StringBuffer();
        int s = 0;
        int e = 0;
        while ((e = mol.indexOf("\\n", s)) >= 0) {
            newmol.append(mol.substring(s, e));
            newmol.append(System.getProperty("file.separator"));
            s = e + 1;
        }
        newmol.append(mol.substring(s));
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(newmol
                .toString()));
        IMolecule cdkmol = (IMolecule) reader.read(DefaultChemObjectBuilder
                .getInstance().newMolecule());
        generateModel(theJcpp, cdkmol, false,false);
        theJcpp.get2DHub().updateView();
        // the newly opened file should nicely fit the screen
        theJcpp.getRenderPanel().setFitToScreen(true);
        theJcpp.getRenderPanel().update(
                theJcpp.getRenderPanel().getGraphics());
        // enable zooming by removing constraint
        theJcpp.getRenderPanel().setFitToScreen(false);
    }

    /**
     * This method sets a new structure in the editor and removes the old one.
     * This method replaces all \n characters with the system line separator.
     * This can be used when setting a mol file in an applet without knowing
     * which platform the applet is running on.
     * 
     * @param mol
     *            The mol file to set
     * @throws CDKException
     */
    public void setMolFileWithReplace(String mol) throws CDKException {
        StringBuffer newmol = new StringBuffer();
        int s = 0;
        int e = 0;
        while ((e = mol.indexOf("\\n", s)) >= 0) {
            newmol.append(mol.substring(s, e));
            newmol.append(System.getProperty("file.separator"));
            s = e + 1;
        }
        setMolFile(newmol.toString());
    }

    /**
     * Sets a mol file in the applet
     * 
     * @param mol
     * @throws Exception
     */
    public void setMolFile(String mol) throws CDKException {
        
        ISimpleChemObjectReader cor = new MDLV2000Reader(new StringReader(mol), Mode.RELAXED);
        IChemModel chemModel = JChemPaint.getChemModelFromReader(cor);
        JChemPaint.cleanUpChemModel(chemModel);
        theJcpp.setChemModel(chemModel);
        theJcpp.get2DHub().updateView();
        // the newly opened file should nicely fit the screen
        theJcpp.getRenderPanel().setFitToScreen(true);
        theJcpp.getRenderPanel().update(
                theJcpp.getRenderPanel().getGraphics());
        // enable zooming by removing constraint
        theJcpp.getRenderPanel().setFitToScreen(false);
    }

    /**
     * Clears the applet
     */
    public void clear() {
        theJcpp.get2DHub().zap();
        theJcpp.get2DHub().updateView();
        theJcpp.getRenderPanel().getRenderer().getRenderer2DModel()
                .setZoomFactor(1);

        IChemObjectSelection selection = new LogicalSelection(
                LogicalSelection.Type.NONE);
        theJcpp.getRenderPanel().getRenderer().getRenderer2DModel()
                .setSelection(selection);
    }

    /**
     * A method for highlighting atoms from JavaScript
     * 
     * @param atom
     *            The atom number (starting with 0)
     */
    public void selectAtom(int atom) {
        RendererModel rendererModel = theJcpp.get2DHub().getRenderer()
                .getRenderer2DModel();
        IChemModel chemModel = theJcpp.getChemModel();
        rendererModel.setExternalHighlightColor(Color.RED);
        IAtomContainer ac = chemModel.getMoleculeSet().getBuilder()
                .newAtomContainer();
        ac.addAtom(chemModel.getMoleculeSet().getMolecule(0).getAtom(atom));
        rendererModel.setExternalSelectedPart(ac);
        getTheJcpp().repaint();
    }

    /**
     * Makes all implicit hydrogens explicit (same as using
     * Edit->Hydrogens->Make all Implicit Hydrogens Explicit)
     */
    public void makeHydrogensExplicit() {
        getTheJcpp().get2DHub().makeAllImplicitExplicit();
        getTheJcpp().repaint();
    }

    /**
     * Makes all explicit hydrogens implicit (same as using
     * Edit->Hydrogens->Make all Explicit Hydrogens Implicit)
     */
    public void makeHydrogensImplicit() {
        getTheJcpp().get2DHub().makeAllExplicitImplicit();
        getTheJcpp().repaint();
    }

    /**
     * @return Returns the jexf.
     */
    private JExternalFrame getJexf() {
        if (jexf == null)
            jexf = new JExternalFrame();
        return jexf;
    }

    /**
     * sets title for external frame adds listener for double clicks in order to
     * open external frame
     */
    private void prepareExternalFrame() {
        if (this.getParameter("name") != null)
            getJexf().setTitle(this.getParameter("name"));
        if (getParameter("detachable") != null
                && getParameter("detachable").equals("true")) {
            getTheJcpp().getRenderPanel().addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    Container applet = (Container)e.getSource();
                    while(!(applet instanceof JChemPaintEditorApplet || applet instanceof JChemPaintViewerApplet)){
                        applet=applet.getParent();
                    }
                    if (e.getButton() == 1 && e.getClickCount() == 2 && applet instanceof JChemPaintViewerApplet)
                        if (!getJexf().isShowing()) {
                            getJexf().show(getTheJcpp());
                        }
                }
            });
        }
        if (getParameter("detachableeditor") != null
                && getParameter("detachableeditor").equals("true")) {
            getTheJcpp().getRenderPanel().addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    Container applet = (Container)e.getSource();
                    while(!(applet instanceof JChemPaintEditorApplet || applet instanceof JChemPaintViewerApplet)){
                        applet=applet.getParent();
                    }
                    if (e.getButton() == 1 && e.getClickCount() == 2 && applet instanceof JChemPaintViewerApplet)
                        if (!getJexf().isShowing()) {
                            final JChemPaintPanel p = new JChemPaintPanel(theJcpp.getChemModel(),JChemPaintEditorApplet.GUI_APPLET,debug);
                            p.setName("appletframe");
                            p.setShowInsertTextField(false);
                            p.setShowStatusBar(false);
                            p.getChemModel().setID("JChemPaint Editor");
                            getJexf();
                            jexf.setTitle("JChemPaint Editor");
                            jexf.add(p);
                            jexf.pack();
                            jexf.setVisible(true);
                            jexf.addWindowListener(new WindowAdapter(){
                                public void windowClosing(WindowEvent e) {
                                    JChemPaintAbstractApplet.this.setChemModel(p.getChemModel());
                                }
                            });
                        }
                }
            });
        }
    }

    protected void setChemModel(IChemModel chemModel) {
        theJcpp.setChemModel(chemModel);
        theJcpp.get2DHub().updateView();
    }

    public static void generateModel(AbstractJChemPaintPanel chemPaintPanel, IMolecule molecule, boolean generateCoordinates, boolean shiftPasted) {
        if (molecule == null) return;

        // get relevant bits from active model
        IChemModel chemModel = chemPaintPanel.getChemModel();
        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        if (moleculeSet == null) {
            moleculeSet = new MoleculeSet();
        }
        
        // On copy & paste on top of an existing drawn structure, prevent the
        // pasted section to be drawn exactly on top or to far away from the 
        // original by shifting it to a fixed position next to it. 
        if (shiftPasted && moleculeSet.getAtomContainer(0)!=null && moleculeSet.getAtomContainer(0).getAtomCount()!=0) {
            // where is the right border of the current structure?
            double maxXCurr = Double.NEGATIVE_INFINITY;
            for (IAtom atom : moleculeSet.getAtomContainer(0).atoms()) {
                if(atom.getPoint2d().x>maxXCurr)
                    maxXCurr = atom.getPoint2d().x;
            }
            // where is the left border of the pasted structure?
            double minXPaste = Double.POSITIVE_INFINITY;
            for (IAtom atom : molecule.atoms()) {
                if(atom.getPoint2d().x<minXPaste)
                    minXPaste = atom.getPoint2d().x;
            }
            // shift the pasted structure to be nicely next to the existing one.
            final int MARGIN=1;
            final double SHIFT = maxXCurr - minXPaste; 
            for (IAtom atom : molecule.atoms()) {
                atom.setPoint2d(new Point2d (atom.getPoint2d().x+MARGIN+SHIFT, atom.getPoint2d().y ));
            }
        }

        if(generateCoordinates){
            // now generate 2D coordinates
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setTemplateHandler(new TemplateHandler(moleculeSet.getBuilder()));
            try {
                sdg.setMolecule(molecule);
                sdg.generateCoordinates(new Vector2d(0, 1));
                molecule = sdg.getMolecule();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        moleculeSet.getAtomContainer(0).add(molecule);

        IUndoRedoFactory i= chemPaintPanel.get2DHub().getUndoRedoFactory();
        UndoRedoHandler ih= chemPaintPanel.get2DHub().getUndoRedoHandler();
        if (i!=null) {
            IUndoRedoable undoredo = i.getAddAtomsAndBondsEdit(chemPaintPanel.get2DHub().getIChemModel(), 
            molecule, "Paste", chemPaintPanel.get2DHub());
            ih.postEdit(undoredo);
        }
        
        //moleculeSet.addMolecule(molecule); // don't create another atom container...
        ControllerHub.avoidOverlap(chemModel);
        chemPaintPanel.getChemModel().setMoleculeSet(moleculeSet);
        chemPaintPanel.get2DHub().updateView();
    }

}
