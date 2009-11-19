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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * Undo/Redo Edit class for the ChangeCharge actions in AbstarctController2D,
 * containing the methods for undoing and redoing the regarding changes
 * 
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 10979 $
 */
public class ChangeChargeEdit implements IUndoRedoable {

    private static final long serialVersionUID = 1237756549190508501L;

    private IAtom atom;

	private int formerCharge;

	private int newCharge;
	
	private String type;
	
	private IChemModelRelay chemModelRelay=null;

	/**
	 * @param atomInRange
	 *            The atom been changed
	 * @param formerCharge
	 *            The former charge of this atom
	 * @param newCharge
	 *            The new charge of this atom
	 */
	public ChangeChargeEdit(IAtom atomInRange, int formerCharge,
			int newCharge, String type, IChemModelRelay chemModelRelay) {
		this.atom = atomInRange;
		this.formerCharge = formerCharge;
		this.newCharge = newCharge;
		this.type = type;
		this.chemModelRelay=chemModelRelay;
	}

	public void redo() {
		this.atom.setFormalCharge(newCharge);
		chemModelRelay.updateAtom(atom);
	}

	public void undo() {
		this.atom.setFormalCharge(formerCharge);
		chemModelRelay.updateAtom(atom);
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
