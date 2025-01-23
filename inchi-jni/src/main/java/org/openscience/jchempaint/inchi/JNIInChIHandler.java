package org.openscience.jchempaint.inchi;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;

import io.github.dan2097.jnainchi.InchiStatus;

/**
 * InChI handler that uses JNI-InChI.
 *
 * @author John May
 */
public final class JNIInChIHandler implements InChIHandler {

    @Override public IAtomContainer parse(InChI inchi) throws CDKException {

        InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
        InChIToStructure its = igf.getInChIToStructure(inchi.getInChI(),
                                                       DefaultChemObjectBuilder.getInstance());

        InchiStatus status = its.getStatus();
        if (status != InchiStatus.SUCCESS) 
            throw new CDKException("Could not parse InChI - " + its.getMessage());

        return its.getAtomContainer();
    }

    @Override public InChI generate(IAtomContainer container) throws CDKException {
        InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
        InChIGenerator its = igf.getInChIGenerator(container);

        InchiStatus status = its.getStatus();
        if (status != InchiStatus.SUCCESS)
                    throw new CDKException("Could not generate InChI - " + its.getMessage());

        InChI inchi = new InChI();
        inchi.setInChI(its.getInchi());
        inchi.setKey(its.getInchiKey());
        inchi.setAuxInfo(its.getAuxInfo());
        return inchi;
    }
}
