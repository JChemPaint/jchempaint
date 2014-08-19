package org.openscience.jchempaint.undoredo;

import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.controller.undoredo.MakeReactantOrProductInExistingReactionEdit;

public class SwingMakeReactantInExistingReactionEdit extends
		MakeReactantOrProductInExistingReactionEdit  implements UndoableEdit{

	public SwingMakeReactantInExistingReactionEdit(IChemModel chemModel,
			IAtomContainer ac, IAtomContainer oldcontainer, String s, boolean reactantOrProduct, 
			String type) {
		super(chemModel, ac, oldcontainer, s, reactantOrProduct, type);
	}

	public boolean addEdit(UndoableEdit arg0) {
		return false;
	}

	public void die() {
	}

	public String getRedoPresentationName() {
		return getPresentationName();
	}

	public String getUndoPresentationName() {
		return getPresentationName();
	}

	public boolean isSignificant() {
		return true;
	}

	public boolean replaceEdit(UndoableEdit arg0) {
		return false;
	}
}
