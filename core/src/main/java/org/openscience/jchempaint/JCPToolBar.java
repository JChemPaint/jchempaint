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
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.util.UIScale;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.action.JCPAction;
import org.openscience.jchempaint.controller.AddBondDragModule;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IControllerModule;
import org.openscience.jchempaint.controller.SelectSquareModule;

/**
 *  This class makes the JCPToolBar
 *
 */
public class JCPToolBar extends JToolBar
{
    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(JCPToolBar.class);
    public static Color BUTON_ACTIVE_COLOR = new Color(98, 182, 207, 111);

    private static final float ICON_FONT_SIZE = 22.0f;
    // JWM: not entirely sure how to get the text centered correctly
    public static final Insets ICON_FONT_INSETS = new Insets(2, 0, 0, 0);

    private static Font iconFont;
    private static Properties iconFontMap;

    static {
        try (InputStream in = JCPToolBar.class.getResourceAsStream("fonts/JCPIcons.ttf")) {
            if (in != null) iconFont = Font.createFont(Font.TRUETYPE_FONT, in)
                                           .deriveFont(UIScale.scale(ICON_FONT_SIZE));
        } catch (FontFormatException | IOException e) {
            logger.error("Could not load JCP Icon font: " + e.getMessage());
        }
        try (InputStream in = JCPToolBar.class.getResourceAsStream("fonts/JCPIcons.map")) {
            if (in != null) {
                iconFontMap = new Properties();
                iconFontMap.load(in);
            }
        } catch (IOException e) {
            logger.error("Could not load JCP Icon font: " + e.getMessage());
        }
        if (iconFont == null || iconFontMap == null) {
            iconFont = null;
            iconFontMap = null;
        }
    }

    public JCPToolBar(int orientation) {
        super(orientation);
    }

    /**
     * Gets the toolbar attribute of the MainContainerPanel object
     *
     * @param chemPaintPanel the application/applet panel
     * @param key the toolbar key (the properties file is used to determine the items)
     * @param direction {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}
     * @param blocked A list of menuitesm/buttons which should be ignored when building gui.
     * @return The toolbar value
     */
    public static JComponent getToolbar(AbstractJChemPaintPanel chemPaintPanel,
                                        String key,
                                        int direction,
                                        Set<String> blocked)
    {
        ResourceBundle bundle = JCPPropertyHandler.getInstance(true)
                                                  .getGUIDefinition(chemPaintPanel.getGuistring());
        if (!bundle.containsKey(key))
            return null;
        String[] resourceStrings = bundle.getString(key).split(",");
        List<JToolBar> toolBars = new ArrayList<>();
        for (String resourceString : resourceStrings) {
            JToolBar toolbar = createToolbar(direction, resourceString.trim(), chemPaintPanel, blocked);
            if (toolbar != null)
                toolBars.add(toolbar);
        }

        if (toolBars.isEmpty())
            return null;
        else if (toolBars.size() == 1)
            return toolBars.get(0);
        else {
            // we make the box in the opposite orientation
            Box box = new Box(direction ^ 0x1);
            for (JToolBar toolBar : toolBars)
                box.add(toolBar);
            return box;
        }
    }

