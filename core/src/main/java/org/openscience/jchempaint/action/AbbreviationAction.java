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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.undoredo.SgroupEdits;
import org.openscience.jchempaint.dialog.EnterElementOrGroupDialog;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Actions for expanding/contracting/creating abbreviations.
 *
 * @author John Mayfield
 */
public class AbbreviationAction extends JCPAction {

  static Sgroup create(IChemModel model,
                       Iterable<IAtom> atoms,
                       String label) {
    Sgroup sgroup = new Sgroup();
    sgroup.setType(SgroupType.CtabAbbreviation);
    sgroup.setSubscript(label);
    IAtomContainer container = null;
    for (IAtom a : atoms) {
      sgroup.addAtom(a);
    }

    // find the crossing bonds
    for (IAtom a : atoms) {
      if (container == null || !container.contains(a))
        container = ChemModelManipulator.getRelevantAtomContainer(model, a);
      if (container == null)
        continue; // error?
      final Set<IAtom> atomset = sgroup.getAtoms();
      for (IBond bond : container.getConnectedBondsList(a)) {
        if (!atomset.contains(bond.getOther(a))) {
          sgroup.addBond(bond);
        }
      }
    }

    return sgroup;
  }

  private static boolean attach(Sgroup newSgroup, IChemModel model) {
    if (newSgroup.getAtoms().isEmpty())
      return false;
    IAtom atom = newSgroup.getAtoms().iterator().next();
    IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(model, atom);
    if (container == null)
      return false;
    Sgroup.add(container, newSgroup);
    return true;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    final String[] cmd = e.getActionCommand().split("@");
    assert cmd.length == 2;
    final String type = cmd[1];

    ControllerHub controller = jcpPanel.get2DHub();

    boolean changed = false;
    boolean expanding = false;
    switch (type) {
      case "abbreviate":

        IChemObjectSelection selection = controller.getRenderer()
                                                   .getRenderer2DModel()
                                                   .getSelection();
        if (selection == null || !selection.isFilled()) {
          JOptionPane.showMessageDialog(null,
                                        GT.get("Please select one or more atoms to abbreviate"),
                                        GT.get("No selection"),
                                        JOptionPane.WARNING_MESSAGE);
          return;
        }

        String label = EnterElementOrGroupDialog.showDialog(null, null,
                                                            GT.get("Enter your abbreviation label:"),
                                                            GT.get("Enter label"),
                                                            new String[0],
                                                            "",
                                                            "");
        if (label == null || label.trim().isEmpty())
          return; // bail out!

        IChemModel model = controller.getChemModel();

        // create the sgroup and attach it to the relevant container
        changed = attach(create(model, selection.elements(IAtom.class), label),
                         model);
        break;
      case "expand":
        expanding = true; // fall through
      case "contract":

        IChemObject object = getSource(e);
        IAtom atom = null;
        if (object instanceof IAtom)
          atom = (IAtom)object;
        if (atom == null)
          atom = controller.getRenderer()
                           .getRenderer2DModel()
                           .getHighlightedAtom();
        if (atom == null)
          return;

        // sort small to big -> if sgroups are nested and we contract, the
        // "inner" one is contracted first
        List<Sgroup> sgroups = new ArrayList<>(controller.getSgroups(atom));
        sgroups.sort(Comparator.comparingInt((Sgroup o) -> o.getAtoms().size()));

        for (Sgroup sgroup : sgroups) {
          if (sgroup.getAtoms().contains(atom)) {
            Boolean expanded = sgroup.getValue(SgroupKey.CtabExpansion);
            if (expanded == null) expanded = false;
            if (expanding != expanded) {
              sgroup.putValue(SgroupKey.CtabExpansion, !expanded);
              controller.getUndoRedoHandler().postEdit(SgroupEdits.expansionToggle(Collections.singletonList(sgroup)));
              changed = true;
              break; // one at a time!
            }
          }
        }
        break;
    }

    if (changed) {
      controller.updateView();
      controller.getRenderer()
                .getRenderer2DModel()
                .setHighlighted(null);
    }
  }
}
