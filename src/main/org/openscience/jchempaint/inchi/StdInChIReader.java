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
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;

/**
 * Class to read an InChI file which expected to be some text file
 * with InChI=.... lines in there. These lines are fed into the StdInChIParser 
 * 
 * @author markr
 *
 */
public class StdInChIReader {


    /**
     * Read the InChI=.. lines from a give text file containing InChI(s)
     * @param url
     * @return chemModel with molecule set with molecule(s) created using InChI
     * @throws CDKException
     */
    public static IChemModel readInChI(URL url) throws CDKException {

        IChemModel chemModel = new ChemModel();
        try {
            IMoleculeSet moleculeSet = new MoleculeSet();
            chemModel.setMoleculeSet(moleculeSet);
            StdInChIParser parser = new StdInChIParser();

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                if (line.toLowerCase().startsWith("inchi=")) {
                    IAtomContainer atc = parser.parseInchi(line);
                    moleculeSet.addAtomContainer(atc);
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
