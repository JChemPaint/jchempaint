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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

/**
 * Model for {@link Renderer} that contains settings for drawing objects.
 *
 * @cdk.module render
 * @cdk.svnrev $Revision$
 */
public class JChemPaintRendererModel extends RendererModel implements Serializable, Cloneable {

    private static final long serialVersionUID = -4420308906715213445L;

    private RenderingParameters parameters;

    private boolean rotating = false;

    private Map<Integer, Boolean> flags = new HashMap<Integer, Boolean>();
    /**
     * The color hash is used to color substructures.
     *
     * @see #getColorHash()
     */
    private Map<IChemObject, Color> colorHash =
        new Hashtable<IChemObject, Color>();

	/**
     * Constructor for the RendererModel.
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

    public Point2d getSelectionRotateControl() {
        return getSelection() != null && getSelection().isFilled()
               ? determineRotateControlPoint(getSelectionBounds())
               : null;
    }

    public RectangleElement getSelectionBounds() {
        return determineSelectionBounds(getSelection());
    }

    private Point2d determineRotateControlPoint(RectangleElement bounds) {
        double centerX = bounds.xCoord + (bounds.width/2);
        return new Point2d(centerX, bounds.yCoord + 3*(getHighlightDistance()/getScale()/getZoomFactor()));
    }

    private RectangleElement determineSelectionBounds(IChemObjectSelection selection) {
        if (selection == null) return null;
        Collection<IAtom> atoms = new HashSet<>(selection.elements(IAtom.class));
        for (IBond bond : selection.elements(IBond.class)) {
            atoms.add(bond.getBegin());
            atoms.add(bond.getEnd());
        }

        Bounds bounds = new Bounds();
        for (IAtom atom : atoms) {
            // check if we have the actual bounds
            Bounds atomBounds = atom.getProperty(CDKConstants.RENDER_BOUNDS);
            if (atomBounds != null) {
                bounds.add(atomBounds);
            } else {
                Point2d p = atom.getPoint2d();
                bounds.add(p.x, p.y);
            }
        }

        // if the selection is small, pad some width/height
        if (bounds.width() < 0.01 || bounds.height() < 0.01) {
            double pad = (getHighlightDistance()/getScale()/getZoomFactor());
            for (IAtom a : atoms) {
                bounds.add(a.getPoint2d().x - pad, a.getPoint2d().y - pad);
                bounds.add(a.getPoint2d().x - pad, a.getPoint2d().y + pad);
                bounds.add(a.getPoint2d().x + pad, a.getPoint2d().y - pad);
                bounds.add(a.getPoint2d().x + pad, a.getPoint2d().y + pad);
            }
        }

        return new RectangleElement(bounds.minX, bounds.maxY, bounds.width(), -bounds.height(),
                                    false, getSelectedPartColor());
    }

    @Override
    public void setSelection(IChemObjectSelection selection) {
        super.setSelection(selection);
    }

    public int getArrowHeadWidth() {
        if (hasParameter(BasicSceneGenerator.ArrowHeadWidth.class))
            return get(BasicSceneGenerator.ArrowHeadWidth.class).intValue();
        return new BasicSceneGenerator.ArrowHeadWidth().getDefault().intValue();
    }
    
    public void setArrowHeadWidth(int arrowHeadWidth) {
        if (hasParameter(BasicSceneGenerator.ArrowHeadWidth.class))
            set(BasicSceneGenerator.ArrowHeadWidth.class, (double) arrowHeadWidth);
    }

    public boolean getHighlightShapeFilled() {
        return this.parameters.isHighlightShapeFilled();
    }

    public void setHighlightShapeFilled(boolean highlightShapeFilled) {
        this.parameters.setHighlightShapeFilled(highlightShapeFilled);
    }

    public RenderingParameters.AtomShape getHighlightBondShape() {
        return this.parameters.getHighlightBondShape();
    }

    public double getWedgeWidth() {
        if (hasParameter(StandardGenerator.WedgeRatio.class))
            return get(StandardGenerator.WedgeRatio.class);
        return new StandardGenerator.WedgeRatio().getDefault();
    }

    public void setWedgeWidth(double wedgeWidth) {
        if (hasParameter(StandardGenerator.WedgeRatio.class))
            set(StandardGenerator.WedgeRatio.class, wedgeWidth);
    }

    public double getHashSpacing() {
        if (hasParameter(StandardGenerator.HashSpacing.class))
            return get(StandardGenerator.HashSpacing.class);
        return new StandardGenerator.HashSpacing().getDefault();
    }

    public void setHashSpacing(double wedgeWidth) {
        if (hasParameter(StandardGenerator.HashSpacing.class))
            set(StandardGenerator.HashSpacing.class, wedgeWidth);
    }

    public double getRingProportion() {
        if (hasParameter(BasicBondGenerator.TowardsRingCenterProportion.class))
            return get(BasicBondGenerator.TowardsRingCenterProportion.class);
        return new BasicBondGenerator.TowardsRingCenterProportion().getDefault();
    }

    public void setRingProportion(double ringProportion) {
        if (hasParameter(BasicBondGenerator.TowardsRingCenterProportion.class))
            set(BasicBondGenerator.TowardsRingCenterProportion.class, ringProportion);
    }

    public BasicAtomGenerator.Shape getCompactShape() {
        if (hasParameter(BasicAtomGenerator.CompactShape.class))
            return get(BasicAtomGenerator.CompactShape.class);
        return new BasicAtomGenerator.CompactShape().getDefault();
    }

    public void setCompactShape(BasicAtomGenerator.Shape compactShape) {
        if (hasParameter(BasicAtomGenerator.CompactShape.class))
            set(BasicAtomGenerator.CompactShape.class, compactShape);
    }

    public double getScale() {
        if (hasParameter(BasicSceneGenerator.Scale.class))
            return super.get(BasicSceneGenerator.Scale.class);
        return new BasicSceneGenerator.Scale().getDefault();
    }

    public void setScale(double scale) {
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
        if (hasParameter(BasicSceneGenerator.FontName.class))
            return get(BasicSceneGenerator.FontName.class);
        return new BasicSceneGenerator.FontName().getDefault();
    }

    /**
     * Set the name of the font family (Arial, etc).
     */
    public void setFontName(String fontName) {
        if (hasParameter(BasicSceneGenerator.FontName.class))
            set(BasicSceneGenerator.FontName.class, fontName);
    }

