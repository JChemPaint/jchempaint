/*
 * Copyright (C) 2009 Stefan Kuhn <shk3@users.sourceforge.net>
 *
 * Contact: cdk-jchempaint@lists.sourceforge.net
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

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.ReactionChain;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.List;

public class ReactionArrowModule extends ControllerModuleAdapter {

    protected static ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(ReactionArrowModule.class);
    Point2d startPoint = null;
    protected String ID;

    /**
     * Constructor
     * @param chemModelRelay
     */
    public ReactionArrowModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
        logger.debug("constructor");
    }

    @Override
    public void mouseClickedDown(Point2d worldCoordFrom, int modifiers) {
        startPoint = worldCoordFrom;
    }

    @Override
    public void mouseClickedUp(Point2d endPoint, int modifiers) {
        //first, we get rid of the phantom arrow
        chemModelRelay.setPhantomArrow(null, null);
        chemModelRelay.updateView();
        //for each molecule, we need to look if the majority of
        // its atoms are to the left or right or on the arrow
        IAtomContainerSet moleculeSet = super.chemModelRelay.getChemModel().getMoleculeSet();
        if (moleculeSet.getAtomContainerCount() == 0 || (moleculeSet.getAtomContainerCount() == 1 && moleculeSet.getAtomContainer(0).getAtomCount() == 0)) {
            return;
        }
        
        final Vector2d unit = newUnitVector(startPoint, endPoint);
        final Vector2d perp = new Vector2d(unit.y, -unit.x);
        
        List<IAtomContainer> reactants = new ArrayList<IAtomContainer>(2);
        List<IAtomContainer> products = new ArrayList<IAtomContainer>(2);
        
        // determine the reactants and products of a reaction we do this by
        // taking the perpendicular vector and testing where atoms lie with
        // respect to the start and end of the arrow. The following diagram
        // may help visualise what the startPos and endPos values are.
        //
        // startPos < 0   startPos > 0
        //                       endPost < 0   endPost > 0
        //              ^                    ^
        //              |                    |
        //              --------------------->
        //
        for (IAtomContainer container : moleculeSet.atomContainers()) {
            
            // count the number of atoms before and after the arrow for this molecule
            int beforeStart = 0;
            int afterEnd = 0;
                       
            for (final IAtom atom : container.atoms()){
                final Point2d p = atom.getPoint2d(); 
                
                // sign( (Bx-Ax)*(Y-Ay) - (By-Ay)*(X-Ax) )
                final double endPos   = (perp.x) * (p.y - endPoint.y) - (perp.y) * (p.x - endPoint.x);
                final double startPos = (perp.x) * (p.y - startPoint.y) - (perp.y) * (p.x - startPoint.x);

                if (endPos > 0)
                    afterEnd++;
                if (startPos < 0)
                    beforeStart++;
            }

            if (beforeStart > afterEnd) {
                reactants.add(container);
            }
            else if (beforeStart < afterEnd) {
                products.add(container);
            } else {
                //TODO catalysts in general
            }
        }
        
        // don't create a new reaction
        if (reactants.isEmpty() && products.isEmpty())
            return;
        
        // do reaction creation
        IReactionSet reactionSet = super.chemModelRelay.getChemModel().getReactionSet();
        if (reactionSet == null) {
            reactionSet = new ReactionChain(); //reactionSet = super.chemModelRelay.getChemModel().getBuilder().newInstance(IReactionSet.class);
            super.chemModelRelay.getChemModel().setReactionSet(reactionSet);
        }
        IReaction reaction = moleculeSet.getBuilder().newInstance(IReaction.class);
        ((ReactionChain) reactionSet).addReaction(reaction, reactionSet.getReactionCount()); //reactionSet.addReaction(reaction);
        reaction.setID(ReactionHub.newReactionId(chemModelRelay));
        
        for (IAtomContainer reactant : reactants) {
            ReactionHub.makeReactantInExistingReaction((ControllerHub) super.chemModelRelay,
                                                       reaction.getID(),
                                                       cloneReactionParticipant(reactant),
                                                       reactant);
        }
        for (IAtomContainer product : products) {
            ReactionHub.makeProductInExistingReaction((ControllerHub) super.chemModelRelay,
                                                      reaction.getID(),
                                                      cloneReactionParticipant(product),
                                                      product);
        }
        
        
    }
    
    static Vector2d newUnitVector(Point2d a, Point2d b) {
        Vector2d v = new Vector2d(b.x - a.x,
                                  b.y - a.y);
        v.normalize();
        return v;
    }
    
    IAtomContainer cloneReactionParticipant(IAtomContainer org) {
        try {
            IAtomContainer cpy = (IAtomContainer) org.clone();
            if (org.getID() != null) {
                cpy.setID(org.getID());
            } else {
                cpy.setID("ac" + System.currentTimeMillis());
            }
            return cpy;
        } catch (CloneNotSupportedException e) {
            // never going to happen (should be suppressed)
            logger.error("Could not clone IAtomContainer: ", e.getMessage());
            logger.debug(e);
            throw new InternalError("Could not clone IAtomContainer: " + e.getMessage());
        }
    }

    /**
     * On mouse drag, quasi-3D rotation around the center is done
     * (It isn't real 3D rotation because of truncation of transformation
     * matrix to 2x2)
     */
    @Override
    public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo, int modifiers) {
        chemModelRelay.setPhantomArrow(startPoint, worldCoordTo);
        chemModelRelay.updateView();
    }

    public String getDrawModeString() {
        return "Draw a reaction Arrow";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
