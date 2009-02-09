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
package org.openscience.jchempaint.dialog.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;

import org.openscience.cdk.renderer.RendererModel;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.dialog.FieldTablePanel;

import com.ozten.font.JFontChooser;

/**
 * @cdk.bug          1525961
 */
public class RendererModelEditor extends FieldTablePanel implements ActionListener {
    
    private static final long serialVersionUID = 8694652992068950179L;
    
    private JCheckBox drawNumbers;
    
    private JCheckBox showAtomAtomMapping;
    
    private JCheckBox useKekuleStructure;
    
    private JCheckBox showEndCarbons;
    
    private JCheckBox showImplicitHydrogens;
    
    private JCheckBox showAromaticity;
    
    private JCheckBox showAromaticityInCDKStyle;
    
    private JCheckBox colorAtomsByType;
    
    private JCheckBox showToolTip;
    
    private JCheckBox showReactionBoxes;
    
    private JCheckBox useAntiAliasing;
    
    private JCheckBox isCompact;
    
    private JSlider bondWidth;
    
    private JSlider bondLength;
    
    private JSlider highlightDistance;
    
    private JSlider atomRadius;

    private JLabel fontName;
    
    private JButton chooseFontButton;
    
    private String currentFontName;
    
    private JLabel color;
    
    private JButton chooseColorButton;
    
    private Color currentColor;
    
    private JFrame frame;
    
    private RendererModel model;
    
	public RendererModelEditor(JFrame frame) {
        super();
        this.frame = frame;
        constructPanel();
	}
    
    private void constructPanel() {
        
        drawNumbers = new JCheckBox();
        addField(GT._("Draw atom numbers"), drawNumbers);
        
        showAtomAtomMapping = new JCheckBox();
        addField(GT._("Show atom-atom mappings"), showAtomAtomMapping);
        
        useKekuleStructure = new JCheckBox();
        addField(GT._("Explicit carbons"), useKekuleStructure);
        
        showEndCarbons = new JCheckBox();
        addField(GT._("Show explicit methyl groups"), showEndCarbons);
        
        showImplicitHydrogens = new JCheckBox();
        addField(GT._("Show implicit hydrogens if atom symbol is shown"), 
                showImplicitHydrogens);
        
        showAromaticity = new JCheckBox();
        addField(GT._("Use aromatic ring circles"), showAromaticity);
        
        showAromaticityInCDKStyle = new JCheckBox();
        addField(GT._("Use CDK style aromaticity indicators"), 
                showAromaticityInCDKStyle);
        
        colorAtomsByType = new JCheckBox();
        addField(GT._("Color atoms by element"), colorAtomsByType);
        
        useAntiAliasing = new JCheckBox();
        addField(GT._("Use Anti-Aliasing"), useAntiAliasing);
        
        showToolTip = new JCheckBox();
        addField(GT._("Show tooltips"), showToolTip);
        
        showReactionBoxes = new JCheckBox();
        addField(GT._("Show boxes around reactions"), showReactionBoxes);
        
        isCompact = new JCheckBox();
        addField(GT._("Show atoms in compact form"), isCompact);
        
        atomRadius = new JSlider(0, 20);
        atomRadius.setSnapToTicks(true);
        atomRadius.setPaintLabels(true);
        atomRadius.setPaintTicks(true);
        atomRadius.setMajorTickSpacing(5);
        atomRadius.setMajorTickSpacing(1);
        addField(GT._("Atom size"), atomRadius);
        
        bondWidth = new JSlider(1, 5);
        bondWidth.setSnapToTicks(true);
        bondWidth.setPaintLabels(true);
        bondWidth.setPaintTicks(true);
        bondWidth.setMajorTickSpacing(1);
        addField(GT._("Bond width"), bondWidth);
        
        bondLength = new JSlider(20, 60);
        bondLength.setSnapToTicks(true);
        bondLength.setPaintLabels(true);
        bondLength.setPaintTicks(true);
        bondLength.setMajorTickSpacing(5);
        bondLength.setMinorTickSpacing(1);
        addField(GT._("Bond length"), bondLength);
        
        highlightDistance = new JSlider(0, 25);
        highlightDistance.setSnapToTicks(true);
        highlightDistance.setPaintLabels(true);
        highlightDistance.setPaintTicks(true);
        highlightDistance.setMajorTickSpacing(5);
        highlightDistance.setMinorTickSpacing(1);
        addField(GT._("Highlight Distance"), highlightDistance);
        
        currentFontName = "";
        fontName = new JLabel();
        addField(GT._("Font name"), fontName);
        
        chooseFontButton = new JButton(GT._("Choose Font..."));
        chooseFontButton.addActionListener(this);
        chooseFontButton.setActionCommand("chooseFont");
        addField("", chooseFontButton);
        
        color = new JLabel(GT._("BACKCOLOR"));
        addField(GT._("Background color"), color);
        
        chooseColorButton = new JButton(GT._("Choose Color..."));
        chooseColorButton.addActionListener(this);
        chooseColorButton.setActionCommand("chooseColor");
        addField("", chooseColorButton);
    }
    
