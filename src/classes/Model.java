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

/**
 *
 * @author Isabella
 */
public class Model {
    private Model model;
    private HashMap<Long, Intersection> inMap;
    //private RoadPart[] roadPartArr;
    private Rect boundingArea; // The area the model encloses
    private final QuadTree tree;
    private static int quadCounter; //Used for loading
    
    public static final RenderInstructions defaultInstructions = new RenderInstructions();
    /**
     * Initializes the static variables
     */
    static {
        // Create the default render instructions :
        defaultInstructions.addMapping(Color.red, RoadType.Highway);
        defaultInstructions.addMapping(Color.red, RoadType.HighwayExit);
        defaultInstructions.addMapping(new Color(255,170,100), RoadType.PrimeRoute);
        defaultInstructions.addMapping(new Color(0,255,25,200), RoadType.Path);
        defaultInstructions.addMapping(Color.blue, RoadType.Ferry);
        defaultInstructions.addMapping(new Color(200,200,255), RoadType.Other);
    }
    
    /**
     * Returns the smallest rectangle bounding two intersections
     * @param one The first intersection
     * @param two The second intersection
     * @return 
     */
    private Rect getRect(Intersection one, Intersection two) {
        double x = Math.min(one.x, two.x);
        double y = Math.min(one.y, two.y);
        double width = Math.abs(one.x - two.x);
        double height = Math.abs(one.y - two.y);
        return new Rect(x,y,width,height);
    }
    
    /**
     * This should only be used for testing. Returns the model's QuadTree instance
     * @return the model's QuadTree instance
     */
    public QuadTree getTree() {
        return tree;
    }
    
    public int intersectionCount() {
        return inMap.size();
    }
    
    public Model(Intersection[] inters, RoadPart[] roads, IProgressBar... bar) {
        IProgressBar progbar = null; // Optional progress bar
        if (bar.length != 0) { 
            progbar = bar[0]; 
            progbar.setTarget("Populating Quad Tree...", 812301);
        }
        // Find the bounding area of the intersections
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        
        inMap = new HashMap<>();
        for (Intersection i : inters) {
            inMap.put(i.id, i); // Add intersections to the map
            minX = Math.min(i.x, minX);
            maxX = Math.max(i.x, maxX);
            minY = Math.min(i.y, minY);
            maxY = Math.max(i.y, maxY);
        }
        
        // Create the quad tree
        boundingArea = new Rect(minX, minY, maxX-minX, maxY-minY);
        tree = new QuadTree(boundingArea, 400, 30);
        
        // Fill the quad tree
        System.out.println("Populating the Quad Tree...");
        long t1 = System.nanoTime();
        if (progbar != null) {
            for (RoadPart part : roads) {
                part.setPoints(inMap.get(part.sourceID), inMap.get(part.targetID));
                tree.add(part);
                progbar.update(1);
            }
        } else {
            for (RoadPart part : roads) {
                part.setPoints(inMap.get(part.sourceID), inMap.get(part.targetID));
                tree.add(part);
            }
        }
        tree.freeze(); // Freeze that tree ;)
        
        double secs = (System.nanoTime()-t1)/1000000000.0;
        System.out.println("Finished!");
        System.out.println("Populating the tree took "+secs+" seconds");
    }
    
    /**
     * Returns the rectangle enclosing all objects contained in the model
     * @return A rectangle enclosing all intersections
     */
    public Rect getBoundingArea() {
        return boundingArea;
    }
    
    /**
     * Returns the lines of the model from the given area, prepared for the 
     * size of the given viewer component
     * @param p the projection to get the lines of
     * @param windowHeight The height of the target window
     * @param instructions The instructions for coloring/rendering of the lines
     * @param prioritized A list of roads to be prioritized, from highest to lowest
     * @return A list of lines converted to local coordinates for the view
     */
    public Line[] getLines(Viewport.Projection p, double windowHeight, 
            RenderInstructions instructions, ArrayList<RoadType> prioritized) {
       long t1 = System.nanoTime();

        RoadType[] types = instructions.getRenderedTypes();
        RoadPart[] roadArr = tree.getSelectedIn(p.source, types);
        if (roadArr.length == 0) {
            return new Line[0];
        }
        Line[] lineArr = new Line[roadArr.length];

        // Prepare the prioritized lists
        HashMap<RoadType, ArrayList<Line>> prioLines = new HashMap<>();
        for (RoadType type : prioritized) {
            prioLines.put(type, new ArrayList<Line>());
        }
        
        System.out.println("Constructing lines");
        long lineT1 = System.nanoTime();
        
        double ppu = p.target.height / p.source.height;
        double heightFac = windowHeight - p.target.y;
        double x1 = p.source.x;
        double y1 = p.source.y;
        int i = 0;
        for(RoadPart road: roadArr) {
            if (instructions.getColor(road.type) == instructions.getVoidColor()) {
                continue; // Ignore undrawn roads
            }
            
            if (prioritized.contains(road.type)) {
                prioLines.get(road.type).add(road.asLine(x1, y1, p.target, ppu, heightFac, instructions));
            } else {
                lineArr[i++] = road.asLine(x1, y1, p.target, ppu, heightFac, instructions);
            }
        }
        double lineDelta = (System.nanoTime()-lineT1)/1e9;
        System.out.println("The line construction loop took "+lineDelta+"s");

        // Add the prioritized lines in order
        int insert = i;
        for (int j = prioLines.size()-1; j >= 0; j--) {
            ArrayList<Line> pLines = prioLines.get(prioritized.get(j));
            for (int k = 0; k < pLines.size(); k++) {
                lineArr[insert++] = pLines.get(k);
            }
        }

        double deltaTime = (System.nanoTime()-t1)/1e9;
        System.out.println("Returned "+lineArr.length+" drawlines in "+deltaTime+" secs.");
        return lineArr;
    }
    
    // Without <target height> and <priorities>
    public Line[] getLines(Viewport.Projection p, RenderInstructions instructions) {
        return getLines(p, p.target.height, instructions, new ArrayList<RoadType>());
    }
    
    // Without <target height>
    public Line[] getLines(Viewport.Projection p, RenderInstructions instructions, 
            ArrayList<RoadType> priorities) {
        return getLines(p, p.target.height, instructions, priorities);
    }
    
    // Without <priorities>
    public Line[] getLines(Viewport.Projection p, int windowHeight, 
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
     * @param area The area to look in
     * @param ins Instructions for the current way of rendering
     * @return Road parts in the area
     */
    public RoadPart[] getRoads(Rect area, RenderInstructions ins) {
        return tree.getSelectedIn(area, ins.getRenderedTypes());
    }
    
}

