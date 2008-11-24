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
package org.openscience.jchempaint.io;

import java.io.File;

import javax.swing.Icon;

/**
 * The file view class
 *
 */
public class JCPFileView extends javax.swing.filechooser.FileView
{

	/**
	 *  Gets the name attribute of the JCPFileView object
	 *
	 *@param  f  Description of the Parameter
	 *@return    The name value
	 */
	public String getName(File f)
	{
		return null;
		// let the L&F FileView figure this out
	}


	/**
	 *  Gets the description attribute of the JCPFileView object
	 *
	 *@param  f  Description of the Parameter
	 *@return    The description value
	 */
	public String getDescription(File f)
	{
		return null;
		// let the L&F FileView figure this out
	}


	/**
	 *  Gets the traversable attribute of the JCPFileView object
	 *
	 *@param  f  Description of the Parameter
	 *@return    The traversable value
	 */
	public Boolean isTraversable(File f)
	{
		return null;
		// let the L&F FileView figure this out
	}


	/**
	 *  Gets the typeDescription attribute of the JCPFileView object
	 *
	 *@param  f  Description of the Parameter
	 *@return    The typeDescription value
	 */
	public String getTypeDescription(File f)
	{
		String extension = JCPFileFilter.getExtension(f);
		JCPFileFilter jcpff = new JCPFileFilter(extension);
		String type = null;

		if (extension != null)
		{
			type = jcpff.getDescription();
		}
		return type;
	}


	/**
	 *  Gets the icon attribute of the JCPFileView object
	 *
	 *@param  f  Description of the Parameter
	 *@return    The icon value
	 */
	public Icon getIcon(File f)
	{
		Icon icon = null;

//        String extension = JCPFileFilter.getExtension(f);

//        if (extension != null) {
//            if (extension.equals(Utils.jpeg) ||
//                extension.equals(Utils.jpg)) {
//                icon = jpgIcon;
//            } else if (extension.equals(Utils.gif)) {
//                icon = gifIcon;
//            } else if (extension.equals(Utils.tiff) ||
//                       extension.equals(Utils.tif)) {
//                icon = tiffIcon;
//            }
//        }
		return icon;
	}

}

