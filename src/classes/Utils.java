package classes;

import static classes.Loader.encoding;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import krak.DataLine;

/**
 * The Utils class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 14-Feb-2014
 */
public class Utils {
    
    /**
     * Returns the first rect clamped to the biggest rect it contains, with the
     * ratio of the second.
     * @param rect The rect to be clamped
     * @param ratioRect The rect with the ratio to use
     * @return The first rect clamped to the biggest rect it contains, with the
     * ratio of the second.
     */
    public static Rect clampRect(Rect rect, Rect ratioRect) {
        double ratio = ratioRect.width / ratioRect.height;
        float w = rect.width;
        float h = rect.height;
        if (w < (h*ratio)) { // Height is larger
            h = (float)(w / ratio);
        } else {
            w = (float)(h * ratio);
        }
        return new Rect(rect.x, rect.y, w, h);
    }
    
    /**
     * Returns the first rect clamped to the biggest rect it contains, with the
     * ratio of the second.
     * @param dim The dimension to be clamped
     * @param ratioDim The dimension with the ratio to use
     * @return The first rect clamped to the biggest rect it contains, with the
     * ratio of the second.
     */
    public static Dimension clampDimension(Dimension dim, Dimension ratioDim) {
        double ratio = (double)ratioDim.width / ratioDim.height;
        int w = dim.width;
        int h = dim.height;
        if (w < (h*ratio)) {
            h = (int)Math.round(w / ratio);
        } else {
            w = (int)Math.round(h * ratio);
        }
        return new Dimension(w, h);
    }
    
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
     * An exception for when the files cannot be loaded
     */
    public static class LoadFileException extends Exception {
        public final String path;
        public LoadFileException(String msg, String path) {
            super(msg);
            this.path = path;
        }
    }
    
    /**
     * Returns a file stream for the given relative path (in the project source)
     * @param path The path to the eg. 'resources/roads.txt'
     * @return A file stream of the file.
     * @throws classes.Utils.LoadFileException
     */
    public static InputStream getFileStream(String path) throws LoadFileException {
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new LoadFileException("Unknown file path: '"+path+"'", path);
        }
        return in;
    }
    
    /**
     * Attempts to return the current working directory
     * @return 
     */
    public static String getcwd() {
        return Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
    
    /**
     * Returns a path to the source folder of the project
     * @return 
     */
    public static Path getSourceDir() {
        return Paths.get(Paths.get(getcwd()).getParent().getParent()+"","src");
    }
    
    /**
     * A simple tokenizer for naïvely tokenizing comma-separated values
     */
    public static class Tokenizer {
        private static DataLine line;
        public static void setLine(String text) {
            DataLine.resetInterner();
            line = new DataLine(text);
        }
        public static String getString() { return line.getString(); }
        public static int getInt() { return line.getInt(); }
        public static double getDouble() { return line.getDouble(); }
        public static long getLong() { return line.getLong(); }
        public static float getFloat() { return line.getFloat(); }
        public static char getChar() { return line.getChar(); }
        public static short getShort() { return line.getShort(); }
    }
    
     /**
     * A method to easily save stuff to a file :)
     * The file will be created in the given folder of the project
     * @param <T> An object implementing CharSequence (like Strings)
     * @param data The list of CharSequence-implementing objects
     * @param filepath The path to the file eg: resources/roads.txt
     */
    public static <T extends CharSequence> void save(T[] data, String filepath) {
        Path srcdir = Paths.get(Paths.get(getcwd()).getParent().getParent()+"","src");
        String path = Paths.get(srcdir.toString(), filepath).toString();
        File file = new File(path);
        try { file.createNewFile(); }// Ensure that the path exists
        catch (IOException ex) {
            throw new RuntimeException("Error at file.createNewFile()");
        }
        try {
            Files.write(file.toPath(), Arrays.asList(data), Charset.forName(encoding));
        } catch (IOException ex) {
            throw new RuntimeException("Error while saving data to '"+path+"'");
        }
    }
}
