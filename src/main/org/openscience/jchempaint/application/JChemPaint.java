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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.INChIReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.io.JCPFileFilter;

public class JChemPaint {
	
	public static int instancecounter=1;
	
	@SuppressWarnings("static-access")
    public static void main(String[] args) {
		try
		{
			String vers = System.getProperty("java.version");
			String requiredJVM = "1.5.0";
			Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
			String version = "Could not determine JCP version";
			if(self!=null)
				version=self.getImplementationVersion();
			if (vers.compareTo(requiredJVM) < 0)
			{
				System.err.println("WARNING: JChemPaint "+version+" must be run with a Java VM version " +
						requiredJVM + " or higher.");
				System.err.println("Your JVM version: " + vers);
				System.exit(1);
			}

			Options options = new Options();
			options.addOption("h", "help", false, "give this help page");
			options.addOption("v", "version", false, "gives JChemPaints version number");
			options.addOption(
					OptionBuilder.withArgName("property=value").
					hasArg().
					withValueSeparator().
					withDescription("supported options are given below").
					create("D")
					);

			CommandLine line = null;
			try
			{
				CommandLineParser parser = new PosixParser();
				line = parser.parse(options, args);
			} catch (UnrecognizedOptionException exception)
			{
				System.err.println(exception.getMessage());
				System.exit(-1);
			} catch (ParseException exception)
			{
				System.err.println("Unexpected exception: " + exception.toString());
			}

			if (line.hasOption("v"))
			{
				System.out.println("JChemPaint v." + version + "\n");
				System.exit(0);
			}

			if (line.hasOption("h"))
			{
                System.out.println("JChemPaint v." + version + "\n");

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("JChemPaint", options);

				// now report on the -D options
				System.out.println();
				System.out.println("The -D options are as follows (defaults in parathesis):");
				System.out.println("  cdk.debugging     [true|false] (false)");
				System.out.println("  cdk.debug.stdout  [true|false] (false)");
				System.out.println("  devel.gui         [true|false] (false)");
				System.out.println("  gui               [stable|experimental] (stable)");
				System.out.println("  user.language     [DE|EN|NL|PL] (EN)");

				System.exit(0);
			}

			// Process command line arguments
			String modelFilename = "";
			args = line.getArgs();
			if (args.length > 0)
			{
				modelFilename = args[0];
				File file = new File(modelFilename);
				if (!file.exists())
				{
					System.err.println("File does not exist: " + modelFilename);
					System.exit(-1);
				}
				showInstance(file,null,null);
			}else{
				showEmptyInstance();
			}

		} catch (Throwable t)
		{
			System.err.println("uncaught exception: " + t);
			t.printStackTrace(System.err);
		}
	}
	
	public static void showEmptyInstance() {
		IChemModel chemModel=DefaultChemObjectBuilder.getInstance().newChemModel();
		showInstance(chemModel, GT._("Untitled-")+(instancecounter++));
	}

