/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn, Christoph Steinbeck
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
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.undo.UndoableEdit;
import javax.vecmath.Point2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.undoredo.AddHydrogenEdit;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;


/**
 * An action triggering everything related to hydrogens
 *
 */
public class AddHydrogenAction extends JCPAction
{

	private static final long serialVersionUID = 7696756423842199080L;

	public void actionPerformed(ActionEvent event)
	{
		logger.debug("Trying to add hydrogen in mode: ", type);
		if (jcpPanel.getChemModel() != null)
		{
            if (type.equals("allexplicit")) {
                jcpPanel.get2DHub().makeAllImplicitExplicit();
            }
            else if ( type.equals("trackimplicit")) {
				if(!jcpPanel.get2DHub().getController2DModel().getAutoUpdateImplicitHydrogens()){
					jcpPanel.get2DHub().getController2DModel().setAutoUpdateImplicitHydrogens(true);
				}else{
					jcpPanel.get2DHub().getController2DModel().setAutoUpdateImplicitHydrogens(false);
				}
            }
            else if (type.equals("allimplicit")) {
                jcpPanel.get2DHub().makeAllExplicitImplicit();
            }
            else if (type.equals("updateimplicit")) {
                jcpPanel.get2DHub().updateImplicitHydrogenCounts();
            }
            jcpPanel.get2DHub().updateView();
		}
	}
}

