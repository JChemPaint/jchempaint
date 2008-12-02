/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-05-01 20:15:34 +0100 (Tue, 01 May 2007) $
 *  $Revision: 8292 $
 *
 *  Copyright (C) 2003-2007  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.controller.MoveModule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * This class implements editing options from the 'Edit' menu.
 * These actions are implemented:
 * <ul>
 *   <li>cut, deletes all atoms and connected electron containers
 *   <li>cutSelected, deletes all selected atoms and electron containers
 *   <li>selectAll, selects all atoms and electron containers
 *   <li>selectFromChemObject,selects all atoms and electron containers in
 *       the IChemObject set in the event source
 * </ul>
 *
 * @author        hel
 * @cdk.created       27. April 2005
 * @cdk.module    jchempaint
 */
public class EditAction extends JCPAction {

	private static final long serialVersionUID = -1051272879400028225L;

	public void actionPerformed(ActionEvent event) {
		// learn some stuff about event
		logger.debug("Event source: ", event.getSource().getClass().getName());
		logger.debug("  IChemObject: ", getSource(event));

		Renderer2DModel renderModel = jcpPanel.get2DHub().getIJava2DRenderer().getRenderer2DModel();
		org.openscience.cdk.interfaces.IChemModel chemModel = jcpPanel.getChemModel();
		/*if (type.equals("cut")) {
			org.openscience.cdk.interfaces.IAtom atomInRange = null;
			IChemObject object = getSource(event);
			logger.debug("Source of call: ", object);
			if (object instanceof Atom) {
				atomInRange = (Atom) object;
			}
			else {
				atomInRange = renderModel.getHighlightedAtom();
			}
			if (atomInRange != null) {
				try{
		            Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		            IAtomContainer tocopyclone=atomInRange.getBuilder().newAtomContainer();
		            tocopyclone.addAtom((IAtom)atomInRange.clone());
		            tocopyclone.getAtom(0).setPoint2d(renderModel.getRenderingCoordinate(atomInRange));
		            JcpSelection jcpselection=new CopyPasteAction().new JcpSelection(tocopyclone);
		            sysClip.setContents(jcpselection,null);
				}catch(Exception ex){
					//shouldn't happen
					ex.printStackTrace();
				}
				ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, atomInRange);
			}
			else {
				org.openscience.cdk.interfaces.IBond bond = renderModel.getHighlightedBond();
				if (bond != null) {
					ChemModelManipulator.removeElectronContainer(chemModel, bond);
				}
			}
			jcpModel.fireChange();
		}
		else if (type.equals("cutSelected")) {
			IAtomContainer undoRedoContainer = chemModel.getBuilder().newAtomContainer();
			logger.debug("Deleting all selected atoms...");
			if (renderModel.getSelectedPart() == null || renderModel.getSelectedPart().getAtomCount() == 0) {
				JOptionPane.showMessageDialog(jcpPanel, "No selection made. Please select some atoms first!", "Error warning", JOptionPane.WARNING_MESSAGE);
			}
			else {
				IAtomContainer selected = renderModel.getSelectedPart();
				try{
		            Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		            IAtomContainer tocopyclone=(IAtomContainer)selected.clone();
		            for(int i=0;i<tocopyclone.getAtomCount();i++){
		            	tocopyclone.getAtom(i).setPoint2d(renderModel.getRenderingCoordinate(selected.getAtom(i)));
		            }
		            JcpSelection jcpselection=new CopyPasteAction().new JcpSelection(tocopyclone);
		            sysClip.setContents(jcpselection,null);
				}catch(Exception ex){
					//shouldn't happen
					ex.printStackTrace();
				}
				logger.debug("Found # atoms to delete: ", selected.getAtomCount());
				for (int i = 0; i < selected.getAtomCount(); i++) {
					undoRedoContainer.add(selected);
					for(int k=0;k<ChemModelManipulator.getRelevantAtomContainer(chemModel,selected.getAtom(i)).getConnectedBondsCount(selected.getAtom(i));k++){
						undoRedoContainer.addBond((IBond)ChemModelManipulator.getRelevantAtomContainer(chemModel,selected.getAtom(i)).getConnectedBondsList(selected.getAtom(i)).get(k));
					}
					ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, selected.getAtom(i));
				}
			}
			renderModel.setSelectedPart(new org.openscience.cdk.AtomContainer());
			RemoveAtomsAndBondsEdit  edit = new RemoveAtomsAndBondsEdit(chemModel,undoRedoContainer,"Cut selected");
            jcpPanel.getUndoSupport().postEdit(edit);
			jcpModel.fireChange();
		}
		else */if (type.equals("selectAll")) {
			IAtomContainer wholeModel = chemModel.getBuilder().newAtomContainer();
        	Iterator containers = ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
        	while (containers.hasNext()) {
        		wholeModel.add((IAtomContainer)containers.next());
        	}
        	List<IAtom> atoms=new ArrayList<IAtom>();
        	for(IAtom atom:wholeModel.atoms()){
        		atoms.add(atom);
        	}
			renderModel.getShapeSelection().atoms.addAll(atoms);
        	List<IBond> bonds=new ArrayList<IBond>();
        	for(IBond bond:wholeModel.bonds()){
        		bonds.add(bond);
        	}
			renderModel.getShapeSelection().bonds.addAll(bonds);
			jcpPanel.getLastActionButton().setBackground(Color.LIGHT_GRAY);
			jcpPanel.setLastActionButton(jcpPanel.getMoveButton());
			jcpPanel.getMoveButton().setBackground(Color.GRAY);
			jcpPanel.get2DHub().setActiveDrawModule(new MoveModule(jcpPanel.get2DHub()));
		} /*else if (type.equals("selectMolecule")) {
			IChemObject object = getSource(event);
			if (object instanceof Atom) {
				renderModel.setSelectedPart(ChemModelManipulator.getRelevantAtomContainer(jcpModel.getChemModel(),(Atom)object));
			} else if (object instanceof org.openscience.cdk.interfaces.IBond) {
				renderModel.setSelectedPart(ChemModelManipulator.getRelevantAtomContainer(jcpModel.getChemModel(),(Bond)object));
			} else {
				logger.warn("selectMolecule not defined for the calling object ", object);
			}
			jcpModel.fireChange();
		} else if (type.equals("selectFromChemObject")) {
			// FIXME: implement for others than Reaction, Atom, Bond
			IChemObject object = getSource(event);
			if (object instanceof Atom) {
				IAtomContainer container = new org.openscience.cdk.AtomContainer();
				container.addAtom((Atom) object);
				renderModel.setSelectedPart(container);
				jcpModel.fireChange();
			}
			else if (object instanceof org.openscience.cdk.interfaces.IBond) {
				IAtomContainer container = new org.openscience.cdk.AtomContainer();
				container.addBond((Bond) object);
				renderModel.setSelectedPart(container);
				jcpModel.fireChange();
			}
			else if (object instanceof Reaction) {
				IAtomContainer wholeModel = jcpModel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = ReactionManipulator.getAllAtomContainers((Reaction)object).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
				renderModel.setSelectedPart(wholeModel);
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select everything in : ", object);
			}
		}
		else if (type.equals("selectReactionReactants")) {
			IChemObject object = getSource(event);
			if (object instanceof Reaction) {
				Reaction reaction = (Reaction) object;
				IAtomContainer wholeModel = jcpModel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = MoleculeSetManipulator.getAllAtomContainers(reaction.getReactants()).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
				renderModel.setSelectedPart(wholeModel);
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select reactants from : ", object);
			}
		}
		else if (type.equals("selectReactionProducts")) {
			IChemObject object = getSource(event);
			if (object instanceof Reaction) {
				Reaction reaction = (Reaction) object;
				IAtomContainer wholeModel = jcpModel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = MoleculeSetManipulator.getAllAtomContainers(reaction.getProducts()).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
				renderModel.setSelectedPart(wholeModel);
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select reactants from : ", object);
			}
		}*/
		else {
			logger.warn("Unsupported EditAction: " + type);
		}
		jcpPanel.get2DHub().updateView();
	}

}

