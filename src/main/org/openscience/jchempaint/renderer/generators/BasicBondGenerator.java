/* Copyright (C) 2008-2009  Arvid Berg <goglepox@users.sf.net>
 *               2008-2009  Gilleain Torrance <gilleain@users.sf.net> 
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
package org.openscience.jchempaint.renderer.generators;

import java.awt.Color;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerComparatorBy2DCenter;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.LineElement;
import org.openscience.jchempaint.renderer.elements.WedgeLineElement;
import org.openscience.jchempaint.renderer.elements.WigglyLineElement;
import org.openscience.jchempaint.renderer.elements.WedgeLineElement.Direction;

/**
 * @cdk.module renderbasic
 */
public class BasicBondGenerator implements IGenerator {
	
	private static ILoggingTool logger = LoggingToolFactory
			.createLoggingTool(BasicBondGenerator.class);

	protected IRingSet ringSet;

	/**
	 * A hack to allow the HighlightGenerator to override the standard colors.
	 * Set it to non-null to have all bond-lines in this color.
	 */
	private Color overrideColor = null;

	/**
	 * A similar story to the override color
	 */
	private double overrideBondWidth = -1;

	public BasicBondGenerator() {
	}

	public void setOverrideColor(Color color) {
		this.overrideColor = color;
	}

	public void setOverrideBondWidth(double bondWidth) {
		this.overrideBondWidth = bondWidth;
	}

	protected IRingSet getRingSet(final IAtomContainer atomContainer,RendererModel renderModel) {
		//if(renderModel.isRecalculationRequiredForSSSR() || this.ringSet==null) {
		//	System.out.println("recalc SSSR");
		//	renderModel.setRecalculationRequiredForSSSR(false);
			IRingSet ringSet = atomContainer.getBuilder().newInstance(
					IRingSet.class);
			try {
				IAtomContainerSet molecules = ConnectivityChecker
						.partitionIntoMolecules(atomContainer);
				for (IAtomContainer mol : molecules.atomContainers()) {
					SSSRFinder sssrf = new SSSRFinder(mol);
					ringSet.add(sssrf.findSSSR());
				}
	
				return ringSet;
			} catch (Exception exception) {
				logger.warn("Could not partition molecule: "
						+ exception.getMessage());
				logger.debug(exception);
				return ringSet;
			}
		//}
		//else {
		//	return this.ringSet;
		//}
			
	}

	/**
	 * Determine the color of a bond, returning either the default color, the
	 * override color or whatever is in the color hash for that bond.
	 * 
	 * @param bond
	 *            the bond we are generating an element for
	 * @param model
	 *            the rendering model
	 * @return the color to paint the bond
	 */
	public Color getColorForBond(IBond bond, RendererModel model) {
		if (this.overrideColor != null) {
			return overrideColor;
		}

		Color color = model.getColorHash().get(bond);
		if (color == null) {
			return model.getDefaultBondColor();
		} else {
			return color;
		}
	}

	/**
	 * Determine the width of a bond, returning either the width defined in the
	 * model, or the override width. Note that this will be scaled to the space
	 * of the model.
	 * 
	 * @param bond
	 *            the bond to determine the width for
	 * @param model
	 *            the renderer model
	 * @return a double in chem-model space
	 */
	public double getWidthForBond(IBond bond, RendererModel model) {
		double scale = model.getScale();
		if (this.overrideBondWidth != -1) {
			return this.overrideBondWidth / scale;
		} else {
			return model.getBondWidth() / scale;
		}
	}

	public double getWidthForWedge(RendererModel model) {
		double scale = model.getScale();
		return model.getWedgeWidth() / scale;
	}

	public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
		ElementGroup group = new ElementGroup();
		this.ringSet = this.getRingSet(ac,model);

		// Sort the ringSet consistently to ensure consistent rendering.
		// If this is omitted, the bonds may 'tremble'.
		ringSet.sortAtomContainers(new AtomContainerComparatorBy2DCenter());

