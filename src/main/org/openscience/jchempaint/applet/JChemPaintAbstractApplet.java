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

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.swing.JApplet;
import javax.vecmath.Vector2d;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.jchempaint.InsertTextPanel;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.action.CreateSmilesAction;

/**
 * An abstract class for JCP applets, doing parameter parsing
 * 
 */
public abstract class JChemPaintAbstractApplet extends JApplet {
	private JChemPaintPanel theJcpp = null;
	//JExternalFrame jexf = null;
	private boolean detacheable=false;

	private static String appletInfo = "JChemPaint Applet. See http://cdk.sourceforge.net "
			+ "for more information";

	private static String[][] paramInfo = {
			{ "background", "color", 	"Background color as integer" },
			{ "atomNumbersVisible", "true or false", "should atom numbers be shown"},
			{ "load", "url", "URL of the chemical data" },
			{ "compact", "true or false", "compact means elements shown as dots, no figures etc. (default false)"},
			{ "tooltips", "string like 'atomumber|test|atomnumber|text", "the texts will be used as tooltips for the respective atoms (leave out if none required"},
			{ "impliciths", "true or false", "the implicit hs will be added from start (default false)"},
			{ "spectrumRenderer", "string", "name of a spectrum applet (see subproject in NMRShiftDB) where peaks should be highlighted when hovering over atom"},
			{ "hightlightTable", "true or false", "if true peaks in a table will be highlighted when hovering over atom, ids are assumed to be tableid$atomnumber (default false)"},
			{ "smiles", "string", "a structure to load as smiles"},
			{ "scrollbars", "true or false", "if the molecule is too big to be displayed in normal size, shall scrollbars be used (default) or the molecule be resized - only for viewer applet"},
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


	public void initPanelAndModel(JChemPaintPanel jcpp) {
//		getContentPane().removeAll();
//		getContentPane().setLayout(new BorderLayout());
//TODO		try{
//	    	theModel.setTitle("JCP Applet" /* getNewFrameName() */);
//    		theModel.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
//    	}catch(NullPointerException ex){
//    		//It seems we get an npe here sometimes. the line is not necessary
//    	}
//		// Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
//		// String version = self.getImplementationVersion();
//		// model.setSoftware("JChemPaint " + version);
//		theModel.setSoftware("JChemPaint " /*+  version */);
//		theModel.setGendate((Calendar.getInstance()).getTime().toString());
//		jcpp.setJChemPaintModel(theModel,new Dimension((int)this.getWidth()-100,(int)this.getHeight()-100));
//		jcpp.registerModel(theModel);
//		if(detacheable)
//			jcpp.addFilePopUpMenu();	
//		if(getParameter("compact")!=null && getParameter("compact").equals("true")){
//			theModel.getRendererModel().setIsCompact(true);
//		}
//		if(getParameter("impliciths")!=null && getParameter("impliciths").equals("false")){
//			 theModel.getControllerModel().setAutoUpdateImplicitHydrogens(false);
//			 theModel.getRendererModel().setShowImplicitHydrogens(false);
//			 theModel.getRendererModel().setShowEndCarbons(false);
//		}else{
//			 theModel.getControllerModel().setAutoUpdateImplicitHydrogens(true);
//			 theModel.getRendererModel().setShowImplicitHydrogens(true);
//			 theModel.getRendererModel().setShowEndCarbons(true);
//			 
//			 HydrogenAdder hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
//			 if(theModel.getChemModel()!=null && theModel.getChemModel().getMoleculeSet()!=null){
//	        	java.util.Iterator mols = theModel.getChemModel().getMoleculeSet().molecules();
//				while (mols.hasNext())
//				{
//					org.openscience.cdk.interfaces.IMolecule molecule = (IMolecule)mols.next();
//					if (molecule != null)
//						
//					{
//						try{
//								hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
//						}catch(Exception ex){
//							//do nothing
//						}
//					}
//				}
//			 }
//			 
//		}
//		if(getParameter("tooltips")!=null){
//			StringTokenizer st=new StringTokenizer(getParameter("tooltips"),"|");
//			IAtomContainer container = theModel.getChemModel().getBuilder().newAtomContainer();
//        	Iterator containers = ChemModelManipulator.getAllAtomContainers(theModel.getChemModel()).iterator();
//        	while (containers.hasNext()) {
//        		container.add((IAtomContainer)containers.next());
//        	}
//			while(st.hasMoreTokens()){
//				IAtom atom = container.getAtom(Integer.parseInt(st.nextToken())-1);
//				theModel.getRendererModel().getToolTipTextMap().put(atom,st.nextToken());
//			}
//			theModel.getRendererModel().setShowTooltip(true);
//		}
//		if(theJcpp.getJChemPaintModel()!=null){
//			jcpp.scaleAndCenterMolecule(theModel.getChemModel(),new Dimension((int)this.getSize().getWidth()-100,(int)this.getSize().getHeight()-100));
//		if(theModel.getChemModel().getMoleculeSet()!=null){
//	        int smallestX=Integer.MAX_VALUE;
//	        int largestX=Integer.MIN_VALUE;
//	        int smallestY=Integer.MAX_VALUE;
//	        int largestY=Integer.MIN_VALUE;
//	        for(int i=0;i<theModel.getChemModel().getMoleculeSet().getMoleculeCount();i++){
//	          for(int k=0;k<theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtomCount();k++){
//	            if(((Point2d)theModel.getRendererModel().getRenderingCoordinate(theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtom(k))).x<smallestX)
//	              smallestX=(int)((Point2d)theModel.getRendererModel().getRenderingCoordinate(theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtom(k))).x;
//	            if(((Point2d)theModel.getRendererModel().getRenderingCoordinate(theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtom(k))).x>largestX)
//	              largestX=(int)((Point2d)theModel.getRendererModel().getRenderingCoordinate(theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtom(k))).x;
//	            if(((Point2d)theModel.getRendererModel().getRenderingCoordinate(theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtom(k))).y<smallestY)
//	              smallestY=(int)((Point2d)theModel.getRendererModel().getRenderingCoordinate(theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtom(k))).y;
//	            if(((Point2d)theModel.getRendererModel().getRenderingCoordinate(theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtom(k))).y>largestY)
//	              largestY=(int)((Point2d)theModel.getRendererModel().getRenderingCoordinate(theModel.getChemModel().getMoleculeSet().getMolecule(i).getAtom(k))).y;
//	          }
//	        }
//	        if(!theModel.getRendererModel().getIsCompact()){
//	        	if(jcpp.isShowscrollbars()){
//			        int x=largestX-smallestX+30;
//			        int y=largestY - smallestY+30;
//			        if(x<300)
//			          x=300;
//			        if(y<300)
//			          y=300;
//			        //the -60 is to prevent the vertical scrollbar to display even if not needed
//			        theModel.getRendererModel().setBackgroundDimension(new Dimension(x,y-60));
//	        	}
//		        jcpp.scaleAndCenterMolecule(theModel.getChemModel(),theModel.getRendererModel().getBackgroundDimension());
//	        }else{
//	        	theModel.getRendererModel().setBackgroundDimension(new Dimension((int)(.9*this.getSize().getWidth()),(int)(.9*this.getSize().getHeight())));
//	        	IAtomContainer atomContainer = theModel.getChemModel().getBuilder().newAtomContainer();
//            	Iterator containers = ChemModelManipulator.getAllAtomContainers(theModel.getChemModel()).iterator();
//            	while (containers.hasNext()) {
//            		atomContainer.add((IAtomContainer)containers.next());
//            	}
//	    		GeometryTools.translateAllPositive(atomContainer,theModel.getRendererModel().getRenderingCoordinates());
//	    		GeometryTools.scaleMolecule(atomContainer, theModel.getRendererModel().getBackgroundDimension(), 0.8,theModel.getRendererModel().getRenderingCoordinates());			
//	    		GeometryTools.center(atomContainer, theModel.getRendererModel().getBackgroundDimension(),theModel.getRendererModel().getRenderingCoordinates());
//
//	        }
//	      }
//	    }
//		//embedded means that additional instances can't be created, which is
//		// needed for applet as well
//		jcpp.setEmbedded();
//		getContentPane().add(jcpp, BorderLayout.CENTER);
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
		initPanelAndModel(theJcpp);
		theJcpp.repaint();
	}

	/**
	 * Loads a molecule from a url into jcp
	 * 
	 * @param fileURL
	 */
	public void loadModelFromUrl(URL fileURL) {
		try {
			InputStreamReader isReader = new InputStreamReader(fileURL.openStream());
			ISimpleChemObjectReader reader = new ReaderFactory().createReader(isReader);
			ChemModel chemModel = (ChemModel) reader.read(new ChemModel());
    
			if(chemModel.getMoleculeSet()!=null){
				int count=0;
				for(int i=0;i<chemModel.getMoleculeSet().getMoleculeCount();i++){
					for(int k=0;k<chemModel.getMoleculeSet().getMolecule(i).getAtomCount();k++){
						chemModel.getMoleculeSet().getMolecule(i).getAtom(k).setProperty("OriginalNumber", new Integer(count));
						count++;
					}
				}
			}
			theJcpp.setChemModel(chemModel);
		} catch (Exception exception) {
			System.out.println("Cannot parse model: " + exception.toString());
			exception.printStackTrace();
		}
		initPanelAndModel(theJcpp);
	}
	
	@Override
	public void start() {
		//Parameter parsing goes here
		loadModelFromParam();
		String atomNumbers=getParameter("atomNumbersVisible");
		if(atomNumbers!=null){
			if(atomNumbers.equals("true"))
				theJcpp.get2DHub().getIJava2DRenderer().getRenderer2DModel().setDrawNumbers(true);
		}
		String background = getParameter("background");
		if(background!=null){
			if(background.indexOf("#")==0)
				theJcpp.get2DHub().getIJava2DRenderer().getRenderer2DModel().setBackColor(Color.decode(background));
			else
				theJcpp.get2DHub().getIJava2DRenderer().getRenderer2DModel().setBackColor(new Color(Integer.parseInt(background)));
		}
	}
	
	@Override
	public void init(){
		//TODO prepareExternalFrame();
	}

	@Override
	public void stop() {
	}
	/**
	 * @return Returns the theJcpp.
	 */
	public JChemPaintPanel getTheJcpp() {
		return theJcpp;
	}
	/**
	 * @param theJcpp The theJcpp to set.
	 */
	public void setTheJcpp(JChemPaintPanel theJcpp) {
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
    repaint();
  }


  /**
   * This method sets a new structure in the editor and removes the old one.
   * This method replaces all \n characters with the system line separator. This can be used when setting a mol file in an applet
   * without knowing which platform the applet is running on.
   * 
   * @param mol The mol file to set
   */
  public void setMolFileWithReplace(String mol){
	StringBuffer newmol=new StringBuffer();
    int s = 0;
    int e = 0;
    while ((e = mol.indexOf("\\n", s)) >= 0) {
      newmol.append(mol.substring(s, e));
      newmol.append(System.getProperty("file.separator"));
      s = e + 1;
    }
    newmol.append(mol.substring(s));
    //TODO theJcpp.showChemFile(new StringReader(newmol.toString()));
    repaint();
  }
  
  	/**
  	 * Sets a mol file in the applet
  	 * 
	 * @param mol
	 * @throws Exception
	 */
	public void setMolFile(String mol) throws Exception{
		//TODO theJcpp.showChemFile(new StringReader(mol));
		repaint();
	}

  
	/**
	 * Clears the applet
	 */
	public void clear(){
	  theJcpp.setChemModel(new ChemModel());
	  repaint();
	}

//  TODO public void selectAtom(int atom){
//    theJcpp.getJChemPaintModel().getRendererModel().setExternalHighlightColor(Color.RED);
//    IAtomContainer ac=theJcpp.getJChemPaintModel().getChemModel().getMoleculeSet().getBuilder().newAtomContainer();
//    ac.addAtom(theJcpp.getJChemPaintModel().getChemModel().getMoleculeSet().getMolecule(0).getAtom(atom));
//    theJcpp.getJChemPaintModel().getRendererModel().setExternalSelectedPart(ac);
//    getTheJcpp().repaint();
//  }

	/**
	 * @return Returns the jexf.
	 */
//	TODO private JExternalFrame getJexf() {
//		if (jexf == null)
//			jexf = new JExternalFrame();
//		return jexf;
//	}

	/**
	 * sets title for external frame
	 * adds listener for double clicks in order to open external frame
	 */
//	TODO private void prepareExternalFrame() { 
//		if (this.getParameter("name") != null)
//			getJexf().setTitle(this.getParameter("name"));
//		if(getParameter("detachable")!=null && getParameter("detachable").equals("true")){
//			detacheable=true;
//			getTheJcpp().getDrawingPanel().addMouseListener(new MouseAdapter() {
//				public void mousePressed(MouseEvent e) {
//					if (e.getButton() == 1 && e.getClickCount() == 2)
//						if (!getJexf().isShowing()) {
//							getJexf().show(getTheJcpp());
//					}	
//				}
//			});
//		}
//	}
}
