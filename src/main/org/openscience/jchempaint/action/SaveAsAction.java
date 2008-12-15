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
package org.openscience.jchempaint.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.CDKSourceCodeWriter;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.io.listener.SwingGUIListener;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.JCPPropertyHandler;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.io.IJCPFileFilter;
import org.openscience.jchempaint.io.JCPFileView;
import org.openscience.jchempaint.io.JCPSaveFileFilter;

/**
 * Opens a "Save as" dialog
 *
 */
public class SaveAsAction extends JCPAction
{

    private static final long serialVersionUID = -5138502232232716970L;
    
    protected IChemObjectWriter cow;
    protected static String type = null;
    private FileFilter currentFilter = null;


	/**
	 *  Constructor for the SaveAsAction object
	 */
	public SaveAsAction()
	{
		super();
	}

	/**
	 *  Constructor for the SaveAsAction object
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 */
  public SaveAsAction(JChemPaintPanel jcpPanel, boolean isPopupAction)
	{
		super(jcpPanel, "", isPopupAction);
	}


	/**
	 *  Opens a dialog frame and manages the saving of a file.
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{

		IChemModel jcpm = jcpPanel.getChemModel();
		if (jcpm == null)
		{
			String error = "Nothing to save.";
			JOptionPane.showMessageDialog(jcpPanel, error);
		} else
		{
			saveAs(event);
		}
	}

	protected void saveAs(ActionEvent event)
	{
		int ready=1;
		while(ready==1){
			IChemModel model = jcpPanel.getChemModel();
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(jcpPanel.getCurrentWorkDirectory());
			JCPSaveFileFilter.addChoosableFileFilters(chooser);
			if (jcpPanel.getCurrentSaveFileFilter() != null)
			{
				chooser.setFileFilter(jcpPanel.getCurrentSaveFileFilter());
			}
			chooser.setFileView(new JCPFileView());
	
			int returnVal = chooser.showSaveDialog(jcpPanel);
			
			IChemObject object = getSource(event);
			currentFilter = chooser.getFileFilter();
			if(returnVal==JFileChooser.CANCEL_OPTION)
				ready=0;
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				type = ((IJCPFileFilter) currentFilter).getType();
				File outFile = chooser.getSelectedFile();
				if(outFile.exists()){
					ready=JOptionPane.showConfirmDialog((Component)null,"File "+outFile.getName()+" already exists. Do you want to overwrite it?","File already exists",JOptionPane.YES_NO_OPTION);
				}else{
					try{
						if(new File(outFile.getCanonicalFile()+"."+type).exists()){
							ready=JOptionPane.showConfirmDialog((Component)null,"File "+outFile.getName()+" already exists. Do you want to overwrite it?","File already exists",JOptionPane.YES_NO_OPTION);
						}
					}catch(IOException ex){
						logger.error("IOException when trying to ask for existing file");
					}
					ready=0;
				}
				if(ready==0){
					
					if (object == null)
					{
						// called from main menu
						try
						{
							if (type.equals(JCPSaveFileFilter.mol))
							{
								outFile = saveAsMol(model, outFile);
							} else if (type.equals(JCPSaveFileFilter.cml))
							{
								outFile = saveAsCML2(model, outFile);
							} else if (type.equals(JCPSaveFileFilter.smiles))
							{
								outFile = saveAsSMILES(model, outFile);
							} else if (type.equals(JCPSaveFileFilter.cdk))
							{
								outFile = saveAsCDKSourceCode(model, outFile);
							} else
							{
								String error = "Cannot save file in this format: " + type;
								logger.error(error);
								JOptionPane.showMessageDialog(jcpPanel, error);
								return;
							}
							jcpPanel.setModified(false);
						} catch (Exception exc)
						{
							String error = "Error while writing file: " + exc.getMessage();
							logger.error(error);
							logger.debug(exc);
							JOptionPane.showMessageDialog(jcpPanel, error);
						}
		
					} else if (object instanceof Reaction)
					{
						try
						{
							if (type.equals(JCPSaveFileFilter.cml))
							{
								outFile = saveAsCML2(object, outFile);
							} else
							{
								String error = "Cannot save reaction in this format: " + type;
								logger.error(error);
								JOptionPane.showMessageDialog(jcpPanel, error);
							}
							jcpPanel.setModified(false);
						} catch (Exception exc)
						{
							String error = "Error while writing file: " + exc.getMessage();
							logger.error(error);
							logger.debug(exc);
							JOptionPane.showMessageDialog(jcpPanel, error);
						}
					}
					jcpPanel.setCurrentWorkDirectory(chooser.getCurrentDirectory());
					jcpPanel.setCurrentSaveFileFilter(chooser.getFileFilter());
					jcpPanel.setIsAlreadyAFile(outFile);
					jcpPanel.getChemModel().setID(outFile.getName());
					jcpPanel.setTitle(outFile.getName());
				}
			}
		}
	}

    private boolean askIOSettings() {
        return JCPPropertyHandler.getInstance().getJCPProperties()
            .getProperty("askForIOSettings", "true").equals("true");
    }
    
	protected File saveAsMol(IChemModel model, File outFile) throws Exception
	{
		logger.info("Saving the contents in a MDL molfile file...");
        String fileName = outFile.toString();
        if (!fileName.endsWith(".mol")) {
            fileName += ".mol";
            outFile = new File(fileName);
        }
        outFile=new File(fileName);
        cow = new MDLWriter(new FileWriter(outFile));
		if (cow != null && askIOSettings())
		{
			cow.addChemObjectIOListener(new SwingGUIListener(jcpPanel, 4));
		}
		org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
		cow.write(som);
		cow.close();
		jcpPanel.setTitle(jcpPanel.getChemModel().getID());
		return outFile;
	}

	protected File saveAsCML2(IChemObject object, File outFile) throws Exception
	{
		if(Float.parseFloat(System.getProperty("java.specification.version"))<1.5){
			JOptionPane.showMessageDialog(null,"For saving as CML you need Java 1.5 or higher!");
			return outFile;
		}
		logger.info("Saving the contents in a CML 2.0 file...");
        String fileName = outFile.toString();
        if (!fileName.endsWith(".cml")) {
            fileName += ".cml";
            outFile = new File(fileName);
        }
        FileWriter sw = new FileWriter(outFile);
        Class cmlWriterClass = this.getClass().getClassLoader().loadClass("org.openscience.cdk.io.CMLWriter");

        if (cmlWriterClass != null) {
        	cow = (IChemObjectWriter)cmlWriterClass.newInstance();
        	Constructor constructor = cow.getClass().getConstructor(new Class[]{Writer.class});
        	cow = (IChemObjectWriter)constructor.newInstance(new Object[]{sw});
        } else {
        	// provide a fail save for JChemPaint builds for Java 1.4
        	cow = new MDLWriter(sw);
        }
		if (cow != null && askIOSettings())
		{
			cow.addChemObjectIOListener(new SwingGUIListener(jcpPanel, 4));
		}
		cow.write(object);
		cow.close();
		sw.close();
		jcpPanel.setTitle(jcpPanel.getChemModel().getID());
		return outFile;
	}

	protected File saveAsSMILES(IChemModel model, File outFile) throws Exception
	{
		logger.info("Saving the contents in SMILES format...");
        String fileName = outFile.toString();
        if (!fileName.endsWith(".smi")) {
            fileName += ".smi";
            outFile = new File(fileName);
        }
        cow = new SMILESWriter(new FileWriter(outFile));
		if (cow != null && askIOSettings())
		{
			cow.addChemObjectIOListener(new SwingGUIListener(jcpPanel, 4));
		}
		org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
		cow.write(som);
		cow.close();
		jcpPanel.setTitle(jcpPanel.getChemModel().getID());
		return outFile;
	}

	protected File saveAsCDKSourceCode(IChemModel model, File outFile) throws Exception
	{
		logger.info("Saving the contents as a CDK source code file...");
        String fileName = outFile.toString();
        if (!fileName.endsWith(".cdk")) {
            fileName += ".cdk";
            outFile = new File(fileName);
        }
		cow = new CDKSourceCodeWriter(new FileWriter(outFile));
		if (cow != null && askIOSettings())
		{
			cow.addChemObjectIOListener(new SwingGUIListener(jcpPanel, 4));
		}
		Iterator containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
		while (containers.hasNext()) {
			IAtomContainer ac = (IAtomContainer)containers.next();
			if (ac != null) {
				cow.write(ac);
			} else {
				System.err.println("AC == null!");
			}
		}
		cow.close();
		jcpPanel.setTitle(jcpPanel.getChemModel().getID());
		return outFile;
	}
}

