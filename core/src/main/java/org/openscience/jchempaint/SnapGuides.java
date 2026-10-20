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
package org.openscience.jchempaint;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;
import org.openscience.jchempaint.SnapGuides.Guide.Direction;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.openscience.jchempaint.SnapGuides.Guide.Direction.Horizontal;
import static org.openscience.jchempaint.SnapGuides.Guide.Direction.Vertical;
import static org.openscience.jchempaint.SnapGuides.Type.Center;

/**
 * This generator facilitates "snapping" when moving objects display a visual
 * guide to show what you are snapping to. Different types of guide are
 * calculated for selected/unselected set, then the horizontal or vertical
 * guides line up the coordinates of the selected object are snapped and the
 * guide is displayed.
 *
 * @author John Mayfield
 */
public final class SnapGuides implements IGenerator<IAtomContainer> {

    /** The default snap distance, see {@link SnapDistance} param */
    private static final double DEFAULT_SNAP_DISTANCE = 0.5d;
    /* Determines if a guide is active or not */
    private static final double DISPLAY_THRESHOLD = 0.02d;
    /* At most 2 guides (per dimension) are displayed */
    public static final int MAX_ACTIVE_GUIDES = 2;
    /* Colors for vertical/horizontal guides */
    private static final Color DEFAULT_VERTICAL_COLOR = new Color(180, 255, 113);
    private static final Color DEFAULT_HORIZONTAL_COLOR = new Color(255, 112, 112);

    /**
     * The type of guide, note the order here defines the priority.
     */
    public enum Type {
        Bound,     // min/max bounds of an object
        Ring,      // min/max bounds of a ring
        Center     // center bounds
    }

    static final class Guide implements Comparable<Guide> {

        enum Direction {
            Horizontal,
            Vertical
        }

        final double pos;
        final SnapGuides.Type type;
        final Direction dir;
        boolean active = false;

        Guide(double pos, SnapGuides.Type type, Direction dir) {
            this.pos = pos;
            this.type = type;
            this.dir = dir;
        }

        IRenderingElement toElement(double width) {
            ElementGroup group = new ElementGroup();
            if (dir == Horizontal) {
                group.add(new LineElement(0, pos,
                                          Integer.MAX_VALUE, pos, width,
                                          DEFAULT_HORIZONTAL_COLOR));
            } else { // vertical
                group.add(new LineElement(pos, 0, pos,
                                          -Integer.MAX_VALUE, width,
                                          DEFAULT_VERTICAL_COLOR));
            }
            return group;
        }

        @Override
        public int compareTo(Guide o) {
            if (dir != o.dir)
                return dir.compareTo(o.dir);
            if (type != o.type)
                return type.compareTo(o.type);
            return Double.compare(pos, o.pos);
        }

        static List<Guide> ofBounds(Bounds b, SnapGuides.Type type) {
            return Arrays.asList(new Guide(b.minX, type, Vertical),
                                 new Guide(b.maxX, type, Vertical),
                                 new Guide(b.minY, type, Horizontal),
                                 new Guide(b.maxY, type, Horizontal));
        }

        static List<Guide> ofCenters(Bounds b) {
            return Arrays.asList(new Guide(b.centerX(), Center, Vertical),
                                 new Guide(b.centerY(), Center, Horizontal));
        }

        @Override
        public String toString() {
            if (dir == Vertical)
                return "Guide{" + type + "@x=" + pos +'}';
            else
                return "Guide{" + type + "@y=" + pos +'}';
        }
    }

    /**
     * This class indexes a set of guides into buckets, the size of the bucket,
     * the bucket size is based on the distance we are searching for.
     */
    static final class GuideSet {
        final List<Guide> guides;
        private final double maxDist;
        private final double gridSize;

        private final Map<Integer, List<Guide>> horizontal = new HashMap<>();
        private final Map<Integer, List<Guide>> vertical = new HashMap<>();

