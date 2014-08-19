/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
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
import org.openscience.jchempaint.controller.ChangeSingleElectronModule;

/**
 */
public class ChangeSingleElectronAction extends JCPAction {

	private static final long serialVersionUID = 1898335761308427006L;
	
	public void actionPerformed(ActionEvent event) {
        logger.debug("Converting to radical: ", type);
        IChemObject object = getSource(event);
        Iterator<IAtom> atomsInRange = null;
		if (object == null){
			//this means the main menu was used
			if(jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().isFilled())
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
		ChangeSingleElectronModule newActiveModule = null;
		while(atomsInRange.hasNext()){
            IAtom atom = atomsInRange.next();
            if(type.equals("add")){
                jcpPanel.get2DHub().addSingleElectron(atom);
                logger.info("Added single electron to atom");
                newActiveModule = new ChangeSingleElectronModule(jcpPanel.get2DHub(), true);
            }else{
                jcpPanel.get2DHub().removeSingleElectron(atom);
                logger.info("Removed single electron to atom");
                newActiveModule = new ChangeSingleElectronModule(jcpPanel.get2DHub(), false);
            }
        }
        jcpPanel.get2DHub().updateView();
        newActiveModule.setID(type);
        jcpPanel.get2DHub().setActiveDrawModule(newActiveModule);
    }
}