		for (IBond bond : ac.bonds()) {
			group.add(this.generate(bond, model));
		}
		return group;
	}

	public IRenderingElement generate(IBond currentBond, RendererModel model) {
		IRing ring = getSmallestRing(ringSet, currentBond);
		if (ring != null) {
			return generateRingElements(currentBond, ring, model);
		} else {
			return generateBond(currentBond, model);
		}
	}

	/**
	 * This is an alternative for getHeaviestRing. Which did not work
	 * so nicely for example with CHEBI:100010.
	 * @param ringSet
	 * @param bond
	 * @return
	 */
	private IRing getSmallestRing(IRingSet ringSet, IBond bond) {
		IRingSet rings = ringSet.getRings(bond);
		IRing ring = null;
		int minOrderSum = Integer.MAX_VALUE;
		for (Object ring1 : rings.atomContainers()) {
			if (minOrderSum > ((IRing) ring1).getBondOrderSum()) {
				ring = (IRing) ring1;
				minOrderSum = ring.getBondOrderSum();
			}
		}
		return ring;
	}

	public IRenderingElement generateBondElement(IBond bond, RendererModel model) {
		return this.generateBondElement(bond, bond.getOrder(), model);
	}

	/**
	 * Generate a LineElement or an ElementGroup of LineElements for this bond.
	 * This version should be used if you want to override the type - for
	 * example, for ring double bonds.
	 * 
	 * @param bond
	 *            the bond to generate for
	 * @param type
	 *            the type of the bond - single, double, etc
	 * @return
	 */
	public IRenderingElement generateBondElement(IBond bond, IBond.Order type,
			RendererModel model) {
		// More than 2 atoms per bond not supported by this module
		if (bond.getAtomCount() > 2)
			return null;

		// is object right? if not replace with a good one
		Point2d p1 = bond.getAtom(0).getPoint2d();
		Point2d p2 = bond.getAtom(1).getPoint2d();
		Color color = this.getColorForBond(bond, model);
		double bondWidth = this.getWidthForBond(bond, model);
		double bondDistance = model.getBondDistance() / model.getScale();
		if (type == IBond.Order.SINGLE) {
			return new LineElement(p1.x, p1.y, p2.x, p2.y, bondWidth, color);
		} else {
			ElementGroup group = new ElementGroup();
			switch (type) {
			case DOUBLE:
				this.createLines(p1, p2, bondWidth, bondDistance, color, group);
				break;
			case TRIPLE:
				this.createLines(p1, p2, bondWidth, bondDistance * 2, color,
						group);
				group.add(new LineElement(p1.x, p1.y, p2.x, p2.y, bondWidth,
						color));
				break;
			case QUADRUPLE:
				this.createLines(p1, p2, bondWidth, bondDistance, color, group);
				this.createLines(p1, p2, bondWidth, bondDistance * 4, color,
						group);
			default:
				break;
			}
			return group;
		}
	}

	private IRenderingElement generateBoldBondElement(IBond bond,
			RendererModel model) {
		Point2d p1 = bond.getAtom(0).getPoint2d();
		Point2d p2 = bond.getAtom(1).getPoint2d();
		return new LineElement(p1.x, p1.y, p2.x, p2.y, getWidthForWedge(model),
				getColorForBond(bond, model));
	}

	private void createLines(Point2d p1, Point2d p2, double width, double dist,
			Color c, ElementGroup group) {
		double[] out = generateDistanceData(p1, p2, dist);
		LineElement l1 = new LineElement(out[0], out[1], out[4], out[5], width,
				c);
		LineElement l2 = new LineElement(out[2], out[3], out[6], out[7], width,
				c);
		group.add(l1);
		group.add(l2);
	}

	private double[] generateDistanceData(Point2d p1, Point2d p2, double dist) {
		Vector2d normal = new Vector2d();
		normal.sub(p2, p1);
		normal = new Vector2d(-normal.y, normal.x);
		normal.normalize();
		normal.scale(dist);

		Point2d line1p1 = new Point2d();
		Point2d line1p2 = new Point2d();
		line1p1.add(p1, normal);
		line1p2.add(p2, normal);

		normal.negate();
		Point2d line2p1 = new Point2d();
		Point2d line2p2 = new Point2d();
		line2p1.add(p1, normal);
		line2p2.add(p2, normal);

		return new double[] { line1p1.x, line1p1.y, line2p1.x, line2p1.y,
				line1p2.x, line1p2.y, line2p2.x, line2p2.y };
	}

	public IRenderingElement generateRingElements(IBond bond, IRing ring,
			RendererModel model) {
		// check for bold bond
		if (isBetweenTwoWedges(bond, ring) && !isTriple(bond)) {
			if (isSingle(bond)) {
				return generateBoldBondElement(bond, model);
			} else {
				ElementGroup pair = new ElementGroup();
				IRenderingElement e1 = generateInnerElement(bond, ring, model,
						true);
				IRenderingElement e2 = generateBoldBondElement(bond, model);
				pair.add(e1);
				pair.add(e2);
				return pair;
			}
		}
		if (isSingle(bond) && isStereoBond(bond)) {
			return generateStereoElement(bond, model);
		} else if (isDouble(bond)) {
			ElementGroup pair = new ElementGroup();
			IRenderingElement e1 = generateBondElement(bond,
					IBond.Order.SINGLE, model);
			IRenderingElement e2 = generateInnerElement(bond, ring, model,
					false);
			pair.add(e1);
			pair.add(e2);
			return pair;
		} else {
			return generateBondElement(bond, model);
		}
	}

	public LineElement generateInnerElement(IBond bond, IRing ring,
			RendererModel model) {
		return generateInnerElement(bond, ring, model, false);
	}

	public LineElement generateInnerElement(IBond bond, IRing ring,
			RendererModel model, boolean isBold) {
		Point2d center = GeometryTools.get2DCenter(ring);
		Point2d a = bond.getAtom(0).getPoint2d();
		Point2d b = bond.getAtom(1).getPoint2d();

		double distance;
		if (!isBold)
			distance = 0.15;
		else
			distance = 0.25;

		// Track ticket #63
		// Correct distance for larger rings (like annulene) that have a ring
		// center far away:
		try {
			if (b.distance(a) < center.distance(a))
				distance = distance * (b.distance(a) / center.distance(a));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Point2d w = new Point2d();
		w.interpolate(a, center, distance);
		Point2d u = new Point2d();
		u.interpolate(b, center, distance);

		double alpha = 0.2;
		Point2d ww = new Point2d();
		ww.interpolate(w, u, alpha);
		Point2d uu = new Point2d();
		uu.interpolate(u, w, alpha);

		return new LineElement(u.x, u.y, w.x, w.y,
				getWidthForBond(bond, model), getColorForBond(bond, model));
	}

	/**
	 * Generates a rendering element element for a stereo bond.
	 * 
	 * @param bond
	 *            The bond to generate for.
	 * @param model
	 *            The current RendererModel.
	 * @return The rendering element.
	 */
	private IRenderingElement generateStereoElement(IBond bond,
			RendererModel model) {

		IBond.Stereo stereo = bond.getStereo();
		if (stereo == IBond.Stereo.E_OR_Z) {
			IRenderingElement base = generateBondElement(bond,
					IBond.Order.SINGLE, model);
			return new WigglyLineElement((LineElement) base, getColorForBond(
					bond, model));
		} else {
			// set wedge type
			int wedgeType = 1;
			if (stereo == IBond.Stereo.DOWN
					|| stereo == IBond.Stereo.DOWN_INVERTED)
				wedgeType = 0;
			if (stereo == IBond.Stereo.UP_OR_DOWN
					|| stereo == IBond.Stereo.UP_OR_DOWN_INVERTED)
				wedgeType = 2;

			// set Direction
			Direction dir = Direction.toSecond;
			if (stereo == IBond.Stereo.DOWN_INVERTED
					|| stereo == IBond.Stereo.UP_INVERTED
					|| stereo == IBond.Stereo.UP_OR_DOWN_INVERTED)
				dir = Direction.toFirst;

			IRenderingElement base = generateBondElement(bond,
					IBond.Order.SINGLE, model);
			return new WedgeLineElement((LineElement) base, wedgeType, dir,
					getColorForBond(bond, model));
		}
	}

	public boolean isTriple(IBond bond) {
		return bond.getOrder() == IBond.Order.TRIPLE;
	}

	public boolean isDouble(IBond bond) {
		return bond.getOrder() == IBond.Order.DOUBLE;
	}

	public boolean isSingle(IBond bond) {
		return bond.getOrder() == IBond.Order.SINGLE;
	}

	public boolean isStereoBond(IBond bond) {
		return bond.getStereo() != IBond.Stereo.NONE
				&& bond.getStereo() != (IBond.Stereo) CDKConstants.UNSET
				&& bond.getStereo() != IBond.Stereo.E_Z_BY_COORDINATES;
	}

	public boolean isBetweenTwoWedges(IBond bond, IRing ring) {
		IBond bond0 = ring.getNextBond(bond, bond.getAtom(0));
		IBond bond1 = ring.getNextBond(bond, bond.getAtom(1));
		if (bond0.getStereo() == IBond.Stereo.UP
				&& bond1.getStereo() == IBond.Stereo.UP_INVERTED)
			return true;
		else
			return false;
	}

	public boolean bindsHydrogen(IBond bond) {
		for (int i = 0; i < bond.getAtomCount(); i++) {
			IAtom atom = bond.getAtom(i);
			if ("H".equals(atom.getSymbol()))
				return true;
		}
		return false;
	}

	public IRenderingElement generateBond(IBond bond, RendererModel model) {
		if (!model.getShowExplicitHydrogens() && bindsHydrogen(bond)) {
			return null;
		}

		if (isStereoBond(bond)) {
			return generateStereoElement(bond, model);
		} else {
			return generateBondElement(bond, model);
		}
	}

	public List<IGeneratorParameter> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
