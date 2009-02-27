/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Christoph Steinbeck
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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openscience.cdk.interfaces.IChemObject;

/**
 *  A pop-up menu for JChemPaint
 *
 */
public class JChemPaintPopupMenu extends JPopupMenu
{

	private static final long serialVersionUID = -1172105004348414589L;
    private IChemObject source;
    private JChemPaintMenuHelper menuHelper=new JChemPaintMenuHelper();
    
    public void setSource(IChemObject object) {
        this.source = object;
    }
   
    public IChemObject getSource() {
        return this.source;
    }

	/**
	 *  Constructor for the JChemPaintPopupMenu object.
	 *
	 * @param  jcpPanel  The JChemPaintPanel this menu is used for.
	 * @param  type      The String used to identify the Menu
	 * @param  guiString The string identifying the gui to build (i. e. the properties file to use)
	 */
	JChemPaintPopupMenu(JChemPaintPanel jcpPanel, String type, String guiString)
	{
		String menuTitle = JCPMenuTextMaker.getInstance(jcpPanel.getGuistring()).getText(type + "MenuTitle");
		JMenuItem titleMenuItem = new JMenuItem(menuTitle);
		titleMenuItem.setEnabled(false);
		titleMenuItem.setArmed(false);
		this.add(titleMenuItem);
		this.addSeparator();
		menuHelper.createMenu(jcpPanel, type + "popup", true, guiString, this);
	}
}

