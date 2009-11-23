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
import java.net.MalformedURLException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;
import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.io.JCPFileFilter;
import org.openscience.jchempaint.io.JCPFileView;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;

/**
 * Shows the open dialog
 * 
 */
public class OpenAction extends JCPAction {

    private static final long serialVersionUID = 1030940425527065876L;

    /**
     * Opens an empty JChemPaint frame.
     * 
     * @param e
     *            Description of the Parameter
     */
    public void actionPerformed(ActionEvent e) {

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(jcpPanel.getCurrentWorkDirectory());
        JCPFileFilter.addChoosableFileFilters(chooser);
        if (jcpPanel.getCurrentOpenFileFilter() != null) {
            for(int i=0;i<chooser.getChoosableFileFilters().length;i++){
                if(chooser.getChoosableFileFilters()[i].getDescription().equals(jcpPanel.getCurrentOpenFileFilter().getDescription()))
                    chooser.setFileFilter(chooser.getChoosableFileFilters()[i]);
            }
        }
        if (jcpPanel.getLastOpenedFile() != null) {
            chooser.setSelectedFile(jcpPanel.getLastOpenedFile());
        }
        chooser.setFileView(new JCPFileView());

        int returnVal = chooser.showOpenDialog(jcpPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            jcpPanel.setCurrentWorkDirectory(chooser.getCurrentDirectory());
            jcpPanel.setCurrentOpenFileFilter(chooser.getFileFilter());

            javax.swing.filechooser.FileFilter ff = chooser.getFileFilter();
            if (ff instanceof JCPFileFilter) {
                type = ((JCPFileFilter) ff).getType();

            }
            if (jcpPanel.getGuistring().equals(
                    JChemPaintEditorApplet.GUI_APPLET) ||
                    JChemPaintPanel.getAllAtomContainersInOne(jcpPanel.getChemModel()).getAtomCount()==0) {
                int clear = jcpPanel.showWarning();
                if (clear == JOptionPane.YES_OPTION) {
                    try {
                        IChemModel chemModel = null;
                            chemModel = JChemPaint
                                    .readFromFileReader(chooser
                                            .getSelectedFile().toURI().toURL(),
                                            chooser.getSelectedFile().toURI()
                                                    .toString(), type);
                        if (jcpPanel.get2DHub().getUndoRedoFactory() != null
                                && jcpPanel.get2DHub().getUndoRedoHandler() != null) {
                            IUndoRedoable undoredo = jcpPanel.get2DHub()
                                    .getUndoRedoFactory().getLoadNewModelEdit(
                                            jcpPanel.getChemModel(),
                                            jcpPanel.getChemModel()
                                                    .getMoleculeSet(),
                                            jcpPanel.getChemModel()
                                                    .getReactionSet(),
                                            chemModel.getMoleculeSet(),
                                            chemModel.getReactionSet(),
                                            "Load "
                                                    + chooser.getSelectedFile()
                                                            .getName());
                            jcpPanel.get2DHub().getUndoRedoHandler().postEdit(
                                    undoredo);
                        }
                        jcpPanel.getChemModel().setMoleculeSet(
                                chemModel.getMoleculeSet());
                        jcpPanel.getChemModel().setReactionSet(chemModel.getReactionSet());
                        jcpPanel.getRenderPanel().getRenderer()
                                .getRenderer2DModel().setSelection(
                                        new LogicalSelection(
                                                LogicalSelection.Type.NONE));

                        // the newly opened file should nicely fit the screen
                        jcpPanel.getRenderPanel().setFitToScreen(true);

                        // quick fix upside down mol files
                        // jcpPanel.get2DHub().flip(true);

                        jcpPanel.getRenderPanel().update(
                                jcpPanel.getRenderPanel().getGraphics());

                        // enable zooming by removing constraint
                        jcpPanel.getRenderPanel().setFitToScreen(false);

                    
                    
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        jcpPanel.announceError(e1);

                    }
                }
            } else {
                JChemPaint.showInstance(chooser.getSelectedFile(), type,
                        jcpPanel, jcpPanel.isDebug());
            }
        }
    }
}
