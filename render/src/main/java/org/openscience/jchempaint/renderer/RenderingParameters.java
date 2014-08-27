/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *               2009  Arvid Berg <goglepox@users.sf.net>
 *               2009  Egon Willighagen <egonw@users.sf.net>
 *               2009  Stefan Kuhn <shk3@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.jchempaint.renderer;

import java.awt.Color;
//import java.util.Properties;

import org.openscience.cdk.renderer.font.IFontManager;

/**
 * @cdk.module render
 */
public class RenderingParameters {
    
    /**
     * When atoms are selected or in compact mode, they will
     * be covered by a shape determined by this enumeration
     */
    public enum AtomShape { OVAL, SQUARE };

    /**
     * The color used for underlining not typeable atoms.
     */
    private Color notTypeableUnderlineColor = Color.red;

    /**
     * The width on screen of an atom-atom mapping line
     */
    private double mappingLineWidth = 1.0;

    /**
     * The color of the box drawn at the bounds of a
     * molecule, molecule set, or reaction
     */
    private Color boundsColor = Color.LIGHT_GRAY;
                            
    private Color externalHighlightColor = Color.RED;

    private Color hoverOverColor = Color.BLUE;

    /**
     * The maximum distance on the screen the mouse pointer has to be to
     * highlight an element.
     */
    private double highlightDistance;

    private boolean highlightShapeFilled = false;

    private Color mappingColor = Color.gray;
   
    private Color selectedPartColor = new Color(00, 153, 204); //Color.lightGray;

    /**
     * The shape to display over selected atoms
     */
    private AtomShape selectionShape = AtomShape.SQUARE;

    /**
     * The radius on screen of the selection shape
     */
    private double selectionRadius = 3;

    /**
     * Determines whether rings should be drawn with a circle if they are
     * aromatic.
     */
    private boolean showAromaticity = false;

    private boolean showAromaticityInCDKStyle = false;

    private boolean showAtomAtomMapping = true;

    private boolean showAtomTypeNames = false;

    /**
     * Determines whether methyl carbons' symbols should be drawn explicit for
     * methyl carbons. Example C/\C instead of /\.
     */
    private boolean showEndCarbons;

    /** Determines whether explicit hydrogens should be drawn. */
    private boolean showExplicitHydrogens;

    /** Determines whether implicit hydrogens should be drawn. */
    private boolean showImplicitHydrogens;

    private boolean showReactionBoxes = false;

    private boolean willDrawNumbers;


    public boolean isHighlightShapeFilled() {
        return highlightShapeFilled;
    }

    public void setHighlightShapeFilled(boolean highlightShapeFilled) {
        this.highlightShapeFilled = highlightShapeFilled;
    }

    public boolean isShowAromaticityInCDKStyle() {
        return this.showAromaticityInCDKStyle;
    }

    public void setShowAromaticityInCDKStyle(boolean shouldShow) {
        this.showAromaticityInCDKStyle = shouldShow;
    } 

    public double getHighlightDistance() {
        return highlightDistance;
    }

    public void setHighlightDistance(double highlightDistance) {
        this.highlightDistance = highlightDistance;
    }

    public AtomShape getSelectionShape() {
        return this.selectionShape;
    }

    public void setSelectionShape(AtomShape selectionShape) {
        this.selectionShape = selectionShape;
    }                                                                   

    public double getMappingLineWidth() {
        return mappingLineWidth;
    }

    public Color getExternalHighlightColor() {
        return externalHighlightColor;
    }

    public Color getHoverOverColor() {
        return hoverOverColor;
    }

    public Color getMappingColor() {
        return mappingColor;
    }

    public Color getSelectedPartColor() {
        return selectedPartColor;
    }
   
    public boolean isShowAromaticity() {
        return showAromaticity;
    }

    public boolean isShowAtomAtomMapping() {
        return showAtomAtomMapping;
    }

    public boolean isShowAtomTypeNames() {
        return showAtomTypeNames;
    }

    public boolean isShowEndCarbons() {
        return showEndCarbons;
    }

    public boolean isShowExplicitHydrogens() {
        return showExplicitHydrogens;
    }

    public boolean isShowImplicitHydrogens() {
        return showImplicitHydrogens;
    }

    public boolean isShowReactionBoxes() {
        return showReactionBoxes;
    }

    public boolean isWillDrawNumbers() {
        return willDrawNumbers;
    }

    public void setMappingLineWidth(double mappingLineWidth) {
        this.mappingLineWidth = mappingLineWidth;
    }
    
    public void setExternalHighlightColor(Color externalHighlightColor) {
        this.externalHighlightColor = externalHighlightColor;
    }

    public void setHoverOverColor(Color hoverOverColor) {
        this.hoverOverColor = hoverOverColor;
    }

    public void setMappingColor(Color mappingColor) {
        this.mappingColor = mappingColor;
    }

    public void setSelectedPartColor(Color selectedPartColor) {
        this.selectedPartColor = selectedPartColor;
    }

    public void setShowAromaticity(boolean showAromaticity) {
        this.showAromaticity = showAromaticity;
    }

    public void setShowAtomAtomMapping(boolean showAtomAtomMapping) {
        this.showAtomAtomMapping = showAtomAtomMapping;
    }

    public void setShowAtomTypeNames(boolean showAtomTypeNames) {
        this.showAtomTypeNames = showAtomTypeNames;
    }

    public void setShowEndCarbons(boolean showEndCarbons) {
        this.showEndCarbons = showEndCarbons;
    }

    public void setShowExplicitHydrogens(boolean showExplicitHydrogens) {
        this.showExplicitHydrogens = showExplicitHydrogens;
    }

    public void setShowImplicitHydrogens(boolean showImplicitHydrogens) {
        this.showImplicitHydrogens = showImplicitHydrogens;
    }

    public void setShowReactionBoxes(boolean showReactionBoxes) {
        this.showReactionBoxes = showReactionBoxes;
    }

    public void setWillDrawNumbers(boolean willDrawNumbers) {
        this.willDrawNumbers = willDrawNumbers;
    }

    public Color getBoundsColor() {
        return this.boundsColor;
    }

    public void setBoundsColor(Color color) {
        this.boundsColor = color;
    }

	public double getSelectionRadius() {
		return this.selectionRadius;
	}

	public void setSelectionRadius(double selectionRadius) {
		this.selectionRadius = selectionRadius;
	}

    public Color getNotTypeableUnderlineColor() {
        return notTypeableUnderlineColor;
    }

    public void setNotTypeableUnderlineColor(Color notTypeableUnderlineColor) {
        this.notTypeableUnderlineColor = notTypeableUnderlineColor;
    }

}
