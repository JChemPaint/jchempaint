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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JApplet;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.RGroupQueryReader;
import org.openscience.cdk.io.RGroupQueryWriter;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.JExternalFrame;
import org.openscience.jchempaint.StringHelper;
import org.openscience.jchempaint.action.CreateSmilesAction;
import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.controller.IControllerModel;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;

/**
 * An abstract class for JCP applets, doing parameter parsing.
 * 
 * @jcp.params
 * 
 */
public abstract class JChemPaintAbstractApplet extends JApplet {
    private AbstractJChemPaintPanel theJcpp = null;
    private JExternalFrame jexf;
    protected boolean debug = false;
    protected Set<String> blocked = new HashSet<>();
    
    private static String appletInfo = "JChemPaint Applet. See http://jchempaint.github.com "
            + "for more information";

    public static String[][] paramInfo;
    
    static{
    	List<List<String>> infos = new ArrayList<List<String>>();
    	infos.add(Arrays.asList("background", "color",
                     "Background color as integer or hex starting with #"));
    	infos.add(Arrays.asList("atomNumbersVisible", "true or false",
                     "should atom numbers be shown"));
    	infos.add(Arrays.asList("load", "url", "URL of the chemical data"));
    	infos.add(Arrays.asList("compact", "true or false",
                     "compact means elements shown as dots, no figures etc. (default false)" ));
    	infos.add(Arrays.asList("tooltips",
                     "string like 'atomumber|test|atomnumber|text'",
                     "the texts will be used as tooltips for the respective atoms (leave out if none required"));
    	infos.add(Arrays.asList("impliciths", "true or false",
                     "the implicit hs will be added from start (default true)"));
    	infos.add(Arrays.asList("spectrumRenderer",
                     "string",
                     "name of a spectrum applet (see subproject in NMRShiftDB) where peaks should be highlighted when hovering over atom"));
    	infos.add(Arrays.asList("hightlightTable",
                     "true or false",
                     "if true peaks in a table will be highlighted when hovering over atom, ids are assumed to be tableidX, where X=atomnumber starting with 0 (default false)" ));
    	infos.add(Arrays.asList("smiles", "string", "a structure to load as smiles"));
    	infos.add(Arrays.asList("mol", "string", "a structure to load as MOL V2000"));
    	infos.add(Arrays.asList("scrollbars",
                     "true or false",
                     "if the molecule is too big to be displayed in normal size, shall scrollbars be used (default) or the molecule be resized - only for viewer applet"));
    	infos.add(Arrays.asList("dotranslate",
                     "true or false",
                     "should user interface be translated (default) or not (e. g. if you want an English-only webpage)"));
    	infos.add(Arrays.asList("language",
                         "language code",
                         "a valid language code to use for ui language"));
    	infos.add(Arrays.asList("detachable", "true or false",
                     "should the applet be detacheable by a double click (default false)"));
    	infos.add(Arrays.asList("detachableeditor", "true or false",
                     "should the applet be detacheable as an editor by a double click (default false), only for viewer"));
    	infos.add(Arrays.asList("debug", "true or false",
                     "switches on debug output (default false)"));
    	String resource = "org.openscience.jchempaint.resources.features";
        ResourceBundle featuresDefinition = ResourceBundle.getBundle(resource, Locale.getDefault());
        Iterator<String> featuresit = featuresDefinition.keySet().iterator();
        while(featuresit.hasNext()){
        	String feature = featuresit.next();
        	infos.add(Arrays.asList(feature,"on or off","switches on or off the ui elements of this feature (default on)"));
        }
        paramInfo = new String[infos.size()][3];
        for(int i=0;i<infos.size();i++){
        	paramInfo[i]=infos.get(i).toArray(new String[3]);
        }
    }
    
    /**
     * Gives basic information about the applet.
     * @see java.applet.Applet#getAppletInfo()
     */
    @Override
    public String getAppletInfo() {
        return appletInfo;
    }

    /**
     * Gives informations about applet params.
     * @see java.applet.Applet#getParameterInfo()
     */
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
        String mol = null;
        try {
            URL documentBase = getDocumentBase();
            String load = getParameter("load");
            if (load != null)
                fileURL = new URL(documentBase, load);
            smiles = getParameter("smiles");
            mol = getParameter("mol");
        } catch (Exception exception) {
            theJcpp.announceError(exception);
        }
        if (fileURL != null)
            loadModelFromUrl(fileURL, theJcpp);
        if (smiles != null)
            loadModelFromSmiles(smiles);
        if (mol != null) {
            try {
                setMolFileWithReplace(mol);
            } catch (Exception exception) {
                theJcpp.announceError(exception);
            }
        }
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
                IAtomContainer mol = sp.parseSmiles(smiles);

