/*
 *  Copyright (C) 2025 John Mayfield
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.undoredo;

import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * This adapter allows us to wrap anu IUndoRedoable for usage in Java Swing
 * with sensible defaults.
 * 
 * @author John Mayfield
 */
public class SwingUndoableEdit implements UndoableEdit, IUndoRedoable {

    private final IUndoRedoable undoRedoable;

    public SwingUndoableEdit(IUndoRedoable undoRedoable) {
        this.undoRedoable = undoRedoable;
    }

    @Override
    public void undo() throws CannotUndoException {
        undoRedoable.undo();
    }

    @Override
    public boolean canUndo() {
        return undoRedoable.canUndo();
    }

    @Override
    public void redo() throws CannotRedoException {
        undoRedoable.redo();
    }

    @Override
    public boolean canRedo() {
        return undoRedoable.canRedo();
    }

    @Override
    public void die() {

    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public String description() {
        return undoRedoable.description();
    }

    @Override
    public String getUndoPresentationName() {
        return description();
    }

    @Override
    public String getRedoPresentationName() {
        return description();
    }

    @Override
    public String getPresentationName() {
        return description();
    }
}
