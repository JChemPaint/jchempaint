package org.openscience.jchempaint;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.action.JCPAction;


/**
 * A class containing various helper methods used in JChemPaintMenuBar and JChemPaintPopupMenu.
 *
 */
public class JChemPaintMenuHelper {

    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(JChemPaintMenuHelper.class);
    private JCPAction jcpaction;
    private static List<String> usedKeys;


    /**
     * Constructor for JChemPaintMenuHelper
     */
    public JChemPaintMenuHelper(){
        usedKeys = new ArrayList<String>();
    }


    /**
     *  Return the JCPAction instance associated with this JCPPanel.
     *
     *  @return    The jCPAction value
     */
    private JCPAction getJCPAction() {
        if (jcpaction == null) {
            jcpaction = new JCPAction();
        }
        return jcpaction;
    }

    /**
     *  Returns the definition of the subitems of a menu as in the properties files.
     *
     * @param  key       The key for which subitems to return
     * @param  guiString The string identifying the gui to build (i. e. the properties file to use)
     * @return      	 The resource string
     */
    public String getMenuResourceString(String key, String guiString) {
        String str;
        try {
            str = JCPPropertyHandler.getInstance(true).getGUIDefinition(guiString).getString(key);
        } catch (MissingResourceException mre) {
            str = null;
        }
        return str;
    }

    /**
     *  Creates a JMenu given by a String with all the MenuItems specified in the
     *  properties file.
     *
     * @param  key       The String used to identify the Menu
     * @param  jcpPanel  Description of the Parameter
     * @param  isPopup   Tells if this menu will be a popup one or not
     * @param  guiString The string identifying the gui to build (i. e. the properties file to use)
  	 * @param  blocked       A list of menuitesm/buttons which should be ignored when building gui.
     * @return           The created JMenu
     */
    protected JComponent createMenu(AbstractJChemPaintPanel jcpPanel, String key, boolean isPopup, String guiString, Set<String> blocked) {
        logger.debug("Creating menu: ", key);
        JMenu menu = new JMenu(jcpPanel.getMenuTextMaker().getText(key));
        menu.setName(key);
        return createMenu(jcpPanel, key, isPopup, guiString, menu, blocked);
    }


