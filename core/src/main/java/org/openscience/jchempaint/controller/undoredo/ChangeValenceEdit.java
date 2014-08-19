/* $RCSfile$
 * $Author: gilleain $
 * $Date: 2008-11-26 16:01:05 +0000 (Wed, 26 Nov 2008) $
 * $Revision: 13311 $
 *
 * Copyright (C) 2009 Stefan Kuhn
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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * Undo/Redo Edit class for the ChangeValenceAction, containing the methods
 * for undoing and redoing the regarding changes
 * 
 * @cdk.module controlbasic
 */
public class ChangeValenceEdit implements IUndoRedoable {

    private static final long serialVersionUID = 779682083223003185L;

    private IAtom atom;

	private Integer formerValence;

	private Integer valence;
	private String text;
	private IChemModelRelay chemModelRelay=null;

	/**
	 * @param atomInRange The atom which has been changed.
	 * @param formerValence The atom valence before change.
	 * @param valence The atom valence past change.
	 * @param text The text to display for this undo/redo action..
	 * @param chemModelRelay The current instance implementing IChemModelRelay.
	 */
	public ChangeValenceEdit(IAtom atomInRange, Integer formerValence,
			Integer valence, String text, IChemModelRelay chemModelRelay) {
		this.atom = atomInRange;
		this.formerValence = formerValence;
		this.valence = valence;
		this.text=text;
		this.chemModelRelay=chemModelRelay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		this.atom.setValency(valence);
		chemModelRelay.updateAtom(atom);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		this.atom.setValency(formerValence);
		chemModelRelay.updateAtom(atom);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#canRedo()
	 */
	public boolean canRedo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#getPresentationName()
	 */
	public String getPresentationName() {
		return text;
	}

}
