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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.controller.Changed;

/**
 * Edit representing the connection of two atoms with a bond.
 * @author Arvid
 * @cdk.module controlbasic
 */
public class AddBond extends AbstractEdit implements IEdit{

    IAtom atom1;
    IAtom atom2;

    IBond newBond;

    /**
     * Creates an edit representing the creation of a bond between the given
     * atoms.
     * @param atom1 first atom of the bond.
     * @param atom2 second atom of the bond.
     * @param ac    the AtomContainer the atoms are in.
     * @return edit representing creation of the bond.
     */
    public static AddBond addBond(IAtom atom1, IAtom atom2, IAtomContainer ac) {
        return new AddBond(atom1,atom2, ac);
    }

    private AddBond(IAtom atom1, IAtom atom2, IAtomContainer ac) {
        this.atom1 = atom1;
        this.atom2 = atom2;
        this.ac = ac;
    }
    public void redo() {

        newBond = model.getBuilder().newBond(atom1,atom2);
        model.addBond( newBond );

        updateHydrogenCount( atom1,atom2 );
    }

    public void undo() {

        model.removeBond( newBond);

        updateHydrogenCount( new IAtom[] { newBond.getAtom( 0 ),
                                           newBond.getAtom(1)} );
    }

    public Set<Changed> getTypeOfChanges() {

        return changed( Changed.Structure );
    }
}
