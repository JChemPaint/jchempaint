/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 * Copyright (C) 2008  Stefan Kuhn (undo/redo)
 * Copyright (C) 2009  Arvid Berg <goglepox@users.sf.net>
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

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.renderer.selection.AbstractSelection;


/**
 * Deletes closest atom on click
 * 
 * @author Niels Out
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.module  controlbasic
 */
public class RemoveModule extends ControllerModuleAdapter {

	private String ID;
	
	public RemoveModule(IChemModelRelay chemObjectRelay) {
		super(chemObjectRelay);
	}
	
	public void mouseClickedDown(Point2d worldCoordinate) {
	    
	    IAtomContainer selectedAC = getSelectedAtomContainer( worldCoordinate );
	    if(selectedAC == null)
	        return;
	    for(IAtom atom:selectedAC.atoms()) {
	        chemModelRelay.removeAtom(atom);

	    }
	    for(IBond bond:selectedAC.bonds()) {
	        chemModelRelay.removeBondAndLoneAtoms( bond );
	    }
	    setSelection( AbstractSelection.EMPTY_SELECTION );
	}
	
	public String getDrawModeString() {
		return "Delete";
	}
	
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
