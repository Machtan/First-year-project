package classes;

import interfaces.QuadNode;
import interfaces.StreamedContainer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Quad class divides a Rect area into smaller Rect subareas to help ease up 
 * the resources needed when working with areas with a large amount of elements.
 * @author Jakob
 * @author Alekxander
 * @param <T> The type of node to store in the quad
 */
public class Quad <T extends QuadNode> {

    private Quad[] subquads; // 4 subquads if necessarry. Empty if not.
    private HashMap<Rect, FastArList<T>> edgeCases;
    private boolean bottom; // True if the element is the bottommost element.
    private short maxNodes = 400; // Number of nodes a quad can hold before it splits.
    private final short depth; // The depth of the quad
    public final Rect area; // The area of the Quad
    private final short maxDepth; // The max depth
    private FastArList<T> nodeList; // The elements in the Quad.
    
    public Quad(Rect area, short maxNodes, short maxDepth, short depth) {
        this.area = area;
        this.maxNodes = maxNodes;
        this.maxDepth = maxDepth;
        this.depth = depth;
        edgeCases = new HashMap<>();
        Rect verArea = new Rect(area.x + area.width/2, area.y, 0, area.height);
        Rect horArea = new Rect(area.x, area.y + area.height/2, area.width, 0);
        edgeCases.put(verArea, new FastArList<T>());
        edgeCases.put(horArea, new FastArList<T>());
        nodeList = new FastArList<>();
        bottom = true;
    }
    
    /**
     * Adds an item to a quad. If quad has subquads, the item is added to the
     * corresponding subquad instead.
     * @param node 
     */
    public void add(T node) {
        for (Rect r : edgeCases.keySet()) {
            if (node.collidesWith(r)) {
                edgeCases.get(r).add(node);
                return; // Don't do anything else
            }
        }
        if (bottom) {
            //System.out.println("Adding "+node+" to "+this);
            nodeList.add(node);
            if (nodeList.size() > maxNodes && depth < maxDepth) {
                split();
            }
        } else {
            for (Quad subquad : subquads) {
                if (node.collidesWith(subquad.area)) {
                    subquad.add(node);
                    break;
                }
            }
        }
    }
    
    
    /**
     * Fills the given FastArList with the items from the given area of this quad
     * @param area The area to look in
     * @param target The streamed container to add roads to
     */
    protected void getIn(Rect area, StreamedContainer target) {
        for (Rect r: edgeCases.keySet()) { // Add edge cases
            if (r.collidesWith(area)) {
                //System.out.println("Edge case at "+r+" collides with "+area);
                for (T node : edgeCases.get(r)) {
                    if (node.collidesWith(area)) {
                        target.add(node);
                    }
                }
            }
        }
        if (bottom) {
            if (area.contains(this.area)) {
                for(T node : nodeList) {
                    target.add(node);
                }
                
            } else {
                for (T node : nodeList) {
                    if (node.collidesWith(area)) {
                        target.add(node);
                    }
                }
            }
        } else {
            for (Quad subquad : subquads) {
                if (subquad.area.collidesWith(area)) {
                    subquad.getIn(area, target);
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
            for (T node : nodeList) {
                for (Quad subquad : subquads) {
                    if (node.collidesWith(subquad.area)) {
                        subquad.add(node);
                        //continue;
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
