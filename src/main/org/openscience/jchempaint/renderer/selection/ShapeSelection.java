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
package org.openscience.jchempaint.renderer.selection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;

/**
 * @cdk.module rendercontrol
 */
public abstract class ShapeSelection implements IncrementalSelection {
    
    public final List<IAtom> atoms = new ArrayList<IAtom>();
    
    public final List<IBond> bonds = new ArrayList<IBond>();
    
    protected boolean finished = false;
    
    public abstract boolean contains(Point2d p);
    
    public abstract void addPoint(Point2d p);
    
    public abstract boolean isEmpty();
    
    /**
     * Call this after the drawing has finished
     */
    public abstract void reset();
    
    public abstract IRenderingElement generate(Color color);
    
    public boolean isFinished() {
        return this.finished;
    }
    
    public boolean contains( IChemObject obj ) {
        if(obj instanceof IAtom) {
            for(IChemObject selected:atoms) {
                if(selected == obj)
                    return true;
            }
        }
        if(obj instanceof IBond) {
            for(IChemObject selected:bonds) {
                if(selected == obj)
                    return true;
            } 
        }
        return false;
    }
    
    /**
     * Call this before starting a new selection.
     */
    public void clear() {
        this.atoms.clear();
        this.bonds.clear();
        this.finished = false;
    }
    
    public boolean isFilled() {
        return !atoms.isEmpty() || !bonds.isEmpty();
    }
    
    /* 
     * Get an IAtomContainer where all the bonds have atoms in 
     * the AtomContainer (no dangling bonds).
     * 
     * (non-Javadoc)
     * @see org.openscience.cdk.renderer.ISelection#getConnectedAtomContainer()
     */
    public IAtomContainer getConnectedAtomContainer() {
        // you should really have used the isFilled method...
        if (atoms.size() == 0) return null;
        
        IAtomContainer ac = atoms.get(0).getBuilder().newAtomContainer();
        for (IAtom atom : atoms) {
            ac.addAtom(atom);
        }
        
        for (IBond bond : bonds) {
            boolean addBond = true;
            for (IAtom atom : bond.atoms()) {
                if (!ac.contains(atom)) addBond = false;
            }
            
            if (addBond) {
                ac.addBond(bond);
            }
        }
        
        return ac;
    }
    
    private void select(IAtomContainer atomContainer) {
    	
        for (IAtom atom : atomContainer.atoms()) {
            if (contains(atom.getPoint2d()) && !atoms.contains(atom)) {
                atoms.add(atom); 
             }
        }
        
        for (IBond bond : atomContainer.bonds()) {
            if (contains(bond.get2DCenter()) && !bonds.contains(bond)) {
                bonds.add(bond); 
             }
        }
    }
    
    public void select(IChemModel chemModel) {
        clear();
        for (IAtomContainer atomContainer : 
            ChemModelManipulator.getAllAtomContainers(chemModel)) {
            select(atomContainer);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <E extends IChemObject> Collection<E> elements(Class<E> clazz){
        Set<E> set = new HashSet<E>();
        if(clazz.isAssignableFrom( IChemObject.class )) {
            set.addAll( (Collection<? extends E>) atoms );
            set.addAll( (Collection<? extends E>) bonds );
            return set;
        }
        if(clazz.isAssignableFrom( IAtom.class )) {
            set.addAll( (Collection<? extends E>) atoms );
            return set;
        }
        if(clazz.isAssignableFrom( IBond.class )) {
            set.addAll( (Collection<? extends E>) bonds );
            return set;
        }
        return Collections.emptySet();
    }
}
