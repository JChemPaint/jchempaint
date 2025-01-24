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
     *
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
     * @param root  the root atom to find a cycle to
     * @param atom  the current atom in the search
     * @param prev  the previous bond
     * @param limit remaining limit on the search (decrements with each descent)
     * @return a path was found (result stored in bonds param) or not
     */
    private static boolean searchAlternatingCycle(List<IBond> bonds,
                                                  IAtom root,
                                                  IAtom atom,
                                                  IBond prev,
                                                  int limit) {
        if (atom.equals(root) &&
            isAlternating(prev, bonds.get(0))) {
            return true;
        }
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
                if (searchAlternatingCycle(bonds,
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
     *
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

        bonds.clear();
        clearBondVisitFlags(bond.getContainer());

        // iteratively deepening search, most cases will be six member rings
        // so we check those first
        bond.set(IChemObject.VISITED);
        bonds.add(bond);
        if (searchAlternatingCycle(bonds, bond.getBegin(), bond.getEnd(), bond, 6))
            return true;
        if (searchAlternatingCycle(bonds, bond.getBegin(), bond.getEnd(), bond, 10))
            return true;
        if (searchAlternatingCycle(bonds, bond.getBegin(), bond.getEnd(), bond, 14))
            return true;
        if (searchAlternatingCycle(bonds, bond.getBegin(), bond.getEnd(), bond, 34))
            return true;
        bond.clear(IChemObject.VISITED);
        return false;
    }

    /**
     * Possible tautomer roles.
     */
    private enum TautRole {
        CanAcceptH,
        CanDonateH,
        Other;

        /**
         * Can the opposite role, e.g. Donor opposite is Acceptor,
         * Acceptor opposite is Donor. Other is Other.
         * @return the opposite role
         */
        TautRole flip() {
            switch (this) {
                case CanDonateH:
                    return CanAcceptH;
                case CanAcceptH:
                    return CanDonateH;
                default:
                    return Other;
            }
        }
    }

    /**
     * Safely access the atom's implicit hydrogen count.
     * @param atom the atom
     * @return the implicit H count (or 0 if null)
     */
    private static int safeImplH(IAtom atom) {
        return atom.getImplicitHydrogenCount() != null
               ? atom.getImplicitHydrogenCount()
               : 0;
    }

    /**
     * Safely access the atom's charge.
     * @param atom the atom
     * @return the charge (or 0 if null)
     */

    private static int charge(IAtom atom) {
        return atom.getFormalCharge() != null
               ? atom.getFormalCharge()
               : 0;
    }

    private static int packedBondInfo(IAtom atom) {
        int result = safeImplH(atom);
        for (IBond bond : atom.bonds()) {
            switch (bond.getOrder()) {
                case SINGLE:
                    result += 0x0001;
                    break;
                case DOUBLE:
                    result += 0x0010;
                    break;
                case TRIPLE:
                    result += 0x0100;
                    break;
                default:
                    result += 0x1000;
                    break;
            }
        }
        return result;
    }

    /**
     * Simple but efficient tautomer role determination.
     * @param atom the atom to type
     * @return the role
     */
    private static TautRole getType(IAtom atom) {
        final int h = safeImplH(atom);
        final int q = charge(atom);
        switch (atom.getAtomicNumber()) {
            case IAtom.N:
            case IAtom.P:
            case IAtom.As:
                // *=NH
                if (h == 1 && q == 0 &&
                    packedBondInfo(atom) == 0x11)
                    return TautRole.CanAcceptH;
                // *-NH2
                if (h == 2 && q == 0 &&
                    packedBondInfo(atom) == 0x3)
                    return TautRole.CanDonateH;
                // *-NH-*
                if (h == 1 && q == 0 &&
                    packedBondInfo(atom) == 0x3)
                    return TautRole.CanDonateH;
                // *-N=*
                if (h == 0 && q == 0 &&
                    packedBondInfo(atom) == 0x11)
                    return TautRole.CanAcceptH;
                break;
            case IAtom.O:
            case IAtom.S:
            case IAtom.Se:
            case IAtom.Te:
                // *-OH
                if (h == 1 && q == 0 &&
                    packedBondInfo(atom) == 0x2)
                    return TautRole.CanDonateH;
                // *=O
                if (h == 0 && q == 0 &&
                    packedBondInfo(atom) == 0x10)
                    return TautRole.CanAcceptH;
                break;
        }
        return TautRole.Other;
    }

    /**
     * A depth-limited depth-first search that find alternating path to an atom
     * of a given tautomer role.
     *
     * @param bonds (output) - the path found
     * @param atom  the current atom in the search
     * @param prev  the previous bond
     * @param role  the role to find
     * @param limit remaining limit on the search (decrements with each descent)
     * @return a path was found (result stored in bonds param) or not
     */
    private static boolean searchForRole(List<IBond> bonds,
                                         IAtom atom,
                                         IBond prev,
                                         TautRole role,
                                         int limit) {
        if (getType(atom) == role &&
            isAlternating(prev, bonds.get(0))) {
            return true;
        }
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
                if (searchForRole(bonds,
                                  bond.getOther(atom),
                                  bond,
                                  role,
                                  limit - 1))
                    return true;
                bonds.remove(bonds.size() - 1);
                bond.clear(IChemObject.VISITED);
            }
        }

        return false;
    }

    /**
     * Find a tautomeric shift path between hetero atoms. Given a bond of which
     * one atom is a hydrogen donor or acceptor it attempts to find a path
     * (1,3-, 1,5-, 1,7, 1,9- or 1,11-) between that atom and a counter role
     * (e.g. starting at a donor we need to find an acceptor).
     *
     * @param bonds the found shift path
     * @param bond the bond to start from (one end should be a H donor/acceptor)
     * @return a shift was found
     */
    public static boolean findTautomerShift(List<IBond> bonds, IBond bond) {

        bonds.clear();

        TautRole begType = getType(bond.getBegin());
        TautRole endType = getType(bond.getEnd());

        // one of them must be a donor or acceptor, if they are both type
        // 'Other' or neither type 'Other' then we can't do anything
        if ((begType == TautRole.Other) == (endType == TautRole.Other))
            return false;

        clearBondVisitFlags(bond.getContainer());

        // iteratively deepening search, most cases will be six member rings
        // so we check those first
        bond.set(IChemObject.VISITED);
        bonds.add(bond);

        if (begType != TautRole.Other) {
            // 1-5, and 1-7 shifts
            if (searchForRole(bonds, bond.getEnd(), bond, begType.flip(), 7)) {
                return true;
            }
            // 1-9, and 1-11 shifts
            if (searchForRole(bonds, bond.getEnd(), bond, begType.flip(), 11)) {
                return true;
            }
        } else {
            // 1-5, and 1-7 shifts
            if (searchForRole(bonds, bond.getBegin(), bond, endType.flip(), 7)) {
                return true;
            }
            // 1-9, and 1-11 shifts
            if (searchForRole(bonds, bond.getBegin(), bond, endType.flip(), 11)) {
                return true;
            }
        }

        bond.clear(IChemObject.VISITED);
        return false;
    }
}
