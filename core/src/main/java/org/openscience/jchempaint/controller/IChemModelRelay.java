/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-09-02 11:46:10 +0100 (su, 02 sep 2007) $
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.lists.sf>
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

import java.io.IOException;
import java.util.Collection;

import javax.vecmath.Point2d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IBond.Stereo;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.IRenderer;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.IncrementalSelection;
import org.openscience.jchempaint.rgroups.RGroupHandler;

/**
 * @cdk.module control
 */
public interface IChemModelRelay extends IAtomBondEdits {

    public enum Direction { UP, DOWN, UNDEFINED, EZ_UNDEFINED };

    /* Interaction*/
    public IControllerModel getController2DModel();
    public IRenderer getRenderer();
    public IChemModel getIChemModel();
    public void setChemModel(IChemModel model);
    public IAtom getClosestAtom(Point2d worldCoord);
    public IBond getClosestBond(Point2d worldCoord);
    
    /**
     * Get the closest atom that is in 'range' (highlight distance) of the
     * atom 'atom', ignoring all the atoms in the collection 'toIgnore'.
     * 
     * @param toIgnore the atoms to ignore in the search
     * @param atom the atom to use as the base of the search
     * @return the closest atom that is in highlight distance
     */
    public IAtom getAtomInRange(Collection<IAtom> toIgnore, IAtom atom);
    
    
    /**
     * Find the atom closest to 'atom', exclusind the atom itself.
     * 
     * @param atom the atom around which to search
     * @return the nearest atom other than 'atom'
     */
    public IAtom getClosestAtom(IAtom atom);
    
    public void updateView();
    
    /**
     * Sets the active controller module.
     * 
     * @param activeDrawModule The new controller module.^M
     */
    public void setActiveDrawModule(IControllerModule activeDrawModule);
    
    /**
     * Fills an IncrementalSelection and sets it as selction in the model.
     *
     * @param selection The selection to fill and to set.
     */
    public void select(IncrementalSelection selection);
    
    /**
     * Sets a selection as selection in the model.
     * 
     * @param selection The selection to set.
     */
    public void select(IChemObjectSelection selection);

    /* Event model */
    public void setEventHandler(IChemModelEventRelayHandler handler);
    public void fireZoomEvent();
    public void fireStructureChangedEvent();
    /**
     * Adds an temporary atom which might be cleared later, when the final
     * atom is added. Controllers can use this to draw temporary atoms, for
     * example while drawing new bonds.
     *
     * @param atom atom to add as phantom
     */
    public void addPhantomAtom(IAtom atom);
    
    /**
     * Sets a phantom = temporary arrow, e. g. for reaction drawing.
     * 
     * @param start The start point.
     * @param end   The end point (if startt and end are null, no arrow is drawn).
     */
    public void setPhantomArrow(Point2d start, Point2d end);
    
    /**
     * Gets start and end for a phantom arrow.
     * 
     * @return [0]=start, [1]=end. Can be null, nothing to be done then.
     */
    public Point2d[] getPhantomArrow();
    /**
     * Adds an temporary bond which might be cleared later, when the final
     * bond is added. Controllers can use this to draw temporary bonds, for
     * example while drawing new bonds.
     *
     * @param bond bond to add as phantom
     */
    public void addPhantomBond(IBond bond);
	/**
	 * Replaces the phantom bonds/atoms with this atomcontainer.
	 * 
	 * @param phantoms The new phantoms
	 */
	public void setPhantoms(IAtomContainer phantoms);
	/**
	 * Sets a text to be rendered as a phantom at a certain position.
	 * 
	 * @param text The text.
	 * @param position The position.
	 */
	public void setPhantomText(String text, Point2d position);
	/**
     * Returns an IAtomContainer containing all phantom atoms and bonds.
     */
    public IAtomContainer getPhantoms();
    /**
     * Deletes all temporary atoms.
     */
    public void clearPhantoms();

    /* Editing actions for the complete model */
    public void updateImplicitHydrogenCounts();
    public void zap();
    public IRing addRing(int size, Point2d worldcoord, boolean undoable);
    /**
     * Adds a ring to an atom.
     * 
     * @param atom    The atom to attach to.
     * @param size    The size of the new ring.
     * @param phantom Should this become a phantom bond or real one?
     * @return The new bond.
     */
    public IRing addRing(IAtom atom, int size, boolean phantom);
    /**
     * Adds a phenyl ring to an atom.
     * 
     * @param atom    The atom to attach to.
     * @param phantom Should this become a phantom bond or real one?
     * @return The new bond.
     */
    public IRing addPhenyl(IAtom atom, boolean phantom);
    public IRing addPhenyl(Point2d worldcoord, boolean undoable);
    /**
     * Adds a ring to an bond.
     * 
     * @param bond    The bond to attach to.
     * @param size    The size of the new ring.
     * @param phantom Should this become a phantom bond or real one?
     * @return The new bond.
     */
    public IRing addRing(IBond bond, int size, boolean phantom);
    /**
     * Adds a phenyl ring to an atom.
     * 
     * @param bond    The bond to attach to.
     * @param phantom Should this become a phantom bond or real one?
     * @return The new bond.
     */
    public IRing addPhenyl(IBond bond, boolean phantom);
    /**
     * Adds a fragment to the chemmodel.
     * 
     * @param toPaste The fragment to add.
     * @param moleculeToAddTo If null, a new molecule in the setOfMolecules will be made, if not null, it will be added to moleculeToAddTo.
     * @param toRemove If not null, this atomcontainer will be added to moleculeToAddTo as well and removed from chemmodel (this is needed if a merge happens).
     */
    public void addFragment(IAtomContainer toPaste, IAtomContainer moleculeToAddTo, IAtomContainer toRemove);
    public IAtomContainer deleteFragment(IAtomContainer toDelete);
    public void cleanup();
    public void flip(boolean horizontal);
    public void invertStereoInSelection();
    /**
     * Adjusts all bond orders to fit valency
     */
    public void adjustBondOrders() throws IOException, ClassNotFoundException, CDKException;
    /**
     * Sets all bond order to single
     */
    public void resetBondOrders();
    public void clearValidation();
    public void makeAllImplicitExplicit();
    public void makeAllExplicitImplicit();
//    public  void cleanupSelection(Selector sectionIdentifier);

    public IUndoRedoFactory getUndoRedoFactory();
    public UndoRedoHandler getUndoRedoHandler();
    public IBond addBond(IAtom fromAtom, IAtom toAtom, Stereo stereo, Order order);
    public IBond addBond(IAtom fromAtom, IAtom toAtom, IBond.Stereo stereo);
    //public IAtom addAtomWithoutUndo(String drawElement, IAtom newAtom, Stereo stereoForNewBond, boolean makePseudoAtom);
    public IAtom addAtomWithoutUndo(String drawElement, IAtom newAtom,
        Stereo stereo, Order order, boolean makePseudoAtom);
    public void setValence(IAtom atom, Integer newValence);
    public IAtom addAtom(String drawElement, int drawIsotopeNumber, Point2d start, boolean makePseudoAtom);
    public void removeBondAndLoneAtoms(IBond bond);
    public IAtom convertToPseudoAtom(IAtom newAtom, String drawElement);

    public void setCursor(int cursor);
    public int getCursor();
    public IChemModel getChemModel();
    public RGroupHandler getRGroupHandler();
	public void setRGroupHandler(RGroupHandler rGroupHandler);
	public void unsetRGroupHandler();

}
