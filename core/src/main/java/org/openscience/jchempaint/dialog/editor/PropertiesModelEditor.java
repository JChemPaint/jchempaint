/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *  Some portions Copyright (C) 2009 Konstantin Tokarev
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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JCPPropertyHandler;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;
import org.openscience.jchempaint.dialog.FieldTablePanel;
import org.openscience.jchempaint.dialog.ModifyRenderOptionsDialog;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.RenderingParameters;

/**
 * @cdk.bug          1525961
 */
public class PropertiesModelEditor extends FieldTablePanel implements ActionListener {

    private static final long serialVersionUID = 8694652992068950179L;

	private String guistring;

    private JCheckBox drawNumbers;

    //private JCheckBox showAtomAtomMapping;

    private JCheckBox useKekuleStructure;

    private JCheckBox showEndCarbons;

    private JCheckBox showExplicitHydrogens;

    private JCheckBox showImplicitHydrogens;

    private JCheckBox showAromaticity;

    //private JCheckBox showAromaticityCDKStyle;

    private JCheckBox colorAtomsByType;

    //private JCheckBox showToolTip;

    private JCheckBox showReactionBoxes;

    //private JCheckBox useAntiAliasing;

    private JCheckBox isFitToScreen;

    private JSlider bondWidth;

    //private JSlider bondLength;

    private JSlider highlightDistance;

    private JSlider atomRadius;

    private JSlider wedgeWidth;

    private ButtonGroup group = new ButtonGroup();
    
    private JRadioButton nonCompactShape;
    
    private JRadioButton compactShapeOval;

    private JRadioButton compactShapeSquare;

    //private JLabel fontName;

    //private JButton chooseFontButton;

    //private String currentFontName;

    private JLabel color;

    private JButton chooseColorButton;

    private Color currentColor;

    private JDialog frame;
    
    private AbstractJChemPaintPanel jcpPanel;

    private RendererModel model;

    private JCheckBox askForIOSettings;

    private JTextField undoStackSize;
    
    private JComboBox language;

    private JComboBox lookAndFeel;

    private GT.Language[] gtlanguages = GT.getLanguageList();
    
    private int tabtoshow;

    public PropertiesModelEditor(JDialog frame, AbstractJChemPaintPanel jcpPanel, int tabtoshow, String gui) {
        super(true);
        this.frame = frame;
		this.guistring = gui;
        this.jcpPanel = jcpPanel;
        this.tabtoshow = tabtoshow;
        constructPanel();
    }

