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
}