                //for some reason, smilesparser sets valencies, which we don't want in jcp
                for(int i=0;i<mol.getAtomCount();i++){
                	mol.getAtom(i).setValency(null);
                }
		JChemPaint.generateModel(theJcpp, mol, true, true);
                /*StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                sdg.setMolecule(mol);
                sdg.generateCoordinates(new Vector2d(0, 1));
                mol = sdg.getMolecule();
                mol = new FixBondOrdersTool().kekuliseAromaticRings(mol);
		
                IChemModel chemModel = DefaultChemObjectBuilder.getInstance()
                        .newInstance(IChemModel.class);
                chemModel.setMoleculeSet(DefaultChemObjectBuilder.getInstance()
                        .newInstance(IAtomContainerSet.class));
                chemModel.getMoleculeSet().addAtomContainer(mol);
                theJcpp.setChemModel(chemModel);
				
		IUndoRedoFactory undoRedoFactory= theJcpp.get2DHub().getUndoRedoFactory();
		UndoRedoHandler undoRedoHandler= theJcpp.get2DHub().getUndoRedoHandler();

		if (undoRedoFactory!=null) {
			IUndoRedoable undoredo = undoRedoFactory.getAddAtomsAndBondsEdit(theJcpp.get2DHub().getIChemModel(), 
			mol, null, "Paste", theJcpp.get2DHub());
			undoRedoHandler.postEdit(undoredo);
		}
		theJcpp.updateUndoRedoControls();*/
		
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
    public void loadModelFromUrl(URL fileURL, AbstractJChemPaintPanel panel) {
        try {
            IChemModel chemModel = JChemPaint.readFromFileReader(fileURL,
                    fileURL.toString(), null, panel);
            theJcpp.setChemModel(chemModel);
        } catch (Exception exception) {
            theJcpp.announceError(exception);
        }
    }

