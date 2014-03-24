package classes;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * The CMouseHandler class handles mouse-based input of the controller, which
 * is mainly used for selecting and zooming in on an area.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 24-Mar-2014
 */

public class CMouseHandler implements MouseListener, MouseMotionListener {
    
    private Point startPos;
    private Point endPos;
    private Rect markRect;
    private final Controller controller;
    private final OptimizedView view;
    
    public CMouseHandler(Controller controller) {
        this.controller = controller;
        view = controller.getView();
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        startPos = e.getLocationOnScreen();
        startPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        markRect = new Rect(startPos.x, startPos.y, 0, 0);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startPos == null) { return; }
        endPos = e.getLocationOnScreen();
        endPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);

        double width = Math.abs(startPos.x - endPos.x);
        double height = Math.abs(startPos.y - endPos.y);

        // Restrict the ratio
        if (width < height*controller.wperh) { // Height is larger
            height = width/controller.wperh; // The smaller is used
            //width = height*wperh; // The bigger is used
        } else {
            width = height*controller.wperh; // The smaller is used
            //height = width/wperh; // The bigger is used
        }

        double x;
        if (endPos.x < startPos.x) {
            x = startPos.x-width;
        } else {
            x = startPos.x;
        }
        double y;
        if (endPos.y < startPos.y) {
            y = startPos.y;
        } else {
            y = startPos.y + height;
        }

        markRect = new Rect(x, y, width, height);
        view.setMarkerRect(markRect);
        view.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        double rHeight = markRect.height;
        double rWidth = markRect.width;
        double rX = markRect.x;
        double rY = markRect.y;
        if (rHeight < 15) { // Default zoom-ish
            rHeight = 120;
            rWidth = rHeight*controller.wperh;
            rX = markRect.x - (rWidth - (markRect.width))/2;
            rY = markRect.y + (rHeight - (markRect.height))/2;
        } 

        // create a new active rect from the marker rect
        double relx = rX      / view.getWidth();
        double rely = 1 - (rY / view.getHeight()); // Invert y
        double relh = rHeight / view.getHeight();
        double relw = rWidth  / view.getWidth(); // same aspect
        
        Rect activeRect = controller.getActiveRect();
        
        double x = activeRect.x + (relx * activeRect.width);
        double y = activeRect.y + (rely * activeRect.height);
        double width = relw * activeRect.width;
        double height = relh * activeRect.height;

        Rect newArea = new Rect(x, y, width, height);

        view.setMarkerRect(null);
        controller.setActiveRect(newArea);
        controller.redraw();
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