    /**
     * Get the style of the font (Normal, Bold).
     *
     * @return the style of the font as a member of the IFontManager.FontStyle
     *         enum
     */
    public IFontManager.FontStyle getFontStyle() {
        if (hasParameter(BasicSceneGenerator.UsedFontStyle.class))
            return get(BasicSceneGenerator.UsedFontStyle.class);
        return new BasicSceneGenerator.UsedFontStyle().getDefault();
    }

    /**
     * Set the style of font to use (Normal, Bold).
     *
     * @param fontStyle a member of the enum in {@link IFontManager}
     */
    public void setFontManager(IFontManager.FontStyle fontStyle) {
        if (hasParameter(BasicSceneGenerator.UsedFontStyle.class))
            set(BasicSceneGenerator.UsedFontStyle.class, fontStyle);
    }

    public boolean getIsCompact() {
        if (hasParameter(BasicAtomGenerator.CompactAtom.class))
            return get(BasicAtomGenerator.CompactAtom.class);
        return new BasicAtomGenerator.CompactAtom().getDefault();
    }

    public void setIsCompact(boolean compact) {
        if (hasParameter(BasicAtomGenerator.CompactAtom.class))
            set(BasicAtomGenerator.CompactAtom.class, compact);
    }

    public boolean getUseAntiAliasing() {
        if (hasParameter(BasicSceneGenerator.UseAntiAliasing.class))
            return get(BasicSceneGenerator.UseAntiAliasing.class);
        return new BasicSceneGenerator.UseAntiAliasing().getDefault();
    }

