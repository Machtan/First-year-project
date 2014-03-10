package interfaces;

import classes.Rect;

/**
 * The QuadNode class is an interface needed for classes that should be 
 * inserted into the QuadTree
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public interface QuadNode {
    public Rect getRect();
}
