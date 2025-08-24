/* $Revision$ $Author$ $Date$
*
*  Copyright (C) 2008 Gilleain Torrance <gilleain.torrance@gmail.com>
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

package org.openscience.jchempaint.renderer.visitor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.cdk.renderer.elements.ArrowElement;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.PathElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.jchempaint.renderer.elements.TextElement;
import org.openscience.jchempaint.renderer.elements.TextGroupElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.jchempaint.renderer.elements.WigglyLineElement;
import org.openscience.cdk.renderer.elements.path.Type;


/**
 * @cdk.module renderawt
 */
public class AWTDrawVisitor extends AbstractAWTDrawVisitor {

    /**
     * The font manager cannot be set by the constructor as it needs to
     * be managed by the Renderer.
     */
    private AWTFontManager fontManager;

    /**
     * The renderer model cannot be set by the constructor as it needs to
     * be managed by the Renderer.
     */
    private JChemPaintRendererModel rendererModel;

    private final Map<Integer, BasicStroke> strokeMap =
            new HashMap<Integer, BasicStroke>();

    private final Map<TextAttribute, Object> map =
            new Hashtable<TextAttribute, Object>();

    private final Graphics2D g;

    public AWTDrawVisitor(Graphics2D g) {
        this.g = g;
        this.fontManager = null;
        this.rendererModel = null;
        this.fm = g.getFontMetrics();

        map.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
    }

    public void visitElementGroup(ElementGroup elementGroup) {
        elementGroup.visitChildren(this);
    }

    public void visit(ElementGroup elementGroup) {
        elementGroup.visitChildren(this);
    }