    /**
     * Gets the menuResourceString attribute of the JChemPaint object
     *
     * @param  key  Description of the Parameter
     * @return      The menuResourceString value
     */
    static String getToolbarResourceString(String key, String guistring)
    {
        String str;
        try
        {
            str = JCPPropertyHandler.getInstance(true).getGUIDefinition(guistring).getString(key);
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

    static JButton createToolbarButton(String key, AbstractJChemPaintPanel chemPaintPanel, boolean elementtype)
    {
        JCPPropertyHandler jcpph = JCPPropertyHandler.getInstance(true);
        JButton b;

        String symbol = iconFont != null ? iconFontMap.getProperty(key, null)
                                         : null;

        if (symbol != null && jcpph.getBool("useFontIcons", true))  {
            b = new JButton(symbol);
            b.setFont(iconFont);
            b.setVerticalTextPosition(SwingConstants.CENTER);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setMargin(ICON_FONT_INSETS);
        } else {
            // image icon
            logger.debug("Trying to find resource for key: ", key);
            URL url = jcpph.getResource(key + JCPAction.imageSuffix);
            logger.debug("Trying to find resource: ", url);
            if (url == null) {
                logger.error("Cannot find resource: ", key, JCPAction.imageSuffix);
                return null;
            }
            ImageIcon image = new ImageIcon(url);
            if (image.getImage() == null) {
                logger.error("Cannot find image: ", url);
                return null;
            }

            b = new JButton(image) {
                private static final long serialVersionUID = 1478990892406874403L;

                public float getAlignmentY() {
                    return 0.5f;
                }
            };

            URL disabledurl = jcpph.getOptionalResource(key + JCPAction.disabled_imageSuffix);
            logger.debug("Trying to find resource: ", url);
            if (disabledurl != null){
                b.setDisabledIcon(new ImageIcon(disabledurl));
            }
            b.setMargin(new Insets(1, 1, 1, 1));
        }

        String astr = null;
        if (elementtype)
            astr = jcpph.getResourceString("symbol" + key + JCPAction.actionSuffix);
        else
            astr = jcpph.getResourceString(key + JCPAction.actionSuffix);

        if (astr == null) {
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
        try {
            // TODO: use getMenuTextMaker?
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

        b.setRequestFocusEnabled(false);
        b.setName(key);
        b.setOpaque(false);
        b.setBackground(null);
        chemPaintPanel.buttons.put(key, b);

        return b;
    }


    /**
     * Creates a toolbar given by a String with all the buttons that are
     * specified in the properties file.
     *
     * @param orientation int The orientation of the toolbar
     * @param resourceString string used to configure the toolbar items
     * @param blocked A list of menuitems/buttons which should be ignored when
     * building gui.
     * @return Component The created toolbar
     */
    private static JToolBar createToolbar(int orientation,
                                          String resourceString,
                                          AbstractJChemPaintPanel chemPaintPanel,
                                          Set<String> blocked) {

        JCPToolBar toolbar = new JCPToolBar(orientation);
        if (resourceString == null) {
            return null;
        }
        if (orientation == SwingConstants.HORIZONTAL) {
            toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
        } else if (orientation == SwingConstants.VERTICAL) {
            toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.PAGE_AXIS));
        }

        String[] toolKeys = StringHelper.tokenize(resourceString);
        JButton button = null;
        for (String toolKey : toolKeys) {
            if (toolKey.equals("|")) {
                toolbar.addSeparator();
            } else if (toolKey.equals("-")) {
                toolbar.add(createSpacerButton());
            } else if (!blocked.contains(toolKey)) {
                button = createToolbarButton(toolKey, chemPaintPanel, toolKey.length() < 3);
                /*
                 * if (toolKeys[i].equals("lasso")) { selectButton = button; }
                 */
                if (button != null) {
                    toolbar.add(button);
                    if (toolKey.equals("bondTool")) {
                        chemPaintPanel.setLastActionButton(button);
                        AddBondDragModule activeModule = new AddBondDragModule(chemPaintPanel.get2DHub(), IBond.Display.Solid, true);
                        activeModule.setID(toolKey);
                        chemPaintPanel.get2DHub().setActiveDrawModule(activeModule);
                    }
                } else {
                    logger.error("Could not create button" + toolKey);
                }
            }
        }

        if (orientation == SwingConstants.HORIZONTAL) {
            toolbar.add(Box.createHorizontalGlue());
        } else {
            toolbar.add(Box.createVerticalGlue());
        }

        ControllerHub relay = chemPaintPanel.get2DHub();
        IControllerModule m = new SelectSquareModule(relay);
        m.setID("select");
        relay.setFallbackModule(m);
        return toolbar;
    }

    private static JButton createSpacerButton() {
        JButton blank = new JButton(" ");
        if (iconFont != null)
            blank.setFont(iconFont);
        blank.setVerticalTextPosition(SwingConstants.CENTER);
        blank.setHorizontalTextPosition(SwingConstants.CENTER);
        blank.setMargin(ICON_FONT_INSETS);
        blank.setEnabled(false);
        blank.setOpaque(false);
        blank.setBorder(new EmptyBorder(2,2,2,2));
        blank.setBackground(null);
        return blank;
    }
}
