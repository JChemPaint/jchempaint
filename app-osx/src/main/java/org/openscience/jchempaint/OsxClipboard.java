package org.openscience.jchempaint;

import org.openscience.jchempaint.action.CopyPasteAction;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OsxClipboard extends Clipboard {

    static {
        try (InputStream in = OsxClipboard.class.getResourceAsStream("libSetClipboard.dylib")) {
            if (in == null) {
                System.err.println("Error: Could not find libSetClipboard!");
            }
            File file = File.createTempFile("libSetClipboard", "dylib");
            try (FileOutputStream fout = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) >= 0) {
                    fout.write(buffer, 0, read);
                }
            }
            System.load(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Clipboard delegate;

    public OsxClipboard(Clipboard delegate) {
        super(delegate.getName());
        this.delegate = delegate;
    }

    public static native void setClipboard(byte[] pdfFile,
                                           byte[] svgFile,
                                           byte[] pngData,
                                           String smi);


    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public synchronized Transferable getContents(Object requestor) {
        return delegate.getContents(requestor);
    }

    @Override
    public DataFlavor[] getAvailableDataFlavors() {
        return delegate.getAvailableDataFlavors();
    }

    @Override
    public boolean isDataFlavorAvailable(DataFlavor flavor) {
        return delegate.isDataFlavorAvailable(flavor);
    }

    @Override
    public Object getData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return delegate.getData(flavor);
    }

    @Override
    public synchronized void addFlavorListener(FlavorListener listener) {
        delegate.addFlavorListener(listener);
    }

    @Override
    public synchronized void removeFlavorListener(FlavorListener listener) {
        delegate.removeFlavorListener(listener);
    }

    @Override
    public synchronized FlavorListener[] getFlavorListeners() {
        return delegate.getFlavorListeners();
    }

    @Override
    public synchronized void setContents(Transferable contents, ClipboardOwner owner) {
        try {

            // if we are copying just a simple string (e.g. SMILES/MOLfile)
            if (contents.getTransferDataFlavors().length == 1 &&
                contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                delegate.setContents(contents, owner);
                return;
            }

            // we can't handle the custom mime-type via the native specific code
            // or more specifically I can't work out how to do it, so we set these
            // here then add on the PDF/SVG data. Note setting the string data
            // here also messes up the native logic so we leave that for the
            // native code (in swift)
            delegate.setContents(new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{
                            CopyPasteAction.MOL_FLAVOR,
                            CopyPasteAction.SMI_FLAVOR,
                            CopyPasteAction.CML_FLAVOR
                    };
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    if (!contents.isDataFlavorSupported(flavor))
                        return false;
                    return flavor == CopyPasteAction.MOL_FLAVOR ||
                           flavor == CopyPasteAction.SMI_FLAVOR ||
                           flavor == CopyPasteAction.CML_FLAVOR;
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    if (isDataFlavorSupported(flavor))
                        return contents.getTransferData(flavor);
                    return null;
                }
            }, owner);

            if (contents.isDataFlavorSupported(CopyPasteAction.PDF_FLAVOR) &&
                contents.isDataFlavorSupported(CopyPasteAction.SVG_FLAVOR)) {
                InputStream pdfData = (InputStream) contents.getTransferData(CopyPasteAction.PDF_FLAVOR);
                InputStream svgData = (InputStream) contents.getTransferData(CopyPasteAction.SVG_FLAVOR);
                String strData = (String) contents.getTransferData(DataFlavor.stringFlavor);
                setClipboard(toByteArray(pdfData),
                             toByteArray(svgData),
                             new byte[0],
                             strData);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] toByteArray(InputStream is) {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) >= 0) {
                bout.write(buffer, 0, read);
            }
            return bout.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
