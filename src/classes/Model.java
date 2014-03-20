/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import enums.RoadType;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Isabella
 */
public class Model {
    private Model model;
    private HashMap<Integer, Intersection> intersecMap;
    //private RoadPart[] roadPartArr;
    private Rect boundingArea; // The area the model encloses
    private final QuadTree<RoadPart> tree;
    
    public static final RenderInstructions defaultInstructions = new RenderInstructions();
    /**
     * Initializes the static variables
     */
    static {
        // Create the default render instructions :
        defaultInstructions.addMapping(Color.red, RoadType.Highway);
        defaultInstructions.addMapping(Color.red, RoadType.HighwayExit);
        defaultInstructions.addMapping(new Color(51,51,255), RoadType.PrimeRoute);
        defaultInstructions.addMapping(new Color(0,255,25), RoadType.Path);
        defaultInstructions.addMapping(Color.black, RoadType.Other);
        
        System.out.println("Initialized the default render instructions!");
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
    
    public Model(Intersection[] intersecArr, RoadPart[] roadPartArr) {
        // Find the bounding area of the intersections
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        
        intersecMap = new HashMap<>();
        for (Intersection i : intersecArr) {
            intersecMap.put(i.id, i); // Add intersections to the map
            if (i.x < minX) {
                minX = i.x;
            } 
            if (i.x > maxX) {
                maxX = i.x;
            }
            if (i.y < minY) { minY = i.y; }
            if (i.y > maxY) { maxY = i.y; }
        }
        
        // Create the quad tree
        boundingArea = new Rect(minX, minY, maxX-minX, maxY-minY);
        tree = new QuadTree<>(boundingArea, 400, 15);
        
        // Fill the quad tree
        System.out.println("Populating the Quad Tree...");
        long t1 = System.nanoTime();
        for (RoadPart part : roadPartArr) {
            Rect rect = getRect(intersecMap.get(part.sourceID), intersecMap.get(part.targetID));
            part.setRect(rect);
            tree.add(part);
        }
        double secs = (System.nanoTime()-t1)/1000000000.0;
        System.out.println("Finished!");
        System.out.println("Populating the tree took "+secs+" seconds");
    }
    
    public int getScreenX(double x, Rect area, Rect target) {
        int screenX = (int)(target.x + (x-area.x) * (target.height / area.height));
        return screenX;
    }
        
    public int getScreenY(double y, Rect area, Rect target) {
        return getScreenY(y, area, target, target.height);
    }
    
    /**
     * Returns y from the slice of a target
     * @param y
     * @param area
     * @param target
     * @param windowheight
     * @return 
     */
    public int getScreenY(double y, Rect area, Rect target, double windowheight) {
        int sy = (int) (windowheight - (target.y + (y-area.y)*(target.height/area.height)));
        return sy;
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
     * @param area The area to find roads inside and constrain the rendering to
     * @param target The area of the view the coordinates should be mapped to
     * @param windowHeight The height of the target window
     * @param instructions The instructions for coloring/rendering of the lines
     * @return A list of lines converted to local coordinates for the view
     */
    public Line[] getLines(Rect area, Rect target, double windowHeight, RenderInstructions instructions) {
        HashSet<RoadPart> roads = tree.getIn(area); // TODO THIS IS THE WEAK LINK!!!
        Line[] lines = new Line[roads.size()];
        
        System.out.println("Returning "+lines.length+" lines from the area "+area);
        
        int i = 0;
        for(RoadPart part: roads) {
            lines[i++] = new Line(
                    getScreenX(intersecMap.get(part.sourceID).x, area, target), 
                    getScreenY(intersecMap.get(part.sourceID).y, area, target, windowHeight),
                    getScreenX(intersecMap.get(part.targetID).x, area, target),
                    getScreenY(intersecMap.get(part.targetID).y, area, target, windowHeight),
                    instructions.getColor(part.type)
            );
        }
        return lines;
    }
    
    public Line[] getLines(Rect area, Rect target, RenderInstructions instructions) {
        return getLines(area, target, target.height, instructions);
    }
}

