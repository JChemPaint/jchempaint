package org.openscience.jchempaint.renderer.generators;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.isomorphism.matchers.RGroup;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.TextElement;

/**
 * Generator for R-group specific visualizations. The class needs an extra
 * IRGroupQuery type attribute to work with, as the atomcontainer provided
 * does not have all the information we need here. 
 * @author Mark R.
 */
public class RGroupGenerator implements IGenerator{

	private IRGroupQuery rGroupQuery;
	private final static String APO1_LABEL="*"; 
	private final static String APO2_LABEL="\"";
	private final static Color  APO_LABEL_COLOR=new Color(102,153,102);

	public IRGroupQuery getRGroupQuery() {
		return rGroupQuery;
	}

	public void setRGroupQuery(IRGroupQuery rGroupQuery) {
		this.rGroupQuery = rGroupQuery;
	}

	/**
	 * Labels an attachment point (apo) atom with a symbol. The apo resides
	 * in a R-group substitute, and it is relevant to visualize which of the
	 * possible atoms in the substitute is the attachment point, so where the 
	 * substitute will connect to the root structure.  
	 * @param apoAtom attachment point (apo) atom
	 * @param diagram 
	 * @param label text to label the apo with
	 * @param ac current atom container
	 * @param model rendermodel with user preferences etc.
	 */
	private void labelApoAtoms (IAtom apoAtom, ElementGroup diagram, String label,IAtomContainer ac,RendererModel model) {
		//TODO: could also adjust offset for zoom level.. (model.getZoomFactor())
		
		if (apoAtom==null)
			return;

		double xOffset=0.2;
		double yOffset=0.2;

		//If preference= carbon not drawn, keep the Y offset 0 (looks better, apo symbol will be closer to atom)
		if( apoAtom.getSymbol().equals("C") && 
			!model.getKekuleStructure() &&
			!(model.getShowEndCarbons()&& ac.getConnectedBondsList(apoAtom).size() == 1)	) {
			yOffset=0;
		}

		if (label.equals(APO2_LABEL)) {
			xOffset+=0.1; //prevent two labels possibly on top of each other, when apo1==apo2
		}
		
		diagram.add(new TextElement(apoAtom.getPoint2d().x+xOffset,apoAtom.getPoint2d().y+yOffset,label,APO_LABEL_COLOR));
	}

	/**
	 * Labels bonds going into root R-groups with appropriate symbol.
	 * @param diagram
	 */
	private void labelRootApoBonds (ElementGroup diagram,IAtomContainer ac) {
		Map<IAtom, Map<Integer, IBond>> rootAttachmentPoints = rGroupQuery.getRootAttachmentPoints();

		for (Iterator<IAtom> atomItr=rootAttachmentPoints.keySet().iterator(); atomItr.hasNext();) {
			Map<Integer,IBond> bonds =rootAttachmentPoints.get(atomItr.next());
			for(Iterator<Integer> bondItr=bonds.keySet().iterator(); bondItr.hasNext();) {
				int bondNum=bondItr.next();
				IBond b = bonds.get(bondNum);
				if (ac.contains(b)) {
			        double xAvg = (b.getAtom(0).getPoint2d().x + b.getAtom(1).getPoint2d().x)/2;
			        double yAvg = (b.getAtom(0).getPoint2d().y + b.getAtom(1).getPoint2d().y)/2; 
			        diagram.add(new TextElement(xAvg+0.1, yAvg-0.1,(bondNum==1?APO1_LABEL:APO2_LABEL),APO_LABEL_COLOR));
				}
			}
		}
	}

	/**
	 * Generate R-group visualizations.
	 */
	public IRenderingElement generate(IAtomContainer ac, RendererModel model) {

		if (rGroupQuery==null || ac.getAtomCount()==0) {
			return null;
		}

		//ToDO find a better place for this
		model.setShowMoleculeTitle(true);

		ElementGroup diagram = new ElementGroup();

		boolean acDetachedFromRGroup=true;

		if (ac==rGroupQuery.getRootStructure()) {

			//DEBUG:
			//System.out.print("          _____\n          "+ac.hashCode()+":");for(IAtom tmp : ac.atoms()) {System.out.print(tmp.getSymbol()+" ");}System.out.println();
			//System.out.print("_____\n"+ac.hashCode()+":");for(IBond tmp : ac.bonds()) {System.out.print(tmp.hashCode()+" ");}System.out.println();
			
			labelRootApoBonds (diagram,ac);
			acDetachedFromRGroup=false;

		}
		else {

			//DEBUG:
			//System.out.print("           >"+ac.hashCode()+":");for(IAtom tmp : ac.atoms()) {System.out.print(tmp.getSymbol()+" "); if(tmp.getSymbol().equals("N")) System.out.print("("+tmp.hashCode()+") ");}System.out.println();
			if (rGroupQuery.getRGroupDefinitions().keySet()!=null) {
				apo:
				for(Iterator<Integer> itr =rGroupQuery.getRGroupDefinitions().keySet().iterator();itr.hasNext();) { 
					for (RGroup rgrp :rGroupQuery.getRGroupDefinitions().get(itr.next()).getRGroups()) {
						if(rgrp.getGroup()==ac) {
							acDetachedFromRGroup=false;
							if(ac.contains(rgrp.getFirstAttachmentPoint()))
								labelApoAtoms(rgrp.getFirstAttachmentPoint(),diagram,APO1_LABEL,ac,model);
							if (ac.contains(rgrp.getSecondAttachmentPoint()))
								labelApoAtoms(rgrp.getSecondAttachmentPoint(),diagram,APO2_LABEL,ac,model);
							break apo;
						}
					}
				}
			}
		}
		
		if (acDetachedFromRGroup) {
			ac.setProperty(CDKConstants.TITLE, "Not in R-Group");
		}
		return diagram;
	}

	public List<IGeneratorParameter> getParameters() {
		// Auto-generated method stub
		return null;
	}



}
