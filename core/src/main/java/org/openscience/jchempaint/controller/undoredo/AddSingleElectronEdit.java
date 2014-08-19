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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * Undo/Redo Edit class for change single electrons, containing the methods
 * for undoing and redoing the regarding changes.
 * 
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 10979 $
 */
public class AddSingleElectronEdit implements IUndoRedoable {

    private static final long serialVersionUID = 2348438340238651134L;

    private IAtomContainer container;

	private IElectronContainer electronContainer;
	
	private String type;
	
	private boolean addSingleElectron;
	
	private IChemModelRelay chemModelRelay;
	
	private IAtom atom;

	/**
	 * @param relevantContainer -
	 *            The container the changes were made
	 * @param electronContainer -
	 *            AtomContainer containing the SingleElectron
	 * @param addSingleElectron true=single electron was added, false=was removed.
	 * @param chemModelRelay    The current chemModelRelay.
	 * @param atom              The atom the change was done on.
	 * @param type              A string representing this edit, to be displayed 
	 *                          in GUI.
	 */
	public AddSingleElectronEdit(IAtomContainer relevantContainer,
			IElectronContainer electronContainer, boolean addSingleElectron, 
			IChemModelRelay chemModelRelay, IAtom atom, String type) {
		this.container = relevantContainer;
		this.electronContainer = electronContainer;
		this.addSingleElectron = addSingleElectron;
		this.type = type;
		this.atom = atom;
		this.chemModelRelay = chemModelRelay;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.controller.undoredo.IUndoRedoable#redo()
	 */
	public void redo(){
	    if(addSingleElectron)
	        container.addElectronContainer(electronContainer);
	    else
	        container.removeElectronContainer(electronContainer);
	    chemModelRelay.updateAtom(atom);
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.controller.undoredo.IUndoRedoable#undo()
	 */
	public void undo(){
	    if(addSingleElectron)
	        container.removeElectronContainer(electronContainer);
	    else
	        container.addElectronContainer(electronContainer);
	    chemModelRelay.updateAtom(atom);
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.controller.undoredo.IUndoRedoable#canRedo()
	 */
	public boolean canRedo() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.controller.undoredo.IUndoRedoable#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}


	/**
	 * A string representing this edit, to be displayed in GUI.
	 * 
	 * @return The presentation string.
	 */
	public String getPresentationName() {
		return type;
	}
}
