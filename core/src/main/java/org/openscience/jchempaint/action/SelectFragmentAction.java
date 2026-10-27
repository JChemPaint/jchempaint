/*
 *  Copyright (C) 2026 John Mayfield
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.controller.SelectSquareModule;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import java.awt.event.ActionEvent;

/**
 * This action allows selection of the whole fragment based on the current
 * hotspot.
 *
 * @author John Mayfield
 */
public class SelectFragmentAction extends JCPAction {

  @Override
  public void actionPerformed(ActionEvent e) {

    final ControllerHub relay = jcpPanel.get2DHub();
    final JChemPaintRendererModel model = relay.getRenderer().getRenderer2DModel();

    IAtom hgAtom = model.getHighlightedAtom();
    IBond hgBond = model.getHighlightedBond();
    if (hgAtom != null) {
      relay.selectFragment(hgAtom);
      relay.setActiveDrawModule(new SelectSquareModule(relay).withID("select"));
    } else if (hgBond != null) {
      relay.selectFragment(hgBond);
      relay.setActiveDrawModule(new SelectSquareModule(relay).withID("select"));
    }

    relay.updateView();
  }

}
