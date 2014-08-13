import java.awt.Color;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.vecmath.Point2d;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.dialog.TemplateBrowser;
import org.openscience.jchempaint.dialog.templates.DummyClass;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.font.AWTFontManager;
import org.openscience.jchempaint.renderer.generators.BasicAtomGenerator;
import org.openscience.jchempaint.renderer.generators.BasicBondGenerator;
import org.openscience.jchempaint.renderer.generators.IGenerator;
import org.openscience.jchempaint.renderer.visitor.AWTDrawVisitor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/*
 * This is a rough util class for creating images for templates. It creates them in bin,
 * they need to be copied to src.
 */
public class TemplateImagesMaker {
    
    public static void main(String[] args) throws CDKException, IOException, TranscoderException{
        File file = new File(new DummyClass().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+TemplateBrowser.TEMPLATES_PACKAGE);
        for (int i=0;i<file.listFiles().length ; i++) {
            if(file.listFiles()[i].isDirectory()){
                File dir = file.listFiles()[i];
                for(int k=0;k<dir.list().length;k++){
                    if(dir.listFiles()[k].getName().indexOf(".mol")>-1){
                        System.err.println(dir.listFiles()[k].getAbsolutePath());
                        MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(dir.listFiles()[k]), Mode.RELAXED);
                        IAtomContainer cdkmol = (IAtomContainer)reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
                        String inputstr = getMolSvg(cdkmol, 100, 100);
                        ImageTranscoder imageTranscoder = new JPEGTranscoder();
                        imageTranscoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
                        TranscoderInput input = new TranscoderInput(new StringReader(inputstr));
                        FileOutputStream ostream = new FileOutputStream(new File(dir.listFiles()[k].getAbsolutePath().substring(0,dir.listFiles()[k].getAbsolutePath().length()-4)+".png"));
                        TranscoderOutput output = new TranscoderOutput(ostream);
                        imageTranscoder.transcode(input, output);
                        ostream.flush();
                        ostream.close();
                    }
                }
            }
        }                
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
    private static String getMolSvg(IAtomContainer cdkmol, int width, int height) throws UnsupportedEncodingException, SVGGraphics2DIOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<IGenerator> generators = new ArrayList<IGenerator>();
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        Renderer renderer = new Renderer(generators,new AWTFontManager(), false);
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
