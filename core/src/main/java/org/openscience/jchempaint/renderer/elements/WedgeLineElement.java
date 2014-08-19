/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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
 * An element representing a dashed, solid or crisscross wedge.
 * Note that according to IUPAC recommendations {@cdk.cite IUPAC2006}  
 * the criss cross wedge is discouraged. We still need to show it, since 
 * e. g. MDL mol file definition has it.
 * 
 * @cdk.module renderbasic
 */
public class WedgeLineElement extends LineElement {

	public final int wedgeType;
	public final Direction direction;

	public enum Direction {
		toFirst, toSecond;
	}

	/**
	 * Constructor for the WedgeLineElement
	 * 
	 * @param x1      x of point 1 of the line.
	 * @param y1      y of point 1 of the line.
	 * @param x2      x of point 2 of the line.
	 * @param y2      y of point 2 of the line.
	 * @param width   width of the line.
	 * @param wedgeType 0=dashed, 1=solid, 2=crisscross.
	 * @param direction true=sharp end at element x1/y1, false=sharp at x2/y2.
	 * @param color   The desired color.
	 */
	public WedgeLineElement(double x1, double y1, double x2, double y2,
			double width, int wedgeType, Direction direction, Color color) {
		super(x1, y1, x2, y2, width, color);
		this.wedgeType = wedgeType;
		this.direction = direction;
	}

	/**
	 * Constructor for the WedgeLineElement
	 * 
	 * @param element The LineElement this WigglyLine is based on.
	 * @param type    0=dashed, 1=solid, 2=crisscross.
	 * @param direction true=sharp end at element x1/y1, false=sharp at x2/y2.
	 * @param color   The desired color.
	 */
	public WedgeLineElement(LineElement element, int type,
			Direction direction, Color color) {
		this(direction == Direction.toFirst ? element.x2: element.x1,
			 direction == Direction.toFirst ? element.y2: element.y1,
			 direction == Direction.toFirst ? element.x1 : element.x2,
			 direction == Direction.toFirst ? element.y1 : element.y2,
		     element.width, type, direction, color);
	}

	@Override
	public void accept(IRenderingVisitor v) {
		v.visit(this);
	}
}
