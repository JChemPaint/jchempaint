/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egon Willighagen, Stefan Kuhn
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.action;

import java.awt.event.ActionEvent;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.controller.IControllerModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * changes the atom symbol
 */
public class ChangeAtomSymbolAction extends JCPAction
{

	private static final long serialVersionUID = -8502905723573311893L;

	public void actionPerformed(ActionEvent event)
	{
		logger.debug("About to change atom type of relevant atom!");
		IControllerModel c2dm = jcpPanel.get2DHub().getController2DModel();
		IAtom atomInRange = null;
		IChemObject object = getSource(event);
		logger.debug("Source of call: ", object);
		if (object instanceof IAtom)
		{
			atomInRange = (IAtom) object;
		} else
		{
			atomInRange = jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getHighlightedAtom();
		}
		String s = event.getActionCommand();
		String symbol = s.substring(s.indexOf("@") + 1);
		//if the atom is a pseudoatom, we must convert it back to a normal atom
		if(atomInRange instanceof IPseudoAtom){
	        IPseudoAtom atom = (IPseudoAtom)atomInRange;
	        IAtom newAtom = atom.getBuilder().newAtom(symbol, atom.getPoint2d());
	        jcpPanel.get2DHub().replaceAtom(newAtom, atom);
		}else{
            jcpPanel.get2DHub().setSymbol(atomInRange,symbol);
			// modify the current atom symbol
			c2dm.setDrawElement(symbol);
		}
		//TODO still needed? should this go in hub?
		// configure the atom, so that the atomic number matches the symbol
		try
		{
			IsotopeFactory.getInstance(atomInRange.getBuilder()).configure(atomInRange);
		} catch (Exception exception)
		{
			logger.error("Error while configuring atom");
			logger.debug(exception);
		}
		jcpPanel.get2DHub().updateView();
	}
}

