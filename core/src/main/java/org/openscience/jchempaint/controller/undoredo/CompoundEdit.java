/*
 *  Copyright (C) 2025 John Mayfield
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
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
package org.openscience.jchempaint.controller.undoredo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A composite edit is made up of multiple sub-edits combined. The order is
 * important, the edits should be added in the order they were done, they will
 * then be undone in reverse. This allows multiple changed to be made at once
 * and then combined.
 *
 * @author John Mayfield
 */
public class CompoundEdit implements IUndoRedoable {

    private final List<IUndoRedoable> edits;
    private String description = "Structure changes";

    public CompoundEdit(String description) {
        this.description = description;
        this.edits = new ArrayList<>();
    }

    public CompoundEdit(String description,
                        List<IUndoRedoable> edits) {
        this.description = description;
        this.edits = new ArrayList<>(edits);
    }

    public CompoundEdit(String description,
                        IUndoRedoable first,
                        IUndoRedoable second) {
        this.description = description;
        this.edits = Arrays.asList(first, second);
    }

    public void add(IUndoRedoable edit) {
        this.edits.add(edit);
    }

    public String description() {
        return description;
    }

    @Override
    public void redo() {
        for (IUndoRedoable child : edits) {
            child.redo();
        }
    }

    @Override
    public void undo() {
        for (int i = edits.size() - 1; i >= 0; i--) {
            edits.get(i).undo();
        }
    }

    @Override
    public boolean canRedo() {
        for (IUndoRedoable child : edits) {
            if (!child.canRedo())
                return false;
        }
        return !edits.isEmpty();
    }

    @Override
    public boolean canUndo() {
        for (IUndoRedoable child : edits) {
            if (!child.canUndo())
                return false;
        }
        return !edits.isEmpty();
    }
}
