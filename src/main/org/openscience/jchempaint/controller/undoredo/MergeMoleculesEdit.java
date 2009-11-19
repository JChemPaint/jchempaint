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
    
    private IAtom deletedAtom;
    private List<IBond> deletedBonds;
    private Map<IBond, Integer> bondsWithReplacedAtom;
    private IChemModelRelay c2dm;
    private String type;
    private IAtomContainer ac;
    private Vector2d offset;
    private IAtom atomwhichwasmoved;
    
	public MergeMoleculesEdit(IAtom deletedAtom, IAtomContainer containerWhereAtomWasIn, List<IBond> deletedBonds, Map<IBond, Integer> bondsWithReplacedAtom, Vector2d offset, IAtom atomwhichwasmoved, String type, IChemModelRelay c2dm) {
		this.deletedAtom = deletedAtom;
		this.deletedBonds = deletedBonds;
		this.bondsWithReplacedAtom = bondsWithReplacedAtom;
		this.c2dm = c2dm;
		this.atomwhichwasmoved = atomwhichwasmoved;
		this.type = type;
		this.ac= containerWhereAtomWasIn;
		this.offset = offset;
	}

	public void redo() throws CannotRedoException {
		ac.removeAtom(deletedAtom);
		for(IBond bond : deletedBonds){
			ac.removeBond(bond);
		}
		for(IBond bond : bondsWithReplacedAtom.keySet()){
			bond.setAtom(atomwhichwasmoved, bondsWithReplacedAtom.get(bond));
		}
		deletedAtom.getPoint2d().x-=offset.x;
		deletedAtom.getPoint2d().y-=offset.y;
		atomwhichwasmoved.getPoint2d().x-=offset.x;
		atomwhichwasmoved.getPoint2d().y-=offset.y;
		c2dm.updateAtom(atomwhichwasmoved);
	}

	public void undo() throws CannotUndoException {
		ac.addAtom(deletedAtom);
		for(IBond bond : deletedBonds){
			ac.addBond(bond);
		}
		for(IBond bond : bondsWithReplacedAtom.keySet()){
			bond.setAtom(deletedAtom, bondsWithReplacedAtom.get(bond));
		}
		deletedAtom.getPoint2d().x+=offset.x;
		deletedAtom.getPoint2d().y+=offset.y;
		atomwhichwasmoved.getPoint2d().x+=offset.x;
		atomwhichwasmoved.getPoint2d().y+=offset.y;
		c2dm.updateAtom(deletedAtom);
		c2dm.updateAtom(atomwhichwasmoved);
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
