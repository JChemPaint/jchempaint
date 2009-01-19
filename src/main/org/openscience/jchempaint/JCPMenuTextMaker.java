package org.openscience.jchempaint;

import java.util.HashMap;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class JCPMenuTextMaker {
	private static JCPMenuTextMaker instance=null;
	private I18n i18n = I18nFactory.getI18n(JChemPaintPanel.class, "app.i18n.Messages");
	private Map<String,String> entries=new HashMap<String,String>();
	
	public JCPMenuTextMaker(){
		entries.put("file", i18n.tr("file"));
		entries.put("new", i18n.tr("new"));
		entries.put("atomMenuTitle", i18n.tr("atomMenuTitle"));
		entries.put("open", i18n.tr("open"));
		entries.put("saveAs", i18n.tr("saveAs"));
		entries.put("view", i18n.tr("view"));
		entries.put("print", i18n.tr("print"));
		entries.put("save", i18n.tr("save"));
		entries.put("edit", i18n.tr("edit"));
		entries.put("report", i18n.tr("report"));
		entries.put("close", i18n.tr("close"));
		entries.put("exit", i18n.tr("exit"));
		entries.put("undo", i18n.tr("undo"));
		entries.put("redo", i18n.tr("redo"));
		entries.put("selectAll", i18n.tr("selectAll"));
		entries.put("copy", i18n.tr("copy"));
		entries.put("paste", i18n.tr("paste"));
		entries.put("cutSelected", i18n.tr("cutSelected"));
		entries.put("hydrogen", i18n.tr("hydrogen"));
		entries.put("adjustBondOrders", i18n.tr("adjustBondOrders"));
		entries.put("resetBondOrders", i18n.tr("resetBondOrders"));
		entries.put("flip", i18n.tr("flip"));
		entries.put("cleanup", i18n.tr("cleanup"));
		entries.put("preferences", i18n.tr("preferences"));
		entries.put("toolbar", i18n.tr("toolbar"));
		entries.put("statusbar", i18n.tr("statusbar"));
		entries.put("menubar", i18n.tr("menubar"));
		entries.put("insertstructure", i18n.tr("insertstructure"));
		entries.put("zoomin", i18n.tr("zoomin"));
		entries.put("zoomout", i18n.tr("zoomout"));
		entries.put("zoomoriginal", i18n.tr("zoomoriginal"));
		entries.put("renderOptions", i18n.tr("renderOptions"));
		entries.put("validate", i18n.tr("validate"));
		entries.put("createSMILES", i18n.tr("createSMILES"));
		entries.put("createInChI", i18n.tr("createInChI"));
		entries.put("help", i18n.tr("help"));
		entries.put("tutorial", i18n.tr("tutorial"));
		entries.put("license", i18n.tr("license"));
		entries.put("about", i18n.tr("about"));
		entries.put("addImplHydrogen", i18n.tr("addImplHydrogen"));
		entries.put("makeHydrogenExplicit", i18n.tr("makeHydrogenExplicit"));
		entries.put("makeHydrogenImplicit", i18n.tr("makeHydrogenImplicit"));
		entries.put("editPreferences", i18n.tr("editPreferences"));
		entries.put("reloadPreferences", i18n.tr("reloadPreferences"));
		entries.put("savePreferences", i18n.tr("savePreferences"));
		entries.put("runValidate", i18n.tr("runValidate"));
		entries.put("clearValidate", i18n.tr("clearValidate"));
		entries.put("flipHorizontal", i18n.tr("flipHorizontal"));
		entries.put("flipVertical", i18n.tr("flipVertical"));
		entries.put("selectFromChemObject", i18n.tr("selectFromChemObject"));
		entries.put("selectMolecule", i18n.tr("selectMolecule"));
		entries.put("symbolChange", i18n.tr("symbolChange"));
		entries.put("isotopeChange", i18n.tr("isotopeChange"));
		entries.put("convertToRadical", i18n.tr("convertToRadical"));
		entries.put("reaction", i18n.tr("reaction"));
		entries.put("showChemObjectProperties", i18n.tr("showChemObjectProperties"));
		entries.put("showACProperties", i18n.tr("showACProperties"));
		entries.put("makeNormal", i18n.tr("makeNormal"));
		entries.put("addReactantToNewReaction", i18n.tr("addReactantToNewReaction"));
		entries.put("addReactantToExistingReaction", i18n.tr("addReactantToExistingReaction"));
		entries.put("addProductToNewReaction", i18n.tr("addProductToNewReaction"));
		entries.put("addProductToExistingReaction", i18n.tr("addProductToExistingReaction"));
		entries.put("commonSymbols", i18n.tr("commonSymbols"));
		entries.put("halogenSymbols", i18n.tr("halogenSymbols"));
		entries.put("nobelSymbols", i18n.tr("nobelSymbols"));
		entries.put("nmrNuclei", i18n.tr("nmrNuclei"));
		entries.put("otherSymbols", i18n.tr("otherSymbols"));
		entries.put("pseudoSymbols", i18n.tr("pseudoSymbols"));
		entries.put("majorPlusThree", i18n.tr("majorPlusThree"));
		entries.put("majorPlusTwo", i18n.tr("majorPlusTwo"));
		entries.put("majorPlusOne", i18n.tr("majorPlusOne"));
		entries.put("major", i18n.tr("major"));
		entries.put("majorMinusOne", i18n.tr("majorMinusOne"));
		entries.put("majorMinusTwo", i18n.tr("majorMinusTwo"));
		entries.put("majorMinusThree", i18n.tr("majorMinusThree"));
		entries.put("symbolC", i18n.tr("symbolC"));
		entries.put("symbolO", i18n.tr("symbolO"));
		entries.put("symbolN", i18n.tr("symbolN"));
		entries.put("symbolH", i18n.tr("symbolH"));
		entries.put("symbolP", i18n.tr("symbolP"));
		entries.put("symbolS", i18n.tr("symbolS"));
		entries.put("symbolF", i18n.tr("symbolF"));
		entries.put("symbolCl", i18n.tr("symbolCl"));
		entries.put("symbolBr", i18n.tr("symbolBr"));
		entries.put("symbolI", i18n.tr("symbolI"));
		entries.put("symbolHe", i18n.tr("symbolHe"));
		entries.put("symbolNe", i18n.tr("symbolNe"));
		entries.put("symbolAr", i18n.tr("symbolAr"));
		entries.put("symbolB", i18n.tr("symbolB"));
		entries.put("symbolP", i18n.tr("symbolP"));
		entries.put("symbolLi", i18n.tr("symbolLi"));
		entries.put("symbolBe", i18n.tr("symbolBe"));
		entries.put("symbolNa", i18n.tr("symbolNa"));
		entries.put("symbolMg", i18n.tr("symbolMg"));
		entries.put("symbolAl", i18n.tr("symbolAl"));
		entries.put("symbolSi", i18n.tr("symbolSi"));
		entries.put("symbolFe", i18n.tr("symbolFe"));
		entries.put("symbolCo", i18n.tr("symbolCo"));
		entries.put("symbolAg", i18n.tr("symbolAg"));
		entries.put("symbolPt", i18n.tr("symbolPt"));
		entries.put("symbolAu", i18n.tr("symbolAu"));
		entries.put("symbolHg", i18n.tr("symbolHg"));
		entries.put("symbolCu", i18n.tr("symbolCu"));
		entries.put("symbolNi", i18n.tr("symbolNi"));
		entries.put("symbolZn", i18n.tr("symbolZn"));
		entries.put("symbolSn", i18n.tr("symbolSn"));
		entries.put("pseudoStar", i18n.tr("pseudoStar"));
		entries.put("pseudoR", i18n.tr("pseudoR"));
		entries.put("bond", i18n.tr("bond"));
		entries.put("cyclesymbol", i18n.tr("cyclesymbol"));
		entries.put("periodictable", i18n.tr("periodictable"));
		entries.put("enterelement", i18n.tr("enterelement"));
		entries.put("up_bond", i18n.tr("up_bond"));
		entries.put("down_bond", i18n.tr("down_bond"));
		entries.put("plus", i18n.tr("plus"));
		entries.put("minus", i18n.tr("minus"));
		entries.put("move", i18n.tr("move"));
		entries.put("eraser", i18n.tr("eraser"));
		entries.put("lasso", i18n.tr("lasso"));
		entries.put("select", i18n.tr("select"));
		entries.put("triangle", i18n.tr("triangle"));
		entries.put("square", i18n.tr("square"));
		entries.put("pentagon", i18n.tr("pentagon"));
		entries.put("hexagon", i18n.tr("hexagon"));
		entries.put("heptagon", i18n.tr("heptagon"));
		entries.put("octagon", i18n.tr("octagon"));
		entries.put("benzene", i18n.tr("benzene"));
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
