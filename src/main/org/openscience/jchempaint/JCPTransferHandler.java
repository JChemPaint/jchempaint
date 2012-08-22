package org.openscience.jchempaint;

import javax.swing.TransferHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.applet.JChemPaintEditorApplet;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;

import org.openscience.cdk.interfaces.IChemModel;

public class JCPTransferHandler extends TransferHandler {
    private static final long serialVersionUID = 1L;
 
    private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
 
  private DataFlavor fileFlavor, stringFlavor;
  private DataFlavor uriListFlavor;

  private JChemPaintPanel jcpPanel;
 
  public JCPTransferHandler(JChemPaintPanel panel) {
 
    fileFlavor = DataFlavor.javaFileListFlavor;
    stringFlavor = DataFlavor.stringFlavor;
 
    try {
      uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    jcpPanel = panel;
  }
 
  @Override
  public boolean importData(JComponent c, Transferable t) {
 
    if (!canImport(c, t.getTransferDataFlavors())) {
      return false;
    }
 
    try {
      // Windows
      if (hasFileFlavor(t.getTransferDataFlavors())) {
 
        final java.util.List files = (java.util.List) t
            .getTransferData(fileFlavor);
 
        process(files);       
 
        return true;
 
      // Linux
      }else if(hasURIListFlavor(t.getTransferDataFlavors())){
 
          final List<File> files = textURIListToFileList((String) t.getTransferData(uriListFlavor));
 
        if(files.size()>0){
 
            process(files);
        }
 
      }else if (hasStringFlavor(t.getTransferDataFlavors())) {
 
        String str = ((String) t.getTransferData(stringFlavor));
 
        System.out.println(str);
 
        return true;
      }
    } catch (UnsupportedFlavorException ufe) {
      System.out.println("importData: unsupported data flavor");
    } catch (IOException ieo) {
      System.out.println("importData: I/O exception");
    }
    return false;
  }
 
  @Override
  public int getSourceActions(JComponent c) {
    return COPY;
  }
 
  @Override
  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    if (hasFileFlavor(flavors)) {
      return true;
    }
    if (hasStringFlavor(flavors)) {
      return true;
    }
    return false;
  }
 
  private boolean hasFileFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (fileFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
 
  private boolean hasStringFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (stringFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }

  private void process(java.util.List<File> l){
     for (File f : l) {
         // Taken from OpenAction with small changes
        if (jcpPanel.getGuistring().equals(
                    JChemPaintEditorApplet.GUI_APPLET) ||
                    JChemPaintPanel.getAllAtomContainersInOne(jcpPanel.getChemModel()).getAtomCount()==0) {
                int clear = jcpPanel.showWarning();
                if (clear == JOptionPane.YES_OPTION) {
                    try {
                        IChemModel chemModel = null;
                            chemModel = JChemPaint.readFromFile(f, null, jcpPanel);
                        if (jcpPanel.get2DHub().getUndoRedoFactory() != null
                                && jcpPanel.get2DHub().getUndoRedoHandler() != null) {
                            IUndoRedoable undoredo = jcpPanel.get2DHub()
                                    .getUndoRedoFactory().getLoadNewModelEdit(
                                            jcpPanel.getChemModel(),
						null,
                                            jcpPanel.getChemModel()
                                                    .getMoleculeSet(),
                                            jcpPanel.getChemModel()
                                                    .getReactionSet(),
                                            chemModel.getMoleculeSet(),
                                            chemModel.getReactionSet(),
                                            "Load "
                                                    + f.getName());
                            jcpPanel.get2DHub().getUndoRedoHandler().postEdit(
                                    undoredo);
                        }
                        jcpPanel.getChemModel().setMoleculeSet(
                                chemModel.getMoleculeSet());
                        jcpPanel.getChemModel().setReactionSet(chemModel.getReactionSet());
                        jcpPanel.getRenderPanel().getRenderer()
                                .getRenderer2DModel().setSelection(
                                        new LogicalSelection(
                                                LogicalSelection.Type.NONE));

                        // the newly opened file should nicely fit the screen
                        jcpPanel.getRenderPanel().setFitToScreen(true);

                        jcpPanel.getRenderPanel().update(
                                jcpPanel.getRenderPanel().getGraphics());

                        // enable zooming by removing constraint
                        jcpPanel.getRenderPanel().setFitToScreen(false);

                    
                    
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        jcpPanel.announceError(e1);

                    }
                }
        } else {
            JChemPaint.showInstance(f, null, null, jcpPanel.isDebug());
        }
     }
  }
   
  private boolean hasURIListFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (uriListFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
 
  private static List<File> textURIListToFileList(String data) {
    List<File> list = new ArrayList<File>(1);
    for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
      String s = st.nextToken();
      if (s.startsWith("#")) {
        // the line is a comment (as per the RFC 2483)
        continue;
      }
      try {
        URI uri = new URI(s);
        File file = new File(uri);
        list.add(file);
      } catch (URISyntaxException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
    return list;
  }
 
}

