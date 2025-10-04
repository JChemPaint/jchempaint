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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.io.CDKSourceCodeWriter;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.MDLRXNWriter;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.RGroupQueryWriter;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.io.listener.SwingGUIListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JCPPropertyHandler;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.inchi.InChI;
import org.openscience.jchempaint.inchi.InChITool;
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
    protected boolean wasCancelled = false;


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
    public SaveAsAction(AbstractJChemPaintPanel jcpPanel, boolean isPopupAction)
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
            String error = GT.get("Nothing to save.");
            JOptionPane.showMessageDialog(jcpPanel, error,error,JOptionPane.WARNING_MESSAGE);
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
            chooser.setName("save");
            chooser.setCurrentDirectory(jcpPanel.getCurrentWorkDirectory());
            chooser.setAcceptAllFileFilterUsed(false);
            JCPSaveFileFilter.addChoosableFileFilters(chooser);
            if (jcpPanel.getCurrentSaveFileFilter() != null)
            {
                for(int i=0;i<chooser.getChoosableFileFilters().length;i++){
                    if(chooser.getChoosableFileFilters()[i].getDescription().equals(jcpPanel.getCurrentSaveFileFilter().getDescription()))
                        chooser.setFileFilter(chooser.getChoosableFileFilters()[i]);
                }
            } else {
                chooser.setFileFilter(chooser.getChoosableFileFilters()[0]);
            }
            chooser.setFileView(new JCPFileView());
            if(jcpPanel.isAlreadyAFile()!=null)
                chooser.setSelectedFile(jcpPanel.isAlreadyAFile());

            int returnVal = chooser.showSaveDialog(jcpPanel);

            IChemObject object = getSource(event);
            FileFilter currentFilter = chooser.getFileFilter();
            if(returnVal==JFileChooser.CANCEL_OPTION){
                ready=0;
                wasCancelled = true;
            }
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                if(!(currentFilter instanceof IJCPFileFilter)){
                    JOptionPane.showMessageDialog(jcpPanel, GT.get("Please choose a file type!"), GT.get("No file type chosen"), JOptionPane.INFORMATION_MESSAGE);
                    return;
                }else{
                    type = ((IJCPFileFilter) currentFilter).getType();
                    File outFile = chooser.getSelectedFile();
                    if(outFile.exists()){
                        ready=JOptionPane.showConfirmDialog((Component)null,
                        GT.get("File {0} already exists. Do you want to overwrite it?", outFile.getName()), 
                        GT.get("File already exists"),JOptionPane.YES_NO_OPTION);
                    }else{
                        try{
                            if(new File(outFile.getCanonicalFile()+"."+type).exists()){
                                ready=JOptionPane.showConfirmDialog((Component)null,
                                GT.get("File {0} already exists. Do you want to overwrite it?", outFile.getName()), 
                                GT.get("File already exists"),JOptionPane.YES_NO_OPTION);
                            }
                        }catch(Throwable ex){
                            jcpPanel.announceError(ex);
                        }
                        ready=0;
                    }
                    if(ready==0){

                        if (object == null)
                        {
                            // called from main menu, only possibility
                            try
                            {
                                if (type.equals(JCPSaveFileFilter.mol))
                                {
                                    outFile = saveAsMol(model, outFile);
                                } else if (type.equals(JCPSaveFileFilter.inchi))
                                {
                                    outFile = saveAsInChI(model, outFile);
                                } else if (type.equals(JCPSaveFileFilter.cml))
                                {
                                    outFile = saveAsCML2(model, outFile);
                                } else if (type.equals(JCPSaveFileFilter.smiles))
                                {
                                    outFile = saveAsSMILES(model, outFile);
                                } else if (type.equals(JCPSaveFileFilter.cdk))
                                {
                                    outFile = saveAsCDKSourceCode(model, outFile);
                                } else if (type.equals(JCPSaveFileFilter.rxn))
                                {
                                    outFile = saveAsRXN(model, outFile);
                                } else
                                {
                                    String error = GT.get("Cannot save file in this format:") + " " + type;
                                    logger.error(error);
                                    JOptionPane.showMessageDialog(jcpPanel, error);
                                    return;
                                }
                                jcpPanel.setModified(false);
                            } catch (Exception exc)
                            {
                                String error = GT.get("Error while writing file")+": " + exc.getMessage();
                                logger.error(error);
                                logger.debug(exc);
                                JOptionPane.showMessageDialog(jcpPanel, error);
                            }
                        }
                        jcpPanel.setCurrentWorkDirectory(chooser.getCurrentDirectory());
                        jcpPanel.setCurrentSaveFileFilter(chooser.getFileFilter());
                        jcpPanel.setIsAlreadyAFile(outFile);
                        if(outFile!=null){
                            jcpPanel.getChemModel().setID(outFile.getName());
                            if(jcpPanel instanceof JChemPaintPanel)
                                ((JChemPaintPanel)jcpPanel).setTitle(outFile.getName());
                        }
                    }
                }
            }
        }
    }

    protected File saveAsRXN(IChemModel model, File outFile) throws IOException, CDKException {
        if(model.getMoleculeSet()!=null && model.getMoleculeSet().getAtomContainerCount()>0){
			String error = GT.get("Problems handling data");
			String message = GT.get("{0} files cannot contain extra molecules. You painted molecules outside the reaction(s), which will not be in the file. Continue?", "RXN");

			int answer = JOptionPane.showConfirmDialog(jcpPanel, message, error, JOptionPane.YES_NO_OPTION);
			if(answer == JOptionPane.NO_OPTION)
				return null;
    	}
    	if(model.getReactionSet()==null || model.getReactionSet().getReactionCount()==0){
			String error = GT.get("Problems handling data");
			String message = GT.get("RXN can only save reactions. You have no reactions painted!");
			JOptionPane.showMessageDialog(jcpPanel, message, error, JOptionPane.WARNING_MESSAGE);
			return null;
    	}
        logger.info("Saving the contents in an rxn file...");
        String fileName = outFile.toString();
        if (!fileName.endsWith(".rxn")) {
            fileName += ".rxn";
            outFile = new File(fileName);
        }
        outFile=new File(fileName);
        cow = new MDLRXNWriter(new FileWriter(outFile));
        cow.write(model.getReactionSet());
        cow.close();
        if(jcpPanel instanceof JChemPaintPanel)
            ((JChemPaintPanel)jcpPanel).setTitle(jcpPanel.getChemModel().getID());
        return outFile;
    }

    private boolean askIOSettings() {
        return JCPPropertyHandler.getInstance(true).getJCPProperties()
        .getProperty("General.askForIOSettings").equals("true");
    }

    protected File saveAsMol(IChemModel model, File outFile) throws Exception
    {
        logger.info("Saving the contents in a MDL molfile file...");
        
        if(model.getMoleculeSet()==null || model.getMoleculeSet().getAtomContainerCount()==0){
            String error = GT.get("Problems handling data");
            String message = GT.get("MDL mol files can only save molecules. You have no molecules painted!");
            JOptionPane.showMessageDialog(jcpPanel, message, error, JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if(model.getReactionSet()!=null && model.getReactionSet().getReactionCount()>0){
            String error = GT.get("Problems handling data");
            String message = GT.get("{0} files cannot contain reactions. Your have reaction(s) painted. The reactants/products of these will be included as separate molecules. Continue?", "MDL mol");
            int answer = JOptionPane.showConfirmDialog(jcpPanel, message, error, JOptionPane.YES_NO_OPTION);
            if(answer == JOptionPane.NO_OPTION)
                return null;
        }
        boolean saveAsRgrpQuery=false;
   	    IRGroupQuery rGroupQuery = null;
		  if(jcpPanel.get2DHub().getRGroupHandler()!=null)
			  rGroupQuery= jcpPanel.get2DHub().getRGroupHandler().getrGroupQuery();

        if(rGroupQuery!=null){
            String error = GT.get("Please choose a file type!");
            String message = GT.get("Would you like to save the drawing as an R-group Query File? (RGFile = extended MOLfile)");
            int answer = JOptionPane.showConfirmDialog(jcpPanel, message, error, JOptionPane.YES_NO_OPTION);
            if(answer == JOptionPane.YES_OPTION)
            	saveAsRgrpQuery=true;
        }
        
        String fileName = outFile.toString();
        if (!fileName.endsWith(".mol")) {
            fileName += ".mol";
            outFile = new File(fileName);
        }
        outFile=new File(fileName);
        
        if(saveAsRgrpQuery) {
        	cow = new RGroupQueryWriter(new FileWriter(outFile));
        	
        	boolean problem=false;
        	String message="";
        	jcpPanel.get2DHub().getRGroupHandler().cleanUpRGroup(jcpPanel.get2DHub().getChemModel().getMoleculeSet());

        	if(!rGroupQuery.areRootAtomsDefined()) {
                message = GT.get("The R-group Query is not valid: there are substitutes that have no corresponding atom in the root structure.");
                problem=true;
        	}
        	if(!rGroupQuery.areSubstituentsDefined()) {
                message = GT.get("The R-group Query is not valid: the root structure has R# definitions for which no substitutes are defined.");
				problem=true;
        	}
        	if (problem) {
                String error = GT.get("Could not save file");
				JOptionPane.showMessageDialog(jcpPanel, message, GT.get(error), JOptionPane.INFORMATION_MESSAGE);
                return null;
        	}

        	cow.write(rGroupQuery);
        }
        else {
        	cow = new MDLV2000Writer(new FileWriter(outFile));
            cow.write(model);
        }
        	
        cow.close();

        if(jcpPanel instanceof JChemPaintPanel)
            ((JChemPaintPanel)jcpPanel).setTitle(jcpPanel.getChemModel().getID());
        return outFile;
    }

    protected File saveAsCML2(IChemObject object, File outFile) throws Exception
    {
//        if(Float.parseFloat(System.getProperty("java.specification.version"))<1.5){
//            JOptionPane.showMessageDialog(null,"For saving as CML you need Java 1.5 or higher!");
//            return outFile;
//        }
//        logger.info("Saving the contents in a CML 2.0 file...");
//        String fileName = outFile.toString();
//        if (!fileName.endsWith(".cml")) {
//            fileName += ".cml";
//            outFile = new File(fileName);
//        }
//        FileWriter sw = new FileWriter(outFile);
//        cow = new CMLWriter(sw);
//        if (cow != null && askIOSettings())
//        {
//            cow.addChemObjectIOListener(new SwingGUIListener(jcpPanel, IOSetting.Importance.HIGH));
//        }
//        cow.write(object);
//        cow.close();
//        sw.close();
//        if(jcpPanel instanceof JChemPaintPanel)
//            ((JChemPaintPanel)jcpPanel).setTitle(jcpPanel.getChemModel().getID());
//        return outFile;
        throw new IllegalStateException();
    }

    
    
    protected File saveAsInChI(IChemObject object, File outFile) throws Exception
    {
        logger.info("Saving the contents in an InChI textfile...");
        String fileName = outFile.toString();
        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
            outFile = new File(fileName);
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

        String eol=System.getProperty("line.separator");
        if (object instanceof IChemModel) {
            IAtomContainerSet mSet = ((IChemModel) object).getMoleculeSet();
            for (IAtomContainer atc : mSet.atomContainers()) {
                InChI inchi = InChITool.generateInchi(atc);
                out.write(inchi.getInChI()+eol);
                out.write(inchi.getAuxInfo()+eol);
                out.write(inchi.getKey()+eol);
            }
        }
        else if (object instanceof IAtomContainer) {
            IAtomContainer atc = (IAtomContainer) object;
            InChI inchi = InChITool.generateInchi(atc);
            out.write(inchi.getInChI()+eol);
            out.write(inchi.getAuxInfo()+eol);
            out.write(inchi.getKey()+eol);
        }
        out.close();
        return outFile;
    }
    
    
    protected File saveAsSMILES(IChemModel model, File outFile) throws Exception
    {
        
        logger.info("Saving the contents in SMILES format...");
        if(model.getReactionSet()!=null && model.getReactionSet().getReactionCount()>0){
            String error = GT.get("Problems handling data");
            String message = GT.get("{0} files cannot contain reactions. Your have reaction(s) painted. The reactants/products of these will be included as separate molecules. Continue?", "SMILES");

            int answer = JOptionPane.showConfirmDialog(jcpPanel, message, error, JOptionPane.YES_NO_OPTION);
            if(answer == JOptionPane.NO_OPTION)
                return null;
        }
        String fileName = outFile.toString();
        if (!fileName.endsWith(".smi") && !fileName.endsWith(".smiles")) {
            fileName += ".smi";
            outFile = new File(fileName);
        }
        cow = new SMILESWriter(new FileWriter(outFile));
        if (cow != null && askIOSettings())
        {
            cow.addChemObjectIOListener(new SwingGUIListener(jcpPanel, IOSetting.Importance.HIGH));
        }
        Iterator<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
        IAtomContainerSet som = model.getBuilder().newInstance(IAtomContainerSet.class);
        while (containers.hasNext()) {
            //Clone() is here because the SMILESWriter sets valencies and we don't
            //want these changes visible
            som.addAtomContainer((IAtomContainer) containers.next().clone());
        }		
        cow.write(som);
        cow.close();
        if(jcpPanel instanceof JChemPaintPanel)
            ((JChemPaintPanel)jcpPanel).setTitle(jcpPanel.getChemModel().getID());
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
            cow.addChemObjectIOListener(new SwingGUIListener(jcpPanel, IOSetting.Importance.HIGH));
        }
        Iterator<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
        while (containers.hasNext()) {
            IAtomContainer ac = (IAtomContainer)containers.next();
            if (ac != null) {
                cow.write(ac);
            } else {
                System.err.println("AC == null!");
            }
        }
        cow.close();
        if(jcpPanel instanceof JChemPaintPanel)
            ((JChemPaintPanel)jcpPanel).setTitle(jcpPanel.getChemModel().getID());
        return outFile;
    }

    /**
     * Tells if the save as has been cancelled.
     * 
     * @return True if cancel has been used on the save as dialog, false else.
     */
    public boolean getWasCancelled() {
        return wasCancelled;
    }
}
