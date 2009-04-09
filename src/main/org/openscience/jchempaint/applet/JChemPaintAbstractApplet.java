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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JApplet;
import javax.vecmath.Vector2d;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.controller.IControllerModel;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.InsertTextPanel;
import org.openscience.jchempaint.JExternalFrame;
import org.openscience.jchempaint.action.CreateSmilesAction;
import org.openscience.jchempaint.application.JChemPaint;

/**
 * An abstract class for JCP applets, doing parameter parsing
 * 
 */
public abstract class JChemPaintAbstractApplet extends JApplet {
	private AbstractJChemPaintPanel theJcpp = null;
	private JExternalFrame jexf;

	private static String appletInfo = "JChemPaint Applet. See http://cdk.sourceforge.net "
			+ "for more information";

	private static String[][] paramInfo = {
			{ "background", "color", 	"Background color as integer" },
			{ "atomNumbersVisible", "true or false", "should atom numbers be shown"},
			{ "load", "url", "URL of the chemical data" },
			{ "compact", "true or false", "compact means elements shown as dots, no figures etc. (default false)"},
			{ "tooltips", "string like 'atomumber|test|atomnumber|text'", "the texts will be used as tooltips for the respective atoms (leave out if none required"},
			{ "impliciths", "true or false", "the implicit hs will be added from start (default true)"},
			{ "spectrumRenderer", "string", "TODO name of a spectrum applet (see subproject in NMRShiftDB) where peaks should be highlighted when hovering over atom"},
			{ "hightlightTable", "true or false", "TODO if true peaks in a table will be highlighted when hovering over atom, ids are assumed to be tableid$atomnumber (default false)"},
			{ "smiles", "string", "a structure to load as smiles"},
			{ "scrollbars", "true or false", "if the molecule is too big to be displayed in normal size, shall scrollbars be used (default) or the molecule be resized - only for viewer applet"},
			{ "dotranslate", "true or false", "should user interface be translated (default) or not (e. g. if you want an English-only webpage)"},
			{ "detachable", "true or false", "should the applet be detacheable by a double click (default false)"}
	};

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
		String smiles=null;
		try {
			URL documentBase = getDocumentBase();
			String load = getParameter("load");
			if (load != null)
				fileURL = new URL(documentBase, load);
			smiles = getParameter("smiles");
		} catch (Exception exception) {
			System.out.println("Cannot load model: " + exception.toString());
			exception.printStackTrace();
		}
		if(fileURL!=null)
			loadModelFromUrl(fileURL);
		if(smiles!=null)
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
				SmilesParser sp=new SmilesParser(DefaultChemObjectBuilder.getInstance());
				IMolecule mol = sp.parseSmiles(smiles);
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				sdg.setMolecule(mol);
				sdg.generateCoordinates(new Vector2d(0, 1));
				mol=sdg.getMolecule();
				IChemModel chemModel = DefaultChemObjectBuilder.getInstance().newChemModel();
				chemModel.setMoleculeSet(DefaultChemObjectBuilder.getInstance().newMoleculeSet());
				chemModel.getMoleculeSet().addAtomContainer(mol);
				theJcpp.setChemModel(chemModel);
			} catch (Exception exception) {
				System.out.println("Cannot parse model: " + exception.toString());
				exception.printStackTrace();
			}
		}else{
			theJcpp.setChemModel(new ChemModel());
		}
	}


	public void setSmiles(String smiles){
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
			IChemModel chemModel = JChemPaint.readFromFile(new InputStreamReader(fileURL.openStream()), fileURL.toString(), null);
			theJcpp.setChemModel(chemModel);
		} catch (Exception exception) {
			System.out.println("Cannot parse model: " + exception.toString());
			exception.printStackTrace();
		}
	}
	
	@Override
	public void start() {
		RendererModel rendererModel = 
		    theJcpp.get2DHub().getRenderer().getRenderer2DModel();
		IChemModel chemModel = theJcpp.getChemModel();
		IControllerModel controllerModel = 
		    theJcpp.get2DHub().getController2DModel();
		
		//Parameter parsing goes here
		loadModelFromParam();
		String atomNumbers=getParameter("atomNumbersVisible");
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
        }
		
		if (getParameter("compact") != null
                && getParameter("compact").equals("true")) {
			rendererModel.setIsCompact(true);
		}
		
		if (getParameter("tooltips") != null) {
			StringTokenizer st = 
			    new StringTokenizer(getParameter("tooltips"), "|");
			IAtomContainer container = 
			    theJcpp.getChemModel().getBuilder().newAtomContainer();
	    	Iterator<IAtomContainer> containers = 
	    	    ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
	    	
	    	while (containers.hasNext()) {
	    		container.add(containers.next());
	    	}
	    	
			while (st.hasMoreTokens()) {
				IAtom atom = 
				    container.getAtom(Integer.parseInt(st.nextToken()) - 1);
				rendererModel.getToolTipTextMap().put(atom, st.nextToken());
			}
			rendererModel.setShowTooltip(true);
		}
		
		if(getParameter("dotranslate") != null && getParameter("dotranslate").equals("false")){
			GT.setDoTranslate(false);
		}
		
		if (getParameter("impliciths") != null 
		        && getParameter("impliciths").equals("false")) {
			controllerModel.setAutoUpdateImplicitHydrogens(false);
			rendererModel.setShowImplicitHydrogens(false);
			rendererModel.setShowEndCarbons(false);
		} else {
			 controllerModel.setAutoUpdateImplicitHydrogens(true);
			 rendererModel.setShowImplicitHydrogens(true);
			 rendererModel.setShowEndCarbons(true);	
			 
			 if (chemModel != null) {
				 List<IAtomContainer> atomContainers = 
				     ChemModelManipulator.getAllAtomContainers(chemModel);
				 for(int i=0;i<atomContainers.size();i++)
				 {
					 try {
						CDKHydrogenAdder.getInstance(
						        atomContainers.get(i).getBuilder())
						        .addImplicitHydrogens(atomContainers.get(i));
					} catch (CDKException e) {
						//do nothing
					}
				 }
			 }			 
		}
	}
	
	@Override
	public void init(){
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
	 * @param theJcpp The theJcpp to set.
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
	public String getMolFile() throws CDKException{
	    StringWriter sw = new StringWriter();
	    MDLWriter mdlwriter = new MDLWriter(sw);
	    mdlwriter.dontWriteAromatic();
		org.openscience.cdk.interfaces.IMoleculeSet som = theJcpp.getChemModel().getMoleculeSet();
	    mdlwriter.write(som);
	    return(sw.toString());
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
	public String getSmiles() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
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
	public String getSmilesChiral() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException{
	  return CreateSmilesAction.getChiralSmiles(theJcpp.getChemModel());  
	}

  /**
   * This method sets a structure in the editor and leaves the old one.
   * This method replaces all \n characters with the system line separator. This can be used when setting a mol file in an applet
   * without knowing which platform the applet is running on.
   * 
   * @param mol The mol file to set (V2000)
   * @throws Exception
   */
  public void addMolFileWithReplace(String mol) throws Exception{
	StringBuffer newmol=new StringBuffer();
    int s = 0;
    int e = 0;
    while ((e = mol.indexOf("\\n", s)) >= 0) {
    	newmol.append(mol.substring(s, e));
    	newmol.append(System.getProperty("file.separator"));
    	s = e + 1;
    }
    newmol.append(mol.substring(s));
    MDLV2000Reader reader=new MDLV2000Reader(new StringReader(newmol.toString()));
    IMolecule cdkmol=(IMolecule)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
    new InsertTextPanel(theJcpp,null).generateModel(cdkmol);
    theJcpp.get2DHub().updateView();
    repaint();
  }


  /**
   * This method sets a new structure in the editor and removes the old one.
   * This method replaces all \n characters with the system line separator. This can be used when setting a mol file in an applet
   * without knowing which platform the applet is running on.
   * 
   * @param mol The mol file to set
 * @throws CDKException 
   */
  public void setMolFileWithReplace(String mol) throws CDKException{
	StringBuffer newmol=new StringBuffer();
    int s = 0;
    int e = 0;
    while ((e = mol.indexOf("\\n", s)) >= 0) {
      newmol.append(mol.substring(s, e));
      newmol.append(System.getProperty("file.separator"));
      s = e + 1;
    }
    newmol.append(mol.substring(s));
    MDLV2000Reader reader=new MDLV2000Reader(new StringReader(mol));
    IMolecule cdkmol=(IMolecule)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
    theJcpp.setChemModel(ChemModelManipulator.newChemModel(cdkmol));
    theJcpp.get2DHub().updateView();
    repaint();
  }
  
  	/**
  	 * Sets a mol file in the applet
  	 * 
	 * @param mol
	 * @throws Exception
	 */
	public void setMolFile(String mol) throws CDKException{
	    MDLV2000Reader reader=new MDLV2000Reader(new StringReader(mol));
	    IMolecule cdkmol=(IMolecule)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
	    theJcpp.setChemModel(ChemModelManipulator.newChemModel(cdkmol));
	    theJcpp.get2DHub().updateView();
	    repaint();
	}

  
	/**
	 * Clears the applet
	 */
	public void clear(){
	  theJcpp.setChemModel(new ChemModel());
	  theJcpp.get2DHub().updateView();
	  repaint();
	}

	/**
	 * A method for highlighting atoms from JavaScript
	 * 
	 * @param atom The atom number (starting with 0)
	 */
	public void selectAtom(int atom){
		RendererModel rendererModel = 
		    theJcpp.get2DHub().getRenderer().getRenderer2DModel();
		IChemModel chemModel = theJcpp.getChemModel();
	    rendererModel.setExternalHighlightColor(Color.RED);
	    IAtomContainer ac =
	        chemModel.getMoleculeSet().getBuilder().newAtomContainer();
	    ac.addAtom(chemModel.getMoleculeSet().getMolecule(0).getAtom(atom));
	    rendererModel.setExternalSelectedPart(ac);
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
	 * sets title for external frame
	 * adds listener for double clicks in order to open external frame
	 */
	private void prepareExternalFrame() { 
		if (this.getParameter("name") != null)
			getJexf().setTitle(this.getParameter("name"));
		if(getParameter("detachable")!=null && getParameter("detachable").equals("true")){
			getTheJcpp().getRenderPanel().addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 1 && e.getClickCount() == 2)
						if (!getJexf().isShowing()) {
							getJexf().show(getTheJcpp());
					}	
				}
			});
		}
	}
}
