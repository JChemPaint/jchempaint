/*
 *  Copyright (C) 2010 Mark Rijnbeek
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

package org.openscience.jchempaint.controller.undoredo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.isomorphism.matchers.RGroup;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.rgroups.RGroupHandler;

/**
 * Undo-redo class for clicking together an R-Group query in JCP.
 * @author markr
 */
public class RGroupEdit implements IUndoRedoable {

	private String type;
	private boolean isNewRgrp;
	private IChemModelRelay hub;
	private RGroupHandler rgrpHandler;
	private Map<IAtom,IAtomContainer> existingAtomDistr;
	private Map<IBond,IAtomContainer> existingBondDistr;
	private IAtomContainer existingRoot;
    private Map<IAtom, Map<Integer, IBond>> existingRootAttachmentPoints;
    private Map<Integer,RGroupList> existingRgroupLists =null;
    private Map<RGroup, Map<Integer,IAtom>> existingRGroupApo;
    private IAtomContainer redoRootStructure;
    private Map<IAtom, Map<Integer, IBond>> redoRootAttachmentPoints;
    private Map<Integer,RGroupList> redoRgroupLists =null;
    private Map<RGroup, Map<Integer,IAtom>> redoRGroupApo=null;
    private IMolecule userSelection;
	
	public RGroupEdit(String _type
			        , boolean _isNewRgrp
			        , IChemModelRelay _hub
			        , RGroupHandler _rgrpHandler
			        , Map<IAtom,IAtomContainer> _existingAtomDistr
			        , Map<IBond,IAtomContainer> _existingBondDistr
			        , IAtomContainer _existingRoot
			        , Map<IAtom, Map<Integer, IBond>> _existingRootAttachmentPoints
			        , Map<RGroup, Map<Integer,IAtom>> _existingRGroupApo
			        , Map<Integer,RGroupList> _existingRgroupLists
			        , IAtomContainer _userSelection
	)
	{
		this.type=_type;
		this.isNewRgrp=_isNewRgrp;
		this.hub=_hub;
		this.rgrpHandler=_rgrpHandler;
		this.existingRoot=_existingRoot;
		this.existingRootAttachmentPoints=_existingRootAttachmentPoints;
		this.existingRGroupApo=_existingRGroupApo;
		this.existingAtomDistr=_existingAtomDistr;
		this.existingBondDistr=_existingBondDistr;
	    this.existingRgroupLists=_existingRgroupLists;
	    this.redoRootStructure=rgrpHandler.getrGroupQuery().getRootStructure();
		this.userSelection=(IMolecule)_userSelection;
		if (_existingRgroupLists!=null) {
			redoRgroupLists = new HashMap<Integer,RGroupList>();
			for (Iterator<Integer> itr=rgrpHandler.getrGroupQuery().getRGroupDefinitions().keySet().iterator(); itr.hasNext();) {
				int rNum=itr.next();
				redoRgroupLists.put(rNum, rgrpHandler.getrGroupQuery().getRGroupDefinitions().get(rNum));
			}
		}
		if(existingRGroupApo!=null) {
			RGroup undoRGroup=existingRGroupApo.keySet().iterator().next();			
    		for (Iterator<Integer> rnumItr= hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().keySet().iterator(); rnumItr.hasNext();) {
    			for (RGroup rgrp:  hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().get(rnumItr.next()).getRGroups()) {
    				if(rgrp.equals(undoRGroup)) {
    			        redoRGroupApo= new HashMap <RGroup,Map<Integer,IAtom>>();
    			        HashMap<Integer,IAtom> map = new HashMap<Integer,IAtom>();
    			        map.put(1, rgrp.getFirstAttachmentPoint());
    			        map.put(2, rgrp.getSecondAttachmentPoint());
						redoRGroupApo.put(rgrp,map); 
    				}
    			}
    		}
		}
	
	}

