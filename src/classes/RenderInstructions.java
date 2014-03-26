package classes;

import enums.RoadType;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The RenderInstructions class is used by the model to filter roads, and to
 * determine how they should be marked up, before being passed to the model
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class RenderInstructions {
    
    private final Color voidColor;
    private Color defaultColor;
    private HashMap<RoadType, Color> colorMap;
    
    /**
     * Constructor for the RenderInstructions class
     */
    public RenderInstructions () {
        defaultColor = new Color(0,0,0);
        voidColor = new Color(200,200,255);
        colorMap = new HashMap<>();
    }
    
    public void setColor(RoadType type, Color color) {
        colorMap.remove(type);
        colorMap.put(type,color);
    }
    
    public Color getVoidColor() {
        return voidColor;
    }
    
    /**
     * Returns the color mapped to 
     * @param type
     * @return 
     */
    public Color getColor(RoadType type) {
        if (colorMap.containsKey(type)) {
            return colorMap.get(type);
        } else {
            return defaultColor;
        }
    }
    
  
    
    /**
     * Sets a new color for the given type of road
     * @param color The color the road shall be drawn with
     * @param type The type of road
     */
    public void addMapping(Color color, RoadType type) { 
        colorMap.put(type, color);
    }
    
    /**
     * Returns the default color of the map
     * @return 
     */
    public Color getDefaultColor() {
        return defaultColor;
    }
    
    /**
     * 
     * @param color 
     */
    public void setDefaultColor(Color color) {
        defaultColor = color;
    }
    
    /**
     * Returns the types of roads rendered with these instructions
     * @return the types of roads rendered with these instructions
     */
    public HashSet<RoadType> getRenderedTypes() {
        HashSet<RoadType> types = new HashSet<>();
        System.out.println("Rendered:");
        for (RoadType type : colorMap.keySet()) {
            if (colorMap.get(type) != voidColor) {
                types.add(type);
                System.out.println("- "+type);
            }
        }
        return types;
    }
    
    /**
     * Creates a render instruction excluding everything but the given road type 
     * @return a render instruction excluding everything but the given road type 
     */
    public RenderInstructions getExclusive() {
        RenderInstructions ins = new RenderInstructions();
        ins.setDefaultColor(ins.voidColor);
        for (RoadType type : colorMap.keySet()) {
            if (!(colorMap.get(type)==voidColor)) {
                ins.addMapping(colorMap.get(type), type);
            }
        }
        return ins;
    }
}
