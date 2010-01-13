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
package org.openscience.jchempaint.applet;

import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openscience.cdk.ChemModel;
import org.openscience.jchempaint.JChemPaintViewerPanel;

public class JChemPaintViewerApplet extends JChemPaintAbstractApplet{
	
    int oldnumber=-1;
    
	public void init() {
		boolean fitToScreen = false;
		String scrollbarParam = this.getParameter("scrollbars"); 
		if (scrollbarParam != null && scrollbarParam.equals("false")) {
			fitToScreen = true;
		}
		JChemPaintViewerPanel p
		    = new JChemPaintViewerPanel(
		            new ChemModel(), getWidth(), getHeight(), fitToScreen, debug, this);	
		setTheJcpp(p);
		this.add(p);
		super.init();
	}


	  /**
	   * Handles interaction with a peak table
	   * @param atomNumber atom number of peaks highlighted in table
	   */
	  public void highlightPeakInTable(int atomNumber) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
	    if(getParameter("highlightTable")==null || getParameter("highlightTable").equals("false"))
	      return;
	    

	    Class[] paratypes={new Applet().getClass()};
	    Class jso = Class.forName("netscape.javascript.JSObject");
	    Method getWindowMethod=jso.getMethod("getWindow", paratypes);
	    Object win=getWindowMethod.invoke(jso, new Object[] {this});
	    Class[] paratypes2={new String("").getClass()};
	    Method evalMethod=jso.getMethod("eval", paratypes2);
	    Class[] paratypes3={new String("").getClass(),new Object().getClass()};
	    Method setMemberMethod=jso.getMethod("setMember", paratypes3);

	    if(oldnumber!=-1){
	        Object tr = evalMethod.invoke(win,new Object[]{"document.getElementById(\"tableid"+oldnumber+"\").style"});
	        if((oldnumber+1)%2==0)
	            setMemberMethod.invoke(tr, new Object[]{"backgroundColor","#D3D3D3"});
	        else
	            setMemberMethod.invoke(tr, new Object[]{"backgroundColor","white"});
	    }
	    Object tr = evalMethod.invoke(win,new Object[]{"document.getElementById(\"tableid"+atomNumber+"\").style"});
	    if(tr==null){
	        oldnumber=-1;
	    }else{
	        setMemberMethod.invoke(tr, new Object[]{"backgroundColor","#FF6600"});
	        oldnumber=atomNumber;
	    }
	  }
}
