/*
 *  $RCSfile$
 *  $Author: shk3 $
 *  $Date: 2008-10-02 16:45:12 +0100 (Thu, 02 Oct 2008) $
 *  $Revision: 12535 $
 *
 *  Copyright (C) 2003-2007  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.visitor.AWTDrawVisitor;

/**
 * @cdk.module jchempaint
 * @author steinbeck
 */
public class ZoomAction extends JCPAction {

    private static final long serialVersionUID = -2459332630141921895L;
    public static boolean zoomDone=false;
    
    public void actionPerformed(ActionEvent e) {

        RendererModel rendererModel = 
            jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel();
        double zoom = rendererModel.getZoomFactor();
        logger.debug("Zooming in/out in mode: ", type);
        
        if (type.equals("in") && zoom < 10) {
            rendererModel.setZoomFactor(zoom * 1.2);
        } else if (type.equals("out") && zoom > .1) {
            rendererModel.setZoomFactor(zoom / 1.2);
        } else if (type.equals("original")) {
            rendererModel.setZoomFactor(1);
        } else {
            logger.error("Unkown zoom command: " + type);
        }
        zoomDone=true;
        jcpPanel.get2DHub().updateView();
        jcpPanel.updateStatusBar();
        jcpPanel.getRenderPanel().update(jcpPanel.getRenderPanel().getGraphics());

    }

}