	public static void showInstance(File inFile, String type, JChemPaintPanel jcpPanel){
		if (!inFile.exists()) {
            JOptionPane.showMessageDialog(jcpPanel, "File " + inFile.getPath()
                    + " does not exist.");
            return;
        }
        if (type == null) {
            type = "unknown";
        }
        ISimpleChemObjectReader cor = null;
        /*
         * Have the ReaderFactory determine the file format
         */
        try {
            ReaderFactory factory = new ReaderFactory();
            cor = factory.createReader(new FileReader(inFile));
        } catch (IOException ioExc) {
            // we do nothing right now and hoe it still works
        } catch (Exception exc) {
            // we do nothing right now and hoe it still works
        }
        if (cor == null) {
            // try to determine from user's guess
            try {
                FileInputStream reader = new FileInputStream(inFile);
                if (type.equals(JCPFileFilter.cml)
                        || type.equals(JCPFileFilter.xml)) {
                    cor = new CMLReader(reader);
                } else if (type.equals(JCPFileFilter.sdf)) {
                    cor = new MDLV2000Reader(reader);
                } else if (type.equals(JCPFileFilter.mol)) {
                    cor = new MDLV2000Reader(reader);
                } else if (type.equals(JCPFileFilter.inchi)) {
                    cor = new INChIReader(reader);
                }

                // XXX TMP FIXME hack to ensure that testing with a file
                // as a command-line arg in eclipse will work (for a mol file)
                else {
                    cor = new MDLV2000Reader(reader);
                }
            } catch (FileNotFoundException exception) {
                // we do nothing right now and hoe it still works
            }
        }
        if (cor == null) {
            JOptionPane.showMessageDialog(jcpPanel,
                    "Could not determine file format.");
            return;
        }
        // this takes care of files called .mol, but having several, sdf-style
        // entries
        if (cor instanceof MDLV2000Reader) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(inFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("$$$$")) {
                        String message = "It seems you opened a mol or sdf"
                                + " file containing several molecules. "
                                + "Only the first one will be shown";
                        JOptionPane.showMessageDialog(jcpPanel, message,
                                "sdf-like file",
                                JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            } catch (IOException ex) {
                // we do nothing - firstly if IO does not work, we should not
                // get here, secondly, if only this does not work, don't worry
            }
        }
        String error = null;
        ChemModel chemModel = null;
        IChemFile chemFile = null;
        if (cor.accepts(IChemFile.class)) {
            // try to read a ChemFile
            try {
                chemFile = (IChemFile) cor
                        .read((IChemObject) new org.openscience.cdk.ChemFile());
                if (chemFile == null) {
                    error = "The object chemFile was empty unexpectedly!";
                }
            } catch (Exception exception) {
                error = "Error while reading file: " + exception.getMessage();
                exception.printStackTrace();
            }
        }
        if (error != null) {
            JOptionPane.showMessageDialog(jcpPanel, error);
            return;
        }
        if (cor.accepts(ChemModel.class)) {
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
        if (error != null) {
            JOptionPane.showMessageDialog(jcpPanel, error);
        }
        // check for bonds
        if (ChemModelManipulator.getBondCount(chemModel) == 0) {
            error = "Model does not have bonds. Cannot depict contents.";
        }
        // check for coordinates
        JChemPaint.checkCoordinates(chemModel);

        chemModel.setID(inFile.getName());
        JChemPaintPanel p = showInstance(chemModel, inFile.getName());
        p.setCurrentWorkDirectory(inFile.getParentFile());
        p.setLastOpenedFile(inFile);
        p.setIsAlreadyAFile(inFile);
	}
	
	// TODO
	private static void checkCoordinates(IChemModel chemModel) {
//	 if ((GeometryTools.has2DCoordinates(chemModel)==0)) {
//        String error = "Model does not have 2D coordinates. Cannot open file.";
//        logger.warn(error);
//        JOptionPane.showMessageDialog(this, error);
//        CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel, jchemPaintModel.getRendererModel().getRenderingCoordinates());
//        frame.pack();
//        frame.show();
//        return;
//    } else if ((GeometryTools.has2DCoordinatesNew(chemModel)==1)) {
//        int result=JOptionPane.showConfirmDialog(this,"Model has some 2d coordinates. Do you want to show only the atoms with 2d coordiantes?","Only some 2d cooridantes",JOptionPane.YES_NO_OPTION);
//        if(result>1){
//            CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel, jchemPaintModel.getRendererModel().getRenderingCoordinates());
//            frame.pack();
//            frame.show();
//            return;
//        }else{
//            for(int i=0;i<chemModel.getMoleculeSet().getAtomContainerCount();i++){
//                for(int k=0;i<chemModel.getMoleculeSet().getAtomContainer(i).getAtomCount();k++){
//                    if(chemModel.getMoleculeSet().getAtomContainer(i).getAtom(k).getPoint2d()==null)
//                        chemModel.getMoleculeSet().getAtomContainer(i).removeAtomAndConnectedElectronContainers(chemModel.getMoleculeSet().getAtomContainer(i).getAtom(k));
//                }                       
//            }
//        }
//    }
//    if(jcpPanel.getJChemPaintModel().getControllerModel().getAutoUpdateImplicitHydrogens()){
//        HydrogenAdder hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
//        java.util.Iterator mols = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().molecules();
//        while (mols.hasNext())
//        {
//            org.openscience.cdk.interfaces.IMolecule molecule = (IMolecule)mols.next();
//            if (molecule != null)
//            {
//                try{
//                        hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
//                }catch(Exception ex){
//                    //do nothing
//                }
//            }
//        }
//    }
	}
	
	
	public static JChemPaintPanel showInstance(IChemModel chemModel, String title){
		JFrame f = new JFrame(title);
		chemModel.setID(title);
		f.addWindowListener(new JChemPaintPanel.AppCloser());
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JChemPaintPanel p = new JChemPaintPanel(chemModel,"stable");
		f.setPreferredSize(new Dimension(1000,500));
		f.add(p);
		f.pack();
		f.setVisible(true);
		return p;
	}

}
