/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-05-01 20:15:34 +0100 (Tue, 01 May 2007) $
 *  $Revision: 8292 $
 *
 *  Copyright (C) 2003-2007  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sf.net
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
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JCheckBoxMenuItem;

import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.validate.BasicValidator;
import org.openscience.cdk.validate.CDKValidator;
import org.openscience.cdk.validate.DictionaryValidator;
import org.openscience.cdk.validate.ValidatorEngine;
import org.openscience.jchempaint.JCPPropertyHandler;
import org.openscience.jchempaint.dialog.ValidateFrame;


/**
 * An action opening a validation frame
 * 
 * @cdk.module jchempaint
 * @author     E.L. Willighagen <elw38@cam.ac.uk>
 */
public class ValidateAction extends JCPAction
{

    private static final long serialVersionUID = -3776589605934024224L;

	private static ValidatorEngine engine;

	private static DictionaryDatabase dictdb;
    
    ValidateFrame frame = null;

	public void actionPerformed(ActionEvent event)
	{
		logger.debug("detected validate action: ", type);
		if (type.equals("run"))
		{
			IChemObject object = getSource(event);
			if (object == null)
			{
				// called from main menu
				org.openscience.cdk.interfaces.IChemModel model = jcpPanel.getChemModel();
				if (model != null)
				{
					runValidate(model);
				} else
				{
					System.out.println("Empty model");
				}
			} else
			{
				// calleb from popup menu
				logger.debug("Validate called from popup menu!");
				runValidate(object);
			}
		} else if (type.equals("clear"))
		{
			clearValidate();
		} else if (type.startsWith("toggle") && type.length() > 6)
		{
			String toggle = type.substring(6);
			try
			{
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
				boolean newChecked = !menuItem.isSelected();
				menuItem.setSelected(newChecked);
				if (toggle.equals("Basic"))
				{
					if (newChecked)
					{
						logger.info("Turned on " + toggle);
						getValidatorEngine().addValidator(new BasicValidator());
					} else
					{
						logger.info("Turned off " + toggle);
						getValidatorEngine().removeValidator(new BasicValidator());
					}
				} else if (toggle.equals("CDK"))
				{
					if (newChecked)
					{
						logger.info("Turned on " + toggle);
						getValidatorEngine().addValidator(new CDKValidator());
					} else
					{
						logger.info("Turned off " + toggle);
						getValidatorEngine().removeValidator(new CDKValidator());
					}
				} else
				{
					logger.error("Don't know what to toggle: " + toggle);
				}
			} catch (ClassCastException exception)
			{
				logger.error("Cannot toggle a non JCheckBoxMenuItem!");
			}
		} else
		{
			logger.error("Unknown command: " + type);
		}
	}

	private void clearValidate()
	{
		jcpPanel.get2DHub().clearValidation();
		jcpPanel.get2DHub().updateView();
	}

	private void runValidate(IChemObject object)
	{
		logger.info("Running validation");
		clearValidate();
		if (jcpPanel.getChemModel() != null)
		{
			frame = new ValidateFrame(jcpPanel);
			frame.validate(object);
			frame.pack();
			frame.setVisible(true);
		}
	}
	
	/**
	 *  Gets the validatorEngine attribute of the JChemPaintEditorPanel class
	 *
	 *@return    The validatorEngine value
	 */
	public static ValidatorEngine getValidatorEngine()
	{
		if (engine == null)
		{
			engine = new ValidatorEngine();
			// default validators
			engine.addValidator(new BasicValidator());
			engine.addValidator(new CDKValidator());
			engine.addValidator(new DictionaryValidator(getDictionaryDatabase()));
		}
		return engine;
	}
	
	/**
	 *  Gets the dictionaryDatabase attribute of the JChemPaint object
	 *
	 *@return    The dictionaryDatabase value
	 */
	public static DictionaryDatabase getDictionaryDatabase()
	{
		if (dictdb == null)
		{
			dictdb = new DictionaryDatabase();
			try
			{
				File dictdir = new File(JCPPropertyHandler.getInstance().getJChemPaintDir(), "dicts");
				logger.info("User dict dir: ", dictdir);
				logger.debug("       exists: ", dictdir.exists());
				logger.debug("  isDirectory: ", dictdir.isDirectory());
				if (dictdir.exists() && dictdir.isDirectory())
				{
					File[] dicts = dictdir.listFiles();
					for (int i = 0; i < dicts.length; i++)
					{
						// loop over these files and load them
						try
						{
							FileReader reader = new FileReader(dicts[i]);
							String filename = dicts[i].getName();
							dictdb.readDictionary(reader, filename.substring(0, filename.indexOf('.')));
						} catch (IOException exception)
						{
							logger.error("Problem with reading macie dictionary...");
						}
					}
				}
				logger.info("Read these dictionaries: ");
				Enumeration dicts = dictdb.listDictionaries();
				while (dicts.hasMoreElements())
				{
					logger.info(" - ", dicts.nextElement().toString());
				}
			} catch (Exception exc)
			{
				logger.error("Could not handle dictionary initialization. Maybe I'm running in a sandbox.");
			}
		}
		return dictdb;
	}

}