    public void setUseAntiAliasing(boolean bool) {
        if (hasParameter(BasicSceneGenerator.UseAntiAliasing.class))
            set(BasicSceneGenerator.UseAntiAliasing.class, bool);
    }

    public boolean getShowReactionBoxes() {
        return this.parameters.isShowReactionBoxes();
    }

    public void setShowReactionBoxes(boolean bool) {
        this.parameters.setShowReactionBoxes(bool);
        fireChange();
    }

    public boolean getShowMoleculeTitle() {
        if (hasParameter(BasicSceneGenerator.ShowMoleculeTitle.class))
            return get(BasicSceneGenerator.ShowMoleculeTitle.class);
        return new BasicSceneGenerator.ShowMoleculeTitle().getDefault();
    }

    public void setShowMoleculeTitle(boolean bool) {
        if (hasParameter(BasicSceneGenerator.ShowMoleculeTitle.class))
            set(BasicSceneGenerator.ShowMoleculeTitle.class, bool);
    }

    /**
     * The length on the screen of a typical bond.
     *
     * @return the user-selected length of a bond, or the default length.
     */
    public double getBondLength() {
        if (hasParameter(BasicSceneGenerator.BondLength.class))
            return get(BasicSceneGenerator.BondLength.class);
        return new BasicSceneGenerator.BondLength().getDefault();
    }

    /**
     * Set the length on the screen of a typical bond.
     *
     * @param length the length in pixels of a typical bond.
     *
     */
    public void setBondLength(double length) {
        if (hasParameter(BasicSceneGenerator.BondLength.class))
            set(BasicSceneGenerator.BondLength.class, length);
    }

    /**
     * Returns the distance between two lines in a double or triple bond
     *
     * @return the distance between two lines in a double or triple bond
     */
    public double getBondDistance() {
        if (hasParameter(BasicBondGenerator.BondDistance.class))
            return get(BasicBondGenerator.BondDistance.class);
        return new BasicBondGenerator.BondDistance().getDefault();
    }

    /**
     * Sets the distance between two lines in a double or triple bond
     *
     * @param bondDistance
     *            the distance between two lines in a double or triple bond
     */
    public void setBondDistance(double bondDistance) {
        if (hasParameter(BasicBondGenerator.BondDistance.class))
            set(BasicBondGenerator.BondDistance.class, bondDistance);
    }

    /**
     * Returns the thickness of a bond line.
     *
     * @return the thickness of a bond line
     */
    public double getBondWidth() {
        if (hasParameter(StandardGenerator.StrokeRatio.class))
            return get(StandardGenerator.StrokeRatio.class);
        return new StandardGenerator.StrokeRatio().getDefault();
    }

    /**
     * Sets the thickness of a bond line.
     *
     * @param bondWidth
     *            the thickness of a bond line
     */
    public void setBondWidth(double bondWidth) {
        if (hasParameter(StandardGenerator.StrokeRatio.class))
            set(StandardGenerator.StrokeRatio.class, bondWidth);
    }


    /**
     * Returns the thickness of a bond line.
     *
     * @return the thickness of a bond line
     */
    public double getBondSeparation() {
        if (hasParameter(StandardGenerator.BondSeparation.class))
            return get(StandardGenerator.BondSeparation.class);
        return new StandardGenerator.BondSeparation().getDefault();
    }

    /**
     * Sets the thickness of a bond line.
     *
     * @param bondSeparation
     *            the thickness of a bond line
     */
    public void setBondSeparation(double bondSeparation) {
        if (hasParameter(StandardGenerator.BondSeparation.class))
            set(StandardGenerator.BondSeparation.class, bondSeparation);
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
        if (hasParameter(BasicSceneGenerator.ZoomFactor.class))
            return get(BasicSceneGenerator.ZoomFactor.class);
        return new BasicSceneGenerator.ZoomFactor().getDefault();
    }

    /**
     * Returns the zoom factor for the drawing.
     *
     * @param zoomFactor
     *            the zoom factor for the drawing
     */
    public void setZoomFactor(double zoomFactor) {
        if (hasParameter(BasicSceneGenerator.ZoomFactor.class))
            set(BasicSceneGenerator.ZoomFactor.class, zoomFactor);
    }

