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

package org.openscience.jchempaint.rgroups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.vecmath.Point2d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.isomorphism.matchers.RGroup;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.cdk.isomorphism.matchers.RGroupQuery;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * Provides common functionality for JChempants to handle R-groups, such
 * as lay out, clean up and verifying operations. 
 */
public class RGroupHandler  {

	public RGroupHandler (IRGroupQuery _rGroupQuery) {
		rGroupQuery = _rGroupQuery;
	}

	private IRGroupQuery rGroupQuery;

	public IRGroupQuery getrGroupQuery() {
		return rGroupQuery;
	}

	public void setrGroupQuery(IRGroupQuery rGroupQuery) {
		this.rGroupQuery = rGroupQuery;
	}

	
	/**
	 * Creates a {@link org.openscience.cdk.interfaces.IMoleculeSet} from a
	 * provided {@link org.openscience.cdk.isomorphism.matchers.IRGroupQuery).
	 * The root structure becomes the atom container as position zero,  the
	 * substitutes follow on position 1..n, ordered by R-group number.
	 *   
	 * @param chemModel
	 * @param RgroupQuery
	 * @throws CDKException
	 */
	public IMoleculeSet getMoleculeSet (IChemModel chemModel) throws CDKException {

		if (rGroupQuery==null || rGroupQuery.getRootStructure() == null || rGroupQuery.getRootStructure().getAtomCount()==0)
			throw new CDKException( "The R-group is empty");

		IMoleculeSet moleculeSet = new MoleculeSet();
		moleculeSet.addAtomContainer(rGroupQuery.getRootStructure());
		chemModel.setMoleculeSet(moleculeSet);
		for (int rgrpNum : sortRGroupNumbers()) {
			RGroupList rgrpList = rGroupQuery.getRGroupDefinitions().get(rgrpNum);
			for (RGroup rgrp : rgrpList.getRGroups()) {
				chemModel.getMoleculeSet().addAtomContainer(rgrp.getGroup()); 
			}
		}
		return moleculeSet;
	}

	/**
	 * Helper to get an ordered list of R-group numbers for a certain IRGroupQuery. 
	 * @param RgroupQuery 
	 * @return an ordered list of R-group numbers in rgroupQuery.
	 */
	private List<Integer> sortRGroupNumbers () {
		List<Integer> rNumbers = new ArrayList<Integer>();
		if (rGroupQuery!=null) {
			for (Iterator<Integer> itr = rGroupQuery.getRGroupDefinitions().keySet().iterator(); itr.hasNext(); ) {
				rNumbers.add(itr.next());
			}
			Collections.sort(rNumbers);
		}
		return rNumbers;
	}

	/**
	 * Changes atom coordinates so that the substitutes of the R-group are lined 
	 * up underneath the root structure, resulting in a clear presentation.
	 * Method intended to be used after reading an external R-group files (RG files),
	 * overriding the coordinates in the file. 
	 * @param RgroupQuery
	 * @throws CDKException
	 */
	public void layoutRgroup() throws CDKException {

		if (rGroupQuery==null || rGroupQuery.getRootStructure() == null || rGroupQuery.getRootStructure().getAtomCount()==0)
			throw new CDKException( "The R-group is empty");

		/*
		 * This is how we want to layout:
		 * 
		 *    {Root structure}
		 *    
		 *    {R1.a} {R1.b} {R1.c} ...
		 *    {R2.a} {R2.b} ...
		 *    {R3.a} {R3.b} {R3.c} ...
		 *    ....
		 *    ..
		 */
		final double MARGIN=2;
		IAtomContainer rootStruct=rGroupQuery.getRootStructure();
		double xLeft=(findBoundary(rootStruct,true,true,Double.POSITIVE_INFINITY));
		double yBottom=(findBoundary(rootStruct,false,true,Double.POSITIVE_INFINITY))-MARGIN;
		double minListYBottom=yBottom;

		for (int rgrpNum : sortRGroupNumbers()) {
			double listXRight=xLeft;

			RGroupList rgrpList = rGroupQuery.getRGroupDefinitions().get(rgrpNum);
			for (RGroup rgrp : rgrpList.getRGroups()) {

				double rgrpXleft=(findBoundary(rgrp.getGroup(),true,true,Double.POSITIVE_INFINITY));
				double rgrpYtop=(findBoundary(rgrp.getGroup(),false,false,Double.NEGATIVE_INFINITY));
				double shiftX= (listXRight - rgrpXleft);
				double shiftY= (yBottom - rgrpYtop);
				for (IAtom atom : rgrp.getGroup().atoms()) {
					atom.setPoint2d(new Point2d (atom.getPoint2d().x+shiftX, atom.getPoint2d().y+shiftY ));
				}
				minListYBottom=(findBoundary(rgrp.getGroup(),false,true,minListYBottom));
				double rgrpXRight=(findBoundary(rgrp.getGroup(),true,false,Double.NEGATIVE_INFINITY));
				listXRight=rgrpXRight+MARGIN;
			}
			yBottom=minListYBottom-MARGIN;
		}
	}

