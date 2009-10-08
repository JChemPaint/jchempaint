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

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;

/**
 */
public class ReactionEditor extends ChemObjectEditor {
    
    private static final long serialVersionUID = 2386363253522364974L;
    
    private final static String SOLVENT = "org.openscience.cdk.Reaction.Solvent";
    private final static String TEMPERATURE = "org.openscience.cdk.Reaction.Temperature";
    
    private JTextField idField;
    private JComboBox directionField;
    private JTextField solventField;
    private JTextField tempField;
    
	public ReactionEditor() {
        super(false);
        constructPanel();
	}
    
    private void constructPanel() {
        idField = new JTextField(40);
        addField("Reaction ID", idField, this);
        // the options given next should match the order in the Reaction class!
        String[] options = {
            "", "Forward", "Backward", "Bidirectional"
        };
        directionField = new JComboBox(options);
        addField("Direction", directionField, this);
        solventField = new JTextField(40);
        addField("Solvent", solventField, this);
        tempField = new JTextField(10);
        addField("Temperature", tempField, this);
    }
    
    public void setChemObject(IChemObject object) {
        if (object instanceof IReaction) {
            source = object;     
            // update table contents
            IReaction reaction = (IReaction)source;
            idField.setText(reaction.getID());
            //TODO
            //directionField.setSelectedIndex(reaction.getDirection());
            solventField.setText((String)reaction.getProperty(SOLVENT));
            tempField.setText((String)reaction.getProperty(TEMPERATURE));
        } else {
            throw new IllegalArgumentException("Argument must be an Reaction");
        }
    }
	
    public void applyChanges() {
        IReaction reaction = (IReaction)source;
        reaction.setID(idField.getText());
        //TODO 
        //reaction.setDirection(directionField.getSelectedIndex());
        reaction.setProperty(SOLVENT, solventField.getText());
        reaction.setProperty(TEMPERATURE, tempField.getText());
    }
}


