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

import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;


/**
 * Performce a Redo - a repeat of the last undone action
 *
 */
public class RedoAction extends JCPAction
{

    private static final long serialVersionUID = 9219564857373463615L;

    /**
	 *  redoes the last action.
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Redo triggered");
		if (jcpPanel.getRenderPanel().getUndoManager().canRedo()) {
            jcpPanel.getRenderPanel().getUndoManager().redo();
        }
        jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel()
        	.setSelection(new LogicalSelection(LogicalSelection.Type.NONE));
        
        // Set panel to unmodified if chemModel is empty
		jcpPanel.setModified(true);
        IChemModel cm = jcpPanel.get2DHub().getChemModel();
        if ((cm.getMoleculeSet() == null || cm.getMoleculeSet().getMoleculeCount() < 2)
        		&& (cm.getReactionSet() == null || cm.getReactionSet().getReactionCount() == 0)) {
        	if (cm.getMoleculeSet().getMoleculeCount() == 1) {
        		IMolecule mol = cm.getMoleculeSet().getMolecule(0);
        		if (mol.getAtomCount() == 0)
        			jcpPanel.setModified(false);
        	} else {
    			jcpPanel.setModified(false);
        	}
        }

		jcpPanel.updateUndoRedoControls();
		jcpPanel.updateStatusBar();
        RendererModel renderModel = jcpPanel.get2DHub().getRenderer().getRenderer2DModel();
        renderModel.setRecalculationRequiredForSSSR(true);

		jcpPanel.get2DHub().updateView();
	}

}

