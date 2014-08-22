/* Copyright (C) 2008-2009  Gilleain Torrance <gilleain@users.sf.net>
 *               2008-2009  Arvid Berg <goglepox@users.sf.net>
 *                    2009  Stefan Kuhn <shk3@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 *  */
package org.openscience.jchempaint.renderer;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.jchempaint.renderer.RenderingParameters.AtomShape;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.color.RasmolColors;

/**
 * Model for {@link Renderer} that contains settings for drawing objects.
 *
 * @cdk.module render
 * @cdk.svnrev $Revision$
 */
public class JChemPaintRendererModel extends RendererModel implements Serializable, Cloneable {

    private static final long serialVersionUID = -4420308906715213445L;

    private RenderingParameters parameters;

    /** Determines how much the image is zoomed into on. */
    private double zoomFactor = 1.0;

    //this is used for the size of the arrowhead, might become configurable
    public static final int arrowHeadWidth = 10;
    private Map<Integer, Boolean> flags = new HashMap<Integer, Boolean>();
    /**
     * The color hash is used to color substructures.
     *
     * @see #getColorHash()
     */
    private Map<IChemObject, Color> colorHash =
        new Hashtable<IChemObject, Color>();

    private IAtomColorer colorer = new RasmolColors();

    private boolean recalculationRequiredForSSSR=true;


    public boolean isRecalculationRequiredForSSSR() {
		return recalculationRequiredForSSSR;
	}

	public void setRecalculationRequiredForSSSR(boolean recalculationRequiredForSSSR) {
		this.recalculationRequiredForSSSR = recalculationRequiredForSSSR;
	}

	/**
     * Constructor for the RendererModel.
     * 
     * @param useUserSettings Should user setting (in $HOME/.jchempaint/properties) be used or not?
     */
    public JChemPaintRendererModel() {
        this.parameters = new RenderingParameters();
    }

    /**
     * Constructor for the RendererModel.
     * 
     * @param parameters
     * @param useUserSettings Should user setting (in $HOME/.jchempaint/properties) be used or not?
     */
    public JChemPaintRendererModel(RenderingParameters parameters, boolean useUserSettings) {
        this.parameters = parameters;
    }
    
    public int getArrowHeadWidth() {
        return this.parameters.getArrowHeadWidth();
    }
    
    public void setArrowHeadWidth(int arrowHeadWidth) {
        this.parameters.setArrowHeadWidth(arrowHeadWidth);
    }

    public boolean getHighlightShapeFilled() {
        return this.parameters.isHighlightShapeFilled();
    }

    public void setHighlightShapeFilled(boolean highlightShapeFilled) {
        this.parameters.setHighlightShapeFilled(highlightShapeFilled);
    }

    public boolean getShowAromaticityCDKStyle() {
        return this.parameters.isShowAromaticityInCDKStyle();
    }

    public void setShowAromaticityCDKStyle(boolean showIt) {
        this.parameters.setShowAromaticityInCDKStyle(showIt);
        fireChange();
    }

    public double getWedgeWidth() {
        return this.parameters.getWedgeWidth();
    }

    public void setWedgeWidth(double wedgeWidth) {
        this.parameters.setWedgeWidth(wedgeWidth);
    }

    public double getRingProportion() {
        return this.parameters.getRingProportion();
    }

    public void setRingProportion(double ringProportion) {
        this.parameters.setRingProportion(ringProportion);
    }

    public AtomShape getCompactShape() {
        return this.parameters.getCompactShape();
    }

    public void setCompactShape(AtomShape compactShape) {
        this.parameters.setCompactShape(compactShape);
    }

    public double getScale() {
        return this.parameters.getScale();
    }

    public void setScale(double scale) {
        this.parameters.setScale(scale);
        if (hasParameter(BasicSceneGenerator.Scale.class))
            super.set(BasicSceneGenerator.Scale.class, scale);
    }

    public RenderingParameters.AtomShape getSelectionShape() {
        return this.parameters.getSelectionShape();
    }

    public void setSelectionShape(RenderingParameters.AtomShape selectionShape) {
        this.parameters.setSelectionShape(selectionShape);
    }

    /**
     * Get the name of the font family (Arial, etc).
     *
     * @return the name of the font family as a String.
     */
    public String getFontName() {
        return this.parameters.getFontName();
    }

    /**
     * Set the name of the font family (Arial, etc).
     */
    public void setFontName(String fontName) {
        this.parameters.setFontName(fontName);
        fireChange();
    }

    /**
     * Get the style of the font (Normal, Bold).
     *
     * @return the style of the font as a member of the IFontManager.FontStyle
     *         enum
     */
    public IFontManager.FontStyle getFontStyle() {
        return this.parameters.getFontStyle();
    }

