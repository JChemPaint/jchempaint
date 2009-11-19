/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.renderer.generators;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Point2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.TextGroupElement;
import org.openscience.jchempaint.renderer.elements.TextGroupElement.Position;

/**
 * A generator for atoms with mass, charge, etc.
 * 
 * @author maclean
 * @cdk.module renderextra
 *
 */
public class ExtendedAtomGenerator extends BasicAtomGenerator {
    
    public IRenderingElement generate(
            IAtomContainer ac, IAtom atom, RendererModel model) {
        
        Integer majorIsotopeNumber = null;
        if(atom.getMassNumber()!=null){
            try {
    			IIsotope isotope = IsotopeFactory
                    .getInstance(
                            atom.getBuilder()).getMajorIsotope(
                            atom.getSymbol());
    			if(isotope!=null)
    				majorIsotopeNumber = isotope.getMassNumber();
    		} catch (IOException e) {
    
    		}
        }
        if ((!hasCoordinates(atom) 
             || invisibleHydrogen(atom, model) 
             || (invisibleCarbon(atom, ac, model) && !model.getDrawNumbers()))
             && (atom.getMassNumber()==null 
            		 || atom.getMassNumber()==majorIsotopeNumber)
             && atom.getValency()==(Integer)CDKConstants.UNSET
             && !atom.getFlag(CDKConstants.IS_TYPEABLE)) {
            return null;
        } else if (model.getIsCompact()) {
            return this.generateCompactElement(atom, model);
        } else {
            String text;
            if (atom instanceof IPseudoAtom) {
                text = ((IPseudoAtom) atom).getLabel();
            } else if (invisibleCarbon(atom, ac, model) && model.drawNumbers()) {
                text = String.valueOf(ac.getAtomNumber(atom) + 1);
            } else {
                text = atom.getSymbol();
            }
            Point2d p = atom.getPoint2d();
            Color c = getColorForAtom(atom, model);
            TextGroupElement textGroup = new TextGroupElement(p.x, p.y, text, c);
            if(atom.getFlag(CDKConstants.IS_TYPEABLE)){
                textGroup.isNotTypeableUnderlined = true;
                textGroup.notTypeableUnderlineColor = model.getNotTypeableUnderlineColor();
            }
            decorate(textGroup, ac, atom, model);
            return textGroup;
        }
    }
    
    public boolean hideAtomSymbol(IAtom atom, RendererModel model) {
        return atom.getSymbol().equals("C") && !model.getKekuleStructure();
    }
    
    public void decorate(TextGroupElement textGroup, 
                         IAtomContainer ac, 
                         IAtom atom, 
                         RendererModel model) {
        Stack<Position> unused = getUnusedPositions(ac, atom);
        
        if (!invisibleCarbon(atom, ac, model) && model.getDrawNumbers()) {
            Position position = getNextPosition(unused);
            String number = String.valueOf(ac.getAtomNumber(atom) + 1);
            textGroup.addChild(number, position);
        }
        
        if (model.getShowImplicitHydrogens()) {
        	if(atom.getHydrogenCount()!=null){
	            int nH = atom.getHydrogenCount();
	            if (nH > 0) {
	                Position position = getNextPosition(unused);
	                if (nH == 1) {
	                    textGroup.addChild("H", position);
	                } else {
	                    textGroup.addChild("H", String.valueOf(nH), position);
	                }
	            }
        	}
        }
        
        Integer massNumber = atom.getMassNumber();
        if (massNumber != null) {
            try {
                IsotopeFactory factory = 
                    IsotopeFactory.getInstance(ac.getBuilder());
                if(factory.getMajorIsotope(atom.getSymbol())!=null){
	                int majorMass = 
	                    factory.getMajorIsotope(atom.getSymbol()).getMassNumber();
	                if (massNumber != majorMass) {
	                    Position position = getNextPosition(unused);
	                    textGroup.addChild(String.valueOf(massNumber), position);
	                }
                }
            } catch (IOException io) {
                
            }
        }
        
        if(atom.getFormalCharge()!=0){
        	String chargeString="";
	        if (atom.getFormalCharge() == 1) {
	            chargeString = "+";
	        } else if (atom.getFormalCharge() > 1) {
	            chargeString = atom.getFormalCharge() + "+";
	        } else if (atom.getFormalCharge() == -1) {
	            chargeString = "-";
	        } else if (atom.getFormalCharge() < -1) {
	            int absCharge = Math.abs(atom.getFormalCharge());
	            chargeString = absCharge + "-";
	        }
            Position position = getNextPosition(unused);
            textGroup.addChild(chargeString, position);
        }

        if(atom.getValency()!=null){
        	String valencyString="(v"+atom.getValency().toString()+")";
            Position position = getNextPosition(unused);
            textGroup.addChild(valencyString, position);
        }
    }
    
    private Position getNextPosition(Stack<Position> unused) {
        if (unused.size() > 0) {
            return unused.pop();
        } else {
            return Position.N;
        }
    }
    
    public Stack<Position> getUnusedPositions(IAtomContainer ac, IAtom atom) {
        Stack<Position> unused = new Stack<Position>();
        for (Position p : Position.values()) {
            unused.add(p);
        }
        
        for (IAtom connectedAtom : ac.getConnectedAtomsList(atom)) {
            List<Position> used = getPosition(atom, connectedAtom);
            for(int i=0;i<used.size();i++){
                unused.remove(used.get(i));
            }
        }
        return unused;
    }
    
    public List<Position> getPosition(IAtom atom, IAtom connectedAtom) {
        Point2d pA = atom.getPoint2d();
        Point2d pB = connectedAtom.getPoint2d();
        double dx = pB.x - pA.x;
        double dy = pB.y - pA.y;
        List<Position> used=new ArrayList<Position>();
        
        final double DELTA = 0.2;
        
        if (dx < -DELTA) {                          // generally west
            if (dy < -DELTA) {
                used.add(Position.N);
                used.add(Position.NW);
                used.add(Position.W);
            } else if (dy > -DELTA && dy < DELTA) {
                used.add(Position.NW);
                used.add(Position.W);
                used.add(Position.SW);
            } else {
                used.add(Position.W);
                used.add(Position.SW);
                used.add(Position.S);
            }
        } else if (dx > -DELTA && dx < DELTA) {     //  north or south
            if (dy < -DELTA) {
                used.add(Position.NW);
                used.add(Position.N);
                used.add(Position.NE);
            } else if (dy > -DELTA && dy < DELTA) { // right on top of the atom!
                used.add(Position.NW);
                used.add(Position.N);
                used.add(Position.NE);
            } else {
                used.add(Position.SW);
                used.add(Position.S);
                used.add(Position.SE);
            }
        } else {                                    // generally east 
            if (dy < -DELTA) {
                used.add(Position.N);
                used.add(Position.NE);
                used.add(Position.E);
            } else if (dy > -DELTA && dy < DELTA) {
                used.add(Position.NE);
                used.add(Position.E);
                used.add(Position.SE);
            } else {
                used.add(Position.E);
                used.add(Position.SE);
                used.add(Position.S);
            }
        }
        return used;
    }
    
}
