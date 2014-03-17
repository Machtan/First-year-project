/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import enums.RoadType;
import java.awt.Dimension;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JComponent;

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
        defaultInstructions.addMapping(Color.red, RoadType.Motorvej);
        defaultInstructions.addMapping(Color.red, RoadType.Motorvejsafkorsel);
        defaultInstructions.addMapping(new Color(51,51,255), RoadType.PrimaerRute);
        defaultInstructions.addMapping(new Color(0,255,25), RoadType.Sti);
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
        for (RoadPart part : roadPartArr) {
            Rect rect = getRect(intersecMap.get(part.sourceID), intersecMap.get(part.targetID));
            part.setRect(rect);
            tree.add(part);
        }
    }
    
    public int getScreenX(double x, Rect area, Rect target) {
        int screenX = (int)(target.x + (x-area.x) * (target.height / area.height));
        return screenX;
    }
        
    public int getScreenY(double y, Rect area, Rect target) {
        return (int)(target.height - (target.y + (y-area.y) * (target.height / area.height)));
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
     * @param instructions The instructions for coloring/rendering of the lines
     * @return A list of lines converted to local coordinates for the view
     */
    public Line[] getLines(Rect area, Rect target, RenderInstructions instructions) {
        HashSet<RoadPart> roads = tree.getIn(area);
        Line[] lines = new Line[roads.size()];
        
        System.out.println("Returning "+lines.length+" lines from the area "+area);
        
        int i = 0;
        for(RoadPart part: roads) {
            lines[i++] = new Line(
                    getScreenX(intersecMap.get(part.sourceID).x, area, target), 
                    getScreenY(intersecMap.get(part.sourceID).y, area, target),
                    getScreenX(intersecMap.get(part.targetID).x, area, target),
                    getScreenY(intersecMap.get(part.targetID).y, area, target),
                    instructions.getColor(part.type)
            );
        }
        return lines;
    }
}

