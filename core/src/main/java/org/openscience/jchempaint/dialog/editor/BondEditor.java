/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *                2012 Ralf Stephan <ralf@ark.in-berlin.de> 
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

import java.util.List;

import javax.swing.JComboBox;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IBond.Stereo;

import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 */
public class BondEditor extends ChemObjectEditor {

	private static final long serialVersionUID = -5262566515479485581L;
	JComboBox orderField;
	JComboBox stereoField;
	IChemModelRelay hub;
	List<String> blacklist;

	public BondEditor(IChemModelRelay hub, List<String> blacklist) {
		super(false);
		this.hub = hub;
		this.blacklist = blacklist;
		constructPanel();
	}

    // Since the order in the following arrays is arbitrary, we must add
    // index functions, too.
    private String[] orderString  = {GT.get("Single"), GT.get("Double"), GT.get("Triple"), GT.get("Quadruple")};
    private String[] stereoString = {GT.get("None"), GT.get("Up"), GT.get("Down"), GT.get("Up or Down"), GT.get("E or Z")};

    private static int bondOrderIndex(IBond.Order o) {
        switch (o) {
            case SINGLE:
                return 0;
            case DOUBLE:
                return 1;
            case TRIPLE:
                return 2;
            case QUADRUPLE:
                return 3;
        }
        return -1;
    }

    private static int bondStereoIndex(IBond.Stereo s) {
        switch (s) {
            case NONE:
                return 0;
            case UP:
                return 1;
            case DOWN:
                return 2;
            case UP_OR_DOWN:
                return 3;
            case E_OR_Z:
                return 4;
        }
        return -1;
    }

    private void constructPanel() {
        orderField = new JComboBox(orderString);
        addField(GT.get("Bond order"), orderField, this);

        if (!blacklist.contains("stereochemistry")) {
            stereoField = new JComboBox(stereoString);
            addField(GT.get("Bond stereo"), stereoField, this);
        }
    }

    public void setChemObject(IChemObject object) {
        if (object instanceof org.openscience.cdk.interfaces.IBond) {
            source = object;
            IBond bond = (IBond) source;
            orderField.setSelectedIndex(bondOrderIndex(bond.getOrder()));
            if (!blacklist.contains("stereochemistry"))
                stereoField.setSelectedIndex(bondStereoIndex(bond.getStereo()));
        }
        else {
            throw new IllegalArgumentException("Argument must be an Bond");
        }
    }

    public void applyChanges() {
        IBond bond = (IBond) source;

        int newOrder = orderField.getSelectedIndex();
        Order order = null;
        switch (newOrder) {
            case 0:
                order = Order.SINGLE;
                break;
            case 1:
                order = Order.DOUBLE;
                break;
            case 2:
                order = Order.TRIPLE;
                break;
            case 3:
                order = Order.QUADRUPLE;
                break;
        }

        Stereo stereo = null;
        if (order != Order.SINGLE || stereoField == null) {
            stereo = Stereo.NONE;
        }
        else {
            int newStereo = stereoField.getSelectedIndex();
            switch (newStereo) {
                case 0:
                    stereo = Stereo.NONE;
                    break;
                case 1:
                    stereo = Stereo.UP;
                    break;
                case 2:
                    stereo = Stereo.DOWN;
                    break;
                case 3:
                    stereo = Stereo.UP_OR_DOWN;
                    break;
                case 4:
                    stereo = Stereo.E_OR_Z;
                    break;
            }
        }

        hub.changeBond(bond, order, stereo);
    }
}
