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

import java.util.Set;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.jchempaint.controller.Changed;

/**
 * Edit representing the creating of an atom.
 * @author Arvid
 * @cdk.module controlbasic
 */
public class AddAtom extends AbstractEdit implements IEdit {

    Point2d position;
    String symbol;

    IAtom addedAtom;

    /**
     * Creates an edit representing the creation of an atom.
     * @param symbol of the new atom.
     * @param position of the new atom.
     * @return edit representing the creation.
     */
    public static AddAtom createAtom(String symbol, Point2d position) {
        return new AddAtom(symbol,position);
    }

    private AddAtom(String symbol, Point2d position) {
        this.symbol =symbol;
        this.position = position;
    }
    public void redo() {

        addedAtom = model.getBuilder().newAtom( symbol, position );

        updateHydrogenCount( new IAtom[] {addedAtom} );
    }

    public void undo() {

        model.removeAtom( addedAtom );
    }

    public Set<Changed> getTypeOfChanges() {
        return changed( Changed.Structure );
    }

}
