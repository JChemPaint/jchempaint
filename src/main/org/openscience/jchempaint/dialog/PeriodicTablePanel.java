/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egone Willighagen, Miguel Rojas, Geert Josten
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
package org.openscience.jchempaint.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.periodictable.PeriodicTable;
import org.openscience.jchempaint.GT;
/**
 * JPanel version of the periodic system.
 *
 * @author        Egon Willighagen
 * @author        Geert Josten
 * @author        Miguel Rojas
 * @author        Konstantin Tokarev
 * @author        Mark Rijnbeek
 */
public class PeriodicTablePanel extends JPanel
{

    private static final long serialVersionUID = -2539418347261469740L;

    Vector<ICDKChangeListener> listeners = null;
    String selectedElement = null;

    private JPanel panel;
    //private JLabel label;
    private JLayeredPane layeredPane;

    private Map<JButton,Color> buttoncolors = new HashMap<JButton,Color>();

    public static int APPLICATION = 0;
    /*default*/
    public static int JCP = 1;
    /* 
     * set if the button should be written with html - which takes 
     * too long time for loading
     * APPLICATION = with html
     * JCP = default
     */ 

    /**
     *  Constructor of the PeriodicTablePanel object
     */
    public PeriodicTablePanel()
    {
        super();
        setLayout( new BorderLayout());
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(581, 435));
        JPanel tp = PTPanel();
        tp.setBounds(8,85,570, 340);

        panel = CreateLabelProperties(null);

