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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.AtomBondSet;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

/**
 * Changes (Increases or Decreases) Formal Charge of an atom
 * 
 * @author Niels Out
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.module controlbasic
 */
public class ChangeFormalChargeModule extends ControllerModuleAdapter {

	private int change = 0;
	private String ID;

	public ChangeFormalChargeModule(IChemModelRelay chemModelRelay, int change) {
		super(chemModelRelay);
		this.change = change;
	}

	@Override
	public void mouseClickedDown(Point2d worldCoord, int modifiers) {
	    
	    Collection<IAtom> atoms = new HashSet<>();

		JChemPaintRendererModel renderModel = chemModelRelay.getRenderer()
															.getRenderer2DModel();
		IAtom highlightedAtom = renderModel.getHighlightedAtom();
		if (highlightedAtom != null) {
			atoms.add(highlightedAtom);
		} else {
			IChemObjectSelection selection = renderModel.getSelection();
			if (selection != null) {
				atoms = selection.elements(IAtom.class);
			}
		}

		for (IAtom atom : atoms) {
			int newCharge = change;
			if (atom.getFormalCharge() != null)
				newCharge += atom.getFormalCharge();
			chemModelRelay.setCharge(atom, newCharge);
		}
		if (!atoms.isEmpty())
	    	chemModelRelay.updateView();
	}

	public String getDrawModeString() {
		if (change < 0)
            return "Decrease Charge";
        else
            return "Increase Charge";
	}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
