/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn, Tobias Helmus
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
package org.openscience.jchempaint.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JCPMenuTextMaker;
import org.openscience.jchempaint.JCPPropertyHandler;
import org.openscience.jchempaint.action.JCPAction;


/**
 * Simple Dialog that shows the JCP logo and a textfield that allows
 * the user to copy&amp;paste the URL of JChemPaints main site.
 *
 * @cdk.created       27. April 2005
 */
public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 8890609574363086221L;
	
    private static ILoggingTool logger =
	        LoggingToolFactory.createLoggingTool(AboutDialog.class);

	/** Displays the About Dialog for JChemPaint.  */
	public AboutDialog(Frame owner, String guistring) {
		super(owner, JCPMenuTextMaker.getInstance(guistring).getText("about"));
		doInit();
	}

	public void doInit() {
		String version = JCPPropertyHandler.getInstance().getVersion();
		String s1 = "JChemPaint " + version + "\n";
		s1 += GT._("An open-source editor for 2D chemical structures.");
		String s2 = GT._("An OpenScience project.")+"\n";
		s2 += GT._("See 'http://jchempaint.sourceforge.net' for more information.");

		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(Color.white);

		JLabel label1 = new JLabel();

		try {
			JCPPropertyHandler jcpph = JCPPropertyHandler.getInstance();
			URL url = jcpph.getResource("jcplogo" + JCPAction.imageSuffix);
			ImageIcon icon = new ImageIcon(url);
			//ImageIcon icon = new ImageIcon(../resources/);
			label1 = new JLabel(icon);
		} catch (Exception exception) {
			logger.error("Cannot add JCP logo: " + exception.getMessage());
			logger.debug(exception);
		}
		label1.setBackground(Color.white);

		Border lb = BorderFactory.createLineBorder(Color.white, 5);
		JTextArea jtf1 = new JTextArea(s1);
		jtf1.setBorder(lb);
		jtf1.setEditable(false);
		JTextArea jtf2 = new JTextArea(s2);
		jtf2.setEditable(false);
		jtf2.setBorder(lb);
		getContentPane().add("Center", label1);
		getContentPane().add("North", jtf1);
		getContentPane().add("South", jtf2);
		pack();
		setVisible(true);
	}
}

