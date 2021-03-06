package classes;

/**
 * The Viewport class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 05-May-2014
 */
import java.awt.Dimension;
import static java.lang.Math.abs;
import javax.swing.JPanel;

public class Viewport {
    private Rect activeRect;
    private Rect bounds;
    private JPanel target;
    
    /**
     * Projection is a class that represents the projection of a part of the map
     * to a part of the "View" window
     */
    public static class Projection {
        public final Rect source; // The map space to draw
        public final Rect target; // The window space to draw on
        public final float ppu; // Pixels per unit
        public final float upp; // Units per pixel
        /**
         * An empty projection.
         */
        public final static Projection Empty = new Projection(new Rect(0,0,0,0), new Rect(0,0,0,0));
        public Projection(Rect source, Rect target) {
            this.source = source;
            this.target = target;
            ppu = target.width / source.width;
            upp = 1 / ppu;
        }
        public Projection(Rect source, Dimension dim) {
            this(source, new Rect(dim));
        }
        @Override
        public String toString() {
            return "P["+source+" -> "+target+"]";
        }
    }
    
    public Viewport(Rect bounds, float zoomLevel, JPanel target) {
        this.bounds = bounds;
        this.target = target;
        activeRect = bounds;
        refit();
        zoomBy(1-zoomLevel);
    }
    
    public Rect getShown() {
        return activeRect;
    }
    
    /**
     * Returns the size of the viewport's view space
     * @return the size of the viewport's view space
     */
    public Dimension getSize() {
        return target.getSize();
    }
    
    /**
     * Returns the projection of the viewport on the given dimension
     * @param dim The dimension to use
     * @return The projection of the viewport
     */
    public Projection getProjection(Dimension dim) {
        return new Projection(activeRect, dim);
    }
    
    /**
     * Returns the current projection of the viewport
     * @return the current projection of the viewport
     */
    public Projection getProjection() {
        return refit();
    }
    
    /**
     * Changes the source area of the viewport to the given area and returns
     * the resulting projection
     * @param source The area to source from
     * @return The resulting projection
     */
    public Projection setSource(Rect source) {
        //System.out.println("Changing the viewport source to "+source);
        activeRect = source;
        return refit();
    }
    
    /**
     * Returns the map coordinate at the given pixel on the x-axis
     * @param pixelX The pixel x-coordinate
     * @return The map x-coordinate at the pixel
     */
    public float getMapX(int pixelX) {
        float relX = (float)pixelX / target.getWidth();
        float deltaW = activeRect.width * relX;
        return activeRect.x + deltaW;
    }
    
    /**
     * Returns the current width/height ratio
     * @return the current width/height ratio
     */
    public float ratio() { // width per height
        Dimension dim = target.getSize();
        return (float)dim.width / dim.height;
    }
    
    public Rect getMapArea(Rect screenRect) {
        //System.out.println("Getting the map rect for screen rect @ "+screenRect);
        float x = getMapX((int)screenRect.x);
        float y = getMapY((int)screenRect.y);
        
        float width = (screenRect.width/target.getWidth()) * activeRect.width;
        float height = (screenRect.height/target.getHeight()) * activeRect.height;
        
        return new Rect(x, y, width, height);
    }
    
    /**
     * Returns the map coordinate at the given pixel on the y-axis
     * @param pixelY The pixel y-coordinate
     * @return The map y-coordinate at the pixel
     */
    public float getMapY(int pixelY) {
        float relY = (float)pixelY / target.getHeight();
        float deltaH = activeRect.height * relY;
        return activeRect.y + (activeRect.height - deltaH);
    }
    
    /**
     * Returns how long the given pixel range is in map coordinates
     * @param pixels
     * @return 
     */
    public float getLength(int pixels) {
        float upp = activeRect.width / target.getWidth();
        return upp * pixels;
    }
    
