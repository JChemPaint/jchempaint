package org.openscience.jchempaint.controller;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;

public class ReactionHub {


    public static void makeReactantInNewReaction(ControllerHub controllerhub, IAtomContainer newContainer, IAtomContainer oldcontainer) {
        IChemModel chemModel = controllerhub.getChemModel();
        IReaction reaction = newContainer.getBuilder().newInstance(IReaction.class);
        reaction.setID("reaction-" + System.currentTimeMillis());
        IAtomContainer mol=newContainer.getBuilder().newInstance(IAtomContainer.class,newContainer);
        mol.setID(newContainer.getID());
        reaction.addReactant(mol);
        IReactionSet reactionSet = chemModel.getReactionSet();
        if (reactionSet == null)
        {
            reactionSet = chemModel.getBuilder().newInstance(IReactionSet.class);
        }
        reactionSet.addReaction(reaction);
        chemModel.setReactionSet(reactionSet);
        chemModel.getMoleculeSet().removeAtomContainer(oldcontainer);
        if(chemModel.getMoleculeSet().getAtomContainerCount()==0)
            chemModel.getMoleculeSet().addAtomContainer(chemModel.getBuilder().newInstance(IAtomContainer.class));
        if(controllerhub.getUndoRedoFactory()!=null && controllerhub.getUndoRedoHandler()!=null){
            IUndoRedoable undoredo = controllerhub.getUndoRedoFactory().getMakeReactantOrProductInNewReactionEdit(chemModel, newContainer, oldcontainer, true, "Make Reactant in new Reaction");
            controllerhub.getUndoRedoHandler().postEdit(undoredo);
        }
        controllerhub.structureChanged();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IChemModelRelay#makeReactantInExistingReaction(java.lang.String, org.openscience.cdk.interfaces.IAtomContainer, org.openscience.cdk.interfaces.IAtomContainer)
     */
    public static void makeReactantInExistingReaction(ControllerHub controllerhub, String reactionId,
            IAtomContainer newContainer, IAtomContainer oldcontainer) {
        IChemModel chemModel = controllerhub.getChemModel();
        IReaction reaction = ReactionSetManipulator.getReactionByReactionID(chemModel.getReactionSet(), reactionId);
        IAtomContainer mol=newContainer.getBuilder().newInstance(IAtomContainer.class,newContainer);
        mol.setID(newContainer.getID());
        reaction.addReactant(mol);
        chemModel.getMoleculeSet().removeAtomContainer(oldcontainer);
        if(chemModel.getMoleculeSet().getAtomContainerCount()==0)
            chemModel.getMoleculeSet().addAtomContainer(chemModel.getBuilder().newInstance(IAtomContainer.class));
        if(controllerhub.getUndoRedoFactory()!=null && controllerhub.getUndoRedoHandler()!=null){
            IUndoRedoable undoredo = controllerhub.getUndoRedoFactory().getMakeReactantOrProductInExistingReactionEdit(chemModel, newContainer, oldcontainer, reactionId, true, "Make Reactant in "+reactionId);
            controllerhub.getUndoRedoHandler().postEdit(undoredo);
        }
        controllerhub.structureChanged();
    }

    public static void makeProductInNewReaction(ControllerHub controllerhub, IAtomContainer newContainer,
            IAtomContainer oldcontainer) {
        IChemModel chemModel = controllerhub.getChemModel();
        IReaction reaction = newContainer.getBuilder().newInstance(IReaction.class);
        reaction.setID("reaction-" + System.currentTimeMillis());
        IAtomContainer mol=newContainer.getBuilder().newInstance(IAtomContainer.class,newContainer);
        mol.setID(newContainer.getID());
        reaction.addProduct(mol);
        IReactionSet reactionSet = chemModel.getReactionSet();
        if (reactionSet == null)
        {
            reactionSet = chemModel.getBuilder().newInstance(IReactionSet.class);
        }
        reactionSet.addReaction(reaction);
        chemModel.setReactionSet(reactionSet);
        chemModel.getMoleculeSet().removeAtomContainer(oldcontainer);
        if(chemModel.getMoleculeSet().getAtomContainerCount()==0)
            chemModel.getMoleculeSet().addAtomContainer(chemModel.getBuilder().newInstance(IAtomContainer.class));
        if(controllerhub.getUndoRedoFactory()!=null && controllerhub.getUndoRedoHandler()!=null){
            IUndoRedoable undoredo = controllerhub.getUndoRedoFactory().getMakeReactantOrProductInNewReactionEdit(chemModel, newContainer, oldcontainer, false, "Make Reactant in new Reaction");
            controllerhub.getUndoRedoHandler().postEdit(undoredo);
        }
        controllerhub.structureChanged();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IChemModelRelay#makeProductInExistingReaction(java.lang.String, org.openscience.cdk.interfaces.IAtomContainer, org.openscience.cdk.interfaces.IAtomContainer)
     */
    public static void makeProductInExistingReaction(ControllerHub controllerhub, String reactionId,
            IAtomContainer newContainer, IAtomContainer oldcontainer) {
        IChemModel chemModel = controllerhub.getChemModel();
        IReaction reaction = ReactionSetManipulator.getReactionByReactionID(chemModel.getReactionSet(), reactionId);
        IAtomContainer mol=newContainer.getBuilder().newInstance(IAtomContainer.class,newContainer);
        mol.setID(newContainer.getID());
        reaction.addProduct(mol);
        chemModel.getMoleculeSet().removeAtomContainer(oldcontainer);
        if(chemModel.getMoleculeSet().getAtomContainerCount()==0)
            chemModel.getMoleculeSet().addAtomContainer(chemModel.getBuilder().newInstance(IAtomContainer.class));
        if(controllerhub.getUndoRedoFactory()!=null && controllerhub.getUndoRedoHandler()!=null){
            IUndoRedoable undoredo = controllerhub.getUndoRedoFactory().getMakeReactantOrProductInExistingReactionEdit(chemModel, newContainer, oldcontainer, reactionId, false, "Make Reactant in "+reactionId);
            controllerhub.getUndoRedoHandler().postEdit(undoredo);
        }
        controllerhub.structureChanged();
    }
}
