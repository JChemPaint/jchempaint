package org.openscience.jchempaint;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.openscience.cdk.tools.LoggingTool;
import org.openscience.jchempaint.action.JCPAction;


/**
 * A class containing various helper methods used in JChemPaintMenuBar and JChemPaintPopupMenu.
 *
 */
public class JChemPaintMenuHelper {
	
	private LoggingTool logger;
	private JCPAction jcpaction;
	private static List<String> usedKeys = new ArrayList<String>();

	
	/**
	 * Constructor for JChemPaintMenuHelper
	 */
	public JChemPaintMenuHelper(){
		this.logger=new LoggingTool();
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
	protected String getMenuResourceString(String key, String guiString) {
		String str;
		try {
			str = JCPPropertyHandler.getInstance().getGUIDefinition(guiString).getString(key);
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
	 * @return           The created JMenu
	 */
	protected JComponent createMenu(JChemPaintPanel jcpPanel, String key, boolean isPopup, String guiString) {
		logger.debug("Creating menu: ", key);
		JMenu menu = new JMenu(JCPMenuTextMaker.getInstance(jcpPanel.getGuistring()).getText(key));
		menu.setName(key);
		return createMenu(jcpPanel, key, isPopup, guiString, menu);
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
	 * @return           The created JMenu
	 */
	protected JComponent createMenu(JChemPaintPanel jcpPanel, String key, boolean isPopup, String guiString, JComponent menu) {
		String[] itemKeys = StringHelper.tokenize(getMenuResourceString(key, guiString));
		for (int i = 0; i < itemKeys.length; i++) {
			if (itemKeys[i].equals("-")) {
				if(menu instanceof JMenu)
					((JMenu)menu).addSeparator();
				else
					((JPopupMenu)menu).addSeparator();
			}
			else if (itemKeys[i].startsWith("@")) {
				JComponent me = createMenu(jcpPanel, itemKeys[i].substring(1), isPopup, guiString);
				menu.add(me);
			}
			else {
				JMenuItem mi = createMenuItem(jcpPanel, itemKeys[i], isPopup);
				menu.add(mi);
			}
		}
		if(key.equals("atom")){
			jcpPanel.atomMenu=menu;
			menu.setEnabled(false);
		}
		if(key.equals("bond")){
			jcpPanel.bondMenu=menu;
			menu.setEnabled(false);
		}
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
	protected JMenuItem createMenuItem(JChemPaintPanel jcpPanel, String cmd, boolean isPopupMenu) {
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
		String translation = "***" + cmd + "***";
		try {
			translation = JCPMenuTextMaker.getInstance(jcpPanel.getGuistring()).getText(cmd);
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
		//this is to avoid to get a menu with the same name twice
		if(usedKeys.contains(cmd))
			mi.setName(cmd+"2");
		else
			mi.setName(cmd);
		usedKeys.add(cmd);
		logger.debug("Created new menu item...");
		String astr = JCPPropertyHandler.getInstance().getResourceString(cmd + JCPAction.actionSuffix);
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
		return mi;
	}
	
  
  /**
   *  Adds ShortCuts to the JChemPaintMenuBar object.<br>
   *  This simplifies and replaces the previous approach using Keycodes (VK_xxx)
   *  Keycodes do not work across different keyboards, notably the + key
   *  for zooming is problematic.<br>
   *  More here: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4262044
   *
   * @param  cmd  String The Strin to identify the MenuItem.
   * @param  mi   the regarding MenuItem.
   * @param  jcp  The JChemPaintPanel this menu is used for.
   */
  private void addShortCuts(String cmd, JMenuItem mi, JChemPaintPanel jcp) {
      Properties shortCutProps = JCPPropertyHandler.getInstance().getJCPShort_Cuts();

      String keyString = shortCutProps.getProperty(cmd);
      if (keyString != null) {
          KeyStroke keyStroke = KeyStroke.getKeyStroke(keyString);
          mi.setAccelerator(keyStroke);
          jcp.getInputMap().put(keyStroke, mi);
          jcp.getActionMap().put(mi, mi.getAction());
      }
   }
  
	
}
