/*
 *  Copyright (C) 2025 John Mayfield
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
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
package org.openscience.jchempaint.controller;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

import java.util.List;

import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;
import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;

/**
 * Utilities for pushing bond orders around a molecule, for example,
 * alternating Kekulé/tautomeric forms etc.
 *
 * @author john
 */
public class ConjugationTools {

    /**
     * Check if two bonds are alternating, that is one is single and the other
     * is double.
     * @param a first bond
     * @param b second bond
     * @return one bond is single, one bond is double
     */
    private static boolean isAlternating(IBond a, IBond b) {
        return a.getOrder() == SINGLE && b.getOrder() == DOUBLE ||
               a.getOrder() == DOUBLE && b.getOrder() == SINGLE;
    }

    /**
     * A depth-limited depth-first search that find alternating cycle of bonds.
     *
     * @param bonds (output) - the path found
     * @param root the root atom to find a cycle to
     * @param atom the current atom in the search
     * @param prev the previous bond
     * @param limit remaining limit on the search (decrements with each descent)
     * @return a path was found (result stored in bonds param) or not
     */
    public static boolean searchAlternating(List<IBond> bonds,
                                            IAtom root,
                                            IAtom atom,
                                            IBond prev,
                                            int limit) {
        if (atom.equals(root))
            return true;
        if (limit == 0)
            return false;

        for (IBond bond : atom.bonds()) {
            if (bond.equals(prev))
                continue;
            if (bond.is(IChemObject.VISITED))
                continue;
            if (isAlternating(prev, bond)) {
                bond.set(IChemObject.VISITED);
                bonds.add(bond);
                if (searchAlternating(bonds,
                                      root,
                                      bond.getOther(atom),
                                      bond,
                                      limit - 1))
                    return true;
                bonds.remove(bonds.size() - 1);
                bond.clear(IChemObject.VISITED);
            }
        }

        return false;
    }

    /**
     * Clear all the visit flags.
     * @param container the container
     */
    private static void clearBondVisitFlags(IAtomContainer container) {
        for (IBond b : container.bonds())
            b.clear(IChemObject.VISITED);
    }

    /**
     * Find an alternating path of bonds (single,double,single,double,etc) that
     * starts with the provided bond. If no such path exists (or at least not
     * one short enough to be considered reasonable) false is returned.
     *
     * @param bonds (output) - the path found
     * @return a path was found (result stored in bonds param) or not found
     */
    public static boolean findAlternating(List<IBond> bonds, IBond bond) {

        clearBondVisitFlags(bond.getContainer());

        // iteratively deepening search, most cases will be six member rings
        // so we check those first
        bond.set(IChemObject.VISITED);
        bonds.add(bond);
        if (searchAlternating(bonds, bond.getBegin(), bond.getEnd(), bond, 6))
            return true;
        if (searchAlternating(bonds, bond.getBegin(), bond.getEnd(), bond, 10))
            return true;
        if (searchAlternating(bonds, bond.getBegin(), bond.getEnd(), bond, 14))
            return true;
        if (searchAlternating(bonds, bond.getBegin(), bond.getEnd(), bond, 34))
            return true;
        bond.clear(IChemObject.VISITED);
        return false;
    }
}
