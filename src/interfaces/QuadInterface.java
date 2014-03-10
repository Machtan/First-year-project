package interfaces;
import classes.Rect;

/**
 * The QuadInterface interface is an interface for the quad trees
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 * @param <Item> An item implementing the QuadNode interface (Rect getRect())
 */
public interface QuadInterface <Item extends QuadNode>{
    public Item[] getIn(Rect rect);
    public void add(Item node);
}
