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

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

/**
 *  A property manager for JChemPaint.
 *
 */
public class JCPPropertyHandler
{

	private static JCPPropertyHandler jcpPropsHandler = null;

	private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(JCPPropertyHandler.class);
	private Properties currentProperties;
	//private File defaultPropsFile;
	private File userPropsFile;
	//private File userAtypeFile;
	private File uhome;
	private File ujcpdir;
	private ResourceBundle guiDefinition;
	private ResourceBundle resources;
	private Properties shortCutProps;

    private boolean useUserSettings;
	private static boolean oldUseUserSettings;


	/**
	 * Constructor for the JCPPropertyHandler.
	 * 
	 * @param useUserSettings Should user setting (in $HOME/.jchempaint/properties) be used or not?
	 */
	public JCPPropertyHandler(boolean useUserSettings) {
        this.useUserSettings = useUserSettings;
    }


    /**
	 *  Gets the instance attribute of the JCPPropertyHandler class.
	 *
     * @param useUserSettings Should user setting (in $HOME/.jchempaint/properties) be used or not?
	 * @return    The instance value.
	 */
	public static JCPPropertyHandler getInstance(boolean useUserSettings)
	{
		if (jcpPropsHandler == null || oldUseUserSettings!=useUserSettings)
		{
			jcpPropsHandler = new JCPPropertyHandler(useUserSettings);
			oldUseUserSettings = useUserSettings;
		}
		return jcpPropsHandler;
	}


	/**
	 *  Gets the jCPProperties attribute of the JCPPropertyHandler object
	 *
	 *@return    The jCPProperties value
	 */
	public Properties getJCPProperties()
	{
		if (currentProperties == null)
		{
			reloadProperties(useUserSettings);
		}
		return currentProperties;
	}

	public void reloadProperties(boolean useUserSettings)
	{
		Properties applicationProps = null;
		Properties defaultProps = null;
		InputStream defaultStream;
		try
		{
			defaultStream = this.getClass().getClassLoader().getResourceAsStream("org/openscience/jchempaint/resources/JChemPaintResources.properties");
			defaultProps = new Properties();
			defaultProps.load(defaultStream);
			defaultStream.close();
			//the language setting is not in default file, but taken from platform settings
			defaultProps.setProperty("General.language",GT.getLanguage());
			logger.info("Loaded properties from jar");
		} catch (Exception exception)
		{
			logger.error("There was a problem retrieving JChemPaint's default properties.");
			logger.debug(exception);
		}

        applicationProps = new Properties(defaultProps);
		if(useUserSettings){
    		try
    		{
    			// set up real properties
    			FileInputStream appStream = new FileInputStream(getUserPropsFile());
    			applicationProps.load(appStream);
    			appStream.close();
    			logger.info("Loaded user properties from file");
    		} catch (FileNotFoundException exception)
    		{
    			logger.warn("User does not have localized properties in ");
    		} catch (Exception exception)
    		{
    			logger.error("There was a problem retrieving the user properties from file");
    			logger.debug(exception);
    		}
		}
		currentProperties = applicationProps;
	}

	public void saveProperties()
	{
		try
		{
			FileOutputStream appStream = new FileOutputStream(getUserPropsFile());
			currentProperties.store(appStream, null);
			appStream.flush();
			appStream.close();
			logger.info("Properties save to ", getUserPropsFile());
		} catch (Exception exception)
		{
			logger.error("An error has occured while storing properties");
			logger.error("to file ");
			logger.debug(exception);
		}
	}


	/**
	 *  Gets the userHome attribute of the JCPPropertyHandler object
	 *
	 *@return    The userHome value
	 */
	public File getUserHome()
	{
		if (uhome == null)
		{
			try
			{
				uhome = new File(System.getProperty("user.home"));
			} catch (Exception exc)
			{
				logger.error("Could not read a system property. Failing!");
				logger.debug(exc);
			}
		}
		return uhome;
	}


	/**
	 *  Gets the jChemPaintDir attribute of the JCPPropertyHandler object
	 *
	 *@return    The jChemPaintDir value
	 */
	public File getJChemPaintDir()
	{
		if (ujcpdir == null)
		{
			try
			{
				ujcpdir = new File(getUserHome(), ".jchempaint");
				ujcpdir.mkdirs();
			} catch (Exception exc)
			{
				logger.error("Could read a JChemPaint dir. I might be in a sandbox.");
				logger.debug(exc);
			}
		}
		return ujcpdir;
	}


	/**
	 *  Gets the userPropsFile attribute of the JCPPropertyHandler object
	 *
	 *@return    The userPropsFile value
	 */
	public File getUserPropsFile()
	{
		if (userPropsFile == null)
		{
			try
			{
				userPropsFile = new File(getJChemPaintDir(), "properties");
			} catch (Exception exc)
			{
				logger.error("Could not read a system property. I might be in a sandbox.");
				logger.debug(exc);
			}
		}
		return userPropsFile;
	}


    public ResourceBundle getGUIDefinition(String guiString) {
        try {
            String resource = "org.openscience.jchempaint.resources.JCPGUI_" + guiString;
            guiDefinition = ResourceBundle.getBundle(resource, Locale.getDefault());
        } catch (Exception exc) {
            logger.error("Could not read a GUI definition: " + exc.getMessage());
            logger.debug(exc);
        }
        return guiDefinition;
    }

