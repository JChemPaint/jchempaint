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
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import javax.vecmath.Point2d;

import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.PathElement;

/**
 * @cdk.module rendercontrol
 */
public class LassoSelection extends ShapeSelection {
    
    private final ArrayList<Point2d> points;
    private GeneralPath path;
    
    public LassoSelection() {
        this.points = new ArrayList<Point2d>();
        this.path = null;
    }
    
    public IRenderingElement generate(Color color) {
        return new PathElement(this.points, color);
    }
    
    public boolean contains(Point2d p) {
        if (this.points.size() < 3) return false;
        this.path = new GeneralPath();
        Point2d p0 = this.points.get(0);
        this.path.moveTo((float)p0.x, (float)p0.y);
        for (Point2d point : this.points) {
            this.path.lineTo((float)point.x, (float)point.y);
        }
        this.path.closePath();
        return this.path.contains(p.x, p.y);
    }

    public void addPoint(Point2d p) {
        this.points.add(new Point2d(p.x, p.y));
    }

    public boolean isEmpty() {
        return this.points.isEmpty();
    }
    
    public void reset() {
        this.finished = true;
        this.points.clear();
        this.path = null;
    }
}
