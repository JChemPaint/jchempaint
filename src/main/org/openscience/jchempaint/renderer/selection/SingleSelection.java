/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2009 Arvid Berg <goglepox@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.jchempaint.renderer.selection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Represents a single <code>ChemObject</code>
 *
 * @author Arvid
 * @cdk.module rendercontrol
 */
public class SingleSelection<T extends IChemObject> extends AbstractSelection
		implements IChemObjectSelection {

	T selection;

	public SingleSelection(T item) {
		selection = item;
	}

	public IAtomContainer getConnectedAtomContainer() {
		IAtomContainer ac = selection.getBuilder().newAtomContainer();
		addToAtomContainer(ac, selection);
		return ac;
	}

	public boolean isFilled() {
		return selection != null;
	}

	public boolean contains(IChemObject obj) {
		return selection == obj;
	}

	@SuppressWarnings("unchecked")
	public <E extends IChemObject> Collection<E> elements(Class<E> clazz) {
		if (selection == null)
			return Collections.emptySet();
		Set<E> set = new HashSet<E>();
		set.add((E) selection);
		return set;
	}
}
