/*
 * Copyright (C) 2009  Arvid Berg <goglepox@users.sourceforge.net>
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
package org.openscience.jchempaint.controller.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.controller.Changed;

/**
* Edit representing a composition of several edits.<p>
* The edits will be executed one after the other and will be undone in the
* reverse order.
* @author Arvid
* @cdk.module controlbasic
*/
public class CompositEdit extends AbstractEdit {

    private List<IEdit> edits;

    public Set<Changed> getTypeOfChanges() {

        Set<Changed> composit = new HashSet<Changed>();
        for(IEdit edit:edits) {
            composit.addAll( edit.getTypeOfChanges());
        }
        return composit;
    }

   /**
    * Creates an edit containing the given edits.
    * @param edits to be compounded.
    * @return edit representing the compounded edits.
    */
    public static CompositEdit compose(IEdit... edits ) {
        CompositEdit cEdit = new CompositEdit();
        cEdit.edits = Arrays.asList( edits );
        return cEdit;
    }

    /**
     * Create an edit containing the list of edits given.
     * @param edits list of edits to be compounded.
     * @return edit representing the the compunded edits.
     */
    public static CompositEdit compose(List<IEdit> edits) {
        CompositEdit cEdit = new CompositEdit();
        cEdit.edits = new ArrayList<IEdit>( edits );
        return cEdit;
    }

    @Override
    public void execute( IAtomContainer ac ) {
        for(IEdit edit:edits) {
            edit.execute( ac );
        }
    }

    public void redo() {

        for(IEdit edit:edits)
            edit.redo();

    }

    public void undo() {

        List<IEdit> reverse = new ArrayList<IEdit>(edits);
        Collections.reverse( reverse );
        for(IEdit edit:reverse)
            edit.undo();
        // reverse list
    }

}
