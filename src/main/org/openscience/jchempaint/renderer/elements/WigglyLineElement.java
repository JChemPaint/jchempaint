/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2009  Stefan Kuhn <stefan.kuhn@ebi.ac.uk>
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
package org.openscience.jchempaint.renderer.elements;

import java.awt.Color;

/**
 * An element representing a wiggly line. Note that according to IUPAC 
 * recommendations {@cdk.cite IUPAC2006} this is the only recommended type 
 * of undefined stereo bond. The criss cross wedge is discouraged.
 * 
 * @cdk.module renderbasic
 * @author shk3
 */
public class WigglyLineElement extends LineElement {


	/**
	 * @param x1    X coordinate of start point.
	 * @param y1    Y coordinate of start point.
	 * @param x2    X coordinate of end point.
	 * @param y2    Y coordinate of end point.
	 * @param width The disired width
	 * @param color The desired color.
	 */
	public WigglyLineElement(double x1, double y1, double x2, double y2,
			double width, Color color) {
		super(x1, y1, x2, y2, width, color);
	}

	/**
	 * Constructor for the WigglyLineElement
	 * 
	 * @param element The LineElement this WigglyLine is based on.
	 * @param color   The desired color.
	 */
	public WigglyLineElement(LineElement element,
			Color color) {
		this(element.x1, element.y1, element.x2, element.y2,
		     element.width, color);
	}

	@Override
	public void accept(IRenderingVisitor v) {
		v.visit(this);
	}
}