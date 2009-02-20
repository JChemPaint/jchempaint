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
package org.openscience.jchempaint.undoredo;

import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.controller.undoredo.ChangeAtomSymbolEdit;
import org.openscience.cdk.interfaces.IAtom;

/**
 * Undo/Redo Edit class for the ChangeAtomSymbolAction, containing the methods
 * for undoing and redoing the regarding changes
 * 
 * @author tohel
 * @cdk.module controlold
 * @cdk.svnrev  $Revision: 10979 $
 */
public class SwingChangeAtomSymbolEdit extends ChangeAtomSymbolEdit implements UndoableEdit {

	public SwingChangeAtomSymbolEdit(IAtom atomInRange, String formerSymbol,
			String symbol, String type) {
		super(atomInRange, formerSymbol, symbol, type);
	}

	public boolean addEdit(UndoableEdit arg0) {
		return false;
	}

	public void die() {
	}

	public String getRedoPresentationName() {
		return getPresentationName();
	}

	public String getUndoPresentationName() {
		return getPresentationName();
	}

	public boolean isSignificant() {
		return true;
	}

	public boolean replaceEdit(UndoableEdit arg0) {
		return false;
	}

}