    public void visit(ArrowElement line) {
        Stroke savedStroke = this.g.getStroke();

        int w = (int) (line.width * this.transform.getScaleX());
        if (strokeMap.containsKey(w)) {
            this.g.setStroke(strokeMap.get(w));
        }
        else {
            BasicStroke stroke = new BasicStroke(w);
            this.g.setStroke(stroke);
            strokeMap.put(w, stroke);
        }
        this.g.setColor(line.color);
        int[] a = this.transformPoint(line.startX, line.startY);
        int[] b = this.transformPoint(line.endX, line.endY);
        this.g.drawLine(a[0], a[1], b[0], b[1]);
        double arrowWidth = rendererModel.getArrowHeadWidth() / rendererModel.getScale();
        double lenghtOfArrow = Math.sqrt(Math.pow(Math.abs(line.startX - line.endX), 2) + Math.pow(Math.abs(line.startY - line.endY), 2));
        double fractionOfHead = arrowWidth / lenghtOfArrow;
        //headpoint is a line on the arrow arrowWidth away from end
        Point2d headPoint = new Point2d();
        if (line.startX < line.endX)
            headPoint.x = line.startX + (line.endX - line.startX) * (fractionOfHead);
        else
            headPoint.x = line.endX + (line.startX - line.endX) * (1 - fractionOfHead);
        if (line.startY < line.endY)
            headPoint.y = line.startY + (line.endY - line.startY) * (fractionOfHead);
        else
            headPoint.y = line.endY + (line.startY - line.endY) * (1 - fractionOfHead);
        //rotate headpoint in both directions to get end points of arrow
        double relativex = headPoint.x - line.startX;
        double relativey = headPoint.y - line.startY;
        double angle = Math.PI / 6;
        double costheta = Math.cos(angle);
        double sintheta = Math.sin(angle);
        Point2d firstArrowPoint = new Point2d();
        firstArrowPoint.x = relativex * costheta - relativey * sintheta + line.startX;
        firstArrowPoint.y = relativex * sintheta + relativey * costheta + line.startY;
        int[] firstArrowPointCoords = this.transformPoint(firstArrowPoint.x, firstArrowPoint.y);
        this.g.drawLine(a[0], a[1], firstArrowPointCoords[0], firstArrowPointCoords[1]);
        angle = -Math.PI / 6;
        costheta = Math.cos(angle);
        sintheta = Math.sin(angle);
        Point2d secondArrowPoint = new Point2d();
        secondArrowPoint.x = relativex * costheta - relativey * sintheta + line.startX;
        secondArrowPoint.y = relativex * sintheta + relativey * costheta + line.startY;
        int[] secondArrowPointCoords = this.transformPoint(secondArrowPoint.x, secondArrowPoint.y);
        this.g.drawLine(a[0], a[1], secondArrowPointCoords[0], secondArrowPointCoords[1]);
        this.g.setStroke(savedStroke);
    }
    
    
    public void visit(LineElement line) {
        Stroke savedStroke = this.g.getStroke();
        
        double w = line.width * this.transform.getScaleX();
//        if (w <= 1f) w = 1; // mine stoke of 1
        int key = (int)(10*w);

        if (strokeMap.containsKey(key)) {
            this.g.setStroke(strokeMap.get(key));
        } else {
            BasicStroke stroke = new BasicStroke((float)w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            this.g.setStroke(stroke);
            strokeMap.put(key, stroke);
        }
        
        this.g.setColor(line.color);
        double[] a = this.transformPointD(line.firstPointX, line.firstPointY);
        double[] b = this.transformPointD(line.secondPointX, line.secondPointY);
        this.g.draw(new Line2D.Double(a[0], a[1], b[0], b[1]));
        this.g.setStroke(savedStroke);
    }

    public void visit(OvalElement oval) {
        this.g.setColor(oval.color);
        int[] min = 
            this.transformPoint(oval.xCoord - oval.radius, oval.yCoord - oval.radius);
        int[] max =                                              
            this.transformPoint(oval.xCoord + oval.radius, oval.yCoord + oval.radius);
        int w = max[0] - min[0];
        int h = min[1] - max[1];
        if (oval.fill) {
            this.g.fillOval(min[0], max[1], w, h);
        } else {
            this.g.drawOval(min[0], max[1], w, h);
        }
    }

    public void visit(TextElement textElement) {
        this.g.setFont(this.fontManager.getFont());
        Point p = this.getTextBasePoint(
                textElement.text, textElement.x, textElement.y, g);
        Rectangle2D textBounds =
                this.getTextBounds(
                        textElement.text, textElement.x, textElement.y, g, 
                        rendererModel.getZoomFactor());
        
        this.g.setColor(textElement.backColor!=null ? textElement.backColor : this.rendererModel.getBackColor());
        this.g.fill(textBounds);
        this.g.setColor(textElement.color);
        if (textElement.extraZoom!=null)
        	this.g.setFont(new Font (this.g.getFont().getName(), 1, (int)(this.g.getFont().getSize()*textElement.extraZoom))) ;
        this.g.drawString(textElement.text, p.x, p.y);
        
    }
    
    public void visit(WedgeLineElement wedge) {
        // make the vector normal to the wedge axis
        Vector2d normal = 
            new Vector2d(wedge.firstPointY - wedge.secondPointY, wedge.secondPointX - wedge.firstPointX);
        normal.normalize();
        normal.scale(rendererModel.getWedgeWidth() / rendererModel.getScale());  
        
        // make the triangle corners
        Point2d vertexA = new Point2d(wedge.firstPointX, wedge.firstPointY);
        Point2d vertexB = new Point2d(wedge.secondPointX, wedge.secondPointY);
        Point2d vertexC = new Point2d(vertexB);
        vertexB.add(normal);
        vertexC.sub(normal);
        this.g.setColor(wedge.color);
        if (wedge.type == WedgeLineElement.TYPE.DASHED) {
            this.drawDashedWedge(vertexA, vertexB, vertexC);
        } else if(wedge.type == WedgeLineElement.TYPE.WEDGED){
            this.drawFilledWedge(vertexA, vertexB, vertexC);
        } else {
        	this.drawCrissCrossWedge(vertexA, vertexB, vertexC);
        }
    }
    
    public void visit(WigglyLineElement wedge) {
        // make the endpoints
        Point2d vertexA = new Point2d(wedge.firstPointX, wedge.firstPointY);
        Point2d vertexB = new Point2d(wedge.secondPointX, wedge.secondPointY);
        this.g.setColor(wedge.color);
        
        // store the current stroke
        Stroke storedStroke = this.g.getStroke();
        this.g.setStroke(new BasicStroke(1));
        
        // calculate the distances between circles
        double distance = vertexB.distance(vertexA);
        double gapFactor = 0.1;
        double gap = distance * gapFactor;
        double numberOfCircles = distance*5;
        
        int diameter = (int)(rendererModel.getBondLength()*rendererModel.getZoomFactor()*1.2*gapFactor)+2;
        double d = 0;
        double rad=BondTools.giveAngleBothMethods(new Point2d(wedge.firstPointX,wedge.firstPointY), new Point2d(wedge.firstPointX+100,wedge.firstPointY), new Point2d(wedge.secondPointX,wedge.secondPointY), true);
        int degrees=(int)(360*(rad/(2*Math.PI)));
        // draw by interpolating along the imaginary straight line
        for (int i = 0; i < numberOfCircles; i++) {
            Point2d p1 = new Point2d();
            p1.interpolate(vertexA, vertexB, d);
            Point2d p2 = new Point2d();
            p2.interpolate(vertexA, vertexB, d+1/numberOfCircles);
            
            int[] p1T = this.transformPoint(p1.x, p1.y);
            int[] p2T = this.transformPoint(p2.x, p2.y);
            int wh = (int) (new Point2d(p1T[0],p1T[1]).distance(new Point2d(p2T[0],p2T[1])));

            this.g.drawArc(p1T[0]-diameter, p1T[1]-diameter/2, wh,wh, i % 2 == 0 ? degrees : (degrees+180)%360, 180);

            if (distance * (d + gapFactor) >= distance) {
                break;
            } else {
                d += 1/numberOfCircles;
            }
        }
        this.g.setStroke(storedStroke);
        
    }

    private void drawCrissCrossWedge(Point2d vertexA, Point2d vertexB,
			Point2d vertexC) {
        // store the current stroke
        Stroke storedStroke = this.g.getStroke();
        this.g.setStroke(new BasicStroke(1));
        
        // calculate the distances between lines
        double distance = vertexB.distance(vertexA);
        double gapFactor = 0.1;
        double gap = distance * gapFactor;
        double numberOfDashes = distance / gap;
        double d = gapFactor;
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
            this.g.drawLine(p1T[0], p1T[1], p2T[0], p2T[1]);
            if(old==null)
            	old = p2T;
            this.g.drawLine(old[0], old[1], p2T[0], p2T[1]);
            old = p1T;
            if (distance * (d + gapFactor) >= distance) {
                break;
            } else {
                d += gapFactor*2;
            }
        }
        this.g.setStroke(storedStroke);
	}