    /**
     * Returns the projection changes that would result from changing from the
     * given source width by the given amount
     * @param deltaWidth The change in width
     * @return A projection of the lines now visible by the width change
     */
    public Projection widen(int deltaWidth) {
        if (deltaWidth <= 0) {
            return Projection.Empty;
        }
        refit();
        int height = target.getHeight();
        Rect a = activeRect;
        //System.out.println("Widening with window size "+target.getSize());
        Rect targetArea = new Rect(target.getWidth()-deltaWidth, 0, deltaWidth, height);
        Rect sourceArea = new Rect(a.right()-getLength(deltaWidth), a.y, getLength(deltaWidth), a.height);      
        //refit(); // Fit to the new width
        Projection proj = new Projection(sourceArea, targetArea);
        //System.out.println("-> "+proj);
        return proj;
    }
    
    // Returns a rectangle scaled by factor relatively to its center
    private Rect getScaled(Rect rect, float factor) {
        float rw = rect.width * factor;
        float rh = rect.height * factor;
        float hdw = (rect.width - rw) / 2; // half delta width
        float hdh = (rect.height - rh) / 2; // ~ height
        float rx = rect.x + hdw;
        float ry = rect.y + hdh;
        return new Rect(rx, ry, rw, rh);
    }
    
    // Zooms by a relative factor
    public Projection zoomBy(float part) { 
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
        float wperh = ratio();
        if ((activeRect.width / activeRect.height) != wperh) {
            float width = activeRect.height * wperh;
            activeRect = new Rect(activeRect.x, activeRect.y, width, activeRect.height);
        }
        return getProjection(target.getSize()); // Beware, breaks without the param
    }
    
    /**
     * Zooms to the given static zoom level based on the bounds set for this 
     * viewport, and returns the resulting projection
     * @param part The zoom level between 0 and 1
     * @return The new projection
     */
    public Projection zoomTo(float part) {
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
    public Projection[] move(float dx, float dy) { 
        if (dx+dy == 0) {
            return new Projection[]{}; // No movement :u
        }
        
        float width = target.getWidth();
        float height = target.getHeight();
        
        // Pixels per unit
        float ppu = target.getHeight()/activeRect.height;
        float upp = 1f / ppu;

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
            horArea = new Rect(na.x, na.y, abs(dx*upp), a.height); // <-- not working
            horTarget = new Rect(0, 0, abs(dx), height);
        } else if (dx < 0) { // (render)Right pressed -> map goes left
            horArea = new Rect(a.right(), na.y, abs(dx*upp), a.height);
            horTarget = new Rect(width-abs(dx), 0, abs(dx), height);
        }
        if (dy > 0) { // (render)Down -> map up
            verArea = new Rect(na.x, na.y, a.width, abs(dy*upp)); // <-- not working
            verTarget = new Rect(0, 0, width, abs(dy)); // 
        } else if (dy < 0) { // (render)Up -> map down
            verArea = new Rect(na.x, a.top(), a.width, abs(dy*upp));
            verTarget = new Rect(0, height-abs(dy), width, abs(dy)); // 
        }
        
        activeRect = na; // Assign the new active rect

        // Create and return the projections :)
        if ((verTarget != null) && (horTarget != null)) {
            //System.out.println("Returning both targets for ("+dx+", "+dy+")");
            return new Projection[]{new Projection(horArea, horTarget), 
                new Projection(verArea, verTarget)};
        } else if (verTarget != null) {
            //System.out.println("Returning vertical target for ("+dx+", "+dy+")");
            return new Projection[]{new Projection(verArea, verTarget)};
        } else {
            //System.out.println("Returning horizontal target for ("+dx+", "+dy+")");
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
        float upp = activeRect.height / target.getHeight();
        return move(dx, dy); // Currently... unprepared
    }
    
    /**
     * Centers the viewport on the given map coordinates
     * @param x The x-axis coordinate
     * @param y The y-axis coordinate
     */
    public void centerOn(int x, int y) { // Rect coords
        float hw = activeRect.width / 2;
        float hh = activeRect.height / 2;
        activeRect = new Rect(x-hw, y+hh, activeRect.width, activeRect.height);
    }
}
