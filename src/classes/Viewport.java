package classes;

/**
 * The Viewport class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 05-May-2014
 */
import java.awt.Dimension;
import static java.lang.Math.abs;

public class Viewport {
    private Rect activeRect;
    private Rect bounds;
    private double widthFactor = 1; // How big the width is relatively to the correct scaling ratio
    private OptimizedView target;
    private final double wperh;
    
    /**
     * Projection is a class that represents the projection of a part of the map
     * to a part of the "View" window
     */
    public static class Projection {
        public final Rect source; // The map space to draw
        public final Rect target; // The window space to draw on
        public Projection(Rect source, Rect target) {
            this.source = source;
            this.target = target;
        }
        public Projection(Rect source, Dimension dim) {
            this(source, new Rect(dim));
        }
    }
    
    public Viewport(Rect bounds, double zoomLevel, OptimizedView target) {
        this.bounds = bounds;
        this.activeRect = bounds;
        this.target = target;
        wperh = bounds.height / bounds.width;
        zoomBy(1-zoomLevel);
    }
    
    public Rect getShown() {
        return activeRect;
    }
    
    /**
     * Returns the current projection of the viewport
     * @return the current projection of the viewport
     */
    public Projection getProjection() {
        return new Projection(activeRect, target.getSize());
    }
    
    /**
     * Changes the source area of the viewport to the given area and returns
     * the resulting projection
     * @param source The area to source from
     * @return The resulting projection
     */
    public Projection setSource(Rect source) {
        System.out.println("Changing the viewport source to "+source);
        activeRect = source;
        return getProjection();
    }
    
    /**
     * Returns the map coordinate at the given pixel on the x-axis
     * @param pixelX The pixel x-coordinate
     * @return The map x-coordinate at the pixel
     */
    public double getMapX(int pixelX) {
        double relX = (double)pixelX / target.getWidth();
        double deltaW = activeRect.width * relX;
        return activeRect.x + deltaW;
    }
    
    public double ratio() { // width per height
        Dimension dim = target.getSize();
        return (double)dim.width / dim.height;
    }
    
    public Rect getMapArea(Rect screenRect) {
        System.out.println("Getting the map rect for screen rect @ "+screenRect);
        double x = getMapX((int)screenRect.left);
        double y = getMapY((int)screenRect.bottom);
        
        double width = (screenRect.width/target.getWidth()) * activeRect.width;
        double height = (screenRect.height/target.getHeight()) * activeRect.height;
        
        System.out.println("x,y,w,h: "+x+" "+y+" "+width+" "+height);
        System.out.println("Ratio: "+width/height);
        
        return new Rect(x, y, width, height);
    }
    
    /**
     * Returns the map coordinate at the given pixel on the y-axis
     * @param pixelY The pixel y-coordinate
     * @return The map y-coordinate at the pixel
     */
    public double getMapY(int pixelY) {
        double relY = (double)pixelY / target.getHeight();
        double deltaH = activeRect.height * relY;
        return activeRect.y + (activeRect.height - deltaH);
    }
    
    private void changeWidth(double deltaWidth) {
        
    }
    
    /**
     * Resizes the physical window of the viewport and returns the new projection
     * @param dim The new dimension of the window
     * @return The resulting projection
     */
    public Projection resize(Dimension dim) {
        
        return null;
    }
    
    // Returns a rectangle scaled by factor relatively to its center
    private Rect getScaled(Rect rect, double factor) {
        double rw = rect.width * factor;
        double rh = rect.height * factor;
        double hdw = (rect.width - rw) / 2; // half delta width
        double hdh = (rect.height - rh) / 2; // ~ height
        double rx = rect.x + hdw;
        double ry = rect.y + hdh;
        return new Rect(rx, ry, rw, rh);
    }
    
    // Zooms by a relative factor
    public Projection zoomBy(double part) { 
        if (part <= -1) {
            throw new RuntimeException("The zoom factor should be greater than -1");
        }
        activeRect = activeRect.getScaled(1-part);
        return getProjection();
    }
    
