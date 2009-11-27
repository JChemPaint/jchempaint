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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.util.TreeMap;
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
 * the tabs are the names of all directories in TEMPLATES_PACKAGE. _ in 
 * directory name is replaced by a space. All files in 
 * theses directories named *.mol are read as MOL files and put as a template on 
 * the respective tab. The first line of the MOL file is used as name to 
 * display. If there is a *.png file in the same directy, it is used 
 * as icon. Do not put anything else in these directories. TEMPLATES_PACKAGE must 
 * contain a class called DummyClass for the directory being located. 
 * If wished, the tab can be added to the Templates menu with an action like:
 * menuitemnameAction=org.openscience.jchempaint.action.CopyPasteAction@pasteX
 * where X is the directory name.
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
	 * @param tabToSelect a tab with that name will be shown at startup.
     */
    public TemplateBrowser(String tabToSelect) {
        super((JFrame)null, GT._("Structure Templates"), true);
        this.setName("templates");
        myPanel = new JPanel();
        getContentPane().add(myPanel);
        myPanel.setLayout(new BorderLayout());
        yesButton = new JButton(GT._("Cancel"));
        yesButton.addActionListener(this);
        JPanel bottomPanel =new JPanel();
        bottomPanel.add(yesButton);
        myPanel.add(bottomPanel, BorderLayout.SOUTH); 
        tabbedPane = new JTabbedPane();
        Map<String,List<IMolecule>> entriesMol = new TreeMap<String,List<IMolecule>>(); 
        Map<IMolecule, String> entriesMolName = new HashMap<IMolecule, String>();
        Map<String, Icon> entriesIcon = new HashMap<String, Icon>();
        JPanel allPanel = new JPanel();
        GridLayout experimentLayout = new GridLayout(0,8);
        allPanel.setLayout(experimentLayout);
        tabbedPane.addTab(GT._("All"), allPanel );
        try{
            createTemplatesMaps(entriesMol, entriesMolName, entriesIcon, true);
            myPanel.add( tabbedPane, BorderLayout.CENTER );
            Iterator<String> it = entriesMol.keySet().iterator();
            int count=0;
            while(it.hasNext()) {
                String key=it.next();
                JPanel panel = new JPanel();
                panel.setLayout(experimentLayout);
                for(int k=0;k<entriesMol.get(key).size();k++){
                    IMolecule cdkmol = entriesMol.get(key).get(k);
                    Icon icon = entriesIcon.get(entriesMolName.get(cdkmol));
                    JButton button = new JButton();
                    if(icon!=null)
                        button.setIcon(icon);
                    panel.add(button);
                    button.setPreferredSize(new Dimension(100,120));
                    button.setMaximumSize(new Dimension(100,120));
                    button.addActionListener(this);
                    button.setVerticalTextPosition(SwingConstants.BOTTOM);
                    button.setHorizontalTextPosition(SwingConstants.CENTER);
                    button.setText((String)cdkmol.getProperty(CDKConstants.TITLE));
                    button.setToolTipText((String)cdkmol.getProperty(CDKConstants.TITLE));
                    button.setFont(button.getFont().deriveFont(10f));
                    button.setName((String)cdkmol.getProperty(CDKConstants.TITLE));
                    mols.put(button, cdkmol);
                    JButton allButton = new JButton();
                    if(icon!=null)
                        allButton.setIcon(icon);
                    panel.add(button);
                    allButton.setPreferredSize(new Dimension(100,120));
                    allButton.setMaximumSize(new Dimension(100,120));
                    allButton.addActionListener(this);
                    allButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                    allButton.setHorizontalTextPosition(SwingConstants.CENTER);
                    allButton.setText((String)cdkmol.getProperty(CDKConstants.TITLE));
                    allButton.setToolTipText((String)cdkmol.getProperty(CDKConstants.TITLE));
                    allButton.setFont(allButton.getFont().deriveFont(10f));
                    mols.put(allButton, cdkmol);
                    allPanel.add(allButton);
                }
                tabbedPane.addTab(GT.getStringNoExtraction(key.replace('_', ' ')), panel );
                if(tabToSelect.equals(key.replace('_',' '))){
                    tabbedPane.setSelectedIndex(count+1);
                }
                count++;
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
            chosenmolecule.removeProperty(CDKConstants.TITLE);
        }
        this.setVisible(false);        
    }


    /**
     * Extracts templates from directories.
     * 
     * @param entriesMol      A map of category names and structure.
     * @param entriesMolName  A map of structures and names.
     * @param entriesIcon     A map of structures and images.
     * @param withsubdirs     true=all of the above will be filled, false=only names in entriesMol will be filled (values in entriesMol will be empty, other maps as well, these can be passed as null).
     * @throws Exception      Problems reading directories.
     */
    public static void createTemplatesMaps(Map<String, List<IMolecule>> entriesMol,
            Map<IMolecule, String> entriesMolName, Map<String, Icon> entriesIcon, boolean withsubdirs) throws Exception{
        DummyClass dummy = new DummyClass();
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
                            }else if(restname.indexOf("/")>-1 && withsubdirs){
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
                File file = new File(new File(dummy.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath()+File.separator+TEMPLATES_PACKAGE.replace('/', File.separatorChar));
                for (int i=0;i<file.listFiles().length ; i++) {
                    if(file.listFiles()[i].isDirectory()){
                        File dir = file.listFiles()[i];
                        if(!dir.getName().startsWith(".")) { 
                            entriesMol.put(dir.getName(), new ArrayList<IMolecule>());
                            if(withsubdirs){
                                for(int k=0;k<dir.list().length;k++){
                                    if(dir.listFiles()[k].getName().indexOf(".mol")>-1){
                                        MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(dir.listFiles()[k]), Mode.STRICT);
                                        IMolecule cdkmol = (IMolecule)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
                                        entriesMol.get(dir.getName()).add(cdkmol);
                                        entriesMolName.put(cdkmol,dir.listFiles()[k].getName().substring(0,dir.listFiles()[k].getName().length()-4));
                                    }else{
    
                                        Icon icon = new ImageIcon(dir.listFiles()[k].getAbsolutePath());
                                        if ( dir.listFiles()[k].getName().toLowerCase().endsWith("png")) {
                                            entriesIcon.put(dir.listFiles()[k].getName().substring(0,dir.listFiles()[k].getName().length()-4),icon);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }                
            }
        
    }
}
