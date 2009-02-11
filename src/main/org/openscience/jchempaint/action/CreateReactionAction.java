/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Christoph Steinbeck, Stefan Kuhn
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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.GT;


/**
 * Creates a reaction object
 *
 */
public class CreateReactionAction extends JCPAction
{

	private static final long serialVersionUID = -7625810885316702776L;

	public void actionPerformed(ActionEvent event)
	{
		IChemObject object = getSource(event);

		logger.debug("CreateReaction action");
		IChemModel model = jcpPanel.getChemModel();
		IReactionSet reactionSet = model.getReactionSet();
		if (reactionSet == null)
		{
			reactionSet = model.getBuilder().newReactionSet();
		}
		IAtomContainer container = null;
		if (object instanceof IAtom)
		{
			container = ChemModelManipulator.getRelevantAtomContainer(model, (IAtom) object);
		} else
		{
			logger.error("Cannot add to reaction object of type: " + object.getClass().getName());
		}
		if (container == null)
		{
			logger.error("Cannot find container to add object to!");
		} else
		{
			IAtomContainer newContainer;
			try {
				newContainer = (IAtomContainer) container.clone();
			} catch (CloneNotSupportedException e) {
				logger.error("Could not clone IAtomContainer: ", e.getMessage());
				logger.debug(e);
				return;
			}

			logger.debug("type: ", type);
			if ("addReactantToNew".equals(type))
			{
				IReaction reaction = model.getBuilder().newReaction();
				reaction.setID("reaction-" + System.currentTimeMillis());
				reaction.addReactant(model.getBuilder().newMolecule(newContainer));
				reactionSet.addReaction(reaction);
			} else if ("addReactantToExisting".equals(type))
			{
				if (reactionSet.getReactionCount() == 0)
				{
					logger.warn("Cannot add to reaction if no one exists");
					JOptionPane.showMessageDialog(jcpPanel, GT._("No reaction existing. Cannot add therefore to something!"), GT._("No existing reactions"), JOptionPane.WARNING_MESSAGE);
					return;
				} else
				{
//					XXX needs fixing
					Object[] ids = getReactionIDs(reactionSet);
					
					String s = (String) JOptionPane.showInputDialog(
							//jcpPanel.getFrame(),
                            null,
							"Reaction Chooser",
							"Choose reaction to add reaction to",
							JOptionPane.PLAIN_MESSAGE,
							null,
							ids,
							ids[0]
							);
					//String s2 = "";

					if ((s != null) && (s.length() > 0))
					{
						String selectedReactionID = s;
						IReaction reaction = getReaction(reactionSet, selectedReactionID);
						reaction.addReactant(model.getBuilder().newMolecule(newContainer));
					} else
					{
						logger.error("No reaction selected");
					}
				}
			} else if ("addProductToNew".equals(type))
			{
				IReaction reaction = model.getBuilder().newReaction();
				reaction.setID("reaction-" + System.currentTimeMillis());
				reaction.addProduct(model.getBuilder().newMolecule(newContainer));
				reactionSet.addReaction(reaction);
			} else if ("addProductToExisting".equals(type))
			{
				if (reactionSet.getReactionCount() == 0)
				{
					logger.warn("Cannot add to reaction if no one exists");
					JOptionPane.showMessageDialog(jcpPanel, GT._("No reaction existing. Cannot add therefore to something!"), GT._("No existing reactions"), JOptionPane.WARNING_MESSAGE);
					return;
				} else
				{
					//XXX needs fixing
					
					Object[] ids = getReactionIDs(reactionSet);
					String s = (String) JOptionPane.showInputDialog(
                            //jcpPanel.getFrame(),
							null,
                            "Reaction Chooser",
							"Choose reaction to add reaction to",
							JOptionPane.PLAIN_MESSAGE,
							null,
							ids,
							ids[0]
							);
					//String s2 = "";

					if ((s != null) && (s.length() > 0))
					{
						String selectedReactionID = s;
						IReaction reaction = getReaction(reactionSet, selectedReactionID);
						reaction.addProduct(model.getBuilder().newMolecule(newContainer));
					} else
					{
						logger.error("No reaction selected");
					}
				}
			} else
			{
				logger.warn("Don't know about this action type: " + type);
				return;
			}
		}
		// delete atoms in current model
		model.getMoleculeSet().removeAtomContainer(container);
		logger.debug("Deleted atom from old container...");
		model.setReactionSet(reactionSet);
		jcpPanel.get2DHub().updateView();
	}


	/**
	 *  Gets the reactionIDs attribute of the CreateReactionAction object
	 *
	 *@param  reactionSet  Description of the Parameter
	 *@return              The reactionIDs value
	 */
	private Object[] getReactionIDs(IReactionSet reactionSet)
	{
		if (reactionSet != null)
		{
			
			String[] ids = new String[reactionSet.getReactionCount()];
			for (int i = 0; i < reactionSet.getReactionCount(); i++)
			{
				ids[i] = reactionSet.getReaction(i).getID();
			}
			return ids;
		} else
		{
			return new String[0];
		}
	}


	/**
	 *  Gets the reaction attribute of the CreateReactionAction object
	 *
	 *@param  reactionSet  Description of the Parameter
	 *@param  id           Description of the Parameter
	 *@return              The reaction value
	 */
	private org.openscience.cdk.interfaces.IReaction getReaction(org.openscience.cdk.interfaces.IReactionSet reactionSet, String id)
	{
		Iterable<IReaction> reactionIter = reactionSet.reactions();
		for (IReaction reaction : reactionIter)
		{
			if (reaction.getID().equals(id))
			{
				return reaction;
			}
		}
		return null;
	}
}

