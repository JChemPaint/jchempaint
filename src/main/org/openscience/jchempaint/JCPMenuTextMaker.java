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
		entries.put("export", GT._("Export..."));
		entries.put("save", GT._("Save"));
		entries.put("edit", GT._("Edit"));
		entries.put("report", GT._("Report"));
		entries.put("close", GT._("Close"));
		entries.put("exit", GT._("Exit"));
		entries.put("undo", GT._("Undo"));
		entries.put("redo", GT._("Redo"));
		entries.put("selectAll", GT._("Select All"));
		entries.put("copy", GT._("Copy"));
		entries.put("paste", GT._("Paste"));
		entries.put("cutSelected", GT._("Cut"));
		entries.put("cut", GT._("Cut"));
		entries.put("hydrogen", GT._("Hydrogens..."));
		entries.put("adjustBondOrders", GT._("Adjust Bond Orders"));
		entries.put("resetBondOrders", GT._("Reset Bond Orders"));
		entries.put("flip", GT._("Flip"));
		entries.put("cleanup", GT._("Clean Up"));
		entries.put("preferences", GT._("Preferences"));
		entries.put("toolbar", GT._("Toolbar"));
		entries.put("statusbar", GT._("Statusbar"));
		entries.put("menubar", GT._("Menubar"));
		entries.put("insertstructure", GT._("Direct Entry as SMILES/InChI/CAS"));
		entries.put("zoomin", GT._("Zoom in"));
		entries.put("zoomout", GT._("Zoom Out"));
		entries.put("zoomoriginal", GT._("Zoom 100%"));
		entries.put("renderOptions", GT._("Rendering Options"));
		entries.put("validate", GT._("Validate"));
		entries.put("createSMILES", GT._("Create SMILES"));
		entries.put("createInChI", GT._("Create InChI"));
		entries.put("help", GT._("Help"));
		entries.put("tutorial", GT._("Tutorial"));
		entries.put("license", GT._("License"));
		entries.put("about", GT._("About"));
		entries.put("addImplHydrogen", GT._("Switch on/off Tracking of Implicit Hydrogens"));
		entries.put("makeHydrogenExplicit", GT._("Make Existing Implicit Hydrogens Explicit"));
		entries.put("makeHydrogenImplicit", GT._("Make Existing Explicit Hydrogens Implicit"));
		entries.put("updateHydrogenImplicit", GT._("Update Implicit Hydrogen Count"));
		entries.put("editPreferences", GT._("Edit..."));
		entries.put("reloadPreferences", GT._("Reload Preferences"));
		entries.put("savePreferences", GT._("Save Preferences"));
		entries.put("runValidate", GT._("Run Validate"));
		entries.put("clearValidate", GT._("Clear Validate Results"));
		entries.put("flipHorizontal", GT._("Horizontal"));
		entries.put("flipVertical", GT._("Vertical"));
		entries.put("selectFromChemObject", GT._("Select"));
		entries.put("selectMolecule", GT._("Select Molecule"));
		entries.put("symbolChange", GT._("Change Element..."));
		entries.put("isotopeChange", GT._("Change Isotope..."));
		entries.put("convertToRadical", GT._("Convert to Radical"));
		entries.put("reaction", GT._("Reaction..."));
		entries.put("showChemObjectProperties", GT._("Properties"));
		entries.put("showACProperties", GT._("Molecule Properties"));
		entries.put("makeNormal", GT._("Convert to Regular Atom"));
		entries.put("addReactantToNewReaction", GT._("Make Reactant in new Reaction"));
		entries.put("addReactantToExistingReaction", GT._("Make Reactant in existing Reaction"));
		entries.put("addProductToNewReaction", GT._("Make Product in new Reaction"));
		entries.put("addProductToExistingReaction", GT._("Make Product in existing Reaction"));
		entries.put("commonSymbols", GT._("Common Elements..."));
		entries.put("halogenSymbols", GT._("Halogens..."));
		entries.put("nobelSymbols", GT._("Nobel Gases..."));
		entries.put("nmrNuclei", GT._("NMR Nuclei..."));
		entries.put("otherSymbols", GT._("Others..."));
		entries.put("pseudoSymbols", GT._("Pseudo Atoms"));
		entries.put("majorPlusThree", GT._("Major Plus Three"));
		entries.put("majorPlusTwo", GT._("Major Plus Two"));
		entries.put("majorPlusOne", GT._("Major Plus One"));
		entries.put("major", GT._("Major Isotope"));
		entries.put("majorMinusOne", GT._("Major Minus One"));
		entries.put("majorMinusTwo", GT._("Major Minus Two"));
		entries.put("majorMinusThree", GT._("Major Minus Three"));
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
		entries.put("pseudoStar", GT._("*"));
		entries.put("pseudoR", GT._("R"));	
		entries.put("selectReactants", GT._("Select Reactants Only"));
		entries.put("bondTooltip", GT._("Draw Bonds and Atoms"));
		entries.put("cyclesymbolTooltip", GT._("Change the Atom's Symbol"));
		entries.put("periodictableTooltip", GT._("Select new drawing symbol from periodic table"));
		entries.put("enterelementTooltip", GT._("Enter an element symbol via keyboard"));
		entries.put("up_bondTooltip", GT._("Make the Bonds Stereo Up"));
		entries.put("down_bondTooltip", GT._("Make the Bonds Stereo Down"));
		entries.put("plusTooltip", GT._("Increase the charge on an Atom"));
		entries.put("minusTooltip", GT._("Decrease the charge on an Atom"));
		entries.put("moveTooltip", GT._("Move Atoms and Bonds"));
		entries.put("eraserTooltip", GT._("Delete Atoms and Bonds"));
		entries.put("lassoTooltip", GT._("Select Atoms and Bonds"));
		entries.put("selectTooltip", GT._("Select Atoms and Bonds in a square region"));
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
		entries.put("bondMenuTitle", GT._("Bond Popup Menu"));
		entries.put("chemmodelMenuTitle", GT._("ChemModel Popup Menu"));
		entries.put("reactionMenuTitle", GT._("Reaction Popup Menu"));
		entries.put("Enter Element or Group", GT._("Enter Element or Group"));
		entries.put("Add Atom Or Change Element", GT._("Add Atom Or Change Element"));
		entries.put("Draw Bond", GT._("Draw Bond"));
		entries.put("Ring 3", GT._("Ring 3"));
		entries.put("Ring 4", GT._("Ring 4"));
		entries.put("Ring 5", GT._("Ring 5"));
		entries.put("Ring 6", GT._("Ring 6"));
		entries.put("Ring 7", GT._("Ring 7"));
		entries.put("Ring 8", GT._("Ring 8"));
		entries.put("Add or convert to bond up", GT._("Add or convert to bond up"));
		entries.put("Add or convert to bond down", GT._("Add or convert to bond down"));
		entries.put("Decrease Charge", GT._("Decrease Charge"));
		entries.put("Increase Charge", GT._("Increase Charge"));
		entries.put("Cycle Symbol", GT._("Cyclic change of symbol"));
		entries.put("Delete", GT._("Delete"));
		entries.put("Benzene", GT._("Benzene"));
		entries.put("Select in Free Form", GT._("Select in Free Form"));
		entries.put("Select Square", GT._("Select Square"));
		entries.put("Move", GT._("Move"));
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