    private void constructPanel() {

        JPanel rendererOptionsPanel = this.addTab(GT._("Display Preferences"));
		rendererOptionsPanel.setLayout(new java.awt.GridLayout(1,2,5,0));
		JPanel options1 = new JPanel();
		options1.setLayout(new BoxLayout(options1, BoxLayout.PAGE_AXIS));
		rendererOptionsPanel.add(options1);
		JPanel options2 = new JPanel();
		options2.setLayout(new BoxLayout(options2, BoxLayout.PAGE_AXIS));
		rendererOptionsPanel.add(options2);

        //addField("",new JPanel());
        //addField("Rendering Settings",new JPanel());
		addField("", new JLabel(" "), options1);

        drawNumbers = new JCheckBox(GT._("Draw atom numbers"));
		options1.add(drawNumbers);
        //addField(GT._("Draw atom numbers"), drawNumbers, options1);

        //showAtomAtomMapping = new JCheckBox();
        //addField(GT._("Show atom-atom mappings"), showAtomAtomMapping);

        useKekuleStructure = new JCheckBox(GT._("Explicit carbons"));
		options1.add(useKekuleStructure);
        //addField(GT._("Explicit carbons"), useKekuleStructure, options1);

        showEndCarbons = new JCheckBox(GT._("Show explicit methyl groups"));
		options1.add(showEndCarbons);
        //addField(GT._("Show explicit methyl groups"), showEndCarbons, options1);

        showExplicitHydrogens = new JCheckBox(GT._("Show explicit hydrogens"));
		options1.add(showExplicitHydrogens);
        //addField(GT._("Show explicit hydrogens"), showExplicitHydrogens, options1);

        showImplicitHydrogens = new JCheckBox(GT._("Show implicit hydrogens"));
		options1.add(showImplicitHydrogens);
        //addField(GT._("Show implicit hydrogens"), showImplicitHydrogens, options1);

        showAromaticity = new JCheckBox(GT._("Show aromatic ring circles"));
		options1.add(showAromaticity);
        //addField(GT._("Show aromatic ring circles"), showAromaticity, options1);

        //showAromaticityCDKStyle = new JCheckBox();
        //addField(GT._("CDK style aromatics"), showAromaticityCDKStyle);

        colorAtomsByType = new JCheckBox(GT._("Color atoms by element"));
		options1.add(colorAtomsByType);
        //addField(GT._("Color atoms by element"), colorAtomsByType, options1);

        //useAntiAliasing = new JCheckBox();
        //addField(GT._("Use Anti-Aliasing"), useAntiAliasing, options1);

        //showToolTip = new JCheckBox();
        //addField(GT._("Show tooltips"), showToolTip);

        showReactionBoxes = new JCheckBox(GT._("Show boxes around reactions"));
        options1.add(showReactionBoxes);


        isFitToScreen = new JCheckBox(GT._("Set fit to screen"));
		options1.add(isFitToScreen);
        //addField(GT._("Set fit to screen"), isFitToScreen, options1);

        addField("", new JLabel(" "), options1);
        addField("", new JSeparator(), options1);
		addField("", new JLabel(" "), options1);

        nonCompactShape = new JRadioButton(GT._("Show atom symbols"));
        group.add(nonCompactShape);
		options1.add(nonCompactShape);
        //addField(GT._("Show atom symbols"), nonCompactShape, options1);

        compactShapeOval = new JRadioButton(GT._("Show ball atoms"));
        group.add(compactShapeOval);
		options1.add(compactShapeOval);
        //addField(GT._("Show ball atoms"), compactShapeOval, options1);

        compactShapeSquare = new JRadioButton(GT._("Show square atoms"));
        group.add(compactShapeSquare);
		options1.add(compactShapeSquare);
        //addField(GT._("Show square atoms"), compactShapeSquare, options1);
        addField("", new JLabel(" "), options1);
		addField("", new JSeparator(), options1);

        atomRadius = new JSlider(0, 20);
        atomRadius.setSnapToTicks(true);
        atomRadius.setPaintLabels(true);
        atomRadius.setPaintTicks(true);
        atomRadius.setMajorTickSpacing(5);
        atomRadius.setMinorTickSpacing(1);
		addField("", new JLabel(" "), options2);
        addField(GT._("Atom size"), atomRadius, options2);
        addField("", new JLabel(" "), options2);
        addField("", new JSeparator(), options2);


        bondWidth = new JSlider(1, 5);
        bondWidth.setSnapToTicks(true);
        bondWidth.setPaintLabels(true);
        bondWidth.setPaintTicks(true);
        bondWidth.setMajorTickSpacing(1);
		addField("", new JLabel(" "), options2);
        addField(GT._("Bond width"), bondWidth, options2);
        addField("", new JLabel(" "), options2);
        addField("", new JSeparator(), options2);

        //bondLength = new JSlider(20, 60);
        //bondLength.setSnapToTicks(true);
        //bondLength.setPaintLabels(true);
        //bondLength.setPaintTicks(true);
        //bondLength.setMajorTickSpacing(5);
        //bondLength.setMinorTickSpacing(1);
        //addField(GT._("Bond length"), bondLength);

        highlightDistance = new JSlider(0, 25);
        highlightDistance.setSnapToTicks(true);
        highlightDistance.setPaintLabels(true);
        highlightDistance.setPaintTicks(true);
        highlightDistance.setMajorTickSpacing(5);
        highlightDistance.setMinorTickSpacing(1);
		addField("", new JLabel(" "), options2);
        addField(GT._("Highlight/Select diameter"), highlightDistance, options2);
        addField("", new JLabel(" "), options2);
        addField("", new JSeparator(), options2);

        wedgeWidth = new JSlider(1, 10);
        wedgeWidth.setSnapToTicks(true);
        wedgeWidth.setPaintLabels(true);
        wedgeWidth.setPaintTicks(true);
        wedgeWidth.setMajorTickSpacing(1);
		addField("", new JLabel(" "), options2);
        addField(GT._("Wedge width"), wedgeWidth, options2);
        addField("", new JLabel(" "), options2);
        //addField("", new JSeparator(), options2);

        /*
        currentFontName = "";
        fontName = new JLabel();
        addField(GT._("Font name"), fontName);
        chooseFontButton = new JButton(GT._("Choose Font..."));
        chooseFontButton.addActionListener(this);
        chooseFontButton.setActionCommand("chooseFont");
        addField("", chooseFontButton);
         */

        color = new JLabel();
		color.setText("      "+GT._("Background color")+"      ");
		color.setOpaque(true);
		addField("", new JLabel(" "), options1);
		options1.add(color);
		addField("", new JLabel(" "), options1);
        //addField(GT._("Background color"), color, options2);

        chooseColorButton = new JButton(GT._("Choose background color..."));
        chooseColorButton.addActionListener(this);
        chooseColorButton.setActionCommand("chooseColor");
		options1.add(chooseColorButton);
		addField("", new JLabel(" "), options1);
        //addField("", chooseColorButton, options2);
		
        JPanel otherOptionsPanel = this.addTab(GT._("Other Preferences"));
        
        undoStackSize = new JTextField();
        addField(GT._("Number of undoable operations"), undoStackSize, otherOptionsPanel);

        askForIOSettings = new JCheckBox();
        addField(GT._("Ask for CML settings when saving"), askForIOSettings, otherOptionsPanel);

		if (!guistring.equals(JChemPaintEditorApplet.GUI_APPLET)) {
            String [] lookAndFeels = {GT._("System"), "Metal", "Nimbus", "Motif", "GTK", "Windows"};
		    lookAndFeel = new JComboBox(lookAndFeels);
		    addField(GT._("Look and feel"), lookAndFeel, otherOptionsPanel);
            addField("", new JSeparator(), otherOptionsPanel);
        }

        String[] languagesstrings = new String[gtlanguages.length];
        for(int i=0;i<gtlanguages.length;i++){
            languagesstrings[i] = gtlanguages[i].language;
        }
        language = new JComboBox(languagesstrings);
        language.setName("language");
        for(int i=0;i<languagesstrings.length;i++){
            if(gtlanguages[i].code.equals(GT.getLanguage()))
                language.setSelectedIndex(i);
        }
        addField(GT._("User Interface Language"), language, otherOptionsPanel);
        
        this.tabbedPane.setSelectedIndex(tabtoshow);
    }

