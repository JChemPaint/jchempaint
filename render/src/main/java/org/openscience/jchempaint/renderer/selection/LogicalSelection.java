/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *               2009  Arvid Berg <goglepox@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.renderer.selection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.renderer.elements.IRenderingElement;

/**
 * 
 * @author maclean
 * @cdk.module rendercontrol
 */
public class LogicalSelection implements IChemObjectSelection {
    
    public enum Type { ALL, NONE };
    
    private Type type;
    
    private Set<IChemObject> selected = new HashSet<>();
    
    public LogicalSelection(LogicalSelection.Type type) {
        this.type = type;
    }

    public void clear() {
        this.type = Type.NONE;
        this.selected.clear();
    }
    
    public Type getType() {
    	return type;
    }

    public IRenderingElement generate(Color color) {
        return null;
    }

    public IAtomContainer getConnectedAtomContainer() {
        IAtomContainer result = null;
        for (IAtom atom : elements(IAtom.class)) {
            if (result == null) result = atom.getBuilder().newAtomContainer();
            result.addAtom(atom);
        }

        if (result != null) {
            for (IBond bond : elements(IBond.class)) {
                if (result.contains(bond.getBegin()) &&
                    result.contains(bond.getEnd()))
                    result.addBond(bond);
            }
        }

        return result;
    }

    public boolean isFilled() {
        return !this.selected.isEmpty();
    }

    public boolean isFinished() {
        return true;
    }

    public void select(IChemModel chemModel) {
        if (this.type == Type.ALL) {
            for (IAtomContainer mol : ChemModelManipulator.getAllAtomContainers(chemModel)) {
                for (IAtom atom : mol.atoms())
                    this.selected.add(atom);
                for (IBond bond : mol.bonds())
                    this.selected.add(bond);
            }
        }
    }
    
    public void select(Set<IChemObject> chemObjectSet) {
        this.selected.addAll(chemObjectSet);
    }

    public boolean contains( IChemObject obj ) {
        if(type == Type.NONE)
            return false;
        return selected.contains(obj);
    }

    @SuppressWarnings("unchecked")
    public <E extends IChemObject> Collection<E> elements(Class<E> clazz) {
        Collection<E> result = new ArrayList<>();
        if (IAtom.class.isAssignableFrom(clazz)) {
            for (IChemObject chemObj : selected) {
                if (chemObj instanceof IAtom)
                    result.add((E)chemObj);
            }
        }
        else if (IBond.class.isAssignableFrom(clazz)) {
            for (IChemObject chemObj : selected) {
                if (chemObj instanceof IBond)
                    result.add((E)chemObj);
            }
        }
        else if (IChemObject.class.isAssignableFrom(clazz)) {
            for (IChemObject chemObj : selected) {
                result.add((E)chemObj);
            }
        }
        return result;
    }
}
