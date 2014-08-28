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
package org.openscience.jchempaint.renderer.generators;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;
import org.openscience.jchempaint.renderer.selection.IncrementalSelection;

import java.awt.Color;
import java.util.List;

/**
 * Generate the outline of the selection tool (e.g. rectangle or lasso).
 */
public class SelectionToolGenerator implements IGenerator<IAtomContainer> {

    public SelectionToolGenerator() {
    }

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        return generate(model.getSelection(), ((JChemPaintRendererModel) model).getSelectedPartColor());
    }

    protected IRenderingElement generate(IChemObjectSelection selection, Color selectionColor) {
        ElementGroup selectionElements = new ElementGroup();

        if (selection == null)
            return selectionElements;

        if (selection instanceof IncrementalSelection) {
            IncrementalSelection sel = (IncrementalSelection) selection;
            if (!sel.isFinished())
                selectionElements.add(sel.generate(selectionColor));
        }
        return selectionElements;
    }

    public List<IGeneratorParameter<?>> getParameters() {
        // TODO Auto-generated method stub
        return null;
    }
}
