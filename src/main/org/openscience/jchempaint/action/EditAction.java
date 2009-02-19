/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Tobias Helmus, Stefan Kuhn
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.MoveModule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.LogicalSelection;
import org.openscience.cdk.renderer.selection.RectangleSelection;
import org.openscience.cdk.renderer.selection.ShapeSelection;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.jchempaint.action.CopyPasteAction.JcpSelection;

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
 */
public class EditAction extends JCPAction {

	private static final long serialVersionUID = -1051272879400028225L;

	public void actionPerformed(ActionEvent event) {
		// learn some stuff about event
		logger.debug("Event source: ", event.getSource().getClass().getName());
		logger.debug("  IChemObject: ", getSource(event));

		RendererModel renderModel 
		    = jcpPanel.get2DHub().getRenderer().getRenderer2DModel();
		IChemModel chemModel = jcpPanel.getChemModel();
		if (type.equals("cut")) {
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
		}
		else if (type.equals("cutSelected")) {
			IAtomContainer undoRedoContainer = chemModel.getBuilder().newAtomContainer();
			logger.debug("Deleting all selected atoms...");
			if (renderModel.getSelection().getConnectedAtomContainer() == null || renderModel.getSelection().getConnectedAtomContainer().getAtomCount() == 0) {
				JOptionPane.showMessageDialog(jcpPanel, "No selection made. Please select some atoms first!", "Error warning", JOptionPane.WARNING_MESSAGE);
			}
			else {
				IAtomContainer selected = renderModel.getSelection().getConnectedAtomContainer();
				try{
		            Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		            IAtomContainer tocopyclone=(IAtomContainer)selected.clone();
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
			renderModel.setSelection(new LogicalSelection(LogicalSelection.Type.NONE));
		}
		else if (type.equals("selectAll")) {
		    IChemObjectSelection allSelection = 
		        new LogicalSelection(LogicalSelection.Type.ALL);
		    allSelection.select(jcpPanel.getChemModel());
		    renderModel.setSelection(allSelection);
			jcpPanel.setMoveAction();
			ControllerHub hub = jcpPanel.get2DHub(); 
			hub.setActiveDrawModule(new MoveModule(hub));
		} else if (type.equals("selectMolecule")) {
			IChemObject object = getSource(event);
			IAtomContainer relevantAtomContainer=null;
			if (object instanceof Atom) {
				relevantAtomContainer = ChemModelManipulator.getRelevantAtomContainer(jcpPanel.getChemModel(),(Atom)object);
			} else if (object instanceof org.openscience.cdk.interfaces.IBond) {
				relevantAtomContainer = ChemModelManipulator.getRelevantAtomContainer(jcpPanel.getChemModel(),(Bond)object);
			} else {
				logger.warn("selectMolecule not defined for the calling object ", object);
			}
			if(relevantAtomContainer!=null){
	        	ShapeSelection container = new RectangleSelection();
	        	for(IAtom atom:relevantAtomContainer.atoms()){
	        		container.atoms.add(atom);
	        	}
	        	for(IBond bond:relevantAtomContainer.bonds()){
	        		container.bonds.add(bond);
	        	}
				renderModel.setSelection(container);
			}
		} else if (type.equals("selectFromChemObject")) {
			// FIXME: implement for others than Reaction, Atom, Bond
			IChemObject object = getSource(event);
			if (object instanceof Atom) {
				ShapeSelection container = new RectangleSelection();
				container.atoms.add((Atom) object);
				renderModel.setSelection(container);
			}
			else if (object instanceof org.openscience.cdk.interfaces.IBond) {
				ShapeSelection container = new RectangleSelection();
				container.bonds.add((Bond) object);
				renderModel.setSelection(container);
			}
			else if (object instanceof Reaction) {
				IAtomContainer wholeModel = jcpPanel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = ReactionManipulator.getAllAtomContainers((Reaction)object).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
	        	ShapeSelection container = new RectangleSelection();
	        	for(IAtom atom:wholeModel.atoms()){
	        		container.atoms.add(atom);
	        	}
	        	for(IBond bond:wholeModel.bonds()){
	        		container.bonds.add(bond);
	        	}
				renderModel.setSelection(container);
			}
			else {
				logger.warn("Cannot select everything in : ", object);
			}
		}
		else if (type.equals("selectReactionReactants")) {
			IChemObject object = getSource(event);
			if (object instanceof Reaction) {
				Reaction reaction = (Reaction) object;
				IAtomContainer wholeModel = jcpPanel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = MoleculeSetManipulator.getAllAtomContainers(reaction.getReactants()).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
	        	ShapeSelection container = new RectangleSelection();
	        	for(IAtom atom:wholeModel.atoms()){
	        		container.atoms.add(atom);
	        	}
	        	for(IBond bond:wholeModel.bonds()){
	        		container.bonds.add(bond);
	        	}
				renderModel.setSelection(container);
			}
			else {
				logger.warn("Cannot select reactants from : ", object);
			}
		}
		else if (type.equals("selectReactionProducts")) {
			IChemObject object = getSource(event);
			if (object instanceof Reaction) {
				Reaction reaction = (Reaction) object;
				IAtomContainer wholeModel = jcpPanel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = MoleculeSetManipulator.getAllAtomContainers(reaction.getProducts()).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
	        	ShapeSelection container = new RectangleSelection();
	        	for(IAtom atom:wholeModel.atoms()){
	        		container.atoms.add(atom);
	        	}
	        	for(IBond bond:wholeModel.bonds()){
	        		container.bonds.add(bond);
	        	}
				renderModel.setSelection(container);
			}
			else {
				logger.warn("Cannot select reactants from : ", object);
			}
		}
		else {
			logger.warn("Unsupported EditAction: " + type);
		}
		jcpPanel.get2DHub().updateView();
	}

}

