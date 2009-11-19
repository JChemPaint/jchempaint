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

import java.util.Set;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.jchempaint.controller.Changed;

/**
* Edit for changing the stereo order of a bond.
* @author Arvid
* @cdk.module controlbasic
*/
public class SetStereo extends AbstractEdit {

    IBond bond;
    IBond.Stereo newStereo;
    IBond.Stereo oldStereo;

    /**
     * Creates an edit representing a change in stereo type.
     * @param bond affected bond.
     * @param stereo new value.
     * @return edit representing the change.
     */
    public static SetStereo setStereo(IBond bond, IBond.Stereo stereo) {
        return new SetStereo(bond, stereo);
    }

    private SetStereo(IBond bond, IBond.Stereo stereo) {
        this.bond = bond;
        newStereo = stereo;
        oldStereo = bond.getStereo();
    }

    public Set<Changed> getTypeOfChanges() {

        return changed( Changed.Properties );
    }

    public void redo() {

        bond.setStereo( newStereo );
        updateHydrogenCount( bond );
    }

    public void undo() {

        bond.setStereo( oldStereo );
        updateHydrogenCount( bond );
    }

}
