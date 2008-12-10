package org.openscience.jchempaint;

import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.vecmath.Point2d;

import org.openscience.cdk.controller.ControllerModuleAdapter;
import org.openscience.cdk.controller.IChemModelRelay;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.LoggingTool;

public class SwingPopupModule extends ControllerModuleAdapter {

	public SwingPopupModule(RenderPanel renderer,IChemModelRelay chemModelRelay) {
		super(chemModelRelay);
		this.renderer=renderer;
	}


	public String getDrawModeString() {
		return "Popup menu";
	}


	public void mouseClickedUp(Point2d worldCoord) {
		popupMenuForNearestChemObject(renderer.getRenderer().toScreenCoordinates(worldCoord.x, worldCoord.y));
	}

	private LoggingTool logger = new LoggingTool(this);

	private static Hashtable<String, CDKPopupMenu> popupMenus = new Hashtable<String, CDKPopupMenu>();

	private RenderPanel renderer;
		
	/**
	 *  Sets the popupMenu attribute of the Controller2D object
	 *
	 *@param  someClass  The new popupMenu value
	 *@param  menu        The new popupMenu value
	 */
	public void setPopupMenu(Class someClass, CDKPopupMenu menu) {
		SwingPopupModule.popupMenus.put(someClass.getName(), menu);
	}


	/**
	 *  Returns the popup menu for this IChemObject if it is set, and null
	 *  otherwise.
	 *
	 *@param  someClass  Description of the Parameter
	 *@return             The popupMenu value
	 */
	public CDKPopupMenu getPopupMenu(Class classSearched) {
        logger.debug("Searching popup for: ", classSearched.getName());
        while (classSearched.getName().startsWith("org.openscience.cdk")) {
            logger.debug("Searching popup for: ", classSearched.getName());
            if (SwingPopupModule.popupMenus.containsKey(classSearched.getName())) {
                return (CDKPopupMenu) SwingPopupModule.popupMenus.get(classSearched.getName());
            } else {
                logger.debug("  recursing into super class");
                classSearched = classSearched.getSuperclass();
            }
		}
        return null;
	}

	private void popupMenuForNearestChemObject(Point2d mouseCoords)
	{
		IChemObject objectInRange = renderer.getRenderer().getRenderer2DModel().getHighlightedAtom();
		if(objectInRange==null)
			objectInRange = renderer.getRenderer().getRenderer2DModel().getHighlightedBond();
		if(objectInRange==null)
			objectInRange = chemModelRelay.getIChemModel();
		CDKPopupMenu popupMenu = getPopupMenu(objectInRange.getClass());
		if (popupMenu != null)
		{
			popupMenu.setSource(objectInRange);
			logger.debug("Set popup menu source to: ", objectInRange);
			popupMenu.show(renderer, (int)mouseCoords.x, (int)mouseCoords.y);
		} else
		{
			logger.warn("Popup menu is null! Could not set source!");
		}
	}
}
