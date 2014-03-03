package classes;

/**
 * The ZoneType class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
public enum ZoneType {
    TEMP(0);
    
    public final int value;
    ZoneType(int value) {
        this.value = value;
    }
    
    public static ZoneType fromValue(int value) {
        for (ZoneType type : ZoneType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new RuntimeException("Could not load a zone type from the value '"+value+"'");
    }
}
