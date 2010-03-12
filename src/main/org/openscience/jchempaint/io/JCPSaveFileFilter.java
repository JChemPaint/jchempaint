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
package org.openscience.jchempaint.io;

import javax.swing.JFileChooser;

import org.openscience.jchempaint.GT;

/**
 * It intentionally extends JCPFileFilter to remove redundant
 * data.
 *
 */
public class JCPSaveFileFilter extends JCPFileFilter {

    // only those extensions are given here that are *not* on JCPFileFilter
    public final static String svg = "svg";
    public final static String smiles = "smiles";
    public final static String cdk = "cdk";

    public JCPSaveFileFilter(String type) {
        super(type);
    }

    /**
     * Adds the JCPFileFilter to the JFileChooser object.
     */
    public static void addChoosableFileFilters(JFileChooser chooser) {
        chooser.addChoosableFileFilter(new JCPSaveFileFilter(JCPSaveFileFilter.smiles));
        chooser.addChoosableFileFilter(new JCPSaveFileFilter(JCPSaveFileFilter.cdk));
        chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.cml));
        chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.inchi));
        chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.rxn));
        chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.mol));
    }

    /**
     * The description of this filter.
     */
    public String getDescription() {
        String type = (String)types.get(0);
        String result = super.getDescription();
        if (result == null) {
            if (type.equals(svg)) {
                result = "Scalable Vector Graphics";
            } else if (type.equals(smiles)) {
                result = "SMILES";
            } else if (type.equals(cdk)) {
                result = GT._("CDK source code fragment");
            }
        }
        return result;
    }

}
