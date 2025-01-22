package org.openscience.jchempaint.controller;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.jchempaint.renderer.IRenderer;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @cdk.module controlbasic
 */
public class ZoomModule extends ControllerModuleAdapter {

    public static final int MAX_ZOOM_AMOUNT = 10;
    public static final double MIN_ZOOM_AMOUNT = .1;
    private Point2d worldCoord = null;
    private String ID;

    public ZoomModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
    }

    public void mouseWheelMovedForward(int modifiers, int clicks) {
        // shift scroll to zoom
        if ((modifiers & InputEvent.META_DOWN_MASK) == 0)
            return;
        doZoom( .9 );
        chemModelRelay.fireZoomEvent();
        chemModelRelay.updateView();
    }

    public void mouseWheelMovedBackward(int modifiers, int clicks) {
        // shift scroll to zoom
        if ((modifiers & InputEvent.META_DOWN_MASK) == 0)
            return;
        doZoom( 1.1 );
        chemModelRelay.fireZoomEvent();
        chemModelRelay.updateView();
    }

    private void doZoom(double z) {

        IRenderer renderer = chemModelRelay.getRenderer();
        double currentZoom = renderer.getRenderer2DModel().getZoomFactor();
        if (currentZoom * z <= MIN_ZOOM_AMOUNT)
            return;
        if (currentZoom * z >= MAX_ZOOM_AMOUNT)
            return;
        zoom(z);
    }

    private void zoom(double zoomFactor) {
        JChemPaintRendererModel model = chemModelRelay.getRenderer().getRenderer2DModel();
        double zoom = model.getZoomFactor();
        if (zoom <= MIN_ZOOM_AMOUNT || zoom >= MAX_ZOOM_AMOUNT)
            return;
        zoom = zoom * zoomFactor;
        // clamp
        zoom = Math.max(MIN_ZOOM_AMOUNT, Math.min(zoom, MAX_ZOOM_AMOUNT));
        chemModelRelay.getRenderer().setZoom( zoom );
    }

    @Override
    public void mouseMove( Point2d worldCoord ) {
        this.worldCoord = worldCoord;
    }
    
    public String getDrawModeString() {
       return "Zoom";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID=ID;
    }

}
