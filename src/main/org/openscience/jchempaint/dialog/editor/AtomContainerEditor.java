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

import javax.swing.JTextField;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 */
public class AtomContainerEditor extends ChemObjectEditor {
    
    private static final long serialVersionUID = -5106331683641108940L;
    
    JTextField titleField;
    
	public AtomContainerEditor() {
        super(false);
        constructPanel();
	}
    
    private void constructPanel() {
        titleField = new JTextField(30);
        addField("Title", titleField, this);
    }
    
    public void setChemObject(IChemObject object) {
        if (object instanceof IAtomContainer) {
            source = object;
            // update table contents
            IAtomContainer container = (IAtomContainer)source;
            String title = "";
            if (container.getProperty(CDKConstants.TITLE) != null) title = container.getProperty(CDKConstants.TITLE).toString();
            titleField.setText(title);
        } else {
            throw new IllegalArgumentException("Argument must be an AtomContainer");
        }
    }
	
    public void applyChanges() {
        source.setProperty(CDKConstants.TITLE, titleField.getText());
    }
}


