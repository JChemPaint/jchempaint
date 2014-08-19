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
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 13311 $
 */
public class ReplaceAtomEdit implements IUndoRedoable {

    private static final long serialVersionUID = -7667903450980188402L;

    private IChemModel chemModel;

	private IAtom oldAtom;
	
	private IAtom newAtom;

	private String type;
	
	/**
	 * @param chemModel
	 * @param undoRedoContainer
	 * @param c2dm The controller model; if none, set to null
	 */
	public ReplaceAtomEdit(IChemModel chemModel,
			IAtom oldAtom, IAtom newAtom, String type) {
		this.chemModel = chemModel;
		this.oldAtom = oldAtom;
		this.newAtom = newAtom;
		this.type = type;
	}

	public void redo() {
        IAtomContainer relevantContainer = ChemModelManipulator.getRelevantAtomContainer(chemModel, oldAtom);
        AtomContainerManipulator.replaceAtomByAtom(relevantContainer, 
            oldAtom, newAtom);
	}

	public void undo() {
        IAtomContainer relevantContainer = ChemModelManipulator.getRelevantAtomContainer(chemModel, newAtom);
        AtomContainerManipulator.replaceAtomByAtom(relevantContainer, 
            newAtom, oldAtom);
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
