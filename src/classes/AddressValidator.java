package classes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 * The AddressValidator class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 06-Feb-2014
 */
public class AddressValidator {

    // Change file-related stuff HERE
    protected static final String filepath = "resources/road_names.txt";
    protected static final String encoding = "iso8859-1";
    
    protected static HashSet<String> roadnames;
    protected static boolean roadsLoaded = false;
    
    /**
     * Constructor for the AddressValidator class
     * @param filepath The path to the file to load road names from
     * @param encoding The encoding of the passed file, if any
     * @throws java.io.FileNotFoundException
     */
    private static void loadRoads() throws FileNotFoundException {
        if (roadsLoaded) {return;}
        
        URL url = AddressValidator.class.getClassLoader().getResource(filepath);
        try {
            roadnames = new HashSet<>(Files.readAllLines(Paths.get(url.toURI()), 
                    Charset.forName(encoding)));
        } catch (URISyntaxException | IOException ex) {
            throw new FileNotFoundException("Cannot read path: "+filepath);
        } 

        System.out.printf("Initalizing validator with %s road names...\n", roadnames.size());
        roadsLoaded = true;
    }
    
    // This method signature will not change
    /**
     * Validates the given road name by comparing to a list from a file
     * @param roadname
     * @return 
     */
    public static boolean validate(String roadname) {
        if (!roadsLoaded) {
            try {
                loadRoads();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return false;
            }
        }
        return roadnames.contains(roadname);
    }
    
    /**
     * Returns an array of the road names
     * Useful for testing with the roads as input
     * @return an array of the road names
     */
    public static String[] getArray() {
        if (!roadsLoaded) {
            try {
                loadRoads();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return new String[0];
            }
        }
        return roadnames.toArray(new String[0]);
    }
    
}
