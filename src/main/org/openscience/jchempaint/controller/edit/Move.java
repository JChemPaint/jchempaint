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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.controller.Changed;

/**
* Edit representing movement of atoms.
* @author Arvid
* @cdk.module controlbasic
*/
public class Move extends AbstractEdit {

    Collection<IAtom> atomsToMove;
    Vector2d amoutn;

    /**
     * Creates an edit representing the move of atom(s) passed as argument.
     * @param amoutn to move.
     * @param atoms to move.
     * @return edit representing the move.
     */
    public static Move move(Vector2d amoutn,IAtom... atoms) {
        return new Move(amoutn, Arrays.asList( atoms));
    }

    /**
     * Creates an edit representing the move of atoms referenced by the bond(s).
     * @param amount to move.
     * @param bonds bond or bonds of which atoms will be moved.
     * @return edit representing the move.
     */
    public static Move move(Vector2d amount,IBond... bonds) {
        Set<IAtom> atoms = new HashSet<IAtom>();
        for(IBond bond:bonds) {
            for(IAtom atom:bond.atoms())
                atoms.add( atom );
        }
        return new Move(amount,atoms);
     }

    /**
     * Creates an edit representing the move of the atoms in a collection.<p>
     * The atoms in the given collection are moved the amount given by the vector.
     * @param amount to move.
     * @param atoms to move.
     * @return edit representing the move.
     */
    public static Move move(Vector2d amount,Collection<IAtom> atoms) {
        return new Move(amount,atoms);
    }

    protected Move(Vector2d amount,Collection<IAtom> atomsToMove) {
        this.atomsToMove = new ArrayList<IAtom>(atomsToMove);
        this.amoutn = new Vector2d(amount);
    }

    public Set<Changed> getTypeOfChanges() {

        return changed( Changed.Coordinates );
    }

    public void redo() {

        for(IAtom atom:atomsToMove) {
            atom.getPoint2d().add( amoutn );
        }
    }

    public void undo() {
        for(IAtom atom:atomsToMove) {
            atom.getPoint2d().sub( amoutn );
        }
    }

}
