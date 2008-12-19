/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Christoph Steinbeck, Stefan Kuhn
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

import javax.swing.JOptionPane;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.exception.CDKException;


/**
 * Triggers the adjustment of BondOrders
 *
 */
public class AdjustBondOrdersAction extends JCPAction
{

	private static final long serialVersionUID = -2930750443449102916L;

	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Adjusting bondorders: ", type);
		if (type.equals("clear"))
		{
			try{
				jcpPanel.get2DHub().resetBondOrders();
			} catch (Exception exc)
			{
				String error = "Could not adjust bondorders.";
				logger.error(error);
				logger.debug(exc);
				JOptionPane.showMessageDialog(jcpPanel, error);
			}
		} else
		{
			try
			{
				jcpPanel.get2DHub().adjustBondOrders();
			} catch (Exception exc)
			{
				String error = "Could not adjust bondorders.";
				logger.error(error);
				logger.debug(exc);
				JOptionPane.showMessageDialog(jcpPanel, error);
			}
		}
		jcpPanel.get2DHub().updateView();
	}
}

