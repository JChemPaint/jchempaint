/**
 *  Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) Project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.jchempaint.renderer.visitor;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ArrowElement;
import org.openscience.jchempaint.renderer.elements.AtomSymbolElement;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.LineElement;
import org.openscience.jchempaint.renderer.elements.OvalElement;
import org.openscience.jchempaint.renderer.elements.PathElement;
import org.openscience.jchempaint.renderer.elements.RectangleElement;
import org.openscience.jchempaint.renderer.elements.TextElement;
import org.openscience.jchempaint.renderer.elements.TextGroupElement;
import org.openscience.jchempaint.renderer.elements.WedgeLineElement;
import org.openscience.jchempaint.renderer.elements.WigglyLineElement;
import org.openscience.jchempaint.renderer.font.IFontManager;

/**
 * @author maclean
 * @cdk.module rendersvg
 * @cdk.bug 2403250
 */
public class SVGGenerator implements IDrawVisitor {

    /**
     * The renderer model cannot be set by the constructor as it needs to
     * be managed by the Renderer.
     */
	private RendererModel rendererModel;
	
	public static final String HEADER = "<?xml version=\"1.0\"?>\n" +
			"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n" +
			"\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
			"<svg xmlns=\"http://www.w3.org/2000/svg\" " +
			"width=\"1000\" height=\"600\">";

	private final StringBuffer svg = new StringBuffer();

	private AffineTransform transform;

	public SVGGenerator() {
		svg.append(SVGGenerator.HEADER);
	}

	private void newline() {
		svg.append("\n");
	}

	public String getResult() {
		newline();
		svg.append("</svg>");
		return svg.toString();
	}

