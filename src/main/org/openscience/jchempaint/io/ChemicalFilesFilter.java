/**
 * 
 */
package org.openscience.jchempaint.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import org.openscience.jchempaint.GT;

/**
 * Displays all CML/SMILES/IUPAC/SDF/MOL/RXN files
 * in a filechooser
 * 
 * @author ralf
 *
 */
public class ChemicalFilesFilter extends javax.swing.filechooser.FileFilter {

	/**
	 *  Get the extension of a file.
	 *  Gets the extension attribute of the JCPFileFilter class
	 *
	 *@param  f  Description of the Parameter
	 *@return    The extension value
	 */
	public static String getExtension(File f)
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
		{
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
        	extension = extension.toLowerCase();
            if (extension.equals("smi") ||
                    extension.equals("smiles") ||
                    extension.equals("sdf") ||
                    extension.equals("txt") ||
                    extension.equals("iupac") ||
                    extension.equals("inchi") ||
                    extension.equals("mol") ||
                    extension.equals("rxn")) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "SMILES/IUPAC/SDF/MOL/RXN";
    }
}
