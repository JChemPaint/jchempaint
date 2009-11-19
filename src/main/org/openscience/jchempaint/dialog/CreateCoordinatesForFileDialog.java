/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-05-01 20:15:34 +0100 (Tue, 01 May 2007) $
 *  $Revision: 8292 $
 *
 *  Copyright (C) 1997-2007  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sf.net
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
package org.openscience.jchempaint.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.geometry.Projector;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.ControllerHub;

/**
 * Dialog for coordinate creationg
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class CreateCoordinatesForFileDialog extends JInternalFrame
{

	private static final long serialVersionUID = 6717348756533287248L;
	
	private IChemModel chemModel;
	private JRadioButton generate2DButton;
	private JRadioButton from3DButton;


	/**
	 *  Constructor for the CreateCoordinatesForFileDialog object
	 *
	 *@param  model  Description of the Parameter
	 */
	public CreateCoordinatesForFileDialog(IChemModel model)
	{
		super("Coordinate Creation", true, true, true, true);

		this.chemModel = model;
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// options
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new GridLayout(0, 1));
		ButtonGroup group = new ButtonGroup();
		generate2DButton = new JRadioButton("create with layout algorithm");
		group.add(generate2DButton);
		radioPanel.add(generate2DButton);
		if (GeometryTools.has3DCoordinates(chemModel)) {
			from3DButton = new JRadioButton("create from 3D coordinates in file");
			group.add(from3DButton);
			radioPanel.add(from3DButton);
			from3DButton.setSelected(true);
		} else
		{
			generate2DButton.setSelected(true);
		}

		JPanel optionPane = new JPanel();
		optionPane.setLayout(new GridLayout(0, 1));
		JLabel label = new JLabel("The file does not contain 2D Coordinates or only some. Should I create those?");
		optionPane.add(label);
		optionPane.add(radioPanel);

		//buttons
		JButton cancelButton = new JButton("Cancel");
		JButton createButton = new JButton("Create");
		cancelButton.addActionListener(new CancelAction());
		createButton.addActionListener(new CreateAction());
		getRootPane().setDefaultButton(createButton);

		//Lay out the buttons from left to right.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(createButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(cancelButton);

		contentPane.add(optionPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	public void closeFrame()
	{
		dispose();
	}

	class CancelAction extends AbstractAction
	{

		private static final long serialVersionUID = -2305492502437164455L;

		CancelAction()
		{
			super("Cancel");
		}

		public void actionPerformed(ActionEvent event)
		{
			closeFrame();
		}
	}


	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 */
	class CreateAction extends AbstractAction
	{

		private static final long serialVersionUID = 7041050310635125218L;
		
		StructureDiagramGenerator diagramGenerator;

		CreateAction()
		{
			super("Create");
			this.diagramGenerator = new StructureDiagramGenerator();
		}

		public void actionPerformed(ActionEvent event)
		{
			if (from3DButton != null && from3DButton.isSelected())
			{
				// JOptionPane.showMessageDialog(jchempaint, "Not implemented yet");
				Iterator containers = ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
				while (containers.hasNext()) {
					Projector.project2D((IAtomContainer)containers.next());
				}
			} else
			{
		        for (IAtomContainer container :
		            ChemModelManipulator.getAllAtomContainers(chemModel)) {
		            if (ConnectivityChecker.isConnected(container)) {
		                ControllerHub.generateNewCoordinates(container);
		            } else {
		                // deal with disconnected atom containers
		                IMoleculeSet molecules =
		                    ConnectivityChecker.partitionIntoMolecules(container);
		                for (IAtomContainer subContainer : molecules.molecules()) {
		                	ControllerHub.generateNewCoordinates(subContainer);
		                }

		            }
		        }
		        ControllerHub.avoidOverlap(chemModel);
			}
			closeFrame();
		}

	}

}

