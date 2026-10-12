/*
 * Copyright (C) 2005-2008 Tobias Helmus, Stefan Kuhn
 * Copyright (C) 2026 John Mayfield
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
import org.openscience.cdk.tools.periodictable.PeriodicTable;
import org.openscience.jchempaint.controller.IChemModelRelay;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Undo/Redo Edit class for the ChangeAtomSymbolAction, containing the methods
 * for undoing and redoing the regarding changes
 *
 * @author tohel
 * @cdk.module controlbasic
 */
public class ChangeAtomSymbolEdit implements IUndoRedoable {

    private final IAtom atom;
    private final int oldElement;
    private final int newElement;
    private final String type;
    private final IChemModelRelay chemModelRelay;

    public ChangeAtomSymbolEdit(IAtom atom,
                                int oldElement,
                                int newElement,
                                String type,
                                IChemModelRelay chemModelRelay) {
        this.atom = atom;
        this.oldElement = oldElement;
        this.newElement = newElement;
        this.type = type;
        this.chemModelRelay = chemModelRelay;
    }

    public ChangeAtomSymbolEdit(IAtom atom,
                                String oldSymbol,
                                String newSymbol,
                                String type,
                                IChemModelRelay chemModelRelay) {
        this(atom,
             PeriodicTable.getAtomicNumber(oldSymbol),
             PeriodicTable.getAtomicNumber(newSymbol),
             type,
             chemModelRelay);
    }

    public ChangeAtomSymbolEdit(IAtom atomInRange,
                                String formerSymbol,
                                String symbol,
                                IChemModelRelay chemModelRelay) {
        this(atomInRange, formerSymbol, symbol,
             "Change atom to " + symbol,
             chemModelRelay);
    }

    @Override
    public String description() {
        return type;
    }

    @Override
    public void redo() throws CannotRedoException {
        atom.setAtomicNumber(newElement);
        chemModelRelay.updateAtom(atom);
    }

    @Override
    public void undo() throws CannotUndoException {
        atom.setAtomicNumber(oldElement);
        chemModelRelay.updateAtom(atom);
    }
}
