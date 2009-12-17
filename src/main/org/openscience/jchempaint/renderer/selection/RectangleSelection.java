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
    double startX=0;
    double startY=0;
   
    public RectangleSelection() {
        this.rectangle = new Rectangle2D.Double();
    }
    
    public IRenderingElement generate(Color color) {
        return new RectangleElement(
                this.rectangle.getMinX(),
                this.rectangle.getMaxY(),
                this.rectangle.getMaxX(),
                this.rectangle.getMinY(),
                color);
    }
        
    public boolean contains(Point2d p) {
        return this.rectangle.contains(p.x, p.y);
    }

    // x will be the smallest x around (the base..)
    // and negative width is not allowed 
    // adding points always works, but setRect goes wrong. 
    // .. the x is actually p.x 
    
    public void addPoint(Point2d p) {
        if (rectangle.getHeight() == 0 && rectangle.getWidth() == 0) {
            rectangle = new Rectangle2D.Double(p.x, p.y, 0.1, 0.1);
            startX=p.x;
            startY=p.y;
        } 

        else {
            if (rectangle.contains(new Point2D.Double(p.x, p.y))) {
                double width=0, height=0;
                double x = rectangle.getX();
                if (x == startX) {
                    width=p.x - x;
                }
                else {
                    x=p.x;
                    width=Math.abs(startX-p.x);
                }
                
                double y = rectangle.getY();
                if (y == startY) {
                    height=p.y - y;
                }
                else {
                    y=p.y;
                    height=Math.abs(startY-p.y);
                }
                rectangle.setRect(x, y, width, height);

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
