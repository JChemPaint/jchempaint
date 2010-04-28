import html.*;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;

class HtmlExample {

  public static FileOutputStream Output;
  public static PrintStream file;

  public static void main(String argv[]) {
    Tag html = new Tag("html");
    Tag body = new Tag("body");
       
    // a simple header
    Tag head = new Tag("head");
    Tag title = new Tag("title");
    title.add("HTML generator example");
    head.add(title);
    body.add(head);

    // add h1 title
    Tag h1 = new Tag("h1");
    h1.add("HTML Generator demo");
    body.add(h1);

    // creat a table
    Tag table = 
        new Tag("table", "border=1 cellpadding=0 cellspacing=0");
    body.add(table); // table is not finished yet!

    // create two rows with five columns each
    Tag row1 = new Tag("tr");
    Tag row2 = new Tag("tr");

    for (int j = 0; j < 5; j++) {
        // create new cell tag
        Tag cell = new Tag("td");
        // fill in content
        cell.add(Integer.toString(j));
        // add cell (same object) to row1 and row2
        row1.add(cell);
        row2.add(cell);
    }

    // rows can be added later
    table.add(row1);
    table.add(row2);
    
    // now replace cell 4 in row 2
    // first, create new cell with its content
    Tag cell = new Tag("td");
    cell.add("*");
    // overwrite old content
    row2.set(3, cell); // we can use any method from java.util.List
    
    // add line break (no closing tag) after table
    body.add(new Tag("br", false));

    // simple string
    body.add("End of Example");

    // add title again at bottom - re-use h1 object
    body.add(h1);

    html.add(body); // no header here
    
    // Write to file
    try
    {     
      Output = new FileOutputStream("example.html");
      file = new PrintStream(Output);
    }
    catch(Exception e)
    {
      System.out.println("Could not write file!");
    }
    file.println(html);
  }
}
