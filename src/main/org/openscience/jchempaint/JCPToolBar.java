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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.net.URL;
import java.util.MissingResourceException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicBorders;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.controller.AddBondDragModule;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.jchempaint.action.ChangeAtomSymbolAction;
import org.openscience.jchempaint.action.JCPAction;

/**
 *  This class makes the JCPToolBar
 *
 */
public class JCPToolBar
{

    private static IAtomColorer colorer = new CDK2DAtomColors();
    private static LoggingTool logger;
    public static Color BUTTON_INACTIVE_COLOR=Color.WHITE;//new Color(230,230,230);
    /**
     *  Gets the toolbar attribute of the MainContainerPanel object
     *
     *@return    The toolbar value
     */
    public static JToolBar getToolbar(JChemPaintPanel chemPaintPanel, String key, int horizontalorvertical)
    {
        //Controller2DHub hub
        if (logger == null)
        {
            logger = new LoggingTool(JCPToolBar.class);
        }

        JToolBar maintoolbar=(JToolBar)createToolbar(horizontalorvertical, key, chemPaintPanel, 1);
        return maintoolbar;
    }


    /**
     *  Gets the menuResourceString attribute of the JChemPaint object
     *
     *@param  key  Description of the Parameter
     *@return      The menuResourceString value
     */
    static String getToolbarResourceString(String key, String guistring)
    {
        String str;
        try
        {
            str = JCPPropertyHandler.getInstance().getGUIDefinition(guistring).getString(key);
        } catch (MissingResourceException mre)
        {
            mre.printStackTrace();
            str = null;
        }
        return str;
    }


    /**
     *  Creates a JButton given by a String with an Image and adds the right
     *  ActionListener to it.
     *
     *@param  key  String The string used to identify the button
     *@param  elementtype  If true a special type of button for element symbols will be created
     *@return      JButton The JButton with already added ActionListener
     */

    static JButton createToolbarButton(String key, JChemPaintPanel chemPaintPanel, boolean elementtype)
    {
        JCPPropertyHandler jcpph = JCPPropertyHandler.getInstance();
        JButton b = null;

        /*if(!elementtype){ */

        logger.debug("Trying to find resource for key: ", key);
        URL url = jcpph.getResource(key + JCPAction.imageSuffix);
        logger.debug("Trying to find resource: ", url);
        if (url == null)
        {
            logger.error("Cannot find resource: ", key, JCPAction.imageSuffix);
            return null;
        }
        ImageIcon image = new ImageIcon(url);
        if (image == null)
        {
            logger.error("Cannot find image: ", url);
            return null;
        }
        b =
            new JButton(image)
        {
            private static final long serialVersionUID = 1478990892406874403L;

            public float getAlignmentY()
            {
                return 0.5f;
            }
        };
        String astr=null;
        if (elementtype)
            astr = jcpph.getResourceString("symbol"+key + JCPAction.actionSuffix);
        else
            astr = jcpph.getResourceString(key + JCPAction.actionSuffix);
            
        if (astr == null)
        {
            astr = key;
        }
        JCPAction a = new JCPAction().getAction(chemPaintPanel, astr);
        if (a != null)
        {
            b.setActionCommand(astr);
            logger.debug("Coupling action to button...");
            b.addActionListener(a);
            b.setEnabled(a.isEnabled());
        } else
        {
            logger.error("Could not find JCPAction class for:", astr);
            b.setEnabled(false);
        }
        try
        {
            String tip = JCPMenuTextMaker.getInstance("applet").getText(key + JCPAction.TIPSUFFIX);
            if (tip != null)
            {
                b.setToolTipText(tip);
            }
        } catch (MissingResourceException e)
        {
            logger.warn("Could not find Tooltip resource for: ", key);
            logger.debug(e);
        }
        URL disabledurl = jcpph.getResource(key + JCPAction.disabled_imageSuffix);
        logger.debug("Trying to find resource: ", url);
        if (disabledurl != null){
            ImageIcon disabledimage = new ImageIcon(disabledurl);
            if (image != null){
                b.setDisabledIcon(disabledimage);
            }
        }
        /* OBSOLETE - remove

                }
                else{ 
			b=new JButton();
			//We use a panel instead of setting the text directly so that the 
			//buttons get a border when being activated (other buttons have an 
			//icon with while background for that effect)
			JPanel innerPanel=new JPanel();
			innerPanel.setBackground(Color.white);
			innerPanel.setPreferredSize(new Dimension(28,28));
			innerPanel.setMaximumSize(new Dimension(28,28));
			innerPanel.setSize(new Dimension(28,28));
			JLabel label = new JLabel(key);
                        label.setForeground(colorer.getAtomColor(DefaultChemObjectBuilder.getInstance().newAtom(key)));
			innerPanel.add(label);
			b.add(innerPanel);
			ChangeAtomSymbolAction a = new ChangeAtomSymbolAction();
			a.setJChemPaintPanel(chemPaintPanel);
			b.setActionCommand(a.getClass().getName()+"@"+key);
			b.addActionListener(a);
			b.setEnabled(a.isEnabled());
			b.setToolTipText(GT._("Change drawing symbol to {0}",key));
			b.setSize(32,32);
			b.setPreferredSize(new Dimension(32,32));
			b.setMaximumSize(new Dimension(32,32));
		}
         */
        b.setRequestFocusEnabled(false);
        b.setMargin(new Insets(1, 1, 1, 1));
        b.setName(key);
        chemPaintPanel.buttons.put(key, b);

        CompoundBorder compBorder1 = new CompoundBorder(new EmptyBorder(0,0,0,0), new LineBorder(new Color(164,164,164),1, true));
        Color highlighter = new Color(80,144,166);
        CompoundBorder compBorder2 = new CompoundBorder(new BasicBorders.RolloverButtonBorder(highlighter, highlighter, highlighter, highlighter),compBorder1);
        b.setBorder(compBorder2); 
        return b;
    }


