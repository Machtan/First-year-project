package classes;

import java.io.File;
import java.net.URL;

/**
 * The Utils class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 14-Feb-2014
 */
public class Utils {
    /**
     * Joins an array of strings with a given separator
     * @param strings The strings
     * @param separator The separator
     * @return A joined string
     */
    public static String joinStrings(String[] strings, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
                String sep = ( i < (strings.length-1))? separator: "";
                sb.append(strings[i]).append(sep);
            }
        return sb.toString();
    }
    
    public static String joinStrings(Object[] objs, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objs.length; i++) {
                String sep = ( i < (objs.length-1))? separator: "";
                sb.append(objs[i]).append(sep);
            }
        return sb.toString();
    }
    
    /**
     * Uses the class path to load a file. Kinda necessary in NetBeans.
     * @param path The path to the file to load eg. resources/image.png if a 
     * file called 'image.png' is in the source folder called 'resources'.
     * This function throws a RuntimeException if the file cannot be read.
     * @return A file object to the file on the path
     */
    public static File getFile(String path) throws RuntimeException {
        try {
            ClassLoader cl = Utils.class.getClassLoader();
            URL url = cl.getResource(path);
            if (url != null) {
                return new File(url.toURI());
            } else {
                throw new Exception("See below"); 
            }
        } catch (Exception ex) {
            throw new RuntimeException("Bad file path: " + path);
        }
    }
    
    /**
     * Attempts to return the current working directory
     * @return 
     */
    public static String getcwd() {
        return Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
}
