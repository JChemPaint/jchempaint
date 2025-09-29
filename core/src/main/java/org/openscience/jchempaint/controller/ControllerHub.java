/* $Revision: 7636 $ $Author: egonw $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007-2008  Egon Willighagen <egonw@users.sf.net>
 *               2005-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.controller;

import org.apache.log4j.Logger;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.XMLIsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Display;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IBond.Stereo;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.layout.AtomPlacer;
import org.openscience.cdk.layout.RingPlacer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.stereo.Projection;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.validate.ProblemMarker;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.RenderPanel;
import org.openscience.jchempaint.applet.JChemPaintAbstractApplet;
import org.openscience.jchempaint.controller.undoredo.AddAtomsAndBondsEdit;
import org.openscience.jchempaint.controller.undoredo.AdjustBondOrdersEdit;
import org.openscience.jchempaint.controller.undoredo.ChangeAtomSymbolEdit;
import org.openscience.jchempaint.controller.undoredo.ChangeHydrogenCountEdit;
import org.openscience.jchempaint.controller.undoredo.ChangeIsotopeEdit;
import org.openscience.jchempaint.controller.undoredo.CompoundEdit;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.controller.undoredo.ReplaceAtomEdit;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.BoundsCalculator;
import org.openscience.jchempaint.renderer.IRenderer;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.selection.IncrementalSelection;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;
import org.openscience.jchempaint.rgroups.RGroupHandler;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that will central interaction point between a mouse event throwing
 * widget (SWT or Swing) and the Controller2D modules. IMPORTANT: All actions in
 * this class must adhere to the following rules: - They keep any fragments in
 * separate Molecules in the SetOfMolecules, i. e. if splits or merges are done,
 * they must handle this (precondition and postcondition: Each Molecule in
 * SetOfMolecules is a linked graph). - The chemModel always contains a
 * SetOfMolecules with at least one Molecule, this can be empty. No other
 * containers are allowed to be empty (precondition and postcondition:
 * SetOfMolecules.getAtomContainerCount>0, atomCount>0 for all Molecules in
 * SetOfMolecules where index>0).
 *
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.module controlbasic
 * @author Niels Out
 * @author egonw
 */
public class ControllerHub implements IMouseEventRelay, IChemModelRelay {

	private static final Logger log = Logger.getLogger(ControllerHub.class);
	private IChemModel chemModel;

	private IControllerModel controllerModel;

	private IRenderer renderer;

	private RenderPanel eventRelay;

	private List<IControllerModule> generalModules;

	private List<IChangeModeListener> changeModeListeners = new ArrayList<IChangeModeListener>();

	private static StructureDiagramGenerator diagramGenerator;

	private IControllerModule activeDrawModule;
	private IControllerModule fallbackModule;

	private final static RingPlacer ringPlacer = new RingPlacer();

	private IAtomContainer phantoms;

	private IChemModelEventRelayHandler changeHandler;

	private IUndoRedoFactory undoredofactory;

	private UndoRedoHandler undoredohandler;

	private CDKAtomTypeMatcher matcher;

	private static RGroupHandler rGroupHandler;

	int oldMouseCursor = Cursor.DEFAULT_CURSOR;

	private Point2d phantomArrowStart = null;

	private Point2d phantomArrowEnd = null;

	private Point2d phantomTextPosition = null;

	private String phantomText = null;

	/** Alternative input mode allow different actions when alt is held. */
	private boolean altMode = false;

    private Cursor rotateCursor;

	public ControllerHub(IControllerModel controllerModel, IRenderer renderer,
			IChemModel chemModel, RenderPanel eventRelay,
			UndoRedoHandler undoredohandler, IUndoRedoFactory undoredofactory,
			boolean isViewer, JChemPaintAbstractApplet applet) {
		this.controllerModel = controllerModel;
		this.renderer = renderer;
		this.chemModel = chemModel;
		this.eventRelay = eventRelay;
		this.phantoms = chemModel.getBuilder().newInstance(IAtomContainer.class);
		this.undoredofactory = undoredofactory;
		this.undoredohandler = undoredohandler;
		generalModules = new ArrayList<IControllerModule>();
		if (!isViewer) {
			registerGeneralControllerModule(new ZoomModule(this));
		}
		registerGeneralControllerModule(new HighlightModule(this, applet));
		matcher = CDKAtomTypeMatcher.getInstance(chemModel.getBuilder());

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        rotateCursor = toolkit.createCustomCursor(toolkit.getImage(getClass().getResource("cursors/rotate.png"))
                                                         .getScaledInstance(16, 16, Image.SCALE_SMOOTH),
                                                  new Point(8, 8),
                                                  "rotate");
	}

	public IControllerModel getController2DModel() {
		return controllerModel;
	}

	public IRenderer getRenderer() {
		return renderer;
	}

    public IChemModel getIChemModel() {
		return chemModel;
	}

	public void setChemModel(IChemModel model) {
		this.chemModel = model;
		structureChanged();
	}

	/**
	 * Unregister all general IController2DModules.
	 */
	public void unRegisterAllControllerModule() {
		generalModules.clear();
	}

	/**
	 * Adds a general IController2DModule which will catch all mouse events.
	 */
	public void registerGeneralControllerModule(IControllerModule module) {
		module.setChemModelRelay(this);
		generalModules.add(module);
	}

