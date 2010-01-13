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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Swing class that allows easy building of edit forms.
 *
 * @cdk.svnrev  $Revision: 11740 $
 */
public class FieldTablePanel extends JPanel {
        
	private static final long serialVersionUID = -697566299504877020L;
	
	protected int rows;

    protected JTabbedPane tabbedPane;
    
    /**
     * Constructor for field table panel.
     * 
     * @param hasTabs True=tabs are added, false=fields go directly on here.
     */
    public FieldTablePanel(boolean hasTabs) {
        if(hasTabs){
            setLayout(new BorderLayout());
            tabbedPane = new JTabbedPane();
            add( tabbedPane, BorderLayout.CENTER );
        }else{
            setLayout(new GridBagLayout());
        }
        rows = 0;
    }
    
    /**
     * Adds a tab.
     * 
     * @param header The header for the tab.
     * @return A JPanel, which you will need later to add fields.
     */
    public JPanel addTab(String header){
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        tabbedPane.addTab(header, panel );
        return panel;
    }
    
    /**
     * Adds a new JComponent to the 2 column table layout. Both
     * elements will be layed out in the same row. For larger
     * <code>JComponent</code>s the addArea() can be used.
     * 
     * @param labelText The text in left column.
     * @param component The control to add.
     * @param panel     The panel to add to. This must be either a panel you got from addTab or null if in no tab mode.
     */
    public void addField(String labelText, JComponent component, JPanel panel) {
        if(panel==null)
            panel=this;
        rows++;
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel label = new JLabel("", JLabel.TRAILING);
        if (labelText != null && labelText.length() > 0) {
            label = new JLabel(labelText + ": ", JLabel.TRAILING);
        }
        label.setLabelFor(component);
        constraints.gridx = 0;
        constraints.gridy = rows;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.weightx = 1.0;
        panel.add(label, constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, constraints);
    }
    
    /**
     * Adds a new JComponent to the 2 column table layout. The JLabel
     * will be placed in one row, while the <code>JComponent</code>
     * will be placed in a second row.
     *
     * @see #addField(String, JComponent)
     */
    public void addArea(String labelText, JComponent component) {
        rows++;
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel label = new JLabel(labelText + ": ");
        label.setLabelFor(component);
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = rows;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.weightx = 1.0;
        add(label, constraints);
        rows++;
        constraints.gridy = rows;
        JScrollPane editorScrollPane = new JScrollPane(component);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(250, 145));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));
        add(editorScrollPane, constraints);
    }
    
}



