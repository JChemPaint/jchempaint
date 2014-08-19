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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Undo/Redo Edit superclass for all edit classes for coordinate changing
 * actions, containing the methods for undoing and redoing the regarding changes
 * 
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 10979 $
 */
public class ChangeCoordsEdit implements IUndoRedoable {

    private static final long serialVersionUID = -8964120764568614909L;
    
    private Map<IAtom, Point2d[]> atomCoordsMap;

    private String type;

    /**
     * @param atomCoordsMap
     *            A HashMap containing the changed atoms as key and an Array
     *            with the former and the changed coordinates as Point2ds
     */
    public ChangeCoordsEdit(Map<IAtom, Point2d[]> atomCoordsMap, String type) {
        this.atomCoordsMap = atomCoordsMap;
        this.type=type;
    }

    public void redo() {
        Set<IAtom> keys = atomCoordsMap.keySet();
        Iterator<IAtom> it = keys.iterator();
        while (it.hasNext()) {
            IAtom atom = it.next();
            Point2d[] coords = atomCoordsMap.get(atom);
            atom.setNotification(false);
            atom.setPoint2d(coords[0]);
            atom.setNotification(true);
        }
    }

    public void undo() {
        Set<IAtom> keys = atomCoordsMap.keySet();
        Iterator<IAtom> it = keys.iterator();
        while (it.hasNext()) {
            IAtom atom = (IAtom) it.next();
            Point2d[] coords = atomCoordsMap.get(atom);
            atom.setPoint2d(coords[1]);
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
