package org.openscience.jchempaint.inchi;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A wrapper class for InChI parsing and generation.
 * 
 * @author John May
 */
public interface InChIHandler {

    /**
     * Parse an InChI input.
     *
     * @param inchi input InChI string
     * @return CDK atom container
     */
    IAtomContainer parse(InChI inchi) throws CDKException;

    /**
     * Generate an InChI output.
     *
     * @param container structure representation
     * @return the InChI string
     */
    InChI generate(IAtomContainer container) throws CDKException;
}
