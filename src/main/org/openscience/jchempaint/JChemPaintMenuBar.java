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
package org.openscience.jchempaint;

import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *  An extension of JMenuBar for JCP purposes
 *
 */
public class JChemPaintMenuBar extends JMenuBar {

	private static final long serialVersionUID = -8358165408129203644L;

	private String guiString;
    
    private JChemPaintMenuHelper menuHelper=new JChemPaintMenuHelper();
    
	/**
	 *  Constructor for the JChemPaintMenuBar object. Creates a JMenuBar with all the menues that are specified in the properties
	 *  file. <p>
	 *
	 *  The menu items in the bar are defined by the property 'menubar' in
	 *  org.openscience.cdk.applications.jchempaint.resources.JChemPaint.properties.
	 *
	 * @param  jcpPanel        Description of the Parameter
	 * @param  guiString       Description of the Parameter
	 * @param  menuDefinition  Description of the Parameter
  	 * @param  blacklist       A list of menuitesm/buttons which should be ignored when building gui.
	 */
	public JChemPaintMenuBar(AbstractJChemPaintPanel jcpPanel, String guiString, List<String> blacklist) {
		this.guiString = guiString;
		addNormalMenuBar(jcpPanel, menuHelper.getMenuResourceString("menubar", guiString), blacklist);
		this.add(Box.createHorizontalGlue());
		this.add(menuHelper.createMenu(jcpPanel, "help", false, guiString, blacklist));
	}


	/**
	 *  Adds a feature to the NormalMenuBar attribute of the JChemPaintMenuBar
	 *  object
	 *
	 * @param  jcpPanel        The feature to be added to the NormalMenuBar
	 *      attribute
	 * @param  menuDefinition  The feature to be added to the NormalMenuBar
	 *      attribute
  	 * @param  blacklist       A list of menuitesm/buttons which should be ignored when building gui.
	 */
	private void addNormalMenuBar(AbstractJChemPaintPanel jcpPanel, String menuDefinition, List<String> blacklist) {
		String definition = menuDefinition;
		String[] menuKeys = StringHelper.tokenize(definition);
		for (int i = 0; i < menuKeys.length; i++) {
		    JComponent m;
		    if(menuHelper.getMenuResourceString(menuKeys[i], guiString)==null)
		        m = menuHelper.createMenuItem(jcpPanel, menuKeys[i], false);
		    else
		        m = menuHelper.createMenu(jcpPanel, menuKeys[i], false, guiString, blacklist);
			if (m != null) {
				this.add(m);
			}
		}
	}


	/**
	 *  Creates a JMenu which can be part of the menu of an application embedding jcp.
	 *
	 * @param  jcpPanel   Description of the Parameter
	 * @return            The created JMenu
  	 * @param  blacklist       A list of menuitesm/buttons which should be ignored when building gui.
	 */
	public JMenu getMenuForEmbedded(JChemPaintPanel jcpPanel, List<String> blacklist) {
		String definition = menuHelper.getMenuResourceString("menubar", guiString);
		String[] menuKeys = StringHelper.tokenize(definition);
		JMenu superMenu = new JMenu("JChemPaint");
		for (int i = 0; i < menuKeys.length; i++) {
			JComponent m = menuHelper.createMenu(jcpPanel, menuKeys[i], false, guiString, blacklist);
			if (m != null) {
				superMenu.add(m);
			}
		}
		return (superMenu);
	}

}

