/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 * Copyright (C) 2008  Stefan Kuhn (undo redo)
 * Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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

/**
 * Changes (Increases or Decreases) single electrons of an atom
 * 
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.module controlbasic
 */
public class ChangeSingleElectronModule extends ControllerModuleAdapter {

	private boolean add = true;
	private String ID;

	/**
	 * Constructor for the ChangeSingleElectronModule.
	 * 
	 * @param chemModelRelay The current chemModelRelay.
	 * @param add            true=single electrons get added, 
	 *                       false=single electrons get removed
	 */
	public ChangeSingleElectronModule(IChemModelRelay chemModelRelay, boolean add) {
		super(chemModelRelay);
		this.add = add;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.controller.ControllerModuleAdapter#mouseClickedDown(javax.vecmath.Point2d)
	 */
	public void mouseClickedDown(Point2d worldCoord) {
	    
	    IAtomContainer selectedAC = getSelectedAtomContainer( worldCoord );
	    if(selectedAC == null) return;
	    for(IAtom atom:selectedAC.atoms()) {
            if(add){
                chemModelRelay.addSingleElectron(atom);
            }else{
                chemModelRelay.removeSingleElectron(atom);
            }
	    }
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.controller.IControllerModule#getDrawModeString()
	 */
	public String getDrawModeString() {
		if (add)
            return "Add Single Electron";
        else
            return "Remove Single Electron";
	}

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IControllerModule#getID()
     */
    public String getID() {
        return ID;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IControllerModule#setID(java.lang.String)
     */
    public void setID(String ID) {
        this.ID=ID;
    }

}
