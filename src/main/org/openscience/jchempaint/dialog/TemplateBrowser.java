/* Copyright (C) 2009 Stefan Kuhn <stefan.kuhn@ebi.ac.uk>
 *
 * Contact: cdk-jchempaint@lists.sourceforge.net
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.dialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.dialog.templates.DummyClass;

/**
 * This class shows a list of templates. The one chosen by the user can queried 
 * with getChosenmolecule(). The templates are organized in tabs. The headers of 
 * the tabs are the names of all directories in TEMPLATES_PACKAGE. All files in 
 * theses directories are read as MOL files and put as a template on the respective 
 * tab. Do not put anything else in these directories. TEMPLATES_PACKAGE must 
 * contain a class called DummyClass.
 *
 */
public class TemplateBrowser extends JDialog implements ActionListener {
    
    private static final long serialVersionUID = -7684345027847830963L;
    private JPanel myPanel;
    private JButton yesButton;
    private JTabbedPane tabbedPane;
    private Map<JButton, IMolecule> mols = new HashMap<JButton, IMolecule>();
    private IMolecule chosenmolecule;
    public final static String TEMPLATES_PACKAGE = "org/openscience/jchempaint/dialog/templates";

    /**
     * The molecule chosen by the user.
     * 
     * @return The molecule, null if cancelled.
     */
    public IMolecule getChosenmolecule() {
        return chosenmolecule;
    }

    /**
     * Constructor for TemplateBrowser.
     */
    public TemplateBrowser() {
        super((JFrame)null, GT._("Structure Templates"), true);
        myPanel = new JPanel();
        getContentPane().add(myPanel);
        myPanel.setLayout(new BorderLayout());
        yesButton = new JButton("Cancel");
        yesButton.addActionListener(this);
        JPanel bottomPanel =new JPanel();
        bottomPanel.add(yesButton);
        myPanel.add(bottomPanel, BorderLayout.SOUTH); 
        tabbedPane = new JTabbedPane();
        DummyClass dummy = new DummyClass();
        Map<String,List<IMolecule>> entriesMol = new HashMap<String,List<IMolecule>>(); 
        Map<IMolecule, String> entriesMolName = new HashMap<IMolecule, String>();
        Map<String, Icon> entriesIcon = new HashMap<String, Icon>();
        try {
            try{
                // Create a URL that refers to a jar file on the net
                URL url = new URL("jar:"+dummy.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()+"!/");
                // Get the jar file
                JarURLConnection conn = (JarURLConnection)url.openConnection();
                JarFile jarfile = conn.getJarFile();
                for (Enumeration<JarEntry> e = jarfile.entries() ; e.hasMoreElements() ;) {
                    JarEntry entry = e.nextElement();
                    if(entry.getName().indexOf(TEMPLATES_PACKAGE+"/")==0){
                        String restname = entry.getName().substring(new String(TEMPLATES_PACKAGE+"/").length());
                        if(restname.length()>2){
                            if(restname.indexOf("/")==restname.length()-1){
                                entriesMol.put(restname.substring(0,restname.length()-1), new ArrayList<IMolecule>());
                            }else if(restname.indexOf("/")>-1){
                                if(entry.getName().indexOf(".mol")>-1){
                                    InputStream ins = dummy.getClass().getClassLoader().getResourceAsStream(entry.getName());
                                    MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
                                    IMolecule cdkmol = (IMolecule)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
                                    entriesMol.get(restname.substring(0,restname.indexOf("/"))).add(cdkmol);
                                    entriesMolName.put(cdkmol,entry.getName().substring(0,entry.getName().length()-4));
                                }else{
                                    Icon icon = new ImageIcon(new URL(url.toString()+entry.getName()));
                                    entriesIcon.put(entry.getName().substring(0,entry.getName().length()-4),icon);
                                }
                            }
                        }
                    }
                }
            }catch(ZipException ex){
                //This is a version we fall back to if no jar available. This should be in Eclipse only.
                //You need to change separator chars in TEMPLATES_PACKAGE if on windows or other weired OSes.
                File file = new File(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath()+File.separator+TEMPLATES_PACKAGE);
                for (int i=0;i<file.listFiles().length ; i++) {
                    if(file.listFiles()[i].isDirectory()){
                        File dir = file.listFiles()[i];
                        entriesMol.put(dir.getName(), new ArrayList<IMolecule>());
                        for(int k=0;k<dir.list().length;k++){
                            if(dir.listFiles()[k].getName().indexOf(".mol")>-1){
                                MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(dir.listFiles()[k]), Mode.STRICT);
                                IMolecule cdkmol = (IMolecule)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
                                entriesMol.get(dir.getName()).add(cdkmol);
                                entriesMolName.put(cdkmol,dir.listFiles()[k].getName().substring(0,dir.listFiles()[k].getName().length()-4));
                            }else{
                                Icon icon = new ImageIcon(dir.listFiles()[k].getAbsolutePath());
                                entriesIcon.put(dir.listFiles()[k].getName().substring(0,dir.listFiles()[k].getName().length()-4),icon);
                            }
                        }
                    }
                }                
            }
            myPanel.add( tabbedPane, BorderLayout.CENTER );
            Iterator<String> it = entriesMol.keySet().iterator();
            while(it.hasNext()) {
                String key=it.next();
                JPanel panel = new JPanel();
                GridLayout experimentLayout = new GridLayout(0,8);
                panel.setLayout(experimentLayout);
                for(int k=0;k<entriesMol.get(key).size();k++){
                    IMolecule cdkmol = entriesMol.get(key).get(k);
                    Icon icon = entriesIcon.get(entriesMolName.get(cdkmol));
                    JButton button = new JButton();
                    if(icon!=null)
                        button.setIcon(icon);
                    panel.add(button);
                    button.addActionListener(this);
                    button.setVerticalTextPosition(SwingConstants.BOTTOM);
                    button.setHorizontalTextPosition(SwingConstants.CENTER);
                    button.setText((String)cdkmol.getProperty(CDKConstants.TITLE));
                    button.setToolTipText((String)cdkmol.getProperty(CDKConstants.TITLE));
                    mols.put(button, cdkmol);
                }
                tabbedPane.addTab(key, panel );
            }                
            pack();
            setVisible(true);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()!=yesButton){
            chosenmolecule = mols.get(e.getSource());
        }
        this.setVisible(false);        
    }
}
