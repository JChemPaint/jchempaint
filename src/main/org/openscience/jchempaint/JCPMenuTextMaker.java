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
package org.openscience.jchempaint;

import java.util.HashMap;
import java.util.Map;

import org.openscience.jchempaint.applet.JChemPaintEditorApplet;

/**
 * This class held text entries for menu items, tool tips etc., which are
 * configured in the JCPGUI_*.properties files. They all need an entry
 * in the entries map to be recognized by the localization system. The same
 * is true about the DrawModeString for the status bar, which come from 
 * controller modules in cdk and are only in English there.
 *
 */
public class JCPMenuTextMaker {
	private static JCPMenuTextMaker instance=null;
	private Map<String,String> entries=new HashMap<String,String>();
	
	/**
	 * The constructor. Private since class is a singleton.
	 */
	private JCPMenuTextMaker(String guistring){
		entries.put("file", GT._("File"));
		entries.put("new", GT._("New"));
		entries.put("atomMenuTitle", GT._("Atom Popup Menu"));
		entries.put("open", GT._("Open"));
		entries.put("saveAs", GT._("Save As..."));
		entries.put("view", GT._("View"));
		entries.put("print", GT._("Print..."));
		entries.put("export", GT._("Save As Image..."));
		entries.put("save", GT._("Save"));
		entries.put("edit", GT._("Edit"));
		entries.put("report", GT._("Report"));
		entries.put("close", GT._("Close"));
		entries.put("exit", GT._("Exit"));
		entries.put("undo", GT._("Undo"));
		entries.put("redo", GT._("Redo"));
		entries.put("selectAll", GT._("Select All"));
		entries.put("copy", GT._("Copy"));
		entries.put("copyAsSmiles", GT._("Copy As SMILES"));
		entries.put("eraser", GT._("Delete"));
		entries.put("paste", GT._("Paste"));
		entries.put("pasteTemplate", GT._("All Templates"));
		entries.put("cut", GT._("Cut"));
		entries.put("atomMenu", GT._("Atom"));
		entries.put("bondMenu", GT._("Bond"));
		entries.put("tools", GT._("Tools"));
		entries.put("templates", GT._("Templates"));
		entries.put("radical", GT._("Radical"));
		entries.put("bond", GT._("Single"));
		entries.put("down_bond", GT._("Stereo Down"));
		entries.put("up_bond", GT._("Stereo Up"));
		entries.put("undefined_bond", GT._("Undefined Stereo"));
		entries.put("undefined_stereo_bond", GT._("Undefined E/Z"));
		entries.put("formalCharge", GT._("Charge"));
		entries.put("plus", GT._("Plus"));
		entries.put("minus", GT._("Minus"));
		entries.put("hydrogen", GT._("Implicit Hydrogens"));
		entries.put("flip", GT._("Flip"));
		entries.put("cleanup", GT._("Clean Structure"));
		entries.put("toolbar", GT._("Toolbar"));
		entries.put("statusbar", GT._("Statusbar"));
		entries.put("menubar", GT._("Menubar"));
		entries.put("insertstructure", GT._("Direct Entry as SMILES/InChI/CAS"));
		entries.put("zoomin", GT._("Zoom In"));
		entries.put("zoomout", GT._("Zoom Out"));
		entries.put("zoomoriginal", GT._("Zoom 100%"));
		entries.put("options", GT._("Preferences..."));
		entries.put("createSMILES", GT._("Create SMILES"));
		entries.put("createInChI", GT._("Create InChI"));
		entries.put("help", GT._("Help"));
		entries.put("tutorial", GT._("Tutorial"));
		entries.put("feedback", GT._("Report Feedback"));
		entries.put("license", GT._("License"));
		entries.put("about", GT._("About"));
		entries.put("hydroon", GT._("On All"));
		entries.put("hydrooff", GT._("Off"));
		entries.put("flipHorizontal", GT._("Horizontal"));
		entries.put("flipVertical", GT._("Vertical"));
		entries.put("selectFromChemObject", GT._("Select"));
		entries.put("symbolChange", GT._("Change Element"));
		entries.put("periodictable", GT._("Periodic Table"));
		entries.put("enterelement", GT._("Custom"));
		entries.put("isotopeChange", GT._("Isotopes"));
		entries.put("convertToRadical", GT._("Add Electron Pair"));
                entries.put("convertFromRadical", GT._("Remove Electron Pair"));
		entries.put("showChemObjectProperties", GT._("Properties"));
		entries.put("showACProperties", GT._("Molecule Properties"));
		entries.put("makeNormal", GT._("Convert to Regular Atom"));
		entries.put("commonSymbols", GT._("Common Elements"));
		entries.put("halogenSymbols", GT._("Halogens"));
		entries.put("nobelSymbols", GT._("Nobel Gases"));
		entries.put("alkaliMetals", GT._("Alkali Metals"));
		entries.put("alkaliEarthMetals", GT._("Alkali Earth Metals"));
		entries.put("transitionMetals", GT._("Transition Metals"));
		entries.put("metals", GT._("Metals"));
		entries.put("metalloids", GT._("Metalloids"));
		entries.put("Transition m", GT._("Non-Metals"));
		entries.put("pseudoSymbols", GT._("Pseudo Atoms"));
		entries.put("majorPlusThree", GT._("Major Plus {0}","3"));
		entries.put("majorPlusTwo", GT._("Major Plus {0}","2"));
		entries.put("majorPlusOne", GT._("Major Plus {0}","1"));
		entries.put("major", GT._("Major Isotope"));
		entries.put("majorMinusOne", GT._("Major Minus {0}","1"));
		entries.put("majorMinusTwo", GT._("Major Minus {0}","2"));
		entries.put("majorMinusThree", GT._("Major Minus {0}","3"));
		entries.put("valence", GT._("Valence"));
		entries.put("valenceOff", GT._("Valence Off"));
		entries.put("valence1", GT._("Valence {0}","1"));
		entries.put("valence2", GT._("Valence {0}","2"));
		entries.put("valence3", GT._("Valence {0}","3"));
		entries.put("valence4", GT._("Valence {0}","4"));
		entries.put("valence5", GT._("Valence {0}","5"));
		entries.put("valence6", GT._("Valence {0}","6"));
		entries.put("valence7", GT._("Valence {0}","7"));		
		entries.put("valence8", GT._("Valence {0}","8"));
		entries.put("symbolC", GT._("C"));
		entries.put("symbolO", GT._("O"));
		entries.put("symbolN", GT._("N"));
		entries.put("symbolH", GT._("H"));
		entries.put("symbolP", GT._("P"));
		entries.put("symbolS", GT._("S"));
		entries.put("symbolF", GT._("F"));
		entries.put("symbolCl", GT._("Cl"));
		entries.put("symbolBr", GT._("Br"));
		entries.put("symbolI", GT._("I"));
		entries.put("symbolHe", GT._("He"));
		entries.put("symbolNe", GT._("Ne"));
		entries.put("symbolAr", GT._("Ar"));
		entries.put("symbolB", GT._("B"));
		entries.put("symbolP", GT._("P"));
		entries.put("symbolLi", GT._("Li"));
		entries.put("symbolBe", GT._("Be"));
		entries.put("symbolNa", GT._("Na"));
		entries.put("symbolMg", GT._("Mg"));
		entries.put("symbolAl", GT._("Al"));
		entries.put("symbolSi", GT._("Si"));
		entries.put("symbolFe", GT._("Fe"));
		entries.put("symbolCo", GT._("Co"));
		entries.put("symbolAg", GT._("Ag"));
		entries.put("symbolPt", GT._("Pt"));
		entries.put("symbolAu", GT._("Au"));
		entries.put("symbolHg", GT._("Hg"));
		entries.put("symbolCu", GT._("Cu"));
		entries.put("symbolNi", GT._("Ni"));
		entries.put("symbolZn", GT._("Zn"));
		entries.put("symbolSn", GT._("Sn"));
		entries.put("symbolK", GT._("K"));
		entries.put("symbolRb", GT._("Rb"));
		entries.put("symbolCs", GT._("Cs"));
		entries.put("symbolFr", GT._("Fr"));
		entries.put("symbolCa", GT._("Ca"));
		entries.put("symbolSr", GT._("Sr"));
		entries.put("symbolBa", GT._("Ba"));
		entries.put("symbolRa", GT._("Ra"));
		entries.put("symbolSc", GT._("Sc"));
		entries.put("symbolTi", GT._("Ti"));
		entries.put("symbolV", GT._("V"));
		entries.put("symbolCr", GT._("Cr"));
		entries.put("symbolMn", GT._("Mn"));
		entries.put("symbolY", GT._("Y"));
		entries.put("symbolZr", GT._("Zr"));
		entries.put("symbolNb", GT._("Nb"));
		entries.put("symbolMo", GT._("Mo"));
		entries.put("symbolTc", GT._("Tc"));
		entries.put("symbolRu", GT._("Ru"));
		entries.put("symbolRh", GT._("Rh"));
		entries.put("symbolPd", GT._("Pd"));
		entries.put("symbolCd", GT._("Cd"));
		entries.put("symbolHf", GT._("Hf"));
		entries.put("symbolTa", GT._("Ta"));
		entries.put("symbolW", GT._("W"));
		entries.put("symbolRe", GT._("Re"));
		entries.put("symbolOs", GT._("Os"));
		entries.put("symbolIr", GT._("Ir"));
		entries.put("symbolRf", GT._("Rf"));
		entries.put("symbolDb", GT._("Db"));
		entries.put("symbolSg", GT._("Sg"));
		entries.put("symbolBh", GT._("Bh"));
		entries.put("symbolHs", GT._("Hs"));
		entries.put("symbolMt", GT._("Mt"));
		entries.put("symbolDs", GT._("Ds"));
		entries.put("symbolRg", GT._("Rg"));
		entries.put("symbolGa", GT._("Ga"));
		entries.put("symbolIn", GT._("In"));
		entries.put("symbolTl", GT._("Tl"));
		entries.put("symbolPb", GT._("Pb"));
		entries.put("symbolBi", GT._("Bi"));
		entries.put("symbolGe", GT._("Ge"));
		entries.put("symbolAs", GT._("As"));
		entries.put("symbolSb", GT._("Sb"));
		entries.put("symbolTe", GT._("Te"));
		entries.put("symbolPo", GT._("Po"));
		entries.put("pseudoStar", GT._("Variable Attachment Point *"));
		entries.put("pseudoR", GT._("R"));	
		entries.put("pseudoRX", GT._("R.."));	
		entries.put("pseudoR1", GT._("R1"));	
		entries.put("pseudoR2", GT._("R2"));	
		entries.put("pseudoR3", GT._("R3"));	
		entries.put("pseudoR4", GT._("R4"));	
		entries.put("bondTooltip", GT._("Draw Bonds and Atoms"));
		entries.put("cyclesymbolTooltip", GT._("Change the Atom's Symbol"));
		entries.put("periodictableTooltip", GT._("Select new drawing symbol from periodic table"));
		entries.put("enterelementTooltip", GT._("Enter an element symbol via keyboard"));
		entries.put("up_bondTooltip", GT._("Make the Bonds Stereo Up"));
		entries.put("down_bondTooltip", GT._("Make the Bonds Stereo Down"));
		entries.put("plusTooltip", GT._("Increase the charge on an Atom"));
		entries.put("minusTooltip", GT._("Decrease the charge on an Atom"));
		entries.put("eraserTooltip", GT._("Delete Atoms and Bonds"));
		entries.put("lassoTooltip", GT._("Select Atoms and Bonds in a free-form region"));
		entries.put("selectTooltip", GT._("Select Atoms and Bonds in a rectangular region"));
		entries.put("triangleTooltip", GT._("Add a propane ring"));
		entries.put("squareTooltip", GT._("Add a butane ring"));
		entries.put("pentagonTooltip", GT._("Add a pentane ring"));
		entries.put("hexagonTooltip", GT._("Add a hexane ring"));
		entries.put("heptagonTooltip", GT._("Add a heptane ring"));
		entries.put("octagonTooltip", GT._("Add a octane ring"));
		entries.put("benzeneTooltip", GT._("Add a benzene ring"));
		entries.put("cleanupTooltip", GT._("Relayout the structures"));
		if(guistring.equals(JChemPaintEditorApplet.GUI_APPLET))
			entries.put("newTooltip", GT._("Clear"));
		else
			entries.put("newTooltip", GT._("Create a new file"));
		entries.put("openTooltip", GT._("Open a file"));
		entries.put("redoTooltip", GT._("Redo Action"));
		entries.put("saveAsTooltip", GT._("Save to a file"));
		entries.put("undoTooltip", GT._("Undo Action"));
		entries.put("zoominTooltip", GT._("Zoom in"));
		entries.put("zoomoutTooltip", GT._("Zoom out"));
		entries.put("undefined_bondTooltip", GT._("Stereo up or stereo down bond"));
		entries.put("undefined_stereo_bondTooltip", GT._("Any stereo bond"));
		entries.put("rotateTooltip", GT._("Rotate selection"));
		entries.put("cutTooltip", GT._("Cut selection"));
		entries.put("copyTooltip", GT._("Copy selection to clipboard"));
		entries.put("pasteTooltip", GT._("Paste from clipboard"));
		entries.put("flipVerticalTooltip", GT._("Flip vertical"));
		entries.put("flipHorizontalTooltip", GT._("Flip horizontal"));
		entries.put("pasteTemplateTooltip", GT._("Choose from complex templates"));
		entries.put("bondMenuTitle", GT._("Bond Popup Menu"));
		entries.put("chemmodelMenuTitle", GT._("ChemModel Popup Menu"));
		entries.put("Enter Element or Group", GT._("Enter Element or Group"));
		entries.put("Add Atom Or Change Element", GT._("Add Atom Or Change Element"));
		entries.put("Draw Bond", GT._("Draw Bond"));
		entries.put("Ring 3", GT._("Ring {0}","3"));
		entries.put("Ring 4", GT._("Ring {0}","4"));
		entries.put("Ring 5", GT._("Ring {0}","5"));
		entries.put("Ring 6", GT._("Ring {0}","6"));
		entries.put("Ring 7", GT._("Ring {0}","7"));
		entries.put("Ring 8", GT._("Ring {0}","8"));
		entries.put("Add or convert to bond up", GT._("Add or convert to bond up"));
		entries.put("Add or convert to bond down", GT._("Add or convert to bond down"));
		entries.put("Decrease Charge", GT._("Decrease Charge"));
		entries.put("Increase Charge", GT._("Increase Charge"));
		entries.put("Cycle Symbol", GT._("Cyclic change of symbol"));
		entries.put("Delete", GT._("Delete"));
		entries.put("Benzene", GT._("Benzene"));
		entries.put("Select in Free Form", GT._("Select in Free Form"));
		entries.put("Select Square", GT._("Select Rectangle"));
                entries.put("CTooltip", GT._("Change drawing symbol to C"));
                entries.put("HTooltip", GT._("Change drawing symbol to H"));
                entries.put("OTooltip", GT._("Change drawing symbol to O"));
                entries.put("NTooltip", GT._("Change drawing symbol to N"));
                entries.put("PTooltip", GT._("Change drawing symbol to P"));
                entries.put("STooltip", GT._("Change drawing symbol to S"));
                entries.put("FTooltip", GT._("Change drawing symbol to F"));
                entries.put("FTooltip", GT._("Change drawing symbol to Cl"));
                entries.put("BrTooltip", GT._("Change drawing symbol to Br"));
                entries.put("ITooltip", GT._("Change drawing symbol to I"));

}
	
	/**
	 * Gives the text for an item.
	 * 
	 * @param key The key for the text
	 * @return The text in current language
	 */
	public String getText(String key){
		if(entries.get(key)==null)
			return key;
		else
			return entries.get(key);
	}
	
	/**
	 * Gives an instance of JCPMenuTextMaker.
	 * 
	 * @return The instance
	 */
	public static JCPMenuTextMaker getInstance(String guistring){
		if(instance==null){
			instance=new JCPMenuTextMaker(guistring);
		}
		return instance;
	}
}