    /**
     * Set the style of font to use (Normal, Bold).
     *
     * @param fontStyle a member of the enum in {@link IFontManager}
     */
    public void setFontManager(IFontManager.FontStyle fontStyle) {
        this.parameters.setFontStyle(fontStyle);
    }

    public boolean getIsCompact() {
        return this.parameters.isCompact();
    }

    public void setIsCompact(boolean compact) {
        this.parameters.setCompact(compact);
    }

    public boolean getUseAntiAliasing() {
        return this.parameters.isUseAntiAliasing();
    }

    public void setUseAntiAliasing(boolean bool) {
        this.parameters.setUseAntiAliasing(bool);
        fireChange();
    }

    public boolean getShowReactionBoxes() {
        return this.parameters.isShowReactionBoxes();
    }

    public void setShowReactionBoxes(boolean bool) {
        this.parameters.setShowReactionBoxes(bool);
        fireChange();
    }

    public boolean getShowMoleculeTitle() {
        return this.parameters.isShowMoleculeTitle();
    }

    public void setShowMoleculeTitle(boolean bool) {
        this.parameters.setShowMoleculeTitle(bool);
        fireChange();
    }

    /**
     * The length on the screen of a typical bond.
     *
     * @return the user-selected length of a bond, or the default length.
     */
    public double getBondLength() {
        return this.parameters.getBondLength();
    }

    /**
     * Set the length on the screen of a typical bond.
     *
     * @param bondLength the length in pixels of a typical bond.
     *
     */
    public void setBondLength(double length) {
        this.parameters.setBondLength(length);
    }

    /**
     * Returns the distance between two lines in a double or triple bond
     *
     * @return the distance between two lines in a double or triple bond
     */
    public double getBondDistance() {
        return this.parameters.getBondDistance();
    }

    /**
     * Sets the distance between two lines in a double or triple bond
     *
     * @param bondDistance
     *            the distance between two lines in a double or triple bond
     */
    public void setBondDistance(double bondDistance) {
        this.parameters.setBondDistance(bondDistance);
        fireChange();
    }

    /**
     * Returns the thickness of a bond line.
     *
     * @return the thickness of a bond line
     */
    public double getBondWidth() {
        return this.parameters.getBondWidth();
    }

    /**
     * Sets the thickness of a bond line.
     *
     * @param bondWidth
     *            the thickness of a bond line
     */
    public void setBondWidth(double bondWidth) {
        this.parameters.setBondWidth(bondWidth);
        fireChange();
    }

    /**
     * Returns the thickness of an atom atom mapping line.
     *
     * @return the thickness of an atom atom mapping line
     */
    public double getMappingLineWidth() {
        return this.parameters.getMappingLineWidth();
    }

    /**
     * Sets the thickness of an atom atom mapping line.
     *
     * @param mappingLineWidth
     *            the thickness of an atom atom mapping line
     */
    public void setMappingLineWidth(double mappingLineWidth) {
        this.parameters.setMappingLineWidth(mappingLineWidth);
        fireChange();
    }

    /**
     * A zoom factor for the drawing.
     *
     * @return a zoom factor for the drawing
     */
    public double getZoomFactor() {
        return this.zoomFactor;
    }

