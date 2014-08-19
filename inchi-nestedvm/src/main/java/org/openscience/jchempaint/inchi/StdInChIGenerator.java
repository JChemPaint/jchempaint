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

import org.iupac.StdInChI;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates InChI data using a C->Java conversion of the InChI command 
 * line tool 'stdinchi'.<br>
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
public class StdInChIGenerator extends StdInChITool {

   /*
    * Booleans for structure perception 
    */
    
    /**
     * Set to true for option -NEWPSOFF: Both ends of wedge point to stereo centers.
     */
    private boolean bothWedgeEndsPointToStereoCenter=false; 

    /**
     * Set to true for option -DoNotAddH: Don't add H according to usual valences: all H are explicit.
     */
    private boolean hydrogensNotAdded=false;           
    
    /**
     * Set to true for option -SNon: Exclude stereo
     */
    private boolean stereoExcluded=false; 

    
    
    /**
     * Generate InChI for a given atom container.<BR> 
     * Take current user's home directory as temporary dir.<BR>
     * Overloads {@linkplain #generateInchi(org.openscience.cdk.interfaces.IAtomContainer, String)}
     *  
     * @param molfile
     * @return InChI generated from Molfile
     * @throws java.io.IOException
     * @throws org.openscience.cdk.exception.CDKException 
     */
    public InChI generateInchi(IAtomContainer atc) 
    throws IOException, CDKException {

     String workingDir=System.getProperty("user.dir")+
                       System.getProperty("file.separator");
     return generateInchi(atc,workingDir);
    }
    
   
    /**
     * Generate InChI for a give atom container.<BR> 
     * Temporary working directory provided as argument.
     * 
     * @param molfile
     * @param workingDir working directory including a final slash
     * @return InChI generated from Molfile
     * @throws java.io.IOException
     * @throws org.openscience.cdk.exception.CDKException 
     */
    public InChI generateInchi(IAtomContainer atc, String workingDir) 
       throws IOException, CDKException {
        //Prevent standard error being written with detailed log 
        PrintStream stErr = System.err;
        System.setErr(new PrintStream(new ByteArrayOutputStream()));

        InChI inchi = new InChI();
        
        // Use random file numbering to make class thread safe(r)
        String tmpFileBase = "cdk"+getFileSeq();

        // Set up temporary file names for generator to work with 
        String tmpMolFile = workingDir + tmpFileBase + MOLFILE_EXTENSION;
        String tmpOutFile = workingDir + tmpFileBase + OUTFILE_EXTENSION;
        String tmpLogFile = workingDir + tmpFileBase + LOGFILE_EXTENSION;
        String tmpPrbFile = workingDir + tmpFileBase + PROBFILE_EXTENSION;
        
        try {

            StringWriter writer = new StringWriter();
            MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
            
            mdlWriter.write(atc);
            String mdl = writer.toString();
            
            //Write the Molfile String into a file for the generator to work on.  
            FileWriter fstream = new FileWriter(tmpMolFile);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(mdl);
            out.close();

            //Prepare an array for the InChI generator.
            List<String> argsList = new ArrayList<String>();
            argsList.add("");
            argsList.add(tmpMolFile);
            argsList.add(tmpOutFile);
            argsList.add(tmpLogFile);
            argsList.add(tmpPrbFile);
            argsList.add("-Key");
            if(bothWedgeEndsPointToStereoCenter)
                argsList.add("-NEWPSOFF");
            if(hydrogensNotAdded)
                argsList.add("-DoNotAddH");
            if(stereoExcluded)
                argsList.add("-SNon");
            String[] args = new String[argsList.size()];
            args=argsList.toArray(args);

            //Call the actual InChI generation.
            StdInChI stdinchi = new StdInChI();
            stdinchi.run(args);
            
            // Read the generated InChi from the output file 
            FileInputStream fInstream = new FileInputStream(tmpOutFile);
            DataInputStream in = new DataInputStream(fInstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {

                if (strLine.startsWith("InChI=")) {
                    inchi.setInChI(strLine);
                }
                if (strLine.startsWith("AuxInfo=")) {
                    inchi.setAuxInfo(strLine);
                }
                if (strLine.startsWith("InChIKey=") ) {
                    inchi.setKey(strLine);
                }
            }
            in.close();
            
            if (inchi.getInChI()==null) {
                //Something went wrong .. pick up the error from the log file
                String errMsg=getErrorMsg (tmpLogFile);
                if(!errMsg.equals("")) {
                    throw new CDKException (errMsg);
                }
                else {
                    throw new CDKException 
                    ("Unknown problem with InChI generation.");
                }
            }
            
        } finally {
            /* Delete any temporary files  */
            deleteFileIfExists(tmpMolFile);
            deleteFileIfExists(tmpOutFile);
            deleteFileIfExists(tmpLogFile);
            deleteFileIfExists(tmpPrbFile);
            System.setErr(stErr);
        }
        return inchi;
    }

    
    public boolean isBothWedgeEndsPointToStereoCenter() {
        return bothWedgeEndsPointToStereoCenter;
    }


    public void setBothWedgeEndsPointToStereoCenter(
            boolean bothWedgeEndsPointToStereoCenter) {
        this.bothWedgeEndsPointToStereoCenter = bothWedgeEndsPointToStereoCenter;
    }


    public boolean isHydrogensNotAdded() {
        return hydrogensNotAdded;
    }


    public void setHydrogensNotAdded(boolean hydrogensNotAdded) {
        this.hydrogensNotAdded = hydrogensNotAdded;
    }


    public boolean isStereoExcluded() {
        return stereoExcluded;
    }


    public void setStereoExcluded(boolean stereoExcluded) {
        this.stereoExcluded = stereoExcluded;
    }


}
