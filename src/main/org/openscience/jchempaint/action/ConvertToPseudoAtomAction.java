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

import java.awt.event.ActionEvent;

import org.openscience.cdk.Atom;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
;

/**
 * Converts to and from pseudo atoms
 */
public class ConvertToPseudoAtomAction extends JCPAction {

	private static final long serialVersionUID = -598284013998335002L;

	public void actionPerformed(ActionEvent event) {
        logger.debug("Converting to: ", type);
        IChemObject object = getSource(event);
        org.openscience.cdk.interfaces.IChemModel model = jcpPanel.getChemModel();
        if (object != null) {
            if (object instanceof Atom) {
            	if(type.equals("normal")){
                    PseudoAtom pseudo = (PseudoAtom)object;
                    IAtom normal = pseudo.getBuilder().newAtom(pseudo);
                    normal.setSymbol("C");
                    jcpPanel.get2DHub().replaceAtom(normal,pseudo);
            	}else{
                    Atom atom = (Atom)object;
                    PseudoAtom pseudo = new PseudoAtom(atom);
                    pseudo.setLabel(type);
                    jcpPanel.get2DHub().replaceAtom(pseudo,atom);
            	}
            } else {
                logger.error("Object not an Atom! Cannot convert into a PseudoAtom!");
            }
        } else {
            logger.warn("Cannot convert a null object!");
        }
        jcpPanel.get2DHub().updateView();
    }
}
