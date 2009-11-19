/* $Revision: $ $Author:  $ $Date$
 *
 * Copyright (C) 2007  Gilleain Torrance <gilleain.torrance@gmail.com>
 * Copyright (C) 2008  Stefan Kuhn (undo redo)
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

import static org.openscience.jchempaint.controller.edit.AppendAtom.addNewBond;
import static org.openscience.jchempaint.controller.edit.SetBondOrder.cycleBondValence;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.jchempaint.controller.edit.AppendAtom;
import org.openscience.jchempaint.renderer.selection.AbstractSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

/**
 * Adds a bond on clicking an atom, or cycles the order of clicked bonds.
 *
 * @cdk.module controlbasic
 */
public class AddBondModule extends ControllerModuleAdapter {

	private String ID;
	
    public AddBondModule(IChemModelRelay relay) {
        super(relay);
    }

    public void mouseClickedDown(Point2d worldCoordinate) {
        IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoordinate);
        IBond closestBond = chemModelRelay.getClosestBond(worldCoordinate);

        IChemObject singleSelection =
            getHighlighted(worldCoordinate, closestAtom, closestBond);

        if (singleSelection == null) {
            chemModelRelay.execute(addNewBond(worldCoordinate, chemModelRelay
                    .getIChemModel().getMoleculeSet().getAtomContainer(0)));
            setSelection(AbstractSelection.EMPTY_SELECTION);
        } else if (singleSelection instanceof IAtom) {
            String atomType =
                chemModelRelay.getController2DModel().getDrawElement();

            chemModelRelay.execute(AppendAtom.appendAtom(
                    atomType, (IAtom) singleSelection, chemModelRelay
                    .getIChemModel().getMoleculeSet().getAtomContainer(0)));
        } else if (singleSelection instanceof IBond) {
            chemModelRelay.execute(cycleBondValence((IBond) singleSelection));
            setSelection(new SingleSelection<IChemObject>(singleSelection));
        }
        chemModelRelay.updateView();
    }

    public String getDrawModeString() {
        return "Draw Bond";
    }
    
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }


}
