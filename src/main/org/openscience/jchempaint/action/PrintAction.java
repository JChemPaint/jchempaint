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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Opens a print dialog
 *
 * @cdk.module    jchempaint
 */
public class PrintAction extends JCPAction implements Printable {

    private static final long serialVersionUID = 3944389510342678007L;

    /**
	 *  Opens a dialog frame and manages the printing of a file.
	 *
	 * @param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event) {

		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (PrinterException pe) {
				jcpPanel.announceError(pe);
			}
		}
	}

	/**
	 *  Prints the actual drawingPanel
	 *
	 * @param  g           Graphics object of drawinPanel
	 * @param  pageFormat  Description of the Parameter
	 * @param  pageIndex   Description of the Parameter
	 * @return             Description of the Return Value
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		}
		else {
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			jcpPanel.getRenderPanel().takeSnapshot(g2d);
			return (PAGE_EXISTS);
		}
	}
}

