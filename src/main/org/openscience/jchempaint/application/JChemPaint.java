/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
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
package org.openscience.jchempaint.application;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.vecmath.Point2d;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.INChIReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.io.JCPFileFilter;

public class JChemPaint {

    public static int instancecounter = 1;

    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        try {
            String vers = System.getProperty("java.version");
            String requiredJVM = "1.5.0";
            Package self = Package.getPackage("org.openscience.jchempaint");
            String version = GT._("Could not determine JCP version");
            if (self != null)
                version = self.getImplementationVersion();
            if (vers.compareTo(requiredJVM) < 0) {
                System.err.println(GT._("WARNING: JChemPaint") + " " + version
                        + " " + GT._("must be run with a Java VM version")
                        + " " + requiredJVM + " " + GT._("or higher."));
                System.err.println(GT._("Your JVM version") + ": " + vers);
                System.exit(1);
            }

            Options options = new Options();
            options.addOption("h", "help", false, GT._("gives this help page"));
            options.addOption("v", "version", false, GT
                    ._("gives JChemPaints version number"));
            options.addOption("d", "debug", false,
                    "switches on various debug options");
            options.addOption(OptionBuilder.withArgName("property=value")
                    .hasArg().withValueSeparator().withDescription(
                            GT._("supported options are given below")).create(
                            "D"));

            CommandLine line = null;
            try {
                CommandLineParser parser = new PosixParser();
                line = parser.parse(options, args);
            } catch (UnrecognizedOptionException exception) {
                System.err.println(exception.getMessage());
                System.exit(-1);
            } catch (ParseException exception) {
                System.err.println("Unexpected exception: "
                        + exception.toString());
            }

            if (line.hasOption("v")) {
                System.out.println("JChemPaint v." + version + "\n");
                System.exit(0);
            }

            if (line.hasOption("h")) {
                System.out.println("JChemPaint v." + version + "\n");

                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("JChemPaint", options);

                // now report on the -D options
                // TODO which of these are still used?
                System.out.println();
                System.out
                        .println("The -D options are as follows (defaults in parathesis):");
                System.out.println("  cdk.debugging     [true|false] (false)");
                System.out.println("  cdk.debug.stdout  [true|false] (false)");
                System.out.println("  devel.gui         [true|false] (false)");
                System.out
                        .println("  gui               [stable|experimental] (stable)");
                System.out.println("  user.language     [DE|EN|NL|PL] (EN)");

                System.exit(0);
            }
            boolean debug = false;
            if (line.hasOption("d")) {
                debug = true;
            }

            // Process command line arguments
            String modelFilename = "";
            args = line.getArgs();
            if (args.length > 0) {
                modelFilename = args[0];
                File file = new File(modelFilename);
                if (!file.exists()) {
                    System.err.println(GT._("File does not exist") + ": "
                            + modelFilename);
                    System.exit(-1);
                }
                showInstance(file, null, null, debug);
            } else {
                showEmptyInstance(debug);
            }

        } catch (Throwable t) {
            System.err.println("uncaught exception: " + t);
            t.printStackTrace(System.err);
        }
    }

    public static void showEmptyInstance(boolean debug) {
        IChemModel chemModel = DefaultChemObjectBuilder.getInstance()
                .newChemModel();
        chemModel.setMoleculeSet(chemModel.getBuilder().newMoleculeSet());
        chemModel.getMoleculeSet().addAtomContainer(
                chemModel.getBuilder().newMolecule());
        showInstance(chemModel, GT._("Untitled-") + (instancecounter++), debug);
    }

    public static void showInstance(File inFile, String type,
            JChemPaintPanel jcpPanel, boolean debug) {
        try {
            IChemModel chemModel = JChemPaint.readFromFile(inFile, type);

            String name = inFile.getName();
            JChemPaintPanel p = JChemPaint.showInstance(chemModel, name, debug);
            p.setCurrentWorkDirectory(inFile.getParentFile());
            p.setLastOpenedFile(inFile);
            p.setIsAlreadyAFile(inFile);
        } catch (CDKException ex) {
            JOptionPane.showMessageDialog(jcpPanel, ex.getMessage());
            return;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(jcpPanel, GT._("File does not exist")
                    + ": " + inFile.getPath());
            return;
        }
    }

    public static IChemModel readFromFileReader(URL fileURL, String url,
            String type) throws CDKException {
        ISimpleChemObjectReader cor = JChemPaint.createReader(fileURL, url,
                type);
        IChemModel chemModel = JChemPaint.getChemModelFromReader(cor);
        flipOnReadingAndWriting(chemModel);
        JChemPaint.cleanUpChemModel(chemModel);

        return chemModel;
    }

    public static IChemModel readFromFile(File file, String type)
            throws CDKException, FileNotFoundException {
        Reader reader = new FileReader(file);
        String url = file.toURI().toString();
        ISimpleChemObjectReader cor = null;
        try {
            cor = JChemPaint.createReader(file.toURI().toURL(), url, type);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (cor instanceof CMLReader)
            cor.setReader(new FileInputStream(file)); // hack
        else
            cor.setReader(new FileReader(file)); // hack

        IChemModel chemModel = JChemPaint.getChemModelFromReader(cor);
        JChemPaint.cleanUpChemModel(chemModel);

        return chemModel;
    }

    public static void cleanUpChemModel(IChemModel chemModel)
            throws CDKException {
        JChemPaint.setReactionIDs(chemModel);
        JChemPaint.replaceReferencesWithClones(chemModel);
        //if we want to have reactions, we need this
        //JChemPaint.removeDuplicateMolecules(chemModel);

        // check for bonds
        if (ChemModelManipulator.getBondCount(chemModel) == 0) {
            throw new CDKException(
                    "Model does not have bonds. Cannot depict contents.");
        }

        JChemPaint.checkCoordinates(chemModel);
        JChemPaint.removeEmptyMolecules(chemModel);

        ControllerHub.avoidOverlap(chemModel);
        
        //We update implicit Hs in any case
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(chemModel.getBuilder());
        for (IAtomContainer container :
            ChemModelManipulator.getAllAtomContainers(chemModel)) {
           for (IAtom atom : container.atoms()) {
               if (!(atom instanceof IPseudoAtom)) {
                   try {
                       IAtomType type = matcher.findMatchingAtomType(
                           container, atom
                       );
                       if (type != null &&
                           type.getFormalNeighbourCount() != null) {
                           int connectedAtomCount = container.getConnectedAtomsCount(atom);
                           atom.setHydrogenCount(
                               type.getFormalNeighbourCount() - connectedAtomCount
                           );
                       }
                   } catch ( CDKException e ) {
                       e.printStackTrace();
                   }
               }
           }
       }
    }

    private static Reader getReader(URL url) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(url.openStream());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return reader;

    }

    private static ISimpleChemObjectReader createReader(URL url,
            String urlString, String type) throws CDKException {
        if (type == null) {
            type = "mol";
        }

        ISimpleChemObjectReader cor = null;

        /*
         * Have the ReaderFactory determine the file format
         */
        cor = new MDLV2000Reader(getReader(url), Mode.RELAXED);
        try {
            ReaderFactory factory = new ReaderFactory();
            cor = factory.createReader(getReader(url));
            // this is a workaround for bug #2698194, since it works with url
            // only
            if (cor instanceof CMLReader) {
                cor = new CMLReader(urlString);
            }
        } catch (IOException ioExc) {
            // we do nothing right now and hope it still works
        } catch (Exception exc) {
            // we do nothing right now and hope it still works
        }
        if (cor == null) {
            // try to determine from user's guess
            if (type.equals(JCPFileFilter.cml)
                    || type.equals(JCPFileFilter.xml)) {
                // this is a workaround for bug #2698194, since it works with
                // url only
                cor = new CMLReader(urlString);
            } else if (type.equals(JCPFileFilter.sdf)) {
                cor = new MDLV2000Reader(getReader(url));// TODO once merged,
                                                         // egons new reader
                                                         // needs to be used
                                                         // here
            } else if (type.equals(JCPFileFilter.mol)) {
                cor = new MDLV2000Reader(getReader(url));
            } else if (type.equals(JCPFileFilter.inchi)) {
                try {
                    cor = new INChIReader(new URL(urlString).openStream());
                } catch (MalformedURLException e) {
                    // These should not happen, since URL is built from a file
                    // before
                } catch (IOException e) {
                    // These should not happen, since URL is built from a file
                    // before
                }
            } else if (type.equals(JCPFileFilter.rxn)) {
                cor = new MDLRXNV2000Reader(getReader(url));
            } else if (type.equals(JCPFileFilter.smi)) {
                cor = new SMILESReader(getReader(url));
            }
        }
        if (cor == null) {
            throw new CDKException(GT._("Could not determine file format"));
        }
        // this takes care of files called .mol, but having several, sdf-style
        // entries
        if (cor instanceof MDLV2000Reader) {
            try {
                BufferedReader in = new BufferedReader(getReader(url));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("$$$$")) {
                        String message = GT
                                ._("It seems you opened a mol or sdf"
                                        + " file containing several molecules. "
                                        + "Only the first one will be shown");
                        JOptionPane.showMessageDialog(null, message, GT
                                ._("sdf-like file"),
                                JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            } catch (IOException ex) {
                // we do nothing - firstly if IO does not work, we should not
                // get here, secondly, if only this does not work, don't worry
            }
        }

        return cor;
    }

    public static IChemModel getChemModelFromReader(ISimpleChemObjectReader cor)
            throws CDKException {
        String error = null;
        ChemModel chemModel = null;
        IChemFile chemFile = null;
        if (cor.accepts(IChemFile.class) && chemModel==null) {
            // try to read a ChemFile
            try {
                chemFile = (IChemFile) cor.read((IChemObject) new ChemFile());
                if (chemFile == null) {
                    error = "The object chemFile was empty unexpectedly!";
                }
            } catch (Exception exception) {
                error = "Error while reading file: " + exception.getMessage();
                exception.printStackTrace();
            }
        }
        if (error != null) {
            throw new CDKException(error);
        }
        if (cor.accepts(ChemModel.class) && chemModel==null) {
            // try to read a ChemModel
            try {

                chemModel = (ChemModel) cor.read((IChemObject) new ChemModel());
                if (chemModel == null) {
                    error = "The object chemModel was empty unexpectedly!";
                }
            } catch (Exception exception) {
                error = "Error while reading file: " + exception.getMessage();
                exception.printStackTrace();
            }
        }

        // Smiles reading
        if (cor.accepts(MoleculeSet.class) && chemModel==null) {
            // try to read a Molecule set
            try {
                IMoleculeSet som = (MoleculeSet) cor.read(new MoleculeSet());
                chemModel = new ChemModel();
                chemModel.setMoleculeSet(som);
                if (chemModel == null) {
                    error = "The object chemModel was empty unexpectedly!";
                }
            } catch (Exception exception) {
                error = "Error while reading file: " + exception.getMessage();
                exception.printStackTrace();
            }
        }

        // MDLV3000 reading
        if (cor.accepts(Molecule.class) && chemModel==null) {
            // try to read a Molecule
                IMolecule mol = (Molecule) cor.read(new Molecule());
                if(mol!=null ) 
                    try{
                        IMoleculeSet newSet = new MoleculeSet();
                        newSet.addMolecule(mol);
                        chemModel = new ChemModel();
                        chemModel.setMoleculeSet(newSet);
                        if (chemModel == null) {
                            error = "The object chemModel was empty unexpectedly!";
                        }
                    } catch (Exception exception) {
                        error = "Error while reading file: " + exception.getMessage();
                        exception.printStackTrace();
                    }
        }

        if (error != null) {
            throw new CDKException(error);
        }

        if (chemModel == null && chemFile != null) {
            chemModel = (ChemModel) chemFile.getChemSequence(0).getChemModel(0);
        }

	//for some reason, smilesparser sets valencies, which we don't want in jcp
        if(cor instanceof SMILESReader){
        	IAtomContainer allinone = JChemPaintPanel.getAllAtomContainersInOne(chemModel);
   	        for(int k=0;k<allinone.getAtomCount();k++){
   	        	allinone.getAtom(k).setValency(null);
    		}
        }

        return chemModel;
    }

    private static void setReactionIDs(IChemModel chemModel) {
        // we give all reactions an ID, in case they have none
        // IDs are needed for handling in JCP
        IReactionSet reactionSet = chemModel.getReactionSet();
        if (reactionSet != null) {
            int i = 0;
            for (IReaction reaction : reactionSet.reactions()) {
                if (reaction.getID() == null)
                    reaction.setID("Reaction " + (++i));
            }
        }
    }

    private static void replaceReferencesWithClones(IChemModel chemModel)
            throws CDKException {
        // we make references in products/reactants clones, since same compounds
        // in different reactions need separate layout (different positions etc)
        if (chemModel.getReactionSet() != null) {
            for (IReaction reaction : chemModel.getReactionSet().reactions()) {
                int i = 0;
                IMoleculeSet products = reaction.getProducts();
                for (IAtomContainer product : products.atomContainers()) {
                    try {
                        products.replaceAtomContainer(i,
                                (IAtomContainer) product.clone());
                    } catch (CloneNotSupportedException e) {
                    }
                    i++;
                }
                i = 0;
                IMoleculeSet reactants = reaction.getReactants();
                for (IAtomContainer reactant : reactants.atomContainers()) {
                    try {
                        reactants.replaceAtomContainer(i,
                                (IAtomContainer) reactant.clone());
                    } catch (CloneNotSupportedException e) {
                    }
                    i++;
                }
            }
        }

        // check for bonds
        if (ChemModelManipulator.getBondCount(chemModel) == 0) {
            throw new CDKException(GT
                    ._("Cannot depict contents. Model does not have bonds."));
        }
    }

    private static void removeDuplicateMolecules(IChemModel chemModel) {
        // we remove molecules which are in MoleculeSet as well as in a reaction
        IReactionSet reactionSet = chemModel.getReactionSet();
        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        if (reactionSet != null && moleculeSet != null) {
            List<IAtomContainer> aclist = ReactionSetManipulator
                    .getAllAtomContainers(reactionSet);
            for (int i = moleculeSet.getAtomContainerCount() - 1; i >= 0; i--) {
                for (int k = 0; k < aclist.size(); k++) {
                    String label = moleculeSet.getAtomContainer(i).getID();
                    if (aclist.get(k).getID().equals(label)) {
                        chemModel.getMoleculeSet().removeAtomContainer(i);
                        break;
                    }
                }
            }
        }
    }

    private static void removeEmptyMolecules(IChemModel chemModel) {
        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        if (moleculeSet != null && moleculeSet.getAtomContainerCount() == 0) {
            chemModel.setMoleculeSet(null);
        }
    }

    private static void checkCoordinates(IChemModel chemModel)
            throws CDKException {
        for (IAtomContainer next : ChemModelManipulator
                .getAllAtomContainers(chemModel)) {
            if (GeometryTools.has2DCoordinatesNew(next) != 2) {
                String error = GT._("Not all atoms have 2D coordinates."
                        + " JCP can only show full 2D specified structures."
                        + " Shall we lay out the structure?");
                int answer = JOptionPane.showConfirmDialog(null, error,
                        "No 2D coordinates", JOptionPane.YES_NO_OPTION);

                if (answer == JOptionPane.NO_OPTION) {
                    throw new CDKException(GT
                            ._("Cannot display without 2D coordinates"));
                } else {
                    // CreateCoordinatesForFileDialog frame =
                    // new CreateCoordinatesForFileDialog(chemModel);
                    // frame.pack();
                    // frame.show();

                    IMoleculeSet set = chemModel.getMoleculeSet();
                    chemModel.setMoleculeSet(generate2dCoordinates(set));
                    return;
                }
            }
        }

        /*
         * Add implicit hydrogens (in ControllerParameters,
         * autoUpdateImplicitHydrogens is true by default, so we need to do that
         * anyway)
         */
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(chemModel
                .getBuilder());
        for (IAtomContainer molecule : ChemModelManipulator
                .getAllAtomContainers(chemModel)) {
            if (molecule != null) {
                try {
                    hAdder.addImplicitHydrogens(molecule);
                } catch (CDKException e) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Helper method to generate 2d coordinates when JChempaint loads a molecule
     * without 2D coordinates. Typically happens for SMILES strings.
     * 
     * @param molecules
     * @throws Exception
     */
    private static IMoleculeSet generate2dCoordinates(IMoleculeSet molecules) {
        IMoleculeSet molSet2Dcalculated = new MoleculeSet();
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        for (int atIdx = 0; atIdx < molecules.getAtomContainerCount(); atIdx++) {
            IAtomContainer mol = molecules.getAtomContainer(atIdx);
            sdg.setMolecule(mol.getBuilder().newMolecule(mol));
            try {
                sdg.generateCoordinates();
            } catch (Exception e) {
                e.printStackTrace();
            }
            IAtomContainer ac = sdg.getMolecule();
            molSet2Dcalculated.addAtomContainer(ac);
        }
        return molSet2Dcalculated;
    }

    public static JChemPaintPanel showInstance(IChemModel chemModel,
            String title, boolean debug) {
        JFrame f = new JFrame(title);
        chemModel.setID(title);
        f.addWindowListener(new JChemPaintPanel.AppCloser());
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JChemPaintPanel p = new JChemPaintPanel(chemModel, "stable", debug);
        f.setPreferredSize(new Dimension(1000, 500));
        f.add(p);
        f.pack();
        Point point = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getCenterPoint();
        int w2 = (f.getWidth() / 2);
        int h2 = (f.getHeight() / 2);
        f.setLocation(point.x - w2, point.y - h2);
        f.setVisible(true);
        return p;
    }


    public static void flipOnReadingAndWriting (IChemModel chemModel) {
        // flip needed due to difference between Java and chem coordinates
        if (chemModel.getMoleculeSet()!=null && chemModel.getMoleculeSet().getAtomContainer(0)!=null)
            for (IAtom atom : chemModel.getMoleculeSet().getAtomContainer(0).atoms()) {
                if (atom.getPoint2d()!=null) {
                    atom.setPoint2d(new Point2d(atom.getPoint2d().x,atom.getPoint2d().y*-1));
                }
             }

    }
}
