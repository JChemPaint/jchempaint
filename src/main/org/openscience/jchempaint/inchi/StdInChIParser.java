/* 
 * Copyright (C) 2009  Mark Rijnbeek <mark_rynbeek@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.inchi;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.iupac.StdInChI;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Parses an InChI string into an IAtomContainer using a C->Java conversion 
 * of the InChI command line tool 'stdinchi'.<br>
 * Conversion of this C program was done with NestedVM, an example of this 
 * particular conversion can be found here:<br> 
 * http://depth-first.com/articles/2007/12/03/.<br>
 * General info is at http://wiki.brianweb.net/NestedVM/QuickStartGuide.
 * <br><br>
 * This class does file based operations, all files are temporary and
 * are deleted afterwards.<br>
 * Thread safety is hard to enforce with file based operations, but the class
 * does work on uniquely named files per request.   
 * <br> 
 *
 * @author Mark Rijnbeek
 * 
 */
public class StdInChIParser extends StdInChITool{

    /**
     * Generate InChI for a given atom container.<BR> 
     * Take current user's home directory as temporary dir.<BR>
     * Overloads {@linkplain #parseInchi(String, String)}.
     * 
     * @param inchi to convert to an IAtomContainer
     * @return CDK IAtomContainer
     * @throws Exception
     */
    public IAtomContainer parseInchi(String inchi)throws Exception {

     String workingDir=System.getProperty("user.dir")+
                       System.getProperty("file.separator");
     
     return parseInchi(inchi,workingDir);
    }
    

    /**
     * Generate InChI for a given atom container.<BR> 
     * Temporary working directory provided as argument.
     * @param inchi to convert to an IAtomContainer
     * @param workingDir
     * @return CDK IAtomContainer
     * @throws Exception
     */
    public IAtomContainer parseInchi(String inchi, String workingDir) 
    throws Exception {

        //Prevent standard error being written with detailed log 
        PrintStream stErr = System.err;
        System.setErr(new PrintStream(new ByteArrayOutputStream()));

        IAtomContainer atc=null;
        
        // Use random file numbering to make class thread safe(r)
        String tmpFileBase = "cdk"+getFileSeq();
        //System.out.println(tmpFileBase);
        

        // Set up temporary file names for generator to work with 
        String tmpInFile = workingDir + tmpFileBase + INFILE_EXTENSION;
        String tmpOutFile = workingDir + tmpFileBase + OUTFILE_EXTENSION;
        String tmpMolFile = workingDir + tmpFileBase + MOLFILE_EXTENSION;
        String tmpLogFile = workingDir + tmpFileBase + LOGFILE_EXTENSION;
        String tmpPrbFile = workingDir + tmpFileBase + PROBFILE_EXTENSION;

        try {

            //From InChI to full InChI -----------------------------------------

            // write user InChi to file 
            BufferedWriter out = new BufferedWriter(new FileWriter(tmpInFile));
            out.write(inchi+eol);
            out.close();

            //Prepare an array for the InChI generator.
            List<String> argsList = new ArrayList<String>();
            argsList.add("");
            argsList.add(tmpInFile);
            argsList.add(tmpOutFile);
            argsList.add(tmpLogFile);
            argsList.add(tmpPrbFile);
            argsList.add("-InChI2Struct");

            String[] args = new String[argsList.size()];
            args=argsList.toArray(args);

            //Call to make the full InChI file based on user's basic InChI
            StdInChI stdinchi = new StdInChI();
            stdinchi.run(args);

            //Check if something went wrong .. pick up any error from log file
            String errMsg=getErrorMsg (tmpLogFile);
            if(!errMsg.equals("")) {
                throw new CDKException (errMsg);
            }
            
            //From full InChI to Molfile ---------------------------------------
            argsList = new ArrayList<String>();
            argsList.add("");
            argsList.add(tmpOutFile);
            argsList.add(tmpMolFile);
            argsList.add(tmpLogFile);
            argsList.add(tmpPrbFile);
            argsList.add("-OutputSDF");

            args = new String[argsList.size()];
            args=argsList.toArray(args);

            //Call to make the full InChI file
            StdInChI stdinchi2 = new StdInChI();
            stdinchi2.run(args);

            //Check if something went wrong .. pick up any error from log file
            errMsg=getErrorMsg (tmpLogFile);
            if(!errMsg.equals("")) {
                throw new CDKException (errMsg);
            }

            //From Molfile to Atom container -----------------------------------
            InputStream ins = new FileInputStream(tmpMolFile);
            MDLV2000Reader reader = new MDLV2000Reader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            atc= ChemFileManipulator.getAllAtomContainers(chemFile).get(0);
            atc.setProperty(CDKConstants.TITLE, null);

            // Generate coordinates for the atom container
            StructureDiagramGenerator sdg =
                new StructureDiagramGenerator((IMolecule)atc);
            sdg.generateCoordinates();


        } finally {

            //Always delete any temporary files
            deleteFileIfExists(tmpInFile);
            deleteFileIfExists(tmpOutFile);
            deleteFileIfExists(tmpMolFile);
            deleteFileIfExists(tmpLogFile);
            deleteFileIfExists(tmpPrbFile);
            System.setErr(stErr);

        }
        return atc;
    }

    public static void main(String[] args) throws Exception {

        StdInChIParser parser = new StdInChIParser();
        IAtomContainer m  = parser.parseInchi("InChI=1S/C2H6/c1-2/h1-2H3");

        for (int i = 0; i < 10; i++) {
            StdInChIGenerator gen = new StdInChIGenerator();
            gen.generateInchi(m);
            
        }
    }

}