    public void setModel(RendererModel model) {
        this.model = model;
        drawNumbers.setSelected(model.getDrawNumbers());
        //showAtomAtomMapping.setSelected(model.getShowAtomAtomMapping());
        useKekuleStructure.setSelected(model.getKekuleStructure());
        showEndCarbons.setSelected(model.getShowEndCarbons());
        showExplicitHydrogens.setSelected(model.getShowExplicitHydrogens());
        showImplicitHydrogens.setSelected(model.getShowImplicitHydrogens());
        showAromaticity.setSelected(model.getShowAromaticity());
        //showAromaticityCDKStyle.setSelected(model.getShowAromaticityCDKStyle());
        colorAtomsByType.setSelected(model.getColorAtomsByType());
        //useAntiAliasing.setSelected(model.getUseAntiAliasing());
        //showToolTip.setSelected(model.getShowTooltip());
        showReactionBoxes.setSelected(model.getShowReactionBoxes());
        isFitToScreen.setSelected(model.isFitToScreen());

        atomRadius.setValue((int)model.getAtomRadius());
        bondWidth.setValue((int)model.getBondWidth());
        //bondLength.setValue((int)model.getBondLength());
        highlightDistance.setValue((int)model.getHighlightDistance());
        wedgeWidth.setValue((int)model.getWedgeWidth());

        if (!model.getIsCompact()){
            group.setSelected(nonCompactShape.getModel(), true);
        } else if(model.getCompactShape() == RenderingParameters.AtomShape.OVAL) {
            group.setSelected(compactShapeOval.getModel(), true);
        } else {
            group.setSelected(compactShapeSquare.getModel(), true);
        }
        /*
        currentFontName = model.getFontName();
        if (!currentFontName.equals("")) {
            fontName.setText(currentFontName);
        }
         */

        currentColor = model.getBackColor();
        if (currentColor != null) {
            color.setBackground(currentColor);
        }
        //the general settings
        Properties props = JCPPropertyHandler.getInstance(true).getJCPProperties();
        askForIOSettings.setSelected(props.getProperty("General.askForIOSettings").equals("true"));
        undoStackSize.setText(props.getProperty("General.UndoStackSize", "50"));
        if (!guistring.equals(JChemPaintEditorApplet.GUI_APPLET)) {
            lookAndFeel.setSelectedIndex(Integer.parseInt(props.getProperty("LookAndFeel", "0")));
        }
        language.setSelectedItem(props.getProperty("General.language"));
        validate();
    }

