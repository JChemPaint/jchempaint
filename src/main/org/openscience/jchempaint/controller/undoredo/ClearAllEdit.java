/* $RCSfile$
 * $Author: gilleain $
 * $Date: 2008-11-26 16:01:05 +0000 (Wed, 26 Nov 2008) $
 * $Revision: 13311 $
 *
 * Copyright (C) 2005-2008 Stefan Kuhn
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.controller.undoredo;

import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * @cdk.module controlbasic
 * @cdk.svnrev  $Revision: 10979 $
 */
public class ClearAllEdit implements IUndoRedoable {

    private static final long serialVersionUID = -9022673628051651034L;
    
    private IChemModel chemModel;
	private IMoleculeSet som;
	private IReactionSet sor;
	private String type;

	public ClearAllEdit(IChemModel chemModel, IMoleculeSet som, IReactionSet sor, String type) {
		this.chemModel = chemModel;
		this.som=som;
		this.sor=sor;
		this.type=type;
	}

	public void redo() {
    	if(chemModel.getMoleculeSet()!=null)
    		chemModel.getMoleculeSet().removeAllAtomContainers();
    	if(chemModel.getReactionSet()!=null)
    		chemModel.getReactionSet().removeAllReactions();	}

	public void undo() {
		if(som!=null)
			chemModel.setMoleculeSet(som);
		if(sor!=null)
			chemModel.setReactionSet(sor);
	}

	public boolean canRedo() {
		return true;
	}

	public boolean canUndo() {
		return true;
	}
	
	public String getPresentationName() {
		return type;
	}
}
