package org.openscience.jchempaint.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.swing.JOptionPane;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.FormatFactory;
import org.openscience.cdk.io.INChIReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.RGroupQueryReader;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.RGroupQueryFormat;
import org.openscience.jchempaint.GT;

/**
 * Some file handling operation, moved out of JChemPaint class. 
 * @author markr
 */

public class FileHandler {

    /**
     * Creates a reader for a given URL, guessing the file type
     * using the various IChemFormats. 
     * @param url
     * @param urlString
     * @param type
     * @return
     * @throws CDKException
     */
    public static ISimpleChemObjectReader createReader(URL url,String urlString, String type) throws CDKException {

    	if (type == null) {
            type = "mol";
        }

        ISimpleChemObjectReader cor = null;

        try {

        	/* ReaderFactory.createReader was used before to find the right reader, but this
        	 * created problems (when applet shrunk with Yguard) because of the classloader 
        	 * used in the ReaderFactory, runtime errors. 
        	 * Instead, we avoid ReaderFactory and pick the right reader below ourselves. */
        	
        	Reader input = new BufferedReader(getReader(url));
        	FormatFactory formatFactory = new FormatFactory(8192);
        	IChemFormat format=formatFactory.guessFormat(input);
        	
        	if (format!=null) {
	        	if (format instanceof RGroupQueryFormat ) {
	                cor = new RGroupQueryReader();
		        	cor.setReader(input);
	        	}
	        	else if (format instanceof CMLFormat ) {
	        		cor = new CMLReader(urlString);
		        	cor.setReader(url.openStream());
	        	}
	        	else if (format instanceof MDLV2000Format ) { 
	                cor = new MDLV2000Reader(getReader(url));
		        	cor.setReader(input);
	        	}
	        	// SMILES format is never guessed :(
	        	//else if (format instanceof SMILESFormat ) { 
	        	//	cor = new SMILESReader(getReader(url));
		        //	cor.setReader(input);
	        	//}
	        	//InChI format is never guessed :(
	        	//else if (format instanceof INChIPlainTextFormat ) {
	        	//	cor = new INChIPlainTextReader(getReader(url));
		        // 	cor.setReader(input);
	        	//}
        	}
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        
        if (cor == null) {
            // try to determine from user's guess
            if (type.equals(JCPFileFilter.cml)|| type.equals(JCPFileFilter.xml)) {
                cor = new CMLReader(urlString);

            } else if (type.equals(JCPFileFilter.sdf)) {
                cor = new MDLV2000Reader(getReader(url));
            } else if (type.equals(JCPFileFilter.mol)) {
                cor = new MDLV2000Reader(getReader(url));
            } else if (type.equals(JCPFileFilter.inchi)) {
                try {
                    cor = new INChIReader(new URL(urlString).openStream());
                } catch (Exception e) {
                	e.printStackTrace();
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

        // Take care of files called .mol, but having several, sdf-style entries
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
            	ex.printStackTrace();
            }
        }
        return cor;
    }

    /**
     * Private helper method to construct a reader from a URL.
     * @param url
     * @return
     */
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
}
