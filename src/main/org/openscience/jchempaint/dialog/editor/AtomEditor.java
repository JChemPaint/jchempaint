/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
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

import java.io.IOException;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.controller.IChemModelRelay;

/**
 */
public class AtomEditor extends ChemObjectEditor {
    
    private static final long serialVersionUID = -6693485657147158966L;
    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(AtomEditor.class);
    JTextField symbolField;
    JSpinner   hCountField;
    JSpinner   formalChargeField;
    JSpinner   isotopeField;
    IChemModelRelay hub;
    int majorIsotopeNumber;
    
	public AtomEditor(IChemModelRelay hub) {
        super(false);
        constructPanel();
        this.hub=hub;
        super.mayclose=false;
	}
    
    private void constructPanel() {
        symbolField = new JTextField(4);
        symbolField.getDocument().addDocumentListener(new MyDocumentListener(this));
        addField("Symbol", symbolField, this);
        hCountField = new JSpinner(new SpinnerNumberModel());
        addField("H Count", hCountField, this);
        formalChargeField = new JSpinner(new SpinnerNumberModel());
        addField("Formal Charge", formalChargeField, this);
        isotopeField = new JSpinner(new SpinnerNumberModel());
        addField("Isotope number", isotopeField, this);
    }
    
    public void setChemObject(IChemObject object) {
        if (object instanceof IAtom) {
            source = object;
            // update table contents
            IAtom atom = (IAtom)source;
            symbolField.setText(atom.getSymbol());
            hCountField.setValue(new Integer(atom.getHydrogenCount()==null ? 0 : atom.getHydrogenCount()));
            formalChargeField.setValue(new Integer(atom.getFormalCharge()));
            try {
                IIsotope isotope = 
                    IsotopeFactory
                        .getInstance(
                                atom.getBuilder()).getMajorIsotope(
                                atom.getSymbol());
                majorIsotopeNumber = isotope.getMassNumber();
                isotopeField.setValue(atom.getMassNumber()-majorIsotopeNumber);
            } catch (Exception exception) {
                logger.error("Error while configuring atom");
                logger.debug(exception);
                isotopeField.setValue(0);
            }
        } else {
            throw new IllegalArgumentException("Argument must be an Atom");
        }
    }
	
    public void applyChanges() {
        IAtom atom = (IAtom)source;
        try{
	        if(IsotopeFactory.getInstance(atom.getBuilder()).getElement(symbolField.getText())!=null){
	        	if(atom.getHydrogenCount()==null || atom.getHydrogenCount()!=((Integer)hCountField.getValue()).intValue())
	        		hub.setHydrogenCount(atom,((Integer)hCountField.getValue()).intValue());
	        	if(atom.getFormalCharge()==null || atom.getFormalCharge()!=((Integer)formalChargeField.getValue()).intValue())
	        		hub.setCharge(atom,((Integer)formalChargeField.getValue()).intValue());
	        	if(!atom.getSymbol().equals(symbolField.getText()))
	        		hub.setSymbol(atom, symbolField.getText());
	        	if(atom.getMassNumber()==null || atom.getMassNumber()!=majorIsotopeNumber+((Integer)isotopeField.getValue()).intValue())
	        		hub.setMassNumber(atom,majorIsotopeNumber+((Integer)isotopeField.getValue()).intValue());
	        }else{
	            PseudoAtom pseudo = new PseudoAtom(atom);
	            pseudo.setLabel(symbolField.getText());
	            pseudo.setHydrogenCount(((Integer)hCountField.getValue()).intValue());
	            pseudo.setFormalCharge(((Integer)formalChargeField.getValue()).intValue());
	            pseudo.setMassNumber(majorIsotopeNumber+((Integer)isotopeField.getValue()).intValue());
	            hub.replaceAtom(pseudo, atom);
	        }
        }catch(IOException ex){
        	if(atom.getHydrogenCount()!=((Integer)hCountField.getValue()).intValue())
        		hub.setHydrogenCount(atom,((Integer)hCountField.getValue()).intValue());
        	if(atom.getFormalCharge()!=((Integer)formalChargeField.getValue()).intValue())
        		hub.setCharge(atom,((Integer)formalChargeField.getValue()).intValue());
        	if(!atom.getSymbol().equals(symbolField.getText()))
        		hub.setSymbol(atom, symbolField.getText());
        	if(atom.getMassNumber()!=majorIsotopeNumber+((Integer)isotopeField.getValue()).intValue())
        		hub.setMassNumber(atom,majorIsotopeNumber+((Integer)isotopeField.getValue()).intValue());
        	logger.error("IOException when trying to test element symbol");
        }
    }
    
    class MyDocumentListener implements DocumentListener {
        AtomEditor atomEditor;
    	public MyDocumentListener(AtomEditor atomEditor){
    		this.atomEditor = atomEditor;
    	}
        public void insertUpdate(DocumentEvent e) {
        	checkValidity(e);
        }
        public void removeUpdate(DocumentEvent e) {
            checkValidity(e);
        }
        private void checkValidity(DocumentEvent e){
            if(((Document)e.getDocument()).getLength()==0)
            	atomEditor.mayclose=false;
            else
            	atomEditor.mayclose=true;
        }
        public void changedUpdate(DocumentEvent e) {
            //Plain text components do not fire these events
        }
    }
}