    /**
     *  Creates a toolbar given by a String with all the buttons that are specified
     *  in the properties file.
     *
     *@param  orientation  int The orientation of the toolbar
     *@param  kind         String The String used to identify the toolbar
     *@return              Component The created toolbar
     */
    public static Component createToolbar(int orientation, String kind, JChemPaintPanel chemPaintPanel, int lines)
    {
        JToolBar toolbar2 = new JToolBar(orientation);
        String[] toolKeys = StringHelper.tokenize(getToolbarResourceString(kind, chemPaintPanel.getGuistring()));
        JButton button = null;
        Box box=null;
        int counter=0;
        for (int i = 0; i < toolKeys.length; i++)
        {
            if (toolKeys[i].equals("-"))
            {
                toolbar2.add(box);
                if (orientation == SwingConstants.HORIZONTAL)
                {
                    toolbar2.add(Box.createHorizontalStrut(5));
                } else if (orientation == SwingConstants.VERTICAL)
                {
                    toolbar2.add(Box.createVerticalStrut(5));
                }
                counter=0;
            } 
            else
            {
                if(counter % lines==0){
                    if(box!=null)
                        toolbar2.add(box);
                    box=new Box(BoxLayout.Y_AXIS);
                }
                button = (JButton) createToolbarButton(toolKeys[i], chemPaintPanel, toolKeys[i].length()<3);
                /*if (toolKeys[i].equals("lasso"))
				{
					selectButton = button;
				}*/
                if (button != null)
                {
                    box.add(button);
                    if (toolKeys[i].equals("bond"))
                    {
                        //button.setBackground(Color.GRAY);
                        button.setBackground(new Color(238,238,238)); 
                        chemPaintPanel.setLastActionButton(button);
                        AddBondDragModule activeModule = new AddBondDragModule(chemPaintPanel.get2DHub(), IBond.Stereo.NONE, true);
                        activeModule.setID(toolKeys[i]);
                        chemPaintPanel.get2DHub().setActiveDrawModule(activeModule);
                        chemPaintPanel.updateStatusBar();
                    } else if(toolKeys[i].equals("C")){
                        button.setBackground(Color.GRAY);
                        chemPaintPanel.setLastSecondaryButton(button);
                    } else
                    {
                        button.setBackground(BUTTON_INACTIVE_COLOR);
                    }
                } else
                {
                    logger.error("Could not create button"+toolKeys[i]);
                }
                counter++;
            }
        }
        if(box!=null)
            toolbar2.add(box);
        if (orientation == SwingConstants.HORIZONTAL)
        {
            toolbar2.add(Box.createHorizontalGlue());
        }
        return toolbar2;
    }
}

