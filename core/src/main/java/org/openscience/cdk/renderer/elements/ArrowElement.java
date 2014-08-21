/* $Revision$ 
 * $Author$ 
 * $Date$ 
 * Copyright (C) 2009 Stefan Kuhn 
 * Contact: cdk-devel@list.sourceforge.net 
 * This program
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.elements;

import java.awt.Color;

/**
 * @cdk.module renderextra
 */
public class ArrowElement implements IRenderingElement {

	public final double x1;
	public final double y1;
	public final double x2;
	public final double y2;
	public final double width;
	public final Color color;
	public final boolean direction;

	public ArrowElement(double x, double y, double x1, double y1, double width, boolean direction, Color color) {
		this.x2 = x;
		this.y2 = y;
		this.x1 = x1;
		this.y1 = y1;
		this.width = width;
		this.color = color;
		this.direction = direction;
	}

	public void accept(IRenderingVisitor v) {
		v.visit(this);
	}
}
