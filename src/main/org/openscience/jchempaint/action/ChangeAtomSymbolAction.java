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
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.controller.IControllerModel;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.jchempaint.dialog.PeriodicTableDialog;
import org.openscience.jchempaint.dialog.PeriodicTablePanel;


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
		IChemObject object = getSource(event);
		logger.debug("Source of call: ", object);
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
		String s = event.getActionCommand();
		String symbol = s.substring(s.indexOf("@") + 1);
		if(symbol.equals("periodictable")){
            // open PeriodicTable panel
            PeriodicTableDialog dialog = new PeriodicTableDialog();
            dialog.setName("periodictabledialog");
            symbol=dialog.getChoosenSymbol();
            if(symbol.equals(""))
            	return;
		}
		while(atomsInRange.hasNext()){
            IAtom atom = atomsInRange.next();
            jcpPanel.get2DHub().setSymbol(atom,symbol);
			//TODO still needed? should this go in hub?
			// configure the atom, so that the atomic number matches the symbol
			try
			{
				IsotopeFactory.getInstance(atom.getBuilder()).configure(atom);
			} catch (Exception exception)
			{
				logger.error("Error while configuring atom");
				logger.debug(exception);
			}
		}
		jcpPanel.get2DHub().updateView();
	}
	

}

