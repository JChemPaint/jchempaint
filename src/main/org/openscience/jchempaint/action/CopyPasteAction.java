/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egon Willighagen, Stefan Kuhn
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
package org.openscience.jchempaint.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.controller.MoveModule;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Action to copy/paste structures.
 *
 * @cdk.bug    1288449
 */
public class CopyPasteAction extends JCPAction{

	private static final long serialVersionUID = -3343207264261279526L;
	
	private DataFlavor molFlavor = new DataFlavor(
		"chemical/x-mdl-molfile", "mdl mol file format");
	private DataFlavor svgFlavor = new DataFlavor(
		"image/svg+xml",          "scalable vector graphics");
	private DataFlavor cmlFlavor = new DataFlavor(
		  "image/cml",          "chemical markup language");
    
	public void actionPerformed(ActionEvent e) {
    	try {
    		handleSystemClipboard();
	        logger.info("  type  ", type);
	        logger.debug("  source ", e.getSource());
	        Renderer2DModel renderModel = jcpPanel.get2DHub().getIJava2DRenderer().getRenderer2DModel();
	        if ("copy".equals(type)) {
	            IAtomContainer tocopy = jcpPanel.getChemModel().getBuilder().newAtomContainer();
	            for(int i=0;i<renderModel.getShapeSelection().atoms.size();i++){
	            	tocopy.addAtom(renderModel.getShapeSelection().atoms.get(i));
	            }
	            for(int i=0;i<renderModel.getShapeSelection().bonds.size();i++){
	            	tocopy.addBond(renderModel.getShapeSelection().bonds.get(i));
	            	if(!tocopy.contains(renderModel.getShapeSelection().bonds.get(i).getAtom(0)))
	            		tocopy.addAtom((renderModel.getShapeSelection().bonds.get(i).getAtom(0)));
	            	if(!tocopy.contains(renderModel.getShapeSelection().bonds.get(i).getAtom(1)))
	            		tocopy.addAtom((renderModel.getShapeSelection().bonds.get(i).getAtom(1)));	            		
	            }
	            if (tocopy == null) {
	            	return;
	            }
	            Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
	            JcpSelection jcpselection=new JcpSelection(tocopy);
	            sysClip.setContents(jcpselection,null);
	        } else if ("paste".equals(type)) {
	        	Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
	        	Transferable transfer = sysClip.getContents( null );
	        	ISimpleChemObjectReader reader = null;
	        	// if a MIME type is given ...
	        	if (transfer!=null && (transfer.isDataFlavorSupported (molFlavor))) {
	        		String mol = (String) transfer.getTransferData (molFlavor);
	        		logger.debug("Dataflavor molFlavor found");
		        	reader = new MDLV2000Reader(new StringReader(mol));
	        	} else if(transfer!=null && (transfer.isDataFlavorSupported (DataFlavor.stringFlavor))) {
	        		// otherwise, try to use the ReaderFactory...
	        		logger.debug("Dataflavor stringFlavor found");
	        		String content = (String) transfer.getTransferData (DataFlavor.stringFlavor);
	        		try {
	        			reader = new ReaderFactory().createReader(new StringReader(content));
	        		} catch (Exception exception) {
	        			logger.warn("Pastes string is not recognized.");
	        		}
	        	}
    			IAtomContainer topaste = null;
        		if (reader != null) {
        			if (reader.accepts(Molecule.class)) { 
        				topaste = (IAtomContainer) reader.read(new Molecule());
        			} else if (reader.accepts(ChemFile.class)) {
        				topaste = new Molecule();
        				IChemFile file = (IChemFile)reader.read(new ChemFile());
                    	Iterator containers = ChemFileManipulator.getAllAtomContainers(file).iterator();
                    	while (containers.hasNext()) {
                    		topaste.add((IAtomContainer)containers.next());
                    	}
        			}
        		}
        		if(topaste==null && transfer!=null && (transfer.isDataFlavorSupported (DataFlavor.stringFlavor))) {
        			try{
        				SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        				topaste = sp.parseSmiles((String) transfer.getTransferData (DataFlavor.stringFlavor));
        				StructureDiagramGenerator sdg = new StructureDiagramGenerator((Molecule)topaste);
                        sdg.setTemplateHandler(
                            new TemplateHandler(topaste.getBuilder())
                        );
                        sdg.generateCoordinates();
        			}catch(Exception ex){
        				//we just try smiles
        			}
        		}
	            if (topaste != null) {
	                //translate the new structure a bit
	                GeometryTools.translate2D(topaste, renderModel.getHighlightRadiusModel(), renderModel.getHighlightRadiusModel()); //in pixels
	                IChemModel chemModel = jcpPanel.getChemModel();
	                IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
	                if (moleculeSet == null) {
	                    moleculeSet = new MoleculeSet();
	                }
	                moleculeSet.addAtomContainer(topaste);
	                jcpPanel.getChemModel().setMoleculeSet(moleculeSet);
	                //We select the inserted structure
	            	renderModel.getShapeSelection().clear();
	            	List<IAtom> atoms=new ArrayList<IAtom>();
	            	for(IAtom atom:topaste.atoms()){
	            		atoms.add(atom);
	            	}
	    			renderModel.getShapeSelection().atoms.addAll(atoms);
	            	List<IBond> bonds=new ArrayList<IBond>();
	            	for(IBond bond:topaste.bonds()){
	            		bonds.add(bond);
	            	}
	            	renderModel.getShapeSelection().bonds.addAll(bonds);
	            	jcpPanel.setMoveAction();
	    			jcpPanel.get2DHub().setActiveDrawModule(new MoveModule(jcpPanel.get2DHub()));
	                jcpPanel.get2DHub().updateView();
	            }
        	}
    	} catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    void handleSystemClipboard()
    {
		Clipboard clipboard = jcpPanel.getToolkit().getSystemClipboard();
		Transferable clipboardContent = clipboard.getContents(this);
		DataFlavor flavors[]=clipboardContent.getTransferDataFlavors();
		String text = "System.clipoard content";
		for(int i=0;i<flavors.length;++i)
		{
			text+="\n\n Name: "+ flavors[i].getHumanPresentableName();
			text+="\n MIME Type: "+flavors[i].getMimeType();
			text+="\n Class: ";
			Class cl = flavors[i].getRepresentationClass();
			if(cl==null) text+="null";
			else text+=cl.getName();
		}
		logger.debug(text);
    }

    class JcpSelection implements Transferable, ClipboardOwner {
  	  private DataFlavor [] supportedFlavors = {
  	      molFlavor, DataFlavor.stringFlavor, svgFlavor, cmlFlavor
  	  };
      String mol;
      String smiles;
      String svg;
      String cml;

      public JcpSelection (IAtomContainer tocopy1) throws Exception{
    	  Molecule tocopy=new Molecule(tocopy1);
    	  // MDL mol output
          StringWriter sw = new StringWriter();
          new MDLWriter(sw).writeMolecule(tocopy);
    	  this.mol=sw.toString();
    	  SmilesGenerator sg=new SmilesGenerator();
    	  smiles = sg.createSMILES(tocopy);
    	  // SVG output
    	  /*TODO sw=new StringWriter();
    	  IChemObjectWriter cow = new SVGWriter(sw);
    	  cow.write(tocopy);
    	  cow.close();
    	  svg=sw.toString();*/
    	  // CML output
    	  sw = new StringWriter();
    	  Class cmlWriterClass = null;
    	  try {
    		  cmlWriterClass = this.getClass().getClassLoader().loadClass("org.openscience.cdk.io.CMLWriter");
    	  } catch (Exception exception) {
    		  logger.error("Could not load CMLWriter: ", exception.getMessage());
    		  logger.debug(exception);
    	  }
    	  if (cmlWriterClass != null) {
    		  IChemObjectWriter cow = (IChemObjectWriter)cmlWriterClass.newInstance();
    		  Constructor constructor = cow.getClass().getConstructor(new Class[]{Writer.class});
    		  cow = (IChemObjectWriter)constructor.newInstance(new Object[]{sw});
    		  cow.write(tocopy);
    		  cow.close();
    	  }
    	  cml=sw.toString();
      }
    	
      public synchronized DataFlavor [] getTransferDataFlavors () {
    	return (supportedFlavors);
   	  }
      
      public boolean isDataFlavorSupported (DataFlavor parFlavor) {
    	  for(int i=0;i<supportedFlavors.length;i++){
    		  if(supportedFlavors[i].equals(parFlavor))
    			  return true;
    	  }
    	  return false;
      }
    	
      public synchronized Object getTransferData (DataFlavor parFlavor)	throws UnsupportedFlavorException {
    	if (parFlavor.equals (molFlavor)) {
    		return mol;
    	} else if(parFlavor.equals(DataFlavor.stringFlavor)) {
    		return smiles;
    	} else if(parFlavor.equals(cmlFlavor)) {
    		return cml;
    	} else if(parFlavor.equals(svgFlavor)) {
    		return svg;
    	} else {
    		throw new UnsupportedFlavorException (parFlavor);
    	}
      }
      
      public void lostOwnership (Clipboard parClipboard, Transferable parTransferable) {
    	System.out.println ("Lost ownership");
      }
   }
}

