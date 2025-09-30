/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 2008 Stefan Kuhn
 *  Some portions Copyright (C) 2009 Konstantin Tokarev
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
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
    List<JMenuItem> menus=new ArrayList<JMenuItem>();
    Map<String, JChemPaintPopupMenu> popupmenuitems=new HashMap<String, JChemPaintPopupMenu>();
    protected InsertTextPanel insertTextPanel = null;
    protected String guistring;
	protected RenderPanel renderPanel;
    private FileFilter currentSaveFileFilter;
    private FileFilter currentOpenFileFilter;
    private File currentWorkDirectory;
    private boolean showToolBar = true;
    private boolean showMenuBar = true;
    private JMenuBar menu;
    private JComponent uppertoolbar;
    private JComponent lefttoolbar;
    private JComponent lowertoolbar;
    private JComponent righttoolbar;
    protected JPanel topContainer = null;
    protected JPanel centerContainer = null;
    private JComponent lastActionButton;
    protected JMenuItem undoMenu;
    protected JMenuItem redoMenu;
    protected JMenu rgroupMenu;
    protected boolean debug=false;
    protected boolean modified = false;
    private File isAlreadyAFile;
    private File lastOpenedFile;
	private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(AbstractJChemPaintPanel.class);
    protected static String appTitle = "";
    protected JCPMenuTextMaker menuTextMaker = null;
    protected Set<String> blockList;


	/**
	 * The blocklist is a set of all elements which should not be shown.
	 * 
	 * @return The blocked actions.
	 */
	public Set<String> getBlockedActions() {
		return blockList;
	}

	/**
	 * Gets the RenderPanel in this panel.
	 * 
	 * @return The RenderPanel.
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
	 * Returns the chemmodel used in this panel.
	 * 
	 * @return The chemmodel usedin this panel.
	 */
	public IChemModel getChemModel(){
		return renderPanel.getChemModel();
	}

    /** Access the menu bar. */
    public JMenuBar getJMenuBar() {
        return menu;
    }
    
	/**
	 * Sets the chemmodel used in this panel.
	 * 
	 * @param model The chemmodel to use.
	 */
	public void setChemModel(IChemModel model){
		renderPanel.setChemModel(model);
		//we need to do this to avoid npes later
		renderPanel.getRenderer().getRenderer2DModel().setSelection(new LogicalSelection(LogicalSelection.Type.NONE));
	}
	
	/**
	 * Gives the smiles for the current chemmodel in this panel.
	 * 
	 * @return The smiles for the current chemmodel in this panel.
	 * @throws CDKException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
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
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintStream ps = new PrintStream(baos);
    	ex.printStackTrace(ps);
    	String trace = baos.toString();

    	JOptionPane.showMessageDialog(this, 
    			GT.get("The error was:")+" "+ex.getMessage()+". "+GT.get("\nYou can file a bug report at ")+
    			"https://github.com/JChemPaint/jchempaint/issues "+
    			GT.get("\nWe apologize for any inconvenience!") + trace, GT.get("Error occured"),
    			JOptionPane.ERROR_MESSAGE);
    	
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
        Iterator<JMenuItem> it2 = menus.iterator();
        while(it2.hasNext()){
            JMenuItem button = it2.next();
            button.setText(JCPMenuTextMaker.getInstance(guistring).getText(button.getName().charAt(button.getName().length()-1)=='2' ? button.getName().substring(0,button.getName().length()-1) : button.getName()));
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
        Container parent = this.getParent();
        while(parent.getParent()!=null)
            parent = parent.getParent();
        return parent;
    }

    public String getSVGString() {
        return this.renderPanel.toSVG();
    }

    public byte[] getPDF() {
        return this.renderPanel.toPDF();
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
        JCPPropertyHandler propertyHandler = JCPPropertyHandler.getInstance(true);
        propertyHandler.getJCPProperties().setProperty("insertstructure.value",
                                                       Boolean.toString(showInsertTextField));
        propertyHandler.saveProperties();
        customizeView();
    }

    /**
     * Tells if the enter text field is currently shown or not.
     *
     * @return text field shown or not
     */         
    public boolean getShowInsertTextField() {
        JCPPropertyHandler propertyHandler = JCPPropertyHandler.getInstance(true);
        Properties properties = propertyHandler.getJCPProperties();
        return properties.containsKey("insertstructure.value")
                && Boolean.parseBoolean(properties.getProperty("insertstructure.value"));
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
     * Sets the value of showToolbar.
     *
     *@param showToolBar
     *            The value to assign showToolbar.
     */
    public void setShowToolBar(boolean showToolBar) {
        this.showToolBar = showToolBar;
        customizeView();
    }
    
    public void customizeView() {
        if (showMenuBar) {
            if (menu == null) {
                menu = new JChemPaintMenuBar(this, this.guistring, blockList);
                topContainer.add(menu, BorderLayout.NORTH);
            }
        } else {
            topContainer.remove(menu);
        }
        if (showToolBar) {
            if (uppertoolbar == null) {
                uppertoolbar = JCPToolBar.getToolbar(this, "uppertoolbar", SwingConstants.HORIZONTAL, blockList);
            }
            if (lefttoolbar == null) {
                lefttoolbar = JCPToolBar.getToolbar(this, "lefttoolbar", SwingConstants.VERTICAL, blockList);
            }
            if (righttoolbar == null) {
                righttoolbar = JCPToolBar.getToolbar(this, "righttoolbar", SwingConstants.VERTICAL, blockList);
            }
            if (lowertoolbar == null) {
                lowertoolbar = JCPToolBar.getToolbar(this, "lowertoolbar", SwingConstants.HORIZONTAL, blockList);
            }

            if (uppertoolbar != null)
                topContainer.add(uppertoolbar, BorderLayout.SOUTH);
            if (lefttoolbar != null)
                centerContainer.add(lefttoolbar, BorderLayout.WEST);
            if (righttoolbar != null)
                centerContainer.add(righttoolbar, BorderLayout.EAST);
            if (lowertoolbar != null)
                centerContainer.add(lowertoolbar, BorderLayout.SOUTH);

        } else {
            topContainer.remove(uppertoolbar);
            centerContainer.remove(lowertoolbar);
            centerContainer.remove(lefttoolbar);
            centerContainer.remove(righttoolbar);
        }

        if (getShowInsertTextField()) {
            if (insertTextPanel == null)
                insertTextPanel = new InsertTextPanel(this, null);
            centerContainer.add(insertTextPanel, BorderLayout.NORTH);
        } else {
            if (insertTextPanel != null)
                centerContainer.remove(insertTextPanel);
        }
        topContainer.revalidate();
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
     * Enables or disables all JMenuItems in a JMenu.
     * 
     * @param root  The JMenu to search in.
     * @param b     Enable or disable.
     */
    protected void enOrDisableMenus(JMenu root, boolean b) {
        for(int i=0;i<root.getItemCount();i++){
            if(root.getItem(i) instanceof JMenu){
                ((JMenu)root.getItem(i)).setEnabled(b);
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
                    + " " + GT.get("has unsaved data. Do you want to save it?"),
                    GT.get("Unsaved data"), JOptionPane.YES_NO_CANCEL_OPTION,
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
            // ClearAllEdit(this.getChemModel(),(IAtomContainerSet)this.getChemModel().getMoleculeSet().clone(),this.getChemModel().getReactionSet());
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


    public void updateUndoRedoControls() {
        UndoManager undoManager = renderPanel.getUndoManager();
        JButton redoButton=buttons.get("redo");
        JButton undoButton=buttons.get("undo");
        if (undoManager.canRedo()) {
            redoButton.setEnabled(true);
            redoMenu.setEnabled(true);
            redoButton.setToolTipText(GT.get("Redo")+": "+undoManager.getRedoPresentationName());
        } else {
            redoButton.setEnabled(false);
            redoMenu.setEnabled(false);
            redoButton.setToolTipText(GT.get("No redo possible"));
        }

        if (undoManager.canUndo()) {
            undoButton.setEnabled(true);
            undoMenu.setEnabled(true);
            undoButton.setToolTipText(GT.get("Undo")+": "+undoManager.getUndoPresentationName());
        } else {
            undoButton.setEnabled(false);
            undoMenu.setEnabled(false);
            undoButton.setToolTipText(GT.get("No undo possible"));
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
                ((JFrame) c).setTitle('*' + id + AbstractJChemPaintPanel.getAppTitle());
            else
                ((JFrame) c).setTitle(id + AbstractJChemPaintPanel.getAppTitle());
        }
    }

    public boolean isModified() {
        return modified;
    }

    public JCPMenuTextMaker getMenuTextMaker() {
        return menuTextMaker;
    }

}
