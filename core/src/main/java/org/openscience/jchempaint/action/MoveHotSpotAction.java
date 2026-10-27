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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.controller.SelectSquareModule;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This action allows the hotspot (highlighted atom/bond) to move in a
 * given direction (up/down/left/right). The move can be made twice (e.g. upup).
 *
 * @author John Mayfield
 */
public class MoveHotSpotAction extends JCPAction {

  @Override
  public void actionPerformed(ActionEvent e) {

    final IChemModelRelay relay = jcpPanel.get2DHub();
    final JChemPaintRendererModel model = relay.getRenderer().getRenderer2DModel();

    // re-sync - make sure the highlighted atom has the correct
    //           context (e.g. bonding) based on the current model
    if (model.getHighlightedAtom() != null) {
      IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(relay.getChemModel(),
                                                                               model.getHighlightedAtom());
      if (container != null)
        model.setHighlightedAtom(container.getAtom(container.indexOf(model.getHighlightedAtom())));
      else
        model.setHighlightedAtom(null);
    }
    if (model.getHighlightedBond() != null) {
      IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(relay.getChemModel(),
                                                                               model.getHighlightedBond());
      if (container != null)
        model.setHighlightedBond(container.getBond(container.indexOf(model.getHighlightedBond())));
      else
        model.setHighlightedBond(null);
    }

    String s = e.getActionCommand();
    String type = s.substring(s.indexOf("@") + 1);
    switch (type) {
      case "up":
        model.moveHighlight(KeyEvent.VK_UP);
        break;
      case "down":
        model.moveHighlight(KeyEvent.VK_DOWN);
        break;
      case "left":
        model.moveHighlight(KeyEvent.VK_LEFT);
        break;
      case "right":
        model.moveHighlight(KeyEvent.VK_RIGHT);
        break;
      case "upup":
        model.moveHighlight(KeyEvent.VK_UP);
        model.moveHighlight(KeyEvent.VK_UP);
        break;
      case "downdown":
        model.moveHighlight(KeyEvent.VK_DOWN);
        model.moveHighlight(KeyEvent.VK_DOWN);
        break;
      case "leftleft":
        model.moveHighlight(KeyEvent.VK_LEFT);
        model.moveHighlight(KeyEvent.VK_LEFT);
        break;
      case "rightright":
        model.moveHighlight(KeyEvent.VK_RIGHT);
        model.moveHighlight(KeyEvent.VK_RIGHT);
        break;
      default:
        return;
    }

    relay.setActiveDrawModule(new SelectSquareModule(relay).withID("select"));
    relay.updateView();
  }
}
