/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import enums.RoadType;
import java.awt.Dimension;
import java.awt.Color;
import java.util.HashMap;
import javax.swing.JComponent;

/**
 *
 * @author Isabella
 */
public class Model {
    private Model model;
    private HashMap<Integer, Intersection> intersecMap;
    private HashMap<Color, Line[]> colorMap;
    private RoadPart[] roadPartArr;
    private Rect boundingArea; // The area the model encloses
    
    public Model(Intersection[] intersecArr, RoadPart[] roadPartArr) {
        this.roadPartArr = roadPartArr;
        intersecMap = new HashMap<>();
        
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        
        for (Intersection i : intersecArr) {
            intersecMap.put(i.id, i);
            
            if (i.x < minX) {
                minX = i.x;
            } 
            if (i.x > maxX) {
                maxX = i.x;
            }
            if (i.y < minY) { minY = i.y; }
            if (i.y > maxY) { maxY = i.y; }
        }
        
        boundingArea = new Rect(minX, minY, maxX-minX, maxY-minY);
        
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
        Line[] lineArr = new Line[roadPartArr.length];
        for(int i = 0; i<roadPartArr.length; i++) {
            lineArr[i] = new Line(
                    getScreenX(intersecMap.get(roadPartArr[i].sourceID).x, area, target), 
                    getScreenY(intersecMap.get(roadPartArr[i].sourceID).y, area, target),
                    getScreenX(intersecMap.get(roadPartArr[i].targetID).x, area, target),
                    getScreenY(intersecMap.get(roadPartArr[i].targetID).y, area, target),
                    instructions.getColor(RoadType.fromValue(roadPartArr[i].type%4)));

                   
        }
        return lineArr;
    }
}

