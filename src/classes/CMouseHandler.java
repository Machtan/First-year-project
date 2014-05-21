package classes;

import interfaces.Receiver;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

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
    private Point lastPos;
    private Rect markRect;
    private final Controller controller;
    private final OptimizedView view;
    private boolean isMarking = false;
    private boolean isDragging = false;
    private HashMap<Integer, Boolean> isDown = new HashMap<>();;
    
    public CMouseHandler(Controller controller, OptimizedView target) {
        this.controller = controller;
        isDown.put(dragButton, false);
        isDown.put(markButton, false);
        
        view = target;
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        int button = e.getButton();
        if (isDown.containsKey(button)) {
            isDown.put(button, true);
        }
        startPos = e.getLocationOnScreen();
        startPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        lastPos = startPos;
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
        Point newPos = e.getLocationOnScreen();
        newPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        if (isDown.get(dragButton)) {
            isDragging = true;
            Dimension viewSize = controller.viewport.getSize();
            newPos.x = (int)clamp(newPos.x, 0, viewSize.width);
            newPos.y = (int)clamp(newPos.y, 0, viewSize.height);
            int dx = newPos.x - lastPos.x;
            int dy = -1 * (newPos.y - lastPos.y);
            if ((dx+dy)==0) { return; } // No allowed movement, no drag
            controller.moveMap(dx, dy);
        }
        if (isDown.get(markButton)) {
            isMarking = true;
            controller.setMarkerRect(getMarkRect(startPos, newPos));
            view.repaint();
        }
        lastPos = newPos;
    }
    
    /**
     * Attempts to calculate the shortest path between the start and end
     */
    private void findShortestPath() {
        if ((view.getPathStart() != null) && (view.getPathEnd() != null)) {
           // System.out.println("Finding shortest path!...");
            Road.Edge[] path = PathFinder.findPath(controller.graph, view.getPathStart().id, view.getPathEnd().id);
            view.setPath(path);
            controller.routePanel.setRoute(path);
        }
    }
    
    /**
     * Sets where the path should start
     * @param start 
     */
    private void setPathStart(Road.Node start) {
        controller.routePanel.setPathStart(start);
        view.setPathStart(start);
        findShortestPath();
    }
    
    /**
     * Sets where the path should end
     * @param end 
     */
    private void setPathEnd(Road.Node end) {
        controller.routePanel.setPathEnd(end);
        view.setPathEnd(end);
        findShortestPath();
    }
    
    private class PositionHelper implements Receiver<Road.Node>{
        private final boolean start;
        /**
         * A helper class for receiving positions and sending them to the view
         * The start parameter is whether it should set the starting or ending
         * point of the view, when a node is received.
         */
        public PositionHelper(boolean start) {
            this.start = start;
        }

        @Override
        public void receive(Road.Node obj) {
            if (start) {
                CMouseHandler.this.setPathStart(obj);
            } else {
                CMouseHandler.this.setPathEnd(obj);
            }
            
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        Viewport port = controller.viewport;
        float mapX = port.getMapX(lastPos.x);
        float mapY = port.getMapY(lastPos.y);
        switch(button) {
            case dragButton:
                if (isDragging) {
                    // Some easing movement here or something..?
                } else {
                    // Set the starting position of the view
                    Finder.findNearestNode(mapX, mapY, new PositionHelper(true), controller);
                }
                isDragging = false;
                break;
            case markButton:
                if (isMarking) { // Don't attempt to zoom before clicking :u
                    markRect = getMarkRect(startPos, lastPos);
                    float rHeight = markRect.height;
                    float rWidth = markRect.width;
                    float rX = markRect.x;
                    float rY = markRect.y;
                    if (rHeight < 15) { // Default zoom-ish
                       // System.out.println("Using default zoom");
                        rHeight = 120;
                        rWidth = rHeight*port.ratio();
                        rX = markRect.x - (rWidth - (markRect.width))/2;
                        rY = markRect.y + (rHeight - (markRect.height))/2;
                    }
                    controller.setMarkerRect(null);

                    Rect mapArea = port.getMapArea(new Rect(rX, rY, rWidth, rHeight));
                    controller.draw(port.setSource(mapArea));
                } else {
                    // Set the ending position of the view
                    Finder.findNearestNode(mapX, mapY, new PositionHelper(false), controller);
                }
                isMarking = false;
                break;
        }
        if (isDown.containsKey(button)) {
            isDown.put(button, false);
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

