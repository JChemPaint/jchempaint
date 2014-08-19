/*  Copyright (C) 2009  Stefan Kuhn <shk3@users.sf.net>
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
package org.openscience.jchempaint.renderer.generators;


import java.awt.Color;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.path.PathBuilder;

/**
 * @cdk.module rendercontrol
 */
public class MergeAtomsGenerator extends BasicAtomGenerator
                                implements IGenerator {

    public MergeAtomsGenerator() {}

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
    	ElementGroup selectionElements = new ElementGroup();
    	double radius = model.getHighlightDistance() / model.getScale();
    	radius /= 2.0;
    	for(IAtom atom : model.getMerge().keySet()){
    		Point2d p1 = atom.getPoint2d();
    		Point2d p2 = model.getMerge().get( atom ).getPoint2d();

            // the element size has to be scaled to model space
            // so that it can be scaled back to screen space...
    		PathBuilder pb = new PathBuilder();
    		pb.color( model.getHoverOverColor() );

    		Vector2d vec = new Vector2d();
    		vec.sub( p2, p1 );

    		Vector2d per = GeometryTools.calculatePerpendicularUnitVector( p1, p2 );
    		per.scale( radius );
    		Vector2d per2 = new Vector2d();
    		per2.scale( -1 ,per);

    		Vector2d v1= new Vector2d(vec);
    		Vector2d v2= new Vector2d();

    		v1.normalize();
    		v1.scale( radius );
    		v2.scale( -1, v1 );

    		Point2d f1 = new Point2d();
    		Point2d f2 = new Point2d();
    		Point2d f3 = new Point2d();
    		Point2d s1 = new Point2d();
    		Point2d s2 = new Point2d();
    		Point2d s3 = new Point2d();

    		f1.add( p1, per );
    		f2.add( p1 , v2 );
    		f3.add( p1, per2 );

    		s1.add(p2, per2);
    		s2.add(p2, v1);
    		s3.add( p2, per );


    		pb.moveTo(f1).quadTo( f2,f3 ).lineTo( s1 ).quadTo( s2, s3 ).close();

    		selectionElements.add(pb.createPath());

        }

        return selectionElements;
    }
}