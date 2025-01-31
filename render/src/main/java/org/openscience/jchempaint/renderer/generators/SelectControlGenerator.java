/*
 *  Copyright (C) 2025 John Mayfield
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.util.List;

import static org.openscience.cdk.renderer.elements.RectangleElement.createSquare;

/**
 * Renders the selection control points. These allow rotating, scaling of
 * the selected atoms/bonds.
 *
 * @cdk.module rendercontrol
 */
public class SelectControlGenerator implements IGenerator<IAtomContainer> {

    private boolean autoUpdateSelection = true;

    public SelectControlGenerator() {}

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        JChemPaintRendererModel jcpModel = (JChemPaintRendererModel) model;
        Color selectionColor = jcpModel.getSelectedPartColor();
        IChemObjectSelection selection = jcpModel.getSelection();
        return generate(selection, selectionColor, jcpModel);
    }
    
    protected IRenderingElement generate(IChemObjectSelection selection, Color color, JChemPaintRendererModel model){
        ElementGroup selectionControls = new ElementGroup();
        if(selection==null)
            return selectionControls;
        if (this.autoUpdateSelection || selection.isFilled()) {
            // we also divide by the zoom so the control points stay the same size
            double r = (model.getSelectionRadius() / model.getScale()) / model.getZoomFactor();
            if (!model.isRotating()) {
                RectangleElement box = model.getSelectionBounds();
                selectionControls.add(box);

                if (box.width > 0.001 && box.height < -0.001 &&
                    selection.elements(IAtom.class).size() != 1) {

                    // rotation handle
                    Point2d p = model.getSelectionRotateControl();
                    if (p != null) {
                        selectionControls.add(new OvalElement(p.x, p.y, r, false, color));
                        selectionControls.add(new LineElement(p.x, p.y - r,
                                                              box.xCoord + (box.width / 2),
                                                              box.yCoord,
                                                              1 / model.getScale(),
                                                              color));
                    }

                    // box on the four corners
                    selectionControls.add(createSquare(box.xCoord, box.yCoord, r, true, color));
                    selectionControls.add(createSquare(box.xCoord + box.width, box.yCoord, r, true, color));
                    selectionControls.add(createSquare(box.xCoord, box.yCoord + box.height, r, true, color));
                    selectionControls.add(createSquare(box.xCoord + box.width, box.yCoord + box.height, r, true, color));

                    // box on four sides
                    double cx = box.xCoord + (box.width/2);
                    double cy = box.yCoord + (box.height/2);
                    selectionControls.add(createSquare(cx, box.yCoord, r, true, color));
                    selectionControls.add(createSquare(cx, box.yCoord + box.height, r, true, color));
                    selectionControls.add(createSquare(box.xCoord, cy, r, true, color));
                    selectionControls.add(createSquare(box.xCoord + box.width, cy, r, true, color));
                }
            }
        }
        return selectionControls;
    }

    public List<IGeneratorParameter<?>> getParameters() {
        return null;
    }
}
