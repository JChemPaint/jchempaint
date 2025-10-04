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
package org.openscience.jchempaint.controller;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.selection.AbstractSelection;
import org.openscience.jchempaint.renderer.BoundsCalculator;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;
import org.openscience.jchempaint.renderer.selection.ShapeSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

/**
 * An abstract base class for select modules. Classes extending this must
 * implement getDrawModeString and set selection to the appropriate type in
 * their constructor.
 *
 * @cdk.module controlbasic
 */
public abstract class AbstractSelectModule extends ControllerModuleAdapter {

    protected ShapeSelection selection;
    private Point2d startPoint;
    private boolean init; // ensure initialization only happens once
    private IChemObjectSelection currentSelection;
    private String ID;

    public AbstractSelectModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
    }

    private IAtom getClosestSelAtom(Point2d worldCoord) {
        IAtom closestAtom = null;
        IChemObjectSelection sel = chemModelRelay.getRenderer().getRenderer2DModel().getSelection();
        double closestDistanceSQ = Double.MAX_VALUE;

        for (IAtom atom : sel.elements(IAtom.class)) {
            if (atom.getPoint2d() != null) {
                double distanceSQ = atom.getPoint2d().distanceSquared(
                        worldCoord);
                if (distanceSQ < closestDistanceSQ) {
                    closestAtom = atom;
                    closestDistanceSQ = distanceSQ;
                }
            }
        }
        return closestAtom;
    }

    private IBond getClosestSelBond(Point2d worldCoord) {
        IBond closestBond = null;
        IChemObjectSelection sel = chemModelRelay.getRenderer().getRenderer2DModel().getSelection();
        double closestDistanceSQ = Double.MAX_VALUE;

        for (IBond bond : sel.elements(IBond.class)) {
            if (bond.get2DCenter() != null) {
                double distanceSQ = bond.get2DCenter().distanceSquared(
                        worldCoord);
                if (distanceSQ < closestDistanceSQ) {
                    closestBond = bond;
                    closestDistanceSQ = distanceSQ;
                }
            }
        }
        return closestBond;
    }


    public void mouseDrag(Point2d from, Point2d to, int modifiers) {

        boolean shiftPressed = (modifiers & MouseEvent.SHIFT_DOWN_MASK) != 0;

        Rectangle2D bounds = null;
        boolean inSelectionCircle = false;
        JChemPaintRendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
        IChemObjectSelection sel = model.getSelection();
        if (sel == null) return;
        double d = model.getSelectionRadius() / model.getScale();
        IAtom closestAtom = null;
        IBond closestBond = null;


        // start of the drag
        if (init && from.equals(startPoint)) {
            init = false;

            LogicalSelection lsel = null;
            boolean isAllSelected = false;
            if (sel.getClass().isAssignableFrom(LogicalSelection.class)) {
                lsel = (LogicalSelection) sel;
                isAllSelected = lsel.getType() == LogicalSelection.Type.ALL
                                && sel.isFilled();
            }
            if (!isAllSelected && sel != null && sel.isFilled()) {
                bounds = BoundsCalculator.calculateBounds(sel.getConnectedAtomContainer());
                closestAtom = getClosestSelAtom(startPoint);
                closestBond = getClosestSelBond(startPoint);
            }

            inSelectionCircle =
                    (closestAtom != null && closestAtom.getPoint2d().distance(startPoint) < 4 * d)
                    || (closestBond != null && closestBond.get2DCenter().distance(startPoint) < 4 * d);

            //in case the user either starts dragging inside the current selection
            //rectangle or in a selection circle, we switch to move mode,
            //else we start a new selection
            if ((inSelectionCircle || isAllSelected
                 || (bounds != null
                     && bounds.contains(new Point2D.Double(startPoint.x, startPoint.y))))) {
                IControllerModule newActiveModule = new MoveModule(this.chemModelRelay, this);
                newActiveModule.setID("move");
                chemModelRelay.setActiveDrawModule(newActiveModule);
                Point2d sf = chemModelRelay.getRenderer().toScreenCoordinates(startPoint.x, startPoint.y);
                ((IMouseEventRelay) this.chemModelRelay).mouseClickedDown((int) sf.x, (int) sf.y);
                Point2d st = chemModelRelay.getRenderer().toScreenCoordinates(to.x, to.y);
                ((IMouseEventRelay) this.chemModelRelay).mouseDrag((int) sf.x, (int) sf.y, (int) st.x, (int) st.y, modifiers);
                return;
            }
        }

        this.selection.addPoint(to);
        this.selection.select(chemModelRelay.getIChemModel());
        if (shiftPressed)
            this.selection.difference(currentSelection);

        this.chemModelRelay.select((IChemObjectSelection) selection);
        this.chemModelRelay.updateView();
    }

    public void mouseMove(Point2d p) {
        showMouseCursor(p, this.chemModelRelay);
    }

    public static void showMouseCursor(Point2d p, IChemModelRelay chemModelRelay) {


        IChemModelRelay.CursorType cursor = getSelectionControlType(p, chemModelRelay);
        if (cursor != IChemModelRelay.CursorType.DEFAULT) {
            chemModelRelay.setCursor(cursor);
            return;
        }

        //We look if we the user would move when clicking and show arrows then
        Rectangle2D bounds = null;
        if (chemModelRelay.getRenderer().getRenderer2DModel().getSelection() != null
            && chemModelRelay.getRenderer().getRenderer2DModel()
                             .getSelection().isFilled())
            try {
                bounds = BoundsCalculator.calculateBounds(chemModelRelay
                                                                  .getRenderer().getRenderer2DModel().getSelection()
                                                                  .getConnectedAtomContainer());
            } catch (NullPointerException e) {

                bounds = null;
            }
        IChemObject highlightedAtom = chemModelRelay.getRenderer().getRenderer2DModel().getHighlightedAtom();
        IChemObject highlightedBond = chemModelRelay.getRenderer().getRenderer2DModel().getHighlightedBond();
        if ((bounds != null && bounds.contains(new Point2D.Double(p.x, p.y)))
            || highlightedAtom != null || highlightedBond != null) {
            chemModelRelay.setCursor(Cursor.HAND_CURSOR);
        } else {
            chemModelRelay.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    @Override
    public void mouseClickedDown(Point2d p, int modifiers) {

        if (getSelectionControlType(p, chemModelRelay) != IChemModelRelay.CursorType.DEFAULT) {
            IControllerModule newActiveModule = new MoveModule(this.chemModelRelay, this);
            newActiveModule.setID("Move");
            chemModelRelay.setActiveDrawModule(newActiveModule);
            Point2d sf = chemModelRelay.getRenderer().toScreenCoordinates(p.x, p.y);
            ((IMouseEventRelay) this.chemModelRelay).mouseClickedDown((int) sf.x, (int) sf.y, modifiers);
            return;
        }

        init = true;
        startPoint = p;
        IChemObjectSelection chemObjSelection = chemModelRelay.getRenderer().getRenderer2DModel().getSelection();
        currentSelection = chemObjSelection != null ? new LogicalSelection(chemObjSelection) : null;
    }

    protected static IChemModelRelay.CursorType getSelectionControlType(Point2d p, IChemModelRelay relay) {
        JChemPaintRendererModel jcpModel = relay.getRenderer().getRenderer2DModel();
        RectangleElement rect = jcpModel.getSelectionBounds();
        if (rect == null)
            return IChemModelRelay.CursorType.DEFAULT;
        if (rect.width <= 0.001 || rect.height >= -0.001 ||
            jcpModel.getSelection().elements(IAtom.class).size() == 1)
            return IChemModelRelay.CursorType.DEFAULT;

        double hgDist = (jcpModel.getHighlightDistance() / jcpModel.getScale() / jcpModel.getZoomFactor());

        Point2d rotateControl = jcpModel.getSelectionRotateControl();
        if (rotateControl != null &&
            rotateControl.distance(p) <= hgDist)
            return IChemModelRelay.CursorType.ROTATE;

        // do the point testing towards the outside of the rect, basically we
        // expand by half the highlight distance, this allows single bonds to
        // still be controlled
        rect = new RectangleElement(rect.xCoord - hgDist / 2,
                                    rect.yCoord + hgDist / 2,
                                    rect.width + hgDist,
                                    rect.height - hgDist,
                                    false,
                                    rect.color);

        Point2d nw = new Point2d(rect.xCoord, rect.yCoord);
        Point2d ne = new Point2d(rect.xCoord + rect.width, rect.yCoord);
        Point2d sw = new Point2d(rect.xCoord, rect.yCoord + rect.height);
        Point2d se = new Point2d(rect.xCoord + rect.width, rect.yCoord + rect.height);
        if (p.distance(sw) <= hgDist)
            return IChemModelRelay.CursorType.RESIZE_SW;
        if (p.distance(se) <= hgDist)
            return IChemModelRelay.CursorType.RESIZE_SE;
        if (p.distance(ne) <= hgDist)
            return IChemModelRelay.CursorType.RESIZE_NE;
        if (p.distance(nw) <= hgDist)
            return IChemModelRelay.CursorType.RESIZE_NW;

        Point2d n = new Point2d(rect.xCoord + (rect.width / 2), rect.yCoord);
        Point2d e = new Point2d(rect.xCoord + rect.width, rect.yCoord + (rect.height / 2));
        Point2d s = new Point2d(rect.xCoord + (rect.width / 2), rect.yCoord + rect.height);
        Point2d w = new Point2d(rect.xCoord, rect.yCoord + (rect.height / 2));
        if (p.distance(n) <= hgDist)
            return IChemModelRelay.CursorType.RESIZE_N;
        if (p.distance(e) <= hgDist)
            return IChemModelRelay.CursorType.RESIZE_E;
        if (p.distance(s) <= hgDist)
            return IChemModelRelay.CursorType.RESIZE_S;
        if (p.distance(w) <= hgDist)
            return IChemModelRelay.CursorType.RESIZE_W;

        return IChemModelRelay.CursorType.DEFAULT;
    }

    @Override
    public void mouseClickedUp(Point2d p, int modifiers) {

        boolean shiftPressed = (modifiers & MouseEvent.SHIFT_DOWN_MASK) != 0;

        if (p.equals(startPoint)) {

            // there was no mouse drag so we need to do the selection logic here

            IAtom closestAtom = chemModelRelay.getClosestAtom(p);
            IBond closestBond = chemModelRelay.getClosestBond(p);
            IChemObject singleSelection = getHighlighted(p,
                                                         closestAtom,
                                                         closestBond);

            IChemObjectSelection currentSelection = chemModelRelay.getRenderer().getRenderer2DModel().getSelection();
            IChemObjectSelection sel = null;
            if (shiftPressed && currentSelection != null && currentSelection.isFilled()) {
                Set<IChemObject> chemObjSet = new HashSet<>(currentSelection.elements(IChemObject.class));
                if (!chemObjSet.contains(singleSelection))
                    chemObjSet.add(singleSelection);
                else
                    chemObjSet.remove(singleSelection);
                LogicalSelection logSel = new LogicalSelection(LogicalSelection.Type.ALL);
                logSel.select(chemObjSet);
                sel = logSel;
            } else {
                if (singleSelection == null) {
                    selection.clear();
                    sel = AbstractSelection.EMPTY_SELECTION;
                } else if (singleSelection instanceof IAtom) {
                    sel = new SingleSelection<>((IAtom) singleSelection);
                } else if (singleSelection instanceof IBond) {
                    sel = new SingleSelection<>((IBond) singleSelection);
                }
            }
            this.chemModelRelay.select(sel);
        } else {
            this.selection.select(chemModelRelay.getIChemModel());
            if (shiftPressed)
                this.selection.difference(currentSelection);
            this.chemModelRelay.select((IChemObjectSelection) selection);
            selection.reset();
        }

        this.chemModelRelay.updateView();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    protected void setSelection(IChemObjectSelection selection) {
        super.setSelection(selection);
        startPoint = null;
        init = false;
    }
}
