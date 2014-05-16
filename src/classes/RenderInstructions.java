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
    private RoadType[] renderedTypes;
    
    /**
     * Constructor for the RenderInstructions class
     */
    public RenderInstructions () {
        defaultColor = new Color(0,0,0);
        voidColor = new Color(200,200,255);
        colorMap = new HashMap<>();
        renderedTypes = RoadType.values();
    }
    
    private void recalcRendered() {
        FastArList<RoadType> types = new FastArList<>();
        for (RoadType type : RoadType.values()) {
            if (getColor(type) != voidColor) {
                types.add(type);
            }
        }
        renderedTypes = types.toArray(new RoadType[types.size()]);
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
     * Sets a new color for the given road types
     * @param color The color the road shall be drawn with
     * @param types The types of roads to use this color
     */
    public void addMapping(Color color, RoadType... types) { 
        for (RoadType type : types) {
            colorMap.put(type, color);
        }
        recalcRendered();
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
        recalcRendered();
    }
    
    /**
     * Returns the types of roads rendered with these instructions
     * @return the types of roads rendered with these instructions
     */
    public RoadType[] getRenderedTypes() {
        return renderedTypes;
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
