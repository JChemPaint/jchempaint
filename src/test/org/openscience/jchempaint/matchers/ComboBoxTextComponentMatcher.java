package org.openscience.jchempaint.matchers;

import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;

public class ComboBoxTextComponentMatcher extends GenericTypeMatcher<JComboBox> {

	private String text;
	
	public ComboBoxTextComponentMatcher(String text){
		super(JComboBox.class);
		this.text=text;
	}
	@Override
	protected boolean isMatching(JComboBox arg0) {
		if(arg0.getSelectedItem()!=null && arg0.getSelectedItem().toString().indexOf(text)==0)
			return true;
		else
			return false;
	}

}
