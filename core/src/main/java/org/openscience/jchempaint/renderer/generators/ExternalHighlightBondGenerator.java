/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Stefan Kuhn <shk3@users.sf.net>
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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.selection.AtomContainerSelection;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;

/**
 * A generator to generate the externally selected bonds in ExternalHighlightColor.
 */
public class ExternalHighlightBondGenerator extends SelectBondGenerator {

    public ExternalHighlightBondGenerator() {}

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        if(model.getExternalSelectedPart()==null)
            return new ElementGroup();
        Color selectionColor = model.getExternalHighlightColor();
        IChemObjectSelection selection = new AtomContainerSelection(model.getExternalSelectedPart());
        return generate(selection, selectionColor, model);
    }
}
