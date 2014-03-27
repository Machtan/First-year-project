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
    private int maxNodes = 400; // Number of nodes a quad can hold before it splits.
    private final int depth; // The depth of the quad
    public final Rect area; // The area of the Quad
    private FastArList<RoadPart> nodeList; // The elements in the Quad.
    private RoadPart[] nodes;
    private final int maxDepth; // The max depth
    private int subCount; // The nodes below this one
    
    public Quad(Rect area, int maxNodes, int maxDepth, int depth) {
        this.area = area;
        this.maxNodes = maxNodes;
        this.maxDepth = maxDepth;
        this.depth = depth;
        nodeList = new FastArList<>();
        bottom = true;
        subCount = 0;
    }
    
    /**
     * Adds an item to a quad. If quad has subquads, the item is added to the
     * corresponding subquad instead.
     * @param node 
     */
    public void add(RoadPart node) {
        subCount++;
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
                /*list.ensureCapacity(list.size()+nodes.length);
                for (RoadPart node : nodes) {
                    list.add(node); 
                }*/
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
     * Adds any nodes of the selected types to the given list
     * @param area The area to find nodes in
     * @param list The list to add nodes to
     * @param types The types of nodes to add
     */
    protected void getSelectedIn(Rect area, FastArList<RoadPart> list, HashSet<RoadType> types) {
        if (bottom) {
            if (area.contains(this.area)) { // If all nodes are contained
                for (RoadPart node : nodes) {
                    if (types.contains(node.type)) { // Add them if the type is ok
                        list.add(node);
                    }
                }
            } else {
                for (RoadPart node : nodes) { // Check both for containment and type
                    if (node.getRect().collidesWith(area) && types.contains(node.type)) {
                        list.add(node);
                    }
                }
            }
        } else {
            for (Quad subquad : subquads) {
                if (subquad.area.collidesWith(area)) {
                    subquad.getSelectedIn(area, list, types);
                }
            }
        }
    }
    
    /**
     * Returns the amount of nodes in or below this quad
     * @return the amount of nodes in or below this quad
     */
    public int getSubCount() {
        return subCount;
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
            double hw = 0.5 * area.width; // Half width
            double hh = 0.5 * area.height; // Half height
            Rect swRect = new Rect(area.x, area.y, hw, hh);
            Rect nwRect = new Rect(area.x, area.y + 0.5 * area.height, hw, hh);
            Rect seRect = new Rect(area.x + 0.5 * area.width, area.y, hw, hh);
            Rect neRect = new Rect(area.x + 0.5 * area.width, area.y + hh, hw, hh);
            
            Quad sw = new Quad(swRect, maxNodes, maxDepth, this.depth+1);
            Quad nw = new Quad(nwRect, maxNodes, maxDepth, this.depth+1);
            Quad se = new Quad(seRect, maxNodes, maxDepth, this.depth+1);
            Quad ne = new Quad(neRect, maxNodes, maxDepth, this.depth+1);
            
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
}