	/**
	 * Helper method to find boundaries of a given atom container.
	 * @param atc atom container
	 * @param isX true if interested in X boundary, false for Y
	 * @param smallest true if we want smallest, false if largest
	 * @param startVal starting point
	 * @return boundary coordinate (x or y)
	 */
	private double findBoundary(IAtomContainer atc, boolean isX, boolean smallest, double startVal) {
		double retVal=startVal;
		for (IAtom atom : atc.atoms()) {
			if (isX)
				if (smallest) {
					if(atom.getPoint2d().x<retVal)
						retVal = atom.getPoint2d().x;
				}
				else {
					if(atom.getPoint2d().x>retVal)
						retVal = atom.getPoint2d().x;
				}
			else
				if (smallest) {
					if(atom.getPoint2d().y<retVal)
						retVal = atom.getPoint2d().y;
				}
				else {
					if(atom.getPoint2d().y>retVal)
						retVal = atom.getPoint2d().y;
				}
		}
		return retVal;
	}
	
	/**
	 * Cleans up atom containers in the R-group that do not exists (anymore)
	 * in the molecule set in the hub, possibly due to deletion or merging. 
	 * @param moleculeSet
	 */
	public void cleanUpRGroup(IMoleculeSet moleculeSet){
		List<Integer> rgrpToRemove=new ArrayList<Integer>();
		if (rGroupQuery!=null){
			Map<Integer,RGroupList> def = rGroupQuery.getRGroupDefinitions();
			for(Iterator<Integer> itr= def.keySet().iterator();itr.hasNext();) {
				//Remove RGroups with empty atom containers from RGroupLists
				int rgrpNum=itr.next();
				List<RGroup> rgpList = def.get(rgrpNum).getRGroups();
				for (int i = 0; i < rgpList.size(); i++) {
					if(!exists(rgpList.get(i).getGroup(),moleculeSet) || rgpList.get(i).getGroup().getAtomCount()==0) {
						rgpList.remove(i);
					}
				}
				//Drop RGroupLists that don't have any content atom-wise
				int atomCount=0;
				for (RGroup rgrp :rGroupQuery.getRGroupDefinitions().get(rgrpNum).getRGroups()) {
					atomCount+=rgrp.getGroup().getAtomCount();
				}
				if (atomCount==0) {
					rgrpToRemove.add(rgrpNum);
				}
			}
			for (Integer rgrpNum : rgrpToRemove) {
				rGroupQuery.getRGroupDefinitions().remove(rgrpNum);
			}
		}
		
	}


	/**
	 * Helper method for {@link #cleanUpRGroup(IMoleculeSet)}, checks if
	 * an atom container referred to in the R-group still exists in the current
	 * molecule set in the hub. 
	 * @param atcRgrp
	 * @param chemModel
	 */
	private boolean exists(IAtomContainer atcRgrp,IMoleculeSet moleculeSet) {
		int i=0;
		for (IAtomContainer atc : moleculeSet.atomContainers()) {
			if(atc==atcRgrp)
				return true;
		}
		return false;
	}

	 
	/**
	 * The RGroupQuery references atom containers (the root and the substitutes).
	 * However, other JCP modules can re-create the atom containers, such as happens
	 * in {@link org.openscience.jchempaint.controller.undoredo.RemoveAtomsAndBondsEdit}.
	 * In such cases, this method needs to be called to reset the atom containers in the
	 * RGroup to the newly created ones.
	 * 
	 * @param newSet molecule set with freshly created containers (but existing atoms)
	 * @throws CDKException 
	 */
	public void adjustAtomContainers(IMoleculeSet newSet) throws CDKException {
		//System.out.println("^^^ adjustAtomContainers(IMoleculeSet newSet)");
		boolean hasRoot=false;
		if (rGroupQuery!=null) {
			for (IAtomContainer newAtc : newSet.atomContainers()) {
				atoms:
					for (IAtom movedAtom : newAtc.atoms()) {
						
						if (rGroupQuery.getRootStructure().contains(movedAtom)) {
							//System.out.println("set root "+newAtc.hashCode());
							rGroupQuery.setRootStructure(newAtc);
							newAtc.setProperty(CDKConstants.TITLE, RGroup.ROOT_LABEL);
							hasRoot=true;
							break atoms;
						}
						else {
							Map<Integer,RGroupList> def = rGroupQuery.getRGroupDefinitions();
							for(Iterator<Integer> itr= def.keySet().iterator();itr.hasNext();) {
								int rgrpNum=itr.next();
								List<RGroup> rgpList = def.get(rgrpNum).getRGroups();
								for (int i = 0; i < rgpList.size(); i++) {

									if(rgpList.get(i).getGroup().contains(movedAtom)||
											(rgpList.get(i).getFirstAttachmentPoint()!=null && rgpList.get(i).getFirstAttachmentPoint().equals(movedAtom)))
									{
										rgpList.get(i).setGroup(newAtc);
										/*
										//makes undo of deleting all atoms seq work better.. never mind garbage

										if (!newAtc.contains(rgpList.get(i).getFirstAttachmentPoint()) ) {
											rgpList.get(i).setFirstAttachmentPoint(null);
										}
										if (!newAtc.contains(rgpList.get(i).getSecondAttachmentPoint()) ) {
											rgpList.get(i).setSecondAttachmentPoint(null);
										}
										*/
										newAtc.setProperty(CDKConstants.TITLE, RGroup.makeLabel(rgrpNum));
										break atoms;
									}
								}
							}
						}
					}
			}
			if(!hasRoot) {
				System.err.println(">>BAD: lost track of the R-group");
				this.rGroupQuery=null;
				for (IAtomContainer atc : newSet.atomContainers() ) {
					atc.setProperty(CDKConstants.TITLE, null);
				}
				throw new CDKException ("R-group invalidated");
			}
		}
	}

