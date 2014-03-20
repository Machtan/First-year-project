package enums;

/**
 * The RoadType class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public enum RoadType {
     Highway(1), 
     PrimeRoute(3), 
     Path(8), 
     HighwayExit(31), 
     Other(0);
      
    
    
    private final int value;
    /**
     * Constructor for the RoadType class
     */
    RoadType (int value) {
        this.value = value;
    }
    
    
    public static RoadType fromValue(int value) {
        for (RoadType type : RoadType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return Other; //TODO create types for everything :)
        //throw new RuntimeException("Could not load a zone type from the value '"+value+"'");
    }
    
}
