package classes;

/**
 * The Loader class <More docs goes here>
 *
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.charset.Charset;
import krak.EdgeData;
import krak.KrakLoader;
import java.util.ArrayList;
import krak.NodeData;

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
    //Used for loading
    public static int intersectionCounter;
    public static int roadCounter;

    /**
     * Loads roads from a krak file
     *
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
        System.out.println("Roads loaded! (" + roads.length + " nodes)");
        return roads;
    }

    /**
     * Loads a list of reformatted RoadPart data from a file
     *
     * @param roadFilePath The path to the file
     * @return An array of RoadPart elements
     */
    public static RoadPart[] loadRoads(String roadFilePath) {
        System.out.println("Loading road data...");
        long t1 = System.nanoTime();
        ArrayList<RoadPart> list = new ArrayList<>();

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(Utils.getFile(roadFilePath)),
                    Charset.forName(encoding)));

            String line;
            while ((line = br.readLine()) != null) {
                list.add(new RoadPart(line));
                roadCounter++;
                ProgressBar.updateLabel(ProgressBar.update(roadCounter + intersectionCounter));
            }
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load road data from '" + roadFilePath + "'");
        }
        System.gc();
        System.out.println("Road data loaded!");
        System.out.println("Road data counter: "+ roadCounter);
        double elapsed = (System.nanoTime() - t1) / (1000000000.0);
        System.out.printf("Loaded the roads in %.3f seconds\n", elapsed);
        return list.toArray(new RoadPart[0]);
    }

    /**
     * Loads a list of intersections in the new format from a file
     *
     * @param intersectionFilePath
     * @return
     */
    public static Intersection[] loadIntersections(String intersectionFilePath) {
        System.out.println("Loading intersection data...");
        long t1 = System.nanoTime();
        ArrayList<Intersection> list = new ArrayList<>();

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(Utils.getFile(intersectionFilePath)),
                    Charset.forName(encoding)));

            String line;
            while ((line = br.readLine()) != null) {

                list.add(new Intersection(line));
                intersectionCounter++;
                ProgressBar.update(roadCounter + intersectionCounter);
                ProgressBar.updateLabel(ProgressBar.update(roadCounter + intersectionCounter));
            }
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load intersection data from '" + intersectionFilePath + "'");
        }
        System.gc();
        System.out.println("Intersection data loaded!");
        System.out.println("Intersectioncount: "+ intersectionCounter);
        double elapsed = (System.nanoTime() - t1) / (1000000000.0);
        System.out.printf("Loaded the intersections in %.3f seconds\n", elapsed);
        return list.toArray(new Intersection[0]);
    }

    public static Intersection[] loadKrakIntersections(String nodeFilePath) {
        System.out.println("Loading intersection data...");
        long t1 = System.nanoTime();
        NodeData[] nodes = KrakLoader.loadNodes(nodeFilePath);
        Intersection[] intersections = new Intersection[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            intersections[i] = new Intersection(nodes[i]);
        }
        System.out.println("Intersection data loaded!");
        double elapsed = (System.nanoTime() - t1) / (1000000000.0);
        System.out.printf("Loaded the intersections in %.3f seconds\n", elapsed);
        return intersections;
    }

    /**
     * Saves the roads in the new format
     *
     * @param roads The roads to save
     * @param path The path to save them to (the directory should exist)
     */
    public static void saveRoads(RoadPart[] roads, String path) {
        System.out.println("Saving road data...");
        Utils.save(roads, path);
        System.out.println("Road data saved! (" + roads.length + " parts)");
    }

    /**
     * Saves a list of intersections to a file in the new format
     *
     * @param intersections The intersections
     * @param path The
     */
    public static void saveIntersections(Intersection[] intersections, String path) {
        System.out.println("Saving intersection data...");
        Utils.save(intersections, path);
        System.out.println("Intersection data saved! (" + intersections.length + " locations)");
    }

    public static int getIntersectionCnt() {
        return intersectionCounter;
    }

    public static int getRoadCnt() {
        return roadCounter;
    }

    public static void main(String[] args) {
        System.out.println("Testing...");

        //RoadPart[] roads = loadKrakRoads("krak/kdv_unload.txt"); // 234MB heap ~2x size
        //saveRoads(roads, p);       
        //RoadPart[] roads = loadRoads("resources/roads.txt"); // ~100MB heap ~2x size //now 220 :d
        //Intersection[] intersections = loadKrakIntersections("krak/kdv_node_unload.txt");
        //saveIntersections(intersections, "resources/intersections.txt");
        Intersection[] ints = loadIntersections("resources/intersections.txt"); // ~30MB
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Intersection i : ints) {
            if (i.x < minX) {
                minX = i.x;
            }
            if (i.x > maxX) {
                maxX = i.x;
            }
            if (i.y < minY) {
                minY = i.y;
            }
            if (i.y > maxY) {
                maxY = i.y;
            }
        }

        System.out.println("X ranges from " + minX + " to " + maxX + " (" + (maxX - minX) + ")");
        System.out.println("Y ranges from " + minY + " to " + maxY + " (" + (maxY - minY) + ")");

        MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
        System.out.printf("Heap memory usage: %d MB%n",
                mxbean.getHeapMemoryUsage().getUsed() / (1000000));
    }
}
