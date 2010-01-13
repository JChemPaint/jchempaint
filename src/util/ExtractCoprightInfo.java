import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class ExtractCoprightInfo {
    
    //args[0] is the cdk.lib directory
    public static void main(String[] args) throws Exception{
        String linesep = System.getProperty("line.separator");
        FileOutputStream fos = new FileOutputStream(new File("lib-licenses.txt"));
        fos.write(new String("JCP contains the following libraries. Please read this for comments on copyright etc."+linesep+linesep).getBytes());
        fos.write(new String("Chemistry Development Kit, master version as of "+new Date().toString()+" (http://cdk.sf.net)"+linesep).getBytes());
        fos.write(new String("Copyright 1997-2009 The CDK Development Team"+linesep).getBytes());
        fos.write(new String("License: LGPL v2 (http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)"+linesep).getBytes());
        fos.write(new String("Download: https://sourceforge.net/projects/cdk/files/"+linesep).getBytes());
        fos.write(new String("Source available at: http://sourceforge.net/scm/?type=git&group_id=20024"+linesep+linesep).getBytes());
        File[] files = new File(args[0]).listFiles(new JarFileFilter());
        for(int i=0;i<files.length;i++){
            if(new File(files[i].getPath()+".meta").exists()){
                Map<String,Map<String,String>> metaprops = readProperties(new File(files[i].getPath()+".meta"));
                Iterator<String> itsect =metaprops.keySet().iterator();
                while(itsect.hasNext()){
                    String section = itsect.next();
                    fos.write(new String(metaprops.get(section).get("Library")+" "+metaprops.get(section).get("Version")+" ("+metaprops.get(section).get("Homepage")+")"+linesep).getBytes());
                    fos.write(new String("Copyright "+metaprops.get(section).get("Copyright")+linesep).getBytes());
                    fos.write(new String("License: "+metaprops.get(section).get("License")+" ("+metaprops.get(section).get("LicenseURL")+")"+linesep).getBytes());
                    fos.write(new String("Download: "+metaprops.get(section).get("Download")+linesep).getBytes());
                    fos.write(new String("Source available at: "+metaprops.get(section).get("SourceCode")+linesep+linesep).getBytes());
                }
            }
            if(new File(files[i].getPath()+".extra").exists()){
                fos.write(new String("The author says:"+linesep).getBytes());
                FileInputStream in = new FileInputStream(new File(files[i].getPath()+".extra"));
                int len;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0){
                  fos.write(buf, 0, len);
                }
            }
            fos.write(linesep.getBytes());
        }
        fos.close();
    }


    public static Map<String,Map<String,String>> readProperties(File file) throws IOException {
        Map<String,Map<String,String>> props = new HashMap<String,Map<String,String>>();
        Map<String, String> sectionProps = null;
        FileInputStream fstream = new FileInputStream(file);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        //Read File Line By Line
        while ((line = br.readLine()) != null)   {
          if (line.startsWith("#")) {
            // skip line
          } else if (line.startsWith("[") && line.endsWith("]")) {
            String section = line.substring(line.indexOf("[")+1, line.indexOf("]"));
            sectionProps = new HashMap<String, String>();
            props.put(section, sectionProps);
          } else if (line.contains("=")) {
            String[] keyValue = line.split("=");
            if (sectionProps == null) {
              System.err.println("ERROR: property without required section header");
              System.exit(-1);
            }
            sectionProps.put(keyValue[0], keyValue[1]);
          }
        }
        return props;
      }
}

class JarFileFilter implements FileFilter
{
  private final String[] okFileExtensions = 
    new String[] {"jar"};

  public boolean accept(File file)
  {
    for (String extension : okFileExtensions)
    {
      if (file.getName().toLowerCase().endsWith(extension))
      {
        return true;
      }
    }
    return false;
  }
}