        GuideSet(Set<Guide> guides, double maxDist) {
            this.guides = new ArrayList<>(guides);
            this.maxDist = maxDist;
            this.gridSize = maxDist;
            for (Guide guide : guides) {
                if (guide.dir == Horizontal) {
                    horizontal.computeIfAbsent((int) (guide.pos / gridSize),
                                               k -> new ArrayList<>()).add(guide);
                } else { // vertical
                    vertical.computeIfAbsent((int) (guide.pos / gridSize),
                                             k -> new ArrayList<>()).add(guide);
                }
            }
        }

        GuideSet() {
            this.guides = Collections.emptyList();
            this.maxDist = 0;
            this.gridSize = 0;
        }

        private List<Guide> getBucket(Direction dir, int bucket) {
            return dir == Horizontal
                   ? horizontal.getOrDefault(bucket, Collections.emptyList())
                   : vertical.getOrDefault(bucket, Collections.emptyList());
        }

        Guide get(Guide source) {
            List<Guide> result = new ArrayList<>();
            double best = MAX_ACTIVE_GUIDES * maxDist;

            int bucket = (int) (source.pos / gridSize);
            for (int i = -1; i <= 1; i++) {
                List<Guide> guides = getBucket(source.dir, bucket + i);
                for (Guide target : guides) {
                    double dist = Math.abs(target.pos - source.pos);
                    if (dist > maxDist)
                        continue;
                    if (dist < best)
                        result.clear();
                    if (dist <= best) {
                        result.add(target);
                        best = dist;
                    }
                }
            }

            if (result.isEmpty())
                return null;
            return result.get(0);
        }
    }

    /**
     * Computes the set of guides for a given container.
     * @param container the container
     * @param ignore atoms to be ignored/skipped
     */
    static Set<Guide> computeGuides(IAtomContainer container, Set<IAtom> ignore) {
        return computeGuides(container.atoms(), ignore);
    }

    /**
     * Computes the set of guides for an iterable of atoms.
     * @param atoms the atoms
     * @param ignore atoms to be ignored/skipped
     */
    static Set<Guide> computeGuides(Iterable<IAtom> atoms,
                                    Set<IAtom> ignore) {
        int maxAtomIdx = 0;
        for (IAtom a : atoms)
            maxAtomIdx = Math.max(maxAtomIdx, a.getIndex());

        int[] parts = new int[maxAtomIdx + 1];
        int[] rings = new int[maxAtomIdx + 1];
        int numParts = 0;
        int numRings = 0;
        Deque<IAtom> queue = new ArrayDeque<>();
        for (IAtom start : atoms) {

            // presumes ignore is a complete part
            if (ignore.contains(start))
                continue;

            if (parts[start.getIndex()] == 0) {
                int partId = ++numParts;
                queue.push(start);
                parts[start.getIndex()] = partId;
                while (!queue.isEmpty()) {
                    IAtom a = queue.poll();
                    for (IBond bond : a.bonds()) {
                        IAtom nbor = bond.getOther(a);
                        if (ignore.contains(nbor))
                            continue;
                        if (parts[nbor.getIndex()] == 0) {
                            parts[nbor.getIndex()] = partId;
                            queue.add(nbor);
                        }
                    }
                }
            }

            if (start.isInRing() && rings[start.getIndex()] == 0) {
                queue.push(start);
                int ringId = ++numRings;
                rings[start.getIndex()] = ringId;
                while (!queue.isEmpty()) {
                    IAtom a = queue.poll();
                    for (IBond bond : a.bonds()) {
                        if (!bond.isInRing())
                            continue;
                        IAtom nbor = bond.getOther(a);
                        if (ignore.contains(nbor))
                            continue;
                        if (rings[nbor.getIndex()] == 0) {
                            rings[nbor.getIndex()] = ringId;
                            queue.add(nbor);
                        }
                    }
                }
            }
        }


        Set<Guide> guides = new TreeSet<>();

        Bounds[] partBounds = new Bounds[numParts + 1];
        Bounds[] ringBounds = new Bounds[numRings + 1];
        for (int i = 0; i < partBounds.length; i++)
            partBounds[i] = new Bounds();
        for (int i = 0; i < ringBounds.length; i++)
            ringBounds[i] = new Bounds();

        for (IAtom atom : atoms) {
            Point2d p = atom.getPoint2d();
            int idx = atom.getIndex();
            partBounds[parts[idx]].add(p.x, p.y);
            ringBounds[rings[idx]].add(p.x, p.y);
        }

        for (int i = 1; i <= numParts; i++) {
            guides.addAll(Guide.ofBounds(partBounds[i], Type.Bound));
            guides.addAll(Guide.ofCenters(partBounds[i]));
        }

        for (int i = 1; i <= numRings; i++) {
            guides.addAll(Guide.ofBounds(ringBounds[i], Type.Ring));
        }

        return guides;
    }

