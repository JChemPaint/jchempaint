package org.openscience.jchempaint;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.controller.Controller2DHub;
import org.openscience.cdk.interfaces.IChemModel;

public class JChemPaintPanel extends JPanel {

	RenderPanel p;
	
	private JComponent lastActionButton;

	private File currentWorkDirectory;

	private File lastOpenedFile;

	private FileFilter currentOpenFileFilter;

	private File isAlreadyAFile;
	public JComponent getActionButton() {
		return lastActionButton;
	}
	public void setActionButton(JComponent actionButton) {
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

	public JChemPaintPanel(IChemModel ac){
		this.setLayout(new BorderLayout());
		JMenuBar menu = new JChemPaintMenuBar(this, "stable");
		JPanel topContainer = new JPanel(new BorderLayout());
		topContainer.setLayout(new BorderLayout());
		this.add(topContainer,BorderLayout.NORTH);
		topContainer.add(menu,BorderLayout.NORTH);
		p = new RenderPanel(ac);
		this.add(p,BorderLayout.CENTER);
		JToolBar toolbar = JCPToolBar.getToolbar(this, 1);
		topContainer.add(toolbar,BorderLayout.CENTER);
	}

	public Controller2DHub get2DHub() {
		return p.getHub();
	}
}
