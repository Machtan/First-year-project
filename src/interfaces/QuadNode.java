package interfaces;

import classes.Rect;

/**
 * The QuadNode class is an interface needed for classes that can be 
 * inserted into the QuadTree
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public interface QuadNode {
    public boolean collidesWith(Rect area);
}
