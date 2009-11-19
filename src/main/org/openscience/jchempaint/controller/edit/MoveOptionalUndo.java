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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
* Edit representing a move of a number of atoms and a field indicating undo.<p>
* This is a workaround to avoid a lot of small undo operations when moving.
* When the <code>isFinal==true</code> the edit will be pushed onto the undo
* stack but not executed, if <code>isFinal==false</code> the edit will not be
* pushed on the undo stack but the atoms will be moved.
*
* @author Arvid
* @cdk.module controlbasic
*/
public class MoveOptionalUndo extends Move {

    private boolean isFinal;

    /**
     * Creates an edit representing the move of atom(s) passed as argument.
     * @param amoutn to move.
     * @param isFinal indicating if this is the last move.
     * @param atoms to move.
     * @return edit representing the move.
     */
    public static MoveOptionalUndo move(Vector2d amount,boolean isFinal, IAtom... atoms) {
        return new MoveOptionalUndo( amount, Arrays.asList( atoms), isFinal );
    }

    /**
     * Creates an edit representing the move of atoms referenced by the bond(s).
     * @param amount to move.
     * @param isFinal indicating if this is the last move.
     * @param bonds bond or bonds of which atoms will be moved.
     * @return edit representing the move.
     */
    public static Move move(Vector2d amount,boolean isFinal, IBond... bonds) {
        Set<IAtom> atoms = new HashSet<IAtom>();
        for(IBond bond:bonds) {
            for(IAtom atom:bond.atoms())
                atoms.add( atom );
        }
        return new MoveOptionalUndo(amount,atoms,isFinal);
     }

    /**
     * Creates an edit representing the move of the atoms in a collection.<p>
     * The atoms in the given collection are moved the amount given by the vector.
     * @param amount to move.
     * @param isFinal indicating if this is the last move.
     * @param atoms to move.
     * @return edit representing the move.
     */
    public static Move move(Vector2d amount,boolean isFinal,Collection<IAtom> atoms) {
        return new MoveOptionalUndo(amount,atoms,isFinal);
    }

    private MoveOptionalUndo( Vector2d amoutn, Collection<IAtom> atomsToMove,
                            boolean isFinal) {
        super(amoutn, atomsToMove);
        this.isFinal = isFinal;
    }

    @Override
    public void redo() {
        if(isFinal)
            super.redo();
    }

    @Override
    public void undo() {
        if(isFinal)
            super.undo();
    }

    @Override
    public void execute( IAtomContainer ac ) {

        model = ac;
        if(!isFinal){
            super.redo();
        }
    }

    public boolean isFinal() {
        return isFinal;
    }
}
