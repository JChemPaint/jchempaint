/* $RCSfile$
 * $Author: egonw $
 * $Date: 2008-05-12 07:29:49 +0100 (Mon, 12 May 2008) $
 * $Revision: 10979 $
 *
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 10979 $
 */
public class MoveAtomEdit implements IUndoRedoable {

    private static final long serialVersionUID = -2277790465507859547L;

    private IAtomContainer undoRedoContainer;

	private Vector2d offset;
	
	private String type;

	public MoveAtomEdit(IAtomContainer undoRedoContainer, Vector2d offset, String type) {
		this.undoRedoContainer = undoRedoContainer;
		this.offset=offset;
		this.type=type;
	}

	public void redo() {
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom=undoRedoContainer.getAtom(i);
			atom.getPoint2d().x+=offset.x;
			atom.getPoint2d().y+=offset.y;
		}
	}

	public void undo() {
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom=undoRedoContainer.getAtom(i);
			atom.getPoint2d().x-=offset.x;
			atom.getPoint2d().y-=offset.y;
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
