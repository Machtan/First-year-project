/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import interfaces.QuadNode;

/**
 *
 * @author Alekxander
 * @param <Item>
 */
public class QuadTree<Item extends QuadNode> extends Quad {
    public QuadTree(Rect area, int maxNodes, int maxDepth) {
        super(area, maxNodes, maxDepth, 1);
    }
}
