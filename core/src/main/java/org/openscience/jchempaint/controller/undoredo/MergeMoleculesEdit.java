/* $RCSfile$
 * $Author: gilleain $
 * $Date: 2008-11-26 16:01:05 +0000 (Wed, 26 Nov 2008) $
 * $Revision: 13311 $
 *
 * Copyright (C) 2005-2008 Tobias Helmus, Stefan Kuhn
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.jchempaint.controller.undoredo;

import java.util.List;
import java.util.Map;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * @cdk.module controlextra
 * @cdk.svnrev  $Revision: 10979 $
 */
public class MergeMoleculesEdit  implements IUndoRedoable{
	
    private static final long serialVersionUID = -4093867960954400453L;
    
    private List<IAtom> deletedAtoms;
    private List<List<IBond>> deletedBondss;
    private List<Map<IBond, Integer>> bondsWithReplacedAtoms;
    private IChemModelRelay chemModelRelay;
    private String type;
    private List<IAtomContainer> containers;
    private List<IAtomContainer> droppedContainers;

    private Vector2d offset;
    private List<IAtom> mergedPartnerAtoms;
    private IUndoRedoable moveundoredo;

    Map<Integer,Map<Integer,Integer>> oldRgrpHash;
    Map<Integer,Map<Integer,Integer>> newRgrpHash;
    
	/**
	 * @param deletedAtoms             The atoms which were deleted during the merge.
	 * @param containers               containers participating in the merge
     * @param droppedContainers        containers dropped on merge of 2 separate containers
	 * @param deletedBondss            The bonds which were deleted to merge each individual atom.
	 * @param bondsWithReplacedAtoms   For each atom merge, for each bond in deletedBondss, which of the atoms (0 or 1) in that bond was deletedAtom.
	 * @param offset                   The distance by which the atoms where shifted in merge.
	 * @param mergedPartnerAtoms       The atoms which replace deletedAtoms.
	 * @param moveundoredo             If atoms where moved, but not merged, they go in here, maybe null.
	 * @param type                     The string returned as presentation name.
	 * @param chemModelRelay                     The current chemModelRelay.
	 */

    
    public MergeMoleculesEdit(List<IAtom> deletedAtoms, List<IAtomContainer> containers, List<IAtomContainer> droppedContainers, List<List<IBond>> deletedBondss, List<Map<IBond, Integer>> bondsWithReplacedAtoms, Vector2d offset, List<IAtom> mergedPartnerAtoms, IUndoRedoable moveundoredo, Map<Integer,Map<Integer,Integer>> oldRgrpHash, Map<Integer,Map<Integer,Integer>> newRgrpHash, String type, IChemModelRelay chemModelRelay) {
		this.deletedAtoms = deletedAtoms;
		this.deletedBondss = deletedBondss;
		this.bondsWithReplacedAtoms = bondsWithReplacedAtoms;
		this.chemModelRelay = chemModelRelay;
		this.mergedPartnerAtoms = mergedPartnerAtoms;
		this.type = type;
		this.containers= containers;
		this.droppedContainers= droppedContainers;

		this.offset = offset;
		this.moveundoredo = moveundoredo;
		this.oldRgrpHash=oldRgrpHash;
	    this.newRgrpHash=newRgrpHash;
	}

	public void redo() throws CannotRedoException {
	    for(int i=0;i<deletedAtoms.size();i++){
        	IAtomContainer containerWithMerge =containers.get(i);
        	IAtomContainer droppedContainer =droppedContainers.get(i); // can be null

        	containerWithMerge.removeAtom(deletedAtoms.get(i));
    		for(IBond bond : deletedBondss.get(i)){
    			containerWithMerge.removeBond(bond);
    		}
    		for(IBond bond : bondsWithReplacedAtoms.get(i).keySet()){
    			bond.setAtom(mergedPartnerAtoms.get(i), bondsWithReplacedAtoms.get(i).get(bond));
    		}
    		deletedAtoms.get(i).getPoint2d().x+=offset.x;
    		deletedAtoms.get(i).getPoint2d().y+=offset.y;
    		chemModelRelay.updateAtom(mergedPartnerAtoms.get(i));

    		if (droppedContainer!=null) {
	            for (IAtom mAt : droppedContainer.atoms()) {
	            	if (!containerWithMerge.contains(mAt)) {
	            		containerWithMerge.addAtom(mAt);
	            	}
	            }
	            for (IBond mBond : droppedContainer.bonds()) {
	            	if (!containerWithMerge.contains(mBond)) {
	            		containerWithMerge.addBond(mBond);
	            	}
	            }
	            chemModelRelay.getChemModel().getMoleculeSet().removeAtomContainer(droppedContainer);
            }
	    }
	    if(moveundoredo!=null)
	        moveundoredo.redo();

        if (chemModelRelay.getRGroupHandler()!=null) {
        	chemModelRelay.getRGroupHandler().restoreFromHash(newRgrpHash, chemModelRelay.getChemModel().getMoleculeSet());
        }

	}

	public void undo() throws CannotUndoException {
        for(int i=0;i<deletedAtoms.size();i++){
        	IAtomContainer containerWithMerge =containers.get(i);
        	IAtomContainer droppedContainer =droppedContainers.get(i); // can be null

        	//Put dropped atom and bond back into atc1
        	containerWithMerge.addAtom(deletedAtoms.get(i));
        	for(IBond bond : deletedBondss.get(i)){
                containerWithMerge.addBond(bond);
            }
            for(IBond bond : bondsWithReplacedAtoms.get(i).keySet()){
                bond.setAtom(deletedAtoms.get(i), bondsWithReplacedAtoms.get(i).get(bond));
            }
            deletedAtoms.get(i).getPoint2d().x-=offset.x;
            deletedAtoms.get(i).getPoint2d().y-=offset.y;
            chemModelRelay.updateAtom(deletedAtoms.get(i));
            chemModelRelay.updateAtom(mergedPartnerAtoms.get(i));

            if (droppedContainer!=null) {
            	
	            //remove from atc1 what was merged in from atc2
	            for (IAtom mAt : droppedContainer.atoms()) {
	            	if (containerWithMerge.contains(mAt)) {
	            		containerWithMerge.removeAtom(mAt);
	            	}
	            }
	            for (IBond mBond : droppedContainer.bonds()) {
	            	if (containerWithMerge.contains(mBond)) {
	            		containerWithMerge.removeBond(mBond);
	            	}
	            }
	            //restore removed container atc2 
	            chemModelRelay.getChemModel().getMoleculeSet().addAtomContainer(droppedContainer);
            }
        }
        if(moveundoredo!=null)
            moveundoredo.undo();

        if (chemModelRelay.getRGroupHandler()!=null) {
        	chemModelRelay.getRGroupHandler().restoreFromHash(oldRgrpHash, chemModelRelay.getChemModel().getMoleculeSet());
        }

	}

	public boolean canRedo() {
		return true;
	}

	public boolean canUndo() {
		return true;
	}

	public String getPresentationName() {
		return type;
	}
}
