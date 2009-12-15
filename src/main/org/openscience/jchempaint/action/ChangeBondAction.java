/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egon Willighagen, Stefan Kuhn
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IBond.Stereo;
import org.openscience.jchempaint.controller.AddBondDragModule;


/**
 * changes the atom symbol
 */
public class ChangeBondAction extends JCPAction
{

    private static final long serialVersionUID = -8502905723573311893L;

    public void actionPerformed(ActionEvent event)
    {
        //first switch mode
        AddBondDragModule newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(),IBond.Stereo.NONE, true);
        if(type.equals("down_bond")){
            newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(),IBond.Stereo.DOWN, true);
        }else if(type.equals("up_bond")){
            newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(),IBond.Stereo.UP, true);
        }else if(type.equals("undefined_bond")){
            newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(),IBond.Stereo.UP_OR_DOWN, true);
        }else if(type.equals("undefined_stereo_bond")){
            newActiveModule = new AddBondDragModule(jcpPanel.get2DHub(),IBond.Stereo.E_OR_Z, true);
        }
        newActiveModule.setID(type);
        jcpPanel.get2DHub().setActiveDrawModule(newActiveModule);

        //then handle selection or highlight if there is one
        IChemObject object = getSource(event);
        Iterator<IBond> bondsInRange = null;

        if (object == null){
            //this means the main menu was used
            if(jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection()!=null 
                    && jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().isFilled())
                bondsInRange=jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getSelection().getConnectedAtomContainer().bonds().iterator();
        }else if (object instanceof IBond)
        {
            List<IBond> bonds = new ArrayList<IBond>();
            bonds.add((IBond) object);
            bondsInRange = bonds.iterator();
        } else
        {
            List<IBond> bonds = new ArrayList<IBond>();
            bonds.add(jcpPanel.getRenderPanel().getRenderer().getRenderer2DModel().getHighlightedBond());
            bondsInRange = bonds.iterator();
        }

        if(bondsInRange==null)
            return;
        String s = event.getActionCommand();
        String type = s.substring(s.indexOf("@") + 1);
        while(bondsInRange.hasNext()){
            IBond bond = bondsInRange.next();

            IBond.Stereo stereo=null;
            IBond.Order order=null;

            if(type.equals("bond")){
                stereo=IBond.Stereo.NONE;
                order=IBond.Order.SINGLE;
            }else if(type.equals("double_bond")){
                stereo=IBond.Stereo.NONE;
                order=IBond.Order.DOUBLE;
            }else if(type.equals("triple_bond")){
                stereo=IBond.Stereo.NONE;
                order=IBond.Order.TRIPLE;
            }else if(type.equals("quad_bond")){
                stereo=IBond.Stereo.NONE;
                order=IBond.Order.QUADRUPLE;
            }else if(type.equals("down_bond")){
                stereo=IBond.Stereo.DOWN;
                order=IBond.Order.SINGLE;
            }else if(type.equals("up_bond")){
                stereo=IBond.Stereo.UP;
                order=IBond.Order.SINGLE;
            }else if(type.equals("undefined_bond")){
                stereo=IBond.Stereo.UP_OR_DOWN;
                order=IBond.Order.SINGLE;
            }else if(type.equals("undefined_stereo_bond")){
                stereo=IBond.Stereo.E_OR_Z;
                order=IBond.Order.SINGLE;
            }

            jcpPanel.get2DHub().changeBond(bond,order,stereo);
            
            
        }
        jcpPanel.get2DHub().updateView();
    }
}

