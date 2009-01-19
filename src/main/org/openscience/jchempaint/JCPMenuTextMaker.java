package org.openscience.jchempaint;

import java.util.HashMap;
import java.util.Map;

public class JCPMenuTextMaker {
	private static JCPMenuTextMaker instance=null;
	private Map<String,String> entries=new HashMap<String,String>();
	
	public JCPMenuTextMaker(){
		entries.put("file", GT._("file"));
		entries.put("new", GT._("new"));
		entries.put("atomMenuTitle", GT._("atomMenuTitle"));
		entries.put("open", GT._("open"));
		entries.put("saveAs", GT._("saveAs"));
		entries.put("view", GT._("view"));
		entries.put("print", GT._("print"));
		entries.put("save", GT._("save"));
		entries.put("edit", GT._("edit"));
		entries.put("report", GT._("report"));
		entries.put("close", GT._("close"));
		entries.put("exit", GT._("exit"));
		entries.put("undo", GT._("undo"));
		entries.put("redo", GT._("redo"));
		entries.put("selectAll", GT._("selectAll"));
		entries.put("copy", GT._("copy"));
		entries.put("paste", GT._("paste"));
		entries.put("cutSelected", GT._("cutSelected"));
		entries.put("hydrogen", GT._("hydrogen"));
		entries.put("adjustBondOrders", GT._("adjustBondOrders"));
		entries.put("resetBondOrders", GT._("resetBondOrders"));
		entries.put("flip", GT._("flip"));
		entries.put("cleanup", GT._("cleanup"));
		entries.put("preferences", GT._("preferences"));
		entries.put("toolbar", GT._("toolbar"));
		entries.put("statusbar", GT._("statusbar"));
		entries.put("menubar", GT._("menubar"));
		entries.put("insertstructure", GT._("insertstructure"));
		entries.put("zoomin", GT._("zoomin"));
		entries.put("zoomout", GT._("zoomout"));
		entries.put("zoomoriginal", GT._("zoomoriginal"));
		entries.put("renderOptions", GT._("renderOptions"));
		entries.put("validate", GT._("validate"));
		entries.put("createSMILES", GT._("createSMILES"));
		entries.put("createInChI", GT._("createInChI"));
		entries.put("help", GT._("help"));
		entries.put("tutorial", GT._("tutorial"));
		entries.put("license", GT._("license"));
		entries.put("about", GT._("about"));
		entries.put("addImplHydrogen", GT._("addImplHydrogen"));
		entries.put("makeHydrogenExplicit", GT._("makeHydrogenExplicit"));
		entries.put("makeHydrogenImplicit", GT._("makeHydrogenImplicit"));
		entries.put("editPreferences", GT._("editPreferences"));
		entries.put("reloadPreferences", GT._("reloadPreferences"));
		entries.put("savePreferences", GT._("savePreferences"));
		entries.put("runValidate", GT._("runValidate"));
		entries.put("clearValidate", GT._("clearValidate"));
		entries.put("flipHorizontal", GT._("flipHorizontal"));
		entries.put("flipVertical", GT._("flipVertical"));
		entries.put("selectFromChemObject", GT._("selectFromChemObject"));
		entries.put("selectMolecule", GT._("selectMolecule"));
		entries.put("symbolChange", GT._("symbolChange"));
		entries.put("isotopeChange", GT._("isotopeChange"));
		entries.put("convertToRadical", GT._("convertToRadical"));
		entries.put("reaction", GT._("reaction"));
		entries.put("showChemObjectProperties", GT._("showChemObjectProperties"));
		entries.put("showACProperties", GT._("showACProperties"));
		entries.put("makeNormal", GT._("makeNormal"));
		entries.put("addReactantToNewReaction", GT._("addReactantToNewReaction"));
		entries.put("addReactantToExistingReaction", GT._("addReactantToExistingReaction"));
		entries.put("addProductToNewReaction", GT._("addProductToNewReaction"));
		entries.put("addProductToExistingReaction", GT._("addProductToExistingReaction"));
		entries.put("commonSymbols", GT._("commonSymbols"));
		entries.put("halogenSymbols", GT._("halogenSymbols"));
		entries.put("nobelSymbols", GT._("nobelSymbols"));
		entries.put("nmrNuclei", GT._("nmrNuclei"));
		entries.put("otherSymbols", GT._("otherSymbols"));
		entries.put("pseudoSymbols", GT._("pseudoSymbols"));
		entries.put("majorPlusThree", GT._("majorPlusThree"));
		entries.put("majorPlusTwo", GT._("majorPlusTwo"));
		entries.put("majorPlusOne", GT._("majorPlusOne"));
		entries.put("major", GT._("major"));
		entries.put("majorMinusOne", GT._("majorMinusOne"));
		entries.put("majorMinusTwo", GT._("majorMinusTwo"));
		entries.put("majorMinusThree", GT._("majorMinusThree"));
		entries.put("symbolC", GT._("symbolC"));
		entries.put("symbolO", GT._("symbolO"));
		entries.put("symbolN", GT._("symbolN"));
		entries.put("symbolH", GT._("symbolH"));
		entries.put("symbolP", GT._("symbolP"));
		entries.put("symbolS", GT._("symbolS"));
		entries.put("symbolF", GT._("symbolF"));
		entries.put("symbolCl", GT._("symbolCl"));
		entries.put("symbolBr", GT._("symbolBr"));
		entries.put("symbolI", GT._("symbolI"));
		entries.put("symbolHe", GT._("symbolHe"));
		entries.put("symbolNe", GT._("symbolNe"));
		entries.put("symbolAr", GT._("symbolAr"));
		entries.put("symbolB", GT._("symbolB"));
		entries.put("symbolP", GT._("symbolP"));
		entries.put("symbolLi", GT._("symbolLi"));
		entries.put("symbolBe", GT._("symbolBe"));
		entries.put("symbolNa", GT._("symbolNa"));
		entries.put("symbolMg", GT._("symbolMg"));
		entries.put("symbolAl", GT._("symbolAl"));
		entries.put("symbolSi", GT._("symbolSi"));
		entries.put("symbolFe", GT._("symbolFe"));
		entries.put("symbolCo", GT._("symbolCo"));
		entries.put("symbolAg", GT._("symbolAg"));
		entries.put("symbolPt", GT._("symbolPt"));
		entries.put("symbolAu", GT._("symbolAu"));
		entries.put("symbolHg", GT._("symbolHg"));
		entries.put("symbolCu", GT._("symbolCu"));
		entries.put("symbolNi", GT._("symbolNi"));
		entries.put("symbolZn", GT._("symbolZn"));
		entries.put("symbolSn", GT._("symbolSn"));
		entries.put("pseudoStar", GT._("pseudoStar"));
		entries.put("pseudoR", GT._("pseudoR"));
		entries.put("bond", GT._("bond"));
		entries.put("cyclesymbol", GT._("cyclesymbol"));
		entries.put("periodictable", GT._("periodictable"));
		entries.put("enterelement", GT._("enterelement"));
		entries.put("up_bond", GT._("up_bond"));
		entries.put("down_bond", GT._("down_bond"));
		entries.put("plus", GT._("plus"));
		entries.put("minus", GT._("minus"));
		entries.put("move", GT._("move"));
		entries.put("eraser", GT._("eraser"));
		entries.put("lasso", GT._("lasso"));
		entries.put("select", GT._("select"));
		entries.put("triangle", GT._("triangle"));
		entries.put("square", GT._("square"));
		entries.put("pentagon", GT._("pentagon"));
		entries.put("hexagon", GT._("hexagon"));
		entries.put("heptagon", GT._("heptagon"));
		entries.put("octagon", GT._("octagon"));
		entries.put("benzene", GT._("benzene"));
	}
	
	public String getText(String key){
		return entries.get(key);
	}
	
	public static JCPMenuTextMaker getInstance(){
		if(instance==null)
			instance=new JCPMenuTextMaker();
		return instance;
	}
}
