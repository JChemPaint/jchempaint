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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;

/**
 * 
 * @author maclean
 * @cdk.module rendercontrol
 */
public class LogicalSelection implements IChemObjectSelection {
    
    public enum Type { ALL, NONE };
    
    private Type type;
    
    private IChemModel chemModel;
    
    public LogicalSelection(LogicalSelection.Type type) {
        this.type = type;
    }

    public void clear() {
        this.type = Type.NONE;
        this.chemModel = null;
    }

    public IRenderingElement generate(Color color) {
        return null;
    }

    public IAtomContainer getConnectedAtomContainer() {
        if (this.chemModel != null) {
            IAtomContainer ac = this.chemModel.getBuilder().newAtomContainer();
            for (IAtomContainer other : 
                ChemModelManipulator.getAllAtomContainers(chemModel)) {
                ac.add(other);
            }
            return ac;
        }
        return null;
    }

    public boolean isFilled() {
        return this.chemModel != null;
    }

    public boolean isFinished() {
        return true;
    }

    public void select(IChemModel chemModel) {
        if (this.type == Type.ALL) { 
            this.chemModel = chemModel;
        }
    }
    
    public void select(IAtomContainer atomContainer) {
        this.chemModel = atomContainer.getBuilder().newChemModel();
        IMoleculeSet molSet = atomContainer.getBuilder().newMoleculeSet();
        molSet.addAtomContainer(atomContainer);
        this.chemModel.setMoleculeSet(molSet);
    }

    public boolean contains( IChemObject obj ) {
        if(type == Type.NONE)
            return false;
        
        for(IAtomContainer other:
                    ChemModelManipulator.getAllAtomContainers( chemModel )) {
            if(other == obj) return true;
            
            if(obj instanceof IBond)
                if( other.contains( (IBond) obj)) return true;
            if(obj instanceof IAtom)
                if( other.contains( (IAtom) obj)) return true;
        }
        return false;
    }
    
    public <E extends IChemObject> Collection<E> elements(Class<E> clazz){
        throw new UnsupportedOperationException();
    }
}
