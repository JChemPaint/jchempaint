/* $Revision$ $Author$ $Date$
*
*  Copyright (C) 2008 Gilleain Torrance <gilleain.torrance@gmail.com>
*
*  Contact: cdk-devel@list.sourceforge.net
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public License
*  as published by the Free Software Foundation; either version 2.1
*  of the License, or (at your option) any later version.
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
package org.openscience.jchempaint.controller;

import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module controlbasic
 */
public class ControllerParameters {
    
    private String drawElement = "C";
    
    private boolean drawPseudoAtom=false;
    
    private int drawIsotope = 0;

    private String[] commonElements = { "C", "O", "N", "P", "S", "Br", "Cl", "F", "I" };

    private boolean snapToGridAngle = true;
    
    private int snapAngle = 15;

    private boolean snapToGridCartesian = true;
    
    private int snapCartesian = 10;

    private double bondPointerLength = 20;
    
    private double ringPointerLength = 20;
    
    private boolean autoUpdateImplicitHydrogens = true;
    
    private String defaultElementSymbol = "C";
    
    private IBond.Order maxOrder = IBond.Order.QUADRUPLE;
    
    private boolean hightlighLastSelected = false;


    public IBond.Order getMaxOrder() {
        return this.maxOrder;
    }
    
    public void setMaxOrder(IBond.Order maxOrder) {
        this.maxOrder = maxOrder;
    }

    // for controlling, if the structure or substructural parts might be moved
    private boolean isMovingAllowed = true;
    
    public boolean isMovingAllowed() {
        return isMovingAllowed;
    }

    public void setMovingAllowed(boolean isMovingAllowed) {
        this.isMovingAllowed = isMovingAllowed;
    }

    public boolean isSnapToGridAngle() {
        return snapToGridAngle;
    }

    public void setSnapToGridAngle(boolean snapToGridAngle) {
        this.snapToGridAngle = snapToGridAngle;
    }

    public int getSnapAngle() {
        return snapAngle;
    }

    public void setSnapAngle(int snapAngle) {
        this.snapAngle = snapAngle;
    }

    public boolean isSnapToGridCartesian() {
        return snapToGridCartesian;
    }

    public void setSnapToGridCartesian(boolean snapToGridCartesian) {
        this.snapToGridCartesian = snapToGridCartesian;
    }

    public int getSnapCartesian() {
        return snapCartesian;
    }

    public void setSnapCartesian(int snapCartesian) {
        this.snapCartesian = snapCartesian;
    }

    public double getBondPointerLength() {
        return bondPointerLength;
    }

    public void setBondPointerLength(double bondPointerLength) {
        this.bondPointerLength = bondPointerLength;
    }

    public double getRingPointerLength() {
        return ringPointerLength;
    }

    public void setRingPointerLength(double ringPointerLength) {
        this.ringPointerLength = ringPointerLength;
    }

    public boolean isAutoUpdateImplicitHydrogens() {
        return autoUpdateImplicitHydrogens;
    }

    public void setAutoUpdateImplicitHydrogens(boolean autoUpdateImplicitHydrogens) {
        this.autoUpdateImplicitHydrogens = autoUpdateImplicitHydrogens;
    }

    public String getDefaultElementSymbol() {
        return defaultElementSymbol;
    }

    public void setDefaultElementSymbol(String defaultElementSymbol) {
        this.defaultElementSymbol = defaultElementSymbol;
    }

    public String getDrawElement() {
        return drawElement;
    }

    public void setDrawElement(String drawElement) {
        this.drawElement = drawElement;
    }
    
    public void setDrawPseudoAtom(boolean drawPseudoAtom){
        this.drawPseudoAtom=drawPseudoAtom;
    }
    
    public boolean getDrawPseudoAtom(){
        return drawPseudoAtom;
    }

    public String[] getCommonElements() {
        return commonElements;
    }

    public void setCommonElements(String[] commonElements) {
        this.commonElements = commonElements;
    }
    
    public int getDrawIsotope() {
        return drawIsotope;
    }

    public void setDrawIsotope(int isotope) {
        this.drawIsotope = isotope;
    }
      
    /**
     * @return Tells if after a structure change the affected part should be selected.
     */
    public boolean isHightlighLastSelected() {
        return hightlighLastSelected;
    }

    /**
     * Determines if after a structure change the affected part should be selected.
     * 
     * @param hightlighLastSelected True=will be selected, false=will not be selected.
     */
    public void setHightlighLastSelected(boolean hightlighLastSelected) {
        this.hightlighLastSelected = hightlighLastSelected;
    }
}
