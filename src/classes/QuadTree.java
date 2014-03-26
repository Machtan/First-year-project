/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import enums.RoadType;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author Alekxander
 * @param <Item>
 */
public class QuadTree<Item extends RoadPart> extends Quad {
    public QuadTree(Rect area, int maxNodes, int maxDepth) {
        super(area, maxNodes, maxDepth, 1);
    }
    
    @Override
    public HashSet<Item> getIn(Rect rect) {
        long t1 = System.nanoTime();
        HashSet<Item> result = new HashSet<>();
        super.fillFrom(rect, result);
        double s = (System.nanoTime()-t1)/1000000000.0;
        System.out.println("Returned "+result.size()+" roads from the QuadTree in "+s+"sec");
        return result;
    }
    
    public HashSet<Item> getSelectedIn(Rect rect, HashSet<RoadType> types) {
        long t1 = System.nanoTime();
        HashSet<Item> result = new HashSet<>();
        super.getSelectedIn(rect, result, types);
        double s = (System.nanoTime()-t1)/1000000000.0;
        System.out.println("(Exclusive method) Returned "+result.size()+" roads from the QuadTree in "+s+"sec");
        return result;
    }
    
    public void fillFromRect(Rect rect, Collection<Item> col) {
        super.fillFrom(rect, col);
    }
}
