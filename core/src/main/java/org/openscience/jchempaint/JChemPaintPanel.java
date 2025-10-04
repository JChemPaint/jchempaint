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

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.selection.AbstractSelection;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.applet.JChemPaintAbstractApplet;
import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.controller.AddBondDragModule;
import org.openscience.jchempaint.controller.AddRingModule;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IChangeModeListener;
import org.openscience.jchempaint.controller.IChemModelEventRelayHandler;
import org.openscience.jchempaint.controller.IControllerModule;
import org.openscience.jchempaint.controller.MoveModule;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

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
        this(chemModel, JChemPaint.GUI_APPLICATION, false, null, Collections.emptySet());
    }

    /**
     * Builds a JCPPanel with a certain model and a certain gui.
     *
     * @param chemModel The model to display.
     * @param gui       The gui configuration string
     * @param debug     Should we be in debug mode?
     * @param applet    If this panel is to be in an applet, pass the applet here, else null.
     * @param blocked   A list of menuitesm/buttons which should be ignored when building gui.
     */
    public JChemPaintPanel(IChemModel chemModel, String gui, boolean debug, JChemPaintAbstractApplet applet, Set<String> blocked) {
        GT.setLanguage(JCPPropertyHandler.getInstance(true).getJCPProperties().getProperty("General.language"));
        this.guistring = gui;
        this.blockList = blocked;
        menuTextMaker = JCPMenuTextMaker.getInstance(guistring);
        this.debug = debug;
        try {
            renderPanel = new RenderPanel(chemModel, getWidth(), getHeight(), false, debug, false, applet);
        } catch (IOException e) {
            announceError(e);
        }
        if (gui.equals("application")) {
            setAppTitle(" - " +
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
        centerContainer = new JPanel();
        centerContainer.setLayout(new BorderLayout());
        centerContainer.add(new JScrollPane(renderPanel), BorderLayout.CENTER);
        this.add(centerContainer);

        customizeView();
        updateUndoRedoControls();
        SwingPopupModule inputAdapter = new SwingPopupModule(renderPanel,
                                                             renderPanel.getHub());
        setupPopupMenus(inputAdapter, blockList);
        renderPanel.getHub().registerGeneralControllerModule(inputAdapter);
        renderPanel.getHub().setEventHandler(this);
        renderPanel.getRenderer().getRenderer2DModel().addCDKChangeListener(
                this);
        instances.add(this);
        //we set this to true always, the user should have no option to switch it off
        renderPanel.getHub().getController2DModel().setAutoUpdateImplicitHydrogens(true);
        this.addKeyListener(this);
        renderPanel.addMouseListener(new MouseAdapter() {
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
     * @param blocked      A list of menuitesm/buttons which should be ignored when building gui.
     */
    public void setupPopupMenus(SwingPopupModule inputAdapter, Set<String> blocked) {
        inputAdapter.setPopupMenu(IPseudoAtom.class,
                                  new JChemPaintPopupMenu(this, "pseudo", this.guistring, blocked));
        inputAdapter.setPopupMenu(IAtom.class, new JChemPaintPopupMenu(this,
                                                                       "atom", this.guistring, blocked));
        inputAdapter.setPopupMenu(IBond.class, new JChemPaintPopupMenu(this,
                                                                       "bond", this.guistring, blocked));
        inputAdapter.setPopupMenu(IChemModel.class, new JChemPaintPopupMenu(
                this, "chemmodel", this.guistring, blocked));
    }


    /**
     * Class for closing jcp
     *
     * @author shk3
     * @cdk.created November 23, 2008
     */
    public final static class AppCloser extends WindowAdapter {

        /**
         * closing Event. Shows a warning if this window has unsaved data and
         * terminates jvm, if last window.
         *
         * @param e Description of the Parameter
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
        //move focus
        this.requestFocusInWindow();
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.controller.IChemModelEventRelayHandler#selectionChanged()
     */
    public void selectionChanged() {
        this.requestFocusInWindow();
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.controller.IChemModelEventRelayHandler#structureChanged()
     */
    public void structureChanged() {
        setModified(true);
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
    public void keyPressed(KeyEvent e) {
        JChemPaintRendererModel model = renderPanel.getRenderer().getRenderer2DModel();
        ControllerHub relay = renderPanel.getHub();

        if (relay.setAltInputMode(((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0))) {
            IControllerModule module = this.get2DHub().getActiveDrawModule();
            if (module != null)
                module.updateView();
            return;
        }

        this.get2DHub().clearPhantoms();

        /*
         * ATOM/BOND Hotkeys
         * This will be better as two config files
         * - JCP_AtomHotKeys.properties
         * - JCP_BondHotKeys.properties
         * But is functional for now
         */

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:

                // re-sync - make sure the highlighted atom has the correct
                //           context (e.g. bonding) based on the current model
                if (model.getHighlightedAtom() != null) {
                    IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(relay.getChemModel(),
                                                                                             model.getHighlightedAtom());
                    if (container != null)
                        model.setHighlightedAtom(container.getAtom(container.indexOf(model.getHighlightedAtom())));
                    else
                        model.setHighlightedAtom(null);
                }
                if (model.getHighlightedBond() != null) {
                    IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(relay.getChemModel(),
                                                                                             model.getHighlightedBond());
                    if (container != null)
                        model.setHighlightedBond(container.getBond(container.indexOf(model.getHighlightedBond())));
                    else
                        model.setHighlightedBond(null);
                }

                model.moveHighlight(e.getKeyCode());
                if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0)
                    model.moveHighlight(e.getKeyCode());
                this.get2DHub().updateView();
                return;
        }

        boolean changed = false;

        // if shift or nothing pressed we do the hot keys for atoms/bonds
        if (((e.getModifiersEx() & ~(KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)) == 0)) {
            char x = (char) (e.getKeyCode() >= 'A' && e.getKeyCode() <= 'Z' ? e.getKeyChar() : e.getKeyCode());
            if (model.getHighlightedAtom() != null) {
                IAtom hgAtom = model.getHighlightedAtom();
                changed = true;
                switch (x) {
                    case ' ':
                        relay.selectFragment(hgAtom);
                        break;
                    case '0':
                        relay.addAtom(hgAtom, IAtom.C, true);
                        break;
                    case '1':
                        relay.addAtom(hgAtom, IAtom.C, false);
                        break;
                    case '2':
                        relay.addAcetyl(hgAtom);
                        break;
                    case '3': // fall through 3/a
                    case 'a':
                        relay.addPhenyl(hgAtom, 6, false);
                        break;
                    case '4':
                        relay.addDimethyl(hgAtom, IBond.Display.WedgeBegin);
                        break;
                    case '5':
                        relay.addDimethyl(hgAtom, IBond.Display.WedgeEnd);
                        break;
                    case '6':
                        relay.addRing(hgAtom, 6, false);
                        break;
                    case '7':
                        relay.addRing(hgAtom, 5, false);
                        break;
                    case '8':
                        relay.addAtom(hgAtom, IAtom.C, IBond.Order.DOUBLE, false);
                        break;
                    case '9':
                        relay.addDimethyl(hgAtom, IBond.Display.Solid);
                        break;
                    case 'b':
                        relay.setSymbol(hgAtom, "B");
                        break;
                    case 'c':
                        relay.setSymbol(hgAtom, "C");
                        break;
                    case 'd':
                        relay.setSymbol(hgAtom, "H", 2);
                        break;
                    // case "e": break; // ethyl
                    case 'f':
                        relay.setSymbol(hgAtom, "F");
                        break;
                    case 'h':
                        relay.setSymbol(hgAtom, "H");
                        break;
                    case 'i':
                        relay.setSymbol(hgAtom, "I");
                        break;
                    case 'n':
                    case 'w':
                        relay.setSymbol(hgAtom, "N");
                        break;
                    case 'q':
                    case 'o':
                        relay.setSymbol(hgAtom, "O");
                        break;
                    case 's':
                        relay.setSymbol(hgAtom, "S");
                        break;
                    case 'p':
                        relay.setSymbol(hgAtom, "P");
                        break;
                    case 'r':
                        relay.setSymbol(hgAtom, "R");
                        break;
                    case 'B':
                        relay.setSymbol(hgAtom, "Br");
                        break;
                    case 'C': // fall through C/l
                    case 'l':
                        relay.setSymbol(hgAtom, "Cl");
                        break;
                    case 'S':
                        relay.setSymbol(hgAtom, "Si");
                        break;
                    case '.':
                        relay.convertToPseudoAtom(hgAtom, "_AP1");
                        break;
                    default:
                        changed = false;
                }
            } else if (model.getHighlightedBond() != null) {
                IBond hgBond = model.getHighlightedBond();
                changed = true;
                switch (x) {
                    case ' ':
                        relay.selectFragment(hgBond);
                        break;
                    case '1':
                        relay.cycleBondValence(hgBond);
                        break;
                    case '2':
                        relay.changeBond(hgBond, IBond.Order.DOUBLE, IBond.Display.Solid);
                        break;
                    case '3':
                        relay.changeBond(hgBond, IBond.Order.TRIPLE, IBond.Display.Solid);
                        break;
                    case '4':
                        relay.addRing(hgBond, 4, false);
                        break;
                    case '5':
                        relay.addRing(hgBond, 5, false);
                        break;
                    case '6':
                        relay.addRing(hgBond, 6, false);
                        break;
                    case '7':
                        relay.addRing(hgBond, 7, false);
                        break;
                    case '8':
                        relay.addRing(hgBond, 8, false);
                        break;
                    case 'a':
                        relay.addPhenyl(hgBond, 6, false);
                        break;
                    case 'w':
                        relay.changeBond(hgBond, IBond.Order.SINGLE, IBond.Display.WedgeBegin);
                        break;
                    case 'W': // shift + W
                    case 'h':
                        relay.changeBond(hgBond, IBond.Order.SINGLE, IBond.Display.WedgedHashBegin);
                        break;
                    case 'y':
                        relay.changeBond(hgBond, IBond.Order.SINGLE, IBond.Display.Wavy);
                        break;
                    case 'b':
                        relay.changeBond(hgBond, IBond.Order.SINGLE, IBond.Display.Bold);
                        break;
                    default:
                        changed = false;
                }
            } else if (model.getSelection() == null || !model.getSelection().isFilled()) {
                changed = true;
                switch (x) {
                    case '1':
                        this.get2DHub().setActiveDrawModule(new AddBondDragModule(relay, IBond.Order.SINGLE, true, "bondTool"));
                        break;
                    case '2':
                        this.get2DHub().setActiveDrawModule(new AddBondDragModule(relay, IBond.Order.DOUBLE, true, "double_bondTool"));
                        break;
                    case 'a':
                        this.get2DHub().setActiveDrawModule(new AddRingModule(relay, 6, true, "benzene"));
                        break;
                    case '3':
                        this.get2DHub().setActiveDrawModule(new AddRingModule(relay, 3, false, "triangle"));
                        break;
                    case '4':
                        this.get2DHub().setActiveDrawModule(new AddRingModule(relay, 4, false, "square"));
                        break;
                    case '5':
                        this.get2DHub().setActiveDrawModule(new AddRingModule(relay, 5, false, "pentagon"));
                        break;
                    case '6':
                        this.get2DHub().setActiveDrawModule(new AddRingModule(relay, 6, false, "hexagon"));
                        break;
                    case '7':
                        this.get2DHub().setActiveDrawModule(new AddRingModule(relay, 7, false, "heptagon"));
                        break;
                    case '8':
                        this.get2DHub().setActiveDrawModule(new AddRingModule(relay, 8, false, "octagon"));
                        break;
                    default:
                        changed = false;
                        break;
                }
                if (changed) {
                    this.get2DHub().updateView();
                    changed = false;
                }
            }
        }


        if (changed) {
            this.get2DHub().setActiveDrawModule(null);
            this.get2DHub().updateView();
            this.get2DHub().clearPhantoms();
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
        ControllerHub relay = renderPanel.getHub();
        boolean changed = relay.setAltInputMode(!((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == 0));
        if (changed) {
            this.get2DHub().updateView();
            IControllerModule module = this.get2DHub().getActiveDrawModule();
            if (module != null)
                module.updateView();
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
        if (this.getLastActionButton() != null) {
            this.getLastActionButton().setBackground(null);
        }
        String actionid = newActiveModule.getID();
        //this is because move mode does not have a button
        if (actionid.equals("move"))
            actionid = lastSelectId;
        //we remember the last activated move mode so that we can switch back to it after move
        if (newActiveModule.getID().equals("select") || newActiveModule.getID().equals("lasso"))
            lastSelectId = newActiveModule.getID();
        //we set the new button to active colour
        JButton newActionButton = buttons.get(actionid);
        if (newActionButton != null) {
            this.setLastActionButton(newActionButton);
            newActionButton.setBackground(JCPToolBar.BUTON_ACTIVE_COLOR);
        }
        if (!(newActiveModule instanceof MoveModule)) {
            this.renderPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.get2DHub().updateView();
        }
    }

    /**
     * Gets all atomcontainers of a chemodel in one AtomContainer.
     *
     * @param chemModel The chemodel
     * @return The result.
     */
    public static IAtomContainer getAllAtomContainersInOne(IChemModel chemModel) {
        List<IAtomContainer> acs = ChemModelManipulator.getAllAtomContainers(chemModel);
        IAtomContainer allinone = chemModel.getBuilder().newInstance(IAtomContainer.class);
        for (int i = 0; i < acs.size(); i++) {
            allinone.add(acs.get(i));
        }
        return allinone;
    }
}
