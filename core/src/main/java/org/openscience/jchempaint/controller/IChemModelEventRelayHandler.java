/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-09-02 11:46:10 +0100 (su, 02 sep 2007) $
 * 
 * Copyright (C) 2009  Egon Willighagen <egonw@users.lists.sf>
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
package org.openscience.jchempaint.controller;

/**
 * @cdk.module control
 */
public interface IChemModelEventRelayHandler {

    /**
     * Signals that the connectivity table of the structure has changed, for
     * example, an atom or bond was added or removed. This implies that the
     * coordinates have changed too.
     */
    public void structureChanged();

    /**
     * Signals that the atom or bond properties have changed, like atom symbol
     * or bond order. This excludes coordinate changes, for which
     * {@link #coordinatesChanged()} is used.
     */
    public void structurePropertiesChanged();

    /**
     * Signals that the coordinates of the structure or the coordinates
     * boundaries of the structure have changed.
     */
    public void coordinatesChanged();

    /**
     * Signals that a selection was added, removed or changed.
     */
    public void selectionChanged();
    
    public void zoomChanged();
}
