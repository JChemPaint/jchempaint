/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Stefan Kuhn
 * Copyright (C) 2009  Arvid Berg
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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.jchempaint.renderer.selection.AbstractSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

/**
 * Adds an atom on the given location on mouseclick
 * 
 * @author Stefan Kuhn
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.module controlbasic
 */
public class CycleSymbolModule extends ControllerModuleAdapter {

	private String ID;
	
    public CycleSymbolModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
    }

    public void mouseClickedDown(Point2d worldCoord) {
        
        IChemObject singleSelected = getHighlighted( worldCoord, 
                                                     chemModelRelay.getClosestAtom(worldCoord) );
        IAtom closestAtom; 
        if(singleSelected instanceof IAtom) {
            closestAtom = (IAtom) singleSelected;
        }else {
            setSelection( AbstractSelection.EMPTY_SELECTION );
            return;
        }


        String symbol = closestAtom.getSymbol();
        boolean changed = false;
        String[] elements = 
            chemModelRelay.getController2DModel().getCommonElements(); 
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].equals(symbol)) {
                if (i < elements.length - 2) {
                    chemModelRelay.setSymbol( closestAtom,elements[i + 1]);
                } else {
                    chemModelRelay.setSymbol( closestAtom, elements[0]);
                }
                changed = true;
                break;
            }
        }
        if (!changed)
            closestAtom.setSymbol("C");
        setSelection( new SingleSelection<IAtom>(closestAtom) );
        chemModelRelay.updateView();
    }

	public String getDrawModeString() {
		return "Cycle Symbol";
	}
	
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
