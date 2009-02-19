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
package org.openscience.jchempaint.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JCPPropertyHandler;

/**
 * JFrame that allows setting of a number of general application options.
 * 
 */
public class GeneralSettingsEditor extends FieldTablePanel implements ActionListener {
    
	private static final long serialVersionUID = -6796422949531937872L;

	private JCheckBox askForIOSettings;
	private JTextField undoStackSize;
    
    private JFrame frame;
    
    public GeneralSettingsEditor(JFrame frame) {
        super();
        this.frame = frame;
        constructPanel();
    }
    
    private void constructPanel() {
        askForIOSettings = new JCheckBox();
        addField(GT._("Ask for IO settings"), askForIOSettings);
        undoStackSize = new JTextField();
        addField(GT._("Undo/redo stack size"), undoStackSize);
    }
    
    public void setSettings() {
        Properties props = JCPPropertyHandler.getInstance().getJCPProperties();
        askForIOSettings.setSelected(props.getProperty("askForIOSettings", "true").equals("true"));
        undoStackSize.setText(props.getProperty("General.UndoStackSize"));
        validate();
    }

    public boolean applyChanges() {
        Properties props = JCPPropertyHandler.getInstance().getJCPProperties();
        props.setProperty("askForIOSettings",
            askForIOSettings.isSelected() ? "true" : "false"
        );
        try{
        	int size=Integer.parseInt(undoStackSize.getText());
        	if(size<1 || size>100)
        		throw new Exception("wrong number");
            props.setProperty("General.UndoStackSize",
                    undoStackSize.getText()
            );        	
        }
        catch(Exception ex){
        	JOptionPane.showMessageDialog(this, GT._("Undo/redo stack size")+" "+GT._("must be a number from 1 to 100"), GT._("Undo/redo stack size"), JOptionPane.WARNING_MESSAGE);
        	return false;
        }
        return true;
    }
    
    /**
     * Required by the ActionListener interface.
     */
    public void actionPerformed(ActionEvent e) {
        // nothing to do whatsoever
    }

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
}


