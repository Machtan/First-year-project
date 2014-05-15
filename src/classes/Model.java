/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import enums.RoadType;
import interfaces.IProgressBar;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Isabella
 */
public class Model {

    private Model model;
    private Rect boundingArea; // The area the model encloses
    private final QuadTree tree;
    public final int intersections;
    private static int quadCounter; //Used for loading
    //public final String[] allRoads;

    public static final RenderInstructions defaultInstructions = new RenderInstructions();

    /**
     * Initializes the static variables
     */
    static {
        // Create the default render instructions :
        defaultInstructions.addMapping(Color.red, RoadType.Highway);
        defaultInstructions.addMapping(Color.red, RoadType.HighwayExit);
        defaultInstructions.addMapping(new Color(255, 170, 100), RoadType.PrimeRoute);
        defaultInstructions.addMapping(new Color(0, 255, 25, 200), RoadType.Path);
        defaultInstructions.addMapping(Color.blue, RoadType.Ferry);
        defaultInstructions.addMapping(new Color(200, 200, 255), RoadType.Other);
    }

    /**
     * Returns the smallest rectangle bounding two intersections
     * @param one The first intersection
     * @param two The second intersection
     * @return
     */
    private Rect getRect(Intersection one, Intersection two) {
        float x = (float) Math.min(one.x, two.x);
        float y = (float) Math.min(one.y, two.y);
        float width = (float) Math.abs(one.x - two.x);
        float height = (float) Math.abs(one.y - two.y);
        return new Rect(x, y, width, height);
    }

    /**
     * This should only be used for testing. Returns the model's QuadTree
     * instance
     *
     * @return the model's QuadTree instance
     */
    public QuadTree getTree() {
        return tree;
    }

    public Model(Intersection[] inters, RoadPart[] roads, IProgressBar... bar) {
        IProgressBar progbar = null; // Optional progress bar
        if (bar.length != 0) {
            progbar = bar[0];
            progbar.setTarget("Populating Quad Tree...", 812301);
        }
        // Find the bounding area of the intersections
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        HashMap<Long, Intersection> inMap = new HashMap<>();
        for (Intersection i : inters) {
            inMap.put(i.id, i); // Add intersections to the map
            minX = (float) Math.min(i.x, minX);
            maxX = (float) Math.max(i.x, maxX);
            minY = (float) Math.min(i.y, minY);
            maxY = (float) Math.max(i.y, maxY);
        }

        // Create the quad tree
        boundingArea = new Rect(minX, minY, maxX - minX, maxY - minY);
        tree = new QuadTree(boundingArea, (short) 400, (short) 30);

        // Fill the quad tree
        System.out.println("Populating the Quad Tree...");
        long t1 = System.nanoTime();
        if (progbar != null) {
            int i = 0;
            HashSet<String>tempSet = new HashSet<>();

            for (RoadPart part : roads) {
                part.setPoints(inMap.get(part.sourceID), inMap.get(part.targetID));
                tree.add(part);
                tempSet.add(part.name);
                progbar.update(1);
            }
            //allRoads
            //allRoads = (String[])tempSet.toArray();
        } else {
            for (RoadPart part : roads) {
                part.setPoints(inMap.get(part.sourceID), inMap.get(part.targetID));
                tree.add(part);
            }
        }
        tree.freeze(); // Freeze that tree ;)

        double secs = (System.nanoTime() - t1) / 1000000000.0;
        System.out.println("Finished!");
        System.out.println("Populating the tree took " + secs + " seconds");
        intersections = inMap.size();
        inMap = null;
    }

    /**
     * Returns the rectangle enclosing all objects contained in the model
     *
     * @return A rectangle enclosing all intersections
     */
    public Rect getBoundingArea() {
        return boundingArea;
    }

    /**
     * Returns the lines of the model from the given area, prepared for the size
     * of the given viewer component
     * @param p the projection to get the lines of
     * @param windowHeight The height of the target window
     * @param instructions The instructions for coloring/rendering of the lines
     * @param prioritized A list of roads to be prioritized, from highest to
     * lowest
     * @return A list of lines converted to local coordinates for the view
     */
    public ArrayList<Line> getLines(Viewport.Projection p, float windowHeight,
            RenderInstructions instructions, ArrayList<RoadType> prioritized) {
        if (p.equals(Viewport.Projection.Empty)) {
            return new ArrayList<>(); // Don't waste time on the empty projection ;)
        }

        RoadType[] types = instructions.getRenderedTypes();
        RoadPart[] roadArr = tree.getIn(p.source); //tree.getSelectedIn(p.source, types); // TODO fix
        if (roadArr.length == 0) {
            return new ArrayList<>();
        }
        ArrayList<Line> lines = new ArrayList<>();

        // Prepare the prioritized lists
        HashMap<RoadType, ArrayList<Line>> prioLines = new HashMap<>();
        for (RoadType type : prioritized) {
            prioLines.put(type, new ArrayList<Line>());
        }
        // Make a HashSet for faster containment checks
        HashSet<RoadType> prio = new HashSet<>(prioritized);

        float ppu = p.target.height / p.source.height;
        float heightFac = windowHeight - p.target.y;
        float x1 = p.source.x;
        float y1 = p.source.y;
        int i = 0;
        for (RoadPart road : roadArr) {
            if (instructions.getColor(road.type) == instructions.getVoidColor()) {
                continue; // Ignore undrawn roads
            }

            if (prio.contains(road.type)) {
                prioLines.get(road.type).add(road.asLine(x1, y1, p.target, ppu, heightFac, instructions));
            } else {
                lines.add(road.asLine(x1, y1, p.target, ppu, heightFac, instructions));
            }
        }

        // Add the prioritized lines in order
        int insert = i;
        for (int j = prioLines.size() - 1; j >= 0; j--) {
            ArrayList<Line> pLines = prioLines.get(prioritized.get(j));
            for (int k = 0; k < pLines.size(); k++) {
                lines.add(pLines.get(k));
            }
        }
        return lines;
    }

    // Without <priorities>
    public ArrayList<Line> getLines(Viewport.Projection p, int windowHeight,
            RenderInstructions instructions) {
        return getLines(p, windowHeight, instructions, new ArrayList<RoadType>());
    }

    /**
     * Returns the number of quads in the quad-tree of the model
     * @return the number of quads in the quad-tree of the model
     */
    public static int getQuadCnt() {
        return quadCounter;
    }

    /**
     * Returns the road parts in the given area of the map
     * @param area The area to look in
     * @return Road parts in the area
     */
    public RoadPart[] getRoads(Rect area) {
        return tree.getIn(area);
    }

    /**
     * Returns the road parts in the given area of the map
     *
     * @param area The area to look in
     * @param ins Instructions for the current way of rendering
     * @return Road parts in the area
     */
    public RoadPart[] getRoads(Rect area, RenderInstructions ins) {
        return tree.getIn(area);//tree.getSelectedIn(area, ins.getRenderedTypes()); //TODO fix
    }

}
