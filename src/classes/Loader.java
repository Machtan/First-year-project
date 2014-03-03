package classes;

/**
 * The Loader class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
import classes.Utils.Tokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.charset.Charset;
import java.nio.file.Files;
import krak.EdgeData;
import krak.KrakLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import krak.DataLine;

/*
// This might be a better idea for the road numbering stuff :i
public class Range<T extends Comparable> {
    private T start;
    private T end;
    
    public Range(T start, T end) {
        this.start = start;
        this.end = end;
    }
}*/
public class Loader {
        
    protected static final String encoding = "iso8859-1";
    
    /**
     * Loads roads from a krak file
     * @param krakFilePath
     * @return 
     */
    public static RoadPart[] loadKrakRoads(String krakFilePath) {
        System.out.println("Loading roads from Krak data...");
        EdgeData[] edges = KrakLoader.loadEdges(krakFilePath);
        System.out.println("Converting to the internal data types...");
        RoadPart[] roads = new RoadPart[edges.length];
        for (int i = 0; i < edges.length; i++) {
            roads[i] = new RoadPart(edges[i]);
        }
        System.out.println("Finished converting!");
        System.out.println("Roads loaded! ("+roads.length+" nodes)");
        return roads;
    }
    
    public static RoadPart[] loadRoads(String roadFilePath) {
        System.out.println("Loading road data...");
        long t1 = System.nanoTime();
        ArrayList<RoadPart> list = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(Utils.getFile(roadFilePath)), 
                Charset.forName(encoding)));
            br.readLine(); // Again, first line is column names, not data.
        
            String line;
            while ((line = br.readLine()) != null) {
                list.add(new RoadPart(line));
            }
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load road data from '"+roadFilePath+"'");
        }
        DataLine.resetInterner();
        System.gc();
        System.out.println("Road data loaded!");
        double elapsed = (System.nanoTime() - t1)/(1000000000.0);
        System.out.printf("Loaded the roads in %.3f seconds\n", elapsed);
        return list.toArray(new RoadPart[0]);
    }

    /**
     * Saves the roads in the new format
     * @param roads The roads to save
     * @param path The path to save them to (the directory should exist)
     */
    public static void saveRoads(RoadPart[] roads, String path) {
        System.out.println("Saving road data...");
        Utils.save(roads, path);
        System.out.println("Road data saved! ("+roads.length+" parts)");
    }
    
    /**
     * Saves a list of intersections to a file in the new format
     * @param intersections The intersections
     * @param path The 
     */
    public static void saveIntersections(Intersection[] intersections, String path) {
        System.out.println("Saving intersection data...");
        Utils.save(intersections, path);
        System.out.println("Intersection data saved! ("+intersections.length+" locations)");
    }
    

    
    public static void main(String[] args) {
        System.out.println("Testing...");

        //RoadPart[] roads = loadKrakRoads("krak/kdv_unload.txt"); // 234MB heap ~2x size
        //saveRoads(roads, p);       
        RoadPart[] roads = loadRoads("resources/roads.txt"); // ~100MB heap ~2x size
        
        System.out.println("Sampling roads:");
        for (int i=0; i<50; i++) {
            System.out.println(i+1+": '"+roads[i]+"'");
        }
        
        MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
        System.out.printf("Heap memory usage: %d MB%n",
                mxbean.getHeapMemoryUsage().getUsed() / (1000000));
    }
}
