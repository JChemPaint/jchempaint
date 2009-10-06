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
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
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

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.ext.awt.image.GammaTransfer;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.dialog.templates.DummyClass;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

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
    private final String TEMPLATES_PACKAGE = "org/openscience/jchempaint/dialog/templates";

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
        Map<String,List<IMolecule>> entries = new HashMap<String,List<IMolecule>>(); 
        try {
            try{
                JarFile jarfile = new JarFile(new File(dummy.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
                for (Enumeration<JarEntry> e = jarfile.entries() ; e.hasMoreElements() ;) {
                    JarEntry entry = e.nextElement();
                    if(entry.getName().indexOf(TEMPLATES_PACKAGE+"/")==0){
                        String restname = entry.getName().substring(new String(TEMPLATES_PACKAGE+"/").length());
                        if(restname.length()>2){
                            if(restname.indexOf("/")==restname.length()-1){
                                entries.put(restname.substring(0,restname.length()-1), new ArrayList<IMolecule>());
                            }else if(restname.indexOf("/")>-1){
                                InputStream ins = dummy.getClass().getClassLoader().getResourceAsStream(entry.getName());
                                MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
                                IMolecule cdkmol = (IMolecule)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
                                entries.get(restname.substring(0,restname.indexOf("/"))).add(cdkmol);
                            }
                        }
                    }
                }
            }catch(ZipException ex){
                File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+TEMPLATES_PACKAGE);
                for (int i=0;i<file.listFiles().length ; i++) {
                    if(file.listFiles()[i].isDirectory()){
                        File dir = file.listFiles()[i];
                        entries.put(dir.getName(), new ArrayList<IMolecule>());
                        for(int k=0;k<dir.list().length;k++){
                            MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(dir.listFiles()[k]), Mode.STRICT);
                            IMolecule cdkmol = (IMolecule)reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
                            entries.get(dir.getName()).add(cdkmol);
                        }
                    }
                }                
            }
            myPanel.add( tabbedPane, BorderLayout.CENTER );
            Iterator<String> it = entries.keySet().iterator();
            while(it.hasNext()) {
                String key=it.next();
                JPanel panel = new JPanel();
                GridLayout experimentLayout = new GridLayout(0,8);
                panel.setLayout(experimentLayout);
                for(int k=0;k<entries.get(key).size();k++){
                    IMolecule cdkmol = entries.get(key).get(k);
                    String inputstr = getMolSvg(cdkmol, 100, 100);
                    ImageTranscoder imageTranscoder = new JPEGTranscoder();
                    imageTranscoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
                    TranscoderInput input = new TranscoderInput(new StringReader(inputstr));
                    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                    TranscoderOutput output = new TranscoderOutput(ostream);
                    imageTranscoder.transcode(input, output);
                    ostream.flush();
                    ostream.close();
                    Icon icon = new ImageIcon(ostream.toByteArray());
                    JButton button = new JButton();
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
    
    /**
     * Gets a molecule as an svg graphics.
     * 
     * @param cdkmol The molecule to generate image for.
     * @param width  Size of image.
     * @param height Size of image.
     * @return The svg.
     * @throws UnsupportedEncodingException 
     * @throws SVGGraphics2DIOException 
     */
    private String getMolSvg(IAtomContainer cdkmol, int width, int height) throws UnsupportedEncodingException, SVGGraphics2DIOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<IGenerator> generators = new ArrayList<IGenerator>();
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        Renderer renderer = new Renderer(generators,new AWTFontManager());
        RendererModel r2dm = renderer.getRenderer2DModel();
        r2dm.setDrawNumbers(false);
        r2dm.setBackColor(Color.LIGHT_GRAY);
        r2dm.setIsCompact(true);
        r2dm.setShowImplicitHydrogens(false);
        r2dm.setShowEndCarbons(false);
        int number=((int)Math.sqrt(cdkmol.getAtomCount()))+1;
        int moleculewidth = number*100;
        int moleculeheight = number*100;
        if(width>-1){
            moleculewidth=width;
            moleculeheight=height;
        }
        if(moleculeheight<200 || moleculewidth<200){
          r2dm.setIsCompact(true);
          r2dm.setBondDistance(3);
        }
        Rectangle drawArea = new Rectangle(moleculewidth, moleculeheight);
        renderer.setup(cdkmol, drawArea);
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setBackground(Color.LIGHT_GRAY);
        svgGenerator.setColor(Color.LIGHT_GRAY);
        svgGenerator.fill(new Rectangle(0, 0, moleculewidth, moleculeheight));
        renderer.paintMolecule(cdkmol, new AWTDrawVisitor(svgGenerator), drawArea, false);
        boolean useCSS = false;
        baos = new ByteArrayOutputStream();
        Writer outwriter = new OutputStreamWriter(baos, "UTF-8");
        StringBuffer sb = new StringBuffer();
        svgGenerator.stream(outwriter, useCSS);
        StringTokenizer tokenizer = new StringTokenizer(baos.toString(), "\n");
        while (tokenizer.hasMoreTokens()) {
          String name = tokenizer.nextToken();
          if (name.length() > 4 && name.substring(0, 5).equals("<svg ")) {
            sb.append(name.substring(0, name.length() - 1)).append(" width=\"" + moleculewidth + "\" height=\"" + moleculeheight + "\">" + "\n\r");
          } else {
            sb.append(name + "\n\r");
          }
        }
        return (sb.toString());
      }
}
