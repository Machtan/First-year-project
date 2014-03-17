/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author Alekxander
 */
public class QuadTree extends Quad {
    public QuadTree(Rect area, int maxNodes, int maxDepth) {
        super(area, maxNodes, maxDepth, 1);
    }
}
