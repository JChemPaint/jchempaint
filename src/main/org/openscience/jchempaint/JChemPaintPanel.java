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
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.jchempaint.action.SaveAction;

public class JChemPaintPanel extends AbstractJChemPaintPanel implements IChemObjectListener {

	private JComponent lastActionButton;
	private File currentWorkDirectory;
	private File lastOpenedFile;
	private FileFilter currentOpenFileFilter;
	private File isAlreadyAFile;
	private boolean isModified=false;
	private FileFilter currentSaveFileFilter;
	private JCPStatusBar statusBar;
	public static List<JChemPaintPanel> instances = new ArrayList<JChemPaintPanel>();
	private boolean showInsertTextField = true;
	private InsertTextPanel insertTextPanel = null;
	private JPanel topContainer = null;
	private boolean showToolBar=true;
	private boolean showStatusBar=true;
	private boolean showMenuBar=true;
	private JMenuBar menu;
	private String guistring;
	private JToolBar toolbar;
	private int lines=1;
	
	/**
	 * Builds a JCPPanel with a certain model and a certain gui
	 * 
	 * @param chemModel The model
	 * @param gui The gui string
	 */
	public JChemPaintPanel(IChemModel chemModel, String gui){
		this.guistring=gui;
		this.setLayout(new BorderLayout());
		topContainer = new JPanel(new BorderLayout());
		topContainer.setLayout(new BorderLayout());
		this.add(topContainer,BorderLayout.NORTH);
		renderPanel = new RenderPanel(chemModel, this.getWidth(), this.getHeight());
		this.add(new JScrollPane(renderPanel),BorderLayout.CENTER);
		customizeView();
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
	 *  Tells if a menu is shown
	 *
	 *@return    The showMenu value
	 */
	public boolean getShowMenuBar() {
		return showMenuBar;
	}


	/**
	 *  Sets if a menu is shown
	 *
	 *@param  showMenuBar  The new showMenuBar value
	 */
	public void setShowMenuBar(boolean showMenuBar) {
		this.showMenuBar = showMenuBar;
		customizeView();
	}


	public void customizeView() {
		if (showMenuBar) {
			if (menu == null) {
				menu = new JChemPaintMenuBar(this, this.guistring);
			}
			topContainer.add(menu, BorderLayout.NORTH);
		} else {
			topContainer.remove(menu);
		}
		if (showStatusBar) {
			if (statusBar == null) {
				statusBar = new JCPStatusBar();
			}
			add(statusBar, BorderLayout.SOUTH);
		} else {
			remove(statusBar);
		}
        if (showToolBar) {
            if (toolbar == null) {
                toolbar = JCPToolBar.getToolbar(this, lines);
            }
            topContainer.add(toolbar, BorderLayout.CENTER);
        } else {
            topContainer.remove(toolbar);
        }
        if (showInsertTextField) {
            if (insertTextPanel == null) 
            	insertTextPanel = new InsertTextPanel(this,null);
            topContainer.add(insertTextPanel, BorderLayout.SOUTH);
        } else {
            topContainer.remove(insertTextPanel);
        }
        revalidate();
	}

	/**
	 *  Tells if a status bar is shown
	 *
	 *@return    The showStatusBar value
	 */
	public boolean getShowStatusBar() {
		return showStatusBar;
	}

	/**
	 *  Sets the value of showToolbar.
	 *
	 *@param  showToolBar  The value to assign showToolbar.
	 */
	public void setShowToolBar(boolean showToolBar)
	{
		setShowToolBar(showToolBar, 1);
	}

    /**
	 *  Sets the value of showToolbar.
	 *
	 *@param  showToolBar  The value to assign showToolbar.
	 */
    public void setShowToolBar(boolean showToolBar, int lines) {
        this.showToolBar = showToolBar;
        this.lines=lines;
        customizeView();
    }
    
	/**
	 *  Returns the value of showToolbar.
	 *
	 *@return    The showToolbar value
	 */
	public boolean getShowToolBar()
	{
		return showToolBar;
	}
	
	/**
	 *  Sets if statusbar should be shown
	 *
	 *@param  showStatusBar  The value to assign showStatusBar.
	 */
	public void setShowStatusBar(boolean showStatusBar) {
		this.showStatusBar = showStatusBar;
		customizeView();
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

    /**
     * Set to indicate whether the insert text field should be used.
     *
     * @param showInsertTextField true is the text entry widget is to be shown
     */
    public void setShowInsertTextField(boolean showInsertTextField) {
        this.showInsertTextField = showInsertTextField;
        customizeView();
    }
    
	/**
	 * Tells if the enter text field is currently shown or not.
	 * 
	 * @return text field shown or not
	 */
	public boolean getShowInsertTextField() {
		return showInsertTextField;
	}
	
	public Image takeSnapshot() {
	    return this.renderPanel.takeSnapshot();
	}
	
	public Image takeSnapshot(Rectangle bounds) {
	    return this.renderPanel.takeSnapshot(bounds); 
	}


	/**
	 * Shows a warning if the JCPPanel has unsaved content and does save, if the user wants to do it.
	 * 
	 * @return OptionPane.YES_OPTION/OptionPane.NO_OPTION/OptionPane.CANCEL_OPTION
	 */
	public int showWarning() {
		if (isModified){ //TODO && !getIsOpenedByViewer() && !guiString.equals("applet")) {
			int answer = JOptionPane.showConfirmDialog(this, renderPanel.getChemModel().getID() + " " + JCPLocalizationHandler.getInstance().getString("warning"), JCPLocalizationHandler.getInstance().getString("warningheader"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				new SaveAction(this, false).actionPerformed(new ActionEvent(this, 12, ""));
			}
			return answer;
		/* TODO } else if(guiString.equals("applet")){
			//In case of the applet we do not ask for save but put the clear into the undo stack
			ClearAllEdit coa = null;
			try {
				coa = new ClearAllEdit(this.getChemModel(),(IMoleculeSet)this.getChemModel().getMoleculeSet().clone(),this.getChemModel().getReactionSet());
				this.jchemPaintModel.getControllerModel().getUndoSupport().postEdit(coa);
			} catch (Exception e) {
				logger.error("Clone of IMoleculeSet failed: ", e.getMessage());
            	logger.debug(e);
			}
			return JOptionPane.YES_OPTION;*/
		} else {
			return JOptionPane.YES_OPTION;
		}
	}

	/**
	 *  Class for closing jcp
	 *
	 *@author     shk3
	 *@cdk.created    November 23, 2008
	 */
	public final static class AppCloser extends WindowAdapter {

		/**
		 *  closing Event. Shows a warning if this window has unsaved data and
		 *  terminates jvm, if last window.
		 *
		 *	@param  e  Description of the Parameter
		 */
		public void windowClosing(WindowEvent e) {
//			JFrame rootFrame = (JFrame) e.getSource();
			/*TODO if (rootFrame.getContentPane().getComponent(0) instanceof JChemPaintEditorPanel) {
				JChemPaintEditorPanel panel = (JChemPaintEditorPanel) rootFrame.getContentPane().getComponent(0);
				panel.fireChange(JChemPaintEditorPanel.JCP_CLOSING);
			}*/
			int clear = ((JChemPaintPanel) ((JFrame) e.getSource()).getContentPane().getComponents()[0]).showWarning();
			if (JOptionPane.CANCEL_OPTION != clear) {
				for (int i = 0; i < instances.size(); i++) {
					if (instances.get(i).getParent().getParent().getParent().getParent() == (JFrame)e.getSource()) {
						instances.remove(i);
						break;
					}
				}
				((JFrame) e.getSource()).setVisible(false);
				((JFrame) e.getSource()).dispose();
				if (instances.size() == 0){//TODO && !((JChemPaintPanel)rootFrame.getContentPane().getComponent(0)).isEmbedded()) {
					System.exit(0);
				}
			}
		}
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
		Iterator<JChemPaintPanel> it = ((List<JChemPaintPanel>)((ArrayList<JChemPaintPanel>)instances).clone()).iterator();
		while (it.hasNext()) {
			JFrame frame = (JFrame) it.next().getParent().getParent().getParent().getParent();
			WindowListener[] wls = (WindowListener[]) (frame.getListeners(WindowListener.class));
			wls[0].windowClosing(new WindowEvent(frame, 12));
			frame.setVisible(false);
			frame.dispose();
		}
	}
}
