package classes;

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
    
    private Point startPos;
    private Point endPos;
    private Rect markRect;
    private final Controller controller;
    private final JPanel view;
    
    public CMouseHandler(Controller controller, JPanel target) {
        this.controller = controller;
        view = target;
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        startPos = e.getLocationOnScreen();
        startPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        markRect = new Rect(startPos.x, startPos.y, 0, 0);
    }
    
    private Rect getMarkRect(Point startPos, Point endPos) {
        double width = Math.abs(startPos.x - endPos.x);
        double height = Math.abs(startPos.y - endPos.y);
        double wperh = controller.viewport.ratio();
        
        // Restrict the ratio
        double expectedWidth = height*wperh;
        if (width < expectedWidth) { // Height is larger
            height = width/wperh; // The smaller is used
            
            //width = height*wperh; // The bigger is used
        } else {
            width = expectedWidth; // The smaller is used
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

        return new Rect(x, y, width, height);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startPos == null) { return; }
        endPos = e.getLocationOnScreen();
        endPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        controller.setMarkerRect(getMarkRect(startPos, endPos));
        view.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Viewport port = controller.viewport;
        markRect = getMarkRect(startPos, endPos);
        double rHeight = markRect.height;
        double rWidth = markRect.width;
        double rX = markRect.x;
        double rY = markRect.y;
        if (rHeight < 15) { // Default zoom-ish
            System.out.println("Using default zoom");
            rHeight = 120;
            rWidth = rHeight*port.ratio();
            rX = markRect.x - (rWidth - (markRect.width))/2;
            rY = markRect.y + (rHeight - (markRect.height))/2;
        }
        controller.setMarkerRect(null);
        
        Rect mapArea = port.getMapArea(new Rect(rX, rY, rWidth, rHeight));
        double mapRatio = mapArea.width/mapArea.height;
        
        controller.draw(port.setSource(mapArea));
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

