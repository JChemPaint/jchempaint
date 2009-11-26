/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Egon Willighagen, Stefan Kuhn
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;

import javax.swing.JOptionPane;
import javax.vecmath.Point2d;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.INChIPlainTextReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.applet.JChemPaintAbstractApplet;
import org.openscience.jchempaint.controller.ControllerHub;
import org.openscience.jchempaint.controller.MoveModule;
import org.openscience.jchempaint.controller.RemoveModule;
import org.openscience.jchempaint.controller.SelectSquareModule;
import org.openscience.jchempaint.dialog.TemplateBrowser;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.LogicalSelection;
import org.openscience.jchempaint.renderer.selection.RectangleSelection;
import org.openscience.jchempaint.renderer.selection.ShapeSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

/**
 * Action to copy/paste structures.
 *
 * @cdk.bug    1288449
 */
public class CopyPasteAction extends JCPAction{

    private static final long serialVersionUID = -3343207264261279526L;

    private DataFlavor molFlavor = new DataFlavor(
            "chemical/x-mdl-molfile", "mdl mol file format");
    private DataFlavor svgFlavor = new DataFlavor(
            "image/svg+xml",          "scalable vector graphics");
    private DataFlavor cmlFlavor = new DataFlavor(
            "image/cml",          "chemical markup language");
    private DataFlavor smilesFlavor = new DataFlavor(
            "chemical/x-daylight-smiles", "smiles format");


