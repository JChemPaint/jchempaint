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

/**
 * Undo/Redo Edit class for the ChangeIsotopeAction, containing the methods for
 * undoing and redoing the regarding changes
 * 
 * @cdk.module controlextra
 * @cdk.svnrev  $Revision: 10979 $
 */
public class ChangeIsotopeEdit implements IUndoRedoable {

    private static final long serialVersionUID = -8177452346351978213L;

    private IAtom atom;

	private Integer formerIsotopeNumber;

	private Integer isotopeNumber;
	
	private String type;

	/**
	 * @param atom
	 *            The atom been changed
	 * @param formerIsotopeNumber
	 *            The former mass number
	 * @param isotopeNumber
	 *            The new mass number
	 */
	public ChangeIsotopeEdit(IAtom atom, Integer formerIsotopeNumber,
			Integer isotopeNumber, String type) {
		this.atom = atom;
		this.formerIsotopeNumber = formerIsotopeNumber;
		this.isotopeNumber = isotopeNumber;
		this.type=type;
	}

	public void redo() {
		this.atom.setMassNumber(isotopeNumber);

	}

	public void undo() {
		this.atom.setMassNumber(formerIsotopeNumber);

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
