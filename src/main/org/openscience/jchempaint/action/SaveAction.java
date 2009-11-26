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

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JOptionPane;

import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.io.JCPFileFilter;
import org.openscience.jchempaint.io.JCPSaveFileFilter;

/**
 * Opens a save dialog
 *
 */
public class SaveAction extends SaveAsAction
{

    private static final long serialVersionUID = -6748046051686998776L;


    public SaveAction(){
          super();
    }
        
	/**
	 *  Constructor for the SaveAsAction object
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 */
    public SaveAction(JChemPaintPanel jcpPanel, boolean isPopupAction)
	{
		super(jcpPanel, isPopupAction);
	}


	/**
	 *  Saves a file or calls save as, when current content was not yet saved.
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{
	    if(jcpPanel.isAlreadyAFile()==null){
	    	SaveAsAction saveasaction = new SaveAsAction(jcpPanel,false);
	    	saveasaction.actionPerformed(event);
	    	this.wasCancelled = saveasaction.wasCancelled;
	    }else{
	    	try{
	    		org.openscience.cdk.interfaces.IChemModel model=jcpPanel.getChemModel();
	    		File outFile=jcpPanel.isAlreadyAFile();
				type = JCPFileFilter.getExtension(outFile);
				if (type.equals(JCPSaveFileFilter.mol))
		        {
		          saveAsMol(model, outFile);
		        } else if (type.equals(JCPSaveFileFilter.cml))
		        {
		          saveAsCML2(model, outFile);
		        } else if (type.equals(JCPSaveFileFilter.smiles))
		        {
		          saveAsSMILES(model, outFile);
		        } else if (type.equals(JCPSaveFileFilter.cdk))
		        {
		          saveAsCDKSourceCode(model, outFile);
		        } else
		        {
		          String error = "Cannot save file in this format: " + type;
		          logger.error(error);
		          JOptionPane.showMessageDialog(jcpPanel, error);
		          return;
		        }
		        jcpPanel.setModified(false);
		      }catch(Exception ex){
				String error = "Error while writing file: " + ex.getMessage();
				logger.error(error);
				logger.debug(ex);
				JOptionPane.showMessageDialog(jcpPanel, error);
		      }
	    }
	}
}

