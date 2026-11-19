/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
*
* Copyright (C) 2009  Stefan Kuhn (undo redo)
*
* Contact: cdk-devel@lists.sourceforge.net
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import javax.vecmath.Point2d;
import java.util.Collection;
import java.util.HashSet;

/**
* Changes valence of atoms.
* 
* @author Niels Out
* @cdk.svnrev $Revision: 9162 $
* @cdk.module controlbasic
*/
public class ChangeValenceModule extends ControllerModuleAdapter {

   private int valence = 0;
   private String ID;

   /**
    * Constructor for the ChangeValenceModule.
    * 
    * @param chemModelRelay The current implmentation of IChemModelRelay.
    * @param valence        The valence to set
    */
   public ChangeValenceModule(IChemModelRelay chemModelRelay, int valence) {
       super(chemModelRelay);
       this.valence = valence;
   }

   /* (non-Javadoc) 
    * @see org.openscience.cdk.controller.ControllerModuleAdapter#mouseClickedDown(javax.vecmath.Point2d)
    */
   @Override
   public void mouseClickedDown(Point2d worldCoord, int modifiers) {

       Collection<IAtom> atoms = new HashSet<>();

       JChemPaintRendererModel renderModel = chemModelRelay.getRenderer()
                                                           .getRenderer2DModel();
       IAtom highlightedAtom = renderModel.getHighlightedAtom();
       if (highlightedAtom != null) {
           atoms.add(highlightedAtom);
       } else {
           IChemObjectSelection selection = renderModel.getSelection();
           if (selection != null) {
               atoms = selection.elements(IAtom.class);
           }
       }

       for(IAtom atom : atoms) {
           chemModelRelay.setValence(atom, valence==-1 ? null : valence);
       }

       chemModelRelay.updateView();
   }

   /* (non-Javadoc)
    * @see org.openscience.cdk.controller.ControllerModuleAdapter#setChemModelRelay(org.openscience.cdk.controller.IChemModelRelay)
    */
   public void setChemModelRelay(IChemModelRelay relay) {
       this.chemModelRelay = relay;
   }

   /* (non-Javadoc)
    * @see org.openscience.cdk.controller.IControllerModule#getDrawModeString()
    */
   public String getDrawModeString() {
       return "Valence "+valence;
   }

   /* (non-Javadoc)
    * @see org.openscience.cdk.controller.IControllerModule#getID()
    */
   public String getID() {
       return ID;
   }

   /* (non-Javadoc)
    * @see org.openscience.cdk.controller.IControllerModule#setID(java.lang.String)
    */
   public void setID(String ID) {
       this.ID=ID;
   }

}
