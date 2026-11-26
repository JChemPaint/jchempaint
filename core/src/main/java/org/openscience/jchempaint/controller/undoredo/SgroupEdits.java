/*
 *  Copyright (C) 2026 John Mayfield
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
package org.openscience.jchempaint.controller.undoredo;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.IChemModelRelay;

import java.util.Collection;

/**
 * Collection of edits and utilities and edits for undo/redo of Sgroup.
 */
public class SgroupEdits {

    /**
     * Utility - remove the given Sgroups from a IChemModel.
     * @param model the model
     * @param toRemove the sgroups to remove
     */
    public static void remove(IChemModel model, Collection<Sgroup> toRemove) {
        for (IAtomContainer container : model.getMoleculeSet()) {
            Sgroup.remove(container, toRemove);
        }
    }

    /**
     * Utility - add the given Sgroups to a IChemModel.
     * @param model the model
     * @param toAdd the sgroups to add
     */
    public static void add(IChemModel model, Collection<Sgroup> toAdd) {
        for (Sgroup sgroup : toAdd) {
            IAtomContainer container = null;
            // what if the atoms are in different containers!?! will be fixed
            // when we are a single monolithic container
            for (IAtom atom : sgroup.getAtoms()) {
                container = ChemModelManipulator.getRelevantAtomContainer(model, atom);
                if (container != null) {
                    Sgroup.add(container, sgroup);
                    break;
                }
            }

        }
    }

    
    public static IUndoRedoable expansionToggle(Collection<Sgroup> sgroups) {
        return new IUndoRedoable() {
            private void toggle() {
                for (Sgroup sgroup : sgroups) {
                    Boolean expansion = sgroup.getValue(SgroupKey.CtabExpansion);
                    if (expansion == null) expansion = false;
                    sgroup.putValue(SgroupKey.CtabExpansion, !expansion);
                }
            }

            @Override
            public void redo() {
                toggle();
            }

            @Override
            public void undo() {
                toggle();
            }
        };
    }

    public static IUndoRedoable remove(IChemModelRelay relay,
                                       Collection<Sgroup> sgroups) {
        return new IUndoRedoable() {
            @Override
            public void redo() {
                remove(relay.getChemModel(), sgroups);
            }

            @Override
            public void undo() {
                add(relay.getChemModel(), sgroups);
            }
        };
    }

    public static IUndoRedoable add(IChemModelRelay relay,
                                    Collection<Sgroup> sgroups) {
        return new IUndoRedoable() {
            @Override
            public void redo() {
                add(relay.getChemModel(), sgroups);
            }

            @Override
            public void undo() {
                remove(relay.getChemModel(), sgroups);
            }
        };
    }
}
