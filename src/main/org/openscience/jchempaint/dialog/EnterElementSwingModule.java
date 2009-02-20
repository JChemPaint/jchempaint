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
package org.openscience.jchempaint.dialog;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.vecmath.Point2d;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.controller.ControllerModuleAdapter;
import org.openscience.cdk.controller.IChemModelRelay;
import org.openscience.cdk.controller.IControllerModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.layout.AtomPlacer;
import org.openscience.cdk.layout.RingPlacer;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

public class EnterElementSwingModule extends ControllerModuleAdapter {

	private HashMap<String,IMolecule> funcgroupsmap=new HashMap<String,IMolecule>();
	private final static RingPlacer ringPlacer = new RingPlacer();
	
	public EnterElementSwingModule(IChemModelRelay chemModelRelay) {
		super(chemModelRelay);
        String filename = "org/openscience/jchempaint/resources/funcgroups.txt";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SmilesParser sp=new SmilesParser(DefaultChemObjectBuilder.getInstance());
		StringBuffer sb=new StringBuffer();
		InputStreamReader isr = new InputStreamReader(ins);
		try{
			while(true){
				int i=isr.read();
				if(i==-1){
					break;
				}else if(((char)i)=='\n' || ((char)i)=='\r'){
					if(!sb.toString().equals("")){
						StringTokenizer st=new StringTokenizer(sb.toString());
						String key=(String)st.nextElement();
						String value=(String)st.nextElement();
						funcgroupsmap.put(key, sp.parseSmiles(value));
						sb=new StringBuffer();
					}
				}else{
					sb.append((char)i);
				}
			}
			if(!sb.toString().equals("")){
				StringTokenizer st=new StringTokenizer(sb.toString());
				String key=(String)st.nextElement();
				String value=(String)st.nextElement();
				funcgroupsmap.put(key, sp.parseSmiles(value));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void mouseClickedDown(Point2d worldCoord) {

		IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
		String[] funcGroupsKeys=new String[funcgroupsmap.keySet().size()+1];
        Iterator<String> it=funcgroupsmap.keySet().iterator();
        int h=1;
        funcGroupsKeys[0]="";
        while(it.hasNext()){
        	funcGroupsKeys[h]=(String)it.next();
        	h++;
        }
		String x=EnterElementOrGroupDialog.showDialog(null,null, "Enter an element symbol or choose/enter a functional group abbrivation:", "Enter element", funcGroupsKeys, "","");
		try{
			IAtomContainer ac=(IAtomContainer)funcgroupsmap.get(x.toLowerCase());
			//this means a functional group was entered
			if(ac!=null && !x.equals("")){
				ac=(IAtomContainer)((IAtomContainer)funcgroupsmap.get(x)).clone();
				IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModelRelay.getIChemModel(), closestAtom);
				IAtom lastplaced=null;
				int counter=0;
				//this is the starting point for placing
				ac.getAtom(0).setPoint2d(closestAtom.getPoint2d());
				lastplaced=ac.getAtom(0);
				counter=1;
				container.add(ac);
				List<IBond> connbonds=container.getConnectedBondsList(closestAtom);
				for(int i=0;i<connbonds.size();i++){
					IBond bond=connbonds.get(i);
					if(bond.getAtom(0)==closestAtom){
						bond.setAtom(ac.getAtom(0), 0);
					}else{
						bond.setAtom(ac.getAtom(0), 1);
					}
				}
				container.removeAtomAndConnectedElectronContainers(closestAtom);
				AtomPlacer ap=new AtomPlacer();
				while(lastplaced!=null){
					IAtomContainer placedNeighbours=container.getBuilder().newAtomContainer();
					IAtomContainer unplacedNeighbours=container.getBuilder().newAtomContainer();
					List<IAtom> l=container.getConnectedAtomsList(lastplaced);
					for(int i=0;i<l.size();i++){
						if(l.get(i).getPoint2d()!=null)
							placedNeighbours.addAtom((IAtom)l.get(i));
						else
							unplacedNeighbours.addAtom((IAtom)l.get(i));
					}
					ap.distributePartners(lastplaced, placedNeighbours, GeometryTools.get2DCenter(placedNeighbours), unplacedNeighbours, chemModelRelay.getRenderer().getRenderer2DModel().getBondLength());
					IRingSet ringset=new SSSRFinder(ac).findSSSR();
					for(IAtomContainer ring:ringset.atomContainers()){
						ringPlacer.placeRing((IRing)ring, GeometryTools.get2DCenter(ac), chemModelRelay.getRenderer().getRenderer2DModel().getBondLength());
					}
					System.err.println(chemModelRelay.getRenderer().getRenderer2DModel().getBondLength());
					lastplaced=ac.getAtom(counter);
					counter++;
					if(counter==ac.getAtomCount())
						lastplaced=null;
				}
			}else if(x!=null && x.length()>0){
				if(Character.isLowerCase(x.toCharArray()[0]))
					x=Character.toUpperCase(x.charAt(0))+x.substring(1);
				IsotopeFactory ifa=IsotopeFactory.getInstance(closestAtom.getBuilder());
				IIsotope iso=ifa.getMajorIsotope(x);
				if(iso!=null)
					closestAtom.setSymbol(x);
			}
			chemModelRelay.updateView();				
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public String getDrawModeString() {
		return "Enter Element or Group";
	}
}
