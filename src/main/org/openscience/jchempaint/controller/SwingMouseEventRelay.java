/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net or nout@science.uva.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.jchempaint.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * @cdk.module controlbasic
 */
public class SwingMouseEventRelay implements MouseMotionListener,
        MouseListener, MouseWheelListener {

    private IMouseEventRelay relay;

    /** Position of mouse press for dragging. */
    private int dragFromX = 0;
    
    private int dragFromY = 0;
    
    /** true means mouse was pressed in ball and still in panel. */
    private boolean _canDrag = false;
    
    private boolean isPopupTrigger=false;
    
    public SwingMouseEventRelay(IMouseEventRelay relay) {
        this.relay = relay;
    }

    public void mouseMoved(MouseEvent event) {
        relay.mouseMove(event.getX(), event.getY());
    }

    public void updateView() {
        System.out.println("updating View now in SwingMouseEventRelay");
    }

    public void mouseDragged(MouseEvent event) {
        // check
        // http://www.leepoint.net/notes-java/examples/mouse/020dragdemo.html
        // for implementation
        relay.mouseDrag(dragFromX, dragFromY, event.getX(), event.getY());
        dragFromX = event.getX();
        dragFromY = event.getY();
    }

    public void mouseClicked(MouseEvent event) {
        // normal mouseClicked is the same as mousePressed and mouseReleased
        // after that

        // Double click is a special case
        if (event.getClickCount() > 1)
            relay.mouseClickedDouble(event.getX(), event.getY());
    }

    public void mouseEntered(MouseEvent event) {
        relay.mouseEnter(event.getX(), event.getY());
    }

    public void mouseExited(MouseEvent event) {
        relay.mouseExit(event.getX(), event.getY());
    }

    public void mousePressed(MouseEvent event) {
    	String osType= System.getProperty("os.name");
        if (event.isPopupTrigger() || (osType.indexOf("Windows")>-1 && event.getButton()==MouseEvent.BUTTON3)) {
            relay.mouseClickedDownRight(event.getX(), event.getY());
            isPopupTrigger = true;
        } else {
            relay.mouseClickedDown(event.getX(), event.getY());
            isPopupTrigger = false;
        }
        dragFromX = event.getX();
        dragFromY = event.getY();
    }

    public void mouseReleased(MouseEvent event) {
        if (isPopupTrigger){
            relay.mouseClickedUpRight(event.getX(), event.getY());
            isPopupTrigger=false;
        } else {
            relay.mouseClickedUp(event.getX(), event.getY());
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        int rotation = e.getWheelRotation(); 
        if (rotation > 0) {
            relay.mouseWheelMovedForward(rotation);
        } else {
            relay.mouseWheelMovedBackward(rotation);
        }
    }

}
