/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2009 Stefan Kuhn  <shk3@users.sf.net>
 * Copyright (C) 2010 Conni Wagner  <conni75@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
import java.util.Map;

import org.openscience.jchempaint.applet.JChemPaintAbstractApplet;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Taglet that expands @param tag into a list of 
 * parameters taken from the paramInfo field of the 
 * class. Is supposed to be used in applet classes.
 */
public class JCPParamsTaglet implements Taglet {
    
    private static final String NAME = "jcp.params";
    
    public String getName() {
        return NAME;
    }
    
    public boolean inField() {
        return false;
    }

    public boolean inConstructor() {
        return false;
    }
    
    public boolean inMethod() {
        return false;
    }
    
    public boolean inOverview() {
        return false;
    }

    public boolean inPackage() {
        return false;
    }

    public boolean inType() {
        return true;
    }
    
    public boolean isInlineTag() {
        return false;
    }
    
    public static void register(Map<String, JCPParamsTaglet> tagletMap) {
        JCPParamsTaglet tag = new JCPParamsTaglet();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }

    public String toString(Tag tag) {
        return expand(tag);
    }

    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return null;
        } else {
            StringBuffer list = new StringBuffer();
            for (int i=0; i<tags.length; i++) {
                list.append(expand(tags[i])).append(" ");
            }
            return list.toString();
        }
    }

    private String expand(Tag tag) {
        String[][] params = JChemPaintAbstractApplet.paramInfo;
        StringBuffer value = new StringBuffer();
        value.append("</DL></DT></DD><br><br>The following applet params can be used:<br>");
        for(int i=0;i<params.length;i++){
            value.append("<b>"+params[i][0]+"</b>: "+params[i][1]+", "+params[i][2]+"<br>");
        }
        return value.toString();
    }

}
