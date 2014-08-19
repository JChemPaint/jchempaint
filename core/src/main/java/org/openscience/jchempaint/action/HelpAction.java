/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn, Christoph Steinbeck
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
import java.io.File;
import java.net.URL;

import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.dialog.HelpDialog;
import org.openscience.jchempaint.GT;

/**
 * Pops up the help.
 *
 */
public class HelpAction extends JCPAction
{

	private static final long serialVersionUID = -9213900779679488824L;

	public void actionPerformed(ActionEvent e)
	{

		String helpRoot = "org/openscience/jchempaint/resources/userhelp_jcp/";
		String language = GT.getLanguage();
		URL helpURL = HelpDialog.class.getClassLoader().getResource(helpRoot + language + "/jcp.html");
		if (helpURL == null) {
			language = "en_US";
		}
		
		if (type.equals("tutorial"))
		{
			new HelpDialog(null, helpRoot+language+"/contain/tutorial.html", GT._("JChemPaint Help")).setVisible(true);
		} 
		else if (type.equals("rgpTutorial"))
		{
			new HelpDialog(null, helpRoot+language+"/contain/rgroup_tutorial.html", GT._("JChemPaint Help")).setVisible(true);
		} 
		else if (type.equals("feedback"))
		{
			new HelpDialog(null, helpRoot+language+"/contain/feedback.html", GT._("JChemPaint Help")).setVisible(true);
		} 
		else if (type.equals("license"))
		{
			new HelpDialog(null, helpRoot+language+"/license.html", GT._("JChemPaint License")).setVisible(true);
		} 
		else
		{
			new HelpDialog(null, helpRoot+language+"/jcp.html", GT._("JChemPaint Help")).setVisible(true);
		}
	}
}

