/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.jchempaint.controller;

import java.io.Serializable;
import java.util.HashMap;

import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module controlbasic
 * @cdk.svnrev $Revision$
 */
public class ControllerModel implements Serializable, Cloneable, IControllerModel {

    private static final long serialVersionUID = 9007159812273128989L;

    private ControllerParameters parameters;

    private HashMap<Object, Object> merge = new HashMap<Object, Object>();
    
    public ControllerModel() {
        this.parameters = new ControllerParameters();
    }
    
    /**
     * This is the central facility for handling "merges" of atoms. A merge
     * occures if during moving atoms an atom is in Range of another atom. These
     * atoms are then put into the merge map as a key-value pair. During the
     * move, the atoms are then marked by a circle and on releasing the mouse
     * they get actually merged, meaning one atom is removed and bonds pointing
     * to this atom are made to point to the atom it has been marged with.
     * 
     * @return Returns the merge.map
     * 
     *         FIXME: this belongs in the controller model... this is not about
     *         rendering, it's about editing (aka controlling)
     */
    public HashMap<Object, Object> getMerge() {
        return merge;
    }
    
    
    public IBond.Order getMaxOrder() {
        return this.parameters.getMaxOrder();
    }
    
    public void setMaxOrder(IBond.Order maxOrder) {
        this.parameters.setMaxOrder(maxOrder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#getSnapToGridAngle()
     */
    public boolean getSnapToGridAngle() {
        return this.parameters.isSnapToGridAngle();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.openscience.cdk.controller.IController2DModel#
     * getAutoUpdateImplicitHydrogens()
     */
    public boolean getAutoUpdateImplicitHydrogens() {
        return this.parameters.isAutoUpdateImplicitHydrogens();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.openscience.cdk.controller.IController2DModel#
     * setAutoUpdateImplicitHydrogens(boolean)
     */
    public void setAutoUpdateImplicitHydrogens(boolean update) {
        this.parameters.setAutoUpdateImplicitHydrogens(update);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setSnapToGridAngle(
     * boolean)
     */
    public void setSnapToGridAngle(boolean snapToGridAngle) {
        this.parameters.setSnapToGridAngle(snapToGridAngle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openscience.cdk.controller.IController2DModel#getSnapAngle()
     */
    public int getSnapAngle() {
        return this.parameters.getSnapAngle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openscience.cdk.controller.IController2DModel#setSnapAngle(int)
     */
    public void setSnapAngle(int snapAngle) {
        this.parameters.setSnapAngle(snapAngle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#getSnapToGridCartesian
     * ()
     */
    public boolean getSnapToGridCartesian() {
        return this.parameters.isSnapToGridCartesian();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setSnapToGridCartesian
     * (boolean)
     */
    public void setSnapToGridCartesian(boolean snapToGridCartesian) {
        this.parameters.setSnapToGridCartesian(snapToGridCartesian);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openscience.cdk.controller.IController2DModel#getSnapCartesian()
     */
    public int getSnapCartesian() {
        return this.parameters.getSnapCartesian();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setSnapCartesian(int)
     */
    public void setSnapCartesian(int snapCartesian) {
        this.parameters.setSnapCartesian(snapCartesian);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#getDefaultElementSymbol
     * ()
     */
    public String getDefaultElementSymbol() {
        return this.parameters.getDefaultElementSymbol();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setDefaultElementSymbol
     * (java.lang.String)
     */
    public void setDefaultElementSymbol(String defaultElementSymbol) {
        this.parameters.setDefaultElementSymbol(defaultElementSymbol);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#getBondPointerLength()
     */
    public double getBondPointerLength() {
        return this.parameters.getBondPointerLength();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setBondPointerLength
     * (double)
     */
    public void setBondPointerLength(double bondPointerLength) {
        this.parameters.setBondPointerLength(bondPointerLength);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#getRingPointerLength()
     */
    public double getRingPointerLength() {
        return this.parameters.getRingPointerLength();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setRingPointerLength
     * (double)
     */
    public void setRingPointerLength(double ringPointerLength) {
        this.parameters.setRingPointerLength(ringPointerLength);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setCommonElements(java
     * .lang.String[])
     */
    public void setCommonElements(String[] elements) {
        this.parameters.setCommonElements(elements);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#getCommonElements()
     */
    public String[] getCommonElements() {
        return this.parameters.getCommonElements();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IControllerModel#setDrawPseudoAtom(boolean)
     */
    public void setDrawPseudoAtom(boolean drawPseudoAtom){
        this.parameters.setDrawPseudoAtom(drawPseudoAtom);
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IControllerModel#getDrawPseudoAtom()
     */
    public boolean getDrawPseudoAtom(){
        return this.parameters.getDrawPseudoAtom();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setDrawElement(java
     * .lang.String)
     */
    public void setDrawElement(String element) {
        this.parameters.setDrawElement(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openscience.cdk.controller.IController2DModel#getDrawElement()
     */
    public String getDrawElement() {
        return this.parameters.getDrawElement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openscience.cdk.controller.IController2DModel#isMovingAllowed()
     */
    public boolean isMovingAllowed() {
        return this.parameters.isMovingAllowed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openscience.cdk.controller.IController2DModel#setMovingAllowed(boolean
     * )
     */
    public void setMovingAllowed(boolean isMovingAllowed) {
        this.parameters.setMovingAllowed(isMovingAllowed);
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IControllerModel#getDrawIsotopeNumber()
     */
    public int getDrawIsotopeNumber() {
        return this.parameters.getDrawIsotope();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IControllerModel#setDrawIsotopeNumber(int)
     */
    public void setDrawIsotopeNumber(int isotope) {
        this.parameters.setDrawIsotope(isotope);
        
    }
    
    
    /**
     * @return Tells if after a structure change the affected part should be selected.
     */
    public boolean isHightlighLastSelected() {
        return this.parameters.isHightlighLastSelected();
    }

    /**
     * Determines if after a structure change the affected part should be selected.
     * 
     * @param hightlighLastSelected True=will be selected, false=will not be selected.
     */
    public void setHightlighLastSelected(boolean hightlighLastSelected) {
        this.parameters.setHightlighLastSelected(hightlighLastSelected);
    }
}
