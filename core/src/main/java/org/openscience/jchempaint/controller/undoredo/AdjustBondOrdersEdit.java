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
    
    private String type;
    
    private IChemModelRelay chemModelRelay=null;

	/**
	 * @param changedBonds
	 *            A HashMap containing the changed atoms as key and an Array
	 *            with the former and the changed bondOrder
	 */
	public AdjustBondOrdersEdit(Map<IBond, IBond.Order[]> changedBondsOrder,
			Map<IBond, IBond.Stereo[]> changedBondsStereo, String type,
			IChemModelRelay chemModelRelay) {
		this.changedBondOrders = changedBondsOrder;
		this.changedBondsStereo = changedBondsStereo;
		this.type=type;
		this.chemModelRelay=chemModelRelay;
	}

	public void redo() {
		Set<IBond> keys = changedBondOrders.keySet();
		Iterator<IBond> it = keys.iterator();
		while (it.hasNext()) {
			IBond bond = (IBond) it.next();
			IBond.Order[] bondOrders = changedBondOrders.get(bond);
			bond.setOrder(bondOrders[0]);
			chemModelRelay.updateAtom(bond.getAtom(0));
			chemModelRelay.updateAtom(bond.getAtom(1));
		}
		Set<IBond> keysstereo = changedBondsStereo.keySet();
		Iterator<IBond> itint = keysstereo.iterator();
		while (itint.hasNext()) {
			IBond bond = (IBond) itint.next();
			IBond.Stereo[] bondStereos = changedBondsStereo.get(bond);
			bond.setStereo(bondStereos[0]);
		}
	}

	public void undo() throws CannotUndoException {
		Set<IBond> keys = changedBondOrders.keySet();
		Iterator<IBond> it = keys.iterator();
		while (it.hasNext()) {
			IBond bond = (IBond) it.next();
			IBond.Order[] bondOrders = (IBond.Order[]) changedBondOrders.get(bond);
			bond.setOrder(bondOrders[1]);
			chemModelRelay.updateAtom(bond.getAtom(0));
			chemModelRelay.updateAtom(bond.getAtom(1));
		}
		Set<IBond> keysstereo = changedBondsStereo.keySet();
		Iterator<IBond> itint = keysstereo.iterator();
		while (itint.hasNext()) {
			IBond bond = (IBond) itint.next();
			IBond.Stereo[] bondOrders = changedBondsStereo.get(bond);
			bond.setStereo(bondOrders[1]);
		}
	}

	public boolean canRedo() {
		return true;
	}

	public boolean canUndo() {
		return true;
	}

	public String getPresentationName() {
		return type;
	}
}
