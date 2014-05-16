package classes;

import interfaces.StreamedContainer;

/**
 * QuadTree is a data type that sorts input nodes by their area, and easily
 * retrieves nodes from a specific area.
 * @author Alekxander
 * @author Jakob
 */
public class QuadTree extends Quad {
    public QuadTree(Rect area, short maxNodes, short maxDepth) {
        super(area, maxNodes, maxDepth, (short)1);
    }
    
    /**
     * Fills the target streamed container with roads inside the given area
     * @param area The area to look in
     * @param target The target to notify
     */
    public void getIn(Rect area, StreamedContainer target) {
        long t1 = System.nanoTime();
        target.startStream();
        super.getIn(area, target);
        double s = (System.nanoTime()-t1)/1e9;
        //System.out.println("'getIn' returned "+result.size()+" roads from the QuadTree in "+s+"sec");
        target.endStream();
    }
}
