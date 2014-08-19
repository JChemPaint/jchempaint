package org.openscience.jchempaint.inchi;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Entry point for InChI generation. The tool finds an available {@link
 * org.openscience.jchempaint.inchi.InChIHandler} instance to utilise.
 *
 * @author John May
 */
public final class InChITool {

    static List<InChIHandler> handlers = new ArrayList<InChIHandler>();

    static {
        for (InChIHandler handler : ServiceLoader.load(InChIHandler.class)) {
            handlers.add(handler);
        }
    }

    /**
     * Parse an InChI string and create a CDK structure representation.
     *
     * @param inchi InChI string
     * @return structure representation
     * @throws CDKException InChI could not be parsed
     */
    public static IAtomContainer parseInChI(String inchi) throws CDKException {
        assert !handlers.isEmpty();
        return handlers.get(0).parse(InChI.create(inchi));
    }

    /**
     * Generate an InChI string from a CDK structure representation.
     *
     * @param container structure representation
     * @return InChI instance
     * @throws CDKException InChI could not be generated
     */
    public static InChI generateInchi(IAtomContainer container) throws CDKException {
        assert !handlers.isEmpty();
        return handlers.get(0).generate(container);
    }

    /**
     * Load an IChemModel from an InChI.
     *
     * @param url location
     * @return InChI instance
     * @throws CDKException InChI could not be generated
     */
    public static IChemModel readInChI(URL url) throws CDKException {
        IChemModel chemModel = new ChemModel();
        try {
            IAtomContainerSet moleculeSet = new AtomContainerSet();
            chemModel.setMoleculeSet(moleculeSet);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().startsWith("inchi=")) {
                    moleculeSet.addAtomContainer(parseInChI(line));
                }
            }
            in.close();
        } catch (Exception e) {

            e.printStackTrace();
            throw new CDKException(e.getMessage());
        }
        return chemModel;
    }
}
