package classes;

/**
 * The Datafile class holds information for the loaders about a given file, 
 * such as its length and filename (for showing loading progress)
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 28-Apr-2014
 */
public class Datafile {
    public final String filename;
    public final String progressDescription;
    public final long lines;
    public final Rect bounds;
    /**
     * Constructor for the Datafile class
     */
    public Datafile(String filename, long lines, String progressDescription, Rect bounds) {
        this.filename = filename;
        this.lines = lines;
        this.progressDescription = progressDescription;
        this.bounds = bounds;
    } 
    
    public String toString() {
        return filename+"("+lines+"): '"+progressDescription+"'["+bounds+"]";
    }
}
