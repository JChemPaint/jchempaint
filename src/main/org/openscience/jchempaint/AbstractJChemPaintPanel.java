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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.image.*;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.action.CreateSmilesAction;
import org.openscience.jchempaint.action.JCPAction;
import org.openscience.jchempaint.action.SaveAction;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;

/**
 * An abstract superclass for the viewer and editor panel.
 *
 */
public abstract class AbstractJChemPaintPanel extends JPanel{

    private static final long serialVersionUID = -6591788750314560180L;
    // buttons/menus are remembered in here using the string from config files as key
    Map<String, JButton> buttons=new HashMap<String, JButton>();
    Map<String, JMenuItem> menus=new HashMap<String, JMenuItem>();
    Map<String, JChemPaintPopupMenu> popupmenuitems=new HashMap<String, JChemPaintPopupMenu>();
    protected InsertTextPanel insertTextPanel = null;
    protected JCPStatusBar statusBar;
    protected boolean showStatusBar = true;
    protected String guistring;
	protected RenderPanel renderPanel;
    private FileFilter currentSaveFileFilter;
    private FileFilter currentOpenFileFilter;
    private File currentWorkDirectory;
    private boolean showToolBar = true;
    private boolean showMenuBar = true;
    private boolean showInsertTextField = true;
    private JMenuBar menu;
    private JToolBar uppertoolbar;
    private JToolBar lefttoolbar;
    private JToolBar lowertoolbar;
    private JToolBar righttoolbar;
    protected JPanel topContainer = null;
    protected JPanel centerContainer = null;
    private JComponent lastActionButton;
    protected JMenuItem undoMenu;
    protected JMenuItem redoMenu;
    protected JMenu atomMenu;
    protected JMenu bondMenu;
    protected boolean debug=false;
    protected boolean modified = false;
    private File isAlreadyAFile;
    private File lastOpenedFile;
    protected JComponent lastSecondaryButton;
	private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(AbstractJChemPaintPanel.class);
    protected static String appTitle = "";
    protected JCPMenuTextMaker menuTextMaker = null;

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