        layeredPane.add(tp, new Integer(0));
        layeredPane.add(panel, new Integer(1));
        add(layeredPane);
    }

    private JPanel PTPanel()
    {

        JPanel panel = new JPanel();
        listeners = new Vector<ICDKChangeListener>();
        panel.setLayout(new GridLayout(0, 19));

        //--------------------------------
        Box.createHorizontalGlue();
        panel.add(Box.createHorizontalGlue());
        JButton butt = new JButton("1");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        //--------------------------------
        for (int i = 0; i < 16; i++)
        {
            Box.createHorizontalGlue();
            panel.add(Box.createHorizontalGlue());
        }
        butt = new JButton("18");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);


        butt = new JButton("1");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        panel.add(createButton(GT._("H")));

        butt = new JButton("2");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        for (int i = 0; i < 10; i++)
        {
            panel.add(Box.createHorizontalGlue());
        }
        butt = new JButton("13");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);

        butt = new JButton("14");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);

        butt = new JButton("15");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);

        butt = new JButton("16");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);

        butt = new JButton("17");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        //

        panel.add(createButton(GT._("He")));

        butt = new JButton("2");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);

        panel.add(createButton(GT._("Li")));

        panel.add(createButton(GT._("Be")));
        for (int i = 0; i < 10; i++)
        {
            panel.add(Box.createHorizontalGlue());
        }
        //no metall
        panel.add(createButton(GT._("B")));
        panel.add(createButton(GT._("C")));
        panel.add(createButton(GT._("N")));
        panel.add(createButton(GT._("O")));
        panel.add(createButton(GT._("F")));
        //
        panel.add(createButton(GT._("Ne")));

        butt = new JButton("3");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        panel.add(createButton(GT._("Na")));
        panel.add(createButton(GT._("Mg")));

        butt = new JButton("3");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("4");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("5");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("6");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("7");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("8");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("9");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("10");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("11");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        butt = new JButton("12");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        //no metall
        panel.add(createButton(GT._("Al")));
        panel.add(createButton(GT._("Si")));
        panel.add(createButton(GT._("P")));
        panel.add(createButton(GT._("S")));
        panel.add(createButton(GT._("Cl")));
        //
        panel.add(createButton(GT._("Ar")));

        butt = new JButton("4");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        panel.add(createButton(GT._("K")));
        panel.add(createButton(GT._("Ca")));
        //transition
        panel.add(createButton(GT._("Sc")));
        panel.add(createButton(GT._("Ti")));
        panel.add(createButton(GT._("V")));
        panel.add(createButton(GT._("Cr")));
        panel.add(createButton(GT._("Mn")));
        panel.add(createButton(GT._("Fe")));
        panel.add(createButton(GT._("Co")));
        panel.add(createButton(GT._("Ni")));
        panel.add(createButton(GT._("Cu")));
        panel.add(createButton(GT._("Zn")));
        //no metall
        panel.add(createButton(GT._("Ga")));
        panel.add(createButton(GT._("Ge")));
        panel.add(createButton(GT._("As")));
        panel.add(createButton(GT._("Se")));
        panel.add(createButton(GT._("Br")));
        //
        panel.add(createButton(GT._("Kr")));

        butt = new JButton("5");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        panel.add(createButton(GT._("Rb")));
        panel.add(createButton(GT._("Sr")));
        //transition
        panel.add(createButton(GT._("Y")));
        panel.add(createButton(GT._("Zr")));
        panel.add(createButton(GT._("Nb")));
        panel.add(createButton(GT._("Mo")));
        panel.add(createButton(GT._("Tc")));
        panel.add(createButton(GT._("Ru")));
        panel.add( createButton(GT._("Rh")));
        panel.add(createButton(GT._("Pd")));
        panel.add(createButton(GT._("Ag")));
        panel.add(createButton(GT._("Cd")));
        //no metall
        panel.add(createButton(GT._("In")));
        panel.add(createButton(GT._("Sn")));
        panel.add(createButton(GT._("Sb")));
        panel.add(createButton(GT._("Te")));
        panel.add(createButton(GT._("I")));
        //
        panel.add(createButton(GT._("Xe")));

        butt = new JButton("6");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        panel.add(createButton(GT._("Cs")));
        panel.add(createButton(GT._("Ba")));
        //transition
        panel.add(createButton(GT._("La")));
        panel.add(createButton(GT._("Hf")));
        panel.add(createButton(GT._("Ta")));
        panel.add(createButton(GT._("W")));
        panel.add(createButton(GT._("Re")));
        panel.add(createButton(GT._("Os")));
        panel.add(createButton(GT._("Ir")));
        panel.add(createButton(GT._("Pt")));
        panel.add(createButton(GT._("Au")));
        panel.add(createButton(GT._("Hg")));
        //no metall
        panel.add(createButton(GT._("Tl")));
        panel.add(createButton(GT._("Pb")));
        panel.add(createButton(GT._("Bi")));
        panel.add(createButton(GT._("Po")));
        panel.add(createButton(GT._("At")));
        //
        panel.add(createButton(GT._("Rn")));

        butt = new JButton("7");
        butt.setBorder(new EmptyBorder(2,2,2,2));
        panel.add(butt);
        panel.add(createButton(GT._("Fr")));
        panel.add(createButton(GT._("Ra")));
        //transition
        panel.add(createButton(GT._("Ac")));
        panel.add(createButton(GT._("Rf")));
        panel.add(createButton(GT._("Db")));
        panel.add(createButton(GT._("Sg")));
        panel.add(createButton(GT._("Bh")));
        panel.add(createButton(GT._("Hs")));
        panel.add(createButton(GT._("Mt")));
        panel.add(createButton(GT._("Ds")));
        panel.add(createButton(GT._("Rg")));
        for (int i = 0; i < 10; i++)
        {
            panel.add(Box.createHorizontalGlue());
        }
        //Acti
        panel.add(createButton(GT._("Ce")));
        panel.add(createButton(GT._("Pr")));
        panel.add(createButton(GT._("Nd")));
        panel.add(createButton(GT._("Pm")));
        panel.add(createButton(GT._("Sm")));
        panel.add(createButton(GT._("Eu")));
        panel.add(createButton(GT._("Gd")));
        panel.add(createButton(GT._("Tb")));
        panel.add(createButton(GT._("Dy")));
        panel.add(createButton(GT._("Ho")));
        panel.add(createButton(GT._("Er")));
        panel.add(createButton(GT._("Tm")));
        panel.add(createButton(GT._("Yb")));
        panel.add(createButton(GT._("Lu")));
        for (int i = 0; i < 5; i++)
        {
            panel.add(Box.createHorizontalGlue());
        }
        //Lacti
        panel.add( createButton(GT._("Th")));
        panel.add(createButton(GT._("Pa")));
        panel.add(createButton(GT._("U")));
        panel.add(createButton(GT._("Np")));
        panel.add(createButton(GT._("Pu")));
        panel.add(createButton(GT._("Am")));
        panel.add(createButton(GT._("Cm")));
        panel.add(createButton(GT._("Bk")));
        panel.add(createButton(GT._("Cf")));
        panel.add(createButton(GT._("Es")));
        panel.add(createButton(GT._("Fm")));
        panel.add(createButton(GT._("Md")));
        panel.add(createButton(GT._("No")));
        panel.add(createButton(GT._("Lr")));
        //End
        panel.setVisible(true);
        return panel;
    }

    /**
     * create button. Define the color of the font and background
     *
     *@param elementS  String of the element
     *@return button   JButton
     */
    private JButton createButton(String elementS)
    {
        Color colorF = new Color(0,0,0);

        Color colorB = null;
        String serie = PeriodicTable.getChemicalSeries(elementS);
        if(serie.equals("Noble Gasses"))
            colorB = new Color(255,153,255);
        else if(serie.equals("Halogens"))
            colorB = new Color(255,153,153); 
        else if(serie.equals("Nonmetals"))
            colorB = new Color(255,152,90);
        else if(serie.equals("Metalloids"))
            colorB = new Color(255,80,80);
        else if(serie.equals("Metals"))
            colorB = new Color(255,50,0);
        else if(serie.equals("Alkali Earth Metals"))
            colorB = new Color(102,150,255);
        else if(serie.equals("Alkali Metals"))
            colorB = new Color(130,130,255);
        else if(serie.equals("Transition metals"))
            colorB = new Color(255,255,110);
        else if(serie.equals("Lanthanides"))
            colorB = new Color(255,255,150);
        else if(serie.equals("Actinides"))
            colorB = new Color(255,255,200);

        JButton button = new ElementButton(elementS, new ElementMouseAction(), elementS, colorF);
        button.setBackground(colorB);
        button.setName(elementS);
        buttoncolors.put(button,colorB);

        return button;
    }

    
    /**
     *  Sets the selectedElement attribute of the PeriodicTablePanel object
     *
     *@param  selectedElement  The new selectedElement value
     */
    public void setSelectedElement(String selectedElement)
    {
        this.selectedElement = selectedElement;
    }


    /**
     *  Gets the selectedElement attribute of the PeriodicTablePanel object
     *
     *@return    The selectedElement value
     */
    public String getSelectedElement() throws IOException, CDKException {
        return selectedElement;
    }


    /**
     *  Adds a change listener to the list of listeners
     *
     *@param  listener  The listener added to the list
     */

    public void addCDKChangeListener(ICDKChangeListener listener)
    {
        listeners.add(listener);
    }


    /**
     *  Removes a change listener from the list of listeners
     *
     *@param  listener  The listener removed from the list
     */
    public void removeCDKChangeListener(ICDKChangeListener listener)
    {
        listeners.remove(listener);
    }


    /**
     *  Notifies registered listeners of certain changes that have occurred in this
     *  model.
     */
    public void fireChange()
    {
        EventObject event = new EventObject(this);
        for (int i = 0; i < listeners.size(); i++)
        {
            ((ICDKChangeListener) listeners.get(i)).stateChanged(event);
        }
    }

    /**
     * get translated name of element
     *
     * @author     Geoffrey R. Hutchison
     * @param atomic number of element
     * @return the name element to show
     */
    private String elementTranslator(int element) {
        String result;
        switch(element) {
        case 1:
            result = GT._("Hydrogen");
            break;
        case 2:
            result = GT._("Helium");
            break;
        case 3:
            result = GT._("Lithium");
            break;
        case 4:
            result = GT._("Beryllium");
            break;
        case 5:
            result = GT._("Boron");
            break;
        case 6:
            result = GT._("Carbon");
            break;
        case 7:
            result = GT._("Nitrogen");
            break;
        case 8:
            result = GT._("Oxygen");
            break;
        case 9:
            result = GT._("Fluorine");
            break;
        case 10:
            result = GT._("Neon");
            break;
        case 11:
            result = GT._("Sodium");
            break;
        case 12:
            result = GT._("Magnesium");
            break;
        case 13:
            result = GT._("Aluminum");
            break;
        case 14:
            result = GT._("Silicon");
            break;
        case 15:
            result = GT._("Phosphorus");
            break;
        case 16:
            result = GT._("Sulfur");
            break;
        case 17:
            result = GT._("Chlorine");
            break;
        case 18:
            result = GT._("Argon");
            break;
        case 19:
            result = GT._("Potassium");
            break;
        case 20:
            result = GT._("Calcium");
            break;
        case 21:
            result = GT._("Scandium");
            break;
        case 22:
            result = GT._("Titanium");
            break;
        case 23:
            result = GT._("Vanadium");
            break;
        case 24:
            result = GT._("Chromium");
            break;
        case 25:
            result = GT._("Manganese");
            break;
        case 26:
            result = GT._("Iron");
            break;
        case 27:
            result = GT._("Cobalt");
            break;
        case 28:
            result = GT._("Nickel");
            break;
        case 29:
            result = GT._("Copper");
            break;
        case 30:
            result = GT._("Zinc");
            break;
        case 31:
            result = GT._("Gallium");
            break;
        case 32:
            result = GT._("Germanium");
            break;
        case 33:
            result = GT._("Arsenic");
            break;
        case 34:
            result = GT._("Selenium");
            break;
        case 35:
            result = GT._("Bromine");
            break;
        case 36:
            result = GT._("Krypton");
            break;
        case 37:
            result = GT._("Rubidium");
            break;
        case 38:
            result = GT._("Strontium");
            break;
        case 39:
            result = GT._("Yttrium");
            break;
        case 40:
            result = GT._("Zirconium");
            break;
        case 41:
            result = GT._("Niobium");
            break;
        case 42:
            result = GT._("Molybdenum");
            break;
        case 43:
            result = GT._("Technetium");
            break;
        case 44:
            result = GT._("Ruthenium");
            break;
        case 45:
            result = GT._("Rhodium");
            break;
        case 46:
            result = GT._("Palladium");
            break;
        case 47:
            result = GT._("Silver");
            break;
        case 48:
            result = GT._("Cadmium");
            break;
        case 49:
            result = GT._("Indium");
            break;
        case 50:
            result = GT._("Tin");
            break;
        case 51:
            result = GT._("Antimony");
            break;
        case 52:
            result = GT._("Tellurium");
            break;
        case 53:
            result = GT._("Iodine");
            break;
        case 54:
            result = GT._("Xenon");
            break;
        case 55:
            result = GT._("Cesium");
            break;
        case 56:
            result = GT._("Barium");
            break;
        case 57:
            result = GT._("Lanthanum");
            break;
        case 58:
            result = GT._("Cerium");
            break;
        case 59:
            result = GT._("Praseodymium");
            break;
        case 60:
            result = GT._("Neodymium");
            break;
        case 61:
            result = GT._("Promethium");
            break;
        case 62:
            result = GT._("Samarium");
            break;
        case 63:
            result = GT._("Europium");
            break;
        case 64:
            result = GT._("Gadolinium");
            break;
        case 65:
            result = GT._("Terbium");
            break;
        case 66:
            result = GT._("Dysprosium");
            break;
        case 67:
            result = GT._("Holmium");
            break;
        case 68:
            result = GT._("Erbium");
            break;
        case 69:
            result = GT._("Thulium");
            break;
        case 70:
            result = GT._("Ytterbium");
            break;
        case 71:
            result = GT._("Lutetium");
            break;
        case 72:
            result = GT._("Hafnium");
            break;
        case 73:
            result = GT._("Tantalum");
            break;
        case 74:
            result = GT._("Tungsten");
            break;
        case 75:
            result = GT._("Rhenium");
            break;
        case 76:
            result = GT._("Osmium");
            break;
        case 77:
            result = GT._("Iridium");
            break;
        case 78:
            result = GT._("Platinum");
            break;
        case 79:
            result = GT._("Gold");
            break;
        case 80:
            result = GT._("Mercury");
            break;
        case 81:
            result = GT._("Thallium");
            break;
        case 82:
            result = GT._("Lead");
            break;
        case 83:
            result = GT._("Bismuth");
            break;
        case 84:
            result = GT._("Polonium");
            break;
        case 85:
            result = GT._("Astatine");
            break;
        case 86:
            result = GT._("Radon");
            break;
        case 87:
            result = GT._("Francium");
            break;
        case 88:
            result = GT._("Radium");
            break;
        case 89:
            result = GT._("Actinium");
            break;
        case 90:
            result = GT._("Thorium");
            break;
        case 91:
            result = GT._("Protactinium");
            break;
        case 92:
            result = GT._("Uranium");
            break;
        case 93:
            result = GT._("Neptunium");
            break;
        case 94:
            result = GT._("Plutonium");
            break;
        case 95:
            result = GT._("Americium");
            break;
        case 96:
            result = GT._("Curium");
            break;
        case 97:
            result = GT._("Berkelium");
            break;
        case 98:
            result = GT._("Californium");
            break;
        case 99:
            result = GT._("Einsteinium");
            break;
        case 100:
            result = GT._("Fermium");
            break;
        case 101:
            result = GT._("Mendelevium");
            break;
        case 102:
            result = GT._("Nobelium");
            break;
        case 103:
            result = GT._("Lawrencium");
            break;
        case 104:
            result = GT._("Rutherfordium");
            break;
        case 105:
            result = GT._("Dubnium");
            break;
        case 106:
            result = GT._("Seaborgium");
            break;
        case 107:
            result = GT._("Bohrium");
            break;
        case 108:
            result = GT._("Hassium");
            break;
        case 109:
            result = GT._("Meitnerium");
            break;
        case 110:
            result = GT._("Darmstadtium");
            break;
        case 111:
            result = GT._("Roentgenium");
            break;
        case 112:
            result = GT._("Ununbium");
            break;
        case 113:
            result = GT._("Ununtrium");
            break;
        case 114:
            result = GT._("Ununquadium");
            break;
        case 115:
            result = GT._("Ununpentium");
            break;
        case 116:
            result = GT._("Ununhexium");
            break;
        case 117:
            result = GT._("Ununseptium");
            break;
        case 118:
            result = GT._("Ununoctium");
            break;

        default:
            result = GT._("Unknown");
        }

        return result;
    }

    /**
     * get translated name of element
     *
     * @author     Konstantin Tokarev
     * @param  chemical serie to translate
     * @return the String to show
     */
    public String serieTranslator(String serie) {
        if(serie.equals("Noble Gasses"))
            return GT._("Noble Gases");
        else if(serie.equals("Halogens"))
            return GT._("Halogens"); 
        else if(serie.equals("Nonmetals"))
            return GT._("Nonmetals");
        else if(serie.equals("Metalloids"))
            return GT._("Metalloids");
        else if(serie.equals("Metals"))
            return GT._("Metals");
        else if(serie.equals("Alkali Earth Metals"))
            return GT._("Alkali Earth Metals");
        else if(serie.equals("Alkali Metals"))
            return GT._("Alkali Metals");
        else if(serie.equals("Transition metals"))
            return GT._("Transition metals");
        else if(serie.equals("Lanthanides"))
            return GT._("Lanthanides");
        else if(serie.equals("Actinides"))
            return GT._("Actinides");
        else
            return GT._("Unknown");
    }

    /**
     * get translated name of phase
     *
     * @author     Konstantin Tokarev
     * @param  phase name to translate
     * @return the String to show
     */
    public String phaseTranslator(String serie) {
        if(serie.equals("Gas"))
            return GT._("Gas");
        else if(serie.equals("Liquid"))
            return GT._("Liquid"); 
        else if(serie.equals("Solid"))
            return GT._("Solid");
        else
            return GT._("Unknown");
    }

    /**
     *  Description of the Class
     *
     *@author     steinbeck
     *@cdk.created    February 10, 2004
     */
    public class ElementMouseAction implements MouseListener
    {

        private static final long serialVersionUID = 6176240749900870566L;

        public void mouseClicked(MouseEvent e) {
            fireChange();
        }

        public void mouseEntered(MouseEvent e) {
            ElementButton button = (ElementButton) e.getSource();
            setSelectedElement(button.getElement());

            layeredPane.remove(panel);
            panel = CreateLabelProperties(button.getElement());
            layeredPane.add(panel, new Integer(1));
            layeredPane.repaint();

            button.setBackground(Color.LIGHT_GRAY);
        }

        public void mouseExited(MouseEvent e) {
            ((ElementButton)e.getSource()).setBackground(buttoncolors.get(e.getSource()));
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }
    /**
     * This action fragment a molecule which is on the frame JChemPaint
     *
     */
    class BackAction extends AbstractAction 
    {

        private static final long serialVersionUID = -8708581865777449553L;

        public void actionPerformed(ActionEvent e)
        {
            layeredPane.remove(panel);
            panel = CreateLabelProperties(null);
            layeredPane.add(panel, new Integer(1));
            layeredPane.repaint();
        }
    }

    class ElementButton extends JButton
    {

        private static final long serialVersionUID = 1504183423628680664L;

        private String element;


        /**
         *  Constructor for the ElementButton object
         *
         *@param  element  Description of the Parameter
         */
        public ElementButton(String element)
        {
            super("H");
            this.element = element;
        }
        /**
         *  Constructor for the ElementButton object
         * 
         * @param element Description of the Parameter
         * @param e       Description of the Parameter
         * @param color   Description of the Parameter
         * @param controlViewer Description of the Parameter
         */
        public ElementButton(
                String element, MouseListener e,String buttonString, Color color)
        {
            super(buttonString);
            setForeground(color);
            this.element = element;
            setFont(new Font("Times-Roman",Font.BOLD, 15));
            setBorder( new BevelBorder(BevelBorder.RAISED) );
            setToolTipText(elementTranslator(PeriodicTable.getAtomicNumber(element) ));
            addMouseListener(e);
        }
        /**
         *  Gets the element attribute of the ElementButton object
         *
         *@return    The element value
         */
        public String getElement()
        {
            return this.element;
        }
    }
    
    /**
     *  create the Label
     *
     *@param elementSymbol   String
     *@return pan JPanel
     */
    private JPanel CreateLabelProperties(String elementSymbol) 
    {
        JPanel pan = new JPanel();
        pan.setLayout(new BorderLayout());
        Color color = new Color(255,255,255);
        Point origin = new Point(120, 20);   
        JLabel label;
        if(elementSymbol != null){
            Integer group = PeriodicTable.getGroup(elementSymbol);
            label = new JLabel("<html><FONT SIZE=+2>"
                    +elementTranslator(PeriodicTable.getAtomicNumber(elementSymbol))+" ("+elementSymbol+")</FONT><br> "
                    +GT._("Atomic number")+" "+PeriodicTable.getAtomicNumber(elementSymbol)
                    + (group!=null ? ", "+GT._("Group")+" "+group : "")
                    +", "+GT._("Period")+" "+ PeriodicTable.getPeriod(elementSymbol)+"</html>");
            label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            pan.add(label,BorderLayout.NORTH);

            label = new JLabel("<html><FONT> "
                    +GT._("CAS RN:")+" "+ PeriodicTable.getCASId(elementSymbol)+"<br> "
                    +GT._("Element Category:")+" "+serieTranslator(PeriodicTable.getChemicalSeries(elementSymbol))+"<br> "
                    +GT._("State:")+" "+phaseTranslator(PeriodicTable.getPhase(elementSymbol))+"<br> "
                    +GT._("Electronegativity:")+" "
                    +(PeriodicTable.getPaulingElectronegativity(elementSymbol)==null ? GT._("undefined") : PeriodicTable.getPaulingElectronegativity(elementSymbol))+"<br>"
                    +"</FONT></html>");
            label.setMinimumSize(new Dimension(165,150));
            label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            pan.add(label,BorderLayout.CENTER);
        }
        else
        {
            label = new JLabel("     "+GT._("Periodic Table of elements"));
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.CENTER);
            label.setOpaque(true);
            label.setBackground(color);
            pan.add(label,BorderLayout.CENTER);
        }

        pan.setBackground(color);
        pan.setForeground(Color.black);
        pan.setBorder(BorderFactory.createLineBorder(Color.black));
        pan.setBounds(origin.x, origin.y, 255, 160);
        return pan;
    }
}