    /**
     *  Creates a JMenu given by a String with all the MenuItems specified in the
     *  properties file.
     *
     * @param  key       The String used to identify the Menu
     * @param  jcpPanel  Description of the Parameter
     * @param  isPopup   Tells if this menu will be a popup one or not
     * @param  guiString The string identifying the gui to build (i. e. the properties file to use)
     * @param  menu		 The menu to add the new menu to (must either be JMenu or JPopupMenu)
  	 * @param  blocked       A list of menuitesm/buttons which should be ignored when building gui.
     * @return           The created JMenu
     */
    protected JComponent createMenu(final AbstractJChemPaintPanel jcpPanel, String key, boolean isPopup, String guiString, final JComponent menu, Set<String> blocked) {
    	// block entire menu
    	if (blocked.contains(key)){
    		return null;
    	}

    	String[] itemKeys = StringHelper.tokenize(getMenuResourceString(key, guiString));
        for (int i = 0; i < itemKeys.length; i++) {
        	if (!blocked.contains(itemKeys[i]) && !blocked.contains(itemKeys[i].substring(1))){
	            if (itemKeys[i].equals("-")) {
	                if(menu instanceof JMenu)
	                    ((JMenu)menu).addSeparator();
	                else
	                    ((JPopupMenu)menu).addSeparator();
	            }
	            else if (itemKeys[i].startsWith("@")) {
	                JComponent me = createMenu(jcpPanel, itemKeys[i].substring(1), isPopup, guiString, blocked);
	                menu.add(me);
	            }
	            else {
	                JMenuItem mi = createMenuItem(jcpPanel, itemKeys[i], isPopup);
	                menu.add(mi);
	            }
        	}
        }

        if(key.equals("isotopeChange")){
            ((JMenu)menu).addMenuListener(new MenuListener(){
                public void menuCanceled(MenuEvent arg0) {
                }

                public void menuDeselected(MenuEvent arg0) {
                }

                public void menuSelected(MenuEvent arg0) {
                    menu.removeAll();
                    //the following condition is nasty, but necessary, since depending on if model and/or selection
                    //is empty, various commands return null
                    if((SwingPopupModule.lastAtomPopupedFor!=null && SwingPopupModule.lastAtomPopupedFor.getSymbol()!=null)
                            || (jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection()!=null && 
                                    !jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().elements(IAtom.class).isEmpty())){
                        try {
                            String symbol;
                            if(jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer()!=null &&
                               !jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().elements(IAtom.class).isEmpty())
                                symbol = jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().getAtom(0).getSymbol();
                            else
                                symbol = SwingPopupModule.lastAtomPopupedFor.getSymbol();
                            IIsotope[] isotopes = Isotopes.getInstance().getIsotopes(symbol);
                            for(int i=0;i<isotopes.length;i++){
                                String cmd=isotopes[i].getSymbol()+isotopes[i].getMassNumber();
                                JMenuItem mi = new JMenuItem(cmd);
                                mi.setName(cmd);
                                usedKeys.add(cmd);
                                String astr="org.openscience.jchempaint.action.ChangeIsotopeAction@specific"+isotopes[i].getMassNumber();
                                mi.setActionCommand(astr);
                                JCPAction action = getJCPAction().getAction(jcpPanel, astr, false);
                                if (action != null) {
                                    // sync some action properties with menu
                                    mi.setEnabled(action.isEnabled());
                                    mi.addActionListener(action);
                                    logger.debug("Coupled action to new menu item...");
                                }
                                menu.add(mi);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }							
                    }else{
                        menu.add(new JChemPaintMenuHelper().createMenuItem(jcpPanel, "majorPlusThree", false));
                        menu.add(new JChemPaintMenuHelper().createMenuItem(jcpPanel, "majorPlusTwo", false));
                        menu.add(new JChemPaintMenuHelper().createMenuItem(jcpPanel, "majorPlusOne", false));
                        menu.add(new JChemPaintMenuHelper().createMenuItem(jcpPanel, "major", false));
                        menu.add(new JChemPaintMenuHelper().createMenuItem(jcpPanel, "majorMinusOne", false));
                        menu.add(new JChemPaintMenuHelper().createMenuItem(jcpPanel, "majorMinusTwo", false));
                        menu.add(new JChemPaintMenuHelper().createMenuItem(jcpPanel, "majorMinusThree", false));
                        jcpPanel.enOrDisableMenus((JMenu)menu, jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection()==null || !jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().isFilled() && jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getHighlightedAtom()==null ? false : true);
                    }
                }
            });
        }
        //we make large menus two columns
        JPopupMenu p;
        if(menu instanceof JMenu)
            p = ((JMenu)menu).getPopupMenu();
        else
            p = (JChemPaintPopupMenu)menu;
        if(p.getComponents().length>30){
            Dimension d = p.getPreferredSize();
            d = new Dimension((int)(d.width * 2.5), (int)(d.height * 0.7));
            p.setPreferredSize(d);
            p.setLayout(new FlowLayout());
        }
        if(menu instanceof JMenu)
            jcpPanel.menus.add((JMenu)menu);
        else if(menu instanceof JChemPaintPopupMenu)
            jcpPanel.popupmenuitems.put(key, (JChemPaintPopupMenu)menu);
        return menu;
    }


    /**
     *  Creates a JMenuItem given by a String and adds the right ActionListener to
     *  it.
     *
     * @param  cmd         String The String to identify the MenuItem
     * @param  jcpPanel    Description of the Parameter
     * @param  isPopupMenu Tells if this menu will be a popup one or not
     * @return             JMenuItem The created JMenuItem
     */
    protected JMenuItem createMenuItem(AbstractJChemPaintPanel jcpPanel, String cmd, boolean isPopupMenu) {
        logger.debug("Creating menu item: ", cmd);
        boolean isCheckBox=false;
        if (cmd.endsWith("+")){
            isCheckBox=true;
            cmd=cmd.substring(0, cmd.length() - 1);
        }
        boolean isChecked=false;
        if (cmd.endsWith("+")){
            isChecked=true;
            cmd=cmd.substring(0, cmd.length() - 1);
        }

        JCPPropertyHandler jcpph = JCPPropertyHandler.getInstance(true);
        Properties userProps = jcpph.getJCPProperties();
        if (userProps.containsKey(cmd + ".value")) {
            isChecked = Boolean.parseBoolean(userProps.getProperty(cmd + ".value"));
        }
        
        String translation = "***" + cmd + "***";
        try {
            translation = jcpPanel.getMenuTextMaker().getText(cmd);
            logger.debug("Found translation: ", translation);
        } catch (MissingResourceException mre) {
            logger.error("Could not find translation for: " + cmd);
        }
        JMenuItem mi = null;
        if (isCheckBox) {
            mi = new JCheckBoxMenuItem(translation);
            mi.setSelected(isChecked);
        }
        else {
            mi = new JMenuItem(translation);
        }

        if (!jcpph.getBool("useFontIcons", true)) {
            URL url = jcpph.getOptionalResource(cmd + JCPAction.imageSuffix);
            if (url != null) {
                ImageIcon image = new ImageIcon(url);
                Image img = image.getImage();
                Image newimg = img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
                mi.setIcon(new ImageIcon(newimg));
                URL disabledurl = jcpph.getOptionalResource(cmd + JCPAction.disabled_imageSuffix);
                if (disabledurl != null) {
                    ImageIcon disabledimage = new ImageIcon(disabledurl);
                    if (image != null) {
                        Image disabledimg = disabledimage.getImage();
                        Image disablednewimg = disabledimg.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
                        mi.setDisabledIcon(new ImageIcon(disablednewimg));
                    }
                }
            }
        }
        //this is to avoid to get a menu with the same name twice
        if(usedKeys.contains(cmd))
            mi.setName(cmd+"2");
        else
            mi.setName(cmd);
        usedKeys.add(cmd);
        logger.debug("Created new menu item...");
        String astr = JCPPropertyHandler.getInstance(true).getResourceString(cmd + JCPAction.actionSuffix);
        if (astr == null) {
            astr = cmd;
        }
        mi.setActionCommand(astr);
        JCPAction action = getJCPAction().getAction(jcpPanel, astr, isPopupMenu);
        if (action != null) {
            // sync some action properties with menu
            mi.setEnabled(action.isEnabled());
            mi.addActionListener(action);
            logger.debug("Coupled action to new menu item...");
        }
        else {
            logger.error("Could not find JCPAction class for:" + astr);
            mi.setEnabled(false);
        }
        if(!isPopupMenu)
            addShortCuts(cmd, mi, jcpPanel);
        if(cmd.equals("undo"))
            jcpPanel.undoMenu=mi;
        if(cmd.equals("redo"))
            jcpPanel.redoMenu=mi;
        jcpPanel.menus.add((JMenuItem)mi);
        return mi;
    }


    /**
     *  Adds ShortCuts to the JChemPaintMenuBar object.<br>
     *  This simplifies and replaces the previous approach using Keycodes (VK_xxx)
     *  Keycodes do not work across different keyboards, notably the + key
     *  for zooming is problematic.<br>
     *  More here: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4262044
     *
     * @param  cmd  String The String to identify the MenuItem.
     * @param  mi   the regarding MenuItem.
     * @param  jcp  The JChemPaintPanel this menu is used for.
     */
    private void addShortCuts(String cmd, JMenuItem mi, AbstractJChemPaintPanel jcp) {
        Properties shortCutProps = JCPPropertyHandler.getInstance(true).getJCPShort_Cuts();

        String keyString = shortCutProps.getProperty(cmd);
        if (keyString != null) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(keyString);
            mi.setAccelerator(keyStroke);
            jcp.getInputMap().put(keyStroke, mi);
            jcp.getActionMap().put(mi, mi.getAction());
        }
    }


}
