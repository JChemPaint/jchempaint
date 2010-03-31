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

package org.openscience.jchempaint.dialog.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 * Editor for R-group 'advanced logic': occurrence, restH and the if/then condition.
 *
 */
public class RGroupEditor extends ChemObjectEditor {

	private static final long serialVersionUID = -9076542982586267285L;

	private IChemModelRelay hub;
	private List<Integer> rNumbers;
	private List<JPanel> panels;

	public RGroupEditor(IChemModelRelay hub) {
		super(true);
		this.hub=hub;
		panels= new ArrayList<JPanel>();
		constructPanel();
	}

	private void constructPanel() {

		rNumbers= new ArrayList<Integer>();
		for (Iterator<Integer> rnumItr=hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().keySet().iterator(); rnumItr.hasNext();) {
			rNumbers.add(rnumItr.next());
		}
		Collections.sort(rNumbers);

		for(Integer r : rNumbers) {
			JPanel rgrpPanel = this.addTab("R"+r);

			JTextField occurrenceField = new JTextField(20);
			occurrenceField.setName("occurrence");

			addField(GT._("Occurrence"), occurrenceField, rgrpPanel,DEF_INSET);

			String[] trueFalseStrings = { GT._("True"), GT._("False")};
			JComboBox restHBox= new JComboBox(trueFalseStrings);
			restHBox.setName("restH");
			addField(GT._("Rest H"), restHBox, rgrpPanel,DEF_INSET);

			List<String> otherRnums = new ArrayList<String>();
			otherRnums.add(GT._("None"));
			for (Iterator<Integer> rnumItr=hub.getRGroupHandler().getrGroupQuery().getRGroupDefinitions().keySet().iterator(); rnumItr.hasNext();) {
				int r_=rnumItr.next();
				if (r_!= r) {
					otherRnums.add("R"+r_);
				}
			}
			String[] ifThenStrings=(String[])(otherRnums.toArray(new String[otherRnums.size()]));
			JComboBox ifThenBox = new JComboBox(ifThenStrings);
			ifThenBox.setName("ifThen");
			addField(GT._("If R"+r+" then "), ifThenBox, rgrpPanel,DEF_INSET);
			panels.add(rgrpPanel);
		}
	}

	public void setChemObject(IChemObject object) {
		if (object instanceof IRGroupQuery) {
			source = object;
			IRGroupQuery rgroupQuery = (IRGroupQuery)source;
			for (int i=0; i< rNumbers.size(); i++) {
				int r = rNumbers.get(i);
				JPanel rgrpPanel = panels.get(i);

				RGroupList rgrpList = rgroupQuery.getRGroupDefinitions().get(r);

				JTextField occurrenceField = (JTextField) (rgrpPanel.getComponent(1));
				String occ=rgrpList.getOccurrence();
				occurrenceField.setText(occ);

				JComboBox restHBox = (JComboBox) (rgrpPanel.getComponent(3));
				boolean restH=rgrpList.isRestH();
				String restHString= restH? GT._("True"): GT._("False");
				restHBox.setSelectedItem(restHString);

				JComboBox ifThenBox = (JComboBox) (rgrpPanel.getComponent(5));
				int ifThenR = rgrpList.getRequiredRGroupNumber();
				String ifThenRString= ifThenR==0?GT._("None"):"R"+ifThenR; 
				ifThenBox.setSelectedItem(ifThenRString);
			}

		} else {
			throw new IllegalArgumentException("Argument must be a IRGroupQuery");
		}
	}

	public void applyChanges() {
		IRGroupQuery rgroupQuery = (IRGroupQuery)source;

		//Validate the "occurence" input
		for (int i=0; i< rNumbers.size(); i++) {
			int r = rNumbers.get(i);
			JPanel rgrpPanel = panels.get(i);
			RGroupList rgrpList = rgroupQuery.getRGroupDefinitions().get(r);

			JTextField occurrenceField = (JTextField) (rgrpPanel.getComponent(1));
			String userOccurrenceText=occurrenceField.getText();
			if (userOccurrenceText.trim().equals("") || !RGroupList.isValidOccurrenceSyntax(userOccurrenceText)) {
				throw new RuntimeException (GT._("Invalid occurrence specified for {0}","R"+r));
			}
		}

		//Aply input to model
		for (int i=0; i< rNumbers.size(); i++) {
			int r = rNumbers.get(i);
			JPanel rgrpPanel = panels.get(i);
			RGroupList rgrpList = rgroupQuery.getRGroupDefinitions().get(r);

			JTextField occurrenceField = (JTextField) (rgrpPanel.getComponent(1));
			String userOccurrenceText=occurrenceField.getText();
			try {
				rgrpList.setOccurrence(userOccurrenceText);
			} catch (CDKException e) {
				// won't happen - already checked in previous loop
				e.printStackTrace();
			}

			JComboBox restHBox = (JComboBox) (rgrpPanel.getComponent(3));
			String restHString= (String) (restHBox.getSelectedItem());
			if (restHString.equals(GT._("True")))
				rgrpList.setRestH(true);
			else
				rgrpList.setRestH(false);


			JComboBox ifThenBox = (JComboBox) (rgrpPanel.getComponent(5));
			String ifThenR= (String) (ifThenBox.getSelectedItem());
			if (ifThenR.equals(GT._("None")))
				rgrpList.setRequiredRGroupNumber(0);
			else {
				int userRnumInput = new Integer (ifThenR.substring(1));
				rgrpList.setRequiredRGroupNumber(userRnumInput);
			}
		}


	}


}