    /**
     * NOT FOR USE FROM JavaScript.
     */
    @Override
    public void start() {
        // Parameter parsing goes here
        loadModelFromParam();
        JChemPaintRendererModel rendererModel = theJcpp.get2DHub().getRenderer()
                .getRenderer2DModel();
        IChemModel chemModel = theJcpp.getChemModel();
        IControllerModel controllerModel = theJcpp.get2DHub()
                .getController2DModel();

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
                    .newInstance(IAtomContainer.class);
            Iterator<IAtomContainer> containers = ChemModelManipulator
                    .getAllAtomContainers(chemModel).iterator();

            while (containers.hasNext()) {
                IAtomContainer ac=containers.next();
                container.add(ac);
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

        if (getParameter("language") != null) {
            GT.setLanguage(getParameter("language"));
            theJcpp.updateMenusWithLanguage();
        }
        
        if (getParameter("debug") != null
                && getParameter("debug").equals("true")) {
            this.debug = true;
        }

        if ( (getParameter("impliciths") == null) ||
        	 (getParameter("impliciths") != null && getParameter("impliciths").equals("true")) 
           ) {
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

    /**
     * NOT FOR USE FROM JavaScript.
     */
    @Override
    public void init() {
        String resource = "org.openscience.jchempaint.resources.features";
        ResourceBundle featuresDefinition = ResourceBundle.getBundle(resource, Locale.getDefault());
        Iterator<String> featuresit = featuresDefinition.keySet().iterator();
        while(featuresit.hasNext()){
        	String feature = featuresit.next();
        	if (getParameter(feature) != null
                    && getParameter(feature).equals("off")) {
        		blocked.add(feature);
        		String[] members = StringHelper.tokenize(featuresDefinition.getString(feature));
                Collections.addAll(blocked, members);
        	}
        }
        prepareExternalFrame();
    }

    /**
     * NOT FOR USE FROM JavaScript.
     */
    @Override
    public void stop() {
    }

    /**
     * @return Returns the theJcpp.
     */
    private AbstractJChemPaintPanel getTheJcpp() {
        return theJcpp;
    }

    /**
     * @param theJcpp
     *            The theJcpp to set.
     */
    protected void setTheJcpp(AbstractJChemPaintPanel theJcpp) {
        this.theJcpp = theJcpp;
    }

    /**
     * Gives a mol file of the current molecules in the editor (not reactions).
     * RGroup queries are also saved as .mol files by convention.
     * 
     * @return The mol file
     * @throws CDKException
     */
    public String getMolFile() throws CDKException {

        StringWriter sw = new StringWriter();
        org.openscience.cdk.interfaces.IChemModel som = theJcpp.getChemModel();

    	if (theJcpp.get2DHub().getRGroupHandler()!=null) {
    		try (RGroupQueryWriter rgw = new RGroupQueryWriter(sw)) {
    		    rgw.write(theJcpp.get2DHub().getRGroupHandler().getrGroupQuery());
                rgw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
    	else {
            MDLV2000Writer mdlwriter = new MDLV2000Writer(sw);
            mdlwriter.write(som);
            try {
                mdlwriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
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
        return CreateSmilesAction.getSmiles(theJcpp.getChemModel());
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
            newmol.append(System.getProperty("line.separator"));
            s = e + 2;
        }
        newmol.append(mol.substring(s));
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(newmol
                .toString()));
        IAtomContainer cdkmol = (IAtomContainer) reader.read(DefaultChemObjectBuilder
                .getInstance().newInstance(IAtomContainer.class));
        reader.close();
        JChemPaint.generateModel(theJcpp, cdkmol, false,true);
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
            newmol.append(System.getProperty("line.separator"));
            s = e + 2;
        }
        setMolFile(newmol.toString());
    }

    /**
     * This method sets a new structure in the editor and removes the old one.
     * 
     * @param mol
     * @throws Exception
     */
    public void setMolFile(String mol) throws CDKException {
       
    	ISimpleChemObjectReader cor=null;
    	IChemModel chemModel = null;

    	if (mol.contains("$RGP")) {
    		cor= new RGroupQueryReader(new StringReader(mol)); 
    		chemModel=JChemPaint.getChemModelFromReader(cor, theJcpp);
    	}
    	else {
    		cor= new MDLV2000Reader(new StringReader(mol), Mode.RELAXED); 
    		chemModel=JChemPaint.getChemModelFromReader(cor, theJcpp);
    		JChemPaint.cleanUpChemModel(chemModel,true, theJcpp);
    	}

    	theJcpp.setChemModel(chemModel);
        theJcpp.get2DHub().updateView();

        // the newly opened file should nicely fit the screen
        theJcpp.getRenderPanel().setFitToScreen(true);
        theJcpp.getRenderPanel().update(theJcpp.getRenderPanel().getGraphics());
        // ..enable zooming by removing constraint again
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
     *            The atom number (starting with 0), -1 sets empty selection.
     */
    public void selectAtom(int atom) {
        JChemPaintRendererModel rendererModel = theJcpp.get2DHub().getRenderer()
                .getRenderer2DModel();
        IChemModel chemModel = theJcpp.getChemModel();
        rendererModel.setExternalHighlightColor(Color.RED);
        IAtomContainer ac = chemModel.getMoleculeSet().getBuilder()
                .newInstance(IAtomContainer.class);
        if(atom!=-1){
            ac.addAtom(chemModel.getMoleculeSet().getAtomContainer(0).getAtom(atom));
            rendererModel.setExternalSelectedPart(ac);
        }else{
            rendererModel.setExternalSelectedPart(null);
        }
        getTheJcpp().get2DHub().updateView();
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
     * Tells the mass of the model. This includes all fragments 
     * currently displayed and all their implicit and explicit Hs. 
     * Masses of elements are those of natural abundance. Isotopes are not considered.
     * 
     * @return
     */
    public double getMolMass(){
        IMolecularFormula wholeModel = theJcpp.get2DHub().getIChemModel().getBuilder()
            .newInstance(IMolecularFormula.class);
        Iterator<IAtomContainer> containers = ChemModelManipulator
            .getAllAtomContainers(theJcpp.get2DHub().getIChemModel()).iterator();
        int implicitHs = 0;
        while (containers.hasNext()) {
            for (IAtom atom : containers.next().atoms()) {
                wholeModel.addIsotope(atom);
                if (atom.getImplicitHydrogenCount() != null) {
                    implicitHs += atom.getImplicitHydrogenCount();
                }
            }
        }
        try {
            if (implicitHs > 0)
                wholeModel.addIsotope(Isotopes.getInstance().getMajorIsotope(1),
                                      implicitHs);
        } catch (IOException e) {
        // do nothing
        }
        return MolecularFormulaManipulator.getNaturalExactMass(wholeModel);
    }

    /**
     * Tells the molecular formula of the model. This includes all fragments 
     * currently displayed and all their implicit and explicit Hs.
     * 
     * @return The formula.
     */
    public String getMolFormula(){
        return theJcpp.get2DHub().getFormula();
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
                            final JChemPaintPanel p = new JChemPaintPanel(theJcpp.getChemModel(), JChemPaintEditorApplet.GUI_APPLET, debug, JChemPaintAbstractApplet.this, blocked);
                            p.setName("appletframe");
                            p.setShowInsertTextField(false);
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
}
