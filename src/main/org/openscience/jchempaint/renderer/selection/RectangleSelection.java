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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Point2d;

import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.RectangleElement;

/**
 * @cdk.module rendercontrol
 */
public class RectangleSelection extends ShapeSelection {
    
    private Rectangle2D rectangle;
   
    public RectangleSelection() {
        this.rectangle = new Rectangle2D.Double();
    }
    
    public IRenderingElement generate(Color color) {
        return new RectangleElement(
                this.rectangle.getMinX(),
                this.rectangle.getMinY(),
                this.rectangle.getMaxX(),
                this.rectangle.getMaxY(),
                color);
    }
        
    public boolean contains(Point2d p) {
        return this.rectangle.contains(p.x, p.y);
    }

    public void addPoint(Point2d p) {
    	if (rectangle.getHeight() == 0 && rectangle.getWidth() == 0) {
            rectangle = new Rectangle2D.Double(p.x, p.y, 1, 1);
        } else {
            if (rectangle.contains(new Point2D.Double(p.x, p.y))) {
                double x = rectangle.getX();
                double y = rectangle.getY();
                rectangle.setRect(x, y, p.x - x, p.y - y);
            } else {
                rectangle.add(new Point2D.Double(p.x, p.y));
            }
        }
    }

    public boolean isEmpty() {
        return this.rectangle.isEmpty();
    }

    public void reset() {
        this.finished = true;
        this.rectangle.setRect(rectangle.getX(), rectangle.getY(), 0, 0);
    }
}
