package classes;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 * The CMouseHandler class handles mouse-based input of the controller, which
 * is mainly used for selecting and zooming in on an area.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 24-Mar-2014
 */

public class CMouseHandler implements MouseListener, MouseMotionListener {
    
    // ======== CONFIGURATION ========
    private static final int dragButton = MouseEvent.BUTTON1;
    private static final int markButton = MouseEvent.BUTTON3;    
    
    // ===============================
    private Point startPos;
    private Point endPos;
    private Point lastDragPoint;
    private Rect markRect;
    private final Controller controller;
    private final JPanel view;
    private boolean isMarking;
    private boolean isDragging;
    
    public CMouseHandler(Controller controller, JPanel target) {
        this.controller = controller;
        isMarking = false;
        view = target;
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        switch(e.getButton()) {
            case markButton:
                isMarking = true;
                isDragging = false;
                startPos = e.getLocationOnScreen();
                startPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
                markRect = new Rect(startPos.x, startPos.y, 0, 0);
                break;
            case dragButton:
                lastDragPoint = e.getLocationOnScreen();
                lastDragPoint.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
                isDragging = true;
                isMarking = false;
                break;
        }
    }
    
    /**
     * Returns a given value restricted by the passed min and max values
     * @param val The value to restrict
     * @param min The smallest it may be
     * @param max The biggest it may be
     * @return The restricted value
     */
    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(val, max));
    }
    
    private Rect getMarkRect(Point startPos, Point endPos) {
        Viewport port = controller.viewport;
        endPos.x = (int)clamp(endPos.x, 0, port.getSize().width);
        endPos.y = (int)clamp(endPos.y, 0, port.getSize().height);
        
        float width = Math.abs(startPos.x - endPos.x);
        float height = Math.abs(startPos.y - endPos.y);
        float wperh = controller.viewport.ratio();
        
        // Restrict the ratio
        float expectedWidth = height*wperh;
        if (width < expectedWidth) { // Height is larger
            height = width/wperh; // The smaller is used
            
            //width = height*wperh; // The bigger is used
        } else {
            width = expectedWidth; // The smaller is used
            //height = width/wperh; // The bigger is used
        }

        float x;
        if (endPos.x < startPos.x) {
            x = startPos.x-width;
        } else {
            x = startPos.x;
        }
        float y;
        if (endPos.y < startPos.y) {
            y = startPos.y;
        } else {
            y = startPos.y + height;
        }

        return new Rect(x, y, width, height);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isDragging) {
            Point newPos = e.getLocationOnScreen();
            newPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
            Dimension viewSize = controller.viewport.getSize();
            newPos.x = (int)clamp(newPos.x, 0, viewSize.width);
            newPos.y = (int)clamp(newPos.y, 0, viewSize.height);
            int dx = newPos.x - lastDragPoint.x;
            int dy = -1 * (newPos.y - lastDragPoint.y);
            lastDragPoint = newPos;
            if ((dx+dy)==0) { return; } // No allowed movement, no drag
            controller.moveMap(dx, dy);
        }
        if (isMarking && (startPos != null)) {
            endPos = e.getLocationOnScreen();
            endPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
            controller.setMarkerRect(getMarkRect(startPos, endPos));
            view.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (endPos == null) {
            endPos = startPos;
        }
        switch(e.getButton()) {
            case dragButton:
                if (isDragging) {
                    // Some easing movement here or something...
                }
                isDragging = false;
                break;
            case markButton:
                if (isMarking && (startPos != null)) { // Don't attempt to zoom before clicking :u
                    Viewport port = controller.viewport;
                    markRect = getMarkRect(startPos, endPos);
                    float rHeight = markRect.height;
                    float rWidth = markRect.width;
                    float rX = markRect.x;
                    float rY = markRect.y;
                    if (rHeight < 15) { // Default zoom-ish
                        System.out.println("Using default zoom");
                        rHeight = 120;
                        rWidth = rHeight*port.ratio();
                        rX = markRect.x - (rWidth - (markRect.width))/2;
                        rY = markRect.y + (rHeight - (markRect.height))/2;
                    }
                    controller.setMarkerRect(null);

                    Rect mapArea = port.getMapArea(new Rect(rX, rY, rWidth, rHeight));
                    float mapRatio = mapArea.width/mapArea.height;

                    controller.draw(port.setSource(mapArea));
                }
                isMarking = false;
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
}

