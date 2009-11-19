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
import java.util.HashSet;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * @author Arvid
 * @cdk.module rendercontrol
 */
public class MultiSelection<T extends IChemObject> extends AbstractSelection {

	Collection<T> selection;

	public MultiSelection(Collection<T> selection) {
		this.selection = selection;
	}

	public IAtomContainer getConnectedAtomContainer() {
		IAtomContainer atomContainer = null;
		if (!selection.isEmpty()) {
			for (T value : selection) {
				if (atomContainer == null)
					atomContainer = value.getBuilder().newAtomContainer();
				addToAtomContainer(atomContainer, value);
			}
		}
		return atomContainer;
	}

	public boolean isFilled() {

		return !selection.isEmpty();
	}

	public boolean contains(IChemObject obj) {
		if (obj == null)
			return false;
		for (T selected : selection) {
			if (selected == obj)
				return true;
		}
		return false;
	}

	/**
	 * Return elements in selection of type clazz
	 *
	 * @param clazz
	 * @return A collection containing clazz objects;
	 */
	@SuppressWarnings("unchecked")
	public <E extends IChemObject> Collection<E> elements(Class<E> clazz) {
		Set<E> set = new HashSet<E>();
		if (clazz.isAssignableFrom(IChemObject.class)) {
			set.addAll((Collection<E>) selection);
			return set;
		}
		for (IChemObject obj : selection) {
			// Check if obj is assignable to E
			if (clazz.isAssignableFrom(obj.getClass()))
				set.add((E) obj);
		}
		return set;
	}
}
