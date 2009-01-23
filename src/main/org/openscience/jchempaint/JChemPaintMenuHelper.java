package org.openscience.jchempaint;

import java.lang.reflect.Field;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openscience.jchempaint.action.JCPAction;
import org.openscience.cdk.tools.LoggingTool;

public class JChemPaintMenuHelper {
	
	private LoggingTool logger;
	private JCPAction jcpaction;

	
	public JChemPaintMenuHelper(){
		this.logger=new LoggingTool();
	}

	
	/**
	 *  Return the JCPAction instance associated with this JCPPanel
	 *
	 *@return    The jCPAction value
	 */
	public JCPAction getJCPAction() {
		if (jcpaction == null) {
			jcpaction = new JCPAction();
		}
		return jcpaction;
	}

	/**
	 *  Gets the menuResourceString attribute of the JChemPaint object
	 *
	 * @param  key  Description of the Parameter
	 * @return      The menuResourceString value
	 */
	public String getMenuResourceString(String key, String guiString) {
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
	 * @return           The created JMenu
	 */
	protected JMenu createMenu(JChemPaintPanel jcpPanel, String key, boolean isPopup, String guiString) {
		logger.debug("Creating menu: ", key);
		String[] itemKeys = StringHelper.tokenize(getMenuResourceString(key, guiString));
		JMenu menu = new JMenu(JCPMenuTextMaker.getInstance().getText(key));
		for (int i = 0; i < itemKeys.length; i++) {
			if (itemKeys[i].equals("-")) {
				menu.addSeparator();
			}
			else if (itemKeys[i].startsWith("@")) {
				JMenu me = createMenu(jcpPanel, itemKeys[i].substring(1), isPopup, guiString);
				menu.add(me);
			}
			else if (itemKeys[i].endsWith("+")) {
				JMenuItem mi;
				if(itemKeys[i].endsWith("++"))
					mi = createMenuItem(jcpPanel,
						itemKeys[i].substring(0, itemKeys[i].length() - 2),
						true, true, isPopup
						);
				else
					mi = createMenuItem(jcpPanel,
						itemKeys[i].substring(0, itemKeys[i].length() - 1),
						true, false, isPopup
						);
				menu.add(mi);
			}
			else {
				JMenuItem mi = createMenuItem(jcpPanel, itemKeys[i], false, false, isPopup);
				menu.add(mi);
			}
		}
		return menu;
	}

	
	/**
	 *  Craetes a JMenuItem given by a String and adds the right ActionListener to
	 *  it.
	 *
	 * @param  cmd         String The Strin to identify the MenuItem
	 * @param  jcpPanel    Description of the Parameter
	 * @param  isCheckBox  Description of the Parameter
	 * @param  isChecked   Description of the Parameter
	 * @return             JMenuItem The created JMenuItem
	 */
	protected JMenuItem createMenuItem(JChemPaintPanel jcpPanel, String cmd, boolean isCheckBox, boolean isChecked, boolean isPopupMenu) {
		logger.debug("Creating menu item: ", cmd);
		String translation = "***" + cmd + "***";
		try {
			translation = JCPMenuTextMaker.getInstance().getText(cmd);
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
		return mi;
	}
	
	/**
	 *  Adds ShortCuts to the JChemPaintMenuBar object
	 *
	 * @param  cmd  String The Strin to identify the MenuItem
	 * @param  mi   the regarding MenuItem
	 * @param  jcp  The feature to be added to the ShortCuts attribute
	 */
	private void addShortCuts(String cmd, JMenuItem mi, JChemPaintPanel jcp) {
		Properties shortCutProps = JCPPropertyHandler.getInstance().getJCPShort_Cuts();
		String shortCuts = shortCutProps.getProperty(cmd);
		String charString = null;
		if (shortCuts != null) {
			try {
				String[] scStrings = shortCuts.trim().split(",");
				if (scStrings.length > 1) {
					charString = scStrings[1];
					String altKey = scStrings[0] + "_MASK";
					Field field = Class.forName("java.awt.event.InputEvent").getField(altKey);
					int i = field.getInt(Class.forName("java.awt.event.InputEvent"));
					mi.setAccelerator(KeyStroke.getKeyStroke(charString.charAt(0), i));
					jcp.registerKeyboardAction(mi.getActionListeners()[0], charString, KeyStroke.getKeyStroke(charString.charAt(0), i), JComponent.WHEN_IN_FOCUSED_WINDOW);
				}
				else {
					charString = "VK_" + scStrings[0];
					Field field = Class.forName("java.awt.event.KeyEvent").getField(charString);
					int i = field.getInt(Class.forName("java.awt.event.KeyEvent"));
					mi.setAccelerator(KeyStroke.getKeyStroke(i, 0));
					jcp.registerKeyboardAction(mi.getActionListeners()[0], charString, KeyStroke.getKeyStroke(i, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
				}
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			} catch (NoSuchFieldException nsfe) {
				nsfe.printStackTrace();
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			}
		}
	}
}
