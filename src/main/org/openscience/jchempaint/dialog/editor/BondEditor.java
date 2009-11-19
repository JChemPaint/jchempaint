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

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 */
public class BondEditor extends ChemObjectEditor {
    
    private static final long serialVersionUID = -5262566515479485581L;
    
    JComboBox orderField;
    IChemModelRelay hub;
    
    public BondEditor(IChemModelRelay hub) {
        super(false);
        constructPanel();
        this.hub = hub;
    }
    
    private void constructPanel() {
    	String[] orderString = { Order.SINGLE.toString(), Order.DOUBLE.toString(), Order.TRIPLE.toString(), Order.QUADRUPLE.toString() };
        orderField = new JComboBox(orderString);
        addField("Order", orderField, this);
    }
    
    public void setChemObject(IChemObject object) {
        if (object instanceof org.openscience.cdk.interfaces.IBond) {
            source = object;
            // update table contents
            IBond bond = (IBond)source;
            orderField.setSelectedItem(bond.getOrder().toString());
        } else {
            throw new IllegalArgumentException("Argument must be an Bond");
        }
    }
	
    public void applyChanges() {
        IBond bond = (IBond)source;
        int newOrder = orderField.getSelectedIndex();
        Order order=null;
        if(newOrder==0)
        	order=Order.SINGLE;
        else if(newOrder==1)
        	order=Order.DOUBLE;
        else if(newOrder==2)
        	order=Order.TRIPLE;
        else if(newOrder==3)
        	order=Order.QUADRUPLE;
        hub.setOrder(bond, order);
    }
}