    /**
     * Returns the zoom factor for the drawing.
     *
     * @param zoomFactor
     *            the zoom factor for the drawing
     */
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        fireChange();
    }

    public boolean isFitToScreen() {
        return this.parameters.isFitToScreen();
    }

    public void setFitToScreen(boolean value) {
        this.parameters.setFitToScreen(value);
    }

    /**
     * Returns the foreground color for the drawing.
     *
     * @return the foreground color for the drawing
     */
    public Color getForeColor() {
        return this.parameters.getForeColor();
    }

    /**
     * Sets the foreground color with which bonds and atoms are drawn
     *
     * @param foreColor
     *            the foreground color with which bonds and atoms are drawn
     */
    public void setForeColor(Color foreColor) {
        this.parameters.setForeColor(foreColor);
        fireChange();
    }

    /**
     * Returns the background color
     *
     * @return the background color
     */
    public Color getBackColor() {
        return this.parameters.getBackColor();
    }

    /**
     * Sets the background color
     *
     * @param backColor
     *            the background color
     */
    public void setBackColor(Color backColor) {
        this.parameters.setBackColor(backColor);
        fireChange();
    }

    /**
     * Returns the atom-atom mapping line color
     *
     * @return the atom-atom mapping line color
     */
    public Color getAtomAtomMappingLineColor() {
        return this.parameters.getMappingColor();
    }

    /**
     * Sets the atom-atom mapping line color
     *
     * @param mappingColor
     *            the atom-atom mapping line color
     */
    public void setAtomAtomMappingLineColor(Color mappingColor) {
        this.parameters.setMappingColor(mappingColor);
        fireChange();
    }

    /**
     * Returns if the drawing of atom numbers is switched on for this model
     *
     * @return true if the drawing of atom numbers is switched on for this model
     */
    public boolean drawNumbers() {
        return this.parameters.isWillDrawNumbers();
    }

    public boolean getKekuleStructure() {
        return this.parameters.isKekuleStructure();
    }

    public void setKekuleStructure(boolean kekule) {
        this.parameters.setKekuleStructure(kekule);
        fireChange();
    }

    public boolean getColorAtomsByType() {
        return this.parameters.isColorAtomsByType();
    }

    public void setColorAtomsByType(boolean bool) {
        this.parameters.setColorAtomsByType(bool);
        fireChange();
    }

    public boolean getShowEndCarbons() {
        return this.parameters.isShowEndCarbons();
    }

    public void setShowEndCarbons(boolean showThem) {
        this.parameters.setShowEndCarbons(showThem);
        fireChange();
    }

    public boolean getShowImplicitHydrogens() {
        return this.parameters.isShowImplicitHydrogens();
    }

    public void setShowImplicitHydrogens(boolean showThem) {
        this.parameters.setShowImplicitHydrogens(showThem);
        fireChange();
    }

    public boolean getShowExplicitHydrogens() {
        return this.parameters.isShowExplicitHydrogens();
    }

    public void setShowExplicitHydrogens(boolean showThem) {
        this.parameters.setShowExplicitHydrogens(showThem);
        fireChange();
    }

    public boolean getShowAromaticity() {
        return this.parameters.isShowAromaticity();
    }

    public void setShowAromaticity(boolean showIt) {
        this.parameters.setShowAromaticity(showIt);
        fireChange();
    }

    /**
     * Sets if the drawing of atom numbers is switched on for this model.
     *
     * @param drawNumbers
     *            true if the drawing of atom numbers is to be switched on for
     *            this model
     */
    public void setDrawNumbers(boolean drawNumbers) {
        this.parameters.setWillDrawNumbers(drawNumbers);
        fireChange();
    }

    /**
     * Returns true if atom numbers are drawn.
     */
    public boolean getDrawNumbers() {
        return this.parameters.isWillDrawNumbers();
    }

    public Color getDefaultBondColor() {
        return this.parameters.getDefaultBondColor();
    }

    public void setDefaultBondColor(Color defaultBondColor) {
        this.parameters.setDefaultBondColor(defaultBondColor);
    }

    /**
     * Returns the radius around an atoms, for which the atom is marked
     * highlighted if a pointer device is placed within this radius.
     *
     * @return The highlight distance for all atoms (in screen space)
     */
    public double getHighlightDistance() {
        return this.parameters.getHighlightDistance();
    }

    /**
     * Sets the radius around an atoms, for which the atom is marked highlighted
     * if a pointer device is placed within this radius.
     *
     * @param highlightDistance
     *            the highlight radius of all atoms (in screen space)
     */
    public void setHighlightDistance(double highlightDistance) {
        this.parameters.setHighlightDistance(highlightDistance);
        fireChange();
    }

    /**
     * Returns whether Atom-Atom mapping must be shown.
     */
    public boolean getShowAtomAtomMapping() {
        return this.parameters.isShowAtomAtomMapping();
    }

    /**
     * Sets whether Atom-Atom mapping must be shown.
     */
    public void setShowAtomAtomMapping(boolean value) {
        this.parameters.setShowAtomAtomMapping(value);
        fireChange();
    }

    /**
     * This is used for the size of the compact atom element.
     */
    public double getAtomRadius() {
        return this.parameters.getAtomRadius();
    }

    /**
     * Set the radius of the compact atom representation.
     *
     * @param atomRadius the size of the compact atom symbol.
     *
     */
    public void setAtomRadius(double atomRadius) {
        this.parameters.setAtomRadius(atomRadius);
        fireChange();
    }

    /**
     * Returns the {@link Map} used for coloring substructures.
     *
     * @return the {@link Map} used for coloring substructures
     */
    public Map<IChemObject, Color> getColorHash() {
        return this.colorHash;
    }

    /**
     * Returns the drawing color of the given atom. An atom is colored as
     * highlighted if highlighted. The atom is color marked if in a
     * substructure. If not, the color from the CDK2DAtomColor is used (if
     * selected). Otherwise, the atom is colored black.
     */
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        if (atom == null) {
            return defaultColor;
        }

        Color atomColor = defaultColor;
        if (this.parameters.isColorAtomsByType()) {
            atomColor = colorer.getAtomColor(atom);
        }
        return atomColor;
    }

    /**
     * Returns the background color of the given atom.
     */
    public Color getAtomBackgroundColor(IAtom atom) {
        // logger.debug("Getting atom back color for " + atom.toString());
        Color atomColor = getBackColor();
        // logger.debug("  BackColor: " + atomColor.toString());
        Color hashColor = (Color) this.getColorHash().get(atom);
        if (hashColor != null) {
            // logger.debug(
            // "Background color atom according to hashing (substructure)");
            atomColor = hashColor;
        }
        // logger.debug("Color: " + atomColor.toString());
        return atomColor;
    }

    /**
     * Returns the current atom colorer.
     *
     * @return The AtomColorer.
     */
    public IAtomColorer getAtomColorer() {
        return colorer;
    }

    /**
     * Sets the atom colorer.
     *
     * @param atomColorer
     *            the new colorer.
     */
    public void setAtomColorer(final IAtomColorer atomColorer) {
        colorer = atomColorer;
    }

    /**
     * Sets the {@link Map} used for coloring substructures
     *
     * @param colorHash
     *            the {@link Map} used for coloring substructures
     */
    public void setColorHash(Map<IChemObject, Color> colorHash) {
        this.colorHash = colorHash;
        fireChange();
    }

    /**
     * Sets the showTooltip attribute.
     *
     * @param showToolTip
     *            The new value.
     */
    public void setShowTooltip(boolean showTooltip) {
        this.parameters.setShowTooltip(showTooltip);
        fireChange();
    }

    /**
     * Gets showTooltip attribute.
     *
     * @return The showTooltip value.
     */
    public boolean getShowTooltip() {
        return this.parameters.isShowTooltip();
    }

    /**
     * Gets the color used for drawing the part which was selected externally
     */
    public Color getExternalHighlightColor() {
        return this.parameters.getExternalHighlightColor();
    }

    /**
     * Sets the color used for drawing the part which was selected externally
     *
     * @param externalHighlightColor
     *            The color
     */
    public void setExternalHighlightColor(Color externalHighlightColor) {
        this.parameters.setExternalHighlightColor(externalHighlightColor);
    }

    /**
     * Gets the color used for drawing the part we are hovering over.
     */
    public Color getHoverOverColor() {
        return this.parameters.getHoverOverColor();
    }

    /**
     * Sets the color used for drawing the part we are hovering over.
     *
     * @param hoverOverColor
     *            The color
     */
    public void setHoverOverColor(Color hoverOverColor) {
        this.parameters.setHoverOverColor(hoverOverColor);
    }

    /**
     * Gets the color used for drawing the internally selected part.
     */
    public Color getSelectedPartColor() {
        return this.parameters.getSelectedPartColor();
    }

    /**
     * Sets the color used for drawing the internally selected part.
     *
     * @param selectedPartColor
     *            The color
     */
    public void setSelectedPartColor(Color selectedPartColor) {
        this.parameters.setSelectedPartColor(selectedPartColor);
    }

    public boolean showAtomTypeNames() {
        return this.parameters.isShowAtomTypeNames();
    }

    public void setShowAtomTypeNames(boolean showAtomTypeNames) {
        this.parameters.setShowAtomTypeNames(showAtomTypeNames);
    }

    public double getMargin() {
        return this.parameters.getMargin();
    }

    public void setMargin(double margin) {
        this.parameters.setMargin(margin);
    }

    public Color getBoundsColor() {
        return this.parameters.getBoundsColor();
    }

    public void setBoundsColor(Color color) {
        this.parameters.setBoundsColor(color);
    }

	/**
	 * @return the on screen radius of the selection element
	 */
	public double getSelectionRadius() {
		return this.parameters.getSelectionRadius();
	}

	public void setSelectionRadius(double selectionRadius) {
		this.parameters.setSelectionRadius(selectionRadius);
	}
	
	/**
	 * @return The color used for underlining not typeable atoms.
	 */
	public Color getNotTypeableUnderlineColor(){
	    return this.parameters.getNotTypeableUnderlineColor();
	}

	/**
	 *
	 * @param identifier
	 * @return
	*/
	public boolean getFlag(int identifier) {
	    return flags.get(identifier);
	}

	/**
     *
	 * @return
     */
	public Map<Integer, Boolean> getFlags() {
	    return flags;
	}

	/**
     *
     * @param identifier
     * @param flag
	 */
	public void setFlag(int identifier, boolean flag) {
	    flags.remove(identifier);
	    flags.put(identifier, flag);
    }
}
