import html.*;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;

import org.openscience.jchempaint.GT;

class HtmlExample {

  public static FileOutputStream Output;
  public static PrintStream file;

  public static void main(String argv[]) {
    Tag html = new Tag("html");
    Tag body = new Tag("body");
       
    // a simple header
    Tag head = new Tag("head");
    Tag title = new Tag("title");
    title.add("JChemPaint User's Guide");
    head.add(new Tag("link", "rel=stylesheet type=text/css href=jcp.css title=Style"));
    head.add(title);
    body.add(head);
    
    Tag h1 = new Tag("h1");
    h1.add("JChemPaint User's Guide");
    body.add(h1);
    body.add(new Tag("img", "src=../../large-bin/jcplogo.gif width=150 height=150"));
    Tag p_1 = new Tag("p");
    p_1.add("This user's guide contains the following sections:");
    body.add(p_1);
    
    //GT.setLanguage("ru");
    contentsEntry(body, /*GT._*/("About JChemPaint"), "contain/aboutJCP.html", 
      /*GT._*/("General information about the JChemPaint program and the JChemPaint project."));
    contentsEntry(body, /*GT._*/("Basic Tutorial"), "contain/tutorial.html", 
      /*GT._*/("This section explains on a few basic examples the use of JChemPaint and more in detail how new compounds and reactions can be drawn."));
    contentsEntry(body, /*GT._*/("Rgroup Query Tutorial"), "contain/", 
      /*GT._*/("An introduction to R-groups and how to draw them with JChemPaint."));
    contentsEntry(body, /*GT._*/("Reference Guide"), "contain/referenceGuide.html", 
      /*GT._*/("A general overview on JChemPaint. Describes the use of JChemPaint and how it can be customized. It also tried to describe as best as possible which algorithms are used, and in which CDK classes those can be found."));    
    contentsEntry(body, /*GT._*/("Miscellaneous topics"), "contain/misc.html", 
      /*GT._*/("Some miscellaneous remarks on JChemPaint."));
    contentsEntry(body, /*GT._*/("Feedback"), "contain/feedback.html", 
      /*GT._*/("How to give feedback on JChemPaint."));
    
    Tag h2 = new Tag("h2");
    h2.add("Keeping in Touch");
    body.add(h2);
    
    Tag p_2 = new Tag("p");
    p_2.add("Comments and questions about how the JChemPaint software works are welcome. If you have further questions, please review the FAQ at our home page:");
    body.add(p_2);
    
    Tag p_3 = new Tag("p");
    p_3.add("&nbsp;&nbsp;&nbsp;");
    Tag a = new Tag("a", "href=http://jchempaint.sourceforge.net");
    a.add("href=http://jchempaint.sourceforge.net");
    p_3.add(a);
    body.add(p_3);
    
    html.add(body);
    
    // Write to file
    try
    {     
      Output = new FileOutputStream("dist/classes/org/openscience/jchempaint/resources/userhelp_jcp/en_US/example.html");
      file = new PrintStream(Output);
    }
    catch(Exception e)
    {
      System.out.println("Could not write file!");
    }
    file.println(html);
  }
  
  public static void contentsEntry(Tag body, String name, 
                                    String ref, String description)
  {
      Tag p_1 = new Tag("p");
      Tag b = new Tag("b");
      b.add(name);
      Tag a = new Tag("a", "href="+ref);
      a.add(b);
      p_1.add(a);
      body.add(p_1);
      Tag p_2 = new Tag("p");
      p_2.add(description);
      body.add(p_2);
  }
}
