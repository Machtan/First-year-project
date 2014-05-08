package classes;

import enums.RoadType;
import java.util.HashSet;

/**
 * The Quad class divides a Rect area into smaller Rect subareas to help ease up 
 * the resources needed when working with areas with a large amount of elements.
 * @author Daniel
 * @author Jakob
 * @author Isabella
 * @author Alekxander
 */
public class Quad {

    private Quad[] subquads; // 4 subquads if necessarry. Empty if not.
    private boolean bottom; // True if the element is the bottommost element.
    private short maxNodes = 400; // Number of nodes a quad can hold before it splits.
    private final short depth; // The depth of the quad
    public final Rect area; // The area of the Quad
    private FastArList<RoadPart> nodeList; // The elements in the Quad.
    private RoadPart[] nodes;
    private final short maxDepth; // The max depth
    
    public Quad(Rect area, short maxNodes, short maxDepth, short depth) {
        this.area = area;
        this.maxNodes = maxNodes;
        this.maxDepth = maxDepth;
        this.depth = depth;
        nodeList = new FastArList<>();
        bottom = true;
    }
    
    /**
     * Adds an item to a quad. If quad has subquads, the item is added to the
     * corresponding subquad instead.
     * @param node 
     */
    public void add(RoadPart node) {
        if (bottom) {
            //System.out.println("Adding "+node+" to "+this);
            nodeList.add(node);
            if (nodeList.size() > maxNodes && depth < maxDepth) {
                split();
            }
        } else {
            for (Quad subquad : subquads) {
                if (subquad.area.collidesWith(node.getRect())) {
                    subquad.add(node);
                    //break; // Make it duplicate-safe :)
                }
            }
        }
    }
    
    
    /**
     * Fills the given FastArList with the items from the given area of this quad
     * @param area The area to look in
     * @param list The list to add nodes to
     */
    protected void getIn(Rect area, FastArList<RoadPart> list) {
        if (bottom) {
            if (area.contains(this.area)) {
                list.addAll(nodes);
            } else {
                for (RoadPart node : nodes) {
                    if (node.getRect().collidesWith(area)) {
                        list.add(node);
                    }
                }
            }
        } else {
            for (Quad subquad : subquads) {
                if (subquad.area.collidesWith(area)) {
                    subquad.getIn(area, list);
                }
            }
        }
    }
        
    /**
     * Freezes the tree, preventing further addition to it and improving its 
     * performance by converting lists to arrays
     */
    protected void freeze() {
        if (bottom) {
            nodes = nodeList.toArray(new RoadPart[nodeList.size()]);
            nodeList = null;
        } else {
            for (Quad subquad : subquads) {
                subquad.freeze();
            }
        }
    }
    
    

    /**
     * Splits a quad into four subquads by added four quads into subquads field
     * and setting bottom field to false.
     */
    public void split() {
        if (bottom == true && depth < maxDepth) {
            float hw = 0.5f * area.width; // Half width
            float hh = 0.5f * area.height; // Half height
            Rect swRect = new Rect(area.x, area.y, hw, hh);
            Rect nwRect = new Rect(area.x, area.y + 0.5f * area.height, hw, hh);
            Rect seRect = new Rect(area.x + 0.5f * area.width, area.y, hw, hh);
            Rect neRect = new Rect(area.x + 0.5f * area.width, area.y + hh, hw, hh);
            
            Quad sw = new Quad(swRect, maxNodes, maxDepth, (short)(this.depth+1));
            Quad nw = new Quad(nwRect, maxNodes, maxDepth, (short)(this.depth+1));
            Quad se = new Quad(seRect, maxNodes, maxDepth, (short)(this.depth+1));
            Quad ne = new Quad(neRect, maxNodes, maxDepth, (short)(this.depth+1));
            
            subquads = new Quad[] {sw, nw, se, ne};
            bottom = false;

            // nu skal vi indele nodes fra vores Quad til at være i de mindre subquads
            for (RoadPart i : nodeList.toArray(new RoadPart[nodeList.size()])) {
                for (Quad subquad : subquads) {
                    if (i.getRect().collidesWith(subquad.area)) {
                        subquad.add(i);
                    }
                }
            }
            nodeList = null;
        }
    }
    
    @Override
    public String toString() {
        return "Quad( Depth: "+depth+" Bottom: "+bottom+" @ "+area+")";
    }
}