	/**
	 * Undo actions
	 */
	public void undo() { 

		IRGroupQuery rgrpQ= rgrpHandler.getrGroupQuery();

		if (type.equals("setSubstitute")||type.equals("setRoot")) {
			this.redoRootAttachmentPoints=rgrpHandler.getrGroupQuery().getRootAttachmentPoints();
			for (Iterator<IAtom> atItr = existingAtomDistr.keySet().iterator(); atItr.hasNext();) {
				IAtom atom = atItr.next();
				existingAtomDistr.get(atom).addAtom(atom);
			}
			for (Iterator<IBond> bndItr = existingBondDistr.keySet().iterator(); bndItr.hasNext();) {
				IBond bond = bndItr.next();
				existingBondDistr.get(bond).addBond(bond);
			}
	    	hub.getChemModel().getMoleculeSet().removeAtomContainer(userSelection);
			
			if (type.equals("setRoot")) {
				if (isNewRgrp) {
					rgrpQ.setRootStructure(null);
					rgrpQ.setRootAttachmentPoints(null);
					for (IAtomContainer atc: hub.getIChemModel().getMoleculeSet().atomContainers()) {
						atc.removeProperty(CDKConstants.TITLE);	
					}
					hub.unsetRGroupHandler();
				}
				else {
					existingRoot.setProperty(CDKConstants.TITLE,RGroup.ROOT_LABEL);	
					rgrpQ.setRootStructure(existingRoot);
					rgrpQ.setRootAttachmentPoints(existingRootAttachmentPoints);
				}
			}
	
			else if (type.equals("setSubstitute")) {
				if  (existingRgroupLists !=null) {
					for(Iterator<Integer> rNums=existingRgroupLists.keySet().iterator(); rNums.hasNext();){
						int rNum= rNums.next();
						rgrpQ.getRGroupDefinitions().put(rNum, existingRgroupLists.get(rNum));
					}
				}
			}
		}

		else if (type.startsWith("setAtomApoAction")) {
			RGroup undoRGroup=existingRGroupApo.keySet().iterator().next();			
    		for (Iterator<Integer> rnumItr= hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().keySet().iterator(); rnumItr.hasNext();) {
    			for (RGroup rgrp:  hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().get(rnumItr.next()).getRGroups()) {
    				if(rgrp.equals(undoRGroup)) {
    					IAtom apo1=existingRGroupApo.get(undoRGroup).get(1);
    					IAtom apo2=existingRGroupApo.get(undoRGroup).get(2);
    					rgrp.setFirstAttachmentPoint(apo1);
    					rgrp.setSecondAttachmentPoint(apo2);
    				}
    			}
    		}
		}
		else if (type.startsWith("setBondApoAction")) {
			for(Iterator<IAtom> atItr=existingRootAttachmentPoints.keySet().iterator(); atItr.hasNext();) {
				IAtom rAtom= atItr.next();
				Map<Integer,IBond> undoApo = existingRootAttachmentPoints.get(rAtom);
				Map<Integer,IBond> apoBonds = rgrpQ.getRootAttachmentPoints().get(rAtom);

				redoRootAttachmentPoints= new HashMap<IAtom, Map<Integer, IBond>>();
				Map<Integer,IBond> redoApo = new HashMap<Integer,IBond>(); 
				if(apoBonds.get(1)!=null)
					redoApo.put(1, apoBonds.get(1));
				if(apoBonds.get(2)!=null)
					redoApo.put(2, apoBonds.get(2));
				redoRootAttachmentPoints.put(rAtom, redoApo);
				
				apoBonds.remove(1); apoBonds.remove(2);
				if(undoApo.get(1)!=null) {
					apoBonds.put(1, undoApo.get(1));
				}
				if(undoApo.get(2)!=null) {
					apoBonds.put(2, undoApo.get(2));
				}
			}
		}
		else if (type.equals("clearRgroup")) {
			hub.setRGroupHandler(rgrpHandler);
			rgrpQ.getRootStructure().setProperty(CDKConstants.TITLE, RGroup.ROOT_LABEL);
    		for (Iterator<Integer> rnumItr= hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().keySet().iterator(); rnumItr.hasNext();) {
    			int rNum=rnumItr.next();
    			for (RGroup rgrp:  hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().get(rNum).getRGroups()) {
    				rgrp.getGroup().setProperty(CDKConstants.TITLE,RGroup.makeLabel(rNum));
    			}
    		}
		}
	}

	
	/**
	 * Redo actions
	 */
	public void redo() {

		if(type.equals("setRoot")||type.equals("setSubstitute")) {
		
			if (isNewRgrp) {
				hub.setRGroupHandler(rgrpHandler);
			}
	
			IRGroupQuery rgrpQ= rgrpHandler.getrGroupQuery();
			for (Iterator<IAtom> atItr = existingAtomDistr.keySet().iterator(); atItr.hasNext();) {
				IAtom atom = atItr.next();
				existingAtomDistr.get(atom).removeAtom(atom);
			}
			for (Iterator<IBond> bndItr = existingBondDistr.keySet().iterator(); bndItr.hasNext();) {
				IBond bond = bndItr.next();
				existingBondDistr.get(bond).removeBond(bond);
			}
			hub.getChemModel().getMoleculeSet().addAtomContainer(userSelection);
	
			if (type.equals("setRoot")) {
				rgrpQ.setRootStructure(redoRootStructure);
				rgrpQ.getRootStructure().setProperty(CDKConstants.TITLE,RGroup.ROOT_LABEL);	
				rgrpQ.setRootAttachmentPoints(redoRootAttachmentPoints);
			}
			else if (type.equals("setSubstitute")) {
				if  (redoRgroupLists !=null) {
					for(Iterator<Integer> rNums=redoRgroupLists.keySet().iterator(); rNums.hasNext();){
						int rNum= rNums.next();
						rgrpQ.getRGroupDefinitions().put(rNum, redoRgroupLists.get(rNum));
					}
				}
			}
		}
		else if (type.startsWith("setAtomApoAction")) {
			RGroup redoRGroup=redoRGroupApo.keySet().iterator().next();			
    		for (Iterator<Integer> rnumItr= hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().keySet().iterator(); rnumItr.hasNext();) {
    			for (RGroup rgrp:  hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().get(rnumItr.next()).getRGroups()) {
    				if(rgrp.equals(redoRGroup)) {
    					IAtom apo1=redoRGroupApo.get(redoRGroup).get(1);
    					IAtom apo2=redoRGroupApo.get(redoRGroup).get(2);
    					rgrp.setFirstAttachmentPoint(apo1);
    					rgrp.setSecondAttachmentPoint(apo2);
    				}
    			}
    		}
		}
		else if (type.startsWith("setBondApoAction")) {
			for(Iterator<IAtom> atItr=redoRootAttachmentPoints.keySet().iterator(); atItr.hasNext();) {
				IAtom rAtom= atItr.next();
				Map<Integer,IBond> apoBonds = hub.getRGroupHandler().getrGroupQuery().getRootAttachmentPoints().get(rAtom);

				apoBonds.remove(1); apoBonds.remove(2);
				Map<Integer,IBond> redoApo = redoRootAttachmentPoints.get(rAtom);

				if(redoApo.get(1)!=null) {
					apoBonds.put(1, redoApo.get(1));
				}
				if(redoApo.get(2)!=null) {
					apoBonds.put(2, redoApo.get(2));
				}
			}
		}
		else if (type.equals("clearRgroup")) {
			hub.unsetRGroupHandler();
		}

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
