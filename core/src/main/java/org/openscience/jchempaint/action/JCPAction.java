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
package org.openscience.jchempaint.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.JChemPaintMenuBar;
import org.openscience.jchempaint.JChemPaintPopupMenu;

/**
 * Superclass of all JChemPaint GUI actions
 *
 */
public class JCPAction extends AbstractAction
{

	private static final long serialVersionUID = -4056416630614934238L;
	
	public final static String actionSuffix = "Action";
    public final static String imageSuffix = "Image";
	public final static String disabled_imageSuffix = "DisabledImage";

    public static String iconSet = "IconSet";

    /**
	 *  Description of the Field
	 */
	public final static String TIPSUFFIX = "Tooltip";

    /**
	 *  Description of the Field
	 */
	protected static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(JCPAction.class);

	private Hashtable<String,JCPAction> actions = null;
	private Hashtable<String,JCPAction> popupActions = null;

	/**
	 *  Description of the Field
	 */
	protected String type;

	/**
	 *  Description of the Field
	 */
	protected AbstractJChemPaintPanel jcpPanel = null;

	/**
	 *  Is this popup action assiociated with a PopupMenu or not.
	 */
	private boolean isPopupAction;


	/**
	 *  Constructor for the JCPAction object
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  type           Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 */
	public JCPAction(AbstractJChemPaintPanel jcpPanel, String type, boolean isPopupAction)
	{
		super();
		if (this.actions == null)
		{
			this.actions = new Hashtable<String,JCPAction>();
		}
		if (this.popupActions == null)
		{
			this.popupActions = new Hashtable<String,JCPAction>();
		}
		this.type = "";
		this.isPopupAction = isPopupAction;
		this.jcpPanel = jcpPanel;
	}


	/**
	 *  Constructor for the JCPAction object
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 */
	public JCPAction(AbstractJChemPaintPanel jcpPanel, boolean isPopupAction)
	{
		this(jcpPanel, "", isPopupAction);
	}


	/**
	 *  Constructor for the JCPAction object
	 *
	 *@param  jcpPanel  Description of the Parameter
	 */
	public JCPAction(AbstractJChemPaintPanel jcpPanel)
	{
		this(jcpPanel, false);
	}


	/**
	 *  Constructor for the JCPAction object
	 */
	public JCPAction()
	{
		this(null);
	}


	/**
	 *  Sets the type attribute of the JCPAction object
	 *
	 *@param  type  The new type value
	 */
	public void setType(String type)
	{
		this.type = type;
	}


	/**
	 *  Sets the jChemPaintPanel attribute of the JCPAction object
	 *
	 *@param  jcpPanel  The new jChemPaintPanel value
	 */
	public void setJChemPaintPanel(AbstractJChemPaintPanel jcpPanel)
	{
		this.jcpPanel = jcpPanel;
	}


	/**
	 *  Is this action runnable?
	 *
	 *@return    The enabled value
	 */
	public boolean isEnabled()
	{
		return true;
	}


	/**
	 *  Gets the popupAction attribute of the JCPAction object
	 *
	 *@return    The popupAction value
	 */
	public boolean isPopupAction()
	{
		return isPopupAction;
	}


	/**
	 *  Sets the isPopupAction attribute of the JCPAction object
	 *
	 *@param  isPopupAction  The new isPopupAction value
	 */
	public void setIsPopupAction(boolean isPopupAction)
	{
		this.isPopupAction = isPopupAction;
	}


	/**
	 *  Dummy method.
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{
	}


	/**
	 *  Gets the source attribute of the JCPAction object
	 *
	 *@param  event  Description of the Parameter
	 *@return        The source value
	 */
	public IChemObject getSource(ActionEvent event)
	{
		Object source = event.getSource();
		logger.debug("event source: ", source);
		if (source instanceof JMenuItem)
		{
			Container parent = ((JMenuItem) source).getComponent().getParent();
			// logger.debug("event source parent: " + parent);
			if (parent instanceof JChemPaintPopupMenu)
			{
				return ((JChemPaintPopupMenu) parent).getSource();
			} else if (parent instanceof JPopupMenu)
			{
				// assume that the top menu is indeed a CDKPopupMenu
				logger.debug("Submenu... need to recurse into CDKPopupMenu...");
				while (!(parent instanceof JChemPaintPopupMenu))
				{
					logger.debug("  Parent instanceof ", parent.getClass().getName());
					if (parent instanceof JPopupMenu)
					{
						parent = ((JPopupMenu) parent).getInvoker().getParent();
					} else if (parent instanceof JChemPaintMenuBar)
					{
						logger.info(" Source is MenuBar. MenuBar items don't know about the source");
						return null;
					} else
					{
						logger.error(" Cannot get parent!");
						return null;
					}
				}
				return ((JChemPaintPopupMenu) parent).getSource();
			}
		}
		return null;
	}


	/**
	 *  Gets the action attribute of the JCPAction class
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  actionname     Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 *@return                The action value
	 */
	public JCPAction getAction(AbstractJChemPaintPanel jcpPanel, String actionname, boolean isPopupAction)
	{
		// make sure logger and actions are instantiated
		JCPAction dummy = new JCPAction(jcpPanel);

		// extract type
		String type = "";
		String classname = "";
		int index = actionname.indexOf("@");
		if (index >= 0)
		{
			classname = actionname.substring(0, index);
			// FIXME: it should actually properly check wether there are more chars
			// than just the "@".
			type = actionname.substring(index + 1);
		} else
		{
			classname = actionname;
		}
		logger.debug("Action class: ", classname);
		logger.debug("Action type:  ", type);

		// now get actual JCPAction class
		if (!isPopupAction && actions.containsKey(actionname))
		{
			logger.debug("Taking JCPAction from action cache for:", actionname);
			return (JCPAction) actions.get(actionname);
		} else if (isPopupAction && popupActions.containsKey(actionname))
		{
			logger.debug("Taking JCPAction from popup cache for:", actionname);
			return (JCPAction) popupActions.get(actionname);
		} else
		{
			logger.debug("Loading JCPAction class for:", classname);
			Object o = null;
			try
			{
				// because 'this' is static, it cannot be used to get a classloader,
				// therefore use logger instead
				o = dummy.getClass().getClassLoader().loadClass(classname).newInstance();
			} catch (Exception exc)
			{
				logger.error("Could not find/instantiate class: ", classname);
				logger.debug(exc);
				return dummy;
			}
			if (o instanceof JCPAction)
			{
				JCPAction a = (JCPAction) o;
				a.setJChemPaintPanel(jcpPanel);
				if (type.length() > 0)
				{
					a.setType(type);
				}
				if (isPopupAction)
				{
					popupActions.put(actionname, a);
				} else
				{
					actions.put(actionname, a);
				}
				return a;
			} else
			{
				logger.error("Action is not a JCPAction!");
			}
		}
		return dummy;
	}


	/**
	 *  Gets the action attribute of the JCPAction class
	 *
	 *@param  jcpPanel    Description of the Parameter
	 *@param  actionname  Description of the Parameter
	 *@return             The action value
	 */
	public JCPAction getAction(AbstractJChemPaintPanel jcpPanel, String actionname)
	{
		return getAction(jcpPanel, actionname, false);
	}
}

