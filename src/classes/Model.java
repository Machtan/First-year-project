/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import enums.RoadType;
import interfaces.IProgressBar;
import interfaces.StreamedContainer;
import java.awt.Color;

/**
 * Model is the model handling the tree. It used to handle the retrieval of lines.
 * @author Jakob
 */
public class Model implements StreamedContainer<Road> {
    private QuadTree tree;
    public final Rect bounds;
    
    /* Krak boundaries
    x = [442254.35659 : 892658.21706]
    y = [6049914.43018 : 6402050.98297]
    */
    public Model(Rect boundingBox) {
        bounds = boundingBox;
    }
    
    /**
     * Streams the road of the given projection to a target
     * @param target Where to stream the roads
     * @param p The projection to use as the source
     */
    public void getRoads(StreamedContainer<Road> target, Viewport.Projection p) {
        if (p.equals(Viewport.Projection.Empty)) { 
            target.startStream();
            target.endStream();
        } else {
            tree.getIn(p.source, target);
        }
    }
    
    /**
     * Streams all roads in the model to the target
     * @param target The target to stream roads to
     */
    public void getAllRoads(StreamedContainer<Road> target) {
        tree.getIn(bounds, target);
    }

    @Override
    public void startStream() {
        // Find the bounding area of the intersections
        System.out.println("Populating the Quad Tree...");
        tree = new QuadTree(bounds, (short)400, (short)30);
    }

    @Override
    public void endStream() {
        System.out.println("Finished populating the Quad Tree!");
    }

    @Override
    public void startStream(IProgressBar bar) {
        throw new UnsupportedOperationException("Progress bar unsupported");
    }

    @Override
    public void add(Road obj) {
        tree.add(obj);
    }
    
}