    private void addToClipboard(Clipboard clipboard, IAtomContainer container) {
        try {
            JcpSelection jcpselection = new JcpSelection((IAtomContainer)container.clone());
            clipboard.setContents(jcpselection,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean supported(Transferable transfer, DataFlavor flavor) {
        return transfer != null && transfer.isDataFlavorSupported(flavor);
    }

    public void actionPerformed(ActionEvent e) {
        logger.info("  type  ", type);
        logger.debug("  source ", e.getSource());
        
        RendererModel renderModel =
            jcpPanel.get2DHub().getRenderer().getRenderer2DModel();
        IChemModel chemModel = jcpPanel.getChemModel();
        Clipboard sysClip = jcpPanel.getToolkit().getSystemClipboard();

        if ("copy".equals(type)) {
            handleSystemClipboard(sysClip);
            IAtom atomInRange = null;
            IChemObject object = getSource(e);
            logger.debug("Source of call: ", object);
            if (object instanceof IAtom) {
                atomInRange = (IAtom) object;
            } else {
                atomInRange = renderModel.getHighlightedAtom();
            }
            if (atomInRange != null) {
                IAtomContainer tocopyclone =
                    atomInRange.getBuilder().newAtomContainer();
                try {
                    tocopyclone.addAtom((IAtom) atomInRange.clone());
                    addToClipboard(sysClip, tocopyclone);
                } catch (CloneNotSupportedException e1) {
                    e1.printStackTrace();
                }
            }
            else if(renderModel.getHighlightedBond()!=null){
                IBond bond = renderModel.getHighlightedBond();
                if (bond != null) {
                    IAtomContainer tocopyclone =
                        bond.getBuilder().newAtomContainer();
                    try {
                        tocopyclone.addAtom((IAtom) bond.getAtom(0).clone());
                        tocopyclone.addAtom((IAtom) bond.getAtom(1).clone());
                        tocopyclone.addBond(bond.getBuilder().newBond(tocopyclone.getAtom(0), tocopyclone.getAtom(1), bond.getOrder()));
                        addToClipboard(sysClip, tocopyclone);
                    } catch (CloneNotSupportedException e1) {
                        e1.printStackTrace();
                    }
                }
            }else if(renderModel.getSelection().getConnectedAtomContainer()!=null){
                addToClipboard(sysClip,
                        renderModel.getSelection().getConnectedAtomContainer());
            }else{
                addToClipboard(sysClip, JChemPaintPanel.getAllAtomContainersInOne(chemModel));
            }
        } else if ("copyAsSmiles".equals(type)) {
            handleSystemClipboard(sysClip);
            try {
                if(renderModel.getSelection().getConnectedAtomContainer()!=null){
                    SmilesGenerator sg=new SmilesGenerator();
                    sysClip.setContents(new SmilesSelection(sg.createSMILES(renderModel.getSelection().getConnectedAtomContainer().getBuilder().newMolecule(renderModel.getSelection().getConnectedAtomContainer()))),null);
                }else{
                    sysClip.setContents(new SmilesSelection(CreateSmilesAction.getSmiles(chemModel)),null);
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else if ("eraser".equals(type)) {
            RemoveModule newActiveModule = new RemoveModule(jcpPanel.get2DHub());
            newActiveModule.setID(type);
            jcpPanel.get2DHub().setActiveDrawModule(newActiveModule);
            IAtom atomInRange = null;
            IBond bondInRange = null;
            IChemObject object = getSource(e);
            logger.debug("Source of call: ", object);
            if (object instanceof IAtom) {
                atomInRange = (IAtom) object;
            } else {
                atomInRange = renderModel.getHighlightedAtom();
            }
            if (object instanceof IBond) {
                bondInRange = (IBond) object;
            } else {
                bondInRange = renderModel.getHighlightedBond();
            }
            if (atomInRange != null) {
                jcpPanel.get2DHub().removeAtom(atomInRange);
            } else if (bondInRange != null) {
                jcpPanel.get2DHub().removeBond(bondInRange);
            } else if(renderModel.getSelection()!=null && renderModel.getSelection().getConnectedAtomContainer()!=null){
                IChemObjectSelection selection = renderModel.getSelection();
                IAtomContainer selected = selection.getConnectedAtomContainer();
                jcpPanel.get2DHub().deleteFragment(selected);
                renderModel.setSelection(new LogicalSelection(
                        LogicalSelection.Type.NONE));
                jcpPanel.get2DHub().updateView();
            }
        } else if(type.indexOf("pasteTemplate")>-1){
            //if templates are shown, we extract the tab to show if any
            String templatetab="";
            if(type.indexOf("_")>-1){
                templatetab=type.substring(type.indexOf("_")+1);
            }
            TemplateBrowser templateBrowser = new TemplateBrowser(templatetab);
            if(templateBrowser.getChosenmolecule()!=null){
                //Note: flipping is a workaround for the molfile upside down
                flipStructure(templateBrowser.getChosenmolecule());
                scaleStructure(templateBrowser.getChosenmolecule());
                insertStructure(templateBrowser.getChosenmolecule(), renderModel);
                jcpPanel.getRenderPanel().setZoomWide(true);
                jcpPanel.get2DHub().getRenderer().getRenderer2DModel().setZoomFactor(1);


            }
        } else if ("paste".equals(type)) {
            boolean flip=false;
            handleSystemClipboard(sysClip);
            Transferable transfer = sysClip.getContents( null );
            ISimpleChemObjectReader reader = null;
            String content=null;
            
            if (supported(transfer, molFlavor) ) {
                try {
                    StringBufferInputStream sbis = (StringBufferInputStream) transfer.getTransferData(molFlavor);
                    int x;
                    StringBuffer sb = new StringBuffer();
                    while((x=sbis.read())!=-1){
                        sb.append((char)x);
                    }
                    reader = new MDLReader(new StringReader(sb.toString()));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else if (supported(transfer, DataFlavor.stringFlavor) ) {
                try {
                    content = (String) transfer.getTransferData(DataFlavor.stringFlavor);
                    reader = new ReaderFactory().createReader(new StringReader(content));
                    flip=true;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            // if looks like CML - InputStream required. Reader throws error.
            if(content!=null && content.indexOf("cml")>-1) {
                reader = new CMLReader(new ByteArrayInputStream(content.getBytes()));
            }

            IMolecule toPaste = null;
            if (reader != null) {
                IMolecule readMolecule =
                    chemModel.getBuilder().newMolecule();
                try {
                    if (reader.accepts(Molecule.class)) {
                        toPaste = (IMolecule) reader.read(readMolecule);
                    } else if (reader.accepts(ChemFile.class)) {
                        toPaste = readMolecule;
                        IChemFile file = (IChemFile) reader.read(new ChemFile());
                        for (IAtomContainer ac :
                            ChemFileManipulator.getAllAtomContainers(file)) {
                            toPaste.add(ac);

                        }
                    }
                } catch (CDKException e1) {
                    e1.printStackTrace();
                }
            }

            //we just try smiles and inchi if no reader is found for content
            if (toPaste == null &&
                    supported(transfer, DataFlavor.stringFlavor)) {
                try{
                    SmilesParser sp = new SmilesParser(
                            DefaultChemObjectBuilder.getInstance());
                    toPaste = sp.parseSmiles(
                            ((String) transfer.getTransferData(
                                    DataFlavor.stringFlavor)).trim());

                    IMoleculeSet mols = ConnectivityChecker.partitionIntoMolecules(toPaste);
                    for(int i=0;i<mols.getAtomContainerCount();i++)
                    {
                        StructureDiagramGenerator sdg =
                            new StructureDiagramGenerator((IMolecule)mols.getMolecule(i));

                        sdg.setTemplateHandler(
                                new TemplateHandler(toPaste.getBuilder())
                        );
                        sdg.generateCoordinates();
                    }
                    //for some reason, smilesparser sets valencies, which we don't want in jcp
                    for(int i=0;i<toPaste.getAtomCount();i++){
                        toPaste.getAtom(i).setValency(null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (content.indexOf("INChI")>-1 || content.indexOf("InChI")>-1) { // handle it as an InChI
                        try {
                            StringReader sr = new StringReader(content);
                            INChIPlainTextReader inchireader = new INChIPlainTextReader(sr);
                            IChemFile mol = DefaultChemObjectBuilder.getInstance().newChemFile();
                            toPaste = ((IChemFile) inchireader.read(mol)).getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
                            IMoleculeSet mols = ConnectivityChecker.partitionIntoMolecules(toPaste);
                            for(int i=0;i<mols.getAtomContainerCount();i++)
                            {
                                StructureDiagramGenerator sdg =
                                    new StructureDiagramGenerator((IMolecule)mols.getMolecule(i));

                                sdg.setTemplateHandler(
                                        new TemplateHandler(toPaste.getBuilder())
                                );
                                sdg.generateCoordinates();
                            }
                        } catch (Exception e2) {
                            jcpPanel.announceError(e2);
                        }        			
                    }
                }
            }
            if (toPaste != null) {
                jcpPanel.getRenderPanel().setZoomWide(true);
                jcpPanel.get2DHub().getRenderer().getRenderer2DModel().setZoomFactor(1);

                if (flip)
                   flipStructure(toPaste);
                scaleStructure(toPaste);
                insertStructure(toPaste, renderModel);

            }else{
                JOptionPane.showMessageDialog(jcpPanel, GT._("The content you tried to copy could not be read to any known format"), GT._("Could not process content"), JOptionPane.WARNING_MESSAGE);
            }
        } else if (type.equals("cut")) {
            handleSystemClipboard(sysClip);
            IAtom atomInRange = null;
            IBond bondInRange = null;
            IChemObject object = getSource(e);
            logger.debug("Source of call: ", object);
            if (object instanceof IAtom) {
                atomInRange = (IAtom) object;
            } else {
                atomInRange = renderModel.getHighlightedAtom();
            }
            if (object instanceof IBond) {
                bondInRange = (IBond) object;
            } else {
                bondInRange = renderModel.getHighlightedBond();
            }
            IAtomContainer tocopyclone =
                jcpPanel.getChemModel().getBuilder().newAtomContainer();
            if (atomInRange != null) {
                tocopyclone.addAtom(atomInRange);
                jcpPanel.get2DHub().removeAtom(atomInRange);
            } else if (bondInRange != null) {
                tocopyclone.addBond(bondInRange);
                jcpPanel.get2DHub().removeBond(bondInRange);
            }else if(renderModel.getSelection()!=null && renderModel.getSelection().getConnectedAtomContainer()!=null){
                IChemObjectSelection selection = renderModel.getSelection();
                IAtomContainer selected = selection.getConnectedAtomContainer();
                tocopyclone.add(selected);
                jcpPanel.get2DHub().deleteFragment(selected);
                renderModel.setSelection(new LogicalSelection(
                        LogicalSelection.Type.NONE));
                jcpPanel.get2DHub().updateView();
            }
            if(tocopyclone.getAtomCount()>0 || tocopyclone.getBondCount()>0)
                addToClipboard(sysClip, tocopyclone);
        }else if (type.equals("selectAll")) {
            ControllerHub hub = jcpPanel.get2DHub();
            IChemObjectSelection allSelection =
                new LogicalSelection(LogicalSelection.Type.ALL);

            allSelection.select(hub.getIChemModel());
            renderModel.setSelection(allSelection);
            SelectSquareModule succusorModule = new SelectSquareModule(hub);
            succusorModule.setID("select");
            MoveModule newActiveModule = new MoveModule(hub, succusorModule);
            newActiveModule.setID("move");
            hub.setActiveDrawModule(newActiveModule);
        } else if (type.equals("selectFromChemObject")) {
            // FIXME: implement for others than Reaction, Atom, Bond
            IChemObject object = getSource(e);
            if (object instanceof IAtom) {
                SingleSelection<IAtom> container = new SingleSelection<IAtom>((IAtom)object);
                renderModel.setSelection(container);
            }
            else if (object instanceof IBond) {
                SingleSelection<IBond> container = new SingleSelection<IBond>((IBond)object);
                renderModel.setSelection(container);
            }
            else if (object instanceof IReaction) {
                IAtomContainer wholeModel =
                    jcpPanel.getChemModel().getBuilder().newAtomContainer();
                for (IAtomContainer container :
                    ReactionManipulator.getAllAtomContainers(
                            (IReaction)object)) {
                    wholeModel.add(container);
                }
                ShapeSelection container = new RectangleSelection();
                for (IAtom atom : wholeModel.atoms()) {
                    container.atoms.add(atom);
                }
                for (IBond bond : wholeModel.bonds()) {
                    container.bonds.add(bond);
                }
                renderModel.setSelection(container);
            }
            else {
                logger.warn("Cannot select everything in : ", object);
            }
        } else if (type.equals("selectReactionReactants")) {
            IChemObject object = getSource(e);
            if (object instanceof IReaction) {
                IReaction reaction = (IReaction) object;
                IAtomContainer wholeModel =
                    jcpPanel.getChemModel().getBuilder().newAtomContainer();
                for (IAtomContainer container :
                    MoleculeSetManipulator.getAllAtomContainers(
                            reaction.getReactants())) {
                        wholeModel.add(container);
                }
                ShapeSelection container = new RectangleSelection();
                for (IAtom atom : wholeModel.atoms()) {
                    container.atoms.add(atom);
                }
                for (IBond bond : wholeModel.bonds()) {
                    container.bonds.add(bond);
                }
                renderModel.setSelection(container);
            }
            else {
                logger.warn("Cannot select reactants from : ", object);
            }
        } else if (type.equals("selectReactionProducts")) {
            IChemObject object = getSource(e);
            if (object instanceof IReaction) {
                    IReaction reaction = (IReaction) object;
                    IAtomContainer wholeModel =
                        jcpPanel.getChemModel().getBuilder().newAtomContainer();
                for (IAtomContainer container :
                    MoleculeSetManipulator.getAllAtomContainers(
                            reaction.getProducts())) {
                        wholeModel.add(container);
                }
                ShapeSelection container = new RectangleSelection();
                for (IAtom atom : wholeModel.atoms()) {
                    container.atoms.add(atom);
                }
                for (IBond bond : wholeModel.bonds()) {
                    container.bonds.add(bond);
                }
                renderModel.setSelection(container);
            }
            else {
                logger.warn("Cannot select reactants from : ", object);
            }
        }
        jcpPanel.get2DHub().updateView();
        jcpPanel.updateStatusBar();

    }


    /**
     * A workaround for reading structure from file, these tend to get
     * in upside down
     * TODO remove when obsolete.
     * 
     * @param topaste
     */
    private void flipStructure (IMolecule topaste)  {
        for (IAtom atom : topaste.atoms()) {
            if (atom.getPoint2d()!=null) {
                atom.setPoint2d(new Point2d(atom.getPoint2d().x,atom.getPoint2d().y*-1));
            }
        }
    }

    /**
     * Scale the structure to be pasted to the same scale of the current drawing 
     * @param topaste
     */
    private void scaleStructure (IMolecule topaste)  {
        double bondLengthModel = jcpPanel.get2DHub().calculateAverageBondLength(jcpPanel.get2DHub().getIChemModel().getMoleculeSet());
        double bondLengthInsert = GeometryTools.getBondLengthAverage(topaste);
        double scale=bondLengthModel/bondLengthInsert;
        for (IAtom atom : topaste.atoms()) {
            if (atom.getPoint2d()!=null) {
                atom.setPoint2d(new Point2d(atom.getPoint2d().x*scale,atom.getPoint2d().y*scale));
            }
        }
    }

    /**
     * Inserts a structure into the panel. It adds Hs if needed and highlights the structure after insert.
     * 
     * @param toPaste     The structure to paste.
     * @param renderModel The current renderer model.
     */
    private void insertStructure(IMolecule toPaste, RendererModel renderModel) {
        //add implicit hs
        if(jcpPanel.get2DHub().getController2DModel().getAutoUpdateImplicitHydrogens()){
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(toPaste);
                CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(toPaste
                        .getBuilder());
                hAdder.addImplicitHydrogens(toPaste);
            } catch (CDKException ex) {
                ex.printStackTrace();
                // do nothing
            }
            //valencies are set when doing atom typing, which we don't want in jcp
            for(int i=0;i<toPaste.getAtomCount();i++){
                toPaste.getAtom(i).setValency(null);
            }
        }
        //somehow, in case of single atoms, there are no coordinates
        if(toPaste.getAtomCount()==1 && toPaste.getAtom(0).getPoint2d()==null)
            toPaste.getAtom(0).setPoint2d(new Point2d(0,0));
        JChemPaintAbstractApplet.generateModel(jcpPanel, toPaste, false,true);
        jcpPanel.get2DHub().fireStructureChangedEvent();
        
        //We select the inserted structure
        IChemObjectSelection selection
        = new LogicalSelection(LogicalSelection.Type.ALL);

        selection.select(ChemModelManipulator.newChemModel(toPaste));
        renderModel.setSelection(selection);
        SelectSquareModule succusorModule = new SelectSquareModule(jcpPanel.get2DHub());
        succusorModule.setID("select");
        MoveModule newActiveModule = new MoveModule(jcpPanel.get2DHub(), succusorModule);
        newActiveModule.setID("move");
        jcpPanel.get2DHub().setActiveDrawModule(newActiveModule);        
    }

    @SuppressWarnings("unchecked")
    private void handleSystemClipboard(Clipboard clipboard) {
        Transferable clipboardContent = clipboard.getContents(this);
        DataFlavor flavors[]=clipboardContent.getTransferDataFlavors();
        String text = "System.clipoard content";
        for(int i=0;i<flavors.length;++i)
        {
            text+="\n\n Name: "+ flavors[i].getHumanPresentableName();
            text+="\n MIME Type: "+flavors[i].getMimeType();
            text+="\n Class: ";
            Class cl = flavors[i].getRepresentationClass();
            if(cl==null) text+="null";
            else text+=cl.getName();
        }
        logger.debug(text);
    }

    class JcpSelection implements Transferable, ClipboardOwner {
        private DataFlavor [] supportedFlavors = {
                molFlavor, DataFlavor.stringFlavor, svgFlavor, cmlFlavor, smilesFlavor
        };
        String mol;
        String smiles;
        String svg;
        String cml;

        @SuppressWarnings("unchecked")
        public JcpSelection(IAtomContainer tocopy1) throws Exception {
            IMolecule tocopy= tocopy1.getBuilder().newMolecule(tocopy1);
            // MDL mol output
            StringWriter sw = new StringWriter();
            new MDLWriter(sw).writeMolecule(tocopy);
            this.mol=sw.toString();
            SmilesGenerator sg=new SmilesGenerator();
            smiles = sg.createSMILES(tocopy);
            // SVG output
            svg=jcpPanel.getSVGString();
            // CML output
            sw = new StringWriter();
            Class cmlWriterClass = null;
            try {
                cmlWriterClass = this.getClass().getClassLoader().loadClass(
                "org.openscience.cdk.io.CMLWriter");
                if (cmlWriterClass != null) {
                    IChemObjectWriter cow = (IChemObjectWriter)cmlWriterClass.newInstance();
                    Constructor constructor = cow.getClass().getConstructor(new Class[]{Writer.class});
                    cow = (IChemObjectWriter)constructor.newInstance(new Object[]{sw});
                    cow.write(tocopy);
                    cow.close();
                }
                cml=sw.toString();
            } catch (Exception exception) {
                logger.error("Could not load CMLWriter: ", exception.getMessage());
                logger.debug(exception);
            }
        }

        public synchronized DataFlavor [] getTransferDataFlavors () {
            return (supportedFlavors);
        }

        public boolean isDataFlavorSupported (DataFlavor parFlavor) {
            for(int i=0;i<supportedFlavors.length;i++){
                if(supportedFlavors[i].equals(parFlavor))
                    return true;
            }
            return false;
        }

        public synchronized Object getTransferData (DataFlavor parFlavor)	throws UnsupportedFlavorException {
            if (parFlavor.equals (molFlavor)) {
                return new StringBufferInputStream(mol);
            } else if (parFlavor.equals (smilesFlavor)) {
                return new StringBufferInputStream(smiles);
            } else if(parFlavor.equals(DataFlavor.stringFlavor)) {
                return mol;
            } else if(parFlavor.equals(cmlFlavor)) {
                return new StringBufferInputStream(cml);
            } else if(parFlavor.equals(svgFlavor)) {
                return new StringBufferInputStream(svg);
            } else {
                throw new UnsupportedFlavorException (parFlavor);
            }
        }

        public void lostOwnership (Clipboard parClipboard, Transferable parTransferable) {
            System.out.println ("Lost ownership");
        }
    }

    class SmilesSelection implements Transferable, ClipboardOwner {
        private DataFlavor [] supportedFlavors = {
                DataFlavor.stringFlavor
        };

        String smiles;

        public SmilesSelection(String smiles) throws Exception {
            this.smiles = smiles;
        }

        public synchronized DataFlavor [] getTransferDataFlavors () {
            return (supportedFlavors);
        }

        public boolean isDataFlavorSupported (DataFlavor parFlavor) {
            for(int i=0;i<supportedFlavors.length;i++){
                if(supportedFlavors[i].equals(parFlavor))
                    return true;
            }
            return false;
        }

        public synchronized Object getTransferData (DataFlavor parFlavor)	throws UnsupportedFlavorException {
            if(parFlavor.equals(DataFlavor.stringFlavor)) {
                return smiles;
            } else {
                throw new UnsupportedFlavorException (parFlavor);
            }
        }

        public void lostOwnership (Clipboard parClipboard, Transferable parTransferable) {
            System.out.println ("Lost ownership");
        }
    }
}

