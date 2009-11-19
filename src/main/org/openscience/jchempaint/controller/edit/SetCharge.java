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
import org.openscience.jchempaint.controller.Changed;

/**
* Edit representing a change of an atom's formal charge.
* @author Arvid
* @cdk.module controlbasic
*/
public class SetCharge extends AbstractEdit {

    IAtom atom;
    Integer newCharge;
    Integer oldCharge;

    /**
     *
     * @param atom to change.
     * @param charge new formal charge.
     * @return edit representing the change in formal charge.
     */
    public static SetCharge setCharge(IAtom atom, int charge, IAtomContainer ac) {
        return new SetCharge(atom,charge,ac);
    }

    /**
     * Creates an edit that clears the formal charge of an atom.
     * @param atom to operate on.
     * @param ac    the AtomContainer the atoms are in.
     * @return edit representing the clearing of the formal charge.
     */
    public static SetCharge clearCharge(IAtom atom, IAtomContainer ac) {
        return new SetCharge(atom,null,ac);
    }

    private SetCharge(IAtom atom, Integer charge, IAtomContainer ac) {
        this.atom = atom;
        this.newCharge = charge;
        this.oldCharge = atom.getFormalCharge();
        this.ac = ac;
    }

    public Set<Changed> getTypeOfChanges() {

        return changed( Changed.Properties );
    }

    public void redo() {
        atom.setFormalCharge( newCharge );
        updateHydrogenCount( atom );
    }

    public void undo() {

        atom.setFormalCharge( oldCharge );
        updateHydrogenCount( atom );

    }

}