    /**
     * Refits the viewport to have the correct ratio and returns the new projection
     * @return Returns the new projection
     */
    public Projection refit() {
        double wperh = ratio();
        if ((activeRect.width / activeRect.height) != wperh) {
            System.out.println("Refitting the viewport... :)");
            double width = activeRect.height * wperh;
            setSource(new Rect(activeRect.x, activeRect.y, width, activeRect.height));
        }
        return getProjection();
    }
    
    /**
     * Zooms to the given static zoom level based on the bounds set for this 
     * viewport, and returns the resulting projection
     * @param part The zoom level between 0 and 1
     * @return The new projection
     */
    public Projection zoomTo(double part) {
        if (part < 0 || part > 1) {
            throw new RuntimeException("Absolute zoom must be between 0 and 1");
        }
        activeRect = bounds.getScaled(part);
        return refit();
    }
    
    /**
     * Returns an array with the projections of the newly visible areas, 
     * after moving the viewport by the given values
     * @param dx How much to move on the x-axis
     * @param dy How much to move on the y-axis
     * @return An array with the projections of the newly visible area
     */
    public Projection[] move(double dx, double dy) { 
        if (dx+dy == 0) {
            return new Projection[]{}; // No movement :u
        }
        
        // Copy code from resizeHandler here and refit
       
        double width = target.getWidth();
        double height = target.getHeight();
        
        // Pixels per unit
        double ppu = target.getHeight()/activeRect.height;
        double upp = 1.0 / ppu;

        // Potentially add restriction here?


        // Prepare the visual changes
        Rect verArea = null;
        Rect horArea = null;
        Rect a = activeRect;
        // Create the new active rect
        Rect na = new Rect(activeRect.x-dx*upp, activeRect.y-dy*upp, 
                activeRect.width, activeRect.height); 

        Rect verTarget = null;
        Rect horTarget = null;
        // Find out which parts of the map should be redrawn
        if (dx > 0) { // (render)Left pressed -> map goes right 
            verArea = new Rect(na.left, na.bottom, Math.abs(dx*upp), a.height); // <-- not working
            verTarget = new Rect(0, 0, width, height);
        } else if (dx < 0) { // (render)Right pressed -> map goes left
            verArea = new Rect(a.right, na.bottom, Math.abs(dx*upp), a.height);
            verTarget = new Rect(width-abs(dx), 0, width, height);
        }
        if (dx > 0) { // (render)Down -> map up
            horArea = new Rect(na.left, na.bottom, a.width, Math.abs(dy*upp)); // <-- not working
            horTarget = new Rect(0, 0, width, abs(dy)); // 
        } else if (dy < 0) { // (render)Up -> map down
            horArea = new Rect(na.left, a.top, a.width, Math.abs(dy*upp));
            horTarget = new Rect(0, height-abs(dy), width, abs(dy)); // 
        }
        
        a = na; // Assign the new active rect

        // Create and return the projections :)
        if ((verTarget != null) && (horTarget != null)) {
            return new Projection[]{new Projection(verArea, verTarget), 
                new Projection(horArea, horTarget)};
        } else if (verTarget != null) {
            return new Projection[]{new Projection(verArea, verTarget)};
        } else {
            return new Projection[]{new Projection(horArea, horTarget)};
        }
    } 
    
    /**
     * Returns an array with the projections of the newly visible areas, 
     * after moving the viewport by the given pixel values
     * @param dx The movement on the x-axis
     * @param dy The movement on the y-axis
     * @return an array with the projections of the newly visible areas
     */
    public Projection[] movePixels(int dx, int dy) {
        double upp = activeRect.height / target.getHeight();
        return move(dx * upp, dy * upp);
    }
    
    /**
     * Centers the viewport on the given map coordinates
     * @param x The x-axis coordinate
     * @param y The y-axis coordinate
     */
    public void centerOn(int x, int y) { // Rect coords
        double hw = activeRect.width / 2;
        double hh = activeRect.height / 2;
        activeRect = new Rect(x-hw, y+hh, activeRect.width, activeRect.height);
    }
}
