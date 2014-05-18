/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import enums.RoadType;
import interfaces.IProgressBar;
import interfaces.StreamedContainer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Model is the model handling the tree. It used to handle the retrieval of lines.
 * @author Jakob
 */

public class Model implements StreamedContainer<Road> {
    public final Rect bounds;
    private HashMap<RoadType, QuadTree> trees = new HashMap<>();
    public ArrayList<RoadType> priorities;
    public long roadCount = 0;
    
    /* Krak boundaries
    x = [442254.35659 : 892658.21706]
    y = [6049914.43018 : 6402050.98297]
    */
    public Model(Rect boundingBox) {
        bounds = boundingBox;
        priorities = new ArrayList<>();
        priorities.add(RoadType.Other);
        priorities.add(RoadType.Path);
        priorities.add(RoadType.Ferry);
        priorities.add(RoadType.PrimeRoute);
        priorities.add(RoadType.HighwayExit);
        priorities.add(RoadType.Highway);
    }

    /**
     * Streams the road of the given projection to a target
     * @param target Where to stream the roads
     * @param p The projection to use as the source
     */
    public void getRoads(StreamedContainer<Road> target, Viewport.Projection p) {
        if (p.equals(Viewport.Projection.Empty)) { 
            System.out.println("Model received an empty projection, passing...");
            target.startStream();
            target.endStream();
        } else {
            //System.out.println("[Model] Requesting tree data from "+p.source+"...");
            target.startStream();
            for (RoadType type : priorities) {
                trees.get(type).getIn(p.source, target);
            }
            target.endStream();
        }
    }

    /**
     * Streams all roads in the model to the target
     * @param target The target to stream roads to
     */
    public void getAllRoads(StreamedContainer<Road> target) {
        target.startStream();
        for (RoadType type : priorities) {
            trees.get(type).getIn(bounds, target);
        }
        target.endStream();
    }

    private IProgressBar progbar = null;
    @Override
    public void startStream() {
        // Find the bounding area of the intersections
        System.out.println("Populating the Quad Tree...");
        for (RoadType type : RoadType.values()) {
            trees.put(type, new QuadTree(bounds, (short)400, (short)30));
        }
    }

    @Override
    public void endStream() {
        System.out.println("Finished populating the Quad Tree!");
        progbar = null;
    }

    @Override
    public void startStream(IProgressBar bar) {
        progbar = bar;
        startStream();
    }

    @Override
    public void add(Road obj) {
        roadCount += 1;
        trees.get(obj.type).add(obj);
        if (progbar != null) {
            progbar.update(1);
        }
    }
}
