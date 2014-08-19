package org.openscience.jchempaint.undoredo;

import java.util.Map;

import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.RGroup;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.controller.undoredo.LoadNewModelEdit;
import org.openscience.jchempaint.controller.undoredo.RGroupEdit;
import org.openscience.jchempaint.rgroups.RGroupHandler;

public class SwingRGroupEdit extends RGroupEdit implements UndoableEdit{

	public SwingRGroupEdit(String event, boolean isNewRgrp, IChemModelRelay hub, RGroupHandler rgrpHandler
	        , Map<IAtom,IAtomContainer> _existingAtomDistr, Map<IBond,IAtomContainer> _existingBondDistr
	        , IAtomContainer _existingRoot,Map<IAtom, Map<Integer, IBond>> _existingRootAttachmentPoints
	        , Map<RGroup, Map<Integer,IAtom>> _existingRGroupApo
	        , Map<Integer,RGroupList> _rgroupLists, IAtomContainer _userSelection
	)
	{
		super(event, isNewRgrp, hub, rgrpHandler
		        , _existingAtomDistr, _existingBondDistr
		        , _existingRoot, _existingRootAttachmentPoints, _existingRGroupApo 
		        , _rgroupLists, _userSelection);
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