	/**
	 * Verifies if a merge is allowed from the R-Group's point of view.
	 * Merging between the root structure and r-group substitutes is not allowed, 
	 * because it does not makes sense (plus the root structure could get lost). 
	 * @param hub controller hub that is about to do a merge.
	 */
	public boolean isMergeAllowed(IChemModelRelay hub) {
		//System.out.println("^^^ isMergeAllowed(IChemModelRelay hub)");

		if (rGroupQuery!=null) {
			for (Iterator<IAtom> it = hub.getRenderer().getRenderer2DModel().getMerge().keySet().iterator(); it.hasNext();) {
				IAtom mergedAtom = it.next();
				IAtom mergedPartnerAtom = hub.getRenderer().getRenderer2DModel().getMerge().get(mergedAtom);
				IAtomContainer container1 = ChemModelManipulator.getRelevantAtomContainer(hub.getChemModel(), mergedAtom);
				IAtomContainer container2 = ChemModelManipulator.getRelevantAtomContainer(hub.getChemModel(), mergedPartnerAtom);

				if(container1!=container2) {
					List<IAtomContainer> substitutes = rGroupQuery.getSubstituents();
					if ((container1==rGroupQuery.getRootStructure() && substitutes.contains(container2)) 
  					 || (container2==rGroupQuery.getRootStructure() && substitutes.contains(container1))) {
						JOptionPane.showMessageDialog(hub.getRenderer().getRenderPanel(), GT._("This operation is not allowed in the R-Group configuration."), GT._("R-Group alert"), JOptionPane.INFORMATION_MESSAGE);
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Hashes the R-group's atom container-related information.
	 * This can be used in the undo/redo of modules that change/drop/swap atom containers
	 * such as merging.
	 * @return hash mash of RGroup data
	 */
	public Map<Integer,Map<Integer,Integer>> makeHash() {
    	Map<Integer,Map<Integer,Integer>> rgrpHash = new HashMap<Integer,Map<Integer,Integer>>();
    	
    	if(rGroupQuery!=null) {
			Map<Integer,RGroupList> def = rGroupQuery.getRGroupDefinitions();
			for(Iterator<Integer> itr= def.keySet().iterator();itr.hasNext();) {
				int rgrpNum=itr.next();
				List<RGroup> rgpList = def.get(rgrpNum).getRGroups();
				for(RGroup rgp : rgpList) {
					if (rgp!=null) {
						Map<Integer,Integer> hash = new HashMap<Integer,Integer>();
						hash.put(0, rgp.getGroup()==null?null:rgp.getGroup().hashCode());
						hash.put(1, rgp.getFirstAttachmentPoint()==null?null:rgp.getFirstAttachmentPoint().hashCode());
						hash.put(2, rgp.getSecondAttachmentPoint()==null?null:rgp.getSecondAttachmentPoint().hashCode());
						rgrpHash.put(rgp.hashCode(),hash);
					}
				}
			}
			Map<Integer,Integer> root = new HashMap<Integer,Integer>();
			root.put(0,rGroupQuery.getRootStructure().hashCode()); 
			rgrpHash.put(-1,root );
    	}
    	return rgrpHash;
    }
	/**
	 * See restores what was saved by makeHash().
	 */
	public void restoreFromHash(Map<Integer,Map<Integer,Integer>> mash, IMoleculeSet mset) {
    	if(rGroupQuery!=null) {
    		int rootHash = mash.get(-1).get(0);
    		rGroupQuery.setRootStructure(findContainer(rootHash,mset));

    		Map<Integer,RGroupList> def = rGroupQuery.getRGroupDefinitions();
    		for (Iterator<Integer>rgpHashItr=mash.keySet().iterator(); rgpHashItr.hasNext();) {
    			int rgpHash = rgpHashItr.next();
    			restore:
        		for(Iterator<Integer> itr= def.keySet().iterator();itr.hasNext();) {
    				int rgrpNum=itr.next();
    				List<RGroup> rgpList = def.get(rgrpNum).getRGroups();
    				for(RGroup rgp : rgpList) {
    					if (rgp!=null && rgp.hashCode()==rgpHash) {
    						rgp.setGroup(findContainer(mash.get(rgpHash).get(0),mset));
    						if (rgp.getGroup()!=null) {
    							rgp.setFirstAttachmentPoint(findAtom(mash.get(rgpHash).get(1),rgp.getGroup()));
    							rgp.setSecondAttachmentPoint(findAtom(mash.get(rgpHash).get(2),rgp.getGroup()));
    						}
    						break restore;
    					}
    				}
    			}
    		}
    	}
    }

	/**
	 * Method to detect if removing atoms/bonds has unwanted results for
	 * the R-group. 
	 * 
	 * @param atc
	 * @param hub
	 * @return
	 */
	public boolean checkRGroupOkayForDelete(IAtomContainer atc,IChemModelRelay hub ) {
		//Check if the root would still remain there (partly) after a delete..
		if(rGroupQuery!=null) {
			boolean rootRemains=false;
			root:
			for(IAtom a : rGroupQuery.getRootStructure().atoms()) {
    			if (!atc.contains(a)){
    				rootRemains=true;
    				break root;
    			}
    		}
			if (!rootRemains) {
				int answer = JOptionPane.showConfirmDialog(hub.getRenderer().getRenderPanel(), GT._("This operation would irreversibly remove the R-Group query. Continue?"), GT._("R-Group alert"), JOptionPane.YES_NO_OPTION);
				if(answer == JOptionPane.NO_OPTION)
					return false;
			}
    	}
    	return true;
	}

	/**
	 * TODO 
	 * @param at
	 * @param hub
	 * @return
	 */
	public boolean checkRGroupOkayForDelete(IAtom at,IChemModelRelay hub ) {
		IAtomContainer tmp = new AtomContainer();
		tmp.addAtom(at);
		return (checkRGroupOkayForDelete(tmp,hub));
	}

	/**
	 * TODO
	 * @param atcHash
	 * @param mset
	 * @return
	 */
    private IAtomContainer findContainer (Integer atcHash, IMoleculeSet mset) {
    	if(atcHash!=null)
	    	for (IAtomContainer atc : mset.atomContainers()) {
	    		if(atc.hashCode()==atcHash)
	    			return atc;
	    	}
    	return null;
    }
    
    /**
     * TODO
     * @param atHash
     * @param atc
     * @return
     */
    private IAtom findAtom (Integer atHash, IAtomContainer atc) {
    	if (atHash!=null)
	    	for (IAtom at : atc.atoms()) {
	    		if(at.hashCode()==atHash)
	    			return at;
	    	}
    	return null;
    }

    /**
     * Method to check whether a given atom is part of one of the substitutes.
     * @param atom
     */
    public boolean isAtomPartOfSubstitute(IAtom atom) {
    	if (rGroupQuery!=null && rGroupQuery.getRGroupDefinitions()!=null) {
			for (Iterator<Integer> itr = rGroupQuery.getRGroupDefinitions().keySet().iterator(); itr.hasNext(); ) {
				RGroupList rgpList =rGroupQuery.getRGroupDefinitions().get(itr.next());
				if(rgpList!=null && rgpList.getRGroups()!=null) {
					for (RGroup rgrp: rgpList.getRGroups() ) {
						if(rgrp.getGroup().contains(atom)) {
							return true;
						}
					}
				}
			}
    	}
    	return false;
    }

    /**
     * Method to check whether a given bond exists in the root and is attached
     * to an R-Group. 
     * @param bond
     */
    public boolean isRGroupRootBond(IBond bond) {
    	if (rGroupQuery!=null && rGroupQuery.getRootStructure()!=null && rGroupQuery.getRootStructure().contains(bond)) {
			for(IAtom atom : bond.atoms()) {
				if (atom instanceof PseudoAtom && RGroupQuery.isValidRgroupQueryLabel(((PseudoAtom)atom).getLabel())) {
					return true;
				}
			}
    	}
    	return false;
    }
    
}
