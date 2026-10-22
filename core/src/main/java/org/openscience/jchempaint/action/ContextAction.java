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
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.controller.IChemModelRelay;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import javax.swing.Action;
import java.awt.event.ActionEvent;

/**
 * This action allows the dispatching to different actions based on the context
 * of if an atom or bond is highlighted. You can wrap an existing action
 * 'adding context' of what to do in that instance.
 *
 * @author John Mayfield
 */
public class ContextAction extends JCPAction {

  private Action atomAction;
  private Action bondAction;
  private Action baseAction;

  public ContextAction(AbstractJChemPaintPanel jcpPanel) {
    super(jcpPanel);
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    final IChemModelRelay relay = jcpPanel.get2DHub();
    final JChemPaintRendererModel model = relay.getRenderer().getRenderer2DModel();

    IAtom hgAtom = model.getHighlightedAtom();
    IBond hgBond = model.getHighlightedBond();
    if (hgAtom != null && atomAction != null) {
      atomAction.actionPerformed(e);
      return;
    } else if (hgBond != null && bondAction != null) {
      bondAction.actionPerformed(e);
      return;
    }

    // if no action was made and there is a default action we do that
    if (baseAction != null) {
      baseAction.actionPerformed(e);
    }
  }

  private static ContextAction getContextAction(Action action,
                                                AbstractJChemPaintPanel panel) {
    if (action instanceof ContextAction)
      return ((ContextAction) action);
    ContextAction result = new ContextAction(panel);
    if (action != null)
      result.baseAction = action;
    return result;
  }

  /**
   * Make a new context action for an atom.
   *
   * @param atomAction the action to take when an atom is highlighted
   * @param fallback the fallback action (can be null)
   * @return the new context action
   */
  public static ContextAction atom(JCPAction atomAction, Action fallback) {
    ContextAction ctxAction = getContextAction(fallback, atomAction.jcpPanel);
    if (ctxAction.atomAction != null) {
      LoggingToolFactory.createLoggingTool(ContextAction.class)
                        .warn("Context action is over-riding existing atom action: " + atomAction.type + " " + ctxAction);
    }
    ctxAction.atomAction = atomAction;
    return ctxAction;
  }

  /**
   * Make a new context action for a bond.
   *
   * @param bondAction the action to take when an atom is highlighted
   * @param fallback the fallback action (can be null)
   * @return the new context action
   */
  public static ContextAction bond(JCPAction bondAction, Action fallback) {
    ContextAction ctxAction = getContextAction(fallback, bondAction.jcpPanel);
    if (ctxAction.bondAction != null) {
      LoggingToolFactory.createLoggingTool(ContextAction.class)
                        .warn("Context action is over-riding existing bond action: " + bondAction.type + " " + ctxAction);
    }
    ctxAction.bondAction = bondAction;
    return ctxAction;
  }

  /**
   * Make a new context action for a base action.
   *
   * @param action the action to take by default
   * @param existingAction the fallback action (can be null)
   * @return the new context action
   */
  public static Action base(JCPAction action, Action existingAction) {
    ContextAction ctxAction = getContextAction(existingAction, action.jcpPanel);
    if (ctxAction.baseAction != null) {
      LoggingToolFactory.createLoggingTool(ContextAction.class)
                        .warn("Context action is over-riding existing base action: " + action.type + " " + ctxAction);
    }
    ctxAction.baseAction = action;
    return ctxAction;
  }

  @Override
  public String toString() {
    return "ContextAction{" +
           "atomAction=" + (atomAction instanceof JCPAction ? ((JCPAction) atomAction).type : atomAction) +
           ", bondAction=" + (bondAction instanceof JCPAction ? ((JCPAction) bondAction).type : bondAction) +
           ", baseAction=" + (baseAction instanceof JCPAction ? ((JCPAction) baseAction).type : baseAction) +
           '}';
  }
}
