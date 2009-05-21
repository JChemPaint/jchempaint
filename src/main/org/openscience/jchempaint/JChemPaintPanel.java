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
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.IChemModelEventRelayHandler;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.LogicalSelection;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.jchempaint.action.SaveAction;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;

public class JChemPaintPanel extends AbstractJChemPaintPanel implements
        IChemModelEventRelayHandler, ICDKChangeListener, KeyListener {

    private JComponent lastActionButton;
    private File currentWorkDirectory;
    private File lastOpenedFile;
    private FileFilter currentOpenFileFilter;
    private File isAlreadyAFile;
    private boolean isModified = false;
    private FileFilter currentSaveFileFilter;
    private JCPStatusBar statusBar;
    public static List<JChemPaintPanel> instances = new ArrayList<JChemPaintPanel>();
    private boolean showInsertTextField = true;
    private InsertTextPanel insertTextPanel = null;
    private JPanel topContainer = null;
    private boolean showToolBar = true;
    private boolean showStatusBar = true;
    private boolean showMenuBar = true;
    private JMenuBar menu;
    private String guistring;
    private JToolBar toolbar;
    private int lines = 1;
    // we remember some buttons and menus since these are special
    protected JButton moveButton = null;
    protected JButton undoButton;
    protected JButton redoButton;
	public JButton atomAtomMappingButton;
    protected JMenuItem undoMenu;
    protected JMenuItem redoMenu;
    private LoggingTool logger = new LoggingTool(this);

    public JChemPaintPanel(IChemModel chemModel, String gui) {
        this(chemModel, gui, 1);
    }

    /**
     * Builds a JCPPanel with a certain model and a certain gui
     * 
     * @param chemModel
     *            The model
     * @param gui
     *            The gui string
     */
    public JChemPaintPanel(IChemModel chemModel, String gui, int lines) {
        this.lines = lines;
        this.guistring = gui;
        this.setLayout(new BorderLayout());
        topContainer = new JPanel(new BorderLayout());
        topContainer.setLayout(new BorderLayout());
        this.add(topContainer, BorderLayout.NORTH);
        renderPanel = new RenderPanel(chemModel, getWidth(), getHeight(), false);
        this.add(new JScrollPane(renderPanel), BorderLayout.CENTER);

        customizeView();
        updateUndoRedoControls();
        SwingPopupModule inputAdapter = new SwingPopupModule(renderPanel,
                renderPanel.getHub());
        setupPopupMenus(inputAdapter);
        renderPanel.getHub().registerGeneralControllerModule(inputAdapter);
        renderPanel.getHub().setEventHandler(this);
        renderPanel.getRenderer().getRenderer2DModel().addCDKChangeListener(
                this);
        instances.add(this);
        this.addKeyListener(this);
    }

    public Container getTopLevelContainer() {
        return this.getParent().getParent().getParent().getParent();
    }

    public void setTitle(String title) {
        Container topLevelContainer = this.getTopLevelContainer();
        if (topLevelContainer instanceof JFrame) {
            ((JFrame) topLevelContainer).setTitle(title);
        }
    }

    public void setupPopupMenus(SwingPopupModule inputAdapter) {
        if (inputAdapter.getPopupMenu(PseudoAtom.class) == null) {
            inputAdapter.setPopupMenu(PseudoAtom.class,
                    new JChemPaintPopupMenu(this, "pseudo", this.guistring));
        }
        if (inputAdapter.getPopupMenu(Atom.class) == null) {
            inputAdapter.setPopupMenu(Atom.class, new JChemPaintPopupMenu(this,
                    "atom", this.guistring));
        }
        if (inputAdapter.getPopupMenu(Bond.class) == null) {
            inputAdapter.setPopupMenu(Bond.class, new JChemPaintPopupMenu(this,
                    "bond", this.guistring));
        }
        if (inputAdapter.getPopupMenu(ChemModel.class) == null) {
            inputAdapter.setPopupMenu(ChemModel.class, new JChemPaintPopupMenu(
                    this, "chemmodel", this.guistring));
        }
        if (inputAdapter.getPopupMenu(Reaction.class) == null) {
            inputAdapter.setPopupMenu(Reaction.class, new JChemPaintPopupMenu(
                    this, "reaction", this.guistring));
        }
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
     * @param isModified
     *            is modified
     */
    public void setModified(boolean isModified) {
        this.isModified = isModified;
        Container c = this.getTopLevelContainer();
        if (c instanceof JFrame) {
            String id = renderPanel.getChemModel().getID();
            if (isModified)
                ((JFrame) c).setTitle(id + "*");
            else
                ((JFrame) c).setTitle(id);
        }
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
                insertTextPanel = new InsertTextPanel(this, null);
            topContainer.add(insertTextPanel, BorderLayout.SOUTH);
        } else {
            topContainer.remove(insertTextPanel);
        }
        revalidate();
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
        setShowToolBar(showToolBar, 1);
    }

    /**
     * Sets the value of showToolbar.
     * 
     *@param showToolBar
     *            The value to assign showToolbar.
     */
    public void setShowToolBar(boolean showToolBar, int lines) {
        this.showToolBar = showToolBar;
        this.lines = lines;
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

    public String getGuistring() {
        return guistring;
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

    public String getSVGString() {
        return this.renderPanel.toSVG();
    }

    public Image takeSnapshot() {
        return this.renderPanel.takeSnapshot();
    }

    public Image takeSnapshot(Rectangle bounds) {
        return this.renderPanel.takeSnapshot(bounds);
    }

    /**
     * Shows a warning if the JCPPanel has unsaved content and does save, if the
     * user wants to do it.
     * 
     * @return 
     *         OptionPane.YES_OPTION/OptionPane.NO_OPTION/OptionPane.CANCEL_OPTION
     */
    public int showWarning() {
        if (isModified && !guistring.equals(JChemPaintEditorApplet.GUI_APPLET)) { // TODO
                                                                                  // &&
                                                                                  // !getIsOpenedByViewer())
                                                                                  // {
            int answer = JOptionPane.showConfirmDialog(this, renderPanel
                    .getChemModel().getID()
                    + " " + GT._("has unsaved data. Do you want to save it?"),
                    GT._("Unsaved data"), JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                new SaveAction(this, false).actionPerformed(new ActionEvent(
                        this, 12, ""));
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
     * Class for closing jcp
     * 
     *@author shk3
     *@cdk.created November 23, 2008
     */
    public final static class AppCloser extends WindowAdapter {

        /**
         * closing Event. Shows a warning if this window has unsaved data and
         * terminates jvm, if last window.
         * 
         * @param e
         *            Description of the Parameter
         */
        public void windowClosing(WindowEvent e) {
            // JFrame rootFrame = (JFrame) e.getSource();
            /*
             * TODO if (rootFrame.getContentPane().getComponent(0) instanceof
             * JChemPaintEditorPanel) { JChemPaintEditorPanel panel =
             * (JChemPaintEditorPanel)
             * rootFrame.getContentPane().getComponent(0);
             * panel.fireChange(JChemPaintEditorPanel.JCP_CLOSING); }
             */
            int clear = ((JChemPaintPanel) ((JFrame) e.getSource())
                    .getContentPane().getComponents()[0]).showWarning();
            if (JOptionPane.CANCEL_OPTION != clear) {
                for (int i = 0; i < instances.size(); i++) {
                    if (instances.get(i).getTopLevelContainer() == (JFrame) e
                            .getSource()) {
                        instances.remove(i);
                        break;
                    }
                }
                ((JFrame) e.getSource()).setVisible(false);
                ((JFrame) e.getSource()).dispose();
                if (instances.size() == 0) {// TODO &&
                                            // !((JChemPaintPanel)rootFrame.getContentPane().getComponent(0)).isEmbedded())
                                            // {
                    System.exit(0);
                }
            }
        }
    }

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

    /**
     * Closes all currently opened JCP instances.
     */
    public static void closeAllInstances() {
        int instancesNumber = instances.size();
        for (int i = instancesNumber - 1; i >= 0; i--) {
            JFrame frame = (JFrame) instances.get(i).getTopLevelContainer();
            WindowListener[] wls = (WindowListener[]) (frame
                    .getListeners(WindowListener.class));
            wls[0].windowClosing(new WindowEvent(frame,
                    WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * Selects the move button and action as the current action.
     */
    public void setMoveAction() {
        getLastActionButton().setBackground(Color.LIGHT_GRAY);
        setLastActionButton(moveButton);
        moveButton.setBackground(Color.GRAY);
    }

    public void coordinatesChanged() {
        setModified(true);
        updateStatusBar();
    }

    public void selectionChanged() {
        updateStatusBar();
    }

    public void structureChanged() {
        setModified(true);
        updateStatusBar();
    }

    public void structurePropertiesChanged() {
        setModified(true);
        updateStatusBar();
    }

    public void updateUndoRedoControls() {
        UndoManager undoManager = renderPanel.getUndoManager();

        if (undoManager.canRedo()) {
            redoButton.setEnabled(true);
            redoMenu.setEnabled(true);
            redoButton.setToolTipText(undoManager.getRedoPresentationName());
        } else {
            redoButton.setEnabled(false);
            redoMenu.setEnabled(false);
            redoButton.setToolTipText(GT._("No redo possible"));
        }

        if (undoManager.canUndo()) {
            undoButton.setEnabled(true);
            undoMenu.setEnabled(true);
            undoButton.setToolTipText(undoManager.getUndoPresentationName());
        } else {
            undoButton.setEnabled(false);
            undoMenu.setEnabled(false);
            undoButton.setToolTipText(GT._("No undo possible"));
        }
    }

    public void stateChanged(EventObject event) {
    }

    public void zoomFactorChanged(EventObject event) {
//        this.updateStatusBar();
    }

    public void keyPressed(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent arg0) {
        RendererModel model = renderPanel.getRenderer().getRenderer2DModel();
        ControllerHub relay = renderPanel.getHub();
        if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
            IChemObjectSelection selection = model.getSelection();
            if (selection.isFilled()) {
                IAtomContainer selected = selection.getConnectedAtomContainer();
                relay.deleteFragment(selected);
                model.setSelection(new LogicalSelection(
                        LogicalSelection.Type.NONE));
                relay.updateView();
            }
        } else if (model.getHighlightedAtom() != null) {
            try {
                IAtom closestAtom = model.getHighlightedAtom();
                char x = arg0.getKeyChar();
                if (Character.isLowerCase(x))
                    x = Character.toUpperCase(x);
                IsotopeFactory ifa;
                ifa = IsotopeFactory.getInstance(closestAtom.getBuilder());
                IIsotope iso = ifa.getMajorIsotope(Character.toString(x));
                if (iso != null) {
                    relay.setSymbol(closestAtom, Character.toString(x));
                }
            } catch (IOException e) {
                logger.debug(e);
            }
        }
    }

    public void keyTyped(KeyEvent arg0) {
    }

    public void zoomChanged() {
        this.updateStatusBar();
    }
}
