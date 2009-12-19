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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.AddAtomModule;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IChangeModeListener;
import org.openscience.jchempaint.controller.IChemModelEventRelayHandler;
import org.openscience.jchempaint.controller.IControllerModule;
import org.openscience.jchempaint.controller.MoveModule;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.selection.AbstractSelection;

public class JChemPaintPanel extends AbstractJChemPaintPanel implements
        IChemModelEventRelayHandler, ICDKChangeListener, KeyListener, IChangeModeListener {

    private static final long serialVersionUID = 7810772571955039160L;
    public static List<JChemPaintPanel> instances = new ArrayList<JChemPaintPanel>();
    private String lastSelectId;
    TransferHandler th;

    public JChemPaintPanel() {
    }

	/**
     * Builds a JCPPanel with a certain model and a certain gui
     *
     * @param chemModel
     *            The model
     * @param gui
     *            The gui string
     */
    public JChemPaintPanel(IChemModel chemModel, String gui, boolean debug) {
        this.guistring = gui;
        menuTextMaker = JCPMenuTextMaker.getInstance(guistring);
        this.debug = debug;
        try {
			renderPanel = new RenderPanel(chemModel, getWidth(), getHeight(), false, debug);
		} catch (IOException e) {
			announceError(e);
		}
		if (gui.equals("stable")) {
			setAppTitle(" - JChemPaint");
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
        setupPopupMenus(inputAdapter);
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
        th = renderPanel.getTransferHandler();
    }
    
    public void setTitle(String title) {
        Container topLevelContainer = this.getTopLevelContainer();
        if (topLevelContainer instanceof JFrame) {
            ((JFrame) topLevelContainer).setTitle(title + appTitle);
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

    public void coordinatesChanged() {
        setModified(true);
        updateStatusBar();
    }

    public void selectionChanged() {
        updateStatusBar();
        if(this.getRenderPanel().getRenderer().getRenderer2DModel().getSelection()!=null 
        		&& this.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer()!=null 
        		&& this.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().getAtomCount()>0)
            enOrDisableMenus(atomMenu,true);
        else
            enOrDisableMenus(atomMenu,false);
        if(this.getRenderPanel().getRenderer().getRenderer2DModel().getSelection()!=null 
        		&& this.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer()!=null 
        		&& this.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().getBondCount()>0)
            enOrDisableMenus(bondMenu,true);
        else
            enOrDisableMenus(bondMenu,false);
    }


    public void structureChanged() {
        setModified(true);
        updateStatusBar();
        //if something changed in the structure, selection should be cleared
        //this is behaviour like eg in word processors, if you type, selection goes away
        this.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(AbstractSelection.EMPTY_SELECTION);
        this.get2DHub().updateView();
    }

    public void structurePropertiesChanged() {
        setModified(true);
        updateStatusBar();
        //if something changed in the structure, selection should be cleared
        //this is behaviour like eg in word processors, if you type, selection goes away
        this.getRenderPanel().getRenderer().getRenderer2DModel().setSelection(AbstractSelection.EMPTY_SELECTION);
    }

    public void stateChanged(EventObject event) {
    	updateUndoRedoControls();
    }

    public void zoomFactorChanged(EventObject event) {
    }

    public void keyPressed(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent arg0) {
        RendererModel model = renderPanel.getRenderer().getRenderer2DModel();
        ControllerHub relay = renderPanel.getHub();
        if (model.getHighlightedAtom() != null) {
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
                this.get2DHub().updateView();
            } catch (IOException e) {
                announceError(e);
            }
        }
    }

    public void keyTyped(KeyEvent arg0) {
    }

    public void zoomChanged() {
        this.updateStatusBar();
    }


	/* (non-Javadoc)
	 * @see org.openscience.cdk.controller.ChangeModeListener#modeChanged(org.openscience.cdk.controller.IControllerModule)
	 */
	public void modeChanged(IControllerModule newActiveModule) {
	    //we set the old button to inactive colour
        if (this.getLastActionButton() != null)
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
        if(JCPToolBar.getToolbarResourceString("lefttoolbar", getGuistring()).indexOf(newActiveModule.getID())>-1){
            if(this.buttons.get(this.get2DHub().getController2DModel().getDrawElement())!=null){
                this.buttons.get(this.get2DHub().getController2DModel().getDrawElement()).setBackground(Color.GRAY);
                lastSecondaryButton = this.buttons.get(this.get2DHub().getController2DModel().getDrawElement());
            }else if(buttons.get("periodictable")!=null){
                buttons.get("periodictable").setBackground(Color.GRAY);
                lastSecondaryButton = buttons.get("periodictable");
            }
        }
        if(JCPToolBar.getToolbarResourceString("lowertoolbar", getGuistring()).indexOf(newActiveModule.getID())>-1){
            //the newActiveModule should always be an AddAtomModule, but we still check
            if(newActiveModule instanceof AddAtomModule){
                if(((AddAtomModule)newActiveModule).getStereoForNewBond().equals(IBond.Stereo.NONE)){
                    this.buttons.get("bond").setBackground(Color.GRAY);
                    lastSecondaryButton = this.buttons.get("bond");
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
                this.buttons.get("bond").setBackground(Color.GRAY);
                lastSecondaryButton = this.buttons.get("bond");
            }
        }
        if(!(newActiveModule instanceof MoveModule)){
            this.renderPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.get2DHub().updateView();
        }
        this.updateStatusBar();
	}
	
    public static IAtomContainer getAllAtomContainersInOne(IChemModel chemModel){
		List<IAtomContainer> acs=ChemModelManipulator.getAllAtomContainers(chemModel);
		IAtomContainer allinone=chemModel.getBuilder().newAtomContainer();
		for(int i=0;i<acs.size();i++){
			allinone.add(acs.get(i));
		}
		return allinone;
    }
    
    /**
     * Drag&Drop support
     *
     *@author Konstantin Tokarev
     *
     */
    /*private TransferHandler handler = new TransferHandler() {
        public boolean canImport(TransferHandler.TransferSupport support) {
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            if (copyItem.isSelected()) {
                boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;

                if (!copySupported) {
                    return false;
                }

                support.setDropAction(COPY);
            }

            return true;
        }

        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            Transferable t = support.getTransferable();

            try {
                java.util.List<File> l =
                    (java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);

                for (File f : l) {
                    new Doc(f);
                }
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }
    };*/
}