    public void applyChanges(boolean close) {
        Properties props = JCPPropertyHandler.getInstance(true).getJCPProperties();
        
        model.setDrawNumbers(drawNumbers.isSelected());
        //model.setShowAtomAtomMapping(showAtomAtomMapping.isSelected());
        model.setKekuleStructure(useKekuleStructure.isSelected());
        model.setShowEndCarbons(showEndCarbons.isSelected());
        model.setShowExplicitHydrogens(showExplicitHydrogens.isSelected());
        model.setShowImplicitHydrogens(showImplicitHydrogens.isSelected());
        model.setShowAromaticity(showAromaticity.isSelected());
        //model.setShowAromaticityCDKStyle(showAromaticityCDKStyle.isSelected());
        model.setColorAtomsByType(colorAtomsByType.isSelected());
        //model.setUseAntiAliasing(useAntiAliasing.isSelected());
        //model.setShowTooltip(showToolTip.isSelected());
        model.setShowReactionBoxes(showReactionBoxes.isSelected());
        model.setIsCompact(!nonCompactShape.isSelected());
        model.setFitToScreen(isFitToScreen.isSelected());

        model.setAtomRadius(atomRadius.getValue());
        //model.setBondLength(bondLength.getValue());
        model.setBondWidth(bondWidth.getValue());
        model.setHighlightDistance(highlightDistance.getValue());
        model.setWedgeWidth(wedgeWidth.getValue());

        if (compactShapeOval.isSelected()) {
            model.setCompactShape(RenderingParameters.AtomShape.OVAL);
        } else {
            model.setCompactShape(RenderingParameters.AtomShape.SQUARE);
        }

        model.setBackColor(currentColor);
        
        if(language.getSelectedIndex()!=-1) {
        	GT.setLanguage(gtlanguages[language.getSelectedIndex()].code);
        	jcpPanel.updateMenusWithLanguage();
        	updateLanguge();
        }

        props.setProperty("DrawNumbers",String.valueOf(drawNumbers.isSelected()));
        //props.setProperty("ShowAtomAtomMapping",String.valueOf(showAtomAtomMapping.isSelected()));
        props.setProperty("KekuleStructure",String.valueOf(useKekuleStructure.isSelected()));
        props.setProperty("ShowEndCarbons",String.valueOf(showEndCarbons.isSelected()));
        props.setProperty("ShowExplicitHydrogens",String.valueOf(showExplicitHydrogens.isSelected()));
        props.setProperty("ShowImplicitHydrogens",String.valueOf(showImplicitHydrogens.isSelected()));
        props.setProperty("ShowAromaticity",String.valueOf(showAromaticity.isSelected()));
        //props.setProperty("ShowAromaticityCDKStyle",String.valueOf(showAromaticityCDKStyle.isSelected()));
        props.setProperty("ColorAtomsByType",String.valueOf(colorAtomsByType.isSelected()));
        //props.setProperty("UseAntiAliasing",String.valueOf(useAntiAliasing.isSelected()));
        //props.setProperty("ShowTooltip",String.valueOf(showToolTip.isSelected()));
        props.setProperty("ShowReactionBoxes",String.valueOf(showReactionBoxes.isSelected()));
        props.setProperty("IsCompact",String.valueOf(!nonCompactShape.isSelected()));
        props.setProperty("FitToScreen",String.valueOf(isFitToScreen.isSelected()));

        props.setProperty("AtomRadius",String.valueOf(atomRadius.getValue()));
        //props.setProperty("BondLength",String.valueOf(bondLength.getValue()));
        props.setProperty("BondWidth",String.valueOf(bondWidth.getValue()));
        props.setProperty("HighlightDistance",String.valueOf(highlightDistance.getValue()));
        props.setProperty("WedgeWidth",String.valueOf(wedgeWidth.getValue()));

        if (compactShapeOval.isSelected()) {
            props.setProperty("CompactShape","oval");
        } else {
            props.setProperty("CompactShape","square");
        }

        //props.setFontName(currentFontName);
        props.setProperty("BackColor",String.valueOf(currentColor.getRGB()));
                
        //the general settings
        props.setProperty("General.askForIOSettings",
            String.valueOf(askForIOSettings.isSelected())
        );

        try{
            int size=Integer.parseInt(undoStackSize.getText());
            if(size<1 || size>100)
                throw new Exception("wrong number");
            props.setProperty("General.UndoStackSize",
                    undoStackSize.getText());
            jcpPanel.getRenderPanel().getUndoManager().setLimit(size);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, GT._("Number of undoable operations")+" "+GT._("must be a number from 1 to 100"), GT._("Number of undoable operations"), JOptionPane.WARNING_MESSAGE);
        }
        if (!guistring.equals(JChemPaintEditorApplet.GUI_APPLET)) {
            String lnfName="";
	    	try {
		    	switch(lookAndFeel.getSelectedIndex()) {
			    	case 0: lnfName = UIManager.getSystemLookAndFeelClassName(); break; // System
				    case 1: lnfName = UIManager.getCrossPlatformLookAndFeelClassName(); break; // Metal
                    case 2: lnfName = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"; break; // Nimbus
	    			case 3: lnfName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel"; break; // Motif
		    		case 4: lnfName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"; break; // GTK
                    case 5: lnfName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; break; // Windows
			        default: lnfName = "";
		        }
    			UIManager.setLookAndFeel(lnfName);
                SwingUtilities.updateComponentTreeUI(frame);
                frame.pack();
                // Apply to all instances of JChemPaint
                for (int i = 0; i < JChemPaintPanel.instances.size(); i++) {
                    Container c = JChemPaintPanel.instances.get(i).getTopLevelContainer();
                    if (c instanceof JFrame) {
                        JFrame f = (JFrame) c;
                        SwingUtilities.updateComponentTreeUI(f);
                        f.pack();
                    }
                }
                props.setProperty("LookAndFeel", String.valueOf(lookAndFeel.getSelectedIndex()));
                props.setProperty("LookAndFeelClass", lnfName);
		    }
            catch (UnsupportedLookAndFeelException e) {
	    	    JOptionPane.showMessageDialog(this, GT._("Look and feel")+" \""+lookAndFeel.getSelectedItem()+"\" "+GT._("is not supported on this platform"),GT._("Unsupported look&feel"), JOptionPane.WARNING_MESSAGE);
            }
            catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, GT._("Class not found:")+" "+ lnfName);
            }
            catch (InstantiationException e) {
            // handle exception
	    	    JOptionPane.showMessageDialog(this, GT._("Instantiation Exception:")+" "+ lnfName);
            }
            catch (IllegalAccessException e) {
                JOptionPane.showMessageDialog(this, GT._("Illegal Access: ") + lnfName);
            }
        }

        JCPPropertyHandler.getInstance(true).saveProperties();
        boolean languagechanged = false;
        for(int i=0;i<gtlanguages.length;i++){
            if(gtlanguages[i].language.equals((String)language.getSelectedItem())){
                if(props.getProperty("General.language")==null || 
                  !props.getProperty("General.language").equals(gtlanguages[i].code)){
                    props.setProperty("General.language", gtlanguages[i].code);
                    languagechanged = true;
                }
            }
        }
        JCPPropertyHandler.getInstance(true).saveProperties();
        if(languagechanged && !close){
            //we need to rediplay the dialog to change its language
            this.getParent().getParent().getParent().getParent().setVisible(false);
            RendererModel renderModel = 
                jcpPanel.get2DHub().getRenderer().getRenderer2DModel();
            ModifyRenderOptionsDialog frame =
                    new ModifyRenderOptionsDialog(jcpPanel,renderModel, 1);
            frame.setVisible(true);
        }
    }

    private void updateLanguge() {
        // TODO here the language of the editor window needs to be updated
    }

    /**
     * Required by the ActionListener interface.
     */
    public void actionPerformed(ActionEvent e) {
        /*
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
        */
        if ("chooseColor".equals(e.getActionCommand())) {
            Color newColor = JColorChooser.showDialog(this, GT._("Choose Background Color"), model.getBackColor());
            if (newColor != null) {
                currentColor = newColor;
                color.setBackground(currentColor);
            }
        }
    }     
}


