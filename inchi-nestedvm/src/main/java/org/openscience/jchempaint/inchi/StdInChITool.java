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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Abstract class offering common functionality when 
 * invoking {@linkplain org.iupac.StdInChI}.
 *  
 * @author markr
 *
 */
abstract class StdInChITool {

    final static String INFILE_EXTENSION = ".in";
    final static String OUTFILE_EXTENSION = ".out";
    final static String MOLFILE_EXTENSION = ".mol";
    final static String LOGFILE_EXTENSION = ".log";
    final static String PROBFILE_EXTENSION = ".prb";

    public static String eol = System.getProperty("line.separator");
    private static int seqNum=0;
    
    /**
     * Helper method to issue a file sequence number for a temporary file
     * name required by StdInChI class
     * @return file sequence number
     */
    synchronized int getFileSeq () {
        if (seqNum>=99999)
            seqNum=0;
        return ++seqNum;
    }

    /**
     * Helper method for InChi conversion, gets rid of temporary files.
     * @param fileName
     * @throws java.io.IOException
     */
    void deleteFileIfExists(String fileName) throws IOException {
        File f = new File(fileName);
        if (f.exists()) {
            boolean success = f.delete();
            if (!success) {
                throw new IOException
                ("Warning - could not remove temporary  file " + fileName);
            }
        }
    }

    /**
     * Helper method to distill error message from StdInChI log file.
     * @param tmpLogFile
     * @return
     * @throws java.io.IOException
     */
    String getErrorMsg (String tmpLogFile) throws IOException {

        StringBuffer msg = new StringBuffer();
        File f = new File(tmpLogFile);
        if (f.exists()) {
            FileInputStream fInstream = new FileInputStream(tmpLogFile);
            DataInputStream in = new DataInputStream(fInstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains("Syntax error")||strLine.contains("Error")) {
                        msg.append(strLine+eol);
                }
            }
            in.close();
        }
        return msg.toString();
    }

}
