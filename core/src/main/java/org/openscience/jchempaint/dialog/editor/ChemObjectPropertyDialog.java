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
package org.openscience.jchempaint.dialog.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.controller.ControllerHub;

/**
  * Simple Dialog that shows the loaded dictionaries.
  * 
  */
public class ChemObjectPropertyDialog extends JDialog {

	private static final long serialVersionUID = 1850053536210317644L;
	
	private ChemObjectEditor editor;
	private ControllerHub hub;
    
	/**
	 * Displays the Info Dialog for JChemPaint. 
	 */
    public ChemObjectPropertyDialog(Frame frame, ControllerHub hub, ChemObjectEditor editor) {
        super(frame,"IChemObject Props Dialog");
        this.editor = editor;
        this.hub=hub;
        createDialog();
        pack();
        setVisible(true);
    }
    
    private void createDialog(){
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.lightGray);
        setTitle(GT._("Properties"));
        getContentPane().add("Center",editor);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout ( new FlowLayout(FlowLayout.RIGHT) );
        JButton ok = new JButton(GT._("OK"));
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OKPressed();
            }}
        );
        buttonPanel.add( ok );
        JButton cancel = new JButton(GT._("Cancel"));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeFrame();
            }}
        );
        buttonPanel.add( cancel );
        getRootPane().setDefaultButton(ok);
        getContentPane().add("South",buttonPanel);
        
        validate();
    }
    
    private  void OKPressed() {
    	if(editor.getMayclose()){
    		try {
    			editor.applyChanges();
    			hub.updateView();
    			closeFrame();     			
    		}
    		catch (Exception e) {
        		JOptionPane.showMessageDialog(this,e.getMessage(), GT._("A problem has occurred"), JOptionPane.WARNING_MESSAGE, null);
    		}
    	}else{
    		JOptionPane.showMessageDialog(this,GT._("You did not provide necessary information"), GT._("Incomplete Information"), JOptionPane.WARNING_MESSAGE, null);
    	}
    }

    public void closeFrame() {
        dispose();
    }
}
