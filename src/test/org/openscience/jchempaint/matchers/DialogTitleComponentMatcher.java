package org.openscience.jchempaint.matchers;

import javax.swing.JDialog;

import org.fest.swing.core.GenericTypeMatcher;

public class DialogTitleComponentMatcher extends GenericTypeMatcher<JDialog> {

	private String text;
	
	public DialogTitleComponentMatcher(String text){
		super(JDialog.class);
		this.text=text;
	}
	@Override
	protected boolean isMatching(JDialog arg0) {
		System.err.println(arg0.getTitle()+" "+text);
		System.err.println(arg0.getTitle().equals(text));
		if(arg0.getTitle()!=null && arg0.getTitle().equals(text))
			return true;
		else
			return false;
	}

}
