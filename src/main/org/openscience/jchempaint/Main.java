package org.openscience.jchempaint;

import javax.swing.JFrame;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;

public class Main {
	
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
		JFrame f = new JFrame("TestApp");
		
		IAtomContainer ac = Main.makeMolecule("C1=CC=CC=C1");
		if (ac == null) {
			System.exit(0);
		} 
		RenderPanel p = new RenderPanel(ac);
		f.add(p);
		f.pack();
		f.setVisible(true);
	}

}
