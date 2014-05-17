package classes;

import java.awt.Dimension;
import java.awt.geom.Point2D;

/**
 * The Rect class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class Rect {
    
    public final float x;
    public final float y;
    public final float width;
    public final float height;
    
    /**
     * Constructor for the Rect class
     * @param x The position of the rectangle on the x-axis
     * @param y The position of the rectangle on the y-axis
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     */
    public Rect (float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Rect(Dimension dim) {
        this(0, 0, dim.width, dim.height);
    }
    
    public float right() {
        return x+width;
    }
    public float top() {
        return y+height;
    }
    
    /**
     * Returns whether this rectangle collides with another rectangle
     * @param other The other rectangle
     * @return Whether the rectangles overlap
     */
    public boolean collidesWith(Rect other) {
        return this.right() > other.x && this.x < other.right() && 
                this.top() > other.y && this.y < other.top();
    }
    
    /**
     * Checks whether the other rect is fully contained by this rect
     * @param other The other rect
     * @return whether the other rect is fully contained by this rect
     */
    public boolean contains(Rect other) {
        return !((other.top() > this.top())||(other.y < this.y)|| 
                (other.x < this.x)||(other.right() > this.right()));
    }
    
    @Override
    public String toString() {
        return "Rect("+x+", "+y+", "+width+", "+height+")";
    }
    
    /**
     * Shifts the rect by the given amounts
     * @param x
     * @param y
     * @return A new rect shifted by the given amount
     */
    public Rect shift(float x, float y) {
        return new Rect(this.x+x, this.y+y, this.width, this.height);
    }
    
    /**
     * Returns the center of this rectangle as a 2d point
     * @return the center of this rectangle as a 2d point
     */
    public Point2D.Float center() {
        return new Point2D.Float(x+ width/2, y + height/2);
    }
    
    /**
     * Returns the rect scaled by a given positive factor
     * @param factor The factor to scale by
     * @return A new rect scaled by the factor.
     */
    public Rect getScaled(float factor) {
        if (factor < 0) {
            throw new RuntimeException("The scale factor may not be negative! ("+factor+")");
        }
        float rw = width * factor;
        float rh = height * factor;
        float hdw = (width - rw) / 2; // half delta width
        float hdh = (height - rh) / 2; // ~ height
        float rx = x + hdw;
        float ry = y + hdh;
        return new Rect(rx, ry, rw, rh);
    }
    
    /**
     * Returns the rect moved to the new position
     * @param x
     * @param y
     * @return 
     */
    public Rect shiftTo(float x, float y) {
        return new Rect(x, y, width, height);
    }
}
