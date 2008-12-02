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
package org.openscience.jchempaint;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.jchempaint.action.SaveAction;

public class JChemPaintViewerPanel extends AbstractJChemPaintPanel implements IChemObjectListener {

	private JComponent lastActionButton;
	private File currentWorkDirectory;
	private File lastOpenedFile;
	private FileFilter currentOpenFileFilter;
	private File isAlreadyAFile;
	private boolean isModified=false;
	private FileFilter currentSaveFileFilter;
	public static List<JChemPaintViewerPanel> instances = new ArrayList<JChemPaintViewerPanel>();
	private boolean showInsertTextField = true;
	private String guistring;
	
	/**
	 * Builds a JCPPanel with a certain model and a certain gui
	 * 
	 * @param chemModel The model
	 * @param gui The gui string
	 */
	public JChemPaintViewerPanel(IChemModel chemModel, String gui){
		this.guistring=gui;
		this.setLayout(new BorderLayout());
		renderPanel = new RenderPanel(chemModel);
		this.add(new javax.swing.JScrollPane(renderPanel),BorderLayout.CENTER);
		instances.add(this);
		chemModel.addListener(this);
	}
	
	/**
	 * Called to force a re-centring of the displayed structure. 
	 * 
	 * @param isNewChemModel
	 */
	public void setIsNewChemModel(boolean isNewChemModel) {
	    this.renderPanel.setIsNewChemModel(isNewChemModel);
	}
	

	/**
	 * Helps in keeping the current action button highlighted
	 * 
	 * @return The last action button used
	 */
	public JComponent getLastActionButton() {
		return lastActionButton;
	}
	
	/**
	 * Allows setting of the is modified stage (e. g. after save)
	 * 
	 * @param isModified is modified
	 */
	public void setModified(boolean isModified) {
		this.isModified = isModified;
		if(this.getParent().getParent().getParent().getParent() instanceof JFrame){
			if(isModified)
				((JFrame)this.getParent().getParent().getParent().getParent()).setTitle(renderPanel.getChemModel().getID()+"*");
			else
				((JFrame)this.getParent().getParent().getParent().getParent()).setTitle(renderPanel.getChemModel().getID());
		}
	}


	/**
	 * Helps in keeping the current action button highlighted - needs to be set
	 * if a new action button is choosen
	 * 
	 * @param actionButton The new action button
	 */
	public void setLastActionButton(JComponent actionButton) {
		lastActionButton = actionButton;
	}
	
	/**
	 *  Gets the currentWorkDirectory attribute of the JChemPaintPanel object
	 *
	 *@return    The currentWorkDirectory value
	 */
	public File getCurrentWorkDirectory() {
		return currentWorkDirectory;
	}


	/**
	 *  Sets the currentWorkDirectory attribute of the JChemPaintPanel object
	 *
	 *@param  cwd  The new currentWorkDirectory value
	 */
	public void setCurrentWorkDirectory(File cwd) {
		this.currentWorkDirectory = cwd;
	}

	/**
	 *  Gets the lastOpenedFile attribute of the JChemPaintPanel object
	 *
	 *@return    The lastOpenedFile value
	 */
	public File getLastOpenedFile() {
		return lastOpenedFile;
	}


	/**
	 *  Sets the lastOpenedFile attribute of the JChemPaintPanel object
	 *
	 *@param  lof  The new lastOpenedFile value
	 */
	public void setLastOpenedFile(File lof) {
		this.lastOpenedFile = lof;
	}

	/**
	 *  Gets the currentOpenFileFilter attribute of the JChemPaintPanel object
	 *
	 *@return    The currentOpenFileFilter value
	 */
	public FileFilter getCurrentOpenFileFilter() {
		return currentOpenFileFilter;
	}


	/**
	 *  Sets the currentOpenFileFilter attribute of the JChemPaintPanel object
	 *
	 *@param  ff  The new currentOpenFileFilter value
	 */
	public void setCurrentOpenFileFilter(FileFilter ff) {
		this.currentOpenFileFilter = ff;
	}
	
	/**
	 *  Gets the currentSaveFileFilter attribute of the JChemPaintPanel object
	 *
	 *@return    The currentSaveFileFilter value
	 */
	public FileFilter getCurrentSaveFileFilter() {
		return currentSaveFileFilter;
	}


	/**
	 *  Sets the currentSaveFileFilter attribute of the JChemPaintPanel object
	 *
	 *@param  ff  The new currentSaveFileFilter value
	 */
	public void setCurrentSaveFileFilter(FileFilter ff) {
		this.currentSaveFileFilter = ff;
	}
	
	
	/**
	 *  Sets the file currently used for saving this Panel.
	 *
	 *@param  value  The new isAlreadyAFile value
	 */
	public void setIsAlreadyAFile(File value) {
		isAlreadyAFile = value;
	}


	/**
	 *  Returns the file currently used for saving this Panel, null if not yet
	 *  saved
	 *
	 *@return    The currently used file
	 */
	public File isAlreadyAFile() {
		return isAlreadyAFile;
	}

	public Image takeSnapshot() {
	    return this.renderPanel.takeSnapshot();
	}
	
	public Image takeSnapshot(Rectangle bounds) {
	    return this.renderPanel.takeSnapshot(bounds); 
	}

	/**
	 * Return the ControllerHub of this JCPPanel
	 * 
	 * @return The ControllerHub
	 */
	public ControllerHub get2DHub() {
		return renderPanel.getHub();
	}
	
	/* (non-Javadoc)
	 * @see org.openscience.cdk.interfaces.IChemObjectListener#stateChanged(org.openscience.cdk.interfaces.IChemObjectChangeEvent)
	 */
	public void stateChanged(IChemObjectChangeEvent event) {
		setModified(true);
        /* TODO gives concurrent access problems if (this.getChemModel() != null) {
            for (int i = 0; i < 3; i++) {
              String status = p.getStatus(i);
              statusBar.setStatus(i + 1, status);
            }
        } else {
            if (statusBar != null) {
              statusBar.setStatus(1, "no model");
            }
        }*/
    }
	
	
	/**
	 *  Closes all currently opened JCP instances.
	 */
	public static void closeAllInstances() {
		Iterator<JChemPaintViewerPanel> it = ((List<JChemPaintViewerPanel>)((ArrayList<JChemPaintViewerPanel>)instances).clone()).iterator();
		while (it.hasNext()) {
			JFrame frame = (JFrame) it.next().getParent().getParent().getParent().getParent();
			WindowListener[] wls = (WindowListener[]) (frame.getListeners(WindowListener.class));
			wls[0].windowClosing(new WindowEvent(frame, 12));
			frame.setVisible(false);
			frame.dispose();
		}
	}
}