    /* Internal State */

    private GuideSet guideset = new GuideSet();

    void initGuides(IAtomContainer container,
                    Set<IAtom> ignore,
                    RendererModel model) {
        final double dist = model.get(SnapDistance.class);
        final Set<Type> types = model.get(SnapTypes.class);
        Set<Guide> guides = computeGuides(container, ignore);
        guides.removeIf(g -> !types.contains(g.type));
        this.guideset = new GuideSet(guides, dist);
    }

    void clearGuides() {
        this.guideset = new GuideSet();
    }

    void snap(Iterable<IAtom> atoms, RendererModel model) {

        final Set<Type> types = model.get(SnapTypes.class);

        // fail fast
        if (types.isEmpty() || guideset.guides.isEmpty())
            return;

        // note: selection guides could be cached and moved
        Set<SnapGuides.Guide> selectionGuides = SnapGuides.computeGuides(atoms,
                                                                         Collections.emptySet());
        selectionGuides.removeIf(g -> !types.contains(g.type));

        // hide all existing guides
        for (SnapGuides.Guide guide : guideset.guides)
            guide.active = false;

        // work out which guides are active and how much we should shift
        // molecules by
        int xActive = 0;
        int yActive = 0;
        Double xDelta = null;
        Double yDelta = null;
        for (Guide selection : selectionGuides) {
            Guide target = guideset.get(selection);
            if (target == null || target.active)
                continue;
            double delta = target.pos - selection.pos;
            if (target.dir == Vertical) {
                if (xDelta == null)
                    xDelta = delta;
                target.active = Math.abs(xDelta - delta) <= DISPLAY_THRESHOLD &&
                                xActive++ < MAX_ACTIVE_GUIDES;
            } else if (target.dir == Horizontal) {
                if (yDelta == null)
                    yDelta = delta;
                target.active = Math.abs(yDelta - delta) <= DISPLAY_THRESHOLD &&
                                yActive++ < MAX_ACTIVE_GUIDES;
            }
            if (xActive >= MAX_ACTIVE_GUIDES && yActive >= MAX_ACTIVE_GUIDES)
                break;
        }
        if (xDelta == null)
            xDelta = 0d;
        if (yDelta == null)
            yDelta = 0d;
        for (IAtom atom : atoms) {
            atom.setPoint2d(new Point2d(atom.getPoint2d().x + xDelta,
                                        atom.getPoint2d().y + yDelta));
        }
    }

    /* Rendering */

    @Override
    public IRenderingElement generate(IAtomContainer container, RendererModel model) {
        ElementGroup group = new ElementGroup();
        final Set<Type> types = model.get(SnapTypes.class);
        double scale = model.getParameter(BasicSceneGenerator.Scale.class).getValue();
        double width = 1 / scale;
        for (Guide guide : guideset.guides) {
            if (guide.active && types.contains(guide.type))
                group.add(guide.toElement(width));
        }
        return group;
    }

    /* Rendering Parameters */

    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(new SnapDistance(),
                             new SnapTypes());
    }

    public static final class SnapDistance
            extends AbstractGeneratorParameter<Double> {
        @Override
        public Double getDefault() {
            return DEFAULT_SNAP_DISTANCE;
        }
    }

    public static final class SnapTypes
            extends AbstractGeneratorParameter<Set<Type>> {
        @Override
        public Set<Type> getDefault() {
            return EnumSet.allOf(Type.class);
        }
    }
}
