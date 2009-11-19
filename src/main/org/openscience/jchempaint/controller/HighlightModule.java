/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.jchempaint.renderer.RendererModel;

/**
 * This should highlight the atom/bond when moving over with the mouse
 * 
 * @author Niels Out
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.module controlbasic
 */
public class HighlightModule extends ControllerModuleAdapter {

	public HighlightModule(IChemModelRelay chemObjectRelay) {
	    super(chemObjectRelay);
	    assert(chemObjectRelay!=null);
	}

	private IAtom prevHighlightAtom;
	private IBond prevHighlightBond;
	private String ID;
	
	private void update(IChemObject obj, RendererModel model) {
	    if(obj instanceof IAtom)
	        updateAtom((IAtom)obj,model);
	    else
	    if(obj instanceof IBond)
	        updateBond((IBond)obj,model);
	}
	private void updateAtom(IAtom atom, RendererModel model) {
	    if (prevHighlightAtom != atom) {
            model.setHighlightedAtom(atom);
            prevHighlightAtom = atom;
            prevHighlightBond = null;
            model.setHighlightedBond(null);
            chemModelRelay.updateView();
        }
	}
	
	private void updateBond(IBond bond, RendererModel model) {
	    if (prevHighlightBond != bond) {
            model.setHighlightedBond(bond);
            prevHighlightBond = bond;
            prevHighlightAtom = null;
            model.setHighlightedAtom(null);
            chemModelRelay.updateView();
        }
	}
	
	private void unsetHighlights(RendererModel model) {
	    if (prevHighlightAtom != null || prevHighlightBond != null) {
	        model.setHighlightedAtom(null);
	        model.setHighlightedBond(null);
	        prevHighlightAtom = null;
	        prevHighlightBond = null;
	        chemModelRelay.updateView();
	    }
	}

	public void mouseMove(Point2d worldCoord) {
		IAtom atom = chemModelRelay.getClosestAtom(worldCoord);
		IBond bond = chemModelRelay.getClosestBond(worldCoord);
		RendererModel model = 
		    chemModelRelay.getRenderer().getRenderer2DModel();
		
		IChemObject obj = getHighlighted( worldCoord, atom,bond );
		if(obj == null)
		    unsetHighlights( model );
		else
		    update(obj,model);
	}

	public String getDrawModeString() {
		return "Highlighting";
	}
	
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
