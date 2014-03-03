package classes;

/**
 * The Loader class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.logging.Level;
import java.util.logging.Logger;
import krak.EdgeData;
import krak.KrakLoader;
import krak.NodeData;
import java.nio.file.Path;

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
    
    // This looks too much like the model. Scrap and make the model instead :)
    public static class RoadStructure implements Structure {
        private final int limit = 100;
        private int count;
        
        
        
        public RoadStructure(Road[] roads, Intersection[] intersections) {
            count = 0;
        }

        @Override
        public void addIntersection(Intersection intersection) {
            throw new UnsupportedOperationException("classes.Loader.RoadStructure.addIntersection is not supported yet.");
        }

        @Override
        public void addRoad(Road road) {
            System.out.println("Road: "+road.toString());
            if (count++ == limit) {
                // Throw an exception to stop the program...
                throw new RuntimeException("Ending...");
            }
        }
        
        
    }
    
    /**
     * Loads roads from a krak file
     * @param krakFilePath
     * @return 
     */
    public static Road[] loadKrakRoads(String krakFilePath) {
        EdgeData[] edges = KrakLoader.loadEdges(krakFilePath);
        Road[] roads = new Road[edges.length];
        for (int i = 0; i < edges.length; i++) {
            roads[i] = new Road(edges[i]);
        }
        return roads;
    }

    /**
     * Saves the roads in the new format
     * @param roads The roads to save
     * @param path The path to save them to (the directory should exist)
     */
    public static void saveRoads(Road[] roads, String path) {
        System.out.println("Saving road data...");
        File file = new File(path);
        try { file.createNewFile(); }// Ensure that the path exists
        catch (IOException ex) {
            throw new RuntimeException("Error at file.createNewFile()");
        }
        try {   
            FileWriter writer = new FileWriter(file);
            for (Road road : roads) {
                writer.write(road+"\n");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error at the fileWriter");
        }
        System.out.println("Road data saved!");
    }
    
    public static void main(String[] args) {
        Path path = FileSystems.getDefault().getPath("resources","roads.txt");
        Path srcdir = Utils.getcwd().getParent();
        String p = srcdir.toString()+"roads.txt";
        System.out.println("p: "+p);
        System.out.println("Path: "+path);
        System.out.println("Testing...");
        System.out.println("cwd: "+Utils.getcwd());
        //Road[] roads = loadKrakRoads("krak/kdv_unload.txt");
        Road[] roads = new Road[0];
        saveRoads(roads, "resources/roads.txt"); // Not working yet!
        
    }
}