	private void drawFilledWedge(
            Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        int[] pB = this.transformPoint(vertexB.x, vertexB.y);
        int[] pC = this.transformPoint(vertexC.x, vertexC.y);
        int[] pA = this.transformPoint(vertexA.x, vertexA.y);
        
        int[] xs = new int[] { pB[0], pC[0], pA[0] };
        int[] ys = new int[] { pB[1], pC[1], pA[1] };
        this.g.fillPolygon(xs, ys, 3);
    }
    
    private void drawDashedWedge(
            Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        // store the current stroke
        Stroke storedStroke = this.g.getStroke();
        this.g.setStroke(new BasicStroke(1));
        
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
            this.g.drawLine(p1T[0], p1T[1], p2T[0], p2T[1]);
            if (distance * (d + gapFactor) >= distance) {
                break;
            } else {
                d += gapFactor;
            }
        }
        this.g.setStroke(storedStroke);
    }
    
    public void visit(AtomSymbolElement atomSymbol) {
        this.g.setFont(this.fontManager.getFont());
        Point p = 
            super.getTextBasePoint(
                    atomSymbol.text, atomSymbol.xCoord, atomSymbol.yCoord, g);
        Rectangle2D textBounds = 
            this.getTextBounds(atomSymbol.text, atomSymbol.xCoord, atomSymbol.yCoord, g, 
                    rendererModel.getZoomFactor());
        this.g.setColor(this.rendererModel.getBackColor());
        this.g.fill(textBounds);
        this.g.setColor(atomSymbol.color);
        this.g.drawString(atomSymbol.text, p.x, p.y);
        
        int offset = 10;    // XXX
        String chargeString;
        if (atomSymbol.formalCharge == 0) {
            return;
        } else if (atomSymbol.formalCharge == 1) {
            chargeString = "+";
        } else if (atomSymbol.formalCharge > 1) {
            chargeString = atomSymbol.formalCharge + "+";
        } else if (atomSymbol.formalCharge == -1) {
            chargeString = "-";
        } else if (atomSymbol.formalCharge < -1) {
            int absCharge = Math.abs(atomSymbol.formalCharge);
            chargeString = absCharge + "-";
        } else {
            return;
        }
       
        int x = (int) textBounds.getCenterX();
        int y = (int) textBounds.getCenterY();
        if (atomSymbol.alignment == 1) {           // RIGHT
            this.g.drawString(
                    chargeString, x + offset, (int)textBounds.getMinY());
        } else if (atomSymbol.alignment == -1) {   // LEFT
            this.g.drawString(
                    chargeString, x - offset, (int)textBounds.getMinY());
        } else if (atomSymbol.alignment == 2) {    // TOP
            this.g.drawString(
                    chargeString, x, y - offset);
        } else if (atomSymbol.alignment == -2) {   // BOT
            this.g.drawString(
                    chargeString, x, y + offset);
        }

    }
    
    public void visit(RectangleElement rectangle) {
        int[] p1 = this.transformPoint(rectangle.xCoord, rectangle.yCoord);
        int[] p2 = this.transformPoint(rectangle.xCoord + rectangle.width, rectangle.yCoord + rectangle.height);
        this.g.setColor(rectangle.color);
        if (rectangle.filled) {
            this.g.fill(new Rectangle2D.Double(p1[0], p1[1], p2[0] - p1[0], p2[1] - p1[1]));
        } else {
            this.g.draw(new Rectangle2D.Double(p1[0], p1[1], p2[0] - p1[0], p2[1] - p1[1]));
        }
    }
    
    public void visit(PathElement path) {
        this.g.setColor(path.color);
        for (int i = 1; i < path.points.size(); i++) {
            Point2d point1 = path.points.get(i - 1);
            Point2d point2 = path.points.get(i);
            int[] p1 = this.transformPoint(point1.x, point1.y);
            int[] p2 = this.transformPoint(point2.x, point2.y);
            this.g.drawLine(p1[0], p1[1], p2[0], p2[1]);
        }
    }
    
    public void visit(GeneralPath path) {
        this.g.setColor(path.color);
        Path2D cpy = new Path2D.Double();
        cpy.append(getPathIterator(path, transform), false);

        if (path.fill) {
            this.g.fill(cpy);
        } else {
            Stroke stroke = this.g.getStroke();
            this.g.setStroke(new BasicStroke((float) (path.stroke * transform.getScaleX()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            this.g.draw(cpy);
            this.g.setStroke(stroke);
        }
    }

    private static PathIterator getPathIterator(final GeneralPath path,
            final AffineTransform transform) {
        return new PathIterator() {

            int index;

            private int type(Type type) {
                switch ( type ) {
                    case MoveTo: return SEG_MOVETO;
                    case LineTo: return SEG_LINETO;
                    case QuadTo: return SEG_QUADTO;
                    case CubicTo: return SEG_CUBICTO;
                    case Close: return SEG_CLOSE;
                    default: return SEG_CLOSE;
                }
            }
            public void next() {
                index++;
            }

            public boolean isDone() {
                return index>= path.elements.size();
            }

            public int getWindingRule() {
                return path.winding;
            }

            public int currentSegment(double[] coords) {
                path.elements.get(index).points(coords);
                transform.transform(coords, 0, coords, 0, 3);
                return type(path.elements.get(index).type);
            }

            public int currentSegment( float[] coords ) {
                double[] doubleCoords = new double[6];
                int type = currentSegment(doubleCoords);
                for (int i = 0; i < 5; i++)
                    coords[i] = (float) doubleCoords[i];
                return type;
            }
        };
    }

    public void visit(TextGroupElement textGroup) {
        this.g.setFont(this.fontManager.getFont());
        Point p = 
            super.getTextBasePoint(
                    textGroup.text, textGroup.x, textGroup.y, g);
        Rectangle2D textBounds = 
            this.getTextBounds(textGroup.text, textGroup.x, textGroup.y, g, 
                    rendererModel.getZoomFactor());
        this.g.setColor(textGroup.backColor!=null ? textGroup.backColor : this.rendererModel.getBackColor());
        this.g.fill(textBounds);
        this.g.setColor(textGroup.color);
        this.g.drawString(textGroup.text, p.x, p.y);
        
        int x = (int) textBounds.getCenterX();
        int y = (int) textBounds.getCenterY();
        int x1 = (int) textBounds.getMinX();
        int y1 = (int) textBounds.getMinY();
        int x2 = p.x + (int)textBounds.getWidth();
        int y2 = (int) textBounds.getMaxY();

        for (TextGroupElement.Child child : textGroup.children) {
            //First we calculate the child bounds just to find width and height
            Rectangle2D childBounds = getTextBounds(child.text, 0, 0, g, 
                    rendererModel.getZoomFactor());
            int oW = (int)childBounds.getWidth();
            int oH = (int)childBounds.getHeight();

            //use that to actually calculate the position
            int cx;
            int cy;
            
            switch (child.position) {
                case NE:
                    cx = x2;
                    cy = y1;
                    break;
                case N:
                    cx = x1;
                    cy = y1;
                    break;
                case NW:
                    cx = x1 - oW;
                    cy = y1;
                    break;
                case W:
                    cx = x1 - oW;
                    cy = p.y;
                    break;
                case SW:
                    cx = x1 - oW;
                    cy = y1 + oH;
                    break;
                case S:
                    cx = x1;
                    cy = y2 + oH;
                    break;
                case SE:
                    cx = x2;
                    cy = y2 + oH;
                    break;
                case E:
                    cx = x2;
                    cy = p.y;
                    break;
                default:
                    cx = x;
                    cy = y;
                    break;
            }

            if (child.isComment) {
	            this.g.setColor(Color.BLACK);
                Font f = this.g.getFont();
	            Font commentFont = f.deriveFont(f.getStyle(), f.getSize()/2);
                this.g.setFont(commentFont);
                cx = x1;
                cy = y2 + oH/2;
                this.g.drawString(child.text, cx, cy);
            }
            else {
                //for deleting background of child
                //we need the bounds at the actual positions
                childBounds = getTextBounds(child.text, cx, cy, g
                        , rendererModel.getZoomFactor());
                this.g.setColor(textGroup.backColor!=null ? textGroup.backColor : this.rendererModel.getBackColor());
                Rectangle2D childBackground = new Rectangle2D.Double(cx,
                        cy - childBounds.getHeight(), childBounds.getWidth(), 
                        childBounds.getHeight());
                this.g.fill(childBackground);

            	this.g.setColor(textGroup.color);
	            //write child
	            this.g.drawString(child.text, cx, cy);
	            if (child.subscript != null) {
	                int scx = (int)(cx + (childBounds.getWidth() * 0.75));
	                int scy = (int)(cy + (childBounds.getHeight() / 3));
	                Font f = this.g.getFont();   // TODO : move to font manager
	                Font subscriptFont = f.deriveFont(f.getStyle(), f.getSize() - 2);
	                this.g.setFont(subscriptFont);
	                this.g.setColor(textGroup.color);
	                //write subscript
	                this.g.drawString(child.subscript, scx, scy);
	            }
            }
        }
        if(textGroup.isNotTypeableUnderlined){
            this.g.setColor(Color.RED);
            this.g.drawLine(x1,y2,x2,y2);
        }
    }

    public void visit(IRenderingElement element) {
        Color savedColor = this.g.getColor();
        if (element instanceof ElementGroup)
            visit((ElementGroup) element);
        else if (element instanceof WedgeLineElement)
            visit((WedgeLineElement) element);
        else if (element instanceof WigglyLineElement)
            visit((WigglyLineElement) element);
        else if (element instanceof LineElement)
            visit((LineElement) element);
        else if (element instanceof ArrowElement)
            visit((ArrowElement) element);
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
        else if (element instanceof GeneralPath)
            visit((GeneralPath)element);
        else if (element instanceof Bounds) {
            // ignore
        } else if (element instanceof MarkedElement) {
            visit(((MarkedElement) element).element());
        } else
            System.err.println("Visitor method for "
                    + element.getClass().getName() + " is not implemented");
        this.g.setColor(savedColor);
    }

    /**
     * The font manager must be set by any renderer that uses this class! 
     */
    public void setFontManager(IFontManager fontManager) {
        this.fontManager = (AWTFontManager) fontManager;
    }

    public void setRendererModel(JChemPaintRendererModel rendererModel) {
        this.rendererModel = rendererModel;
        if (rendererModel.getUseAntiAliasing()) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
//            g.setStroke(new BasicStroke((int)rendererModel.getBondWidth()));
        }
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                           RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                           RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                           RenderingHints.VALUE_STROKE_NORMALIZE);
        
    }
}
