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

import org.openscience.cdk.renderer.RendererModel;
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
    private JCheckBox useAA;
    private JLabel fontName;
    private JButton chooseFontButton;
    private Font currentFont;
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
        currentFont = null;
        drawNumbers = new JCheckBox();
        addField("Draw atom numbers", drawNumbers);
        showAtomAtomMapping = new JCheckBox();
        addField("Show atom-atom mappings", showAtomAtomMapping);
        useKekuleStructure = new JCheckBox();
        addField("Explicit carbons", useKekuleStructure);
        showEndCarbons = new JCheckBox();
        addField("Show explicit methyl groups", showEndCarbons);
        showImplicitHydrogens = new JCheckBox();
        addField("Show implicit hydrogens if atom symbol is shown", showImplicitHydrogens);
        showAromaticity = new JCheckBox();
        addField("Use aromatic ring circles", showAromaticity);
        showAromaticityInCDKStyle = new JCheckBox();
        addField("Use CDK style aromaticity indicators", showAromaticityInCDKStyle);
        colorAtomsByType = new JCheckBox();
        addField("Color atoms by element", colorAtomsByType);
        useAA = new JCheckBox();
        addField("Use Anti-Aliasing", useAA);
        showToolTip = new JCheckBox();
        addField("Show tooltips", showToolTip);        
        showReactionBoxes = new JCheckBox();
        addField("Show boxes around reactions", showReactionBoxes);        
        fontName = new JLabel();
        addField("Font name", fontName);
        chooseFontButton = new JButton("Choose Font...");
        chooseFontButton.addActionListener(this);
        chooseFontButton.setActionCommand("chooseFont");
        addField("", chooseFontButton);
        color = new JLabel("BACKCOLOR");
        addField("Background color", color);
        chooseColorButton = new JButton("Choose Color...");
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
        useAA.setSelected(model.getUseAntiAliasing());
        showToolTip.setSelected(model.getShowTooltip());
        showReactionBoxes.setSelected(model.getShowReactionBoxes());
        currentFont = model.getFont();
        if (currentFont != null) {
            fontName.setText(currentFont.getFontName());
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
        model.setUseAntiAliasing(useAA.isSelected());
        model.setShowTooltip(showToolTip.isSelected());
        model.setShowReactionBoxes(showReactionBoxes.isSelected());
        model.setFont(currentFont);
        model.setBackColor(currentColor);
    }
    
    /**
     * Required by the ActionListener interface.
     */
    public void actionPerformed(ActionEvent e) {
        if ("chooseFont".equals(e.getActionCommand())) {
            Font newFont = JFontChooser.showDialog(this.frame, "Choose a Font", "Carbon Dioxide", currentFont);
            if (newFont != null) {
                currentFont = newFont;
                fontName.setText(currentFont.getFontName());
            }
        }
        if ("chooseColor".equals(e.getActionCommand())) {
            Color newColor = JColorChooser.showDialog(this, "Choose Background Color", model.getBackColor());
            if (newColor != null) {
                currentColor = newColor;
                color.setForeground(currentColor);
            }
        }
    }     
}


