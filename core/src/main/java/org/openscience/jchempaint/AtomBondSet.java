package org.openscience.jchempaint;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.ArrayList;
import java.util.List;

/**
 * CDK 2.10 onwards enforces an invariant on AtomContainer's that a bond
 * can not be stored unless it's atoms are also stored (a dangling bond).
 * You must also add atoms before bonds in AtomContainer now.
 * <br/>
 * This class allows arbitrary sets of atoms and bonds to be stored without
 * the no dangling bonds constraint. This is required because sometimes we merge
 * /delete/update bonds.
 */
public class AtomBondSet {

    private final List<IAtom> atoms = new ArrayList<>();
    private final List<IBond> bonds = new ArrayList<>();

    public AtomBondSet() {
    }

    public AtomBondSet(IAtomContainer mol) {
        for (IAtom atom : mol.atoms())
            atoms.add(AtomRef.deref(atom));
        for (IBond bond : mol.bonds())
            bonds.add(BondRef.deref(bond));
    }

    public Iterable<IAtom> atoms() {
        return atoms;
    }

    public Iterable<IBond> bonds() {
        return bonds;
    }

    public int getAtomCount() {
        return atoms.size();
    }

    public int getBondCount() {
        return bonds.size();
    }

    public void add(IAtom atom) {
        atoms.add(atom);
    }

    public void add(IBond bond) {
        bonds.add(bond);
    }

    public void remove(IBond bond) {
        bonds.remove(bond);
    }

    public void remove(IAtom atom) {
        atoms.remove(atom);
    }

    public boolean contains(IAtom atom) {
        return atoms.contains(atom);
    }

    public boolean contains(IBond bond) {
        return bonds.contains(bond);
    }

    public boolean isEmpty() {
        return atoms.isEmpty() && bonds.isEmpty();
    }

    public IAtom getSingleAtom() {
        if (atoms.size() != 1) throw new IllegalArgumentException();
        return atoms.get(0);
    }
}
