/*
 *  Copyright (C) 2010 Mark Rijnbeek
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
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.isomorphism.matchers.RGroup;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.cdk.isomorphism.matchers.RGroupQuery;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.dialog.editor.ChemObjectEditor;
import org.openscience.jchempaint.dialog.editor.ChemObjectPropertyDialog;
import org.openscience.jchempaint.dialog.editor.RGroupEditor;
import org.openscience.jchempaint.io.JCPFileView;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.rgroups.RGroupHandler;

/**
 * Deals with user actions on creating/editing R-groups.
 * 
 */
public class RGroupAction extends JCPAction {

	private static final long serialVersionUID = 7387274752039316786L;

	/**
	 * Handles the user action, such as defining a root structure, substitutes,
	 * attachment atoms and bonds.
	 * 
	 * @see org.openscience.jchempaint.action.JCPAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event)
	{
		System.out.println("action iz "+type);
		IChemObject eventSource = getSource(event);

		IChemObjectSelection selection = jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection();
		if(selection ==null || !selection.isFilled()
				&&(type.equals("setRoot")||type.equals("setSubstitute")) ) {
			JOptionPane.showMessageDialog(jcpPanel, GT._("You have not selected any atoms or bonds."));
			return;
		}

		IChemModelRelay hub = jcpPanel.get2DHub();
		boolean isNewRgroup=false;
		RGroupHandler rGroupHandler=null;
		Map<IAtom,IAtomContainer> existingAtomDistr = new HashMap<IAtom,IAtomContainer>();
		Map<IBond,IAtomContainer> existingBondDistr= new HashMap<IBond,IAtomContainer>();
		IAtomContainer existingRoot=null;
		Map<IAtom, Map<Integer, IBond>> existingRootAttachmentPoints = null;
		Map<RGroup, Map<Integer,IAtom>> existingRGroupApo=null;
		Map<Integer,RGroupList> existingRgroupLists =null;

		IRGroupQuery rgrpQuery=null;
		IAtomContainer molecule=null;

		/* User action: generate possible configurations for the R-group */
		if(type.equals("rgpGenerate")) {
			if((jcpPanel.get2DHub().getRGroupHandler()==null)) {
				JOptionPane.showMessageDialog(jcpPanel, GT._("Please define an R-group (root and substituents) first."));
				return;
			}
            try {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(jcpPanel.getCurrentWorkDirectory());
				chooser.setFileView(new JCPFileView());
				chooser.showSaveDialog(jcpPanel);
				File outFile = chooser.getSelectedFile();
				System.out.println(outFile);
				List<IAtomContainer> molecules= jcpPanel.get2DHub().getRGroupHandler().getrGroupQuery().getAllConfigurations();
				if (molecules.size() > 0) {
					IAtomContainerSet molSet = molecules.get(0).getBuilder().newInstance(IAtomContainerSet.class);
					for (IAtomContainer mol : molecules) {
						molSet.addAtomContainer(mol);
					}
					SDFWriter sdfWriter = new SDFWriter(new FileWriter(outFile));
					sdfWriter.write(molSet);
					sdfWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(jcpPanel, GT._("There was an error generating the configurations {0}",e.getMessage()));
				return;
			}
		
		}
		/* User action: advanced R-group logic */
		else if(type.equals("rgpAdvanced")) {

			if((jcpPanel.get2DHub().getRGroupHandler()==null)) {
				JOptionPane.showMessageDialog(jcpPanel, GT._("Please define an R-group (root and substituent) first."));
				return;
			}
			jcpPanel.get2DHub().getRGroupHandler().cleanUpRGroup(jcpPanel.get2DHub().getChemModel().getMoleculeSet());
			ChemObjectEditor editor = new RGroupEditor(hub);
			editor.setChemObject((org.openscience.cdk.ChemObject)hub.getRGroupHandler().getrGroupQuery());
			ChemObjectPropertyDialog frame = new ChemObjectPropertyDialog(JOptionPane.getFrameForComponent(editor), jcpPanel.get2DHub(),editor);
			frame.pack();
			frame.setVisible(true);
			jcpPanel.get2DHub().updateView();
		}


		//FOLLOWING actions involve undo/redo

		else 
		{

			/* User action: generate possible configurations for the R-group */
			if(type.equals("clearRgroup")) {
				if((jcpPanel.get2DHub().getRGroupHandler()==null)) {
					JOptionPane.showMessageDialog(jcpPanel, GT._("There is no R-group defined"));
					return;
				}
				rGroupHandler=hub.getRGroupHandler();
				hub.unsetRGroupHandler();
				jcpPanel.get2DHub().updateView();

			}

			/* User has indicated that a certain atom in a substituent needs to become attachment point 1 or 2 */
			else if (type.startsWith("setAtomApoAction")) {
				rGroupHandler=hub.getRGroupHandler();
				IAtom apoAtom = (IAtom) eventSource;
				apoLoop:
					for (Iterator<Integer> rnumItr=rGroupHandler.getrGroupQuery().getRGroupDefinitions().keySet().iterator(); rnumItr.hasNext();) {
						for (RGroup rgrp: rGroupHandler.getrGroupQuery().getRGroupDefinitions().get(rnumItr.next()).getRGroups()) {
							if(rgrp.getGroup().contains(apoAtom)) {
								existingRGroupApo= new HashMap <RGroup,Map<Integer,IAtom>>();
								HashMap<Integer,IAtom> map = new HashMap<Integer,IAtom>();
								map.put(1, rgrp.getFirstAttachmentPoint());
								map.put(2, rgrp.getSecondAttachmentPoint());
								existingRGroupApo.put(rgrp,map); 

								boolean firstApo = type.endsWith("1");
								if (firstApo) {
									rgrp.setFirstAttachmentPoint(apoAtom);
								}
								else {
									rgrp.setSecondAttachmentPoint(apoAtom);
								}
								break apoLoop;
							}
						}
					}
			}

			/* User action : certain bond in the root needs to become attachment bond 1 or 2 */
			else if (type.startsWith("setBondApoAction")) {
				rGroupHandler=hub.getRGroupHandler();
				IBond apoBond = (IBond) eventSource;
				Map<Integer, IBond> apoBonds=null;

				//Undo/redo business______
				IAtom pseudo=null;
				if (apoBond.getAtom(0) instanceof IPseudoAtom)
					pseudo=apoBond.getAtom(0);
				else 
					pseudo=apoBond.getAtom(1); 
				Map<Integer, IBond> keepApoBonds = new HashMap<Integer, IBond>();
				if (rGroupHandler.getrGroupQuery().getRootAttachmentPoints()!=null && 
						rGroupHandler.getrGroupQuery().getRootAttachmentPoints().get(pseudo)!=null) {
					apoBonds= rGroupHandler.getrGroupQuery().getRootAttachmentPoints().get(pseudo);
					for (Iterator<Integer> apoItr =apoBonds.keySet().iterator(); apoItr.hasNext();) {
						int apoNum=apoItr.next();
						keepApoBonds.put(apoNum,apoBonds.get(apoNum));
					}
				}
				existingRootAttachmentPoints = new HashMap<IAtom, Map<Integer, IBond>>();
				existingRootAttachmentPoints.put(pseudo, keepApoBonds);
				//________________________


				//Set the new Root APO
				if (rGroupHandler.getrGroupQuery().getRootAttachmentPoints()==null) {
					rGroupHandler.getrGroupQuery().setRootAttachmentPoints(new HashMap<IAtom, Map<Integer, IBond>>());
				}
				Map<IAtom, Map<Integer, IBond>> rootApo=rGroupHandler.getrGroupQuery().getRootAttachmentPoints();
				if (rootApo.get(pseudo)==null) { 
					apoBonds=new HashMap<Integer,IBond>();
					rootApo.put(pseudo, apoBonds);
				}
				else
					apoBonds=rGroupHandler.getrGroupQuery().getRootAttachmentPoints().get(pseudo);

				if (type.endsWith("1")) {
					apoBonds.put(1, apoBond);
					if (apoBonds.get(2)!=null && apoBonds.get(2).equals(apoBond))
						apoBonds.remove(2);
				}
				if (type.endsWith("2")) {
					apoBonds.put(2, apoBond);
					if (apoBonds.get(1)!=null && apoBonds.get(1).equals(apoBond))
						apoBonds.remove(1);
				}

			}


			/* User action: certain atom+bond selection is to be the root structure. */
			else if (type.equals("setRoot")) {

				IAtomContainer atc =selection.getConnectedAtomContainer();
				if (!isProperSelection(atc)) {
					JOptionPane.showMessageDialog(jcpPanel, GT._("Please do not make a fragmented selection."));
					return;
				}

				molecule = createMolecule (atc,existingAtomDistr,existingBondDistr);
				hub.getChemModel().getMoleculeSet().addAtomContainer(molecule);

				if (hub.getRGroupHandler() == null) {
					isNewRgroup=true;
					rgrpQuery = newRGroupQuery(molecule.getBuilder());
					rGroupHandler = new RGroupHandler(rgrpQuery);
					hub.setRGroupHandler(rGroupHandler);
				}
				else {
					rGroupHandler=hub.getRGroupHandler();
					rgrpQuery = hub.getRGroupHandler().getrGroupQuery();
					if(rgrpQuery.getRootStructure()!=null) {
						existingRoot=rgrpQuery.getRootStructure();
						rgrpQuery.getRootStructure().removeProperty(CDKConstants.TITLE);
					}
				}
				molecule.setProperty(CDKConstants.TITLE,RGroup.ROOT_LABEL);
				rgrpQuery.setRootStructure(molecule);

				//Remove old root apo's
				existingRootAttachmentPoints = rgrpQuery.getRootAttachmentPoints();
				rgrpQuery.setRootAttachmentPoints(null);

				//Define new root apo's
				Map<IAtom, Map<Integer, IBond>> apoBonds= new HashMap<IAtom, Map<Integer, IBond>>();
				for (IAtom atom : molecule.atoms()) {
					if (atom instanceof IPseudoAtom) {
						IPseudoAtom pseudo = (IPseudoAtom)atom;
						if (pseudo.getLabel()!=null && RGroupQuery.isValidRgroupQueryLabel(pseudo.getLabel())) {
							chooseRootAttachmentBonds(pseudo,molecule,apoBonds);
						}
					}
				}
				rgrpQuery.setRootAttachmentPoints(apoBonds);

			}

			/* User action: certain atom+bond selection is to be a substituent. */
			else if (type.equals("setSubstitute")) {

				if (hub.getRGroupHandler() == null || hub.getRGroupHandler().getrGroupQuery()==null ||
						hub.getRGroupHandler().getrGroupQuery().getRootStructure()==null) {
					JOptionPane.showMessageDialog(jcpPanel, GT._("Please define a root structure first."));
					return;
				}

				IAtomContainer atc =selection.getConnectedAtomContainer();
				if (!isProperSelection(atc)) {
					JOptionPane.showMessageDialog(jcpPanel, GT._("Please do not make a fragmented selection."));
					return;
				}

				// Check - are there any R-groups -> collect them so that user input can be validated
				Map<Integer,Integer> validRnumChoices=new HashMap<Integer,Integer>();
				for (IAtom atom : hub.getRGroupHandler().getrGroupQuery().getRootStructure().atoms()) {
					if (atom instanceof IPseudoAtom) {
						IPseudoAtom pseudo = (IPseudoAtom)atom;
						if (pseudo.getLabel()!=null && RGroupQuery.isValidRgroupQueryLabel(pseudo.getLabel())) {
							int bondCnt=0;
							int rNum=new Integer(pseudo.getLabel().substring(1));
							for (IBond b : hub.getRGroupHandler().getrGroupQuery().getRootStructure().bonds())
								if (b.contains(atom))
									bondCnt++;

							if ((!validRnumChoices.containsKey(rNum))||
									validRnumChoices.containsKey(rNum)&& validRnumChoices.get(rNum)<bondCnt)
								validRnumChoices.put(rNum,bondCnt);
						}
					}
				}
				// Here we test: the user wants to define a substitute, but are there any R1..R32 groups to begin with?
				if (validRnumChoices.size()==0) {
					JOptionPane.showMessageDialog(jcpPanel, GT._("There are no numbered R-atoms in the root structure to refer to."));
					return;
				}

				//Now get user input to determine which R# atom to hook up with the substituent 
				boolean inputOkay=false;
				String userInput=null;
				Integer rNum=0;
				do {
					userInput = JOptionPane.showInputDialog(GT._("Enter an R-group number "),validRnumChoices.get(0));
					if (userInput == null)
						return;
					try {
						rNum = new Integer(userInput);
						if(!validRnumChoices.containsKey(rNum))
							JOptionPane.showMessageDialog(null, GT._("The number you entered has no corresponding R-group in the root."));
						else
							inputOkay=true;
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, GT._("This is not a valid R-group label.\nPlease label in range R1 .. R32"));
					}
				}
				while (!inputOkay);
				rGroupHandler=hub.getRGroupHandler();

				rgrpQuery = hub.getRGroupHandler().getrGroupQuery();
				if (rgrpQuery.getRGroupDefinitions()==null) {
					rgrpQuery.setRGroupDefinitions(new HashMap<Integer, RGroupList>());
				}

				if (rgrpQuery.getRGroupDefinitions().get(rNum)==null) {
					RGroupList rList = new RGroupList(rNum);
					rList.setRGroups(new ArrayList<RGroup>());
					rgrpQuery.getRGroupDefinitions().put(rNum, rList);
				}

				molecule = createMolecule (atc,existingAtomDistr,existingBondDistr);
				existingRgroupLists = new HashMap<Integer,RGroupList>();

				// Now see if the user's choice for a substituent has overlaps with already defined existing 
				// substitutes. If so, these existing ones get thrown out (we can't have multiple substituents
				// defined for the same atoms. 
				for(Iterator<Integer> itr=rgrpQuery.getRGroupDefinitions().keySet().iterator();itr.hasNext();) {
					int rgrpNum=itr.next();
					RGroupList rgrpList = rgrpQuery.getRGroupDefinitions().get(rgrpNum);
					if(rgrpList!=null) {
						existingRgroupLists.put(rgrpNum,  makeClone(rgrpList));
						List<RGroup> cleanList = new ArrayList<RGroup>();
						for (int j=0; j<rgrpList.getRGroups().size(); j++){
							RGroup subst= rgrpList.getRGroups().get(j);
							boolean remove=false;
							removeCheck:
								for(IAtom atom : molecule.atoms()) {
									if (subst.getGroup().contains(atom)) {
										remove=true;
										break removeCheck;
									}
								}
							if (!remove) {
								cleanList.add(subst);
							}
						}
						rgrpList.setRGroups(cleanList);
					}
				}

				hub.getChemModel().getMoleculeSet().addAtomContainer(molecule);
				molecule.setProperty(CDKConstants.TITLE,RGroup.makeLabel(rNum));

				RGroup rgrp = new RGroup();
				rgrp.setGroup(molecule);
				rgrpQuery.getRGroupDefinitions().get(rNum).getRGroups().add(rgrp);

				//Set default APO atoms (randomly picked) for the new substitute
				int apoCount=validRnumChoices.get(rNum);
				int apoSet=0;
				apoBreak:
					for(IAtom atom : molecule.atoms()) {
						if (apoSet==apoCount)
							break apoBreak;
						if (apoSet==0) {
							rgrp.setFirstAttachmentPoint(atom);
						}
						if (apoSet==1) {
							rgrp.setSecondAttachmentPoint(atom);
						}
						apoSet++;
					}
			}


			if (hub.getUndoRedoFactory() != null && jcpPanel.get2DHub().getUndoRedoHandler() != null) {
				IUndoRedoable undoredo = jcpPanel.get2DHub().getUndoRedoFactory().getRGroupEdit
				( type, isNewRgroup, hub,rGroupHandler, existingAtomDistr, existingBondDistr, 
						existingRoot, existingRootAttachmentPoints, existingRGroupApo, existingRgroupLists,molecule);
				jcpPanel.get2DHub().getUndoRedoHandler().postEdit(undoredo);
			}

			jcpPanel.get2DHub().updateView();
		}
	}

	/**
	 * Initializes an empty RGroupQuery.
	 * 
	 * @return a new empty RGroupQuery
	 */
	private IRGroupQuery newRGroupQuery(IChemObjectBuilder builder) {
		IRGroupQuery rgrpQuery = new RGroupQuery(DefaultChemObjectBuilder.getInstance());
		rgrpQuery.setRootStructure(builder.newInstance(IAtomContainer.class));
		rgrpQuery
				.setRootAttachmentPoints(new HashMap<IAtom, Map<Integer, IBond>>());
		rgrpQuery.setRGroupDefinitions(new HashMap<Integer, RGroupList>());
		return rgrpQuery;
	}

	/**
	 * Chooses (picks) one or more attachment bonds for a (new) R# atom that is
	 * a root member.
	 * 
	 * @param rAtom
	 * @param root
	 * @param rootAttachmentPoints
	 */
	private void chooseRootAttachmentBonds(IAtom rAtom, IAtomContainer root,
			Map<IAtom, Map<Integer, IBond>> rootAttachmentPoints) {
		int apoIdx = 1;
		Map<Integer, IBond> apoBonds = new HashMap<Integer, IBond>();
		Iterator<IBond> bonds = root.bonds().iterator();
		// Pick up to two apo bonds randomly
		while (bonds.hasNext() && apoIdx <= 2) {
			IBond bond = bonds.next();
			if (bond.contains(rAtom)) {
				apoBonds.put((apoIdx), bond);
				apoIdx++;
			}
		}
		rootAttachmentPoints.put(rAtom, apoBonds);
	}

	/**
	 * Determines if a user has made a proper selection for R-Group
	 * manipulation. Proper means: make a selection that includes all
	 * atoms/bonds that are bound together in a structure, not leaving any
	 * orphans dangling.
	 * 
	 * @param atc
	 * @return
	 */
	private boolean isProperSelection(IAtomContainer atc) {
		boolean properSelection = true;
		completeSelection: for (IAtom atom : atc.atoms()) {

			IAtomContainer modelAtc = ChemModelManipulator
					.getRelevantAtomContainer(jcpPanel.getChemModel(), atom);
			List<IAtom> connectedAtoms = new ArrayList<IAtom>();
			findConnectedAtoms(atom, modelAtc, connectedAtoms);
			for (IAtom modelAt : connectedAtoms) {
				if (!atc.contains(modelAt)) {
					properSelection = false;
					break completeSelection;
				}
			}
		}
		return properSelection;
	}

	/**
	 * Starting from start point atom, finds all other atoms connected to it by
	 * traversing a graph. Used to determine a proper selection.
	 * 
	 * @param atom
	 * @param atc
	 * @param result
	 */
	private void findConnectedAtoms(IAtom atom, IAtomContainer atc,
			List<IAtom> result) {
		result.add(atom);
		for (IBond bond : atc.bonds()) {
			if (bond.contains(atom)) {
				if (!result.contains(bond.getConnectedAtom(atom))) {
					findConnectedAtoms(bond.getConnectedAtom(atom), atc, result);
				}
			}
		}
	}

	/**
	 * Creates a new molecule based on a user selection, and removes the
	 * selected atoms/bonds from the atom container where they are currently in.
	 */
	private IAtomContainer createMolecule(IAtomContainer atc,
			Map<IAtom, IAtomContainer> existingAtomDistr,
			Map<IBond, IAtomContainer> existingBondDistr) {
		for (IAtom atom : atc.atoms()) {
			IAtomContainer original = ChemModelManipulator
					.getRelevantAtomContainer(jcpPanel.getChemModel(), atom);
			existingAtomDistr.put(atom, original);
			original.removeAtom(atom);
		}
		for (IBond bond : atc.bonds()) {
			IAtomContainer original = ChemModelManipulator
					.getRelevantAtomContainer(jcpPanel.getChemModel(), bond);
			existingBondDistr.put(bond, original);
			original.removeBond(bond);
		}
		IAtomContainer molecule = atc.getBuilder().newInstance(IAtomContainer.class);
		molecule.add(atc);
		return molecule;
	}

	/**
	 * Clones an RGroupList
	 * 
	 * @param original
	 * @return
	 */
	private static RGroupList makeClone(RGroupList original) {
		RGroupList clone = new RGroupList(original.getRGroupNumber());
		try {
			clone.setOccurrence(original.getOccurrence());
			clone.setRequiredRGroupNumber(original.getRequiredRGroupNumber());
			clone.setRestH(original.isRestH());
			List<RGroup> rgpList = new ArrayList<RGroup>();
			for (RGroup r : original.getRGroups()) {
				rgpList.add(r);
			}
			clone.setRGroups(rgpList);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		return clone;
	}
}
