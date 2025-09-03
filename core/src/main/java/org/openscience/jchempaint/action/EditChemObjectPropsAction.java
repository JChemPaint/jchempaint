/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egon Willighagen, Stefan Kuhn
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.jchempaint.dialog.editor.AtomEditor;
import org.openscience.jchempaint.dialog.editor.BondEditor;
import org.openscience.jchempaint.dialog.editor.ChemObjectEditor;
import org.openscience.jchempaint.dialog.editor.ChemObjectPropertyDialog;
import org.openscience.jchempaint.dialog.editor.PseudoAtomEditor;
import org.openscience.jchempaint.dialog.editor.ReactionEditor;

/**
 * Action for triggering an edit of a IChemObject
 *
 */
public class EditChemObjectPropsAction extends JCPAction {

	private static final long serialVersionUID = 7123137508085454087L;

	public void actionPerformed(ActionEvent event) {
		IChemObject object = getSource(event);
		logger.debug("Showing object properties for: ", object);
		ChemObjectEditor editor = null;
		if (object instanceof IPseudoAtom) {
			editor = new PseudoAtomEditor();
		}
		else if (object instanceof IAtom) {
			editor = new AtomEditor(jcpPanel.get2DHub());
		}
		else if (object instanceof IReaction) {
			editor = new ReactionEditor();
		}
		else if (object instanceof IBond) {
			editor = new BondEditor(jcpPanel.get2DHub(), jcpPanel.getBlockedActions());
		}
		
		if (editor != null) {
			editor.setChemObject(object);
			ChemObjectPropertyDialog frame =
					new ChemObjectPropertyDialog(JOptionPane.getFrameForComponent(editor), jcpPanel.get2DHub(),editor);
			frame.pack();
			frame.setVisible(true);
		}
		jcpPanel.get2DHub().updateView();
	}
}

