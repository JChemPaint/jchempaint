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

import java.io.IOException;
import java.io.OptionalDataException;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * Undo/Redo Edit class for the ChangeAtomSymbolAction, containing the methods
 * for undoing and redoing the regarding changes
 * 
 * @author tohel
 * @cdk.module controlbasic
 */
public class ChangeAtomSymbolEdit implements IUndoRedoable {

    private static final long serialVersionUID = 779682083223003185L;

    private IAtom atom;

	private String formerSymbol;

	private String symbol;
	private String type;
	private IChemModelRelay chemModelRelay=null;

	/**
	 * @param atomInRange
	 *            The atom been changed
	 * @param formerSymbol
	 *            The atom symbol before change
	 * @param symbol
	 *            The atom symbol past change
	 */
	public ChangeAtomSymbolEdit(IAtom atomInRange, String formerSymbol,
			String symbol, String type, IChemModelRelay chemModelRelay) {
		this.atom = atomInRange;
		this.formerSymbol = formerSymbol;
		this.symbol = symbol;
		this.type=type;
		this.chemModelRelay=chemModelRelay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		this.atom.setSymbol(symbol);
		try {
			IsotopeFactory.getInstance(atom.getBuilder()).configure(atom);
			chemModelRelay.updateAtom(atom);
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		this.atom.setSymbol(formerSymbol);
		chemModelRelay.updateAtom(atom);
		try {
			IsotopeFactory.getInstance(atom.getBuilder()).configure(atom);
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		return type;
	}

}
