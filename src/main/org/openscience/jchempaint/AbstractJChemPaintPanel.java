/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 2008 Stefan Kuhn
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
package org.openscience.jchempaint;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.jchempaint.action.CreateSmilesAction;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;

/**
 * An abstract superclass for the viewer and editor panel.
 *
 */
public abstract class AbstractJChemPaintPanel extends JPanel{

	protected RenderPanel renderPanel;
    protected LoggingTool logger = new LoggingTool(this);

	/**
	 * 
	 * 
	 * @return
	 */
	public RenderPanel getRenderPanel() {
		return renderPanel;
	}

	/**
	 * Return the ControllerHub of this JCPPanel
	 * 
	 * @return The ControllerHub
	 */
	public ControllerHub get2DHub() {
		return renderPanel.getHub();
	}
	
	/**
	 * Return the chemmodel of this JCPPanel
	 * 
	 * @return
	 */
	public IChemModel getChemModel(){
		return renderPanel.getChemModel();
	}

	/**
	 * Return the chemmodel of this JCPPanel
	 * 
	 * @return
	 */
	public void setChemModel(IChemModel model){
		renderPanel.setChemModel(model);
		//we need to do this to avoid npes later
		renderPanel.getRenderer().getRenderer2DModel().setSelection(new LogicalSelection(LogicalSelection.Type.NONE));
	}
	
	public String getSmiles() throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException{
		return CreateSmilesAction.getSmiles(getChemModel());
	}
	
    
    /**
     * This method handles an error when we do not know what to do. It clearly 
     * announces to the user that an error occured. This is preferable compared 
     * to failing silently.
     * 
     * @param ex The throwable which occured.
     */
    public void announceError(Throwable ex){
    	JOptionPane.showMessageDialog(this, 
    			GT._("The error was:")+" "+ex.getMessage()+". "+GT._("\nYou can file a bug report at ")+
    			"https://sourceforge.net/tracker/?func=browse&group_id=20024&atid=120024. "+
    			GT._("\nWe apologize for any inconvenience!"), GT._("Error occured"),
    			JOptionPane.ERROR_MESSAGE);
    	ex.printStackTrace();
    	logger.error(ex.getMessage());
    }
}
