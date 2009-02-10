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
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.openscience.cdk.Element;
import org.openscience.cdk.PeriodicTableElement;
import org.openscience.cdk.config.ElementPTFactory;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.jchempaint.GT;
/**
 * JPanel version of the periodic system.
 *
 * @author        Egon Willighagen
 * @author        Geert Josten
 * @author        Miguel Rojas
 */
public class PeriodicTablePanel extends JPanel
{

    private static final long serialVersionUID = -2539418347261469740L;
    
    Vector listeners = null;
	PeriodicTableElement selectedElement = null;
	
	private JPanel panel;
	//private JLabel label;
	private JLayeredPane layeredPane;
	
	private ElementPTFactory factory;
	private LoggingTool logger;
	
	public static int APPLICATION = 0;
	/*default*/
	public static int JCP = 1;
	/* 
	 * set if the button should be written with html - which takes 
	 * too long time for loading
	 * APPLICATION = with html
	 * JCP = default
	 */ 	
	private int controlViewerButton;
	
	/**
	*  Constructor of the PeriodicTablePanel object
	*/
	public PeriodicTablePanel()
	{
		super();
		setLayout( new BorderLayout());
		try {
			factory = ElementPTFactory.getInstance();
		} catch (Exception ex1) 
		{
			logger.error(ex1.getMessage());
			logger.debug(ex1);
		}
		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(611, 575));
		layeredPane.setBorder(BorderFactory.createTitledBorder(
                                    GT._("Periodic Table for JChemPaint")));
		JPanel tp = PTPanel();
		tp.setBounds(8,85,600, 480);
		
		JButton button = new JButton(GT._("Reload"));
		button.setVerticalTextPosition(AbstractButton.BOTTOM);
		button.setHorizontalTextPosition(AbstractButton.CENTER);
		button.setMnemonic(KeyEvent.VK_R);
		button.setToolTipText(GT._("Click this button to go back to PeriodicTable."));
		button.setFont(new Font("Times-Roman",Font.BOLD, 10));
		button.setBounds(510, 20, 90, 20);
		button.addActionListener( new BackAction() );
		panel = CreateLabelProperties(null);
		
