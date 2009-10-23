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
package org.openscience.jchempaint.action;

import java.awt.event.ActionEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.io.IJCPFileFilter;
import org.openscience.jchempaint.io.JCPExportFileFilter;
import org.openscience.jchempaint.io.JCPFileView;

/**
 * Opens a save dialog
 *
 */
public class ExportAction extends SaveAsAction {

    private static final long serialVersionUID = -6748046051686998776L;
    private FileFilter currentFilter = null;

    public ExportAction() {
        super();
    }

    /**
     * Constructor for the ExportAction object
     *
     *@param jcpPanel
     *            the parent Panel
     *@param isPopupAction
     *            true if this is a popup action
     */
    public ExportAction(JChemPaintPanel jcpPanel, boolean isPopupAction) {
        super(jcpPanel, isPopupAction);
    }

    /**
     * Exports the canvas as an image.
     *
     *@param event
     *            the action event that triggered this action.
     */
    public void actionPerformed(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(this.jcpPanel.getCurrentWorkDirectory());
        JCPExportFileFilter.addChoosableFileFilters(chooser);
        if (currentFilter != null) {
            {
                for(int i=0;i<chooser.getChoosableFileFilters().length;i++){
                    if(chooser.getChoosableFileFilters()[i].getDescription().equals(currentFilter.getDescription()))
                        chooser.setFileFilter(chooser.getChoosableFileFilters()[i]);
                }
            }
        }
        chooser.setFileView(new JCPFileView());

        int returnVal = chooser.showSaveDialog(jcpPanel);
        currentFilter = chooser.getFileFilter();
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if(!(currentFilter instanceof IJCPFileFilter)){
                JOptionPane.showMessageDialog(jcpPanel, GT._("Please chose a file type!"), GT._("No file type chosen"), JOptionPane.INFORMATION_MESSAGE);
                return;
            }else{
                type = ((JCPExportFileFilter) currentFilter).getType();
                File outFile = new File(
                        chooser.getSelectedFile().getAbsolutePath());
                String fileName = outFile.toString();
                if (!fileName.endsWith("."+type)) {
                    fileName += "."+type;
                    outFile = new File(fileName);
                }
                if (outFile.exists()) {
                    String message = "File already exists. Do you want to overwrite it?";
                    String title = "File already exists";
                    int value = JOptionPane.showConfirmDialog(jcpPanel, message,
                            title, JOptionPane.YES_NO_OPTION);
                    if (value == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
    
                if (type.equals(JCPExportFileFilter.svg)) {
                    try {
                    	String svg = this.jcpPanel.getSVGString();
                    	FileWriter writer = new FileWriter(outFile);
                    	writer.append(svg);
                    	writer.flush();
                        JOptionPane.showMessageDialog(jcpPanel,
                                "Exported image to " + outFile);
                        return;
                    } catch (IOException e) {
                        String error = "Problem exporting to svg";
                        JOptionPane.showMessageDialog(jcpPanel, error);
                        return;
                    }
                } else {
                    RenderedImage image = (RenderedImage) this.jcpPanel
                            .takeSnapshot();
                    try {
                        String imageIOType;
                        if (type.equals(JCPExportFileFilter.bmp)) {
                            imageIOType = "BMP";
                        } else if (type.equals(JCPExportFileFilter.jpg)) {
                            imageIOType = "JPEG";
                        } else {
                            imageIOType = "PNG";
                        }
    
                        boolean succeeded = ImageIO.write(image, imageIOType,
                                outFile);
                        if (succeeded) {
                            JOptionPane.showMessageDialog(jcpPanel,
                                    "Exported image to " + outFile);
                            return;
                        } else {
                            // no writer of type imageIOType found
                            ImageIO.write(image, "PNG", outFile);
                            JOptionPane.showMessageDialog(jcpPanel,
                                    "Exported image to " + outFile+
                                    " as PNG, since "+type+" could not be written");
                            return;
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        JOptionPane.showMessageDialog(jcpPanel,
                                "Problem exporting image");
                    }
                }
            }
        } else if (returnVal == JFileChooser.CANCEL_OPTION) {
            return;
        }
    }

}
