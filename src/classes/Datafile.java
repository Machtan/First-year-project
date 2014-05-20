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
    public final String charset;
    /**
     * Constructor for the Datafile class
     * @param filename The name of the file to load
     * @param lines How many elements will be loaded from the file
     * @param progressDescription What the progress bar should say while loading
     * @param bounds The 'show all' bounds of the data set
     * @param charset Which character set to load it from
     */
    public Datafile(String filename, long lines, String progressDescription, Rect bounds, String charset) {
        this.filename = filename;
        this.lines = lines;
        this.progressDescription = progressDescription;
        this.bounds = bounds;
        this.charset = charset;
    } 
    
    public String toString() {
        return filename+"("+lines+"): '"+progressDescription+"'["+bounds+"]";
    }
}
