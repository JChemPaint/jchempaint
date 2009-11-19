/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;

/**
 * @cdk.module  rendercontrol
 */
public class ControllerFeedbackGenerator {

	private RendererModel model;
	
    public ControllerFeedbackGenerator(IAtomContainer ac, RendererModel r2dm) {
    	this.model = r2dm;
    }
    
    public IRenderingElement generate(IAtomContainer ac, IAtom atom) {
        if (this.model.getHighlightedAtom() == atom) {
			return generateHighlightElement(atom);
        }
        return null;
    }

    private IRenderingElement generateHighlightElement( IAtom atom ) {

        // create highlight base on symbol
        // would be nice to attach it to a AtomSymbolElement and use it's 
        // data to calculate the surrounding highlight or change the text's
        // appearance 
        return null;
    }
}
