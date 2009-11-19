/*
 * Copyright (C) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.jchempaint.controller.edit;

import java.util.Collection;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.controller.Changed;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;

/**
 * Represents an edit operation that can be undone.
 * @author Arvid
 * @cdk.module control
 */
public interface IEdit extends IUndoRedoable{

    public void redo();

    public void undo();

    /**
     * Executes this edit operation on the given atom container.
     * @param atomContainer the atom container to work upon
     */
    public void execute( IAtomContainer atomContainer );

    /**
     * Returns a set defining what this edit has changed
     * @return a <code>Set</code> with <code>Changed</code>
     */
    public Set<Changed> getTypeOfChanges();

}
