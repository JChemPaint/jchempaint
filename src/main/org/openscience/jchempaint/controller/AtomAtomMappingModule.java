/* 
 * Copyright (C) 2009  Stefan Kuhn
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
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.renderer.RendererModel;

/**
 * Adds an atom on the given location on mouseclick
 * 
 * @author shk3
 * @cdk.module controlextra
 */
public class AtomAtomMappingModule extends ControllerModuleAdapter {
	
	private IAtom startAtom;
	private String ID;

	public AtomAtomMappingModule(IChemModelRelay chemModelRelay) {
		super(chemModelRelay);
	}

	public void mouseClickedDown(Point2d worldCoord) {
		RendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
		double dH = model.getHighlightDistance() / model.getScale();
		IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);

		if(closestAtom == null || closestAtom.getPoint2d().distance(worldCoord) > dH)
			startAtom = null;
		else
			startAtom = chemModelRelay.getClosestAtom(worldCoord);
	}
	
	public void mouseClickedUp(Point2d worldCoord){
		RendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
		double dH = model.getHighlightDistance() / model.getScale();
		IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);

		IAtom endAtom = null;
		if(closestAtom != null && closestAtom.getPoint2d().distance(worldCoord) < dH)
			endAtom = chemModelRelay.getClosestAtom(worldCoord);
		if(endAtom!=null && startAtom!=null){
			IMapping mapping = startAtom.getBuilder().newMapping(startAtom, endAtom);
			// ok, now figure out if they are in one reaction
			IReaction reaction1 = ChemModelManipulator.getRelevantReaction(chemModelRelay.getIChemModel(), startAtom);
			IReaction reaction2 = ChemModelManipulator.getRelevantReaction(chemModelRelay.getIChemModel(), endAtom);
			if (reaction1 != null && reaction2 != null && reaction1 == reaction2)
			{
				((IReaction)reaction1).addMapping(mapping);
			}else{
				//TODO what to do? message box? that would be a swing component
			}
		}
		startAtom = null;
	}

	public String getDrawModeString() {
		return "Do Atom-Atom Mapping";
	}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
