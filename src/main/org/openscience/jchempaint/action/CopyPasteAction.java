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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.List;

import javax.swing.JOptionPane;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.MoveModule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.INChIPlainTextReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.LogicalSelection;
import org.openscience.cdk.renderer.selection.RectangleSelection;
import org.openscience.cdk.renderer.selection.ShapeSelection;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.InsertTextPanel;

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


	private void addToClipboard(Clipboard clipboard, IAtomContainer container) {
	    try {
    	    JcpSelection jcpselection = new JcpSelection(container);
            clipboard.setContents(jcpselection,null);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private boolean supported(Transferable transfer, DataFlavor flavor) {
	    return transfer != null && transfer.isDataFlavorSupported(flavor);
	}

	public void actionPerformed(ActionEvent e) {
    	    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    		handleSystemClipboard(sysClip);
	        logger.info("  type  ", type);
	        logger.debug("  source ", e.getSource());

	        RendererModel renderModel =
	            jcpPanel.get2DHub().getRenderer().getRenderer2DModel();
	        IChemModel chemModel = jcpPanel.getChemModel();

	        if ("copy".equals(type)) {
    			IAtom atomInRange = null;
    			IChemObject object = getSource(e);
    			logger.debug("Source of call: ", object);
    			if (object instanceof IAtom) {
    				atomInRange = (IAtom) object;
    			} else {
    				atomInRange = renderModel.getHighlightedAtom();
    			}
    			if (atomInRange != null) {
    			    IAtomContainer tocopyclone =
    			        atomInRange.getBuilder().newAtomContainer();
    			    try {
                        tocopyclone.addAtom((IAtom) atomInRange.clone());
                        addToClipboard(sysClip, tocopyclone);
    			    } catch (CloneNotSupportedException e1) {
    			        e1.printStackTrace();
    			    }
    			}
    			else if(renderModel.getHighlightedBond()!=null){
    				IBond bond = renderModel.getHighlightedBond();
    				if (bond != null) {
        			    IAtomContainer tocopyclone =
        			        bond.getBuilder().newAtomContainer();
        			    try {
                            tocopyclone.addAtom((IAtom) bond.getAtom(0).clone());
                            tocopyclone.addAtom((IAtom) bond.getAtom(1).clone());
                            tocopyclone.addBond(bond.getBuilder().newBond(tocopyclone.getAtom(0), tocopyclone.getAtom(1), bond.getOrder()));
                            addToClipboard(sysClip, tocopyclone);
        			    } catch (CloneNotSupportedException e1) {
        			        e1.printStackTrace();
        			    }
    				}
    			}else if(renderModel.getSelection().getConnectedAtomContainer()!=null){
	        		addToClipboard(sysClip,
	                    renderModel.getSelection().getConnectedAtomContainer());
	        	}else{
	        		List<IAtomContainer> acs=ChemModelManipulator.getAllAtomContainers(chemModel);
	        		IAtomContainer allinone=chemModel.getBuilder().newAtomContainer();
	        		for(int i=0;i<acs.size();i++){
	        			allinone.add(acs.get(i));
	        		}
	        		addToClipboard(sysClip,allinone);
	        	}
	        } else if ("copyAsSmiles".equals(type)) {
	        	try {
		        	if(renderModel.getSelection().getConnectedAtomContainer()!=null){
		        		SmilesGenerator sg=new SmilesGenerator();
		        		sysClip.setContents(new SmilesSelection(sg.createSMILES(renderModel.getSelection().getConnectedAtomContainer().getBuilder().newMolecule(renderModel.getSelection().getConnectedAtomContainer()))),null);
		        	}else{
		        		sysClip.setContents(new SmilesSelection(CreateSmilesAction.getSmiles(chemModel)),null);
		        	}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        } else if ("delete".equals(type)) {
    			IAtom atomInRange = null;
    			IChemObject object = getSource(e);
    			logger.debug("Source of call: ", object);
    			if (object instanceof IAtom) {
    				atomInRange = (IAtom) object;
    			} else {
    				atomInRange = renderModel.getHighlightedAtom();
    			}
    			if (atomInRange != null) {
    				jcpPanel.get2DHub().removeAtom(atomInRange);
    			}
    			else if(renderModel.getHighlightedBond()!=null){
    				jcpPanel.get2DHub().removeBond(renderModel.getHighlightedBond());
    			}else if(renderModel.getSelection().getConnectedAtomContainer()!=null){
    				IChemObjectSelection selection = renderModel.getSelection();
	                IAtomContainer selected = selection.getConnectedAtomContainer();
	                jcpPanel.get2DHub().deleteFragment(selected);
	                renderModel.setSelection(new LogicalSelection(
	                        LogicalSelection.Type.NONE));
	                jcpPanel.get2DHub().updateView();
	            }
	        } else if ("paste".equals(type)) {
	        	Transferable transfer = sysClip.getContents( null );
	        	ISimpleChemObjectReader reader = null;
		        String content=null;
	        	// if a MIME type is given ...
	        	try {
	        	    if (supported(transfer, molFlavor)) {

	        	        String mol =
	        	            (String) transfer.getTransferData(molFlavor);
	        	        reader = new MDLV2000Reader(new StringReader(mol));
	        	    } else if (supported(transfer, DataFlavor.stringFlavor)) {
	        	        // otherwise, try to use the ReaderFactory...
	        	        content = (String) transfer.getTransferData(
	        	                DataFlavor.stringFlavor);
	        	        reader = new ReaderFactory().createReader(
	        	                new StringReader(content));
	        	    }
	        	} catch (UnsupportedFlavorException e1) {
	        	    e1.printStackTrace();
	        	} catch (IOException e1) {
	        	    e1.printStackTrace();
	        	}

    			IMolecule toPaste = null;
        		if (reader != null) {
        		    IMolecule readMolecule =
        		        chemModel.getBuilder().newMolecule();
        			try {
                        if (reader.accepts(Molecule.class)) {
                        	toPaste = (IMolecule) reader.read(readMolecule);
                        } else if (reader.accepts(ChemFile.class)) {
                        	toPaste = readMolecule;
                        	IChemFile file = (IChemFile) reader.read(
                        	            chemModel.getBuilder().newChemModel());
                        	for (IAtomContainer ac :
                        	    ChemFileManipulator.getAllAtomContainers(file)) {
                        	    toPaste.add(ac);
                        	}
                        }
                    } catch (CDKException e1) {
                        e1.printStackTrace();
                    }
        		}
        		//we just try smiles and inchi if no reader is found for content
        		if (toPaste == null &&
        		        supported(transfer, DataFlavor.stringFlavor)) {
        			try{
        				SmilesParser sp = new SmilesParser(
        				        DefaultChemObjectBuilder.getInstance());
        				toPaste = sp.parseSmiles(
        				        (String) transfer.getTransferData(
        				                DataFlavor.stringFlavor));

        				StructureDiagramGenerator sdg =
        				    new StructureDiagramGenerator((IMolecule)toPaste);

                        sdg.setTemplateHandler(
                            new TemplateHandler(toPaste.getBuilder())
                        );
                        sdg.generateCoordinates();
        			} catch (Exception ex) {
        				if (content.indexOf("INChI")>-1) { // handle it as an InChI
        		            try {
        		            	StringReader sr = new StringReader(content);
        		                INChIPlainTextReader inchireader = new INChIPlainTextReader(sr);
        		                IChemFile mol = DefaultChemObjectBuilder.getInstance().newChemFile();
        		                toPaste = ((IChemFile) inchireader.read(mol)).getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
    	        				StructureDiagramGenerator sdg =
    	        				    new StructureDiagramGenerator((IMolecule)toPaste);

    	                        sdg.setTemplateHandler(
    	                            new TemplateHandler(toPaste.getBuilder())
    	                        );
    	                        sdg.generateCoordinates();
    	                    } catch (Exception e2) {
        		            	e2.printStackTrace();
        		            }        			
        		        }
        			}
        		}
	            if (toPaste != null) {
	            	InsertTextPanel.generateModel(jcpPanel, toPaste, false);

	                //We select the inserted structure
	                IChemObjectSelection selection
	                    = new LogicalSelection(LogicalSelection.Type.ALL);

	                selection.select(ChemModelManipulator.newChemModel(toPaste));
	                renderModel.setSelection(selection);
	            	jcpPanel.setMoveAction();

	    			jcpPanel.get2DHub().setActiveDrawModule(new MoveModule(jcpPanel.get2DHub()));
	            }else{
	            	JOptionPane.showMessageDialog(jcpPanel, GT._("The content you tried to copy could not be read to any known format"), GT._("Could not process content"), JOptionPane.WARNING_MESSAGE);
	            }
        	} else if (type.equals("cut")) {
    			IAtom atomInRange = null;
    			IChemObject object = getSource(e);
    			logger.debug("Source of call: ", object);
    			if (object instanceof IAtom) {
    				atomInRange = (IAtom) object;
    			} else {
    				atomInRange = renderModel.getHighlightedAtom();
    			}
    			if (atomInRange != null) {
    			    IAtomContainer tocopyclone =
    			        atomInRange.getBuilder().newAtomContainer();
    			    try {
                        tocopyclone.addAtom((IAtom) atomInRange.clone());
                        addToClipboard(sysClip, tocopyclone);
    			    } catch (CloneNotSupportedException e1) {
    			        e1.printStackTrace();
    			    }
    				jcpPanel.get2DHub().removeAtom(atomInRange);
    			}
    			else {
    				IBond bond = renderModel.getHighlightedBond();
    				if (bond != null) {
    					jcpPanel.get2DHub().removeBond(bond);
    				}
    			}
    		}
    		else if (type.equals("cutSelected")) {
    			logger.debug("Deleting all selected atoms...");
    			IAtomContainer selected =
    			    renderModel.getSelection().getConnectedAtomContainer();
    			if (selected == null || selected.getAtomCount() == 0) {
    				JOptionPane.showMessageDialog(jcpPanel,
    				        "No selection made. Please select some " +
    				        		"atoms first!",
    				        "Error warning", JOptionPane.WARNING_MESSAGE);
    			} else {
    				IAtomContainer tocopyclone;
                    try {
                        tocopyclone = (IAtomContainer) selected.clone();
    				addToClipboard(sysClip, tocopyclone);
                    } catch (CloneNotSupportedException e1) {
                        e1.printStackTrace();
                    }
    				logger.debug("Found # atoms to delete: ",
    				        selected.getAtomCount());
    				jcpPanel.get2DHub().deleteFragment(selected);
    			}
    			renderModel.setSelection(
    			        new LogicalSelection(LogicalSelection.Type.NONE));

    		}
    		else if (type.equals("selectAll")) {
    			ControllerHub hub = jcpPanel.get2DHub();
    		    IChemObjectSelection allSelection =
    		        new LogicalSelection(LogicalSelection.Type.ALL);

    		    allSelection.select(hub.getIChemModel());
    		    renderModel.setSelection(allSelection);
    			jcpPanel.setMoveAction();
    			hub.setActiveDrawModule(new MoveModule(hub));
    		} else if (type.equals("selectMolecule")) {
    			IChemObject object = getSource(e);
    			IAtomContainer relevantAtomContainer = null;
    			if (object instanceof IAtom) {
    				relevantAtomContainer =
    				    ChemModelManipulator.getRelevantAtomContainer(
    				            chemModel,(IAtom)object);
    			} else if (object instanceof IBond) {
    				relevantAtomContainer =
    				    ChemModelManipulator.getRelevantAtomContainer(
    				            chemModel,(IBond)object);
    			} else {
    				logger.warn("selectMolecule not defined for the calling " +
    						"object ", object);
    			}
    			if (relevantAtomContainer != null) {
    	        	ShapeSelection container = new RectangleSelection();
    	        	for (IAtom atom : relevantAtomContainer.atoms()) {
    	        		container.atoms.add(atom);
    	        	}
    	        	for (IBond bond : relevantAtomContainer.bonds()) {
    	        		container.bonds.add(bond);
    	        	}
    				renderModel.setSelection(container);
    			}
    		} else if (type.equals("selectFromChemObject")) {
    			// FIXME: implement for others than Reaction, Atom, Bond
    			IChemObject object = getSource(e);
    			if (object instanceof IAtom) {
    				ShapeSelection container = new RectangleSelection();
    				container.atoms.add((IAtom) object);
    				renderModel.setSelection(container);
    			}
    			else if (object instanceof IBond) {
    				ShapeSelection container = new RectangleSelection();
    				container.bonds.add((IBond) object);
    				renderModel.setSelection(container);
    			}
    			else if (object instanceof IReaction) {
    				IAtomContainer wholeModel =
    				    jcpPanel.getChemModel().getBuilder().newAtomContainer();
    	        	for (IAtomContainer container :
    	        	    ReactionManipulator.getAllAtomContainers(
    	        	            (IReaction)object)) {
    	        	    wholeModel.add(container);
    	        	}
    	        	ShapeSelection container = new RectangleSelection();
    	        	for (IAtom atom : wholeModel.atoms()) {
                        container.atoms.add(atom);
                    }
                    for (IBond bond : wholeModel.bonds()) {
                        container.bonds.add(bond);
                    }
                    renderModel.setSelection(container);
    			}
    			else {
    				logger.warn("Cannot select everything in : ", object);
    			}
    		}
            jcpPanel.get2DHub().updateView();
            jcpPanel.updateStatusBar();

    }

    private void handleSystemClipboard(Clipboard clipboard) {
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

      public JcpSelection(IAtomContainer tocopy1) throws Exception {
    	  IMolecule tocopy= tocopy1.getBuilder().newMolecule(tocopy1);
    	  // MDL mol output
          StringWriter sw = new StringWriter();
          new MDLWriter(sw).writeMolecule(tocopy);
    	  this.mol=sw.toString();
    	  SmilesGenerator sg=new SmilesGenerator();
    	  smiles = sg.createSMILES(tocopy);
    	  // SVG output
    	  svg=jcpPanel.getSVGString();
    	  // CML output
    	  sw = new StringWriter();
    	  Class cmlWriterClass = null;
    	  try {
    		  cmlWriterClass = this.getClass().getClassLoader().loadClass(
    		          "org.openscience.cdk.io.CMLWriter");
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
    
   class SmilesSelection implements Transferable, ClipboardOwner {
		private DataFlavor [] supportedFlavors = {
				DataFlavor.stringFlavor
		};
		
		String smiles;

		public SmilesSelection(String smiles) throws Exception {
			this.smiles = smiles;
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
	      	if(parFlavor.equals(DataFlavor.stringFlavor)) {
	      		return smiles;
	    	} else {
	    		throw new UnsupportedFlavorException (parFlavor);
	    	}
      	}

        public void lostOwnership (Clipboard parClipboard, Transferable parTransferable) {
        	System.out.println ("Lost ownership");
        }
     }
}

