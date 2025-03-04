package org.openscience.jchempaint.controller;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.jchempaint.renderer.IRenderer;
import org.openscience.jchempaint.renderer.JChemPaintRendererModel;

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

    public void mouseWheelMovedForward(int clicks) {
        doZoom( .9 );
        chemModelRelay.fireZoomEvent();
        chemModelRelay.updateView();
    }

    public void mouseWheelMovedBackward(int clicks) {
        doZoom( 1.1 );
        chemModelRelay.fireZoomEvent();
        chemModelRelay.updateView();
    }

    private void doZoom(double z) {

        IRenderer renderer = chemModelRelay.getRenderer();
        double currentZoom = renderer.getRenderer2DModel().getZoomFactor();
        System.err.println(z);

        if (currentZoom * z <= MIN_ZOOM_AMOUNT)
            return;
        if (currentZoom * z >= MAX_ZOOM_AMOUNT)
            return;

        Point2d screenCoord = 
            renderer.toScreenCoordinates( worldCoord.x, worldCoord.y );
        zoom(z);
        Point2d newScreenCoords = 
            renderer.toScreenCoordinates( worldCoord.x, worldCoord.y );
        
        Vector2d v= new Vector2d();
        v.sub( screenCoord, newScreenCoords );
        renderer.shiftDrawCenter( v.x, v.y );
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
