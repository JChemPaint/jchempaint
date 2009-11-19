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

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.selection.MultiSelection;

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
   public void mouseClickedDown(Point2d worldCoord) {
       
       IAtomContainer selectedAC = getSelectedAtomContainer( worldCoord );
       if(selectedAC == null) return;
       Set<IAtom> newSelection = new HashSet<IAtom>();
       for(IAtom atom:selectedAC.atoms()) {
           newSelection.add( atom );
           chemModelRelay.setValence(atom, valence==-1 ? null : valence);
       }
       setSelection( new MultiSelection<IAtom>(newSelection) );
       chemModelRelay.updateView();// FIXME do you really need to call it here?
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
