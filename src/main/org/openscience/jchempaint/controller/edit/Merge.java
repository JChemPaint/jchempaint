/*
 * Copyright (C) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 * Copyright (C) 2005-2008 Tobias Helmus, Stefan Kuhn
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.jchempaint.controller.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.controller.Changed;

/**
* Edit representing the merger of atoms.
* @author Arvid
* @cdk.module controlbasic
*/
public class Merge extends AbstractEdit {

    Map<IAtom,IAtom> mergeModel;

    List<IBond> deletedBonds;
    Map<IBond,ReplacedAtom> modifiedBonds;

    /**
     * Creates an edit representing the merger of the atom pairs in the 
     * <code>mergeModel</code>
     * @param mergeModel <code>Map</code> representing atom pairs to be merged.
     * @return edit representing the merger of atoms in the 
     * <code>mergeModel</code>.
     */
    public static Merge merge(Map<IAtom,IAtom> mergeModel) {
        return new Merge( mergeModel );
    }

    private Merge(Map<IAtom, IAtom> mergeModel) {
        this.mergeModel = new HashMap<IAtom, IAtom>(mergeModel);
    }

    public Set<Changed> getTypeOfChanges() {
        return changed( Changed.Structure,Changed.Properties );
    }

    public void redo() {

        mergeMolecules( mergeModel );

        Collection<IAtom> atoms = new ArrayList<IAtom>();
        atoms.addAll(mergeModel.values());
        updateHydrogenCount( atoms );
    }

    public void undo() {

        for(IAtom atom: mergeModel.keySet()) {
            model.addAtom(atom);
        }
        for(IBond bond:deletedBonds) {
            model.addBond( bond );
        }
        for(IBond bond : modifiedBonds.keySet()){
            ReplacedAtom replaced = modifiedBonds.get( bond );
            bond.setAtom(replaced.oldAtom, replaced.positionInBond);
        }

        Collection<IAtom> atoms = new ArrayList<IAtom>();
        atoms.addAll( mergeModel.keySet() );
        atoms.addAll(mergeModel.values());
        updateHydrogenCount( atoms );

    }

    private static class ReplacedAtom {
        final IAtom oldAtom;
        final int positionInBond;

        public ReplacedAtom(IAtom atom,int pos) {
            this.oldAtom = atom;
            this.positionInBond = pos;
        }
    }

    /**
     * Merge parts of an atom-container.
     * @param mergeModel atoms to be merged
     */
    public void mergeMolecules(Map<IAtom,IAtom> mergeModel) {

        deletedBonds = new ArrayList<IBond>();
        modifiedBonds = new HashMap<IBond, ReplacedAtom>();
        IAtomContainer container = model;

        for(IAtom mergedAtom: mergeModel.keySet()) {
            IAtom mergedPartnerAtom = mergeModel.get( mergedAtom );

            // In the next loop we remove bonds that are redundant, that is
            // to say bonds that exist on both sides of the parts to be merged
            // and would cause duplicate bonding in the end result.

            for ( IAtom atom2 : mergeModel.keySet() ) {
                IBond bondPartner =
                                model.getBond( mergeModel.get( mergedAtom ),
                                               mergeModel.get( atom2 ) );
                if ( bondPartner != null ) {
                    IBond bond = model.getBond( mergedAtom, atom2 );
                    if(bond != null) {
                        model.removeBond( bond );
                        deletedBonds.add( bond );
                    }
                }
            }

            //After the removal of redundant bonds, the actual merge is done.
            //One half of atoms in the merge map are removed and their bonds
            //are mapped to their replacement atoms.
            for (IBond bond : container.bonds()) {
                if (bond.contains(mergedAtom)) {
                    if (bond.getAtom(0).equals(mergedAtom)) {
                        bond.setAtom(mergedPartnerAtom, 0);
                        modifiedBonds.put(bond, new ReplacedAtom( mergedAtom, 0 ));
                    } else {
                        bond.setAtom(mergedPartnerAtom, 1);
                        modifiedBonds.put(bond, new ReplacedAtom( mergedAtom, 1 ));
                    }
                }
            }
            container.removeAtom(mergedAtom);
        }
    }
}
