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

import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.ReactionChain;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void mouseClickedDown(Point2d worldCoordFrom) {
        startPoint = worldCoordFrom;
    }

    @Override
    public void mouseClickedUp(Point2d worldCoord) {
        //first, we get rid of the phantom arrow
        chemModelRelay.setPhantomArrow(null, null);
        chemModelRelay.updateView();
        //for each molecule, we need to look if the majority of
        // its atoms are to the left or right or on the arrow
        IAtomContainerSet moleculeSet = super.chemModelRelay.getChemModel().getMoleculeSet();
        if (moleculeSet.getAtomContainerCount() == 0 || (moleculeSet.getAtomContainerCount() == 1 && moleculeSet.getAtomContainer(0).getAtomCount() == 0)) {
            return;
        }
        //calculate a second point on the perpendicular through start point
        double angle = Math.PI / 2;
        double costheta = Math.cos(angle);
        double sintheta = Math.sin(angle);
        Point2d point = new Point2d(worldCoord);
        Point2d center = new Point2d(startPoint);
        double relativex = point.x - center.x;
        double relativey = point.y - center.y;
        Point2d secondOnStartPerpendicular = new Point2d();
        secondOnStartPerpendicular.x = relativex * costheta - relativey * sintheta + center.x;
        secondOnStartPerpendicular.y = relativex * sintheta + relativey * costheta + center.y;
        //and on the perpendicular through the end
        point = new Point2d(startPoint);
        center = new Point2d(worldCoord);
        relativex = point.x - center.x;
        relativey = point.y - center.y;
        Point2d secondOnEndPerpendicular = new Point2d();
        secondOnEndPerpendicular.x = relativex * costheta - relativey * sintheta + center.x;
        secondOnEndPerpendicular.y = relativex * sintheta + relativey * costheta + center.y;
        
        List<IAtomContainer> reactants = new ArrayList<IAtomContainer>(2);
        List<IAtomContainer> products = new ArrayList<IAtomContainer>(2);
        
        // determine the reactants and products of a reaction
        for (int i = moleculeSet.getAtomContainerCount() - 1; i >= 0; i--) {
            int left = 0;
            int right = 0;
            for (int k = 0; k < moleculeSet.getAtomContainer(i).getAtomCount(); k++) {
                if (BondTools.giveAngleBothMethods(startPoint, secondOnStartPerpendicular, moleculeSet.getAtomContainer(i).getAtom(k).getPoint2d(), false) < 0) {
                    right++;
                } else {
                    left++;
                }
            }
            int leftend = 0;
            int rightend = 0;
            for (int k = 0; k < moleculeSet.getAtomContainer(i).getAtomCount(); k++) {
                if (BondTools.giveAngleBothMethods(secondOnEndPerpendicular, worldCoord, moleculeSet.getAtomContainer(i).getAtom(k).getPoint2d(), false) < 0) {
                    rightend++;
                } else {
                    leftend++;
                }
            }

            if (left > right) {
                //is a reactant
                reactants.add(moleculeSet.getAtomContainer(i));
            }
            if (rightend > leftend) {
                //is a product
                products.add(moleculeSet.getAtomContainer(i));
            } else {
                //is a catalyst
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
            throw new InternalError("Could not clone IAtomContainer: ", e);
        }
    }

    /**
     * On mouse drag, quasi-3D rotation around the center is done
     * (It isn't real 3D rotation because of truncation of transformation
     * matrix to 2x2)
     */
    @Override
    public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo) {
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
