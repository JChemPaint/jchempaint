package org.openscience.jchempaint.inchi;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.IOException;

/**
 * @author John May
 */
public final class StdInChIHandler implements InChIHandler {

    @Override public IAtomContainer parse(InChI inchi) throws CDKException {
        try {
            return new StdInChIParser().parseInchi(inchi.getInChI());
        } catch (Exception e) {
            throw new CDKException("Could not parse InChI", e);
        }
    }

    @Override public InChI generate(IAtomContainer container) throws CDKException {
        try {
            return new StdInChIGenerator().generateInchi(container);
        } catch (CDKException e) {
            throw new CDKException("Could not generate InChI", e);
        } catch (IOException e) {
            throw new CDKException("Could not generate InChI", e);
        }
    }
}
