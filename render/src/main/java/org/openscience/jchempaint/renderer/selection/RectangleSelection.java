/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.jchempaint.renderer.selection;

import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.RectangleElement;

import javax.vecmath.Point2d;
import java.awt.Color;

/**
 * Rectangle selection is defined by two points. Adding extra points will update
 * the second coordinate but the first point is fixed.
 *
 * @cdk.module rendercontrol
 */
public class RectangleSelection extends ShapeSelection {

    private Point2d first;
    private Point2d second;

    public RectangleSelection() {
    }

    public IRenderingElement generate(Color color) {
        if (first == null || second == null)
            return null;
        double minX = Math.min(first.x, second.x);
        double maxY = Math.max(first.y, second.y);
        double maxX = Math.max(first.x, second.x);
        double minY = Math.min(first.y, second.y);
        return new RectangleElement(minX, maxY, maxX, minY, color);
    }

    public boolean contains(Point2d p) {
        if (first == null || second == null)
            return false;
        double minX = Math.min(first.x, second.x);
        double maxY = Math.max(first.y, second.y);
        double maxX = Math.max(first.x, second.x);
        double minY = Math.min(first.y, second.y);
        return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY;
    }

    public void addPoint(Point2d p) {
        if (first == null)
            first = p;
        else
            second = p;
    }

    public boolean isEmpty() {
        return first == null || second == null;
    }

    public void reset() {
        this.finished = true;
        first = null;
        second = null;
    }
}
