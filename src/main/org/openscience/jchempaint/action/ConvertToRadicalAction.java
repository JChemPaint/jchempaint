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

import javax.swing.JOptionPane;

import org.openscience.cdk.Atom;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 */
public class ConvertToRadicalAction extends JCPAction {

	private static final long serialVersionUID = 1898335761308427006L;
	
	public void actionPerformed(ActionEvent event) {
        logger.debug("Converting to radical: ", type);
        IChemObject object = getSource(event);
        org.openscience.cdk.interfaces.IChemModel model = jcpPanel.getChemModel();
        if (object != null) {
            if (object instanceof Atom) {
                Atom atom = (Atom)object;
                
                IAtomContainer relevantContainer = ChemModelManipulator.getRelevantAtomContainer(model, atom);
                double number=0;
				try {
					number = new LonePairElectronChecker().getFreeLonePairs(atom, relevantContainer);
				} catch (CDKException e) {
					e.getMessage();
					logger.error(e.getMessage());
					logger.debug(e);				}
                
                if(number > 0.0)
                {
                	SingleElectron singleElectron = new SingleElectron(atom);
                    relevantContainer.addSingleElectron(singleElectron);
                    logger.info("Added single electron to atom");
                    logger.debug("new AC: ", relevantContainer);
                    
                }
                else JOptionPane.showMessageDialog(jcpPanel,"A radical cannot be added to this atom." +
        		" Re-try with less hydrogens.");

            
            } else {
                logger.error("Object not an Atom! Cannot convert into a radical!");
            }
        } else {
            logger.warn("Cannot convert a null object!");
        }
        jcpPanel.get2DHub().updateView();
    }
}
