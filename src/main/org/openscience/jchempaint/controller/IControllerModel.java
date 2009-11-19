/* $Revision: 10179 $ $Author: egonw $ $Date: 2008-02-18 17:05:56 +0100 (Mon, 18 Feb 2008) $
 * 
 * Copyright (C) 2008  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.jchempaint.controller;

import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module control
 */
public interface IControllerModel {

    public abstract IBond.Order getMaxOrder();
    
    public abstract void setMaxOrder(IBond.Order maxOrder);
    

	/**
	 * Returns the snapToGridAngle mode
	 *
	 * @return the snapToGridAngle mode
	 */
	public abstract boolean getSnapToGridAngle();

	/**
	 * Returns true if the number of implicit hydrogens is updated
	 * when an Atom is edited.
	 */
	public abstract boolean getAutoUpdateImplicitHydrogens();

	/**
	 * Sets whether the number of implicit hydrogens is update when an
	 * Atom is edited.
	 */
	public abstract void setAutoUpdateImplicitHydrogens(boolean update);

	/**
	 * Sets the snapToGridAngle mode
	 *
	 * @param   snapToGridAngle
	 */
	public abstract void setSnapToGridAngle(boolean snapToGridAngle);

	/**
	 * Returns the snapAngle mode
	 *
	 * @return the snapAngle mode
	 */
	public abstract int getSnapAngle();

	/**
	 * Sets the snapAngle mode
	 *
	 * @param   snapAngle  
	 */
	public abstract void setSnapAngle(int snapAngle);

	/**
	 * Returns the snapToGridCartesian mode
	 *
	 * @return the snapToGridCartesian mode
	 */
	public abstract boolean getSnapToGridCartesian();

	/**
	 * Sets the snapToGridCartesian mode
	 *
	 * @param   snapToGridCartesian  
	 */
	public abstract void setSnapToGridCartesian(boolean snapToGridCartesian);

	/**
	 *  Returns the snapCartesian value
	 *
	 * @return the snapCartesian value
	 */
	public abstract int getSnapCartesian();

	/**
	 * Sets the snapCartesian value
	 *
	 * @param   snapCartesian  
	 */
	public abstract void setSnapCartesian(int snapCartesian);

	public abstract String getDefaultElementSymbol();

	/**
	 * Sets the default element symbol
	 *
	 * @param   defaultElementSymbol  
	 */
	public abstract void setDefaultElementSymbol(String defaultElementSymbol);

	/**
	 * Returns the bond pointer length
	 *
	 * @return the length
	 */
	public abstract double getBondPointerLength();

	/**
	 * Sets the pointer length
	 *
	 * @param   bondPointerLength  
	 */
	public abstract void setBondPointerLength(double bondPointerLength);

	/**
	 * Returns the ring pointer length
	 *
	 * @return the length
	 */
	public abstract double getRingPointerLength();

	/**
	 * Sets the pointer length
	 *
	 * @param   ringPointerLength  
	 */
	public abstract void setRingPointerLength(double ringPointerLength);

	public abstract void setCommonElements(String[] elements);

	public abstract String[] getCommonElements();

	public abstract void setDrawElement(String element);

    /**
     * Sets the isotope number which new atoms will get by default.
     * 0 means new elements will be created with major isotope.
     * 
     * @param isotope The isotope number
     */
    public abstract void setDrawIsotopeNumber(int isotope);
    
    /**
     * Sets if newly created atoms are supposed to become pseudo atoms or not.
     * 
     * @param drawPseudoAtom true=new atoms will be pseudo atoms, false=they will 
     * be regular atoms.
     */
    public void setDrawPseudoAtom(boolean drawPseudoAtom);
    
    /**
     * Tells if newly created atoms are supposed to become pseudo atoms or not.
     * 
     * @return true=new atoms will be pseudo atoms, false=they will 
     * be regular atoms.
     */
    public boolean getDrawPseudoAtom();

	/**
	 * Element symbol that <b>new</b> atoms get by default.
	 */
	public abstract String getDrawElement();
	
    /**
     * Isotope number that <b>new</b> atoms get by default.
     */
    public abstract int getDrawIsotopeNumber();

	/**
	 * To retrieve the value of the isMovingAllowed flag
	 * @return boolean isMovingAllowed
	 */
	public abstract boolean isMovingAllowed();

	/**
	 * Lets you set the siMovingAllowed flag
	 * @param isMovingAllowed
	 */
	public abstract void setMovingAllowed(boolean isMovingAllowed);

    public boolean isHightlighLastSelected();

    public void setHightlighLastSelected(boolean hightlighLastSelected);
}