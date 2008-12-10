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
package org.openscience.jchempaint.action;

import java.awt.event.ActionEvent;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.controller.IChemModelRelay;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.renderer.LogicalSelection;
import org.openscience.cdk.renderer.selection.RectangleSelection;
import org.openscience.cdk.renderer.selection.ShapeSelection;

/**
 * Triggers the invocation of the structure diagram generator
 * 
 */
public class CleanupAction extends JCPAction {

    private static final long serialVersionUID = -1048878006430754582L;
    private StructureDiagramGenerator diagramGenerator;

    /**
     * Constructor for the CleanupAction object
     */
    public CleanupAction() {
        super();
    }

    /**
     * Re-lays out a molecule
     * 
     *@param e
     *            Description of the Parameter
     */
    public void actionPerformed(ActionEvent e) {
        logger.info("Going to perform a clean up...");
        if (diagramGenerator == null) {
            diagramGenerator = new StructureDiagramGenerator();
            diagramGenerator.setTemplateHandler(
                    new TemplateHandler(jcpPanel.getChemModel().getBuilder()));
        }
        
        logger.debug("getting ChemModel");
        
        IChemModel model = jcpPanel.getChemModel();
        logger.debug("got ChemModel");
        IMoleculeSet som = model.getMoleculeSet();
        
        if (som != null) {
            logger.debug("no mols in som: ", som.getMoleculeCount());
            for (IAtomContainer molecule : som.molecules()) {
                if (molecule != null && molecule.getAtomCount() > 0) {
                    IAtomContainer cleanedMol = relayoutMolecule(molecule);
                    for (int j = 0; j < molecule.getAtomCount(); j++) {
                        IAtom atom = molecule.getAtom(j);
                        IAtom newAtom = cleanedMol.getAtom(j);
                        Point2d newCoord = newAtom.getPoint2d();
                        atom.setPoint2d(newCoord);
                    }
                }
            }
        }
        
        IReactionSet reactionSet = model.getReactionSet();
        if (reactionSet != null) {
            for (IReaction reaction : reactionSet.reactions()) {
                for (IAtomContainer molecule : reaction.getReactants()
                        .atomContainers()) {
                    IAtomContainer cleanedMol = relayoutMolecule(molecule);
                    for (int j = 0; j < molecule.getAtomCount(); j++) {
                        IAtom atom = molecule.getAtom(j);
                        IAtom newAtom = cleanedMol.getAtom(j);
                        Point2d newCoord = newAtom.getPoint2d();
                        atom.setPoint2d(newCoord);
                    }
                }
                for (IAtomContainer molecule : reaction.getProducts()
                        .atomContainers()) {
                    IAtomContainer cleanedMol = relayoutMolecule(molecule);
                    for (int j = 0; j < molecule.getAtomCount(); j++) {
                        IAtom atom = molecule.getAtom(j);
                        IAtom newAtom = cleanedMol.getAtom(j);
                        Point2d newCoord = newAtom.getPoint2d();
                        atom.setPoint2d(newCoord);
                    }
                }
            }
        }
        
        IChemModelRelay hub = jcpPanel.get2DHub(); 
        hub.getIJava2DRenderer().getRenderer2DModel().setSelection(new LogicalSelection(LogicalSelection.Type.NONE));
        jcpPanel.setIsNewChemModel(true);
        hub.updateView();
    }

    private IAtomContainer relayoutMolecule(IAtomContainer molecule) {
        if (molecule != null) {
            if (molecule.getAtomCount() > 2) {
                try {
                    double bondLength = GeometryTools.getBondLengthAverage(molecule);
                    diagramGenerator.setBondLength(bondLength);
    
                    // since we will copy the coordinates later anyway, let's
                    // use a NonNotifying data class
                    // XXX this is no longer true?
                    IChemObjectBuilder builder 
                        = NoNotificationChemObjectBuilder.getInstance();
                    
                    diagramGenerator.setMolecule(builder.newMolecule(molecule));
                    diagramGenerator
                            .generateExperimentalCoordinates(new Vector2d(0, 1));
                    molecule = diagramGenerator.getMolecule();
    
                } catch (Exception exc) {
                    logger.error("Could not generate coordinates for molecule");
                    logger.debug(exc);
                }
            } else {
                logger.info("Molecule with less than 2 atoms are not cleaned up");
            }
        } else {
            logger.error("Molecule is null! Cannot do layout!");
        }
        return molecule;
    }
}