    public boolean isFitToScreen() {
        if (hasParameter(BasicSceneGenerator.FitToScreen.class))
            return get(BasicSceneGenerator.FitToScreen.class);
        return new BasicSceneGenerator.FitToScreen().getDefault();
    }

    public void setFitToScreen(boolean value) {
        if (hasParameter(BasicSceneGenerator.FitToScreen.class))
            set(BasicSceneGenerator.FitToScreen.class, value);
    }

    /**
     * Returns the foreground color for the drawing.
     *
     * @return the foreground color for the drawing
     */
    public Color getForeColor() {
        if (hasParameter(BasicSceneGenerator.ForegroundColor.class))
            return get(BasicSceneGenerator.ForegroundColor.class);
        return new BasicSceneGenerator.ForegroundColor().getDefault();
    }

    /**
     * Sets the foreground color with which bonds and atoms are drawn
     *
     * @param foreColor
     *            the foreground color with which bonds and atoms are drawn
     */
    public void setForeColor(Color foreColor) {
        if (hasParameter(BasicSceneGenerator.ForegroundColor.class))
            set(BasicSceneGenerator.ForegroundColor.class, foreColor);
    }

    /**
     * Returns the background color
     *
     * @return the background color
     */
    public Color getBackColor() {
        if (hasParameter(BasicSceneGenerator.BackgroundColor.class))
            return get(BasicSceneGenerator.BackgroundColor.class);
        return new BasicSceneGenerator.BackgroundColor().getDefault();
    }

    /**
     * Sets the background color
     *
     * @param backColor
     *            the background color
     */
    public void setBackColor(Color backColor) {
        if (hasParameter(BasicSceneGenerator.BackgroundColor.class))
            set(BasicSceneGenerator.BackgroundColor.class, backColor);
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
        if (hasParameter(BasicAtomGenerator.KekuleStructure.class))
            return get(BasicAtomGenerator.KekuleStructure.class);
        return new BasicAtomGenerator.KekuleStructure().getDefault();
    }

    public void setKekuleStructure(boolean kekule) {
        if (hasParameter(BasicAtomGenerator.KekuleStructure.class))
            set(BasicAtomGenerator.KekuleStructure.class, kekule);
    }

    public boolean getColorAtomsByType() {
        if (hasParameter(BasicAtomGenerator.ColorByType.class))
            return get(BasicAtomGenerator.ColorByType.class);
        return new BasicAtomGenerator.ColorByType().getDefault();
    }

    public void setColorAtomsByType(boolean bool) {
        if (hasParameter(BasicAtomGenerator.ColorByType.class))
            set(BasicAtomGenerator.ColorByType.class, bool);
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
        if (hasParameter(RingGenerator.ShowAromaticity.class))
            return get(RingGenerator.ShowAromaticity.class);
        return new RingGenerator.ShowAromaticity().getDefault();
    }

    public void setShowAromaticity(boolean showIt) {
        if (hasParameter(RingGenerator.ShowAromaticity.class))
            set(RingGenerator.ShowAromaticity.class, showIt);
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
        if (hasParameter(BasicBondGenerator.DefaultBondColor.class))
            return get(BasicBondGenerator.DefaultBondColor.class);
        return new BasicBondGenerator.DefaultBondColor().getDefault();
    }

    public void setDefaultBondColor(Color defaultBondColor) {
        if (hasParameter(BasicBondGenerator.DefaultBondColor.class))
            set(BasicBondGenerator.DefaultBondColor.class, defaultBondColor);
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
        if (hasParameter(BasicAtomGenerator.AtomRadius.class))
            return get(BasicAtomGenerator.AtomRadius.class);
        return new BasicAtomGenerator.AtomRadius().getDefault();
    }

