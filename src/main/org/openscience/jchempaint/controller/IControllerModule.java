/* $Revision: 7636 $ $Author: egonw $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import javax.vecmath.Point2d;

/**
 * Interface that Controller2D modules must implement. Each module is
 * associated with an editing mode (DRAWMODE_*), as given in
 * Controller2DModel.
 * 
 * @author egonw
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.module  control
 *
 * @see    ControllerModel
 */
public interface IControllerModule {

	public void setChemModelRelay(IChemModelRelay relay);
	
	public void mouseWheelMovedBackward(int clicks);

	public void mouseWheelMovedForward(int clicks);

	
	/**
	 * @param Point2d worldCoord
	 */
	public void mouseClickedUp(Point2d worldCoord);

	/**
	 * @param Point2d worldCoord
	 */
	public void mouseClickedDown(Point2d worldCoord);

	/**
	 * @param Point2d worldCoord
	 */
	public void mouseClickedUpRight(Point2d worldCoord);

	/**
	 * @param Point2d worldCoord
	 */
	public void mouseClickedDownRight(Point2d worldCoord);

	/**
	 * @param Point2d worldCoord
	 */
	public void mouseClickedDouble(Point2d worldCoord);

	/**
	 * @param Point2d worldCoord
	 */
	public void mouseMove(Point2d worldCoord);

	/**
	 * @param Point2d worldCoord
	 */
	public void mouseEnter(Point2d worldCoord);

	/**
	 * @param Point2d worldCoord
	 */
	public void mouseExit(Point2d worldCoord);

	/**
	 * @param Point2d worldCoordFrom
	 * @param Point2d worldCoordTo
	 */
	public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo);

	/**
	 * @return A string describing this module e. g. for the status bar
	 */
	public String getDrawModeString();
	
	/**
	 * @return Returns the ID attribute
	 */
	public String getID();

	/**
	 * Sets the ID attribute
	 * 
	 * @param ID The ID attribute
	 */
	public void setID(String ID);
	
	/**
	 * Set wasEscaped flag to true
	 */
	public void escapeTheMode();
	
	/**
	 * Returns wasEscaped attribute
	 */
	public boolean wasEscaped();
}
