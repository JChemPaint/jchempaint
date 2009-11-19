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

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.BondManipulator;
import org.openscience.jchempaint.controller.Changed;

/**
* Edit representing the changing of the bond order of a bond.
* @author Arvid
* @cdk.module controlbasic
*/
public class SetBondOrder extends AbstractEdit{

    IBond bond;
    IBond.Order newOrder;
    IBond.Order oldOrder;

    /**
     * Create an edit representing the cycling.<p>
     * Returns either a <code>SetBondOrder</code> edit or a
     * <code>SetStereo</code> edit depending on the stereo state of the bond.
     * @param bond to cycle valence on.
     * @return edit representing the cycling.
     */
    public static IEdit cycleBondValence(IBond bond) {
        // FIXME maxOrder shoulb be getController2DModel.getMaxOrder();
        IBond.Order maxOrder = IBond.Order.TRIPLE;
        IBond.Order order;
        // special case : reset stereo bonds
        if (bond.getStereo() != IBond.Stereo.NONE) {
           return SetStereo.setStereo( bond, IBond.Stereo.NONE);
        }else{
            // cycle the bond order up to maxOrder
            if (BondManipulator.isLowerOrder(bond.getOrder(), maxOrder)) {
                order = BondManipulator.increaseBondOrder(bond.getOrder());
            } else {
                order = IBond.Order.SINGLE;
            }
            return new SetBondOrder(bond,order);
        }
    }

    /**
     * Creates an edit representing the change of bond order.
     * @param bond to change.
     * @param order to change to.
     * @return edit representing the change to a new bond order.
     */
    public static SetBondOrder setOrder(IBond bond, IBond.Order order) {
        return new SetBondOrder( bond ,order);
    }

    private SetBondOrder(IBond bond,IBond.Order order) {
        this.bond = bond;
        this.newOrder = order;
        this.oldOrder = bond.getOrder();
    }

    public void redo() {

        bond.setOrder( newOrder);

        updateHydrogenCount( bond.getAtom(0), bond.getAtom(1) );
    }

    public void undo() {

        bond.setOrder(oldOrder);
        updateHydrogenCount( bond.getAtom(0), bond.getAtom(1) );
    }

    public Set<Changed> getTypeOfChanges() {
        return changed(Changed.Properties);
    }
}
