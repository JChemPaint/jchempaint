/*
 *  Copyright (C) 2026 John Mayfield
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

import org.openscience.cdk.interfaces.IChemObject;

import java.util.Collection;

/**
 * This simple edit allows us to set properties on a bunch of IChemObjects.
 */
public class PropertyEdit implements IUndoRedoable {

    Collection<IChemObject> chemObjs;
    String propertyName;
    Object oldValue;
    Object newValue;

    public PropertyEdit(Collection<IChemObject> chemObjs, String propertyName, Object oldValue, Object newValue) {
        this.chemObjs = chemObjs;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public void redo() {
        for (IChemObject chemObj : chemObjs) {
            chemObj.setProperty(propertyName, newValue);
        }
    }

    @Override
    public void undo() {
        for (IChemObject chemObj : chemObjs) {
            chemObj.setProperty(propertyName, oldValue);
        }
    }
}
