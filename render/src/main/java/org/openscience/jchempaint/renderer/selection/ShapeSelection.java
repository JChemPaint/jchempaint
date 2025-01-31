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

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @cdk.module rendercontrol
 */
public abstract class ShapeSelection implements IncrementalSelection {

    public final Set<IAtom> atoms = new HashSet<>();

    public final Set<IBond> bonds = new HashSet<>();

    protected boolean finished = false;

    public abstract boolean contains(Point2d p);

    public abstract void addPoint(Point2d p);

    public abstract boolean isEmpty();

    public void addAtom(IAtom atom) {
        atoms.add(atom);
    }

    public void addBond(IBond bond) {
        bonds.add(bond);
    }

    /**
     * Call this after the drawing has finished
     */
    public abstract void reset();

    public abstract IRenderingElement generate(Color color);

    public boolean isFinished() {
        return this.finished;
    }

    public boolean contains(IChemObject obj) {
        if (obj instanceof IAtom) {
            return atoms.contains(obj);
        }
        if (obj instanceof IBond) {
            return bonds.contains(obj);
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

        IAtomContainer ac = null;
        for (IAtom atom : atoms) {
            if (ac == null) ac = atom.getBuilder().newAtomContainer();
            ac.addAtom(atom);
        }

        if (ac == null)
            return new AtomContainer();

        for (IBond bond : bonds) {
            if (ac.contains(bond.getBegin()) && ac.contains(bond.getEnd()))
                ac.addBond(bond);
        }

        return ac;
    }

    private void select(IAtomContainer atomContainer) {

        for (IAtom atom : atomContainer.atoms()) {
            if (contains(atom.getPoint2d())) {
                atoms.add(atom);
            }
        }

        for (IBond bond : atomContainer.bonds()) {
            if (contains(bond.getAtom(0).getPoint2d())
                && contains(bond.getAtom(1).getPoint2d())) {
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

    public void difference(IChemObjectSelection selection) {
        for (IAtom atom : selection.elements(IAtom.class)) {
            if (atoms.contains(atom))
                atoms.remove(atom);
            else
                atoms.add(atom);
        }
        for (IBond bond : selection.elements(IBond.class)) {
            if (bonds.contains(bond))
                bonds.remove(bond);
            else
                bonds.add(bond);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends IChemObject> Collection<E> elements(Class<E> clazz) {
        Set<E> set = new HashSet<E>();
        if (IAtom.class.isAssignableFrom(clazz)) {
            set.addAll((Collection<? extends E>) atoms);
            return set;
        }
        if (IBond.class.isAssignableFrom(clazz)) {
            set.addAll((Collection<? extends E>) bonds);
            return set;
        }
        if (IChemObject.class.isAssignableFrom(clazz)) {
            set.addAll((Collection<? extends E>) atoms);
            set.addAll((Collection<? extends E>) bonds);
            return set;
        }
        return Collections.emptySet();
    }
}
