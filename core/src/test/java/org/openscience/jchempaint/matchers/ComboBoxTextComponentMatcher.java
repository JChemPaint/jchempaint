package org.openscience.jchempaint.matchers;

import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;

public class ComboBoxTextComponentMatcher extends GenericTypeMatcher<JComboBox> {

	private String text;
	private String altText;
	
	public ComboBoxTextComponentMatcher(String text){
		super(JComboBox.class);
		this.text=text;
	}
	public ComboBoxTextComponentMatcher(String text, String altText) {
	    super(JComboBox.class);
        this.text = text;
        this.altText = altText;
    }
    @Override
	protected boolean isMatching(JComboBox arg0) {
        if(!arg0.isShowing())
            return false;
		if(arg0.getSelectedItem()!=null && arg0.getSelectedItem().toString().indexOf(text)==0)
			return true;
		else
		    if(arg0.getSelectedItem()!=null && altText!=null && arg0.getSelectedItem().toString().indexOf(altText)==0)
		        return true;
		    else
		        return false;
	}

}
