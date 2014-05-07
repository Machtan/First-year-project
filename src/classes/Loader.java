package classes;

/**
 * The Loader class <More docs goes here>
 *
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
import classes.Utils.LoadFileException;
import interfaces.ILoader;
import interfaces.IProgressBar;
import java.io.BufferedReader;
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
public class Loader implements ILoader {

    protected static final String encoding = "iso8859-1";

    /**
     * Loads a list of reformatted RoadPart data from a file
     *
     * @param roadFilePath The path to the file
     * @param bar An optional progress bar
     * @return An array of RoadPart elements
     */
    public static RoadPart[] loadRoads(String roadFilePath, IProgressBar bar) {
        System.out.println("Loading road data...");
        long t1 = System.nanoTime();
        ArrayList<RoadPart> list = new ArrayList<>();

        BufferedReader br;
        try {
            
            br = new BufferedReader(new InputStreamReader(Utils.getFileStream(roadFilePath),
                    Charset.forName(encoding)));

            String line;
            // Do the long loading without if-statements inside ;)
            if (bar != null) {
                while ((line = br.readLine()) != null) {
                    list.add(new RoadPart(line));
                    bar.update(1);
                }
            } else {
                while ((line = br.readLine()) != null) {
                    list.add(new RoadPart(line));
                }
            }
            
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load road data from '" + roadFilePath + "'");
        } catch (LoadFileException ex) {
            System.out.println("Could not load the file. Error: "+ex);
            if (bar != null) {
                bar.setTarget("Road loading Error!", 0);
            }
            return new RoadPart[0];
        }
        System.gc();
        System.out.println("Road data loaded!");
        double elapsed = (System.nanoTime() - t1) / (1000000000.0);
        System.out.printf("Loaded the roads in %.3f seconds\n", elapsed);
        return list.toArray(new RoadPart[list.size()]);
    }
    
    public static RoadPart[] loadRoads(String roadFilePath) {
        return loadRoads(roadFilePath, null);
    }

    /**
     * Loads a list of intersections in the new format from a file
     * @param intersectionFilePath The relative file path from this project's 
     * source package to a file containing intersection data.
     * @param bar An optional progress bar
     * @return An array of intersections
     */
    public static Intersection[] loadIntersections(String intersectionFilePath, IProgressBar bar) {        
        System.out.println("Loading intersection data...");
        long t1 = System.nanoTime();
        ArrayList<Intersection> list = new ArrayList<>();

        BufferedReader br;
        try {
            //File file = Utils.getFile(intersectionFilePath);
            br = new BufferedReader(new InputStreamReader(Utils.getFileStream(intersectionFilePath),
                    Charset.forName(encoding)));

            String line;
            if (bar != null) { // Conditional stuff here as well
                while ((line = br.readLine()) != null) {
                    list.add(new Intersection(line));
                    bar.update(1);
                }
            } else {
                while ((line = br.readLine()) != null) {
                    list.add(new Intersection(line));
                }
            }
            
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load intersection data from '" + intersectionFilePath + "'");
        } catch (LoadFileException ex) {
            System.out.println("Could not load the file. Error: "+ex);
            if (bar != null) {
                bar.setTarget("Intersection loading Error!", 0);
            }
            return new Intersection[0];
        }
        System.gc();
        System.out.println("Intersection data loaded!");
        double elapsed = (System.nanoTime() - t1) / (1000000000.0);
        System.out.printf("Loaded the intersections in %.3f seconds\n", elapsed);
        return list.toArray(new Intersection[list.size()]);
    }
    
    public static Intersection[] loadIntersections(String intersectionFilePath) {
        return loadIntersections(intersectionFilePath, null);
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

    /**
     * Loads data from the given files
     * @param bar A progress bar (can be null)
     * @param files The files to read from (intersectionfile, roadfile)
     * @return 
     */
    @Override
    public Model loadData(IProgressBar bar, Datafile... files) {
        if (files.length < 2) {
            throw new RuntimeException("Too few files passed to the Loader! (needs 2)");
        }
        Datafile insFile = files[0];
        Datafile roadFile = files[1];
        Intersection[] ins;
        RoadPart[] roads;
        Model model;
        if (bar != null) {
            System.out.println("Loading krak data with a progress bar :u!");
            bar.setTarget(insFile.progressDescription, insFile.lines);
            ins = loadIntersections(insFile.filename, bar);
            bar.setTarget(roadFile.progressDescription, roadFile.lines);
            roads = loadRoads(roadFile.filename, bar);
            model = new Model(ins, roads, bar);
            
        } else {
            System.out.println("Nope, no progress bar here");
            ins = loadIntersections(insFile.filename);
            roads = loadRoads(roadFile.filename);
            model = new Model(ins, roads);
        }
        return model;
    }

    @Override
    public Model loadData(Datafile... files) {
        return loadData(null, files);
    }
}