	public Properties getJCPShort_Cuts() {
		if (shortCutProps == null) {
			try {
				String propertiesFile = "org/openscience/jchempaint/resources/JCPShort_Cuts.properties";
				shortCutProps = new Properties();
				InputStream appStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFile);
				shortCutProps.load(appStream);
				appStream.close();
			} catch (FileNotFoundException fnfe) {fnfe.printStackTrace();} catch (IOException ioe) {}
		}
		return shortCutProps;
	}

	/**
	 *  Gets the resources attribute of the JCPPropertyHandler object
	 *
	 *@return    The resources value
	 */
	public ResourceBundle getResources()
	{
		if (resources == null)
		{
			try
			{
				String resource = "org.openscience.jchempaint.resources.JChemPaintResources";
				resources = ResourceBundle.getBundle(resource);
			} catch (Exception exc)
			{
				logger.error("Could not read the resources.");
				logger.debug(exc);
			}
		}
		return resources;
	}


	/**
	 * Returns an URL build from the path of this object and another part that is
	 * searched in the properties file. Used to find the images for the buttons.
	 *
	 * @param  key  String The String that says which image is searched
	 * @return      URL The URL where the image is located
	 */
	public URL getResource(String key)
	{
		String name = getResourceString(key);
		logger.debug("resource name: ", name);
		if (name != null)
		{
			URL url = this.getClass().getResource(name);
			return url;
		} else
		{
			logger.error("ResourceString is null for: ", key);
		}
		return null;
	}

	/**
	 * Returns the resource URL from the properties file that follows the given
	 * String, if the resource is not found, no log message is emitted.
	 *
	 * @param  key  String The String to be looked after
	 * @return      URL The URL where the image is located
	 */
	public URL getOptionalResource(String key)
	{
		try {
			return getClass().getResource(getResources().getString(key));
		} catch (MissingResourceException ignore) {
			return null;
		}
	}


	/**
	 * Returns the ResourceString from the properties file that follows the given
	 * String.
	 *
	 * @param  key  String The String to be looked after
	 * @return      String The String that follows the key in the properties file
	 */
	public String getResourceString(String key)
	{
		String str;
		try
		{
			str = getResources().getString(key);
		} catch (MissingResourceException mre)
		{
			logger.error("Could not find resource: ", mre.getMessage());
			logger.debug(mre);
			str = null;
		}
		return str;
	}
	
	public String getVersion(){
	    return this.getJCPProperties().getProperty("General.JCPVersion");
	}

    /**
     * Set rendering preferences using this property handler instance.  
     * 
     * @param model rendering model
     */
    public void setRenderingPreferences(JChemPaintRendererModel model) {
        model.setAtomRadius(Double.parseDouble(JCPPropertyHandler.getInstance(useUserSettings)
                                                          .getJCPProperties().getProperty("AtomRadius")));
        model.setBackColor(new Color(Integer.parseInt(JCPPropertyHandler.getInstance(useUserSettings)
                                                                 .getJCPProperties().getProperty("BackColor", String.valueOf(Color.white.getRGB())))));
        model.setBondWidth(Double.parseDouble(JCPPropertyHandler.getInstance(useUserSettings)
                                                                .getJCPProperties().getProperty("BondWidth")));
        model.setCompactShape(JCPPropertyHandler.getInstance(useUserSettings).getJCPProperties()
                                         .getProperty("CompactShape").equals("square") ? BasicAtomGenerator.Shape.SQUARE : BasicAtomGenerator.Shape.OVAL);
        model.setIsCompact(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                         .getJCPProperties().getProperty("IsCompact")));
        model.setColorAtomsByType(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                                  .getJCPProperties().getProperty("ColorAtomsByType")));
        model.setShowImplicitHydrogens(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                                       .getJCPProperties().getProperty("ShowImplicitHydrogens")));
        model.setDrawNumbers(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                                 .getJCPProperties().getProperty("DrawNumbers")));
        model.setKekuleStructure(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                                 .getJCPProperties().getProperty("KekuleStructure")));
        model.setShowEndCarbons(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                                .getJCPProperties().getProperty("ShowEndCarbons")));
        model.setShowExplicitHydrogens(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                                       .getJCPProperties().getProperty("ShowExplicitHydrogens")));
        model.setHighlightDistance(Double.parseDouble(JCPPropertyHandler.getInstance(useUserSettings)
                                                                 .getJCPProperties().getProperty("HighlightDistance")));
        model.setFitToScreen(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                             .getJCPProperties().getProperty("FitToScreen")));
        model.setShowAromaticity(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                                 .getJCPProperties().getProperty("ShowAromaticity")));
        model.setWedgeWidth(Double.parseDouble(JCPPropertyHandler.getInstance(useUserSettings)
                                                          .getJCPProperties().getProperty("WedgeWidth")));
        model.setShowReactionBoxes(Boolean.parseBoolean(JCPPropertyHandler.getInstance(useUserSettings)
                                                                   .getJCPProperties().getProperty("ShowReactionBoxes")));    
    }
}