		layeredPane.add(button, new Integer(1));
		layeredPane.add(tp, new Integer(0));
		layeredPane.add(panel, new Integer(1));
		add(layeredPane);
	}
	
	private JPanel PTPanel()
	{

		controlViewerButton = PeriodicTablePanel.JCP;
		JPanel panel = new JPanel();
		listeners = new Vector();
		panel.setLayout(new GridLayout(0, 18));
		
		//--------------------------------
		JButton butt = new JButton("IA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		//--------------------------------
		for (int i = 0; i < 16; i++)
		{
			Box.createHorizontalGlue();
			panel.add(Box.createHorizontalGlue());
		}
		butt = new JButton("VIIIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		panel.add(createButton("H"));
		
		butt = new JButton("IIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		for (int i = 0; i < 10; i++)
		{
			panel.add(Box.createHorizontalGlue());
		}
		butt = new JButton("IIIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		butt = new JButton("VIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		butt = new JButton("VA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		butt = new JButton("VIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		butt = new JButton("VIIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		//
		
		panel.add(createButton(GT._("He")));
		
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
		
		panel.add(createButton(GT._("Na")));
		panel.add(createButton(GT._("Mg")));
		
		butt = new JButton("IIIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("IVB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("VB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("VIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("VIIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("--");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("VIIIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("--");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("IB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("IIB");
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
		for (int i = 0; i < 9; i++)
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
		for (int i = 0; i < 4; i++)
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
	* create button. Difine the color of the font and background
	*
	*@param elementS  String of the element
	*@return button   JButton
	*/
	private JButton createButton(String elementS)
	{
		PeriodicTableElement element = factory.configure(new PeriodicTableElement(elementS));
		String colorFS = "000000";
		Color colorF = new Color(0,0,0);
		String colorPh = element.getPhase();
		if(colorPh.equals("Solid")){
			colorFS = "000000"; 
			colorF = new Color(0,0,0);
		}
		else if(colorPh.equals("Gas")){
			colorFS = "CC0033"; 
			colorF = new Color(200,0,0);
		}
		else if(colorPh.equals("Liquid")){
			colorFS = "3300CC"; 
			colorF = new Color(0,0,200);
		}
		else if(colorPh.equals("Synthetic")){
			colorFS = "FFCC00";
			colorF = new Color(235,208,6);
		}
		
		Color colorB = null;
		String serie = element.getChemicalSerie();
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
		
		JButton button = new ElementButton(element, new ElementButtonAction(), getTextButton(element,colorFS), colorF);
		button.setBackground(colorB);
		
		return button;
	}
	/**
	 *  Sets the selectedElement attribute of the PeriodicTablePanel object
	 *
	 *@param  selectedElement  The new selectedElement value
	 */
	public void setSelectedElement(PeriodicTableElement selectedElement)
	{
		this.selectedElement = selectedElement;
	}


	/**
	 *  Gets the selectedElement attribute of the PeriodicTablePanel object
	 *
	 *@return    The selectedElement value
	 */
	public Element getSelectedElement()
	{
		return PeriodicTableElement.configure(selectedElement);
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
	 * get the format which the text will be introduce into the button
	 * 
	 * @param element The PeriodicTableElement
	 * @return the String to show
	 */
	public String getTextButton(PeriodicTableElement element, String color){
		String buttonString = null;
		switch (controlViewerButton) {
			case 0: buttonString ="<html><p><u><FONT SIZE=-2>"+element.getAtomicNumber()+"</FONT></u></p><p><font COLOR="+color+">"
			+element.getSymbol()+"<font></p></html>";break;
			case 1: buttonString = element.getSymbol();break;
			default: buttonString = element.getSymbol();break;
		}
		return buttonString;
	}


	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 *@cdk.created    February 10, 2004
	 */
	public class ElementButtonAction extends AbstractAction
	{
	    
        private static final long serialVersionUID = 6176240749900870566L;

		public void actionPerformed(ActionEvent e)
		{
			ElementButton button = (ElementButton) e.getSource();
			setSelectedElement(button.getElement());
			
			layeredPane.remove(panel);
			panel = CreateLabelProperties(button.getElement());
			layeredPane.add(panel, new Integer(1));
			layeredPane.repaint();
			
			fireChange();
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
        
        private PeriodicTableElement element;


		/**
		 *  Constructor for the ElementButton object
		 *
		 *@param  element  Description of the Parameter
		 */
		public ElementButton(PeriodicTableElement element)
		{
			super("H");
			this.element = factory.configure(element);
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
				PeriodicTableElement element, ElementButtonAction e,String buttonString, Color color)
		{
			super(buttonString);
			if(controlViewerButton == JCP){
				setForeground(color);
			}
			
			this.element = element;
			setFont(new Font("Times-Roman",Font.BOLD, 15));
			setBorder( new BevelBorder(BevelBorder.RAISED) );
			setToolTipText(element.getName());
			addActionListener(e);
		}
		/**
		 *  Gets the element attribute of the ElementButton object
		 *
		 *@return    The element value
		 */
		public PeriodicTableElement getElement()
		{
			return this.element;
		}
	}
	/**
	*  create the Label
	*
	*@param element   PeriodicTableElement
	*@return pan      JPanel
	*/
	private JPanel CreateLabelProperties(PeriodicTableElement element) 
	{
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		Color color = new Color(255,255,255);
		Point origin = new Point(90, 20);   	
		JLabel label;
		if(element != null){
			if(controlViewerButton == PeriodicTablePanel.APPLICATION)
			{
				label = new JLabel("<html><PRE>   <FONT SIZE=+2>"
					+element.getSymbol()+"</FONT>"
					+":   At.No "+element.getAtomicNumber()
					+", Group "+element.getGroup()+", Period "
					+ element.getPeriod()+"</PRE></html>");
				pan.add(label,BorderLayout.NORTH);
				
				label = new JLabel("<html><PRE><FONT SIZE=-2>"
					+" CAS id: "+element.getCASid()+"<br>"
					+" Name: "+element.getName()+"<br>"
					+" Serie: "+element.getChemicalSerie()+"<br>"
					+" State: "+element.getPhase()+"<br>"
					+" Appar: XXXX<br>"
					+" Mp: 0.0000<br>"
					+" Bp: 0.0000<br>"
					+" Conduc: 0.0000<br>"
					+" Densit: 0.0000<br>"
					+" VaporH: 0.0000<br>"
					+" XXXX: 0.0000<br>"
					+" XXXX: 0.0000<br>"
					+"</FONT></PRE></html>");
				label.setMinimumSize(new Dimension(145,150));
				pan.add(label,BorderLayout.WEST);
				
				label = new JLabel("<html><PRE><FONT SIZE=-2>"
					+" At. Weight: 0.000000<br>"
					+" At. Radius: 0.0000<br>"
					+" Cov Radius: 0.0000<br>"
					+" VW Radius: 0.0000<br>"
					+" Io Radius: 0.0000<br>"
					+" e config: 1s1<br>"
					+" Valency e: 1s1<br>"
					+" Electro: 0.0<br>"
					+" Oxid: 1<br>"
					+" IP: 0.0000<br>"
					+" Crist: XXXXXX<br>"
					+" XXXX: 0.0000<br>"
					+"</FONT></PRE></html>");
				label.setMinimumSize(new Dimension(145,150));
				pan.add(label,BorderLayout.EAST);
			}
		}
		else
		{
			label = new JLabel("<html></head><br><br>"
				+"<p><FONT><pre>   PERIODIC TABLE<pre></FONT></p>"
				+"<p><PRE>    of elements</PRE></p><br><br><br><br>"
				+"<FONT SIZE=-2>D.I. Mendeleev(1834-1907)</FONT></html>");
			
			label.setOpaque(true);
			label.setBackground(color);
			pan.add(label,BorderLayout.EAST);
			URL url = this.getClass().getResource(
				"/org/openscience/jchempaint/resources/large-bin/periodicTable_Mendeleev.jpg");
			if(url!=null)
			{
				ImageIcon image = new ImageIcon(url);
				
				label = new JLabel(image,JLabel.CENTER);
				
				pan.add(label,BorderLayout.WEST);
			}
		}
		
		pan.setBackground(color);
		pan.setForeground(Color.black);
		pan.setBorder(BorderFactory.createLineBorder(Color.black));
		pan.setBounds(origin.x, origin.y, 295, 210);
		return pan;
	}
	/**
	 * set the form to do a button {html or normal)
	 * 
	 * @param controlViewer
	 */
	public void setControlViewer(int controlViewer){
		this.controlViewerButton = controlViewer;
	}
}