    /**
     * Update the menu bars and toolbars to current language.
     */
    public void updateMenusWithLanguage() {
        menuTextMaker.init(guistring);
        Iterator<String> it = buttons.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            JButton button = buttons.get(key);
            button.setToolTipText(menuTextMaker.getText(key + JCPAction.TIPSUFFIX));
        }
        it = menus.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            JMenuItem button = menus.get(key);
            button.setText(menuTextMaker.getText(key));
        }
        it = popupmenuitems.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            JChemPaintPopupMenu button = popupmenuitems.get(key);
            ((JMenuItem)button.getComponent(0)).setText(menuTextMaker.getText(key.substring(0,key.length()-5) + "MenuTitle"));
        }
        if(insertTextPanel!=null){
            insertTextPanel.updateLanguage();
        }
        if(showStatusBar)
            this.updateStatusBar();
    }    

    /**
     * Updates the status bar to the current values
     */
    public void updateStatusBar() {
        if (showStatusBar) {
            if (this.getChemModel() != null) {
                for (int i = 0; i < 4; i++) {
                    String status = renderPanel.getStatus(i);
                    statusBar.setStatus(i + 1, status);
                }
            } else {
                if (statusBar != null) {
                    statusBar.setStatus(1, "no model");
                }
            }
        }
    }    

    public String getGuistring() {
        return guistring;
    }

    /**
     * Called to force a re-centring of the displayed structure.
     *
     * @param isNewChemModel
     */
    public void setIsNewChemModel(boolean isNewChemModel) {
        this.renderPanel.setIsNewChemModel(isNewChemModel);
    }
    
    public Container getTopLevelContainer() {
        return this.getParent().getParent().getParent().getParent();
    }

    public String getSVGString() {
        return this.renderPanel.toSVG();
    }

    public Image takeSnapshot() {
        return this.renderPanel.takeSnapshot();
    }

    public Image takeTransparentSnapshot() {
        Image snapshot = takeSnapshot();
        ImageFilter filter = new RGBImageFilter() {
            // Alpha bits are set to opaque
            public int markerRGB =
                renderPanel.getRenderer().getRenderer2DModel().getBackColor().getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ( ( rgb | 0xFF000000 ) == markerRGB ) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };
        
        ImageProducer ip = new FilteredImageSource(snapshot.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    /**
     * Gets the currentOpenFileFilter attribute of the JChemPaintPanel object
     *
     *@return The currentOpenFileFilter value
     */
    public FileFilter getCurrentOpenFileFilter() {
        return currentOpenFileFilter;
    }

    /**
     * Sets the currentOpenFileFilter attribute of the JChemPaintPanel object
     *
     *@param ff
     *            The new currentOpenFileFilter value
     */
    public void setCurrentOpenFileFilter(FileFilter ff) {
        this.currentOpenFileFilter = ff;
    }

    /**
     * Gets the currentSaveFileFilter attribute of the JChemPaintPanel object
     *
     *@return The currentSaveFileFilter value
     */
    public FileFilter getCurrentSaveFileFilter() {
        return currentSaveFileFilter;
    }

    /**
     * Sets the currentSaveFileFilter attribute of the JChemPaintPanel object
     *
     *@param ff
     *            The new currentSaveFileFilter value
     */
    public void setCurrentSaveFileFilter(FileFilter ff) {
        this.currentSaveFileFilter = ff;
    }

    /**
     * Gets the currentWorkDirectory attribute of the JChemPaintPanel object
     *
     *@return The currentWorkDirectory value
     */
    public File getCurrentWorkDirectory() {
        return currentWorkDirectory;
    }

    /**
     * Sets the currentWorkDirectory attribute of the JChemPaintPanel object
     *
     *@param cwd
     *            The new currentWorkDirectory value
     */
    public void setCurrentWorkDirectory(File cwd) {
        this.currentWorkDirectory = cwd;
    }

    /**
     * Set to indicate whether the insert text field should be used.
     *
     * @param showInsertTextField
     *            true is the text entry widget is to be shown
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

    /**
     * Tells if a menu is shown
     *
     *@return The showMenu value
     */
    public boolean getShowMenuBar() {
        return showMenuBar;
    }

    /**
     * Sets if a menu is shown
     *
     *@param showMenuBar
     *            The new showMenuBar value
     */
    public void setShowMenuBar(boolean showMenuBar) {
        this.showMenuBar = showMenuBar;
        customizeView();
    }
    
    /**
     * Returns the value of showToolbar.
     *
     *@return The showToolbar value
     */
    public boolean getShowToolBar() {
        return showToolBar;
    }

    /**
     * Sets if statusbar should be shown
     *
     *@param showStatusBar
     *            The value to assign showStatusBar.
     */
    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
        customizeView();
    }

    /**
     * Tells if a status bar is shown
     *
     *@return The showStatusBar value
     */
    public boolean getShowStatusBar() {
        return showStatusBar;
    }
    
    /**
     * Sets the value of showToolbar.
     *
     *@param showToolBar
     *            The value to assign showToolbar.
     */
    public void setShowToolBar(boolean showToolBar) {
        setShowToolBar(showToolBar);
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
            if (uppertoolbar == null) {
                uppertoolbar = JCPToolBar.getToolbar(this, "uppertoolbar", SwingConstants.HORIZONTAL);
            }
            centerContainer.add(uppertoolbar, BorderLayout.NORTH);
            if (lefttoolbar == null) {
                lefttoolbar = JCPToolBar.getToolbar(this, "lefttoolbar", SwingConstants.VERTICAL);
            }
            centerContainer.add(lefttoolbar, BorderLayout.WEST);
            if (righttoolbar == null) {
                righttoolbar = JCPToolBar.getToolbar(this, "righttoolbar", SwingConstants.VERTICAL);
            }
            centerContainer.add(righttoolbar, BorderLayout.EAST);
            if (lowertoolbar == null) {
                lowertoolbar = JCPToolBar.getToolbar(this, "lowertoolbar", SwingConstants.HORIZONTAL);
            }
            centerContainer.add(lowertoolbar, BorderLayout.SOUTH);
        } else {
            centerContainer.remove(uppertoolbar);
            centerContainer.remove(lowertoolbar);
            centerContainer.remove(lefttoolbar);
            centerContainer.remove(righttoolbar);
        }
        if (showInsertTextField) {
            if (insertTextPanel == null)
                insertTextPanel = new InsertTextPanel(this, null);
            topContainer.add(insertTextPanel, BorderLayout.SOUTH);
        } else {
            topContainer.remove(insertTextPanel);
        }
        revalidate();
    }

    /**
     * Helps in keeping the current action button highlighted - needs to be set
     * if a new action button is choosen
     *
     * @param actionButton
     *            The new action button
     */
    public void setLastActionButton(JComponent actionButton) {
        lastActionButton = actionButton;
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
     * Enables or disables all JMenuItems in a JMenu recursivly.
     * 
     * @param root  The JMenu to search in.
     * @param b     Enable or disable.
     */
    protected void enOrDisableMenus(JMenu root, boolean b) {
        for(int i=0;i<root.getItemCount();i++){
            if(root.getItem(i) instanceof JMenu){
                this.enOrDisableMenus((JMenu)root.getItem(i), b);
            }else if(root.getItem(i) instanceof JMenuItem){
                ((JMenuItem)root.getItem(i)).setEnabled(b);
            }
        }
    }
    
    /**
     * Shows a warning if the JCPPanel has unsaved content and does save, if the
     * user wants to do it.
     *
     * @return
     *         OptionPane.YES_OPTION/OptionPane.NO_OPTION/OptionPane.CANCEL_OPTION
     */
    public int showWarning() {
        if (modified && !guistring.equals(JChemPaintEditorApplet.GUI_APPLET)) { // TODO
                                                                                  // &&
                                                                                  // !getIsOpenedByViewer())
                                                                                  // {
            int answer = JOptionPane.showConfirmDialog(this, renderPanel
                    .getChemModel().getID()
                    + " " + GT._("has unsaved data. Do you want to save it?"),
                    GT._("Unsaved data"), JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                SaveAction saveaction = new SaveAction(this, false);
                saveaction.actionPerformed(new ActionEvent(
                        this, 12, ""));
                if(saveaction.getWasCancelled())
                    answer = JOptionPane.CANCEL_OPTION;
            }
            return answer;
        } else if (guistring.equals(JChemPaintEditorApplet.GUI_APPLET)) {
            // In case of the applet we do not ask for save but put the clear
            // into the undo stack
            // ClearAllEdit coa = null;
            // TODO undo redo missing coa = new
            // ClearAllEdit(this.getChemModel(),(IMoleculeSet)this.getChemModel().getMoleculeSet().clone(),this.getChemModel().getReactionSet());
            // this.jchemPaintModel.getControllerModel().getUndoSupport().postEdit(coa);
            return JOptionPane.YES_OPTION;
        } else {
            return JOptionPane.YES_OPTION;
        }
    }

    /**
     * Tells if debug output is desired or not.
     *
     * @return debug output or not.
     */
    public boolean isDebug() {
        return debug;
    }
    
    /**
     * Gets the lastOpenedFile attribute of the JChemPaintPanel object
     *
     *@return The lastOpenedFile value
     */
    public File getLastOpenedFile() {
        return lastOpenedFile;
    }

    /**
     * Sets the lastOpenedFile attribute of the JChemPaintPanel object
     *
     *@param lof
     *            The new lastOpenedFile value
     */
    public void setLastOpenedFile(File lof) {
        this.lastOpenedFile = lof;
    }

    /**
     * Sets the file currently used for saving this Panel.
     *
     *@param value
     *            The new isAlreadyAFile value
     */
    public void setIsAlreadyAFile(File value) {
        isAlreadyAFile = value;
    }

    /**
     * Returns the file currently used for saving this Panel, null if not yet
     * saved
     *
     *@return The currently used file
     */
    public File isAlreadyAFile() {
        return isAlreadyAFile;
    }

    public void setLastSecondaryButton(JComponent lastSecondaryButton) {
        this.lastSecondaryButton = lastSecondaryButton;
    }

    public void updateUndoRedoControls() {
        UndoManager undoManager = renderPanel.getUndoManager();
        JButton redoButton=buttons.get("redo");
        JButton undoButton=buttons.get("undo");
        if (undoManager.canRedo()) {
            redoButton.setEnabled(true);
            redoMenu.setEnabled(true);
            redoButton.setToolTipText(GT._("Redo")+": "+undoManager.getRedoPresentationName());
        } else {
            redoButton.setEnabled(false);
            redoMenu.setEnabled(false);
            redoButton.setToolTipText(GT._("No redo possible"));
        }

        if (undoManager.canUndo()) {
            undoButton.setEnabled(true);
            undoMenu.setEnabled(true);
            undoButton.setToolTipText(GT._("Undo")+": "+undoManager.getUndoPresentationName());
        } else {
            undoButton.setEnabled(false);
            undoMenu.setEnabled(false);
            undoButton.setToolTipText(GT._("No undo possible"));
        }
    }

    public static String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String title) {
        appTitle = title;
    }

    /**
     * Allows setting of the is modified stage (e. g. after save)
     *
     * @param isModified
     *            is modified
     */
    public void setModified(boolean isModified) {
        this.modified = isModified;
        Container c = this.getTopLevelContainer();
        if (c instanceof JFrame) {
            String id = renderPanel.getChemModel().getID();
            //String title = ((JFrame) c).getTitle();
            if (isModified)
                ((JFrame) c).setTitle('*' + id + this.getAppTitle());
            else
                ((JFrame) c).setTitle(id + this.getAppTitle());
/*            
            if (title == null) {
                title = renderPanel.getChemModel().getID();
            }
            if (title.charAt(0) == '*') {
                title = title.substring(1);
            }*/
        }
    }

    public boolean isModified() {
        return modified;
    }

    public JCPMenuTextMaker getMenuTextMaker() {
        return menuTextMaker;
    }
}