    /**
     * Set the radius of the compact atom representation.
     *
     * @param atomRadius the size of the compact atom symbol.
     *
     */
    public void setAtomRadius(double atomRadius) {
        if (hasParameter(BasicAtomGenerator.AtomRadius.class))
            set(BasicAtomGenerator.AtomRadius.class, atomRadius);
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
        if (hasParameter(BasicAtomGenerator.AtomColorer.class))
            return get(BasicAtomGenerator.AtomColorer.class);
        return new BasicAtomGenerator.AtomColorer().getDefault();
    }

    /**
     * Sets the atom colorer.
     *
     * @param atomColorer
     *            the new colorer.
     */
    public void setAtomColorer(final IAtomColorer atomColorer) {
        if (hasParameter(BasicAtomGenerator.AtomColorer.class))
            set(BasicAtomGenerator.AtomColorer.class, atomColorer);
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
     * @param showTooltip
     *            The new value.
     */
    public void setShowTooltip(boolean showTooltip) {
        if (hasParameter(BasicSceneGenerator.ShowTooltip.class))
            set(BasicSceneGenerator.ShowTooltip.class, showTooltip);
    }

    /**
     * Gets showTooltip attribute.
     *
     * @return The showTooltip value.
     */
    public boolean getShowTooltip() {
        if (hasParameter(BasicSceneGenerator.ShowTooltip.class))
            return get(BasicSceneGenerator.ShowTooltip.class);
        return new BasicSceneGenerator.ShowTooltip().getDefault();
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
        if (hasParameter(BasicSceneGenerator.Margin.class))
            return get(BasicSceneGenerator.Margin.class);
        return new BasicSceneGenerator.Margin().getDefault();
    }

    public void setMargin(double margin) {
        if (hasParameter(BasicSceneGenerator.Margin.class))
            set(BasicSceneGenerator.Margin.class, margin);
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

    /**
     * Get the highlighted atom or bond.
     * @return the highlighted atom or bond.
     */
    IChemObject getHighlight() {
        return getHighlightedAtom() != null ? getHighlightedAtom()
                                            : getHighlightedBond();
    }

    /**
     * Set the highlighted atom or bond.
     * @return the highlighted atom or bond.
     */
    void setHighlight(IChemObject chemObject) {
        if (chemObject instanceof IAtom) {
            setHighlightedAtom((IAtom) chemObject);
        } else if (chemObject instanceof IBond) {
            setHighlightedBond((IBond) chemObject);
        }
    }

    /**
     * Change the highlighted atom/bond with arrow keys.
     */
    public void moveHighlight(int key) {
        IChemObject hotspot = getHighlight();
        if (hotspot instanceof IAtom) {
            Point2d p = new Point2d(((IAtom) hotspot).getPoint2d());
            switch (key) {
                case KeyEvent.VK_UP: p.y++; break;
                case KeyEvent.VK_DOWN: p.y--; break;
                case KeyEvent.VK_LEFT: p.x--; break;
                case KeyEvent.VK_RIGHT: p.x++; break;
            }
            IBond best = null;
            double bestDist = Double.NaN;
            for (IBond bond : ((IAtom) hotspot).bonds()) {
                double dist = bond.get2DCenter().distanceSquared(p);
                if (best == null || dist <= bestDist) {
                    best = bond;
                    bestDist = dist;
                }
            }
            if (best != null)
                setHighlight(best);
        }
        else if (hotspot instanceof IBond) {
            IBond bond = (IBond) hotspot;
            Point2d p = new Point2d(bond.get2DCenter());
            switch (key) {
                case KeyEvent.VK_UP: p.y++; break;
                case KeyEvent.VK_DOWN: p.y--; break;
                case KeyEvent.VK_LEFT: p.x--; break;
                case KeyEvent.VK_RIGHT: p.x++; break;
            }
            if (bond.getBegin().getPoint2d().distanceSquared(p) <
                bond.getEnd().getPoint2d().distanceSquared(p))
                setHighlight(bond.getBegin());
            else
                setHighlight(bond.getEnd());
        }
    }

    public void setRotating(boolean b) {
        rotating = b;
    }

    public boolean isRotating() {
        return rotating;
    }
}
