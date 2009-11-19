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

import javax.swing.JOptionPane;

import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;
import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;

/**
 * Opens a new empty JChemPaintFrame.
 * 
 */
public class NewAction extends JCPAction {

    private static final long serialVersionUID = -6710948755122145479L;

    /**
     * Opens an empty JChemPaint frame.
     * 
     *@param e
     *            Description of the Parameter
     */
    public void actionPerformed(ActionEvent e) {
        if (jcpPanel.getGuistring().equals(JChemPaintEditorApplet.GUI_APPLET)) {
            int clear = jcpPanel.showWarning();
            if (clear == JOptionPane.YES_OPTION) {
                jcpPanel.get2DHub().zap();
                jcpPanel.get2DHub().updateView();
                jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel()
                        .setZoomFactor(1);

                IChemObjectSelection selection = new LogicalSelection(
                        LogicalSelection.Type.NONE);
                jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel()
                        .setSelection(selection);

            }
        } else {
            JChemPaint.showEmptyInstance(jcpPanel.isDebug());
        }
    }
}