    public void setModel(RendererModel model) {
        this.model = model;
        drawNumbers.setSelected(model.getDrawNumbers());
        showAtomAtomMapping.setSelected(model.getShowAtomAtomMapping());
        useKekuleStructure.setSelected(model.getKekuleStructure());
        showEndCarbons.setSelected(model.getShowEndCarbons());
        showImplicitHydrogens.setSelected(model.getShowImplicitHydrogens());
        showAromaticity.setSelected(model.getShowAromaticity());
        showAromaticityInCDKStyle.setSelected(model.getShowAromaticityInCDKStyle());
        colorAtomsByType.setSelected(model.getColorAtomsByType());
        useAntiAliasing.setSelected(model.getUseAntiAliasing());
        showToolTip.setSelected(model.getShowTooltip());
        showReactionBoxes.setSelected(model.getShowReactionBoxes());
        isCompact.setSelected(model.getIsCompact());
        
        atomRadius.setValue((int)model.getAtomRadius());
        bondWidth.setValue((int)model.getBondWidth());
        bondLength.setValue((int)model.getBondLength());
        highlightDistance.setValue((int)model.getHighlightDistance());
        
        currentFontName = model.getFontName();
        if (!currentFontName.equals("")) {
            fontName.setText(currentFontName);
        }
        currentColor = model.getBackColor();
        if (currentColor != null) {
            color.setForeground(currentColor);
        }
        validate();
    }
	
    public void applyChanges() {
        model.setDrawNumbers(drawNumbers.isSelected());
        model.setShowAtomAtomMapping(showAtomAtomMapping.isSelected());
        model.setKekuleStructure(useKekuleStructure.isSelected());
        model.setShowEndCarbons(showEndCarbons.isSelected());
        model.setShowImplicitHydrogens(showImplicitHydrogens.isSelected());
        model.setShowAromaticity(showAromaticity.isSelected());
        model.setShowAromaticityInCDKStyle(showAromaticityInCDKStyle.isSelected());
        model.setColorAtomsByType(colorAtomsByType.isSelected());
        model.setUseAntiAliasing(useAntiAliasing.isSelected());
        model.setShowTooltip(showToolTip.isSelected());
        model.setShowReactionBoxes(showReactionBoxes.isSelected());
        model.setIsCompact(isCompact.isSelected());
        
        model.setAtomRadius(atomRadius.getValue());
        model.setBondLength(bondLength.getValue());
        model.setBondWidth(bondWidth.getValue());
        model.setHighlightDistance(highlightDistance.getValue());
        
        model.setFontName(currentFontName);
        model.setBackColor(currentColor);
    }
    
    /**
     * Required by the ActionListener interface.
     */
    public void actionPerformed(ActionEvent e) {
        if ("chooseFont".equals(e.getActionCommand())) {
            Font newFont = JFontChooser.showDialog(
                    this.frame,
                    GT._("Choose a Font"),
                    GT._("Carbon Dioxide"),
                    new Font(currentFontName, Font.PLAIN, 12));
            if (newFont != null) {
                currentFontName = newFont.getFontName();
                fontName.setText(currentFontName);
            }
        }
        if ("chooseColor".equals(e.getActionCommand())) {
            Color newColor = JColorChooser.showDialog(this, GT._("Choose Background Color"), model.getBackColor());
            if (newColor != null) {
                currentColor = newColor;
                color.setForeground(currentColor);
            }
        }
    }     
}


