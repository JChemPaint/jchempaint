/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
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
 *
 */
package org.openscience.jchempaint.controller.undoredo;

import java.util.ArrayList;
import java.util.List;


/**
 * This class needs to be passed to all controller modules, who want to/need to post
 * undo/redoable events. Applications register an IUndoListener on this and pass the edits
 * to the actual undo/redo implementation (provided e. g. by swing or swt).
 * 
 * @cdk.module control
 */
public class UndoRedoHandler {

	List<IUndoListener> undoListeners=new ArrayList<IUndoListener>();

	/**
	 * Only constructor
	 * 
	 * @param c2dm The Controller2dModel of the current application
	 */
	public UndoRedoHandler() {
	}
	

	public void addIUndoListener(IUndoListener listener){
		undoListeners.add(listener);
	}
	
	public void postEdit(IUndoRedoable edit) {
		for(IUndoListener listener:undoListeners){
			listener.doUndo(edit);
		}
	}

}
