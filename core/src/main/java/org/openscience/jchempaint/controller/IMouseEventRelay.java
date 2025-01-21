/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net or nout@science.uva.nl
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

/**
 * 
 * @cdk.module control
 */
public interface IMouseEventRelay {
    
    void mouseWheelMovedBackward(int modifiers, int clicks);
    
    void mouseWheelMovedForward(int modifiers, int clicks);

    void mouseClickedUp(int screenCoordX, int screenCoordY);

    void mouseClickedDown(int screenCoordX, int screenCoordY);
    
	void mouseClickedUp(int screenCoordX, int screenCoordY, int modifiers);
    
	void mouseClickedDown(int screenCoordX, int screenCoordY, int modifiers);

	void mouseClickedUpRight(int screenCoordX, int screenCoordY);
	
	/**
	 * 
	 * @param screenCoordX
	 * @param screenCoordY
	 */
	void mouseClickedDownRight(int screenCoordX, int screenCoordY);
	
	/**
	 * 
	 * @param screenCoordX
	 * @param screenCoordY
	 */
	void mouseClickedDouble(int screenCoordX, int screenCoordY);
	
	/**
	 * 
	 * @param screenCoordX
	 * @param screenCoordY
	 */
	void mouseMove(int screenCoordX, int screenCoordY);
	
	/**
	 * 
	 * @param screenCoordX
	 * @param screenCoordY
	 */
	void mouseEnter(int screenCoordX, int screenCoordY);
	
	/**
	 * 
	 * @param screenCoordX
	 * @param screenCoordY
	 */
	void mouseExit(int screenCoordX, int screenCoordY);
	
	/**
	 * 
	 * @param screenCoordXFrom
	 * @param screenCoordYFrom
	 * @param screenCoordXTo
	 * @param screenCoordYTo
	 */
	void mouseDrag(int screenCoordXFrom, int screenCoordYFrom, 
	        int screenCoordXTo, int screenCoordYTo);
	
	
}
