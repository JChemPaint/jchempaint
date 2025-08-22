/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Christoph Steinbeck, Stefan Kuhn
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
package org.openscience.jchempaint.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JFrame;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.stereo.Projection;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.dialog.TextViewDialog;


/**
 * Creates a SMILES from the current model
 *
 */
public class CreateSmilesAction extends JCPAction
{

	private static final long serialVersionUID = -4886982931009753342L;
	
	TextViewDialog dialog = null;
	JFrame frame = null;

	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Trying to create smile: ", type);
		if (dialog == null)
		{
			dialog = new TextViewDialog(frame, "SMILES", null, false, 40, 2);
			dialog.setName("smilestextdialog");
		}
		try
		{
			String smiles = getSmiles(jcpPanel.getChemModel());
			dialog.setMessage(GT.get("Generated SMILES:"),
                              "SMILES: " + smiles);
		} catch (Exception exception)
		{
			String message = GT.get("Error while creating SMILES:") + " " + exception.getMessage();
			logger.error(message);
			logger.debug(exception);
			dialog.setMessage(GT.get("Error"), message);
		}
		dialog.setVisible(true);
	}
	
	public static String getSmiles(IChemModel model) throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException{
		return getChiralSmiles(model);
	}

    public static String getMolfile(IChemModel model) throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException{
        StringWriter sw = new StringWriter();
        try (SDFWriter sdfw = new SDFWriter(sw)) {
            sdfw.write(model);
        }
        return sw.toString();
    }

    /**
     * @deprecated no such thing as chiral SMILES use {@link #getSmiles} for 'a' SMILES.
     */
    @Deprecated
	public static String getChiralSmiles(IChemModel model) throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        
        SmilesGenerator smigen = SmilesGenerator.isomeric();
        
				StringBuilder sb = new StringBuilder();
        for(IAtomContainer container : ChemModelManipulator.getAllAtomContainers(model)) {
            container.setStereoElements(StereoElementFactory.using2DCoordinates(container)
                                                      .interpretProjections(Projection.Haworth, Projection.Chair)
                                                      .createAll());
            if (sb.length() > 0)
                sb.append('.');
            sb.append(smigen.create(container));
        }
        
        return sb.toString();
	}

}

