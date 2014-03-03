package classes;

import classes.Utils.Tokenizer;
import krak.NodeData;

/**
 * The Intersection class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
public class Intersection {
    
    public final int id;
    public final double x;
    public final double y;
         
    
    /**
     * Constructor for the Intersection class
     */
    public Intersection (NodeData data) {
        id = data.KDV_ID;
        x = data.X_COORD;
        y = data.Y_COORD;
    }
    
    public Intersection (String line) {
        Tokenizer.setLine(line);
        id = Tokenizer.getInt();
        x = Tokenizer.getDouble();
        y = Tokenizer.getDouble();
    }
    
    @Override
    public String toString() {
        return Utils.joinStrings(new Object[]{
            id,
            x,
            y
        }, 
        ",");
    }
}
