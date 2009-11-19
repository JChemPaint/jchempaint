/*
 * Copyright (C) 2009  Arvid Berg <goglepox@users.sourceforge.net>
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.controller.Changed;

/**
 * Abstract superclass of all edits.
 * @author Arvid
 * @cdk.module controlbasic
 */
public abstract class AbstractEdit implements IEdit{

    protected IAtomContainer model;
    private CDKAtomTypeMatcher matcher;
    protected IAtomContainer ac;

    /**
     * Adds <code>Changed</code> types passed in to its changed types set.
     * @param changed changes types that this edit has made
     */
    static Set<Changed> changed(Changed... changed) {
        List<Changed> ch = Arrays.asList( changed );

        Set<Changed> changes = new HashSet<Changed>();
        changes.addAll( ch );
        return changes;
    }


    /**
     * Updates atoms with respect to their hydrogen count
     *
     * @param  atomsToUpdate  The atoms that needs updating
     */
     void updateHydrogenCount( Collection<IAtom> atomsToUpdate ) {
         if(ac==null)
             return;
         for(IAtom atom : atomsToUpdate){
             if(matcher==null)
                 matcher = CDKAtomTypeMatcher.getInstance(atom.getBuilder());
             atom.setHydrogenCount(0);
             try {
                 IAtomType type = matcher.findMatchingAtomType(ac, atom);
                 if (type != null) {
                     Integer neighbourCount = type.getFormalNeighbourCount(); 
                     if (neighbourCount != null) {
                         atom.setHydrogenCount(
                                 neighbourCount
                                 - ac.getConnectedAtomsCount(atom));
                     }
                     //for some reason, the neighbour count takes into account only 
                     //one single electron
                     if(ac.getConnectedSingleElectronsCount(atom)>1 
                             && atom.getHydrogenCount()
                             -ac.getConnectedSingleElectronsCount(atom)+1>-1)
                         atom.setHydrogenCount(atom.getHydrogenCount()
                                 -ac.getConnectedSingleElectronsCount(atom)+1);
                     atom.setFlag(CDKConstants.IS_TYPEABLE, false);
                 }else{
                     atom.setFlag(CDKConstants.IS_TYPEABLE, true);
                 }
             } catch (CDKException e) {
                 e.printStackTrace();
             }
         }
    }

     void updateHydrogenCount(IBond bond) {
         updateHydrogenCount( bond.getAtom( 0 ), bond.getAtom( 1 ) );
     }
     void updateHydrogenCount( IAtom... atoms ) {
         updateHydrogenCount( Arrays.asList( atoms ) );
     }

     public void execute( IAtomContainer ac ) {
         model = ac;
         redo();
    }

     public boolean canRedo() {
         return true;
    }

     public boolean canUndo() {
         return true;
     }
}