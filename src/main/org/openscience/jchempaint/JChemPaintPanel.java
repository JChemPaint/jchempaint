/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
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
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.action.SaveAction;
import org.openscience.jchempaint.applet.JChemPaintAbstractApplet;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;
import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.controller.AddAtomModule;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IChangeModeListener;
import org.openscience.jchempaint.controller.IChemModelEventRelayHandler;
import org.openscience.jchempaint.controller.IControllerModule;
import org.openscience.jchempaint.controller.MoveModule;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.selection.AbstractSelection;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;

public class JChemPaintPanel extends AbstractJChemPaintPanel implements
        IChemModelEventRelayHandler, ICDKChangeListener, KeyListener, IChangeModeListener {

    private static final long serialVersionUID = 7810772571955039160L;
    public static List<JChemPaintPanel> instances = new ArrayList<JChemPaintPanel>();
    private String lastSelectId;
    private JCPTransferHandler handler;

    /**
     * Builds a JCPPanel with a certain model. GUI is that of the application.
     * 
     * @param chemModel The model to display.
     */
    public JChemPaintPanel(IChemModel chemModel) {
        this(chemModel, JChemPaint.GUI_APPLICATION, false, null, new ArrayList<String>());
    }

    /**
     * Builds a JCPPanel with a certain model and a certain gui.
     *
     * @param chemModel   The model to display.
     * @param gui         The gui configuration string
     * @param debug       Should we be in debug mode?
     * @param applet      If this panel is to be in an applet, pass the applet here, else null.
  	 * @param  blacklist       A list of menuitesm/buttons which should be ignored when building gui.
     */
    public JChemPaintPanel(IChemModel chemModel, String gui, boolean debug, JChemPaintAbstractApplet applet, List<String> blacklist) {
        GT.setLanguage(JCPPropertyHandler.getInstance(true).getJCPProperties().getProperty("General.language"));
        this.guistring = gui;
        this.blacklist = blacklist;
        menuTextMaker = JCPMenuTextMaker.getInstance(guistring);
        this.debug = debug;
        try {
			renderPanel = new RenderPanel(chemModel, getWidth(), getHeight(), false, debug, false, applet);
		} catch (IOException e) {
			announceError(e);
		}
		if (gui.equals("application")) {
			setAppTitle(" - "+
			        new JChemPaintMenuHelper().getMenuResourceString("Title", guistring));
		}
        init();        
    }

    protected void init() {
        this.setLayout(new BorderLayout());
        topContainer = new JPanel(new BorderLayout());
        topContainer.setLayout(new BorderLayout());
        this.add(topContainer, BorderLayout.NORTH);

        renderPanel.getHub().addChangeModeListener(this);
        renderPanel.setName("renderpanel");
        centerContainer=new JPanel();
        centerContainer.setLayout(new BorderLayout());
        centerContainer.add(new JScrollPane(renderPanel), BorderLayout.CENTER);
        this.add(centerContainer);

        customizeView();
        updateUndoRedoControls();
        SwingPopupModule inputAdapter = new SwingPopupModule(renderPanel,
                renderPanel.getHub());
        setupPopupMenus(inputAdapter, blacklist);
        renderPanel.getHub().registerGeneralControllerModule(inputAdapter);
        renderPanel.getHub().setEventHandler(this);
        renderPanel.getRenderer().getRenderer2DModel().addCDKChangeListener(
                this);
        instances.add(this);
        //we set this to true always, the user should have no option to switch it off
        renderPanel.getHub().getController2DModel().setAutoUpdateImplicitHydrogens(true);
        this.addKeyListener(this);
        renderPanel.addMouseListener(new MouseAdapter(){
            public void mouseExited(MouseEvent e) {
                //this avoids ghost phantom rings if the user leaves the panel
                JChemPaintPanel.this.get2DHub().clearPhantoms();
                JChemPaintPanel.this.get2DHub().updateView();
            }            
        });
        handler = new JCPTransferHandler(this);
        renderPanel.setTransferHandler(handler);
    }

    /**
     * Gets the top level container (JFrame, Applet) of this panel.
     * 
     * @return The top level container.
     */
    public Container getTopLevelContainer() {
        return this.getParent().getParent().getParent().getParent();
    }

    /**
     * If this panel is in a JFrame, sets the title of the JFrame.
     * 
     * @param title The title to set.
     */
    public void setTitle(String title) {
        Container topLevelContainer = this.getTopLevelContainer();
        if (topLevelContainer instanceof JFrame) {
            ((JFrame) topLevelContainer).setTitle(title + appTitle);
        }
    }

    /**
     * Installs popup menus for this panel.
     * 
     * @param inputAdapter The SwingPopupModule to use for the popup menus.
  	 * @param  blacklist       A list of menuitesm/buttons which should be ignored when building gui.
     */
    public void setupPopupMenus(SwingPopupModule inputAdapter, List<String> blacklist) {
    	inputAdapter.setPopupMenu(PseudoAtom.class,
    			new JChemPaintPopupMenu(this, "pseudo", this.guistring, blacklist));
    	inputAdapter.setPopupMenu(Atom.class, new JChemPaintPopupMenu(this,
    			"atom", this.guistring, blacklist));
    	inputAdapter.setPopupMenu(Bond.class, new JChemPaintPopupMenu(this,
    			"bond", this.guistring, blacklist));
    	inputAdapter.setPopupMenu(ChemModel.class, new JChemPaintPopupMenu(
    			this, "chemmodel", this.guistring, blacklist));
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

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.controller.IChemModelEventRelayHandler#coordinatesChanged()
     */
    public void coordinatesChanged() {
        setModified(true);
        updateStatusBar();
        //move focus
        this.requestFocusInWindow();
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.controller.IChemModelEventRelayHandler#selectionChanged()
     */
    public void selectionChanged() {
        updateStatusBar();
        
        if(this.getRenderPanel().getRenderer().getRenderer2DModel().getSelection()!=null) {
        	IChemObjectSelection selection = this.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();

        	if (selection.getConnectedAtomContainer()!=null && selection.getConnectedAtomContainer().getAtomCount()>0)
                enOrDisableMenus(atomMenu,true);
            else
                enOrDisableMenus(atomMenu,false);

        	if (selection.getConnectedAtomContainer()!=null && selection.getConnectedAtomContainer().getBondCount()>0)
                enOrDisableMenus(bondMenu,true);
            else
                enOrDisableMenus(bondMenu,false);

        }
        this.requestFocusInWindow();
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.controller.IChemModelEventRelayHandler#structureChanged()
     */
    public void structureChanged() {
        setModified(true);
        updateStatusBar();
        //if something changed in the structure, selection should be cleared
        //this is behaviour like eg in word processors, if you type, selection goes away
        this.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(AbstractSelection.EMPTY_SELECTION);
        updateUndoRedoControls();
        this.get2DHub().updateView();
        //move focus
        //renderPanel.requestFocusInWindow();
        this.requestFocusInWindow();
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.controller.IChemModelEventRelayHandler#structurePropertiesChanged()
     */
    public void structurePropertiesChanged() {
        setModified(true);
        updateStatusBar();
        //if something changed in the structure, selection should be cleared
        //this is behaviour like eg in word processors, if you type, selection goes away
        this.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(AbstractSelection.EMPTY_SELECTION);
        //move focus
        this.requestFocusInWindow();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.event.ICDKChangeListener#stateChanged(java.util.EventObject)
     */
    public void stateChanged(EventObject event) {
    	updateUndoRedoControls();
        //move focus
        this.requestFocusInWindow();
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent arg0) {
        RendererModel model = renderPanel.getRenderer().getRenderer2DModel();
        ControllerHub relay = renderPanel.getHub();
        if (model.getHighlightedAtom() != null) {
            try {
                IAtom closestAtom = model.getHighlightedAtom();
                char x = arg0.getKeyChar();                
                if (Character.isLowerCase(x))
                    x = Character.toUpperCase(x);
                System.out.println(x);
                IsotopeFactory ifa;
                ifa = IsotopeFactory.getInstance(closestAtom.getBuilder());
                IIsotope iso = ifa.getMajorIsotope(Character.toString(x));
                if (iso != null) {
                    relay.setSymbol(closestAtom, Character.toString(x));
                }
                this.get2DHub().updateView();
            } catch (IOException e) {
                announceError(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent arg0) {
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.controller.IChemModelEventRelayHandler#zoomChanged()
     */
    public void zoomChanged() {
        this.updateStatusBar();
        //move focus
        this.requestFocusInWindow();
    }


	/* (non-Javadoc)
	 * @see org.openscience.cdk.controller.ChangeModeListener#modeChanged(org.openscience.cdk.controller.IControllerModule)
	 */
	public void modeChanged(IControllerModule newActiveModule) {
        //move focus
        this.requestFocusInWindow();
	    //we set the old button to inactive colour
        if (this.getLastActionButton() != null )
            this.getLastActionButton().setBackground(JCPToolBar.BUTTON_INACTIVE_COLOR);
        if (this.lastSecondaryButton != null)
            this.lastSecondaryButton.setBackground(JCPToolBar.BUTTON_INACTIVE_COLOR);
        String actionid = newActiveModule.getID();
        //this is because move mode does not have a button
        if(actionid.equals("move"))
            actionid=lastSelectId;
        //we remember the last activated move mode so that we can switch back to it after move
        if(newActiveModule.getID().equals("select") || newActiveModule.getID().equals("lasso"))
            lastSelectId = newActiveModule.getID();
        //we set the new button to active colour
        JButton newActionButton=buttons.get(actionid);
        if(newActionButton!=null){
            this.setLastActionButton(newActionButton);
            newActionButton.setBackground(Color.GRAY);
        }
        if(JCPToolBar.getToolbarResourceString("lefttoolbar", getGuistring()).indexOf(newActiveModule.getID())>-1 && !newActiveModule.getID().equals("reactionArrow")){
            if(this.buttons.get(this.get2DHub().getController2DModel().getDrawElement())!=null){
                this.buttons.get(this.get2DHub().getController2DModel().getDrawElement()).setBackground(Color.GRAY);
                lastSecondaryButton = this.buttons.get(this.get2DHub().getController2DModel().getDrawElement());
            }else if(buttons.get("periodictable")!=null){
                buttons.get("periodictable").setBackground(Color.GRAY);
                lastSecondaryButton = buttons.get("periodictable");
            }
        } else if (actionid!=null && actionid.equals("RX")) {
            this.buttons.get("enterR").setBackground(Color.GRAY);
            lastSecondaryButton = this.buttons.get("enterR");
        }
        if(JCPToolBar.getToolbarResourceString("lowertoolbar", getGuistring()).indexOf(newActiveModule.getID())>-1){
            //the newActiveModule should always be an AddAtomModule, but we still check
            if(newActiveModule instanceof AddAtomModule){
                if(((AddAtomModule)newActiveModule).getStereoForNewBond().equals(IBond.Stereo.NONE)){
                    this.buttons.get("bondTool").setBackground(Color.GRAY);
                    lastSecondaryButton = this.buttons.get("bondTool");
                }else if(((AddAtomModule)newActiveModule).getStereoForNewBond().equals(IBond.Stereo.UP)){
                    this.buttons.get("up_bond").setBackground(Color.GRAY);
                    lastSecondaryButton = this.buttons.get("up_bond");
                }else if(((AddAtomModule)newActiveModule).getStereoForNewBond().equals(IBond.Stereo.DOWN)){
                    this.buttons.get("down_bond").setBackground(Color.GRAY);
                    lastSecondaryButton = this.buttons.get("down_bond");
                }else if(((AddAtomModule)newActiveModule).getStereoForNewBond().equals(IBond.Stereo.E_OR_Z)){
                    this.buttons.get("undefined_bond").setBackground(Color.GRAY);
                    lastSecondaryButton = this.buttons.get("undefined_bond");
                }else if(((AddAtomModule)newActiveModule).getStereoForNewBond().equals(IBond.Stereo.UP_OR_DOWN)){
                    this.buttons.get("undefined_stereo_bond").setBackground(Color.GRAY);
                    lastSecondaryButton = this.buttons.get("undefined_stereo_bond");
                }
            }else{
                this.buttons.get("bondTool").setBackground(Color.GRAY);
                lastSecondaryButton = this.buttons.get("bondTool");
            }
        }
        if(!(newActiveModule instanceof MoveModule)){
            this.renderPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.get2DHub().updateView();
        }
        this.updateStatusBar();
	}
	
    /**
     * Gets all atomcontainers of a chemodel in one AtomContainer.
     * 
     * @param chemModel The chemodel
     * @return The result.
     */
    public static IAtomContainer getAllAtomContainersInOne(IChemModel chemModel){
		List<IAtomContainer> acs=ChemModelManipulator.getAllAtomContainers(chemModel);
		IAtomContainer allinone=chemModel.getBuilder().newInstance(IAtomContainer.class);
		for(int i=0;i<acs.size();i++){
			allinone.add(acs.get(i));
		}
		return allinone;
    }

    /**
     * Sets the lastSecondaryButton attribute. Only to be used once from JCPToolBar.
     * 
     * @param lastSecondaryButton The lastSecondaryButton.
     */
    public void setLastSecondaryButton(JComponent lastSecondaryButton) {
        this.lastSecondaryButton = lastSecondaryButton;
    }
}
