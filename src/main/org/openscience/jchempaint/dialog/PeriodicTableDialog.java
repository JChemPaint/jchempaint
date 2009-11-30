/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egone Willighagen, Miguel Rojas, Geert Josten
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
import java.io.IOException;
import java.util.EventObject;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.exception.CDKException;
import org.openscience.jchempaint.GT;

/**
 * Dialog that shows a periodic table. The selected symbol
 * can be derived with getChoosenSymbol after the dialog has been shown.
 * getChoosenSymbol will be "" if dialog has been cancelled.
 */
public class PeriodicTableDialog extends JDialog {

	private static final long serialVersionUID = -1136319713943259980L;
	
	private PeriodicTablePanel ptp;
    private String symbolfromtable="";
    
    public String getChoosenSymbol() {
		return symbolfromtable;
	}

	public PeriodicTableDialog() {
     	super((JFrame)null,true);
        doInit();
    }
    
    public void doInit(){
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.white);
        setTitle(GT._("Choose an element..."));
        
        ptp = new PeriodicTablePanel();
        ptp.addCDKChangeListener(new PTDialogChangeListener());
        getContentPane().add("Center",ptp);
        pack();
		setVisible(true);
  }
    
  class PTDialogChangeListener implements ICDKChangeListener {

        /**
         * Constructor for the PTDialogChangeListener object
         * 
         */
        public PTDialogChangeListener() {
        }

        public void stateChanged(EventObject event) {
            if (event.getSource() instanceof PeriodicTablePanel) {
                PeriodicTablePanel source = (PeriodicTablePanel)event.getSource();
                String symbol=null;
                try {
                     symbol = source.getSelectedElement();
                } catch (CDKException e) {
                    throw new RuntimeException(e);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                   
                }
                symbolfromtable=symbol;
                setVisible(false);
            } else {
            }
        }

		public void zoomFactorChanged(EventObject event) {
		}
    }
}
