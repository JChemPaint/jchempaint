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

import org.openscience.cdk.Atom;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IIsotope;


/**
 * Changes the isotope for a selected atom
 *
 */
public class ChangeIsotopeAction extends JCPAction
{

	private static final long serialVersionUID = -4692219842740123315L;

	public void actionPerformed(ActionEvent event)
	{
		logger.debug("About to change atom type of relevant atom!");
		IChemObject object = getSource(event);
		logger.debug("Source of call: ", object);
		if (object instanceof Atom)
		{
			Atom atom = (Atom) object;
			int isotopeNumber = 0;
			try
			{
				IIsotope isotope = IsotopeFactory.getInstance(atom.getBuilder()).
						getMajorIsotope(atom.getSymbol());
				isotopeNumber = isotope.getMassNumber();
			} catch (Exception exception)
			{
				logger.error("Error while configuring atom");
				logger.debug(exception);
			}
			// adapt for menu chosen
			if (type.equals("major"))
			{
				// that's the default
			} else if (type.equals("majorPlusOne"))
			{
				isotopeNumber++;
			} else if (type.equals("majorPlusTwo"))
			{
				isotopeNumber++;
				isotopeNumber++;
			} else if (type.equals("majorPlusThree"))
			{
				isotopeNumber++;
				isotopeNumber++;
				isotopeNumber++;
			} else if (type.equals("majorMinusOne"))
			{
				isotopeNumber--;
			} else if (type.equals("majorMinusTwo"))
			{
				isotopeNumber--;
				isotopeNumber--;
			} else if (type.equals("majorMinusThree"))
			{
				isotopeNumber--;
				isotopeNumber--;
				isotopeNumber--;
			}
			atom.setMassNumber(isotopeNumber);
			jcpPanel.get2DHub().updateView();
		}
	}
}

