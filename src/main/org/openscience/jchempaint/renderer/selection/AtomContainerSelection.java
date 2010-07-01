/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2010 Stefan Kuhn <shk3@users.sf.net>
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

import java.util.Collection;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * This is a selection which is built from an AtomContainer and cannot be 
 * changed later.
 *
 */
public class AtomContainerSelection extends AbstractSelection {

    IAtomContainer content;
    
    /**
     * Constructor for AtomContainerSelection.
     * 
     * @param ac The selection.
     */
    public AtomContainerSelection(IAtomContainer ac){
        content=ac;
    }
    
    /* (non-Javadoc)
     * @see org.openscience.jchempaint.renderer.selection.IChemObjectSelection#contains(org.openscience.cdk.interfaces.IChemObject)
     */
    public boolean contains(IChemObject obj) {
        if(obj instanceof IAtom) {
            if(content.contains((IAtom)obj)){
                return true;
            }
        }
        if(obj instanceof IBond) {
            if(content.contains((IBond)obj)){
                return true;
            } 
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.renderer.selection.IChemObjectSelection#getConnectedAtomContainer()
     */
    public IAtomContainer getConnectedAtomContainer() {
        return content;
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.renderer.selection.IChemObjectSelection#isFilled()
     */
    public boolean isFilled() {
        if(content!=null && (content.getAtomCount()>0 || content.getBondCount()>0))
            return true;
        else
            return false;
    }

    /* (non-Javadoc)
     * @see org.openscience.jchempaint.renderer.selection.IChemObjectSelection#elements(java.lang.Class)
     */
    public <E extends IChemObject> Collection<E> elements(Class<E> clazz) {
        throw new UnsupportedOperationException();
    }
}
