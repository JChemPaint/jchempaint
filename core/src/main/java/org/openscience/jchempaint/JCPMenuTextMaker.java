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
    protected Map<String,String> entries=new HashMap<String,String>();

    /**
     * The constructor. Protected since class is a singleton.
     */
    protected JCPMenuTextMaker(String guistring){
        init(guistring);
    }

    public void init(String guistring){
        entries.clear();
        entries.put("file", GT.get("File"));
        entries.put("new", GT.get("New"));
        entries.put("atomMenuTitle", GT.get("Atom Popup Menu"));
        entries.put("pseudoMenuTitle", GT.get("Pseudo Atom Popup Menu"));
        entries.put("open", GT.get("Open"));
        entries.put("saveAs", GT.get("Save As..."));
        entries.put("view", GT.get("View"));
        entries.put("print", GT.get("Print..."));
        entries.put("export", GT.get("Save As Image..."));
        entries.put("save", GT.get("Save"));
        entries.put("edit", GT.get("Edit"));
        entries.put("report", GT.get("Report"));
        entries.put("close", GT.get("Close"));
        entries.put("exit", GT.get("Exit"));
        entries.put("undo", GT.get("Undo"));
        entries.put("redo", GT.get("Redo"));
        entries.put("selectAll", GT.get("Select All"));
        entries.put("copy", GT.get("Copy"));
        entries.put("copyAsSmiles", GT.get("Copy As SMILES"));
        entries.put("copyAsMolfile", GT.get("Copy As MOLfile"));
        entries.put("eraser", GT.get("Delete"));
        entries.put("paste", GT.get("Paste"));
        entries.put("pasteTemplate", GT.get("All Templates"));
        entries.put("cut", GT.get("Cut"));
        entries.put("atomMenu", GT.get("Atom"));
        entries.put("bondMenu", GT.get("Bond"));
        entries.put("tools", GT.get("Tools"));
        entries.put("templates", GT.get("Templates"));
        entries.put("radical", GT.get("Radical"));
        entries.put("bond", GT.get("Single"));
        entries.put("double_bond", GT.get("Double"));
        entries.put("triple_bond", GT.get("Triple"));
        entries.put("quad_bond", GT.get("Quadruple"));
        entries.put("down_bond", GT.get("Stereo Down"));
        entries.put("up_bond", GT.get("Stereo Up"));
        entries.put("undefined_bond", GT.get("Undefined Stereo"));
        entries.put("undefined_stereo_bond", GT.get("Undefined E/Z"));
        entries.put("hollow_wedge_bond", GT.get("Hollow Wedge"));
        entries.put("bold_bond", GT.get("Bold Bond"));
        entries.put("hash_bond", GT.get("Hashed Bond"));
        entries.put("dash_bond", GT.get("Dashed Bond"));
        entries.put("arom_bond", GT.get("Aromatic Bond"));
        entries.put("coordination_bond", GT.get("Coordination Bond"));
        entries.put("formalCharge", GT.get("Charge"));
        entries.put("plus", GT.get("Plus"));
        entries.put("minus", GT.get("Minus"));
        entries.put("hydrogen", GT.get("Implicit Hydrogens"));
        entries.put("flip", GT.get("Flip"));
        entries.put("cleanup", GT.get("Clean Structure"));
        entries.put("toolbar", GT.get("Toolbar"));
        entries.put("menubar", GT.get("Menubar"));
        entries.put("insertstructure", GT.get("Direct Entry as SMILES/InChI/CAS"));
        entries.put("zoomin", GT.get("Zoom In"));
        entries.put("zoomout", GT.get("Zoom Out"));
        entries.put("zoomoriginal", GT.get("Zoom 100%"));
        entries.put("options", GT.get("Preferences..."));
        entries.put("createSMILES", GT.get("Create SMILES"));
        entries.put("createInChI", GT.get("Create InChI"));
        entries.put("help", GT.get("Help"));
        entries.put("tutorial", GT.get("Basic Tutorial"));
        entries.put("rgpTutorial", GT.get("R-group Tutorial"));
        entries.put("feedback", GT.get("Report Feedback"));
        entries.put("license", GT.get("License"));
        entries.put("about", GT.get("About"));
        entries.put("hydroon", GT.get("On All"));
        entries.put("hydrooff", GT.get("Off"));
        entries.put("flipHorizontal", GT.get("Horizontal"));
        entries.put("flipVertical", GT.get("Vertical"));
        entries.put("selectFromChemObject", GT.get("Select"));
        entries.put("symbolChange", GT.get("Change Element"));
        entries.put("periodictable", GT.get("Periodic Table"));
        entries.put("enterelement", GT.get("Custom"));
        entries.put("isotopeChange", GT.get("Isotopes"));
        entries.put("convertToRadical", GT.get("Add Single Electron"));
        entries.put("Add Single Electron", GT.get("Add Single Electron"));
        entries.put("convertFromRadical", GT.get("Remove Single Electron"));
        entries.put("Remove Single Electron", GT.get("Remove Single Electron"));
        entries.put("showChemObjectProperties", GT.get("Properties"));
        entries.put("showACProperties", GT.get("Molecule Properties"));
        entries.put("makeNormal", GT.get("Convert to Regular Atom"));
        entries.put("commonSymbols", GT.get("Common Elements"));
        entries.put("halogenSymbols", GT.get("Halogens"));
        entries.put("nobelSymbols", GT.get("Nobel Gases"));
        entries.put("alkaliMetals", GT.get("Alkali Metals"));
        entries.put("alkaliEarthMetals", GT.get("Alkali Earth Metals"));
        entries.put("transitionMetals", GT.get("Transition Metals"));
        entries.put("metals", GT.get("Metals"));
        entries.put("metalloids", GT.get("Metalloids"));
        entries.put("Transition m", GT.get("Non-Metals"));
        entries.put("pseudoSymbols", GT.get("R Group"));
        entries.put("attachmentPoints", GT.get("Attachment Point"));
        entries.put("majorPlusThree", GT.get("Major Plus {0}", "3"));
        entries.put("majorPlusTwo", GT.get("Major Plus {0}", "2"));
        entries.put("majorPlusOne", GT.get("Major Plus {0}", "1"));
        entries.put("major", GT.get("Major Isotope"));
        entries.put("majorMinusOne", GT.get("Major Minus {0}", "1"));
        entries.put("majorMinusTwo", GT.get("Major Minus {0}", "2"));
        entries.put("majorMinusThree", GT.get("Major Minus {0}", "3"));
        entries.put("valence", GT.get("Valence"));
        entries.put("valenceOff", GT.get("Valence Off"));
        entries.put("valence1", GT.get("Valence {0}", "1"));
        entries.put("valence2", GT.get("Valence {0}", "2"));
        entries.put("valence3", GT.get("Valence {0}", "3"));
        entries.put("valence4", GT.get("Valence {0}", "4"));
        entries.put("valence5", GT.get("Valence {0}", "5"));
        entries.put("valence6", GT.get("Valence {0}", "6"));
        entries.put("valence7", GT.get("Valence {0}", "7"));		
        entries.put("valence8", GT.get("Valence {0}", "8"));
        entries.put("symbolC", GT.get("C"));
        entries.put("symbolO", GT.get("O"));
        entries.put("symbolN", GT.get("N"));
        entries.put("symbolH", GT.get("H"));
        entries.put("symbolP", GT.get("P"));
        entries.put("symbolS", GT.get("S"));
        entries.put("symbolF", GT.get("F"));
        entries.put("symbolCl", GT.get("Cl"));
        entries.put("symbolBr", GT.get("Br"));
        entries.put("symbolI", GT.get("I"));
        entries.put("symbolHe", GT.get("He"));
        entries.put("symbolNe", GT.get("Ne"));
        entries.put("symbolAr", GT.get("Ar"));
        entries.put("symbolB", GT.get("B"));
        entries.put("symbolP", GT.get("P"));
        entries.put("symbolLi", GT.get("Li"));
        entries.put("symbolBe", GT.get("Be"));
        entries.put("symbolNa", GT.get("Na"));
        entries.put("symbolMg", GT.get("Mg"));
        entries.put("symbolAl", GT.get("Al"));
        entries.put("symbolSi", GT.get("Si"));
        entries.put("symbolFe", GT.get("Fe"));
        entries.put("symbolCo", GT.get("Co"));
        entries.put("symbolAg", GT.get("Ag"));
        entries.put("symbolPt", GT.get("Pt"));
        entries.put("symbolAu", GT.get("Au"));
        entries.put("symbolHg", GT.get("Hg"));
        entries.put("symbolCu", GT.get("Cu"));
        entries.put("symbolNi", GT.get("Ni"));
        entries.put("symbolZn", GT.get("Zn"));
        entries.put("symbolSn", GT.get("Sn"));
        entries.put("symbolK", GT.get("K"));
        entries.put("symbolRb", GT.get("Rb"));
        entries.put("symbolCs", GT.get("Cs"));
        entries.put("symbolFr", GT.get("Fr"));
        entries.put("symbolCa", GT.get("Ca"));
        entries.put("symbolSr", GT.get("Sr"));
        entries.put("symbolBa", GT.get("Ba"));
        entries.put("symbolRa", GT.get("Ra"));
        entries.put("symbolSc", GT.get("Sc"));
        entries.put("symbolTi", GT.get("Ti"));
        entries.put("symbolV", GT.get("V"));
        entries.put("symbolCr", GT.get("Cr"));
        entries.put("symbolMn", GT.get("Mn"));
        entries.put("symbolY", GT.get("Y"));
        entries.put("symbolZr", GT.get("Zr"));
        entries.put("symbolNb", GT.get("Nb"));
        entries.put("symbolMo", GT.get("Mo"));
        entries.put("symbolTc", GT.get("Tc"));
        entries.put("symbolRu", GT.get("Ru"));
        entries.put("symbolRh", GT.get("Rh"));
        entries.put("symbolPd", GT.get("Pd"));
        entries.put("symbolCd", GT.get("Cd"));
        entries.put("symbolHf", GT.get("Hf"));
        entries.put("symbolTa", GT.get("Ta"));
        entries.put("symbolW", GT.get("W"));
        entries.put("symbolRe", GT.get("Re"));
        entries.put("symbolOs", GT.get("Os"));
        entries.put("symbolIr", GT.get("Ir"));
        entries.put("symbolRf", GT.get("Rf"));
        entries.put("symbolDb", GT.get("Db"));
        entries.put("symbolSg", GT.get("Sg"));
        entries.put("symbolBh", GT.get("Bh"));
        entries.put("symbolHs", GT.get("Hs"));
        entries.put("symbolMt", GT.get("Mt"));
        entries.put("symbolDs", GT.get("Ds"));
        entries.put("symbolRg", GT.get("Rg"));
        entries.put("symbolGa", GT.get("Ga"));
        entries.put("symbolIn", GT.get("In"));
        entries.put("symbolTl", GT.get("Tl"));
        entries.put("symbolPb", GT.get("Pb"));
        entries.put("symbolBi", GT.get("Bi"));
        entries.put("symbolGe", GT.get("Ge"));
        entries.put("symbolAs", GT.get("As"));
        entries.put("symbolSb", GT.get("Sb"));
        entries.put("symbolTe", GT.get("Te"));
        entries.put("symbolPo", GT.get("Po"));
        entries.put("pseudoStar", GT.get("Variable Attachment Point *"));
        entries.put("pseudoR", GT.get("R"));	
        entries.put("pseudoRX", GT.get("Other..."));	
        entries.put("pseudoR1", GT.get("R1"));	
        entries.put("pseudoR2", GT.get("R2"));	
        entries.put("pseudoR3", GT.get("R3"));	
        entries.put("pseudoR4", GT.get("R4"));
        entries.put("pseudoAP1", GT.get("1"));
        entries.put("pseudoAP2", GT.get("2"));
        entries.put("pseudoAP3", GT.get("3"));
        entries.put("bondToolTooltip", GT.get("Draw Bonds and Atoms"));
        entries.put("reactionArrowTooltip", GT.get("Makes a Reaction by Drawing a Reaction Arrow"));
        entries.put("double_bondToolTooltip", GT.get("Draw Double Bonds"));
        entries.put("triple_bondToolTooltip", GT.get("Draw Triple Bonds"));
        entries.put("cyclesymbolTooltip", GT.get("Change the Atom's Symbol"));
        entries.put("periodictableTooltip", GT.get("Select new drawing symbol from periodic table"));
        entries.put("enterelementTooltip", GT.get("Enter an element symbol via keyboard"));
        entries.put("up_bondTooltip", GT.get("Make the Bonds Stereo Up"));
        entries.put("hollow_wedge_bondTooltip", GT.get("Make/add a hollow Wedge"));
        entries.put("bold_bondTooltip", GT.get("Make a bold bond"));
        entries.put("hash_bondTooltip", GT.get("Make a hashed bond"));
        entries.put("dash_bondTooltip", GT.get("Make a dashed bond"));
        entries.put("arom_bondTooltip", GT.get("Make a aromatic bond"));
        entries.put("coordination_bondTooltip", GT.get("Make a coordination bond"));
        entries.put("chainTooltip", GT.get("Draw a chain"));
        entries.put("down_bondTooltip", GT.get("Make the Bonds Stereo Down"));
        entries.put("plusTooltip", GT.get("Increase the charge on an Atom"));
        entries.put("minusTooltip", GT.get("Decrease the charge on an Atom"));
        entries.put("eraserTooltip", GT.get("Delete atoms and bonds"));
        entries.put("lassoTooltip", GT.get("Select atoms and bonds in a free-form region"));
        entries.put("selectTooltip", GT.get("Select objects in a rectangle / move objects"));
        entries.put("triangleTooltip", GT.get("Add a propane ring"));
        entries.put("squareTooltip", GT.get("Add a butane ring"));
        entries.put("pentagonTooltip", GT.get("Add a pentane ring"));
        entries.put("hexagonTooltip", GT.get("Add a hexane ring"));
        entries.put("heptagonTooltip", GT.get("Add a heptane ring"));
        entries.put("octagonTooltip", GT.get("Add a octane ring"));
        entries.put("cyclopentadieneTooltip", GT.get("Add a cyclopentadiene ring"));
        entries.put("chairleftTooltip", GT.get("Add a left handed cyclohexane chair"));
        entries.put("chairrightTooltip", GT.get("Add a right handed cyclohexane chair"));
        entries.put("benzeneTooltip", GT.get("Add a benzene ring"));
        entries.put("cleanupTooltip", GT.get("Relayout the structures"));
        if(guistring.equals(JChemPaintEditorApplet.GUI_APPLET))
            entries.put("newTooltip", GT.get("Clear"));
        else
            entries.put("newTooltip", GT.get("Create new file"));
        entries.put("openTooltip", GT.get("Open existing file"));
        entries.put("saveTooltip", GT.get("Save current file"));
        entries.put("printTooltip", GT.get("Print current file"));
        entries.put("redoTooltip", GT.get("Redo Action"));
        entries.put("saveAsTooltip", GT.get("Save to a file"));
        entries.put("undoTooltip", GT.get("Undo Action"));
        entries.put("zoominTooltip", GT.get("Zoom in"));
        entries.put("zoomoutTooltip", GT.get("Zoom out"));
        entries.put("undefined_bondTooltip", GT.get("Stereo up or stereo down bond"));
        entries.put("undefined_stereo_bondTooltip", GT.get("Any stereo bond"));
        entries.put("rotateTooltip", GT.get("Rotate selection"));
        entries.put("rotate3dTooltip", GT.get("Rotate selection in space"));
        entries.put("cutTooltip", GT.get("Cut selection"));
        entries.put("copyTooltip", GT.get("Copy selection to clipboard"));
        entries.put("pasteTooltip", GT.get("Paste from clipboard"));
        entries.put("flipVerticalTooltip", GT.get("Flip vertical"));
        entries.put("flipHorizontalTooltip", GT.get("Flip horizontal"));
        entries.put("pasteTemplateTooltip", GT.get("Choose from complex templates"));
        entries.put("bondMenuTitle", GT.get("Bond Popup Menu"));
        entries.put("chemmodelMenuTitle", GT.get("ChemModel Popup Menu"));
        entries.put("Enter Element or Group", GT.get("Enter Element or Group"));
        entries.put("Add Atom Or Change Element", GT.get("Add Atom Or Change Element"));
        entries.put("Draw Bond", GT.get("Draw Bond"));
        entries.put("Draw Double Bond", GT.get("Draw Double Bond"));
        entries.put("Draw Triple Bond", GT.get("Draw Triple Bond"));
        entries.put("Draw Quadruple Bond", GT.get("Draw Quadruple Bond"));
        entries.put("Ring 3", GT.get("Ring {0}", "3"));
        entries.put("Ring 4", GT.get("Ring {0}", "4"));
        entries.put("Ring 5", GT.get("Ring {0}", "5"));
        entries.put("Ring 6", GT.get("Ring {0}", "6"));
        entries.put("Ring 7", GT.get("Ring {0}", "7"));
        entries.put("Ring 8", GT.get("Ring {0}", "8"));
        entries.put("Add or convert to bond up", GT.get("Add or convert to bond up"));
        entries.put("Add or convert to bond down", GT.get("Add or convert to bond down"));
        entries.put("Decrease Charge", GT.get("Decrease Charge"));
        entries.put("Increase Charge", GT.get("Increase Charge"));
        entries.put("Cycle Symbol", GT.get("Cyclic change of symbol"));
        entries.put("Delete", GT.get("Delete"));
        entries.put("Move", GT.get("Move"));
        entries.put("Rotate", GT.get("Rotate"));
        entries.put("Rotate in space", GT.get("Rotate in space"));
        entries.put("Benzene", GT.get("Benzene"));
        entries.put("Select in Free Form", GT.get("Select in Free Form"));
        entries.put("Select Square", GT.get("Select / Move"));
        entries.put("CTooltip", GT.get("Change drawing symbol to {0}", "C"));
        entries.put("HTooltip", GT.get("Change drawing symbol to {0}", "H"));
        entries.put("OTooltip", GT.get("Change drawing symbol to {0}", "O"));
        entries.put("NTooltip", GT.get("Change drawing symbol to {0}", "N"));
        entries.put("PTooltip", GT.get("Change drawing symbol to {0}", "P"));
        entries.put("STooltip", GT.get("Change drawing symbol to {0}", "S"));
        entries.put("FTooltip", GT.get("Change drawing symbol to {0}", "F"));
        entries.put("ClTooltip", GT.get("Change drawing symbol to {0}", "Cl"));
        entries.put("BrTooltip", GT.get("Change drawing symbol to {0}", "Br"));
        entries.put("ITooltip", GT.get("Change drawing symbol to {0}", "I"));
        entries.put("enterRTooltip", GT.get("Draw any functional group"));
        entries.put("reaction", GT.get("Reaction"));
        entries.put("addReactantToNewReaction", GT.get("Make Reactant in New Reaction"));
        entries.put("addReactantToExistingReaction", GT.get("Make Reactant in Existing Reaction"));
        entries.put("addProductToNewReaction", GT.get("Make Product in New Reaction"));
        entries.put("addProductToExistingReaction", GT.get("Make Product in Existing Reaction"));
        entries.put("selectReactants", GT.get("Select Reactants"));
        entries.put("selectProducts", GT.get("Select Products"));
        entries.put("reactionMenuTitle", GT.get("Reaction Popup Menu"));
        entries.put("alkaloids", GT.get("Alkaloids"));
        entries.put("amino_acids", GT.get("Amino Acids"));
        entries.put("beta_lactams", GT.get("Beta Lactams"));
        entries.put("carbohydrates", GT.get("Carbohydrates"));
        entries.put("inositols", GT.get("Inositols"));
        entries.put("lipids", GT.get("Lipids"));
        entries.put("miscellaneous", GT.get("Miscellaneous"));
        entries.put("nucleosides", GT.get("Nucleosides"));
        entries.put("porphyrins", GT.get("Porphyrins"));
        entries.put("protecting_groups", GT.get("Protecting Groups"));
        entries.put("steroids", GT.get("Steroids"));
        entries.put("pahs", GT.get("PAHs"));
        entries.put("language", GT.get("Language"));
        entries.put("rgroup", GT.get("R-groups"));
        entries.put("rgroupMenu", GT.get("R-groups"));
        entries.put("rgroupAtomMenu", GT.get("R-group attachment"));
        entries.put("rgroupBondMenu", GT.get("R-group attachment"));
        entries.put("setRoot", GT.get("Define as Root Structure"));
        entries.put("setSubstitute", GT.get("Define as Substituent"));
        entries.put("setAtomApoAction1", GT.get("Set as first attachment point"));
        entries.put("setAtomApoAction2", GT.get("Set as second attachment point"));
        entries.put("setBondApoAction1", GT.get("Set as bond for first attachment point"));
        entries.put("setBondApoAction2", GT.get("Set as bond for second attachment point"));
        entries.put("rgpAdvanced", GT.get("Advanced R-group logic"));
        entries.put("rgpGenerate", GT.get("Generate possible configurations (sdf) "));
        entries.put("rgpGenerateSmi", GT.get("Generate possible configurations (SMILES) "));
        entries.put("clearRgroup", GT.get("Clear R-Group"));
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
