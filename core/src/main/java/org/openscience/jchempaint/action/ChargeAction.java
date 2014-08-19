/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 2009 Stefan Kuhn
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.jchempaint.controller.ChangeFormalChargeModule;


/**
 * Changes the charge
 */
public class ChargeAction extends JCPAction
{

	private static final long serialVersionUID = -8502905723573311893L;

	public void actionPerformed(ActionEvent event)
	{
        String s = event.getActionCommand();
        String action = s.substring(s.indexOf("@") + 1);
        int charge=0;
        if(action.equals("plus2")){
            charge=2;
        }else if(action.equals("plus")){
            charge=1;
        }else if(action.equals("minus")){
            charge=-1;
        }else if(action.equals("minus2")){
            charge=-2;
        }
        ChangeFormalChargeModule newActiveModule = new ChangeFormalChargeModule(jcpPanel.get2DHub(), charge);
        newActiveModule.setID(action);
        jcpPanel.get2DHub().setActiveDrawModule(newActiveModule);
		logger.debug("About to change atom type of relevant atom!");
		Iterator<IAtom> atomsInRange = null;
		IChemObject object = getSource(event);
		logger.debug("Source of call: ", object);
		if (object == null){
			//this means the main menu was used
			if(jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection()!=null && jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().isFilled())
				atomsInRange=jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().atoms().iterator();
		}else if (object instanceof IAtom)
		{
			List<IAtom> atoms = new ArrayList<IAtom>();
			atoms.add((IAtom) object);
			atomsInRange = atoms.iterator();
		} else
		{
			List<IAtom> atoms = new ArrayList<IAtom>();
			atoms.add(jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getHighlightedAtom());
			atomsInRange = atoms.iterator();
		}
		if(atomsInRange==null)
			return;
		while(atomsInRange.hasNext()){
			IAtom atom = atomsInRange.next();
	        int newCharge = charge;
	        if( atom.getFormalCharge() != null)
	            newCharge += atom.getFormalCharge();
	        jcpPanel.get2DHub().setCharge(atom, newCharge);
		}
		jcpPanel.get2DHub().updateView();
	}
}

