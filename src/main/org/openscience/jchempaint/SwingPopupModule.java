/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 2008 Stefan Kuhn
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
package org.openscience.jchempaint;

import java.awt.Rectangle;
import java.util.Hashtable;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.controller.ControllerModuleAdapter;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.RendererModel;

public class SwingPopupModule extends ControllerModuleAdapter {

    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(SwingPopupModule.class);

	private static Hashtable<String, JChemPaintPopupMenu> popupMenus = new Hashtable<String, JChemPaintPopupMenu>();

	private RenderPanel rendererPanel;
	
	private String ID;
	
	public static IAtom lastAtomPopupedFor = null;

	public SwingPopupModule(RenderPanel renderer,IChemModelRelay chemModelRelay) {
		super(chemModelRelay);
		this.rendererPanel=renderer;
	}


	public String getDrawModeString() {
		return "Popup menu";
	}


	public void mouseClickedDownRight(Point2d worldCoord) {
		popupMenuForNearestChemObject(rendererPanel.getRenderer().toScreenCoordinates(worldCoord.x, worldCoord.y));
	}
		
	/**
	 *  Sets the popupMenu attribute of the Controller2D object
	 *
	 *@param  someClass  The new popupMenu value
	 *@param  jchemPaintPopupMenu        The new popupMenu value
	 */
	public void setPopupMenu(Class someClass, JChemPaintPopupMenu jchemPaintPopupMenu) {
		SwingPopupModule.popupMenus.put(someClass.getName(), jchemPaintPopupMenu);
	}


	/**
	 *  Returns the popup menu for this IChemObject if it is set, and null
	 *  otherwise.
	 *
	 *@param  someClass  Description of the Parameter
	 *@return             The popupMenu value
	 */
	public JChemPaintPopupMenu getPopupMenu(Class classSearched) {
		logger.debug("Searching popup for: ", classSearched.getName());
        while (classSearched.getName().startsWith("org.openscience.cdk")) {
            logger.debug("Searching popup for: ", classSearched.getName());
            if (SwingPopupModule.popupMenus.containsKey(classSearched.getName())) {
                return (JChemPaintPopupMenu) SwingPopupModule.popupMenus.get(classSearched.getName());
            } else {
                logger.debug("  recursing into super class");
                classSearched = classSearched.getSuperclass();
            }
		}
        return null;
	}

	private void popupMenuForNearestChemObject(Point2d mouseCoords) {
	    Renderer renderer = rendererPanel.getRenderer();
	    RendererModel rendererModel = renderer.getRenderer2DModel();
		IChemObject objectInRange = rendererModel.getHighlightedAtom();
		
		if (objectInRange == null)
			objectInRange = rendererModel.getHighlightedBond();
		
		//look if we are in a reaction box
		IReactionSet reactionSet = 
		    rendererPanel.getChemModel().getReactionSet();
		
		if (objectInRange == null && reactionSet != null
                && reactionSet.getReactionCount() > 0) {
			
			for(int i=0;i<reactionSet.getReactionCount();i++){
				Rectangle reactionbounds = 
				    renderer.calculateDiagramBounds(reactionSet.getReaction(i));
				if (reactionbounds.contains(mouseCoords.x, mouseCoords.y))
					objectInRange = reactionSet.getReaction(i);
			}
		}
		
		if (objectInRange == null)
			objectInRange = chemModelRelay.getIChemModel();
		if (objectInRange instanceof IAtom)
		    lastAtomPopupedFor = (IAtom)objectInRange;
		
		JChemPaintPopupMenu popupMenu = getPopupMenu(objectInRange.getClass());
		if (popupMenu != null) {
			popupMenu.setSource(objectInRange);
			logger.debug("Set popup menu source to: ", objectInRange);
			popupMenu.show(rendererPanel, (int)mouseCoords.x, (int)mouseCoords.y);
		} else {
			logger.warn("Popup menu is null! Could not set source!");
		}
	}


	public String getID() {
		return ID;
	}


	public void setID(String ID) {
		this.ID = ID;		
	}
}
