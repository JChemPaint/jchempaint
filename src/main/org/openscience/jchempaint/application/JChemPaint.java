package org.openscience.jchempaint.application;

import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFrame;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.jchempaint.JChemPaintPanel;

public class JChemPaint {
	
	public static IAtomContainer makeMolecule(String smiles) {
		SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		try {
			IMolecule mol = parser.parseSmiles(smiles);
			
			StructureDiagramGenerator generator = new StructureDiagramGenerator();
			generator.setMolecule(mol);
			generator.generateCoordinates();
			
			return (IAtomContainer) generator.getMolecule();
		} catch (Exception e) {
			return null;
		}
	}
	
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

			IChemModel chemModel=DefaultChemObjectBuilder.getInstance().newChemModel();
			// Process command line arguments
			String modelFilename = "";
			args = line.getArgs();
			FileReader contentToOpen;
			if (args.length > 0)
			{
				modelFilename = args[0];
				File file = new File(modelFilename);
				if (!file.exists())
				{
					System.err.println("File does not exist: " + modelFilename);
					System.exit(-1);
				}
				// ok, file exists
				contentToOpen = new FileReader(file);
				IChemObjectReader cor = new MDLV2000Reader(contentToOpen);
				//TODO read the file
				//chemModel = (ChemModel) cor.read((ChemObject) new ChemModel());
				IMoleculeSet setOfMolecules = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
				setOfMolecules.addAtomContainer(makeMolecule("CCCCCC"));
				chemModel.setMoleculeSet(setOfMolecules);
				chemModel.setID(file.getName());
			}

			showInstance(chemModel);

		} catch (Throwable t)
		{
			System.err.println("uncaught exception: " + t);
			t.printStackTrace(System.err);
		}
	}
	
	public static void showInstance(IChemModel chemModel){
		JFrame f = new JFrame("JChemPaint");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JChemPaintPanel p = new JChemPaintPanel(chemModel);
		f.setPreferredSize(new Dimension(1000,500));
		f.add(p);
		f.pack();
		f.setVisible(true);
	}

}
