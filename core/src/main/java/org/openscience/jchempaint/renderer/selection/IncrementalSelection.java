/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2009 Arvid Berg <goglepox@users.sf.net>
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

import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;

/**
 * @author Arvid
 * @cdk.module rendercontrol
 */
public interface IncrementalSelection extends IChemObjectSelection{


    /**
     * Use this to check if the selection process has finished.
     * Some implementing classes may just choose to return 'true'
     * if their selection is a simple one-step process.
     *
     * @return true if the selection process is complete
     */
    public boolean isFinished();

    /**
     * Generate a display element that represents this selection.
     * This will be used while isFilled() && !isFinished().
     *
     * @param color the color of the element to generate.
     * @return a rendering element for display purposes.
     */
    public IRenderingElement generate(Color color);
}
