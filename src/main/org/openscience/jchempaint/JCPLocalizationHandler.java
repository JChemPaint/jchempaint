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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *  A property manager for JChemPaint.
 *
 */
public class JCPLocalizationHandler
{

	private static JCPLocalizationHandler instance;

	private ResourceBundle translations;


	/**
	 *  Constructor for the JCPLocalizationHandler object
	 *
	 *@param  locale  Description of the Parameter
	 */
	private JCPLocalizationHandler(Locale locale)
	{
		translations = ResourceBundle.getBundle("org/openscience/jchempaint/resources/JCPBundle", locale);
	}


	/**
	 *  Gets the instance attribute of the JCPLocalizationHandler class
	 *
	 *@param  locale  Description of the Parameter
	 *@return         The instance value
	 */
	public static JCPLocalizationHandler getInstance(Locale locale)
	{
		JCPLocalizationHandler.instance = new JCPLocalizationHandler(locale);
		return JCPLocalizationHandler.instance;
	}


	/**
	 *  Gets the instance attribute of the JCPLocalizationHandler class
	 *
	 *@return    The instance value
	 */
	public static JCPLocalizationHandler getInstance()
	{
		if (JCPLocalizationHandler.instance == null)
		{
			JCPLocalizationHandler.instance = getInstance(new Locale("en", "EN"));
		}
		return JCPLocalizationHandler.instance;
	}


	/**
	 *  Gets the string attribute of the JCPLocalizationHandler object
	 *
	 *@param  key  Description of the Parameter
	 *@return      The string value
	 */
	public String getString(String key)
	{
		return translations.getString(key);
	}
}

