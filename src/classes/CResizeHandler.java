package classes;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.Timer;

/**
 * The CResizeHandler class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 24-Mar-2014
 */
public class CResizeHandler implements ComponentListener, ActionListener {
    private final Timer resizeTimer;
    private final Rect limitRect;
    private Dimension prevSize;
    private Dimension startResizeSize; // The size when a resize is started
    private Controller controller;
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    
    // Tweakable values
    private final static int resizeDelay = 400; // milliseconds
    private final static int margin = 40; // The amount of pixels to load to the right when resizing
    public final static double zoomFactor = 0.7;
    private Rect lastArea;
    
    
    public CResizeHandler(Controller controller) {
        // Prepare resize handling :)
        this.controller = controller;
        controller.getView().addComponentListener(this);
        resizeTimer = new Timer(resizeDelay, this);
        resizeTimer.setRepeats(false);
        Rect activeRect = controller.getActiveRect();
        double lw = activeRect.width * 1.2;
        double lx = activeRect.x - 0.1 * activeRect.width;
        double lh = activeRect.height * 1.2;
        double ly = activeRect.y - 0.1 * activeRect.height;
        limitRect = new Rect(lx, ly, lw, lh);
        prevSize = controller.getView().getSize(); // prepare for scaling
    }
    
    /**
     * Sets the last area of the view
     * @param area The last area
     */
    public void setLastArea(Rect area) {
        lastArea = area;
    }
    
    /**
     * Zooms the view in
     */
    public void zoomIn() {
        Rect activeRect = controller.getActiveRect();
        double newWidth = activeRect.width*zoomFactor;
        double newHeight = activeRect.height*zoomFactor;
        double newX = activeRect.x + (activeRect.width-newWidth)/2;
        double newY = activeRect.y + (activeRect.height-newHeight)/2;
        System.out.println("Zooming...");
        controller.setActiveRect(new Rect(newX, newY, newWidth, newHeight));
        controller.redraw();
        System.out.println("Zoomed!");
    }

    /**
     * Zooms the view out
     */
    public void zoomOut() {
        Rect activeRect = controller.getActiveRect();
        System.out.println("Zooming out!");
        double zOutFactor = 1/zoomFactor;
        double newWidth = activeRect.width*zOutFactor;
        double newHeight = activeRect.height*zOutFactor;
        double newX = activeRect.x - (newWidth-activeRect.width)/2;
        double newY = activeRect.y - (newHeight-activeRect.height)/2;
        if (newHeight > limitRect.height) {
            newHeight = limitRect.height;
            newWidth = limitRect.width;
            newX = limitRect.x;
            newY = limitRect.y;
            System.out.println("Restricted the zooming out");
        }

        if (activeRect.height != limitRect.height) {
            controller.setActiveRect(new Rect(newX, newY, newWidth, newHeight));
            controller.redraw();
        } else {
            System.out.println("No size change, ignoring...");
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        OptimizedView view = controller.getView();
        Rect activeRect = controller.getActiveRect();
        
        if (prevSize == null) { return; } // You're too fast ;)
        if (!view.initialized()) { return; } // You're still too fast ;)
        Dimension newSize = view.getSize();
        if (newSize.height == 0 || newSize.width == 0) { return; } // This cannot be resized ;)
        if (newSize.height != prevSize.height) {
            view.resizeMap(newSize);
            prevSize = newSize;
        } else if (newSize.width > Math.min(view.getSourceWidth()-margin, screenSize.width)) { // The windows is wider now
            int prevRightLimit = view.getSourceWidth();
            int newRightLimit = Math.max(prevRightLimit+margin, view.getWidth()+margin);

            System.out.println("Moving the right limit to "+newRightLimit);
            controller.resizeActiveArea(newSize);

            double sx = lastArea.right;
            double sy = lastArea.y;
            double sw = (newRightLimit-prevRightLimit) * (activeRect.width / view.getWidth());
            double sh = lastArea.height;
            Rect source = new Rect(sx, sy, sw, sh);

            double tx = prevRightLimit;
            double ty = 0;
            double tw = newRightLimit;
            double th = newSize.height;
            Rect target = new Rect(tx, ty, tw, th);

            // Update the image to show the new content ;)
            view.offsetImage(0, 0, controller.getLines(source, target), 
                    new Dimension(newRightLimit, view.getHeight()));
            lastArea = source;
        }
        if (!resizeTimer.isRunning()) {
            startResizeSize = view.getSize();
            resizeTimer.start();
        } else {
            resizeTimer.restart();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { // Once the user has finished redrawing
        // Only redraw if it is needed, plx
        if (controller.getView().getHeight() != startResizeSize.height) {
            System.out.println("Redrawing after resizing...");
            controller.redraw();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {}
    @Override
    public void componentShown(ComponentEvent e) {}
    @Override
    public void componentHidden(ComponentEvent e) {}
}
