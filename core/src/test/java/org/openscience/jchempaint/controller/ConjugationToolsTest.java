package org.openscience.jchempaint.controller;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;
import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;

public class ConjugationToolsTest {

    private static void assertBondOrders(IAtomContainer mol, IBond.Order ... orders) {
        List<IBond.Order> actual   = new ArrayList<>();
        List<IBond.Order> expected = Arrays.asList(orders);
        for (IBond bond : mol.bonds()) {
            actual.add(bond.getOrder());
        }
        Assert.assertEquals(expected, actual);
    }

    private static void flip(IBond bond) {
        if (bond.getOrder() == SINGLE)
            bond.setOrder(DOUBLE);
        else if (bond.getOrder() == DOUBLE)
            bond.setOrder(SINGLE);
        else throw new IllegalArgumentException("Not single or double!");
    }


    private static void flip(List<IBond> bonds) {
        for (IBond bond : bonds)
           flip(bond);
    }

    @Test
    public void testBenzeneStartWithDouble() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1=CC=CC=C1");
        assertBondOrders(mol,
                         DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
        List<IBond> path = new ArrayList<>();
        Assert.assertTrue(ConjugationTools.findAlternating(path, mol.getBond(0)));
        flip(path);
        assertBondOrders(mol,
                         SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE);

    }

    @Test
    public void testBenzeneStartWithSingle() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1=CC=CC=C1");
        assertBondOrders(mol,
                         DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
        List<IBond> path = new ArrayList<>();
        Assert.assertTrue(ConjugationTools.findAlternating(path, mol.getBond(1)));
        flip(path);
        assertBondOrders(mol,
                         SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE);

    }

    @Test
    public void testLargeRing() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1=CC=CC=CC=CC=CC=C1");
        assertBondOrders(mol,
                         DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
        List<IBond> path = new ArrayList<>();
        Assert.assertTrue(ConjugationTools.findAlternating(path, mol.getBond(0)));
        flip(path);
        assertBondOrders(mol,
                         SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE);

    }

    @Test
    public void testNotPossible() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1=CCCC=C1");
        assertBondOrders(mol,
                         DOUBLE, SINGLE, SINGLE, SINGLE, DOUBLE, SINGLE);
        List<IBond> path = new ArrayList<>();
        Assert.assertFalse(ConjugationTools.findAlternating(path, mol.getBond(0)));
    }

    @Test
    public void testNotPossible2() throws InvalidSmilesException {
        // alternating path but the start and end are both double so not okay
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1=CC=CC=CC=1");
        assertBondOrders(mol,
                         DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE);
        List<IBond> path = new ArrayList<>();
        Assert.assertFalse(ConjugationTools.findAlternating(path, mol.getBond(0)));
    }

    @Test
    public void test15Tautomer() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("OC1=CC=NC=C1");
        assertBondOrders(mol,
                         SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
        List<IBond> path = new ArrayList<>();
        Assert.assertFalse(ConjugationTools.findAlternating(path, mol.getBond(0)));
        Assert.assertTrue(ConjugationTools.findTautomerShift(path, mol.getBond(0)));
        flip(path);
        assertBondOrders(mol,
                         DOUBLE, SINGLE, DOUBLE, SINGLE, SINGLE, DOUBLE, SINGLE);

    }

    @Test
    public void test15Tautomer2() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C1C=CNC=C1");
        assertBondOrders(mol,
                         DOUBLE, SINGLE, DOUBLE, SINGLE, SINGLE, DOUBLE, SINGLE);
        List<IBond> path = new ArrayList<>();
        Assert.assertFalse(ConjugationTools.findAlternating(path, mol.getBond(0)));
        Assert.assertTrue(ConjugationTools.findTautomerShift(path, mol.getBond(0)));
        flip(path);
        assertBondOrders(mol,
                         SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);

    }

    @Test
    public void testNonTautomerShift() throws InvalidSmilesException {
        // alternating path but the start and end are both single so not okay
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("OC1=CC=CN=C1");
        assertBondOrders(mol,
                         SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
        List<IBond> path = new ArrayList<>();
        Assert.assertFalse(ConjugationTools.findAlternating(path, mol.getBond(0)));
        Assert.assertFalse(ConjugationTools.findTautomerShift(path, mol.getBond(0)));
        flip(path);

    }

}