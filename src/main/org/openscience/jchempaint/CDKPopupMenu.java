/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
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

import javax.swing.JPopupMenu;

import org.openscience.cdk.interfaces.IChemObject;

/**
 * Basically, identical to the JPopupMenu class, except that this menu
 * can also contain the source for which it was poped up.
 *
 * <p>IMPORTANT: The very nature of this design can lead to race conditions.
 * It would be better that the Event passed to the popup menu would define
 * the IChemObject source.
 *
 */
public class CDKPopupMenu extends JPopupMenu {
   
    private static final long serialVersionUID = -235498895062628065L;
    
    private IChemObject source;
   
    public void setSource(IChemObject object) {
        this.source = object;
    }
   
    public IChemObject getSource() {
        return this.source;
    }
   
}
