package classes;

/**
 * The Loader class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
import classes.Utils.Tokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import krak.EdgeData;
import krak.KrakLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        
    /**
     * Loads roads from a krak file
     * @param krakFilePath
     * @return 
     */
    public static RoadPart[] loadKrakRoads(String krakFilePath) {
        System.out.println("Loading roads from Krak data...");
        EdgeData[] edges = KrakLoader.loadEdges(krakFilePath);
        RoadPart[] roads = new RoadPart[edges.length];
        for (int i = 0; i < edges.length; i++) {
            roads[i] = new RoadPart(edges[i]);
        }
        System.out.println("Roads loaded! ("+roads.length+" nodes)");
        return roads;
    }
    
    public static RoadPart[] loadRoads(String roadFilePath) {
        System.out.println("Loading road data...");
        ArrayList<RoadPart> list = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(Utils.getFile(roadFilePath)));
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
        return list.toArray(new RoadPart[0]);
    }

    /**
     * Saves the roads in the new format
     * @param roads The roads to save
     * @param path The path to save them to (the directory should exist)
     */
    public static void saveRoads(RoadPart[] roads, String path) {
        System.out.println("Saving road data...");
        File file = new File(path);
        try { file.createNewFile(); }// Ensure that the path exists
        catch (IOException ex) {
            throw new RuntimeException("Error at file.createNewFile()");
        }
        try {   
            FileWriter writer = new FileWriter(file);
            for (RoadPart road : roads) {
                writer.write(road+"\n");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error at the fileWriter");
        }
        System.out.println("Road data saved! ("+roads.length+" nodes)");
    }
    
    public static void main(String[] args) {
        /*
        IMPORTANT
        changed "KRATHUS, SKOVALLEEN" in the roads.txt file to SKOVALLEEN
        */
        System.out.println("Testing...");
        
        Path srcdir = Paths.get(Paths.get(Utils.getcwd()).getParent().getParent()+"","src");
        String p = Paths.get(srcdir.toString(), "resources", "roads.txt").toString();
        System.out.println("p: "+p);
        System.out.println("cwd: "+Utils.getcwd());
        //RoadPart[] roads = loadKrakRoads("krak/kdv_unload.txt");
        //Road[] roads = new RoadPart[0];
        //saveRoads(roads, p);
        
        String line = "2,2,3,4,,";
        Tokenizer.setLine(line);
        System.out.println(Tokenizer.getInt()+Tokenizer.getInt()+Tokenizer.getInt()+Tokenizer.getInt()+Tokenizer.getString()+Tokenizer.getString());
        
        
        RoadPart[] roads = loadRoads("resources/roads.txt");
        
        System.out.println("Sampling roads:");
        for (int i=0; i<50; i++) {
            System.out.println(i+1+": '"+roads[i]+"'");
        }
        
        MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
        System.out.printf("Heap memory usage: %d MB%n",
                mxbean.getHeapMemoryUsage().getUsed() / (1000000));
    }
}