	public int[] transformPoint(double x, double y) {
        double[] src = new double[] {x, y};
        double[] dest = new double[2];
        this.transform.transform(src, 0, dest, 0, 1);
        return new int[] { (int) dest[0], (int) dest[1] };
    }

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}

	public void visit(ElementGroup group) {
		group.visitChildren(this);
	}

	public void visit(WedgeLineElement wedge) {
        // make the vector normal to the wedge axis
        Vector2d normal = 
            new Vector2d(wedge.y1 - wedge.y2, wedge.x2 - wedge.x1);
        normal.normalize();
        normal.scale(rendererModel.getWedgeWidth() / rendererModel.getScale());  
        
        // make the triangle corners
        Point2d vertexA = new Point2d(wedge.x1, wedge.y1);
        Point2d vertexB = new Point2d(wedge.x2, wedge.y2);
        Point2d vertexC = new Point2d(vertexB);
        vertexB.add(normal);
        vertexC.sub(normal);
        if (wedge.wedgeType==0) {
            this.drawDashedWedge(vertexA, vertexB, vertexC);
        } else if (wedge.wedgeType==1) {
            this.drawFilledWedge(vertexA, vertexB, vertexC);
        } else {
        	this.drawCrissCrossWedge(vertexA, vertexB, vertexC);
        }
	}
	
	    public void visit(WigglyLineElement wedge) {
		    	//TODO add code. see http://www.w3.org/TR/SVG/paths.html#PathDataCurveCommands
		    }
	
	    private void drawCrissCrossWedge(Point2d vertexA, Point2d vertexB,
						Point2d vertexC) {
			        
			        // calculate the distances between lines
			        double distance = vertexB.distance(vertexA);
			        double gapFactor = 0.1;
			        double gap = distance * gapFactor;
			        double numberOfDashes = distance / gap;
			        double d = 0;
			        int[] old=null;
			        
			        // draw by interpolating along the edges of the triangle
			        for (int i = 0; i < numberOfDashes; i++) {
			            double d2 = d-gapFactor;
			            Point2d p1 = new Point2d();
			            p1.interpolate(vertexA, vertexB, d);
			            Point2d p2 = new Point2d();
			            p2.interpolate(vertexA, vertexC, d2);
			            int[] p1T = this.transformPoint(p1.x, p1.y);
			            int[] p2T = this.transformPoint(p2.x, p2.y);
			    		svg.append(String.format(
								"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
								"style=\"stroke:black; stroke-width:1px;\" />",
								p1T[0],
								p1T[1],
								p2T[0],
								p2T[1]
								));
			            if(old==null)
			            	old = p2T;
			    		svg.append(String.format(
								"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
								"style=\"stroke:black; stroke-width:1px;\" />",
								old[0],
								old[1],
								p2T[0],
								p2T[1]
								));
			            old = p1T;
			            if (distance * (d + gapFactor) >= distance) {
			                break;
			            } else {
			                d += gapFactor*2;
			            }
			        }
				}

	
    private void drawFilledWedge(
            Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        int[] pB = this.transformPoint(vertexB.x, vertexB.y);
        int[] pC = this.transformPoint(vertexC.x, vertexC.y);
        int[] pA = this.transformPoint(vertexA.x, vertexA.y);
        
		svg.append(String.format(
				"<polygon points=\"%s,%s %s,%s %s,%s\" "+
					"style=\"fill:black;"+
					"stroke:black;stroke-width:1\"/>", 
				pB[0],pB[1],
				pC[0],pC[1],
				pA[0],pA[1]
				));
    }

	public void visit(PathElement path) {

	}

	public void visit(LineElement line) {
		newline();

		int[] p1 = transformPoint(line.x1, line.y1);
		int[] p2 = transformPoint(line.x2, line.y2);
		svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:1px;\" />",
					p1[0],
					p1[1],
					p2[0],
					p2[1]
					));
	}
	
    private void drawDashedWedge(
            Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        
        // calculate the distances between lines
        double distance = vertexB.distance(vertexA);
        double gapFactor = 0.1;
        double gap = distance * gapFactor;
        double numberOfDashes = distance / gap;
        double d = 0;
        
        // draw by interpolating along the edges of the triangle
        for (int i = 0; i < numberOfDashes; i++) {
            Point2d p1 = new Point2d();
            p1.interpolate(vertexA, vertexB, d);
            Point2d p2 = new Point2d();
            p2.interpolate(vertexA, vertexC, d);
            int[] p1T = this.transformPoint(p1.x, p1.y);
            int[] p2T = this.transformPoint(p2.x, p2.y);
    		svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:1px;\" />",
					p1T[0],
					p1T[1],
					p2T[0],
					p2T[1]
					));
            
            if (distance * (d + gapFactor) >= distance) {
                break;
            } else {
                d += gapFactor;
            }
        }
    }

	public void visit(OvalElement oval) {
		newline();
		int[] p1 = transformPoint(oval.x - oval.radius, oval.y - oval.radius);
		int[] p2 = transformPoint(oval.x + oval.radius, oval.y + oval.radius);
		int r = (p2[0] - p1[0]) / 2;
		svg.append(String.format(
				"<ellipse cx=\"%s\" cy=\"%s\" rx=\"%s\" ry=\"%s\" " +
				"style=\"stroke:black; stroke-width:1px; fill:none;\" />",
				p1[0] + r, p1[1] + r, r, r));
	}

	public void visit(AtomSymbolElement atomSymbol) {
		newline();
		int[] p = transformPoint(atomSymbol.x, atomSymbol.y);
		svg.append(String.format(
				"<text x=\"%s\" y=\"%s\" style=\"fill:%s\"" +
				">%s</text>",
				p[0],
				p[1],
				toColorString(atomSymbol.color),
				atomSymbol.text
				));
	}

	// this is a stupid method, but no idea how else to do it...
	private String toColorString(Color color) {
		if (color == Color.RED) {
			return "red";
		} else if (color == Color.BLUE) {
			return "blue";
		} else {
			return "black";
		}
	}

	public void visit(TextElement textElement) {
		newline();
		int[] p = transformPoint(textElement.x, textElement.y);
		svg.append(String.format(
				"<text x=\"%s\" y=\"%s\">%s</text>",
				p[0],
				p[1],
				textElement.text
				));
	}
	
	public void visit(TextGroupElement textGroup) {
		//TODO add code here
	}
	
    public void visit(ArrowElement line) {
      
        int w = (int) (line.width * this.rendererModel.getScale());
        int[] a = this.transformPoint(line.x1, line.y1);
        int[] b = this.transformPoint(line.x2, line.y2);
        newline();
		svg.append(String.format(
				"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
				"style=\"stroke:black; stroke-width:"+w+"px;\" />",
				a[0],
				a[1],
				b[0],
				b[1]
				));
        double aW = rendererModel.getArrowHeadWidth() / rendererModel.getScale();
        if(line.direction){
	        int[] c = this.transformPoint(line.x1-aW, line.y1-aW);
	        int[] d = this.transformPoint(line.x1-aW, line.y1+aW);
	        newline();
			svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:"+w+"px;\" />",
					a[0],
					a[1],
					c[0],
					c[1]
					));
			newline();
			svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:"+w+"px;\" />",
					a[0],
					a[1],
					d[0],
					d[1]
					));
        }else{
	        int[] c = this.transformPoint(line.x2+aW, line.y2-aW);
	        int[] d = this.transformPoint(line.x2+aW, line.y2+aW);
	        newline();
			svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:"+w+"px;\" />",
					a[0],
					a[1],
					c[0],
					c[1]
					));
			newline();
			svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:"+w+"px;\" />",
					a[0],
					a[1],
					d[0],
					d[1]
					));
        }        
    }


	public void visit(RectangleElement rectangleElement) {
        int[] pA = this.transformPoint(rectangleElement.x, rectangleElement.y);
        int[] pB = this.transformPoint(rectangleElement.x+rectangleElement.width, rectangleElement.y);
        int[] pC = this.transformPoint(rectangleElement.x, rectangleElement.y+rectangleElement.height);
        int[] pD = this.transformPoint(rectangleElement.x+rectangleElement.width, rectangleElement.y+rectangleElement.height);
        
        newline();
		svg.append(String.format(
				"<polyline points=\"%s,%s %s,%s %s,%s %s,%s %s,%s\""+
					"style=\"fill:none;"+
					"stroke:black;stroke-width:1\"/>", 
				pA[0],pA[1],
				pB[0],pB[1],
				pD[0],pD[1],
				pC[0],pC[1],
				pA[0],pA[1]
				));

	}

	public void visit(IRenderingElement element) {
		if (element instanceof ElementGroup)
			visit((ElementGroup) element);
		else if (element instanceof WedgeLineElement)
			visit((WedgeLineElement) element);
		else if (element instanceof LineElement)
			visit((LineElement) element);
        else if (element instanceof ArrowElement)
            visit((ArrowElement) element);
        else if (element instanceof WigglyLineElement)
        	visit((WigglyLineElement) element);
        else if (element instanceof OvalElement)
			visit((OvalElement) element);
        else if (element instanceof TextGroupElement)
            visit((TextGroupElement) element);
		else if (element instanceof AtomSymbolElement)
			visit((AtomSymbolElement) element);
		else if (element instanceof TextElement)
			visit((TextElement) element);
		else if (element instanceof RectangleElement)
			visit((RectangleElement) element);
		else if (element instanceof PathElement)
			visit((PathElement) element);
		else
			System.err.println("Visitor method for "
					+ element.getClass().getName() + " is not implemented");
	}

    public void setFontManager(IFontManager fontManager) {
        // FIXME : what kind of font management does SVG really need?
    }

    public void setRendererModel(RendererModel rendererModel) {
        this.rendererModel = rendererModel;
    }

}
