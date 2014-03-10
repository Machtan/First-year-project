package classes;

import enums.RoadType;
import java.awt.Color;
import java.util.HashMap;

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
        voidColor = new Color(10,10,10);
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
    
    
}
