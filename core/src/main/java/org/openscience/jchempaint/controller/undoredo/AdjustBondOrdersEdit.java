/* $RCSfile$
 * $Author: gilleain $
 * $Date: 2008-11-26 16:01:05 +0000 (Wed, 26 Nov 2008) $
 * $Revision: 13311 $
 *
 * Copyright (C) 2005-2008 Tobias Helmus, Stefan Kuhn
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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * Undo/Redo Edit class for the AdjustBondOrdesAction, containing the methods
 * for undoing and redoing the regarding changes
 * 
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 10979 $
 */
public class AdjustBondOrdersEdit implements IUndoRedoable {

    private static final long serialVersionUID = 1513012471000333600L;
    
    private Map<IBond, IBond.Order[]> changedBondOrders;
    private Map<IBond, IBond.Stereo[]> changedBondsStereo;
    private Map<IBond, IBond.Display[]> changedBondsDisplay;

    private String type;
    
    private IChemModelRelay chemModelRelay=null;

	/**
	 * @param changedBonds
	 *            A HashMap containing the changed atoms as key and an Array
	 *            with the former and the changed bondOrder
	 */
	public AdjustBondOrdersEdit(
            Map<IBond, IBond.Order[]> changedBondsOrder,
			Map<IBond, IBond.Stereo[]> changedBondsStereo,
            Map<IBond, IBond.Display[]> changedBondsDisplay,
            String type,
			IChemModelRelay chemModelRelay) {
		this.changedBondOrders = changedBondsOrder;
		this.changedBondsStereo = changedBondsStereo;
		this.changedBondsDisplay = changedBondsDisplay;
		this.type=type;
		this.chemModelRelay=chemModelRelay;
	}

    public AdjustBondOrdersEdit(
            Map<IBond, IBond.Order[]> changedBondsOrder,
            Map<IBond, IBond.Stereo[]> changedBondsStereo,
            String type,
            IChemModelRelay chemModelRelay) {
        this(changedBondsOrder, changedBondsStereo, Collections.emptyMap(), type, chemModelRelay);
    }

	public void redo() {
		for (Map.Entry<IBond, IBond.Order[]> e : changedBondOrders.entrySet()) {
			IBond bond = e.getKey();
			IBond.Order[] bondOrders = e.getValue();
			bond.setOrder(bondOrders[0]);
		}
		for (Map.Entry<IBond, IBond.Stereo[]> e : changedBondsStereo.entrySet()) {
			IBond bond = e.getKey();
			IBond.Stereo[] bondStereos = e.getValue();
			bond.setStereo(bondStereos[0]);
		}
        for (Map.Entry<IBond, IBond.Display[]> e : changedBondsDisplay.entrySet()) {
            IBond bond = e.getKey();
            IBond.Display[] bondStereos = e.getValue();
            bond.setDisplay(bondStereos[0]);
        }
		// update once everything has changed
		for (IBond bond : changedBondOrders.keySet()) {
			chemModelRelay.updateAtoms(bond);
		}
	}


	public void undo() throws CannotUndoException {
		for (Map.Entry<IBond, IBond.Order[]> e : changedBondOrders.entrySet()) {
			IBond bond = e.getKey();
			IBond.Order[] bondOrders = e.getValue();
			bond.setOrder(bondOrders[1]);
		}
		for (Map.Entry<IBond, IBond.Stereo[]> e : changedBondsStereo.entrySet()) {
			IBond bond = e.getKey();
			IBond.Stereo[] bondStereos = e.getValue();
			bond.setStereo(bondStereos[1]);
            if (bondStereos[1] == IBond.Stereo.NONE)
                bond.setDisplay(IBond.Display.Solid);
		}
        for (Map.Entry<IBond, IBond.Display[]> e : changedBondsDisplay.entrySet()) {
            IBond bond = e.getKey();
            IBond.Display[] bondStereos = e.getValue();
            bond.setDisplay(bondStereos[1]);
        }
		// update once everything has changed
		for (IBond bond : changedBondOrders.keySet()) {
			chemModelRelay.updateAtoms(bond);
		}
	}

	public boolean canRedo() {
		return true;
	}

	public boolean canUndo() {
		return true;
	}

	public String description() {
		return type;
	}
}