	public void mouseWheelMovedBackward(int modifiers, int clicks) {
		for (IControllerModule module : generalModules) {
			module.mouseWheelMovedBackward(modifiers, clicks);
		}
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null)
			activeModule.mouseWheelMovedBackward(modifiers, clicks);

	}

	public void mouseWheelMovedForward(int modifiers, int clicks) {
		for (IControllerModule module : generalModules) {
			module.mouseWheelMovedForward(modifiers, clicks);
		}
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null)
			activeModule.mouseWheelMovedForward(modifiers, clicks);

	}

	public void mouseClickedDouble(int screenCoordX, int screenCoordY) {
		Point2d worldCoord = renderer.toModelCoordinates(screenCoordX,
				screenCoordY);

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseClickedDouble(worldCoord);
		}

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null)
			activeModule.mouseClickedDouble(worldCoord);
	}

	public void mouseClickedDownRight(int screenX, int screenY) {
		Point2d modelCoord = renderer.toModelCoordinates(screenX, screenY);

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null)
			activeModule.mouseClickedDownRight(modelCoord);
		if (activeModule.wasEscaped()) {
			setActiveDrawModule(null);
			return;
		}

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseClickedDownRight(modelCoord);
		}
}

	public void mouseClickedUpRight(int screenX, int screenY) {
		Point2d modelCoord = renderer.toModelCoordinates(screenX, screenY);

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseClickedUpRight(modelCoord);
		}

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null)
			activeModule.mouseClickedUpRight(modelCoord);
	}

	public void mouseClickedDown(int screenX, int screenY, int modifiers) {
		Point2d modelCoord = renderer.toModelCoordinates(screenX, screenY);

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseClickedDown(modelCoord, modifiers);
		}

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null) {
			activeModule.mouseClickedDown(modelCoord, modifiers);
		}

		if (getCursor() == Cursor.HAND_CURSOR) {
			setCursor(Cursor.MOVE_CURSOR);
			oldMouseCursor = Cursor.HAND_CURSOR;
		} else if (getCursor() != Cursor.DEFAULT_CURSOR) {
			oldMouseCursor = Cursor.DEFAULT_CURSOR;
		}
	}

    public void mouseClickedDown(int screenX, int screenY) {
        mouseClickedDown(screenX, screenY, 0);
    }

	public void mouseClickedUp(int screenX, int screenY, int modifiers) {
		Point2d modelCoord = renderer.toModelCoordinates(screenX, screenY);

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseClickedUp(modelCoord, modifiers);
		}

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null) {
			activeModule.mouseClickedUp(modelCoord, modifiers);
		}

		setCursor(oldMouseCursor);
	}

    public void mouseClickedUp(int screenX, int screenY) {
        mouseClickedUp(screenX, screenY, 0);
    }

	public void mouseDrag(int screenXFrom, int screenYFrom, int screenXTo,
			int screenYTo, int modifiers) {
		Point2d modelCoordFrom = renderer.toModelCoordinates(screenXFrom,
				screenYFrom);
		Point2d modelCoordTo = renderer
				.toModelCoordinates(screenXTo, screenYTo);

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseDrag(modelCoordFrom, modelCoordTo, modifiers);
		}

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null) {
			activeModule.mouseDrag(modelCoordFrom, modelCoordTo, modifiers);
		}
	}

	public void mouseEnter(int screenX, int screenY) {
		Point2d worldCoord = renderer.toModelCoordinates(screenX, screenY);

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseEnter(worldCoord);
		}

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null)
			activeModule.mouseEnter(worldCoord);
	}

	public void mouseExit(int screenX, int screenY) {
		Point2d worldCoord = renderer.toModelCoordinates(screenX, screenY);

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseExit(worldCoord);
		}

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null)
			activeModule.mouseExit(worldCoord);
	}

	public void mouseMove(int screenX, int screenY) {
		Point2d worldCoord = renderer.toModelCoordinates(screenX, screenY);

		// Relay the mouse event to the general handlers
		for (IControllerModule module : generalModules) {
			module.mouseMove(worldCoord);
		}

		// Relay the mouse event to the active
		IControllerModule activeModule = getActiveDrawModule();
		if (activeModule != null)
			activeModule.mouseMove(worldCoord);
	}

	public void updateView() {
		// call the eventRelay method here to update the view..
		eventRelay.updateView();
	}

	public IControllerModule getActiveDrawModule() {
		return activeDrawModule;
	}

	public void setActiveDrawModule(IControllerModule activeDrawModule) {
		clearPhantoms();
		if (activeDrawModule == null)
			activeDrawModule = this.fallbackModule;
		this.activeDrawModule = activeDrawModule;
		for (int i = 0; i < changeModeListeners.size(); i++)
			changeModeListeners.get(i).modeChanged(this.activeDrawModule);
	}

	// OK
	public IAtom getClosestAtom(Point2d worldCoord) {
		IAtom closestAtom = null;
		double closestDistanceSQ = Double.MAX_VALUE;

		for (IAtomContainer atomContainer : ChemModelManipulator
				.getAllAtomContainers(chemModel)) {

			for (IAtom atom : atomContainer.atoms()) {
				if (atom.getPoint2d() != null) {
					double distanceSQ = atom.getPoint2d().distanceSquared(
							worldCoord);
					if (distanceSQ < closestDistanceSQ) {
						closestAtom = atom;
						closestDistanceSQ = distanceSQ;
					}
				}
			}
		}

		return closestAtom;
	}

	// OK
	public IBond getClosestBond(Point2d worldCoord) {
		IBond closestBond = null;
		double closestDistanceSQ = Double.MAX_VALUE;

		for (IAtomContainer atomContainer : ChemModelManipulator
				.getAllAtomContainers(chemModel)) {

			for (IBond bond : atomContainer.bonds()) {
				boolean hasCenter = true;
				for (IAtom atom : bond.atoms())
					hasCenter = hasCenter && (atom.getPoint2d() != null);
				if (hasCenter) {
					double distanceSQ = bond.get2DCenter().distanceSquared(
							worldCoord);
					if (distanceSQ < closestDistanceSQ) {
						closestBond = bond;
						closestDistanceSQ = distanceSQ;
					}
				}
			}
		}
		return closestBond;
	}

	// OK
	public AtomBondSet removeAtomWithoutUndo(IAtom atom) {

		AtomBondSet undoRedoSet = new AtomBondSet();
		if(rGroupHandler!=null && !rGroupHandler.checkRGroupOkayForDelete(atom, this))
			return undoRedoSet;

		undoRedoSet.add(atom);
		Iterator<IBond> connbonds = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, atom)
				.getConnectedBondsList(atom).iterator();
		while (connbonds.hasNext()) {
			IBond connBond = connbonds.next();
			undoRedoSet.add(connBond);
		}
		ChemModelManipulator.removeAtomAndConnectedElectronContainers(
				chemModel, atom);
		for (IBond bond : undoRedoSet.bonds()) {
			updateAtom(bond.getOther(atom));
		}
		structureChanged();
		adjustRgroup();
		return undoRedoSet;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addAtom(java.lang.String,
	 * javax.vecmath.Point2d)
	 */
	public IAtom addAtom(String atomType, Point2d worldCoord,
			boolean makePseudoAtom) {
		return addAtom(atomType, 0, worldCoord, makePseudoAtom);
	}

	//OK TODO this could do with less partitioning
	public AtomBondSet removeAtom(IAtom atom) {
		AtomBondSet undoRedoSet = removeAtomWithoutUndo(atom);
        removeEmptyContainers(chemModel);
	    if(getUndoRedoFactory()!=null && getUndoRedoHandler()!=null){
		    IUndoRedoable undoredo = getUndoRedoFactory().getRemoveAtomsAndBondsEdit(getIChemModel(), undoRedoSet, "Remove Atom",this);
		    getUndoRedoHandler().postEdit(undoredo);
	    }
		return undoRedoSet;
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addAtom(java.lang.String,
	 * int, javax.vecmath.Point2d)
	 */
	public IAtom addAtom(String atomType, int isotopeNumber,
			Point2d worldCoord, boolean makePseudoAtom) {
		AtomBondSet undoRedoSet = new AtomBondSet();
		undoRedoSet.add(addAtomWithoutUndo(atomType, isotopeNumber,
												 worldCoord, makePseudoAtom));
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getAddAtomsAndBondsEdit(chemModel, undoRedoSet,
							null, "Add Atom", this);
			getUndoRedoHandler().postEdit(undoredo);
		}
		return undoRedoSet.getSingleAtom();
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addAtomWithoutUndo(java
	 * .lang.String, javax.vecmath.Point2d)
	 */
	public IAtom addAtomWithoutUndo(String atomType, Point2d worldCoord,
			boolean makePseudoAtom) {
		return addAtomWithoutUndo(atomType, 0, worldCoord, makePseudoAtom);
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addAtomWithoutUndo(java
	 * .lang.String, int, javax.vecmath.Point2d)
	 */
	public IAtom addAtomWithoutUndo(String atomType, int isotopeNumber,
			Point2d worldCoord, boolean makePseudoAtom) {
		IAtom newAtom;
		if (makePseudoAtom) {
			newAtom = makePseudoAtom(atomType, worldCoord);
		} else {
			newAtom = chemModel.getBuilder().newInstance(IAtom.class,atomType, worldCoord);
		}
		if (isotopeNumber != 0)
			newAtom.setMassNumber(isotopeNumber);
		// FIXME : there should be an initial hierarchy?
		IAtomContainerSet molSet = chemModel.getMoleculeSet();
		if (molSet == null) {
			molSet = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
			IAtomContainer ac = chemModel.getBuilder().newInstance(IAtomContainer.class);
			ac.addAtom(newAtom);
			molSet.addAtomContainer(ac);
			chemModel.setMoleculeSet(molSet);
		}
		IAtomContainer newAtomContainer = chemModel.getBuilder().newInstance(IAtomContainer.class);
		if (chemModel.getMoleculeSet().getAtomContainer(0).getAtomCount() == 0)
			newAtomContainer = (IAtomContainer) chemModel.getMoleculeSet()
					.getAtomContainer(0);
		else
			molSet.addAtomContainer(newAtomContainer);
		newAtomContainer.addAtom(newAtom);
		updateAtom(newAtom);
		JChemPaintRendererModel model = this.getRenderer().getRenderer2DModel();
		double nudgeDistance = model.getHighlightDistance() / model.getScale();
		if (getClosestAtom(newAtom) != null)
			newAtom.getPoint2d().x += nudgeDistance;
		structureChanged();
		return newAtom;
	}

	// OK
	public IAtom addAtom(String atomType, IAtom atom, boolean makePseudoAtom) {
		AtomBondSet undoRedoSet = new AtomBondSet();
		undoRedoSet.add(addAtomWithoutUndo(atomType, atom,
												 makePseudoAtom));
		IAtomContainer atomContainer = ChemModelManipulator
				.getRelevantAtomContainer(getIChemModel(), undoRedoSet.getSingleAtom());
		IBond newBond = atomContainer.getBond(atom, undoRedoSet.getSingleAtom());
		undoRedoSet.add(newBond);
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getAddAtomsAndBondsEdit(chemModel, undoRedoSet,
							null, "Add Atom", this);
			getUndoRedoHandler().postEdit(undoredo);
		}
		return undoRedoSet.getSingleAtom();
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addAtomWithoutUndo(java
	 * .lang.String, org.openscience.cdk.interfaces.IAtom)
	 */
	public IAtom addAtomWithoutUndo(String atomType, IAtom atom,
			boolean makePseudoAtom) {
		return addAtomWithoutUndo(atomType, atom, Display.Solid,
				makePseudoAtom);
	}

	public IAtom addAtomWithoutUndo(String atomType, IAtom atom,
									IBond.Display display, Order order, boolean makePseudoAtom) {
		return addAtomWithoutUndo(atomType, atom, display, order, makePseudoAtom, false);
	}

	private static boolean isLeft(Point2d a, Point2d b, Point2d c) {
		return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x) > 0;
	}

	private static boolean isLeft(IAtom a, IAtom b, IAtom c) {
		return isLeft(a.getPoint2d(), b.getPoint2d(), c.getPoint2d());
	}

	private static void reflect(Tuple2d p, Tuple2d base, double a, double b) {
		double x = a * (p.x - base.x) + b * (p.y - base.y) + base.x;
		double y = b * (p.x - base.x) - a * (p.y - base.y) + base.y;
		p.x = x;
		p.y = y;
	}

	private void reflect(Point2d p, Tuple2d begP, Tuple2d endP) {
		double dx = endP.x - begP.x;
		double dy = endP.y - begP.y;

		double a = (dx * dx - dy * dy) / (dx * dx + dy * dy);
		double b = 2 * dx * dy / (dx * dx + dy * dy);

		reflect(p, begP, a, b);
	}


	public IAtom addAtomWithoutUndo(String atomType, IAtom atom,
									IBond.Display display, Order order, boolean makePseudoAtom,
									boolean phantom) {
		return addAtomWithoutUndo(atomType, atom, display, order, makePseudoAtom, phantom, altMode);
	}

	/*
	 * @param cyclicMode sprouts the bond in cyclic mode (as opposed to linear)
	 */
	public IAtom addAtomWithoutUndo(String atomType, IAtom atom,
			                        IBond.Display display, Order order, boolean makePseudoAtom,
									boolean phantom, boolean cyclicMode) {

		IAtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom);

		IAtom newAtom;
		if (makePseudoAtom) {
			newAtom = makePseudoAtom(atomType, null);
		} else {
			newAtom = chemModel.getBuilder().newInstance(IAtom.class,atomType);
		}
		IBond newBond =
		        chemModel.getBuilder().newInstance(IBond.class,atom, newAtom, order);
		newBond.setDisplay(display);

		if (atomCon == null) {
			atomCon = chemModel.getBuilder().newInstance(IAtomContainer.class);
			IAtomContainerSet moleculeSet = chemModel.getMoleculeSet();
			if (moleculeSet == null) {
				moleculeSet = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
				chemModel.setMoleculeSet(moleculeSet);
			}
			moleculeSet.addAtomContainer(atomCon);
		}

		// The AtomPlacer generates coordinates for the new atom
		AtomPlacer atomPlacer = new AtomPlacer();

		// need a temporary container for AtomPlacer to work correctly
		IAtomContainer tmp = atomCon.getBuilder().newAtomContainer();
		tmp.add(atomCon);
		tmp.addAtom(newAtom);
		tmp.newBond(atom, newAtom);

		atomPlacer.setMolecule(tmp);
		double bondLength;
		if (atomCon.getBondCount() >= 1) {
			bondLength = Renderer.calculateBondLength(atomCon);
		} else {
			bondLength = Renderer.calculateBondLength(chemModel.getMoleculeSet());
		}

		// determine the atoms which define where the
		// new atom should not be placed
		List<IAtom> connectedAtoms = atomCon.getConnectedAtomsList(atom);
		if (connectedAtoms.isEmpty()) {
			Point2d newAtomPoint = new Point2d(atom.getPoint2d());
			double angle = Math.toRadians(-30);
			Vector2d vec1 = new Vector2d(Math.cos(angle), Math.sin(angle));
			vec1.scale(bondLength);
			newAtomPoint.add(vec1);
			newAtom.setPoint2d(newAtomPoint);
		} else if (connectedAtoms.size() == 1) {
			IAtomContainer ac = atomCon.getBuilder().newInstance(IAtomContainer.class);
			ac.addAtom(atom);
			ac.addAtom(newAtom);
			Point2d distanceMeasure = new Point2d(0, 0); // XXX not sure about
			// this?
			IAtom connectedAtom = connectedAtoms.get(0);
			Vector2d v = atomPlacer.getNextBondVector(atom, connectedAtom, distanceMeasure, true);
			v.normalize();
			v.scale(bondLength);
			Point2d p = new Point2d(atom.getPoint2d().x + v.x,
									atom.getPoint2d().y + v.y);
			newAtom.setPoint2d(p);

			// if we place the bond flipped before and it was undo/redone
			// (there is only a single atom connected) then we do the opposite
			boolean flip = false;
			if (atom.getProperty("placeFlipped") != null) {
				flip = !atom.<Boolean>getProperty("placeFlipped");
			}
			// no previous default, first place should be a zig-zag
			else if (flipChainAngle(connectedAtom, atom, newAtom)) {
				flip = true;
			}
			// alt-mode switches from whatever has been decided
			if (cyclicMode)
				flip = !flip;
			if (flip)
				reflect(newAtom.getPoint2d(), atom.getPoint2d(), connectedAtom.getPoint2d());
			if (!phantom) {
				atom.setProperty("placeFlipped", flip);
			}
		} else {
			IAtomContainer placedAtoms = atomCon.getBuilder().newAtomContainer();
			for (IAtom conAtom : connectedAtoms)
				placedAtoms.addAtom(conAtom);
			Point2d center2D = GeometryUtil.get2DCenter(placedAtoms);

			IAtomContainer unplacedAtoms = atomCon.getBuilder()
					.newInstance(IAtomContainer.class);
			unplacedAtoms.addAtom(newAtom);

			atomPlacer.distributePartners(atom, placedAtoms, center2D,
					unplacedAtoms, bondLength);
		}

		if (phantom) {
			phantoms.addAtom(atom);
			phantoms.addAtom(newAtom);
			phantoms.addBond(newBond);
		} else {
			atomCon.addAtom(newAtom);
			atomCon.addBond(newBond);
			updateAtom(newBond.getAtom(0));
			updateAtom(newBond.getAtom(1));
			newAtom = atomCon.getAtom(atomCon.getAtomCount()-1);
		}

		// shift the new atom a bit if it is in range of another atom
		JChemPaintRendererModel model = this.getRenderer().getRenderer2DModel();
		double nudgeDistance = model.getHighlightDistance() / model.getScale();
		if (getClosestAtom(newAtom) != null)
			newAtom.getPoint2d().x += nudgeDistance;

		structureChanged();
		return newAtom;
	}

	private static boolean flipChainAngle(IAtom prevAtom, IAtom atom, IAtom newAtom) {
		IAtom reference = null;
		if (prevAtom.getBondCount() == 2) {
			for (IBond bond : prevAtom.bonds()) {
				IAtom nbor = bond.getOther(prevAtom);
				if (!nbor.equals(atom))
					reference = nbor;
			}
		} else if (prevAtom.getBondCount() > 2) {
			for (IBond bond : prevAtom.bonds()) {
				IAtom nbor = bond.getOther(prevAtom);
				if (!nbor.equals(atom) && nbor.getBondCount() != 1) {
					if (reference != null) {
						reference = null;
						break;
					}
					reference = nbor;
				}
			}

			if (reference == null) {
				// no reference, avoid sprouting directly up/down
				if (Math.abs(atom.getPoint2d().x - newAtom.getPoint2d().x) <= 0.01)
					return true;
			}
		}
		if (reference != null &&
			isLeft(atom, prevAtom, reference) ==
			isLeft(atom, prevAtom, newAtom)) {
			return true; // on same side so flip!
		}
		return false;
	}

	public IAtom addAtomWithoutUndo(String atomType, IAtom atom,
			IBond.Display display, boolean makePseudoAtom) {
		return addAtomWithoutUndo(atomType, atom, display, IBond.Order.SINGLE,
				makePseudoAtom);
	}

	// OK
	public void addNewBond(Point2d worldCoordinate, boolean makePseudoAtom) {
		AtomBondSet undoRedoSet = new AtomBondSet();

		// add the first atom in the new bond
		String atomType = getController2DModel().getDrawElement();
		IAtom atom = addAtomWithoutUndo(atomType, worldCoordinate,
				makePseudoAtom);
		undoRedoSet.add(atom);

		// add the second atom to this
		IAtom newAtom = addAtomWithoutUndo(atomType, atom, makePseudoAtom);
		undoRedoSet.add(newAtom);

		IAtomContainer atomContainer = ChemModelManipulator
				.getRelevantAtomContainer(getIChemModel(), newAtom);

		IBond newBond = atomContainer.getBond(atom, newAtom);
		undoRedoSet.add(newBond);
		updateAtom(newBond.getAtom(0));
		updateAtom(newBond.getAtom(1));

		structureChanged();
		if (undoredofactory != null && undoredohandler != null) {
			IUndoRedoable undoredo = undoredofactory.getAddAtomsAndBondsEdit(
					getIChemModel(), undoRedoSet, null, "Add Bond", this);
			undoredohandler.postEdit(undoredo);
		}
	}

	/**
	 * Alternative input mode allow different actions when alt is held.
	 * @param value activate/deactivate
	 * @return mode was changed or not
	 */
	public boolean setAltInputMode(boolean value) {
		if (altMode != value) {
			altMode = value;
			return true;
		} else {
			return false;
		}
	}


	public void addAtom(IAtom atom, int atno, boolean cyclicMode) {
		addAtom(atom, atno, Order.SINGLE, cyclicMode);
	}

	public void addAtom(IAtom atom, int atno, IBond.Order order, boolean cyclicMode) {
		addAtom(atom, atno, order, Display.Solid, cyclicMode);
	}

	/**
	 * Sprout a single atom
	 *
	 * @param atom the atom
	 * @param atno the atomic number
	 * @param cyclicMode the cyclicMode mode, false = zig/zag chain.
	 */
	public void addAtom(IAtom atom,
						int atno,
						IBond.Order order,
						IBond.Display stereo,
						boolean cyclicMode) {
		atom.removeProperty("placeFlipped");
		IAtom newAtom = addAtomWithoutUndo(Elements.ofNumber(atno).symbol(),
										   atom,
										   stereo,
										   order,
										   false,
										   false,
										   cyclicMode);
		renderer.getRenderer2DModel().setHighlightedAtom(newAtom);
		if (getUndoRedoHandler() != null) {
			IAtomContainer container = ChemModelManipulator
					.getRelevantAtomContainer(getIChemModel(), newAtom);
			AtomBondSet atomBondSet = new AtomBondSet();
			atomBondSet.add(newAtom);
			atomBondSet.add(container.getConnectedBondsList(newAtom).get(0));
			IUndoRedoable undoredo = new AddAtomsAndBondsEdit(this.getIChemModel(),
															  atomBondSet,
															  container,
															  "Add Bond",
															  this);
			getUndoRedoHandler().postEdit(undoredo);
		}
	}

	public void addAcetyl(IAtom atom) {
		AtomBondSet undoRedoSet = new AtomBondSet();
		int freeValence = atom.getImplicitHydrogenCount();
		IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(getChemModel(), atom);
		if (freeValence == 1) {
			clearPhantoms();
			IAtom c1 = addAtomWithoutUndo("C", atom, Display.Solid, Order.SINGLE, false, false, false);
			IAtom c2 = addAtomWithoutUndo("C", c1, Display.Solid, Order.SINGLE, false, false, false);
			IAtom o = addAtomWithoutUndo("O", c1, Display.Solid, Order.DOUBLE, false, false, false);
			undoRedoSet.add(c1);
			undoRedoSet.add(c2);
			undoRedoSet.add(o);
			for (IBond bond : container.getConnectedBondsList(c1))
				undoRedoSet.add(bond);
			getRenderer().getRenderer2DModel().setHighlightedAtom(c2);
		}
		else if (freeValence == 2) {
			clearPhantoms();
			IAtom o = addAtomWithoutUndo("O", atom, Display.Solid, Order.DOUBLE, false, false, false);
			undoRedoSet.add(o);
			for (IBond bond : container.getConnectedBondsList(o))
				undoRedoSet.add(bond);
		}
		else if (freeValence > 2) {
			clearPhantoms();
			IAtom c = addAtomWithoutUndo("C", atom, Display.Solid, Order.SINGLE, false, false, false);
			IAtom o = addAtomWithoutUndo("O", atom, Display.Solid, Order.DOUBLE, false, false, false);
			undoRedoSet.add(c);
			undoRedoSet.add(o);
			for (IBond bond : container.getConnectedBondsList(c))
				undoRedoSet.add(bond);
			for (IBond bond : container.getConnectedBondsList(o))
				undoRedoSet.add(bond);
			getRenderer().getRenderer2DModel().setHighlightedAtom(c);

		}

		if (getUndoRedoHandler() != null) {
			getUndoRedoHandler().postEdit(new AddAtomsAndBondsEdit(getIChemModel(),
																   undoRedoSet,
																   null,
																   "Acetyl",
																   this));
		}
	}

	public void addDimethyl(IAtom atom, IBond.Display display) {
		AtomBondSet undoRedoSet = new AtomBondSet();
		int freeValence = atom.getImplicitHydrogenCount();
		IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(getChemModel(), atom);
		if (freeValence == 1) {
			if (display == Display.Solid) {
				clearPhantoms();
				IAtom c = addAtomWithoutUndo("C", atom, Display.Solid, Order.SINGLE, false, false, false);
				IAtom me1 = addAtomWithoutUndo("C", c, Display.Solid, Order.SINGLE, false, false, false);
				IAtom me2 = addAtomWithoutUndo("C", c, Display.Solid, Order.SINGLE, false, false, false);
				undoRedoSet.add(c);
				undoRedoSet.add(me1);
				undoRedoSet.add(me2);
				for (IBond bond : container.getConnectedBondsList(c))
					undoRedoSet.add(bond);
				getRenderer().getRenderer2DModel().setHighlightedAtom(me1);
			} else {
				addAtom(atom, IAtom.C, Order.SINGLE, display, false);
			}
		}
		else if (freeValence >= 2) {
			clearPhantoms();
			IAtom me1 = addAtomWithoutUndo("C", atom, Display.Solid, Order.SINGLE, false, false, false);
			IAtom me2 = addAtomWithoutUndo("C", atom, display, Order.SINGLE, false, false, false);
			undoRedoSet.add(me1);
			undoRedoSet.add(me2);
			for (IBond bond : container.getConnectedBondsList(me1))
				undoRedoSet.add(bond);
			for (IBond bond : container.getConnectedBondsList(me2))
				undoRedoSet.add(bond);
			getRenderer().getRenderer2DModel().setHighlightedAtom(me1);
		}

		if (getUndoRedoHandler() != null) {
			getUndoRedoHandler().postEdit(new AddAtomsAndBondsEdit(getIChemModel(),
																   undoRedoSet,
																   null,
																   "Dimethyl",
																   this));
		}
	}

	/**
	 * Select all atoms which are in the same container as the provided root
     * atom. In altMode the fragment is flood-filled based on weather the
     * provided atom is acyclic/acyclic.
     *
	 * @param root an atom to select from
	 */
	public void selectFragment(IAtom root) {
		LogicalSelection selection = new LogicalSelection(LogicalSelection.Type.ALL);
        IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(getChemModel(), root);
        if (container != null) {
            if (altMode) {
                // flood fill atoms/bonds which are cyclic/acyclic
                Cycles.markRingAtomsAndBonds(container);
                Set<IChemObject> set = new HashSet<>();
                Deque<IAtom> queue = new ArrayDeque<>();
                queue.add(root);
                while (!queue.isEmpty()) {
                    IAtom atom = queue.poll();
                    set.add(atom);
                    for (IBond bond : atom.bonds()) {
                        if (bond.isInRing() == root.isInRing()) {
                            set.add(bond);
                            IAtom nbor = bond.getOther(atom);
                            if (!set.contains(nbor))
                                queue.add(nbor);
                        }
                    }
                }
                for (IChemObject obj : set)
                    selection.select(obj);
            } else {
                selection.select(container);
            }
        }
        select(selection);
	}

	/**
	 * Select all bonds which are in the same container as the provided bond.
     * In altMode the fragment is flood-filled based on weather the
     * provided bond is acyclic/acyclic.
     *
	 * @param root the bond to select from
	 */
	public void selectFragment(IBond root) {
        LogicalSelection selection = new LogicalSelection(LogicalSelection.Type.ALL);
        IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(getChemModel(), root);
        if (container != null) {
            if (altMode) {
                // flood fill atoms/bonds which are cyclic/acyclic
                Cycles.markRingAtomsAndBonds(container);
                Set<IChemObject> set = new HashSet<>();
                Deque<IAtom> queue = new ArrayDeque<>();
                queue.add(root.getBegin());
                queue.add(root.getEnd());
                while (!queue.isEmpty()) {
                    IAtom atom = queue.poll();
                    set.add(atom);
                    for (IBond bond : atom.bonds()) {
                        if (bond.isInRing() == root.isInRing()) {
                            set.add(bond);
                            IAtom nbor = bond.getOther(atom);
                            if (!set.contains(nbor))
                                queue.add(nbor);
                        }
                    }
                }
                for (IChemObject obj : set)
                    selection.select(obj);
            } else {
                selection.select(container);
            }
        }
        select(selection);
	}

	/**
     * Class tracks when a bond order is increased and when.
     */
    private static final class CycledBond {
        IBond bond;
        long time;

        private CycledBond(IBond bond) {
            this.bond = bond;
            this.time = System.nanoTime();
        }

        boolean expired(IBond bond) {
            long deltaT = System.nanoTime() - time;
            return this.bond == null || !bond.equals(this.bond) || TimeUnit.NANOSECONDS.toMillis(deltaT) > 2500;
        }
    }

	private void flipBonds(List<IBond> path, boolean tautomer) {
		Map<IBond,Order[]> changedBonds = new LinkedHashMap<>();
		Map<IAtom,Integer[]> changedAtoms = new LinkedHashMap<>();

		for (IBond b : path) {
			if (b.getOrder() == Order.SINGLE)
				changedBonds.put(b, new Order[]{Order.DOUBLE, Order.SINGLE});
			else if (b.getOrder() == Order.DOUBLE)
				changedBonds.put(b, new Order[]{Order.SINGLE, Order.DOUBLE});
		}

		String description = tautomer
							 ? "Tautomer 1," + (path.size() + 1) + "-shift"
							 : "Alternative Kekule From";
		AdjustBondOrdersEdit adjustBondOrders = new AdjustBondOrdersEdit(changedBonds,
															   Collections.emptyMap(),
															   "Change Bond Orders",
															   this);

		CompoundEdit edit = new CompoundEdit(description);
		if (tautomer) {
			// tautomers need their hydrogen count changed, note update atoms
			// can get in the way here but seems to work okay on O,S,N,P
			int last = path.size()-1;
			IBond begBond = path.get(0);
			IBond endBond = path.get(last);
			IAtom begAtom = begBond.getOther(begBond.getConnectedAtom(path.get(1)));
			IAtom endAtom = endBond.getOther(endBond.getConnectedAtom(path.get(last - 1)));
			if (begBond.getOrder() == Order.DOUBLE) {
				changedAtoms.put(begAtom, new Integer[]{begAtom.getImplicitHydrogenCount()+1,
														begAtom.getImplicitHydrogenCount()});
				changedAtoms.put(endAtom, new Integer[]{endAtom.getImplicitHydrogenCount()-1,
														endAtom.getImplicitHydrogenCount()});
			} else {
				changedAtoms.put(begAtom, new Integer[]{begAtom.getImplicitHydrogenCount()-1,
														begAtom.getImplicitHydrogenCount()});
				changedAtoms.put(endAtom, new Integer[]{endAtom.getImplicitHydrogenCount()+1,
														endAtom.getImplicitHydrogenCount()});
			}
			// we need to set the hydrogen counts forward/backwards because
			// otherwise the automatic CDK atom typing messes things up
			edit.add(new ChangeHydrogenCountEdit(changedAtoms,
												 "Change Hydrogen Counts"));
			edit.add(adjustBondOrders);
			edit.add(new ChangeHydrogenCountEdit(changedAtoms,
												 "Change Hydrogen Counts"));
		} else {
			edit.add(adjustBondOrders);
		}

		edit.redo(); // fire the changes

		if (undoredofactory != null && undoredohandler != null) {
			undoredohandler.postEdit(edit);
		}
	}

	/**
	 * Cycle to an alternative Kekulé form.
	 *
	 * @param bond a bond in the alternating path
	 * @return the kekulé form was alternated
	 */
	private boolean cycleKekuleForm(IBond bond) {

		List<IBond> path = new ArrayList<>(6);
		if (ConjugationTools.findAlternating(path, bond)) {
			flipBonds(path, false);
			// atom update shouldn't be needed since we shifted the bonds
			// but the hybridisation didn't change
			updateView();
			return true;
		}

		if (ConjugationTools.findTautomerShift(path, bond)) {
			flipBonds(path, true);
			updateView();
			return true;
		}

		return false;
	}

	CycledBond cycledBond = new CycledBond(null);

	// OK
	public void cycleBondValence(IBond bond) {
		cycleBondValence(bond, IBond.Order.SINGLE);
	}

	public void cycleBondValence(IBond bond, IBond.Order order) {

		IBond.Order[] orders = new IBond.Order[2];
		IBond.Display[] stereos = new IBond.Display[2];
		orders[1] = bond.getOrder();
		stereos[1] = bond.getDisplay();

		if (altMode) {
			cycleKekuleForm(bond);
			return;
		}

		// special case : reset stereo bonds
		if (bond.getDisplay() != Display.Solid ) {
			bond.setDisplay(Display.Solid);
			bond.setOrder(order);
		} else {
			if (order == IBond.Order.SINGLE) {
				switch (bond.getOrder()) {
                    case SINGLE:
                        bond.setOrder(Order.DOUBLE);
                        cycledBond = new CycledBond(bond);
                        break;
                    case DOUBLE:
                        if (cycledBond.expired(bond)) {
                            bond.setOrder(Order.SINGLE);
                        } else {
                            bond.setOrder(Order.TRIPLE);
                        }
                        break;
                    case TRIPLE:
                        bond.setOrder(Order.SINGLE);
                        break;
                }
			} else {
				if (bond.getOrder() != order) {
					bond.setOrder(order);
				} else {
					bond.setOrder(IBond.Order.SINGLE);
				}
			}
		}

		orders[0] = bond.getOrder();
		stereos[0] = bond.getDisplay();
		Map<IBond, IBond.Order[]> changedBonds = new HashMap<IBond, IBond.Order[]>();
		Map<IBond, IBond.Display[]> changedBondsStereo = new HashMap<IBond, IBond.Display[]>();
		changedBonds.put(bond, orders);
		changedBondsStereo.put(bond, stereos);
		// set hybridization from bond order
		bond.getAtom(0).setHybridization(null);
		bond.getAtom(1).setHybridization(null);
		updateAtom(bond.getAtom(0));
		updateAtom(bond.getAtom(1));
		structureChanged();
		if (undoredofactory != null && undoredohandler != null) {
			IUndoRedoable undoredo = undoredofactory
					.getAdjustBondOrdersEdit(changedBonds,
                                             changedBondsStereo,
							"Adjust Bond Order", this);
			undoredohandler.postEdit(undoredo);
		}
	}

	// OK
	public IBond makeNewStereoBond(IAtom atom, Direction desiredDirection) {
		String atomType = getController2DModel().getDrawElement();
		IAtom newAtom = addAtomWithoutUndo(atomType, atom, controllerModel
				.getDrawPseudoAtom());
		AtomBondSet undoRedoSet = new AtomBondSet();

		// XXX these calls would not be necessary if addAtom returned a bond
		IAtomContainer atomContainer = ChemModelManipulator
				.getRelevantAtomContainer(getIChemModel(), newAtom);
		IBond newBond = atomContainer.getBond(atom, newAtom);

		if (desiredDirection == Direction.UP) {
			newBond.setDisplay(IBond.Display.Up);
		} else if (desiredDirection == Direction.DOWN) {
			newBond.setDisplay(IBond.Display.Down);
		} else if (desiredDirection == Direction.UNDEFINED) {
			newBond.setDisplay(IBond.Display.Wavy);
		} else {
			newBond.setDisplay(IBond.Display.Crossed);
		}
		undoRedoSet.add(newAtom);
		undoRedoSet.add(newBond);
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getAddAtomsAndBondsEdit(getIChemModel(),
							undoRedoSet, null, "Add Stereo Bond", this);
			getUndoRedoHandler().postEdit(undoredo);
		}
		return newBond;
	}

	// OK
	public void moveToWithoutUndo(IAtom atom, Point2d worldCoords) {
		if (atom != null) {
			Point2d atomCoord = new Point2d(worldCoords);
			atom.setPoint2d(atomCoord);
		}
		coordinatesChanged();
	}

	// OK
	public void moveTo(IAtom atom, Point2d worldCoords) {
		if (atom != null) {
			if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
				IAtomContainer undoRedoSet = chemModel.getBuilder()
						.newInstance(IAtomContainer.class);
				undoRedoSet.addAtom(atom);
				Vector2d end = new Vector2d();
				end.sub(worldCoords, atom.getPoint2d());
				IUndoRedoable undoredo = getUndoRedoFactory().getMoveAtomEdit(
						undoRedoSet, end, "Move atom");
				getUndoRedoHandler().postEdit(undoredo);
			}
			moveToWithoutUndo(atom, worldCoords);
		}
	}

	// OK
	public void moveToWithoutUndo(IBond bond, Point2d point) {
		if (bond != null) {
			Point2d center = bond.get2DCenter();
			for (IAtom atom : bond.atoms()) {
				Vector2d offset = new Vector2d();
				offset.sub(atom.getPoint2d(), center);
				Point2d result = new Point2d();
				result.add(point, offset);

				atom.setPoint2d(result);
			}
		}
		coordinatesChanged();
	}

	// OK
	public void moveTo(IBond bond, Point2d point) {
		if (bond != null) {
			if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
				IAtomContainer undoRedoSet = chemModel.getBuilder()
						.newInstance(IAtomContainer.class);
				undoRedoSet.addAtom(bond.getAtom(0));
				undoRedoSet.addAtom(bond.getAtom(1));
				Vector2d end = new Vector2d();
				end.sub(point, bond.getAtom(0).getPoint2d());
				IUndoRedoable undoredo = getUndoRedoFactory().getMoveAtomEdit(
						undoRedoSet, end, "Move atom");
				getUndoRedoHandler().postEdit(undoredo);
			}
			moveToWithoutUndo(bond, point);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addBond(org.openscience
	 * .cdk.interfaces.IAtom, org.openscience.cdk.interfaces.IAtom, int)
	 */
	public IBond addBond(IAtom fromAtom, IAtom toAtom, IBond.Display display,
			IBond.Order order) {
		IBond newBond = chemModel.getBuilder().newInstance(IBond.class,fromAtom, toAtom, order);
        newBond.setDisplay(display);
		IAtomContainer fromContainer = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, fromAtom);
		IAtomContainer toContainer = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, toAtom);

		// we need to check if this merges two atom containers or not
		if (fromContainer != toContainer) {
			fromContainer.add(toContainer);
			chemModel.getMoleculeSet().removeAtomContainer(toContainer);
		}
		fromContainer.addBond(newBond);
		updateAtom(newBond.getAtom(0));
		updateAtom(newBond.getAtom(1));
		structureChanged();
		return newBond;
	}

	public IBond addBond(IAtom fromAtom, IAtom toAtom, IBond.Display stereo) {
		return addBond(fromAtom, toAtom, stereo, IBond.Order.SINGLE);
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addBond(org.openscience
	 * .cdk.interfaces.IAtom, org.openscience.cdk.interfaces.IAtom)
	 */
	public IBond addBond(IAtom fromAtom, IAtom toAtom) {
		return addBond(fromAtom, toAtom, Display.Solid, IBond.Order.SINGLE);
	}

	// OK
	public void setCharge(IAtom atom, int charge) {
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory().getChangeChargeEdit(
					atom, atom.getFormalCharge(), charge,
					"Change charge to " + charge, this);
			getUndoRedoHandler().postEdit(undoredo);
		}
		atom.setFormalCharge(charge);
		updateAtom(atom);
		structurePropertiesChanged();
	}

	// OK
	public void setMassNumber(IAtom atom, int massNumber) {
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory().getChangeIsotopeEdit(
					atom, atom.getMassNumber(), massNumber,
					"Change Atomic Mass to " + massNumber);
			getUndoRedoHandler().postEdit(undoredo);
		}
		atom.setMassNumber(massNumber);
		structurePropertiesChanged();
	}

	public void changeBond(IBond bond, Order order, Display display) {
		changeBonds(Collections.singletonList(bond), order, display);
	}

    private static boolean atWideEndOfAnotherWedge(IAtom atom) {
        for (IBond bond : atom.bonds()) {
            switch (bond.getDisplay()) {
                case Bold:
                case Hash:
                    return true;
                case WedgeBegin:
                case WedgedHashBegin:
                    return bond.getEnd().equals(atom);
                case WedgeEnd:
                case WedgedHashEnd:
                    return bond.getBegin().equals(atom);
            }
        }
        return false;
    }

	public void changeBonds(Collection<IBond> bonds, Order order, Display display) {

        Map<IBond, Order[]> orderChanges = new LinkedHashMap<>();
		Map<IBond, Stereo[]> stereoChanges = new LinkedHashMap<>();
		Map<IBond, Display[]> displayChanges = new LinkedHashMap<>();

		for (IBond bond : bonds) {

            if (bond.getOrder() != order)
                orderChanges.put(bond, new Order[]{order, bond.getOrder()});

			// flip wedges: if the bond is already wedged, and it is wedged the same
			// wedge direction, the intention is to flip the ordering
			// C < O (WedgeBegin) => C > O (WedgeEnd)
			if (display == bond.getDisplay() && display.flip() != display) {
				displayChanges.put(bond, new Display[]{display.flip(), bond.getDisplay()});
			} else if (display != bond.getDisplay()) {

                if (display != Display.Solid && bond.getDisplay() == Display.Solid) {
                    IAtom begin = bond.getBegin();
                    IAtom end = bond.getEnd();
                    // end has more bonds => more likely tetrahedral, or
                    // we already have a wide of a wedge next to us
                    if (end.getBondCount() > begin.getBondCount() ||
                        end.getBondCount() == begin.getBondCount() && atWideEndOfAnotherWedge(begin))
                        display = display.flip();
                }

                displayChanges.put(bond, new Display[]{display, bond.getDisplay()});
			}
		}

		AdjustBondOrdersEdit edit = new AdjustBondOrdersEdit(orderChanges,
															 stereoChanges,
                                                             displayChanges,
															 "Set bond order/stereo",
															 this);
		edit.redo(); // make the changes
		structureChanged();

		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			getUndoRedoHandler().postEdit(edit);
		}
	}


	public void setSymbol(IAtom atom, String symbol) {
		setSymbol(atom, symbol, null);
	}

	/**
	 * Change the Atom Symbol to the given element symbol, setting also its massNumber.
	 * If an exception happens, the massNumber is set to null.
	 * @see org.openscience.jchempaint.controller.IAtomBondEdits#setSymbol(org.openscience.cdk.interfaces.IAtom, java.lang.String)
	 */
	public void setSymbol(IAtom atom, String symbol, Integer massNumber) {
		CompoundEdit edit = new CompoundEdit("Change atom to " + symbol);

		boolean newIsPseudo = Elements.ofString(symbol) == Elements.Unknown;
		boolean swap = atom instanceof IPseudoAtom || newIsPseudo;

		if (swap) {
			IAtom newAtom;
			if (newIsPseudo) {
				newAtom = makePseudoAtom(symbol, null);
			} else {
				newAtom = atom.getBuilder().newInstance(IAtom.class, symbol);
			}
			newAtom.setPoint2d(atom.getPoint2d());
			edit.add(new ReplaceAtomEdit(this.getIChemModel(),
										 atom,
										 newAtom,
										 "Change atom to " + symbol));
		} else {
			edit.add(new ChangeAtomSymbolEdit(atom, atom.getSymbol(), symbol, this));
		}
		edit.add(new ChangeIsotopeEdit(atom,
									   atom.getMassNumber(), massNumber,
									   "Change Mass Number to " + massNumber));

		edit.redo();
		structurePropertiesChanged();
		if (getUndoRedoHandler() != null) {
			getUndoRedoHandler().postEdit(edit);
		}
	}

	// OK
	public void updateImplicitHydrogenCounts() {
		Map<IAtom, Integer[]> atomHydrogenCountsMap = new HashMap<IAtom, Integer[]>();
		for (IAtomContainer container : ChemModelManipulator
				.getAllAtomContainers(chemModel)) {
			for (IAtom atom : container.atoms()) {
				if (!(atom instanceof IPseudoAtom)) {
					try {
						IAtomType type = matcher.findMatchingAtomType(container, atom);
						if (type != null
                                && !type.getAtomTypeName().equals("X")
								&& type.getFormalNeighbourCount() != null) {

                            int connectedAtomCount = container
                                    .getConnectedAtomsCount(atom);
                            atomHydrogenCountsMap.put(atom, new Integer[]{
                                    type.getFormalNeighbourCount()
                                            - connectedAtomCount,
                                    atom.getImplicitHydrogenCount()});
                            atom.setImplicitHydrogenCount(type.getFormalNeighbourCount()
                                                                  - connectedAtomCount);

						} else {
                            atom.setImplicitHydrogenCount(0);
                        }
					} catch (CDKException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getChangeHydrogenCountEdit(atomHydrogenCountsMap,
							"Update implicit hydrogen count");
			getUndoRedoHandler().postEdit(undoredo);
		}
		structurePropertiesChanged();
	}

	public void zap() {
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory().getClearAllEdit(
					chemModel, chemModel.getMoleculeSet(),
					chemModel.getReactionSet(), "Clear Panel");
			getUndoRedoHandler().postEdit(undoredo);
		}
		if (chemModel.getMoleculeSet() != null) {
			IAtomContainerSet molSet = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
			IAtomContainer ac = chemModel.getBuilder().newInstance(IAtomContainer.class);
			molSet.addAtomContainer(ac);
			chemModel.setMoleculeSet(molSet);

		}
		if (chemModel.getReactionSet() != null)
			chemModel.setReactionSet(chemModel.getBuilder().newInstance(IReactionSet.class));
		structureChanged();
	}

	// OK
	public void makeBondStereo(IBond bond, Direction desiredDirection) {
		IBond.Display stereo = bond.getDisplay();
		boolean isUp = isUp(stereo);
		boolean isDown = isDown(stereo);
		boolean isUndefined = isUndefined(stereo);
		if (isUp && desiredDirection == Direction.UP) {
			flipDirection(bond, stereo);
		} else if (isDown && desiredDirection == Direction.DOWN) {
			flipDirection(bond, stereo);
		} else if (isUndefined && desiredDirection == Direction.UNDEFINED) {
			flipDirection(bond, stereo);
		} else if (desiredDirection == Direction.EZ_UNDEFINED) {
			bond.setDisplay(Display.Crossed);
		} else if (desiredDirection == Direction.UNDEFINED) {
			bond.setDisplay(Display.Wavy);
		} else if (desiredDirection == Direction.UP) {
			bond.setDisplay(Display.Up);
		} else if (desiredDirection == Direction.DOWN) {
			bond.setDisplay(Display.Down);
		}
		IBond.Display[] displays = new IBond.Display[2];
		displays[1] = stereo;
		displays[0] = bond.getDisplay();
		Map<IBond, IBond.Order[]> changedBonds = new HashMap<IBond, IBond.Order[]>();
		Map<IBond, IBond.Display[]> changedBondsStereo = new HashMap<IBond, IBond.Display[]>();
		changedBondsStereo.put(bond, displays);
		updateAtom(bond.getAtom(0));
		updateAtom(bond.getAtom(1));
		structureChanged();
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getAdjustBondOrdersEdit(changedBonds, changedBondsStereo,
							"Adjust Bond Stereo", this);
			getUndoRedoHandler().postEdit(undoredo);
		}
	}

	/**
	 * Change the stereo bond from start->end to start<-end.
	 *
	 * @param bond
	 *            the bond to change
	 * @param stereo
	 *            the current stereo of that bond
	 */
	private void flipDirection(IBond bond, IBond.Display stereo) {
        bond.setDisplay(stereo.flip());
	}

	private boolean isUp(IBond.Display stereo) {
		return stereo == Display.WedgeBegin ||
               stereo == Display.WedgeEnd ||
               stereo == Display.HollowWedgeBegin ||
               stereo == Display.HollowWedgeEnd;
	}

	private boolean isDown(IBond.Display stereo) {
		return stereo == Display.WedgedHashBegin
				|| stereo == Display.WedgedHashEnd;
	}

	private boolean isUndefined(IBond.Display stereo) {
		return stereo == Display.Wavy;
	}

	public static void avoidOverlap(IChemModel chemModel){
        //we avoid overlaps
        //first we shift down the reactions
        Rectangle2D usedReactionbounds=null;
        if(chemModel.getReactionSet()!=null){
            for(IReaction reaction : chemModel.getReactionSet().reactions()){
                // now move it so that they don't overlap
                Rectangle2D reactionbounds = BoundsCalculator.calculateBounds(reaction);
                if(usedReactionbounds!=null){
                    double bondLength = Renderer.calculateBondLength(reaction);
                    Rectangle2D shiftedBounds =
                        GeometryTools.shiftReactionVertical(
                                reaction, reactionbounds, usedReactionbounds, bondLength);
                    usedReactionbounds = usedReactionbounds.createUnion(shiftedBounds);
                } else {
                    usedReactionbounds = reactionbounds;
                }
            }
        }
        //then we shift the molecules not to overlap
        Rectangle2D usedBounds = null;
        if(chemModel.getMoleculeSet()!=null){
			List<IAtomContainer> containers = AtomContainerSetManipulator.getAllAtomContainers(chemModel.getMoleculeSet());
			for (IAtomContainer container : containers) {
                // now move it so that they don't overlap
                Rectangle2D bounds = BoundsCalculator.calculateBounds(container);
                if (usedBounds != null) {
                    double bondLength = Renderer.calculateBondLength(container);
                    Rectangle2D shiftedBounds =
                        GeometryTools.shiftContainer(container, bounds, usedBounds, bondLength);
                    usedBounds = usedBounds.createUnion(shiftedBounds);
                } else {
					usedBounds = bounds;
                }
            }

			if (usedBounds != null) {
				double BOND_LENGTH = 1.5;
				for (IAtomContainer container : containers) {
					GeometryUtil.translate2D(container,
											 -(usedBounds.getX() - BOND_LENGTH),
											 -usedBounds.getY() - usedBounds.getHeight() - BOND_LENGTH);
				}
			}

        }
        //and the products/reactants in every reaction
        if(chemModel.getReactionSet()!=null){
            for(IReaction reaction : chemModel.getReactionSet().reactions()){
                usedBounds = null;
                double gap=0;
                double centerY=0;
                for (IAtomContainer container :
                    ReactionManipulator.getAllAtomContainers(reaction)) {
                    // now move it so that they don't overlap
                    Rectangle2D bounds = BoundsCalculator.calculateBounds(container);
                    if (usedBounds != null) {
                        if(gap==0){
                            gap = Renderer.calculateBondLength(container);
                            if(Double.isNaN(gap))
                                gap = 1.5;
                        }
                        Rectangle2D shiftedBounds =
                            GeometryTools.shiftContainer(
                                    container, bounds, usedBounds, gap*2);
                        double yshift=centerY - bounds.getCenterY();
                        Vector2d shift = new Vector2d(0.0, yshift);
                        GeometryUtil.translate2D(container, shift);
                        usedBounds = usedBounds.createUnion(shiftedBounds);
                    } else {
                        usedBounds = bounds;
                        centerY = bounds.getCenterY();
                    }
                }
                //we shift the products an extra bit to make a larget gap between products and reactants
                for(IAtomContainer container : reaction.getProducts().atomContainers()){
                    Vector2d shift = new Vector2d(gap*2, 0.0);
                    GeometryUtil.translate2D(container, shift);
                }
            }
        }
        //TODO overlaps of molecules in molecule set and reactions (ok, not too common, but still...)
    }

	// OK
	public void cleanup() {

		Map<IAtom, Point2d[]> coords = new HashMap<IAtom, Point2d[]>();
		Map<IBond, IBond.Stereo> stereo = new HashMap<>();

		// if there is a selection we clean up in-place
		IChemObjectSelection selection = getRenderer().getRenderer2DModel().getSelection();
        IAtomContainer selected = selection.getConnectedAtomContainer();
        if (selection.isFilled() && ConnectivityChecker.isConnected(selected)) {

			Set<IAtom> selectedAtoms = new HashSet<>();
			for (IBond bond : selection.elements(IBond.class)) {
				selectedAtoms.add(bond.getBegin());
				selectedAtoms.add(bond.getEnd());
			}
			selectedAtoms.addAll(selection.elements(IAtom.class));

			// crossing atoms/bonds
			Set<IAtom> xatoms = new HashSet<>();
			Set<IBond> xbonds = new HashSet<>();
			for (IAtomContainer container : ChemModelManipulator.getAllAtomContainers(chemModel)) {
				for (IBond bond : container.bonds()) {
					if (selectedAtoms.contains(bond.getBegin()) !=
						selectedAtoms.contains(bond.getEnd()))
						xbonds.add(bond);
				}
			}

			for (IBond xbond : xbonds) {
				for (IAtom a : xbond.atoms()) {
					if (selectedAtoms.contains(a))
						xatoms.add(a);
				}
			}

			if (xbonds.size() > 1) {
				// if we have two "leaving" bonds, check if there is a common atom,
				// we then move the anchor to that bond.
				if (xatoms.size() == 1) {
					xbonds.clear();
					IAtom anchorAtom = xatoms.iterator().next();
					for (IBond bond : ChemModelManipulator.getRelevantAtomContainer(getChemModel(),
																					anchorAtom)
														  .getConnectedBondsList(anchorAtom)) {
						if (selection.contains(bond))
							xbonds.add(bond);
					}
				}
			}

			if (xatoms.size() > 1)
				return;



			Point2d oldCenter = GeometryUtil.get2DCenter(selected);

			for (IAtom atom : selected.atoms()) {
				coords.put(atom, new Point2d[]{ null, atom.getPoint2d()});
				atom.setPoint2d(null);
			}
			for (IBond bond : selected.bonds()) {
				stereo.put(bond, bond.getStereo());
				bond.setStereo(Stereo.NONE);
			}

			generateNewCoordinates(selected, xatoms, xbonds);

			// put the molecule back where it was
			Point2d newCenter = GeometryUtil.get2DCenter(selected);
			GeometryUtil.translate2D(selected,
									 oldCenter.x - newCenter.x,
									 oldCenter.y - newCenter.y);

			coordinatesChanged();
			if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
				IUndoRedoable undoredo = getUndoRedoFactory().getChangeCoordsEdit(
						coords, stereo,"Clean Up Selection");
				getUndoRedoHandler().postEdit(undoredo);
			}
			return;
		}

		for (IAtomContainer container : ChemModelManipulator.getAllAtomContainers(chemModel)) {

			// ensure current stereo from 2D is set
			container.setStereoElements(StereoElementFactory.using2DCoordinates(container)
															.interpretProjections(Projection.Haworth, Projection.Chair)
															.createAll());
			for (IAtom atom : container.atoms()) {
				coords.put(atom, new Point2d[]{ null, atom.getPoint2d()});
				atom.setPoint2d(null);
			}
			for (IBond bond : container.bonds()) {
				stereo.put(bond, bond.getStereo());
				bond.setStereo(Stereo.NONE);
			}

			generateNewCoordinates(container);

			for (IAtom atom : container.atoms()) {
				Point2d[] coordsforatom = coords.get(atom);
				coordsforatom[0] = atom.getPoint2d();
			}
		}

        avoidOverlap(chemModel);
        if (rGroupHandler != null) {
            try {
                rGroupHandler.layoutRgroup();
            } catch (CDKException ignore) {
            }
        }

		coordinatesChanged();
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory().getChangeCoordsEdit(
					coords, stereo,"Clean Up");
			getUndoRedoHandler().postEdit(undoredo);
		}
	}

	public static void generateNewCoordinates(IAtomContainer container) {
		generateNewCoordinates(container, Collections.emptySet(), Collections.emptySet());
	}

	public static void generateNewCoordinates(IAtomContainer container, Set<IAtom> afix, Set<IBond> bifx) {
		if (diagramGenerator == null) {
			diagramGenerator = new StructureDiagramGenerator();
		}
		try {
			diagramGenerator.setMolecule(container, false, afix, bifx);
			diagramGenerator.generateCoordinates();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IRing addRing(int ringSize, Point2d worldcoord, boolean phantom) {

		IRing ring = chemModel.getBuilder().newInstance(IRing.class, ringSize, "C");
		double bondLength = Renderer.calculateBondLength(chemModel.getMoleculeSet());
		ringPlacer.placeRing(ring, worldcoord, bondLength, RingPlacer.jcpAngles);

		if (altMode) {
			GeometryUtil.rotate(ring, GeometryUtil.get2DCenter(ring), Math.toRadians(360d/(2*ringSize)));
		}

		if (phantom) {
			phantoms.add(ring);

			//and look if it would merge somewhere
            getRenderer().getRenderer2DModel().getMerge().clear();
            for(IAtom atom : ring.atoms()){
                IAtom closestAtomInRing = this.getClosestAtom(atom);
                if( closestAtomInRing != null) {
					getRenderer().getRenderer2DModel().getMerge().put(closestAtomInRing, atom);
                }
            }
		} else {
			IAtomContainerSet set = chemModel.getMoleculeSet();
			// the molecule set should not be null, but just in case...
			if (set == null) {
				set = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
				chemModel.setMoleculeSet(set);
			}
			IAtomContainer newAtomContainer = chemModel.getBuilder().newInstance(IAtomContainer.class);
			if (chemModel.getMoleculeSet().getAtomContainer(0).isEmpty())
				newAtomContainer = (IAtomContainer) chemModel.getMoleculeSet().getAtomContainer(0);
			else
				chemModel.getMoleculeSet().addAtomContainer(newAtomContainer);

			newAtomContainer.add(ring);
			updateAtoms(ring, ring.atoms());
            AtomBondSet abset = handleMerge(ring);
			structureChanged();

			if (getUndoRedoFactory() != null
				&& getUndoRedoHandler() != null) {
				IUndoRedoable undoredo = getUndoRedoFactory()
						.getAddAtomsAndBondsEdit(getIChemModel(), abset, null,
												 "Ring" + " " + ringSize, this);
				getUndoRedoHandler().postEdit(undoredo);
			}

			renderer.getRenderer2DModel()
					.setHighlightedAtom(newAtomContainer.getAtom(ring.indexOf(ring.getAtom((ringSize/2)-1))));
			clearPhantoms();
		}

		return ring;
	}

	// OK
	public IRing addPhenyl(Point2d worldcoord, int ringSize, boolean phantom) {

		IRing ring = chemModel.getBuilder().newInstance(IRing.class, ringSize, "C");
        if (ringSize == 5) {
            if (altMode) {
                ring.getBond(1).setOrder(IBond.Order.DOUBLE);
                ring.getBond(4).setOrder(IBond.Order.DOUBLE);
            } else {
                ring.getBond(1).setOrder(IBond.Order.DOUBLE);
                ring.getBond(3).setOrder(IBond.Order.DOUBLE);
            }
        } else if (ringSize == 6) {
            ring.getBond(0).setOrder(IBond.Order.DOUBLE);
            ring.getBond(2).setOrder(IBond.Order.DOUBLE);
            ring.getBond(4).setOrder(IBond.Order.DOUBLE);
        }

		double bondLength = Renderer.calculateBondLength(chemModel
				.getMoleculeSet());
		ringPlacer.placeRing(ring, worldcoord, bondLength, RingPlacer.jcpAngles);

		if (altMode) {
			GeometryUtil.rotate(ring, GeometryUtil.get2DCenter(ring), Math.PI/ringSize);
		}

		if (phantom) {
			phantoms.add(ring);

			//and look if it would merge somewhere
			getRenderer().getRenderer2DModel().getMerge().clear();
			for(IAtom atom : ring.atoms()){
				IAtom closestAtomInRing = this.getClosestAtom(atom);
				if( closestAtomInRing != null) {
					getRenderer().getRenderer2DModel().getMerge().put(closestAtomInRing, atom);
				}
			}
		} else {
			IAtomContainerSet set = chemModel.getMoleculeSet();

			// the molecule set should not be null, but just in case...
			if (set == null) {
				set = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
				chemModel.setMoleculeSet(set);
			}

			IAtomContainer newAtomContainer = chemModel.getBuilder().newInstance(IAtomContainer.class);
			if (chemModel.getMoleculeSet().getAtomContainer(0).getAtomCount() == 0)
				newAtomContainer = (IAtomContainer) chemModel.getMoleculeSet()
															 .getAtomContainer(0);
			else
				chemModel.getMoleculeSet().addAtomContainer(newAtomContainer);
			newAtomContainer.add(ring);
			updateAtoms(ring, ring.atoms());
			AtomBondSet abset = handleMerge(ring);
            structureChanged();

            if (getUndoRedoFactory() != null
				&& getUndoRedoHandler() != null) {
				IUndoRedoable undoredo = getUndoRedoFactory()
						.getAddAtomsAndBondsEdit(getIChemModel(),
												 abset, null,
												 "Benzene", this);
				getUndoRedoHandler().postEdit(undoredo);
			}

			renderer.getRenderer2DModel()
					.setHighlightedAtom(newAtomContainer.getAtom(ring.indexOf(ring.getAtom(2))));
			clearPhantoms();
		}


		return ring;
	}

    private AtomBondSet handleMerge(IRing ring) {
        Map<IAtom, IAtom> mergeSet = getRenderer().getRenderer2DModel().getMerge();
        mergeSet.clear();

        //we look if it would merge
        for (IAtom atom : ring.atoms()) {
            IAtom closestAtomInRing = getClosestAtom(atom);
            if (closestAtomInRing != null) {
                mergeSet.put(atom, closestAtomInRing);
            }
        }

        // if we need to merge, we first move the ring so that the merge atoms
        // are exactly on top of each other - if not doing this, rings get distorted.
        for (Map.Entry<IAtom, IAtom> e : mergeSet.entrySet()) {
            IAtom atomOut = e.getKey();
            IAtom atomRep = e.getValue();
            atomOut.getPoint2d().sub(atomRep.getPoint2d());
            Point2d pointSub = new Point2d(atomOut.getPoint2d().x, atomOut.getPoint2d().y);
            for (IAtom atom : ring.atoms()) {
                atom.getPoint2d().sub(pointSub);
            }
        }

        AtomBondSet abset = new AtomBondSet(ring);
        for (IAtom atom : mergeSet.keySet()) {
            abset.remove(atom);
            for (IBond bond : ring.getConnectedBondsList(atom)) {
                if (mergeSet.containsKey(bond.getOther(atom)))
                    abset.remove(bond);
            }
        }

        //and perform the merge
        mergeMolecules(null);
        getRenderer().getRenderer2DModel().getMerge().clear();
        return abset;
    }

    // OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addRing(org.openscience
	 * .cdk.interfaces.IAtom, int, boolean)
	 */
	public IRing addRing(IAtom atom, int ringSize, boolean phantom) {

		IAtomContainer sourceContainer = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom);
		IAtomContainer sharedAtoms = atom.getBuilder().newAtomContainer();
		IRing newRing;

		if (sourceContainer.getConnectedBondsCount(atom) > 1 && !altMode) {
			Point2d conAtomsCenter    = getConnectedAtomsCenter(atom);
			Point2d sharedAtomsCenter = atom.getPoint2d();
			Vector2d ringCenterVector = new Vector2d(sharedAtomsCenter);
			ringCenterVector.sub(conAtomsCenter);
			ringCenterVector.normalize();
			ringCenterVector.scale(1.5);

			newRing = chemModel.getBuilder().newInstance(IRing.class, ringSize, "C");

			IAtom root = newRing.getAtom(0);
			root.setPoint2d(new Point2d(atom.getPoint2d().x + ringCenterVector.x,
										atom.getPoint2d().y + ringCenterVector.y));

			IAtomContainer tmp = chemModel.getBuilder().newAtomContainer();
			tmp.addAtom(atom);
			tmp.addAtom(root);
			tmp.newBond(tmp.getAtom(0), tmp.getAtom(1));
			sharedAtoms.addAtom(root);

			conAtomsCenter    = getConnectedAtomsCenter(tmp.getAtom(tmp.indexOf(root)));
			sharedAtomsCenter = root.getPoint2d();
			ringCenterVector = new Vector2d(sharedAtomsCenter);
			ringCenterVector.sub(conAtomsCenter);

			double bondLength = GeometryUtil.getBondLengthMedian(sourceContainer);
			ringPlacer.setMolecule(tmp);
			ringPlacer.placeSpiroRing(newRing, sharedAtoms,
									  sharedAtomsCenter, ringCenterVector, bondLength);

			newRing.addAtom(atom);
			newRing.addBond(tmp.getBond(0));

			// normally the undo/redo is created by createAttached
			if (!phantom && getUndoRedoFactory() != null
				&& getUndoRedoHandler() != null) {
				AtomBondSet undoRedoSet = new AtomBondSet(newRing);
				undoRedoSet.remove(atom);
				IUndoRedoable undoredo = getUndoRedoFactory()
						.getAddAtomsAndBondsEdit(getIChemModel(),
												 undoRedoSet, null,
												 "Add Ring", this);
				getUndoRedoHandler().postEdit(undoredo);
			}

		} else {

			sharedAtoms.addAtom(atom);

			newRing = createAttachRing(sharedAtoms, ringSize, IElement.C, phantom);
			double bondLength = Renderer.calculateBondLength(sourceContainer);
			Point2d conAtomsCenter = getConnectedAtomsCenter(sharedAtoms, chemModel);

			Point2d sharedAtomsCenter = atom.getPoint2d();
			Vector2d ringCenterVector = new Vector2d(sharedAtomsCenter);
			ringCenterVector.sub(conAtomsCenter);

			if ((ringCenterVector.x == 0 && ringCenterVector.y == 0)) {
				// Rare bug case:
				// the spiro ring can not be attached, it will lead
				// to NaN values deeper down and serious picture distortion.
				// Instead, return empty ring, let user try otherwise..
				return chemModel.getBuilder().newInstance(IRing.class);
			} else {
				ringPlacer.setMolecule(sourceContainer);
				ringPlacer.placeSpiroRing(newRing, sharedAtoms, sharedAtomsCenter,
										  ringCenterVector, bondLength);
			}
		}

		for (IAtom ringAtom : newRing.atoms()) {
			if (phantom)
				this.addPhantomAtom(ringAtom);
			else if (!ringAtom.equals(atom))
				sourceContainer.addAtom(ringAtom);
		}

		for (IBond ringBond : newRing.bonds()) {
			if (phantom)
				this.addPhantomBond(ringBond);
			else
				sourceContainer.addBond(ringBond);
		}
		if (!phantom) {
			updateAtoms(sourceContainer, newRing.atoms());
			int atomToHighlight = newRing.getAtomCount() / 2;
			IAtom hgAtom = sourceContainer.getAtom(sourceContainer.indexOf(newRing.getAtom(atomToHighlight)));
			renderer.getRenderer2DModel().setHighlightedAtom(hgAtom);
		}

		JChemPaintRendererModel rModel = this.getRenderer().getRenderer2DModel();
		double d = rModel.getHighlightDistance() / rModel.getScale();
		for (IAtom newatom : newRing.atoms()) {
			if (!atom.equals(newatom) && getClosestAtom(atom) != null) {
				atom.getPoint2d().x += d;
			}
		}

		structureChanged();
		return newRing;
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addPhenyl(org.openscience
	 * .cdk.interfaces.IAtom, boolean)
	 */
	public IRing addPhenyl(IAtom atom, int ringSize, boolean phantom) {

		IAtomContainer sourceContainer = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom);
		IAtomContainer sharedAtoms = atom.getBuilder().newInstance(IAtomContainer.class);
		IRing newRing;

		if (atom.getBondCount() > 1 && !altMode) {
			Point2d conAtomsCenter    = getConnectedAtomsCenter(atom);
			Point2d sharedAtomsCenter = atom.getPoint2d();
			Vector2d ringCenterVector = new Vector2d(sharedAtomsCenter);
			ringCenterVector.sub(conAtomsCenter);
			ringCenterVector.normalize();
			ringCenterVector.scale(1.5);

			newRing = chemModel.getBuilder().newInstance(IRing.class, ringSize, "C");
            if (ringSize == 5) {
                newRing.getBond(1).setOrder(IBond.Order.DOUBLE);
                newRing.getBond(3).setOrder(IBond.Order.DOUBLE);
            } else if (ringSize == 6) {
                newRing.getBond(0).setOrder(IBond.Order.DOUBLE);
                newRing.getBond(2).setOrder(IBond.Order.DOUBLE);
                newRing.getBond(4).setOrder(IBond.Order.DOUBLE);
            }

			IAtom root = newRing.getAtom(0);
			root.setPoint2d(new Point2d(atom.getPoint2d().x + ringCenterVector.x,
													  atom.getPoint2d().y + ringCenterVector.y));

			IAtomContainer tmp = chemModel.getBuilder().newAtomContainer();
			tmp.addAtom(atom);
			tmp.addAtom(root);
			tmp.newBond(tmp.getAtom(0), tmp.getAtom(1));
			sharedAtoms.addAtom(root);

			conAtomsCenter    = getConnectedAtomsCenter(tmp.getAtom(tmp.indexOf(root)));
			sharedAtomsCenter = root.getPoint2d();
			ringCenterVector = new Vector2d(sharedAtomsCenter);
			ringCenterVector.sub(conAtomsCenter);

			double bondLength = GeometryUtil.getBondLengthMedian(sourceContainer);
			ringPlacer.setMolecule(tmp);
			ringPlacer.placeSpiroRing(newRing, sharedAtoms,
									  sharedAtomsCenter, ringCenterVector, bondLength);

            newRing.addAtom(atom);
            newRing.addBond(tmp.getBond(0));

            // normally the undo/redo is created by createAttached
            if (!phantom && getUndoRedoFactory() != null
                && getUndoRedoHandler() != null) {
                AtomBondSet undoRedoSet = new AtomBondSet(newRing);
                undoRedoSet.remove(atom);
                IUndoRedoable undoredo = getUndoRedoFactory()
                        .getAddAtomsAndBondsEdit(getIChemModel(),
                                                 undoRedoSet, null,
                                                 "Benzene", this);
                getUndoRedoHandler().postEdit(undoredo);
            }

		} else {
			sharedAtoms.addAtom(atom);

			// make a benzene ring
			newRing = createAttachRing(sharedAtoms, ringSize, IElement.C, phantom);
            if (ringSize == 5) {
                newRing.getBond(1).setOrder(IBond.Order.DOUBLE);
                newRing.getBond(3).setOrder(IBond.Order.DOUBLE);
            } else if (ringSize == 6) {
                newRing.getBond(0).setOrder(IBond.Order.DOUBLE);
                newRing.getBond(2).setOrder(IBond.Order.DOUBLE);
                newRing.getBond(4).setOrder(IBond.Order.DOUBLE);
            }

			double bondLength;
			if (sourceContainer.getBondCount() == 0) {
				/*
				 * Special case of adding a ring to a single, unconnected atom -
				 * places the ring centered on the place where the atom was.
				 */
				bondLength = Renderer.calculateBondLength(chemModel.getMoleculeSet());
				Point2d ringCenter = new Point2d(atom.getPoint2d());
				ringPlacer.setMolecule(sourceContainer);
				ringPlacer.placeRing(newRing, ringCenter, bondLength,
									 RingPlacer.jcpAngles);
			} else {
				bondLength = GeometryUtil.getBondLengthMedian(sourceContainer);
				Point2d conAtomsCenter = getConnectedAtomsCenter(sharedAtoms,
																 chemModel);

				Point2d sharedAtomsCenter = atom.getPoint2d();
				Vector2d ringCenterVector = new Vector2d(sharedAtomsCenter);
				ringCenterVector.sub(conAtomsCenter);

				if ((ringCenterVector.x == 0 && ringCenterVector.y == 0)) {
					return chemModel.getBuilder().newInstance(IRing.class);
				} else {
					ringPlacer.setMolecule(sourceContainer);
					ringPlacer.placeSpiroRing(newRing, sharedAtoms,
											  sharedAtomsCenter, ringCenterVector, bondLength);
				}
			}
		}

		// add the ring to the source container/phantoms
		for (IAtom ringAtom : newRing.atoms()) {
			if (phantom)
				this.addPhantomAtom(ringAtom);
			else
				sourceContainer.addAtom(ringAtom);
		}

		for (IBond ringBond : newRing.bonds()) {
			if (phantom)
				this.addPhantomBond(ringBond);
			else
				sourceContainer.addBond(ringBond);
		}
		if (!phantom) {
			updateAtoms(sourceContainer, newRing.atoms());
			int atomToHighlight = newRing.getAtomCount() / 2;
			IAtom hgAtom = sourceContainer.getAtom(sourceContainer.indexOf(newRing.getAtom(atomToHighlight)));
			renderer.getRenderer2DModel().setHighlightedAtom(hgAtom);
		}
		for (IAtom newatom : newRing.atoms()) {
			if (!atom.equals(newatom) && getClosestAtom(atom) != null) {
				JChemPaintRendererModel rModel = this.getRenderer().getRenderer2DModel();
				double d = rModel.getHighlightDistance() / rModel.getScale();
				atom.getPoint2d().x += d;
			}
		}
		structureChanged();
		return newRing;
	}

	// OK
	/**
	 * Constructs a new Ring of a certain size that contains all the atoms and
	 * bonds of the given AtomContainer and is filled up with new Atoms and
	 * Bonds.
	 *
	 * @param sharedAtoms
	 *            The AtomContainer containing the Atoms and bonds for the new
	 *            Ring
	 * @param ringSize
	 *            The size (number of Atoms) the Ring will have
	 * @param atomicNum
	 *            The element number the new atoms will have
	 * @param phantom
	 *            If true we assume this is a phantom ring and do not put it
	 *            into undo.
	 * @return The constructed Ring
	 */
	private IRing createAttachRing(IAtomContainer sharedAtoms, int ringSize,
																 int atomicNum, boolean phantom) {
		IRing newRing = sharedAtoms.getBuilder().newInstance(IRing.class,ringSize);
		for (int i = 0; i < sharedAtoms.getAtomCount(); i++) {
			newRing.addAtom(sharedAtoms.getAtom(i));
		}
		for (int i = sharedAtoms.getAtomCount(); i < ringSize; i++) {
			newRing.newAtom(atomicNum);
		}
		for (IBond bond : sharedAtoms.bonds())
			newRing.addBond(bond);
		for (int i = sharedAtoms.getBondCount(); i < ringSize - 1; i++) {
			newRing.newBond(newRing.getAtom(i),newRing.getAtom(i + 1));
		}
		newRing.newBond(newRing.getAtom(ringSize-1),newRing.getAtom(0));

		if (!phantom && getUndoRedoFactory() != null
				&& getUndoRedoHandler() != null) {
			AtomBondSet undoRedoSet = new AtomBondSet(newRing);
			for (IAtom atom : sharedAtoms.atoms())
				undoRedoSet.remove(atom);
			for (IBond bond : sharedAtoms.bonds())
				undoRedoSet.remove(bond);
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getAddAtomsAndBondsEdit(getIChemModel(),
							undoRedoSet, null, "Ring" + " " + ringSize,
							this);
			getUndoRedoHandler().postEdit(undoredo);
		}
		return newRing;
	}

	// OK
	/**
	 * Searches all the atoms attached to the Atoms in the given AtomContainer
	 * and calculates the center point of them.
	 *
	 * @param sharedAtoms
	 *            The Atoms the attached partners are searched of
	 * @return The Center Point of all the atoms found
	 */
	private Point2d getConnectedAtomsCenter(IAtomContainer sharedAtoms,
			IChemModel chemModel) {
		IAtomContainer conAtoms = sharedAtoms.getBuilder().newInstance(IAtomContainer.class);
		for (IAtom sharedAtom : sharedAtoms.atoms()) {
			conAtoms.addAtom(sharedAtom);
			IAtomContainer atomCon = ChemModelManipulator
					.getRelevantAtomContainer(chemModel, sharedAtom);
			for (IAtom atom : atomCon.getConnectedAtomsList(sharedAtom)) {
				conAtoms.addAtom(atom);
			}
		}
		return GeometryUtil.get2DCenter(conAtoms);
	}


	private Point2d getConnectedAtomsCenter(IAtom atom) {
		IAtomContainer conAtoms = atom.getBuilder().newInstance(IAtomContainer.class);
		conAtoms.addAtom(atom);
		for (IBond bond : atom.bonds()) {
			conAtoms.addAtom(bond.getOther(atom));
		}
		return GeometryUtil.get2DCenter(conAtoms);
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addRing(org.openscience
	 * .cdk.interfaces.IBond, int, boolean)
	 */
	public IRing addRing(IBond bond, int size, boolean phantom) {
		IAtomContainer sharedAtoms = bond.getBuilder().newInstance(IAtomContainer.class);
		IAtom firstAtom = bond.getAtom(0); // Assumes two-atom bonds only
		IAtom secondAtom = bond.getAtom(1);
		sharedAtoms.addAtom(firstAtom);
		sharedAtoms.addAtom(secondAtom);
		sharedAtoms.addBond(bond);
		IAtomContainer sourceContainer = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, firstAtom);

		Point2d sharedAtomsCenter = GeometryUtil.get2DCenter(sharedAtoms);

		// calculate two points that are perpendicular to the highlighted bond
		// and have a certain distance from the bond center
		Point2d firstPoint = firstAtom.getPoint2d();
		Point2d secondPoint = secondAtom.getPoint2d();
		Vector2d diff = new Vector2d(secondPoint);
		diff.sub(firstPoint);
		double bondLength = firstPoint.distance(secondPoint);
		double angle = GeometryUtil.getAngle(diff.x, diff.y);
		Point2d newPoint1 = new Point2d( // FIXME: what is this point??
				(Math.cos(angle + (Math.PI / 2)) * bondLength / 4)
						+ sharedAtomsCenter.x, (Math.sin(angle + (Math.PI / 2))
						* bondLength / 4)
						+ sharedAtomsCenter.y);
		Point2d newPoint2 = new Point2d( // FIXME: what is this point??
				(Math.cos(angle - (Math.PI / 2)) * bondLength / 4)
						+ sharedAtomsCenter.x, (Math.sin(angle - (Math.PI / 2))
						* bondLength / 4)
						+ sharedAtomsCenter.y);

		// decide on which side to draw the ring??
		IAtomContainer connectedAtoms = bond.getBuilder().newInstance(IAtomContainer.class);
		for (IAtom atom : sourceContainer.getConnectedAtomsList(firstAtom)) {
			if (!atom.equals(secondAtom))
				connectedAtoms.addAtom(atom);
		}
		for (IAtom atom : sourceContainer.getConnectedAtomsList(secondAtom)) {
			if (!atom.equals(firstAtom))
				connectedAtoms.addAtom(atom);
		}
		Point2d conAtomsCenter = GeometryUtil.get2DCenter(connectedAtoms);
		double distance1 = newPoint1.distance(conAtomsCenter);
		double distance2 = newPoint2.distance(conAtomsCenter);
		Vector2d ringCenterVector = new Vector2d(sharedAtomsCenter);
		if (distance1 < distance2) {
			ringCenterVector.sub(newPoint1);
		} else { // distance2 <= distance1
			ringCenterVector.sub(newPoint2);
		}

		// construct a new Ring that contains the highlighted bond an its two
		// atoms
		IRing newRing = createAttachRing(sharedAtoms, size, IElement.C, phantom);
		ringPlacer.setMolecule(sourceContainer);
		ringPlacer.placeFusedRing(newRing, sharedAtoms, ringCenterVector, bondLength);
		// add the new atoms and bonds
		for (IAtom ringAtom : newRing.atoms()) {
			if (phantom)
				addPhantomAtom(ringAtom);
			else if (!ringAtom.equals(firstAtom) && !ringAtom.equals(secondAtom))
				sourceContainer.addAtom(ringAtom);
		}
		for (IBond ringBond : newRing.bonds()) {
			if (phantom)
				addPhantomBond(ringBond);
			else if (!ringBond.equals(bond))
				sourceContainer.addBond(ringBond);
		}

		if (!phantom) {
			renderer.getRenderer2DModel().setHighlightedBond(newRing.getBond(size/2));
			updateAtoms(sourceContainer, newRing.atoms());
		}

		JChemPaintRendererModel rModel = this.getRenderer().getRenderer2DModel();
		double d = rModel.getHighlightDistance() / rModel.getScale();
		for (IAtom atom : newRing.atoms()) {
			if (!atom.equals(firstAtom) && !atom.equals(secondAtom)
					&& getClosestAtom(atom) != null) {
				atom.getPoint2d().x += d;
			}
		}



		structureChanged();
		return newRing;
	}

	// OK
	public IAtom getClosestAtom(IAtom atom) {
		return getAtomInRange(null, atom);
	}

	// OK
	public IAtom getAtomInRange(Collection<IAtom> toIgnore, IAtom atom) {
		Point2d atomPosition = atom.getPoint2d();
		JChemPaintRendererModel rModel = this.getRenderer().getRenderer2DModel();
		double highlight = rModel.getHighlightDistance() / rModel.getScale();

		IAtom bestClosestAtom = null;
		double bestDistance = -1;
		for (IAtomContainer atomContainer : ChemModelManipulator
				.getAllAtomContainers(getIChemModel())) {

			IAtom closestAtom = GeometryUtil.getClosestAtom(atomContainer,
					atom);

			if (closestAtom != null) {
				double distance = closestAtom.getPoint2d().distance(
						atomPosition);
				if ((distance > highlight)
						|| (toIgnore != null && toIgnore.contains(closestAtom))) {
					continue;
				} else {
					if (bestClosestAtom == null || distance < bestDistance) {
						bestClosestAtom = closestAtom;
						bestDistance = distance;
					}
				}
			}
		}
		return bestClosestAtom;
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#addPhenyl(org.openscience
	 * .cdk.interfaces.IBond, boolean)
	 */
	public IRing addPhenyl(IBond bond, int ringSize, boolean phantom) {
		IAtomContainer sharedAtoms = bond.getBuilder().newInstance(IAtomContainer.class);
		IAtom firstAtom = bond.getAtom(0); // Assumes two-atom bonds only
		IAtom secondAtom = bond.getAtom(1);
		sharedAtoms.addAtom(firstAtom);
		sharedAtoms.addAtom(secondAtom);
		sharedAtoms.addBond(bond);
		IAtomContainer sourceContainer = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, firstAtom);

		Point2d sharedAtomsCenter = GeometryUtil.get2DCenter(sharedAtoms);

		// calculate two points that are perpendicular to the highlighted bond
		// and have a certain distance from the bond center
		Point2d firstPoint = firstAtom.getPoint2d();
		Point2d secondPoint = secondAtom.getPoint2d();
		Vector2d diff = new Vector2d(secondPoint);
		diff.sub(firstPoint);
		double bondLength = firstPoint.distance(secondPoint);
		double angle = GeometryUtil.getAngle(diff.x, diff.y);
		Point2d newPoint1 = new Point2d( // FIXME: what is this point??
				(Math.cos(angle + (Math.PI / 2)) * bondLength / 4)
						+ sharedAtomsCenter.x, (Math.sin(angle + (Math.PI / 2))
						* bondLength / 4)
						+ sharedAtomsCenter.y);
		Point2d newPoint2 = new Point2d( // FIXME: what is this point??
				(Math.cos(angle - (Math.PI / 2)) * bondLength / 4)
						+ sharedAtomsCenter.x, (Math.sin(angle - (Math.PI / 2))
						* bondLength / 4)
						+ sharedAtomsCenter.y);

		// decide on which side to draw the ring??
		IAtomContainer connectedAtoms = bond.getBuilder().newInstance(IAtomContainer.class);
		for (IAtom atom : sourceContainer.getConnectedAtomsList(firstAtom)) {
			if (!atom.equals(secondAtom))
				connectedAtoms.addAtom(atom);
		}
		for (IAtom atom : sourceContainer.getConnectedAtomsList(secondAtom)) {
			if (!atom.equals(firstAtom))
				connectedAtoms.addAtom(atom);
		}
		Point2d conAtomsCenter = GeometryUtil.get2DCenter(connectedAtoms);
		double distance1 = newPoint1.distance(conAtomsCenter);
		double distance2 = newPoint2.distance(conAtomsCenter);
		Vector2d ringCenterVector = new Vector2d(sharedAtomsCenter);
		if (distance1 < distance2) {
			ringCenterVector.sub(newPoint1);
		} else { // distance2 <= distance1
			ringCenterVector.sub(newPoint2);
		}

		// construct a new Ring that contains the highlighted bond an its two
		// atoms
		IRing newRing = createAttachRing(sharedAtoms, ringSize, IElement.C, phantom);
		ringPlacer.setMolecule(sourceContainer);
		ringPlacer.placeFusedRing(newRing, sharedAtoms, ringCenterVector, bondLength);
		if (sourceContainer.getMaximumBondOrder(bond.getAtom(0)) == IBond.Order.SINGLE
				&& sourceContainer.getMaximumBondOrder(bond.getAtom(1)) == IBond.Order.SINGLE) {
            if (ringSize == 5) {
                if (altMode) {
                    newRing.getBond(1).setOrder(IBond.Order.DOUBLE);
                    newRing.getBond(4).setOrder(IBond.Order.DOUBLE);
                } else {
                    newRing.getBond(1).setOrder(IBond.Order.DOUBLE);
                    newRing.getBond(3).setOrder(IBond.Order.DOUBLE);
                }
            } else if (ringSize == 6) {
                if (altMode) {
                    newRing.getBond(2).setOrder(IBond.Order.DOUBLE);
                    newRing.getBond(4).setOrder(IBond.Order.DOUBLE);
                }
                else {
                    newRing.getBond(1).setOrder(IBond.Order.DOUBLE);
                    newRing.getBond(3).setOrder(IBond.Order.DOUBLE);
                    newRing.getBond(5).setOrder(IBond.Order.DOUBLE);
                }
            }

		} else { // assume Order.DOUBLE, so only need to add 2 double bonds
            if (ringSize == 5) {
                if (altMode)
                    newRing.getBond(3).setOrder(IBond.Order.DOUBLE);
                else
                    newRing.getBond(2).setOrder(IBond.Order.DOUBLE);
            } else if (ringSize == 6) {
                newRing.getBond(2).setOrder(IBond.Order.DOUBLE);
                newRing.getBond(4).setOrder(IBond.Order.DOUBLE);
            }
		}
		// add the new atoms and bonds
		for (IAtom ringAtom : newRing.atoms()) {
			if (phantom)
				this.addPhantomAtom(ringAtom);
			else
				sourceContainer.addAtom(ringAtom);
		}
		for (IBond ringBond : newRing.bonds()) {
			if (!ringBond.equals(bond)) {
				if (phantom)
					this.addPhantomBond(ringBond);
				else
					sourceContainer.addBond(ringBond);
			}
		}

		if (!phantom) {
			renderer.getRenderer2DModel().setHighlightedBond(newRing.getBond(3));
			updateAtoms(sourceContainer, newRing.atoms());
		}

		JChemPaintRendererModel rModel = this.getRenderer().getRenderer2DModel();
		double d = rModel.getHighlightDistance() / rModel.getScale();
		for (IAtom atom : newRing.atoms()) {
			if (!atom.equals(firstAtom) && !atom.equals(secondAtom)
					&& getClosestAtom(atom) != null) {
				atom.getPoint2d().x += d;
			}
		}
		structureChanged();
		return newRing;
	}

	// OK
	public void removeBondWithoutUndo(IBond bond) {
		ChemModelManipulator.removeElectronContainer(chemModel, bond);
		// set hybridization from bond order
		bond.getAtom(0).setHybridization(null);
		bond.getAtom(1).setHybridization(null);
		updateAtom(bond.getAtom(0));
		updateAtom(bond.getAtom(1));
		adjustRgroup();
		structureChanged();
	}

	// OK TODO this could do with less partitioning
	public void removeBond(IBond bond) {
		removeBondWithoutUndo(bond);
		AtomBondSet undAtomContainer = new AtomBondSet();
		undAtomContainer.add(bond);
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getRemoveAtomsAndBondsEdit(getIChemModel(),
							undAtomContainer, "Remove Bond", this);
			getUndoRedoHandler().postEdit(undoredo);
		}
	}

	public void addPhantomAtom(IAtom atom) {
		this.phantoms.addAtom(atom);
	}

	public void addPhantomBond(IBond bond) {
		this.phantoms.addBond(bond);
	}

	public void clearPhantoms() {
		getRenderer().getRenderer2DModel().getMerge().clear();
		this.phantoms.removeAllElements();
	}

	@Override
	public void setPhantoms(IAtomContainer phantoms) {
		this.phantoms = phantoms;

	}

	public IAtomContainer getPhantoms() {
		return this.phantoms;
	}

	public void adjustBondOrders() throws IOException, ClassNotFoundException,
			CDKException {
		// TODO also work on reactions ?!?
		SaturationChecker satChecker = new SaturationChecker();
		List<IAtomContainer> containersList = ChemModelManipulator
				.getAllAtomContainers(chemModel);
		Iterator<IAtomContainer> iterator = containersList.iterator();
		Map<IBond, IBond.Order[]> changedBonds = new HashMap<IBond, IBond.Order[]>();
		while (iterator.hasNext()) {
			IAtomContainer ac = (IAtomContainer) iterator.next();
			for (IBond bond : ac.bonds()) {
				IBond.Order[] orders = new IBond.Order[2];
				orders[1] = bond.getOrder();
				changedBonds.put(bond, orders);
			}
			satChecker.saturate(ac);
			for (IBond bond : ac.bonds()) {
				IBond.Order[] orders = changedBonds.get(bond);
				orders[0] = bond.getOrder();
				changedBonds.put(bond, orders);
			}
		}
		if (this.getController2DModel().getAutoUpdateImplicitHydrogens())
			updateImplicitHydrogenCounts();
		if (undoredofactory != null && undoredohandler != null) {
			IUndoRedoable undoredo = undoredofactory.getAdjustBondOrdersEdit(
					changedBonds, Collections.emptyMap(),
					"Adjust Bond Order of Molecules", this);
			undoredohandler.postEdit(undoredo);
		}
	}

	// OK
	public void resetBondOrders() {
		List<IAtomContainer> containersList = ChemModelManipulator
				.getAllAtomContainers(chemModel);
		Iterator<IAtomContainer> iterator = containersList.iterator();
		Map<IBond, IBond.Order[]> changedBonds = new HashMap<IBond, IBond.Order[]>();
		while (iterator.hasNext()) {
			IAtomContainer ac = iterator.next();
			for (IBond bond : ac.bonds()) {
				IBond.Order[] orders = new IBond.Order[2];
				orders[1] = bond.getOrder();
				orders[0] = Order.SINGLE;
				changedBonds.put(bond, orders);
				bond.setOrder(Order.SINGLE);
			}
		}
		if (this.getController2DModel().getAutoUpdateImplicitHydrogens())
			updateImplicitHydrogenCounts();
		if (undoredofactory != null && undoredohandler != null) {
			IUndoRedoable undoredo = undoredofactory.getAdjustBondOrdersEdit(
					changedBonds, Collections.emptyMap(),
					"Reset Bond Order of Molecules", this);
			undoredohandler.postEdit(undoredo);
		}
	}

	// OK
	public void replaceAtom(IAtom atomnew, IAtom atomold) {
		IAtomContainer relevantContainer = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, atomold);
		AtomContainerManipulator.replaceAtomByAtom(relevantContainer, atomold,
				atomnew);
		updateAtom(atomnew);
		structureChanged();
		if (undoredofactory != null && undoredohandler != null) {
			IUndoRedoable undoredo = undoredofactory.getReplaceAtomEdit(
					chemModel, atomold, atomnew, "Replace Atom");
			undoredohandler.postEdit(undoredo);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seeorg.openscience.cdk.controller.IAtomBondEdits#addSingleElectron(org.
	 * openscience.cdk.interfaces.IAtom)
	 */
	public void addSingleElectron(IAtom atom) {
		IAtomContainer relevantContainer = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, atom);
		ISingleElectron singleElectron = atom.getBuilder().newInstance(ISingleElectron.class,atom);
		relevantContainer.addSingleElectron(singleElectron);
		updateAtom(atom);
		if (undoredofactory != null && undoredohandler != null) {
			IUndoRedoable undoredo = undoredofactory.getSingleElectronEdit(
					relevantContainer, singleElectron, true, this, atom,
					"Add Single Electron");
			undoredohandler.postEdit(undoredo);
		}
	}

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IAtomBondEdits#removeSingleElectron(org
	 * .openscience.cdk.interfaces.IAtom)
	 */
	public void removeSingleElectron(IAtom atom) {
		IAtomContainer relevantContainer = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, atom);
		if (relevantContainer.getConnectedSingleElectronsCount(atom) > 0) {
			ISingleElectron removedElectron = relevantContainer
					.removeSingleElectron(relevantContainer
							.getConnectedSingleElectronsCount(atom) - 1);
			updateAtom(atom);
			if (undoredofactory != null && undoredohandler != null) {
				IUndoRedoable undoredo = undoredofactory.getSingleElectronEdit(
						relevantContainer, removedElectron, false, this, atom,
						"Remove Single Electron");
				undoredohandler.postEdit(undoredo);
			}
		}
	}

	public void clearValidation() {
		Iterator<IAtomContainer> containers = ChemModelManipulator
				.getAllAtomContainers(chemModel).iterator();
		while (containers.hasNext()) {
			IAtomContainer atoms = containers.next();
			for (int i = 0; i < atoms.getAtomCount(); i++) {
				ProblemMarker.unmark(atoms.getAtom(i));
			}
		}
	}

	// OK
	public void flip(boolean horizontal) {
		HashMap<IAtom, Point2d[]> atomCoordsMap = new HashMap<IAtom, Point2d[]>();
		Map<IBond, IBond.Stereo> bondStereo = new HashMap<IBond, IBond.Stereo>();
		JChemPaintRendererModel renderModel = renderer.getRenderer2DModel();

		Set<IBond> anchors = new HashSet<>();
		IAtomContainer toflip;
		IChemObjectSelection select = renderModel.getSelection();
		if (select.isFilled()) {
			for (IAtomContainer container : getChemModel().getMoleculeSet()) {
				for (IBond bond : container.bonds()) {
					if (select.contains(bond.getBegin()) && !select.contains(bond.getEnd())) {
						anchors.add(bond);
					} else if (select.contains(bond.getEnd()) && !select.contains(bond.getBegin())) {
						anchors.add(bond);
					}
				}
			}
			toflip = select.getConnectedAtomContainer();
		} else {
			List<IAtomContainer> toflipall = ChemModelManipulator
					.getAllAtomContainers(chemModel);
			toflip = toflipall.get(0).getBuilder().newInstance(IAtomContainer.class);
			for (IAtomContainer atomContainer : toflipall) {
				toflip.add(atomContainer);
			}
		}

		// if we have two "leaving" bonds, check if there is a common atom,
		// we then move the anchor to that bond.
		if (anchors.size() > 1) {
			Set<IAtom> anchorAtoms = new HashSet<>();
			for (IBond anchor : anchors) {
				for (IAtom a : anchor.atoms()) {
					if (select.contains(a))
						anchorAtoms.add(a);
				}
			}
			if (anchorAtoms.size() == 1) {
				anchors.clear();
				IAtom anchorAtom = anchorAtoms.iterator().next();
				for (IBond bond : ChemModelManipulator.getRelevantAtomContainer(getChemModel(),
																				anchorAtom)
													  .getConnectedBondsList(anchorAtom)) {
					if (select.contains(bond))
						anchors.add(bond);
				}
			}
			System.err.println(anchors.size());
		}

		if (anchors.size() == 1 && !altMode) {
			IBond bond = anchors.iterator().next();
			for (IAtom atom : toflip.atoms()) {
				Point2d p = atom.getPoint2d();
				Point2d backup = new Point2d(p);
				reflect(p, bond.getBegin().getPoint2d(), bond.getEnd().getPoint2d());
				atomCoordsMap.put(atom, new Point2d[]{new Point2d(p), backup});
			}
		} else {
			Point2d center = GeometryUtil.get2DCenter(toflip);
			for (IAtom atom : toflip.atoms()) {
				Point2d p2d = atom.getPoint2d();
				Point2d oldCoord = new Point2d(p2d.x, p2d.y);
				if (horizontal) {
					p2d.y = 2.0 * center.y - p2d.y;
				} else {
					p2d.x = 2.0 * center.x - p2d.x;
				}
				Point2d newCoord = p2d;
				if (!oldCoord.equals(newCoord)) {
					Point2d[] coords = new Point2d[2];
					coords[0] = newCoord;
					coords[1] = oldCoord;
					atomCoordsMap.put(atom, coords);
				}
			}
		}

		// Stereo bonds must be flipped as well to keep the structure
		for (IBond bond : toflip.bonds()) {
			bondStereo.put(bond, bond.getStereo());
			if (bond.getStereo() == IBond.Stereo.UP)
				bond.setStereo(IBond.Stereo.DOWN);
			else if (bond.getStereo() == IBond.Stereo.DOWN)
				bond.setStereo(IBond.Stereo.UP);
			else if (bond.getStereo() == IBond.Stereo.UP_INVERTED)
				bond.setStereo(IBond.Stereo.DOWN_INVERTED);
			else if (bond.getStereo() == IBond.Stereo.DOWN_INVERTED)
				bond.setStereo(IBond.Stereo.UP_INVERTED);
		}

		coordinatesChanged();
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory().getChangeCoordsEdit(
					atomCoordsMap, bondStereo, "Clean Up");
			getUndoRedoHandler().postEdit(undoredo);
		}
	}

	public void invertStereoInSelection() {
		IAtomContainer toflip;
		JChemPaintRendererModel renderModel = renderer.getRenderer2DModel();
		if (renderModel.getSelection().getConnectedAtomContainer() != null
				&& renderModel.getSelection().getConnectedAtomContainer()
						.getAtomCount() != 0) {
			toflip = renderModel.getSelection().getConnectedAtomContainer();
		} else
			return;

		for (IBond bond : toflip.bonds()) {
			if (bond.getStereo() == IBond.Stereo.UP)
				bond.setStereo(IBond.Stereo.DOWN);
			else if (bond.getStereo() == IBond.Stereo.DOWN)
				bond.setStereo(IBond.Stereo.UP);
			else if (bond.getStereo() == IBond.Stereo.UP_INVERTED)
				bond.setStereo(IBond.Stereo.DOWN_INVERTED);
			else if (bond.getStereo() == IBond.Stereo.DOWN_INVERTED)
				bond.setStereo(IBond.Stereo.UP_INVERTED);
		}
	}

	public void setEventHandler(IChemModelEventRelayHandler handler) {
		this.changeHandler = handler;
	}

	protected void structureChanged() {
		if (renderer.getRenderer2DModel().getSelection() instanceof IncrementalSelection)
			select((IncrementalSelection) renderer.getRenderer2DModel()
					.getSelection());
		if (changeHandler != null)
			changeHandler.structureChanged();
	}

	public void fireZoomEvent() {
		changeHandler.zoomChanged();
	}

	public void fireStructureChangedEvent() {

		changeHandler.structureChanged();
	}

	private void structurePropertiesChanged() {
		if (changeHandler != null)
			changeHandler.structurePropertiesChanged();
	}

	private void coordinatesChanged() {
		if (changeHandler != null)
			changeHandler.coordinatesChanged();
	}

	public IUndoRedoFactory getUndoRedoFactory() {
		return undoredofactory;
	}

	public UndoRedoHandler getUndoRedoHandler() {
		return undoredohandler;
	}

	private void selectionChanged() {
		if (changeHandler != null)
			changeHandler.selectionChanged();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#select(org.openscience
	 * .cdk.renderer.selection.IncrementalSelection)
	 */
	public void select(IncrementalSelection selection) {
		if (selection != null)
			selection.select(this.chemModel);
		selectionChanged();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#select(org.openscience
	 * .cdk.renderer.selection.IChemObjectSelection)
	 */
	public void select(IChemObjectSelection selection) {
		getRenderer().getRenderer2DModel().setSelection(selection);
		selectionChanged();
	}

	// OK
	public void addFragment(AtomBondSet toPaste, IAtomContainer moleculeToAddTo, IAtomContainer toRemove) {
		IAtomContainerSet newMoleculeSet = chemModel.getMoleculeSet();
		if (newMoleculeSet == null) {
			newMoleculeSet = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
		}
		IAtomContainerSet oldMoleculeSet = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
		if (moleculeToAddTo == null) {
			moleculeToAddTo = chemModel.getBuilder().newAtomContainer();
			for (IAtom atom : toPaste.atoms())
				moleculeToAddTo.addAtom(atom);
			for (IBond bond : toPaste.bonds())
				moleculeToAddTo.addBond(bond);
			newMoleculeSet.addAtomContainer(moleculeToAddTo);
		} else {
			IAtomContainer mol = chemModel.getBuilder().newInstance(IAtomContainer.class);
			for (IAtom atom: moleculeToAddTo.atoms())
				mol.addAtom(atom);
			for (IBond bond: moleculeToAddTo.bonds())
				mol.addBond(bond);
			oldMoleculeSet.addAtomContainer(mol);
			for (IAtom atom : toPaste.atoms())
				moleculeToAddTo.addAtom(atom);
			for (IBond bond : toPaste.bonds())
				moleculeToAddTo.addBond(bond);
		}
		if (toRemove != null) {
			oldMoleculeSet.addAtomContainer(toRemove);
			moleculeToAddTo.add(toRemove);
			updateAtoms(toRemove, toRemove.atoms());
			newMoleculeSet.removeAtomContainer(toRemove);
		}
		for (IAtomContainer ac: newMoleculeSet.atomContainers())
			updateAtoms(ac, ac.atoms());
		if (undoredofactory != null && undoredohandler != null) {
			IUndoRedoable undoredo = undoredofactory.getLoadNewModelEdit(
					getIChemModel(), this, oldMoleculeSet, null, newMoleculeSet, null, "Add Chain Fragment");
			undoredohandler.postEdit(undoredo);
		}
		chemModel.setMoleculeSet(newMoleculeSet);
		structureChanged();
	}

	// OK
	public AtomBondSet deleteFragment(AtomBondSet selected) {

		AtomBondSet undoRedoSet = new AtomBondSet();
		if (rGroupHandler != null && !rGroupHandler.checkRGroupOkayForDelete(selected, this))
			return undoRedoSet;

		Set<IAtom> adjacentAtoms = new HashSet<>();
		for (IAtom atom : selected.atoms()) {
			undoRedoSet.add(atom);
		}
		for (IAtom atom : undoRedoSet.atoms()) {
			IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom);
			for (IBond bond : container.getConnectedBondsList(atom)) {
				if (!undoRedoSet.contains(bond)) {
					undoRedoSet.add(bond);
					adjacentAtoms.add(bond.getOther(atom));
				}
			}
		}
		for (IBond bond : selected.bonds()) {
			if (!undoRedoSet.contains(bond))
				undoRedoSet.add(bond);
		}


		for (IBond bond : undoRedoSet.bonds()) {
			ChemModelManipulator.removeElectronContainer(chemModel, bond);
		}
		for (IAtom atom : undoRedoSet.atoms()) {
			ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, atom);
		}

		for (IAtom atom : adjacentAtoms) {
			if (!undoRedoSet.contains(atom))
				updateAtom(atom);
		}

		removeEmptyContainers(chemModel);
		if (undoredofactory != null && undoredohandler != null) {
			IUndoRedoable undoredo = undoredofactory
					.getRemoveAtomsAndBondsEdit(chemModel, undoRedoSet, "Cut", this);
			undoredohandler.postEdit(undoredo);
		}
		adjustRgroup();
		structureChanged();
		return undoRedoSet;
	}

	public static void removeEmptyContainers(IChemModel chemModel) {
		Iterator<IAtomContainer> it = ChemModelManipulator
				.getAllAtomContainers(chemModel).iterator();
		while (it.hasNext()) {
			IAtomContainer ac = it.next();
			if (ac.getAtomCount() == 0) {
				chemModel.getMoleculeSet().removeAtomContainer(ac);
			}
		}
		if (chemModel.getMoleculeSet().getAtomContainerCount() == 0)
			chemModel.getMoleculeSet().addAtomContainer(
					chemModel.getBuilder().newInstance(IAtomContainer.class));
	}

	// OK
	/**
	 * Updates an array of atoms with respect to its hydrogen count
	 *
	 *@param container
	 *            The AtomContainer to work on
	 *@param atoms
	 *            The Atoms to update
	 */
	public void updateAtoms(IAtomContainer container, Iterable<IAtom> atoms) {
		for (IAtom atom : atoms) {
			updateAtom(container, atom);
		}
	}

	// OK
	/**
	 * Updates an atom with respect to its hydrogen count
	 *
	 *@param atom
	 *            The Atom to update
	 */
	public void updateAtom(IAtom atom) {
		IAtomContainer container = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, atom);
		if (container != null) {
			updateAtom(container, atom);
		}
	}

	public void updateAtoms(IBond bond) {
		IAtomContainer container = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, bond);
		if (container != null) {
			updateAtom(container, bond.getBegin());
			updateAtom(container, bond.getEnd());
		}
	}

	// OK
	/**
	 * Updates an atom with respect to its hydrogen count
	 *
	 *@param container
	 *            The AtomContainer to work on
	 *@param atom
	 *            The Atom to update
	 */
	private void updateAtom(IAtomContainer container, IAtom atom) {
		if (this.getController2DModel().getAutoUpdateImplicitHydrogens()) {
			atom.setImplicitHydrogenCount(0);
			try {
				IAtomType type = matcher.findMatchingAtomType(container, atom);
				if (type != null && !type.getAtomTypeName().equals("X")) {
					Integer neighbourCount = type.getFormalNeighbourCount();
					if (neighbourCount != null) {
						atom.setImplicitHydrogenCount(neighbourCount
								- container.getConnectedAtomsCount(atom));
					}
					// for some reason, the neighbour count takes into account
					// only
					// one single electron
					if (container.getConnectedSingleElectronsCount(atom) > 1
							&& atom.getImplicitHydrogenCount()
									- container
											.getConnectedSingleElectronsCount(atom)
									+ 1 > -1)
						atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount()
								- container
										.getConnectedSingleElectronsCount(atom)
								+ 1);
					atom.setFlag(CDKConstants.IS_TYPEABLE, false);
				} else {
					atom.setFlag(CDKConstants.IS_TYPEABLE, true);
				}
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}
	}

	// OK
	public void makeAllExplicitImplicit() {
		IAtomContainer undoRedoSet = chemModel.getBuilder()
				.newInstance(IAtomContainer.class);
		List<IAtomContainer> containers = ChemModelManipulator
				.getAllAtomContainers(chemModel);
		for (int i = 0; i < containers.size(); i++) {
			IAtomContainer removeatoms = chemModel.getBuilder()
					.newInstance(IAtomContainer.class);
			for (IAtom atom : containers.get(i).atoms()) {
				if (atom.getSymbol().equals("H")) {
					removeatoms.addAtom(atom);
					removeatoms.addBond(containers.get(i)
							.getConnectedBondsList(atom).get(0));
					containers
							.get(i)
							.getConnectedAtomsList(atom)
							.get(0)
							.setImplicitHydrogenCount(
									containers.get(i).getConnectedAtomsList(
											atom).get(0).getImplicitHydrogenCount() + 1);
				}
			}
			containers.get(i).remove(removeatoms);
			undoRedoSet.add(removeatoms);
		}
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getRemoveAtomsAndBondsEdit(chemModel, new AtomBondSet(undoRedoSet),
							"Make explicit Hs implicit", this);
			getUndoRedoHandler().postEdit(undoredo);
		}
		structureChanged();
	}

	// OK
	public void makeAllImplicitExplicit() {
		AtomBondSet undoRedoSet = new AtomBondSet();
		List<IAtomContainer> containers = ChemModelManipulator
				.getAllAtomContainers(chemModel);
		for (int i = 0; i < containers.size(); i++) {
			for (IAtom atom : containers.get(i).atoms()) {
				int hcount = atom.getImplicitHydrogenCount();
				for (int k = 0; k < hcount; k++) {
					IAtom newAtom = this.addAtomWithoutUndo("H", atom, false);
					IAtomContainer atomContainer = ChemModelManipulator
							.getRelevantAtomContainer(getIChemModel(), newAtom);
					IBond newBond = atomContainer.getBond(atom, newAtom);
					undoRedoSet.add(newAtom);
					undoRedoSet.add(newBond);
				}
			}
		}
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getAddAtomsAndBondsEdit(chemModel, undoRedoSet,
							null, "Make implicit Hs explicit", this);
			getUndoRedoHandler().postEdit(undoredo);
		}
		structureChanged();
	}

	// OK
	public void setImplicitHydrogenCount(IAtom atom, int intValue) {
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			HashMap<IAtom, Integer[]> atomhydrogenmap = new HashMap<IAtom, Integer[]>();
			atomhydrogenmap.put(atom, new Integer[] { intValue,
					atom.getImplicitHydrogenCount() });
			IUndoRedoable undoredo = getUndoRedoFactory()
					.getChangeHydrogenCountEdit(atomhydrogenmap,
							"Change hydrogen count to " + intValue);
			getUndoRedoHandler().postEdit(undoredo);
		}
		atom.setImplicitHydrogenCount(intValue);
		structureChanged();
	}

	// OK
	/**
	 * Merge molecules when a selection is moved onto another part of the
	 * molecule set
	 *
	 */
	public void mergeMolecules(Vector2d movedDistance) {

		JChemPaintRendererModel model = getRenderer().getRenderer2DModel();
		if (rGroupHandler != null) {
			if (!rGroupHandler.isMergeAllowed(this)) {
				model.getMerge().clear();
				updateView();
				throw new RuntimeException("Merge not allowed by RGroupHandler");
			}
		}

		// First try to shift the selection to be exactly on top of
		// the target of the merge. This makes the end results visually
		// more attractive and avoid tilted rings
		//
		Map<IAtom, IAtom> mergeMap = model.getMerge();
		for (Map.Entry<IAtom,IAtom> e : mergeMap.entrySet()) {
			IAtomContainer movedAtomContainer = renderer.getRenderer2DModel()
					.getSelection().getConnectedAtomContainer();
			if (movedAtomContainer != null) {
				IAtom atomA = e.getKey();
				IAtom atomB = e.getValue();
				Vector2d shift = new Vector2d();
				shift.sub(atomB.getPoint2d(), atomA.getPoint2d());

				for (IAtom shiftAtom : movedAtomContainer.atoms()) {
					shiftAtom.getPoint2d().add(shift);
				}
			}
		}

		List<IAtom> mergedAtoms = new ArrayList<IAtom>();
		List<IAtomContainer> containers = new ArrayList<IAtomContainer>();
		List<IAtomContainer> droppedContainers = new ArrayList<IAtomContainer>();

		List<List<IBond>> removedBondss = new ArrayList<List<IBond>>();
		List<Map<IBond, Integer>> bondsWithReplacedAtoms = new ArrayList<Map<IBond, Integer>>();
		List<IAtom> mergedPartnerAtoms = new ArrayList<IAtom>();
		List<IAtom> atomsToUpdate = new ArrayList<>();


        if (mergeMap.isEmpty())
            return;

		// Done shifting, now the actual merging
		for (Map.Entry<IAtom,IAtom> e : mergeMap.entrySet()) {
			List<IBond> removedBonds = new ArrayList<IBond>();
			Map<IBond, Integer> bondsWithReplacedAtom = new HashMap<IBond, Integer>();
			IAtom atomRemoved = e.getKey();
			IAtom atomMerged = e.getValue();

			mergedAtoms.add(atomRemoved);
			mergedPartnerAtoms.add(atomMerged);

			IAtomContainer container1 = ChemModelManipulator
					.getRelevantAtomContainer(chemModel, atomRemoved);
			containers.add(container1);

			IAtomContainer container2 = ChemModelManipulator
					.getRelevantAtomContainer(chemModel, atomMerged);

			// If the atoms are in different atom containers till now, we merge
			// the atom containers first.
			if (container1 != container2) {
				container1.add(container2);
				chemModel.getMoleculeSet().removeAtomContainer(container2);
				droppedContainers.add(container2);
			} else {
				droppedContainers.add(null);
			}

			// Handle the case of a bond between mergedAtom and mergedPartnerAtom.
			// This bond should be removed.
			IBond rb = container1.getBond(atomRemoved, atomMerged);
			if (rb != null) {
				container1.removeBond(rb);
				removedBonds.add(rb);
			}

			// In the next loop we remove bonds that are redundant, that is
			// to say bonds that exist on both sides of the parts to be merged
			// and would cause duplicate bonding in the end result.
			for (IAtom atom : container1.atoms()) {

				if (!atom.equals(atomRemoved)) {
					if (container1.getBond(atomRemoved, atom) != null) {
						if (model.getMerge().containsKey(atom)) {
							for (IAtom atom2 : container2.atoms()) {
								if (!atom2.equals(atomMerged)) {
									if (container1.getBond(atomMerged,
											atom2) != null) {
										if (model.getMerge().get(atom).equals(
												atom2)) {
											IBond redundantBond = container1
													.getBond(atom, atomRemoved);
											container1
													.removeBond(redundantBond);
											removedBonds.add(redundantBond);
										}
									}
								}
							}
						}
					}
				}
			}

			// remove multi-edges
			if (container1.equals(container2)) {
				for (IBond bond : atomRemoved.bonds()) {
					IAtom nbor = bond.getOther(atomRemoved);
					for (IBond bond2 : bond.getOther(atomRemoved).bonds()) {
						if (bond2.getOther(nbor).equals(atomMerged)) {
							container2.removeBond(bond2);
							removedBonds.add(bond2);
							atomsToUpdate.add(nbor);
							break;
						}
					}
				}
			}

			removedBondss.add(removedBonds);

			// After the removal of redundant bonds, the actual merge is done.
			// One half of atoms in the merge map are removed and their bonds
			// are mapped to their replacement atoms.
			for (IBond bond : container1.getConnectedBondsList(atomRemoved)) {
				if (bond.getBegin().equals(atomRemoved)) {
					bond.setAtom(atomMerged, 0);
					bondsWithReplacedAtom.put(bond, 0);
				} else if (bond.getEnd().equals(atomRemoved)) {
					bond.setAtom(atomMerged, 1);
					bondsWithReplacedAtom.put(bond, 1);
				} else {
					ControllerHub.log.warn("Bond did not contain the atom it was adjacent to?");
				}
			}
			container1.removeAtom(atomRemoved);
			updateAtom(atomMerged);
			bondsWithReplacedAtoms.add(bondsWithReplacedAtom);
		}

		for (IAtom atom : atomsToUpdate) {
			updateAtom(atom);
		}

		Map<Integer, Map<Integer, Integer>> oldRGroupHash = null;
		Map<Integer, Map<Integer, Integer>> newRGroupHash = null;

		if (rGroupHandler != null) {
			try {
				oldRGroupHash = rGroupHandler.makeHash();
				rGroupHandler.adjustAtomContainers(chemModel.getMoleculeSet());
				newRGroupHash = rGroupHandler.makeHash();

			} catch (CDKException e) {
				unsetRGroupHandler();
				for (IAtomContainer atc : droppedContainers) {
					atc.setProperty(CDKConstants.TITLE, null);
				}
			}
		}

		// Undo section to undo/redo the merge
		IUndoRedoFactory factory = getUndoRedoFactory();
		UndoRedoHandler handler = getUndoRedoHandler();
		if (movedDistance != null && factory != null && handler != null) {

			// we look if anything has been moved which was not merged
			IAtomContainer undoRedoSet = getIChemModel().getBuilder()
					.newInstance(IAtomContainer.class);

			if (renderer.getRenderer2DModel().getSelection()
					.getConnectedAtomContainer() != null) {
				undoRedoSet.add(renderer.getRenderer2DModel()
						.getSelection().getConnectedAtomContainer());
			}

			Iterator<IAtom> it2 = mergeMap.keySet().iterator();
			while (it2.hasNext()) {
				IAtom remove = it2.next();
				undoRedoSet.removeAtom(remove);
			}
			IUndoRedoable moveundoredo = getUndoRedoFactory().getMoveAtomEdit(
					undoRedoSet, movedDistance, "Move atom");
			IUndoRedoable undoredo = factory.getMergeMoleculesEdit(mergedAtoms,
					containers, droppedContainers, removedBondss,
					bondsWithReplacedAtoms, movedDistance, mergedPartnerAtoms,
					moveundoredo, oldRGroupHash, newRGroupHash,
					"Move and merge atoms", this);
			handler.postEdit(undoredo);

		}

		model.getMerge().clear();
		structureChanged();
		updateView();
	}

	// OK
	public void setValence(IAtom atom, Integer newValence) {
		if (getUndoRedoFactory() != null && getUndoRedoHandler() != null) {
			IUndoRedoable undoredo = getUndoRedoFactory().getChangeValenceEdit(
					atom, atom.getValency(), newValence,
					"Change valence to " + newValence, this);
			getUndoRedoHandler().postEdit(undoredo);
		}
		if (!(atom instanceof IPseudoAtom)) {
			atom.setValency(newValence);
		}
		updateAtom(atom);
		structurePropertiesChanged();
	}

	public void addChangeModeListener(IChangeModeListener listener) {
		changeModeListeners.add(listener);
	}

	public void removeChangeModeListener(IChangeModeListener listener) {
		changeModeListeners.remove(listener);
	}

	public void setFallbackModule (IControllerModule m) {
		this.fallbackModule = m;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#removeBondAndLoneAtoms
	 * (org.openscience.cdk.interfaces.IBond)
	 */
	public void removeBondAndLoneAtoms(IBond bondToRemove) {

		IAtomContainer container = ChemModelManipulator
				.getRelevantAtomContainer(chemModel, bondToRemove.getAtom(0));
		AtomBondSet undoRedoSet = new AtomBondSet();
		undoRedoSet.add(bondToRemove);

		removeBondWithoutUndo(bondToRemove);

		if (container != null) {
			for (int i = 0; i < 2; i++) {
				if (container.getConnectedBondsCount(bondToRemove.getAtom(i)) == 0) {
					removeAtomWithoutUndo(bondToRemove.getAtom(i));
					undoRedoSet.add(bondToRemove.getAtom(i));
				}
			}
		}
		removeEmptyContainers(chemModel);
		IUndoRedoable undoredo = getUndoRedoFactory()
				.getRemoveAtomsAndBondsEdit(chemModel, undoRedoSet,
						"Delete Bond", this);
		getUndoRedoHandler().postEdit(undoredo);

		if(rGroupHandler!=null && !rGroupHandler.checkRGroupOkayForDelete(undoRedoSet, this)) {
			undoredo.undo();
			return;
		}

	}

    private static final Pattern ATTACH_POINT_REGEX = Pattern.compile("_AP([1-9])");

	// OK
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.openscience.cdk.controller.IChemModelRelay#convertToPseudoAtom(org
	 * .openscience.cdk.interfaces.IAtom, java.lang.String)
	 */
	public IPseudoAtom convertToPseudoAtom(IAtom atom, String label) {
        IPseudoAtom pseudo = makePseudoAtom(label, atom.getPoint2d());

        // attachment points only replace atoms if they have a single
        // bond attached, else we sprout off a new atom
        if (pseudo.getAttachPointNum() != 0 && atom.getBondCount() != 1) {
            addAtom(label, atom, true);
        } else {
            replaceAtom(pseudo, atom);
        }
		return pseudo;
	}

    private IPseudoAtom makePseudoAtom(String label, Point2d p) {
        IPseudoAtom pseudo = chemModel.getBuilder()
                                      .newInstance(IPseudoAtom.class);
        pseudo.setAtomicNumber(IAtom.Wildcard);
        pseudo.setImplicitHydrogenCount(0);
        Matcher matcher = ATTACH_POINT_REGEX.matcher(label);
        if (matcher.matches()) {
            pseudo.setAttachPointNum(Integer.parseInt(matcher.group(1)));
        } else {
            pseudo.setLabel(label);
        }
        pseudo.setPoint2d(p);
        return pseudo;
    }


    public void moveTo(IAtom atom, Point2d from, Point2d to, boolean finished) {

        if (!altMode && atom.getBondCount() == 1) {
            IBond bond = atom.bonds().iterator().next();
            IAtom nbor = bond.getOther(atom);

            Vector2d a = new Vector2d(from.x - nbor.getPoint2d().x,
                                      from.y - nbor.getPoint2d().y);
            Vector2d b = new Vector2d(to.x - nbor.getPoint2d().x,
                                      to.y - nbor.getPoint2d().y);
            double angle = Math.atan2(a.x*b.y - a.y*b.x, a.x*b.x + a.y*b.y);

            double snapAngle = (Math.PI/12) * Math.round(angle / (Math.PI/12));

            double cos = Math.cos(snapAngle);
            double sin = Math.sin(snapAngle);

            double x = a.x * cos - a.y * sin;
            double y = a.x * sin + a.y * cos;
            Vector2d c = new Vector2d(x, y);

            to.x = nbor.getPoint2d().x + c.x;
            to.y = nbor.getPoint2d().y + c.y;
        }

        moveToWithoutUndo(atom, to);

        if (finished) {
            IAtomContainer undoRedoSet = chemModel.getBuilder().newInstance(IAtomContainer.class);
            undoRedoSet.addAtom(atom);
            IUndoRedoable undoredo = getUndoRedoFactory().getMoveAtomEdit(
                    undoRedoSet, new Vector2d(to.x - from.x, to.y - from.y),
                    "Move atom(s)");
            getUndoRedoHandler().postEdit(undoredo);
        }
    }

    // OK
	public void moveBy(Collection<IAtom> atoms, Vector2d move,
			Vector2d totalmove) {
		if (totalmove != null && getUndoRedoFactory() != null
				&& getUndoRedoHandler() != null) {
			IAtomContainer undoRedoSet = chemModel.getBuilder()
					.newInstance(IAtomContainer.class);
			for (IAtom atom : atoms) {
				undoRedoSet.addAtom(atom);
			}
			IUndoRedoable undoredo = getUndoRedoFactory().getMoveAtomEdit(
					undoRedoSet, totalmove, "Move atom");
			getUndoRedoHandler().postEdit(undoredo);
		}
		if (move != null) {
			for (IAtom atom : atoms) {
				Point2d newpoint = new Point2d(atom.getPoint2d());
				newpoint.add(move);
				moveToWithoutUndo(atom, newpoint);
			}
		}
	}

	public IChemModel getChemModel() {
		return chemModel;
	}

	/**
	 * Sets the mouse cursor shown on the renderPanel.
	 *
	 * @param cursor One of the constants from java.awt.Cursor.
	 */
	public void setCursor(int cursor) {
		eventRelay.setCursor(new Cursor(cursor));
	}

	public void setCursor(CursorType type) {
		switch (type) {
			case DEFAULT:
				setCursor(Cursor.DEFAULT_CURSOR);
				break;
			case MOVE:
				setCursor(Cursor.HAND_CURSOR);
				break;
			case ROTATE:
				eventRelay.setCursor(rotateCursor);
				break;
			case RESIZE_N:
				setCursor(Cursor.N_RESIZE_CURSOR);
				break;
			case RESIZE_NE:
				setCursor(Cursor.NE_RESIZE_CURSOR);
				break;
			case RESIZE_E:
				setCursor(Cursor.E_RESIZE_CURSOR);
				break;
			case RESIZE_SE:
				setCursor(Cursor.SE_RESIZE_CURSOR);
				break;
			case RESIZE_S:
				setCursor(Cursor.S_RESIZE_CURSOR);
				break;
			case RESIZE_SW:
				setCursor(Cursor.SW_RESIZE_CURSOR);
				break;
			case RESIZE_W:
				setCursor(Cursor.W_RESIZE_CURSOR);
				break;
			case RESIZE_NW:
				setCursor(Cursor.NW_RESIZE_CURSOR);
				break;
		}
	}

	/**
	 * Tells the mouse cursor shown on the renderPanel.
	 *
	 * @return One of the constants from java.awt.Cursor.
	 */
	public int getCursor() {
		return eventRelay.getCursor().getType();
	}

	/**
	 * Tells the molecular formula of the model. This includes all fragments
	 * currently displayed and all their implicit and explicit Hs.
	 *
	 * @return The formula.
	 */
	public String getFormula() {
		IMolecularFormula wholeModel = getIChemModel().getBuilder()
				.newInstance(IMolecularFormula.class);
		Iterator<IAtomContainer> containers = ChemModelManipulator
				.getAllAtomContainers(chemModel).iterator();
		int implicitHs = 0;
		while (containers.hasNext()) {
			for (IAtom atom : containers.next().atoms()) {
				wholeModel.addIsotope(atom);
				if (atom.getImplicitHydrogenCount() != null) {
					implicitHs += atom.getImplicitHydrogenCount();
				}
			}
		}
		try {
			if (implicitHs > 0)
				wholeModel
						.addIsotope(XMLIsotopeFactory.getInstance(
								wholeModel.getBuilder()).getMajorIsotope(1),
								implicitHs);
		} catch (IOException e) {
			// do nothing
		}
		return MolecularFormulaManipulator.getHTML(wholeModel, true, false);
	}



	public RGroupHandler getRGroupHandler() {
		return rGroupHandler;
	}

	/**
	 * See unsetRGroupHandler() to nullify the R-group aspects.
	 */
	public void setRGroupHandler(RGroupHandler rGroupHandler) {
		ControllerHub.rGroupHandler = rGroupHandler;
	}

	public void unsetRGroupHandler() {
		ControllerHub.rGroupHandler = null;
		if (chemModel.getMoleculeSet()!=null)
			for (IAtomContainer atc : chemModel.getMoleculeSet().atomContainers()) {
				atc.removeProperty(CDKConstants.TITLE);
			}
	}


	private void adjustRgroup() {
		if (rGroupHandler != null) {
			try {
				rGroupHandler.adjustAtomContainers(chemModel.getMoleculeSet());
			} catch (CDKException e) {
				unsetRGroupHandler();
				//e.printStackTrace();
			}
		}
	}

    public void setPhantomArrow(Point2d start, Point2d end) {
        this.phantomArrowStart=start;
        this.phantomArrowEnd=end;
    }

    public Point2d[] getPhantomArrow() {
        return new Point2d[]{phantomArrowStart, phantomArrowEnd};
    }

	@Override
	public void setPhantomText(String text, Point2d position) {
		this.phantomText = text;
		this.phantomTextPosition = position;
	}

	public Point2d getPhantomTextPosition() {
		return phantomTextPosition;
	}

	public String getPhantomText() {
		return phantomText;
	}

	@Override
	public void rotate(Map<IAtom,Point2d> atoms, Point2d center, double angle) {

		// by default snap to 10 degrees (PI/18 radians)
		// in alt mode we allow free rotation
		if (!altMode)
			angle = (Math.PI/18) * Math.round(angle / (Math.PI/18));

		/* For more info on the mathematics, see Wiki at
		 * http://en.wikipedia.org/wiki/Coordinate_rotation
		 */
		double cosine = java.lang.Math.cos(angle);
		double sine = java.lang.Math.sin(angle);
		for (Map.Entry<IAtom,Point2d> e : atoms.entrySet()) {
			Point2d refPoint = e.getValue();
			double newX = (refPoint.x * cosine) - (refPoint.y * sine);
			double newY = (refPoint.x * sine) + (refPoint.y * cosine);
			Point2d newCoords = new Point2d(newX + center.x, newY + center.y);
			e.getKey().setPoint2d(newCoords);
		}
	}

	@Override
	public void scale(Map<IAtom,Point2d> atoms, Point2d center, double amount,
					  Scale direction) {

		// in alt mode snap to 1.5 increments
		if (altMode)
			amount = Math.round(2 * amount) / 2d;

		for (Map.Entry<IAtom,Point2d> e : atoms.entrySet()) {
			Point2d refPoint = e.getValue();
			double newX = direction != Scale.Vertical ? (refPoint.x * amount) : refPoint.x;
			double newY = direction != Scale.Horizontal ? (refPoint.y * amount) : refPoint.y;
			Point2d newCoords = new Point2d(newX + center.x, newY + center.y);
			e.getKey().setPoint2d(newCoords);
		}
	}
}

