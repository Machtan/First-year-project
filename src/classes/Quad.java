package classes;

import enums.RoadType;
import interfaces.QuadNode;
import java.util.HashSet;

/**
 * The Quad class divides a Rect area into smaller Rect subareas to help ease up 
 * the resources needed when working with areas with a large amount of elements.
 * @author Daniel
 * @author Jakob
 * @author Isabella
 * @author Alekxander
 */
public class Quad<Item extends RoadPart> {

    // 4 subquads if necessarry. Empty if not.
    private Quad[] subquads;
    // True if the element is the bottommost element.
    private boolean bottom;
    // Number of nodes a quad can hold before it splits.
    private int maxNodes = 400;
    // The depth of the quad
    private int depth;
    // The area of the Quad
    public final Rect area;
    // The elements in the Quad.
    private HashSet<Item> nodes;
    // The max depth
    private int maxDepth;
    
    public Quad(Rect area, int maxNodes, int maxDepth, int depth) {
        this.area = area;
        this.maxNodes = maxNodes;
        this.maxDepth = maxDepth;
        this.depth = depth;
        nodes = new HashSet<>();
        bottom = true;
    }
    
    /**
     * Retrieve elements in a given area.
     * @param rect area to retrieve elements from
     * @return HashSet<Item> with elements.
     */
    public HashSet<Item> getIn(Rect rect) {        
        if (bottom == true) {
            return (HashSet<Item>) nodes.clone();
        } else {
            HashSet<Item> hSet = new HashSet<>();
            for (Quad subquad : subquads) {
                if (subquad.area.collidesWith(rect)) {
                    hSet.addAll(subquad.getIn(rect));
                }
            }
            return hSet;
        }
    }
    
    /**
     * Retrieve elements in a given area and add them to 
     * a given HashSet
     * @param rect area to retrieve elements from.
     * @param set HashSet<Item> to add items to.
     */
    public void getIn(Rect rect, HashSet<Item> set) { // Fills the given set
        if (bottom == true) {
            if (rect.contains(area)) {
                for (Item node : nodes) {
                    if (node.getRect().collidesWith(rect)) {
                        set.add(node);
                    }
                }
            } else {
                set.addAll(nodes);
            }
        } else {
            for (Quad subquad : subquads) {
                if (subquad.area.collidesWith(rect)) {
                    subquad.getIn(rect, set);
                }
            }
        }
    }
    
    /**
     * Retrieve elements in a riven area and add them to a given HashSet
     * if they are of a certain type.
     * @param rect
     * @param set
     * @param types 
     */
    public void getSelectedIn(Rect rect, HashSet<Item> set, HashSet<RoadType> types) {
        if (bottom == true) {
            if (rect.contains(area)) {
                for (Item node : nodes) {
                    if (node.getRect().collidesWith(rect) && types.contains(node.type)) {
                        set.add(node);
                    }
                }
            } else {
                for (Item node : nodes) {
                    if (types.contains(node.type)) {
                        set.add(node);
                    }
                }
            }
        } else {
            for (Quad subquad : subquads) {
                if (subquad.area.collidesWith(rect)) {
                    subquad.getSelectedIn(rect, set, types);
                }
            }
        }
    }
    
    
    /**
     * Adds an item to a quad. If quad has subquads, the item is added to the
     * corresponding subquad instead.
     * @param node 
     */
    public void add(Item node) {
        if (bottom) {
            //System.out.println("Adding "+node+" to "+this);
            nodes.add(node);
            if (nodes.size() > maxNodes && depth < maxDepth) {
                split();
            }
        } else {
            for (Quad subquad : subquads) {
                if (subquad.area.collidesWith(node.getRect())) {
                    subquad.add(node);
                }
            }
        }
    }

    /**
     * Splits a quad into four subquads by added four quads into subquads field
     * and setting bottom field to false.
     */
    public void split() {
        if (bottom == true && depth < maxDepth) {
            double hw = 0.5 * area.width; // Half width
            double hh = 0.5 * area.height; // Half height
            Rect swRect = new Rect(area.x, area.y, hw, hh);
            Quad sw = new Quad(swRect, maxNodes, maxDepth, this.depth+1);
            
            Rect nwRect = new Rect(area.x, area.y + 0.5 * area.height, hw, hh);
            Quad nw = new Quad(nwRect, maxNodes, maxDepth, this.depth+1);
            
            Rect seRect = new Rect(area.x + 0.5 * area.width, area.y, hw, hh);
            Quad se = new Quad(seRect, maxNodes, maxDepth, this.depth+1);
            
            Rect neRect = new Rect(area.x + 0.5 * area.width, area.y + hh, hw, hh);
            Quad ne = new Quad(neRect, maxNodes, maxDepth, this.depth+1);
            
            subquads = new Quad[4];
            subquads[0] = sw;
            subquads[1] = nw;
            subquads[2] = se;
            subquads[3] = ne;
            
            bottom = false;

            // nu skal vi indele nodes fra vores Quad til at være i de mindre subquads
            for (Item i : nodes) {
                for (Quad subquad : subquads) {
                    if (i.getRect().collidesWith(subquad.area)) {
                        subquad.add(i);
                    }
                }
            }
        }
    }
}
