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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.dialog.editor.PropertiesModelEditor;
import org.openscience.jchempaint.renderer.RendererModel;

/**
  * Simple Dialog that shows the loaded dictionaries..
  *
  */
public class ModifyRenderOptionsDialog extends JDialog {

	private static final long serialVersionUID = -7228371698429720333L;
	
	private PropertiesModelEditor editor;
    private RendererModel model;
    private AbstractJChemPaintPanel jcpPanel;
    private int tabtoshow;
    
	/**
	 * Displays the Info Dialog for JChemPaint. 
	 * @param tabtoshow Which tab is to be displayed?
	 */
    public ModifyRenderOptionsDialog(AbstractJChemPaintPanel jcpPanel, RendererModel model, int tabtoshow) {
        super();
        this.model = model;
        this.jcpPanel=jcpPanel;
        this.tabtoshow = tabtoshow;
        editor = new PropertiesModelEditor(this, jcpPanel, tabtoshow,jcpPanel.getGuistring());
        createDialog();
        pack();
        setVisible(true);
    }
    
    private void createDialog(){
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.lightGray);
        setTitle(GT._("Preferences"));
        editor.setModel(model);
        getContentPane().add("Center",editor);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout ( new FlowLayout(FlowLayout.RIGHT) );
        JButton ok = new JButton(GT._("OK"));
        ok.setName("ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OKPressed();
            }}
        );
        buttonPanel.add( ok );
        getRootPane().setDefaultButton(ok);
        JButton apply = new JButton(GT._("Apply"));
        apply.setName("apply");
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ApplyPressed(false);
            }}
        );
        buttonPanel.add( apply );
        JButton cancel = new JButton(GT._("Cancel"));
        cancel.setName("cancel");
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
    
    private  void ApplyPressed(boolean close) {
        // apply new settings
        editor.applyChanges(close);
        jcpPanel.get2DHub().updateView();
    }
    private  void OKPressed() {
        ApplyPressed(true);
        closeFrame();
    }

    public void closeFrame() {
        dispose();
    }
}
