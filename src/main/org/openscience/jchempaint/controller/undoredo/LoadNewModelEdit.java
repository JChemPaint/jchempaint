/* $RCSfile$
 * $Author: gilleain $
 * $Date: 2008-11-26 16:01:05 +0000 (Wed, 26 Nov 2008) $
 * $Revision: 13311 $
 *
 * Copyright (C) 2005-2009 Stefan Kuhn
 *           (C) 2012      Ralf Stephan
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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * @cdk.module controlextra
 * @cdk.svnrev  $Revision: 10979 $
 */
public class LoadNewModelEdit implements IUndoRedoable {

    private static final long serialVersionUID = -9022673628051651034L;
    
    private IChemModel chemModel;
	private IMoleculeSet oldsom;
	private IReactionSet oldsor;
	private IMoleculeSet newsom;
	private IReactionSet newsor;
	private String type;
	private IChemModelRelay chemModelRelay = null;

	/**
	 * Called by
	 *     OpenAction.actionPerformed
	 * @param chemModel
	 * @param relay
	 * @param oldsom
	 * @param oldsor
	 * @param newsom
	 * @param newsor
	 * @param type
	 */
	public LoadNewModelEdit(IChemModel chemModel, IChemModelRelay relay, IMoleculeSet oldsom, IReactionSet oldsor, IMoleculeSet newsom, IReactionSet newsor, String type) {
		this.chemModel = chemModel;
		this.newsom=newsom;
		this.newsor=newsor;
		this.oldsom=oldsom;
		this.oldsor=oldsor;
		this.type=type;
		this.chemModelRelay = relay;
	}

	public void redo() {
		if (chemModelRelay != null) {
			for (IAtomContainer ac : newsom.atomContainers()) {
				chemModelRelay.updateAtoms(ac, ac.atoms());
			}
		}
		chemModel.setMoleculeSet(newsom);
		chemModel.setReactionSet(newsor);
	}

	public void undo() {
		if (chemModelRelay != null) {
			for (IAtomContainer ac : oldsom.atomContainers()) {
				chemModelRelay.updateAtoms(ac, ac.atoms());
			}
		}
		chemModel.setMoleculeSet(oldsom);
		chemModel.setReactionSet(oldsor);
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
