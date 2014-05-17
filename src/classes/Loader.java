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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.charset.Charset;

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
     * @param bar An optional progress bar
     * @return An array of RoadPart elements
     */
    public static Road.Edge[] loadRoads(Datafile file, IProgressBar bar) {
        System.out.println("Loading road data...");
        long t1 = System.nanoTime();
        Road.Edge[] roads = new Road.Edge[(int)file.lines];
        int i = 0;
        try (InputStream stream = Utils.getFileStream(file.filename);
            InputStreamReader is = new InputStreamReader(stream, Charset.forName(encoding));
            BufferedReader br = new BufferedReader(is)) {

            String line;
            // Do the long loading without if-statements inside ;)
            if (bar != null) {
                while ((line = br.readLine()) != null) {
                    roads[i++] = new Road.Edge(line);
                    bar.update(1);
                }
            } else {
                while ((line = br.readLine()) != null) {
                    roads[i++] = new RoadPart(line);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load road data from '" + file.filename + "'");
        } catch (LoadFileException ex) {
            System.out.println("Could not load the file. Error: "+ex);
            if (bar != null) {
                bar.setTarget("Road loading Error!", 0);
            }
            return new RoadPart[0];
        }
        
        System.out.println("Road data loaded!");
        double elapsed = (System.nanoTime() - t1) / (1000000000.0);
        System.out.printf("Loaded the roads in %.3f seconds\n", elapsed);
        System.gc();
        return roads; //arr;
    }
    
    public static Road.Edge[] loadRoads(Datafile file) {
        return loadRoads(file, null);
    }
    
    /**
     * Loads a list of intersections in the new format from a file
     * @param intersectionFilePath The relative file path from this project's 
     * source package to a file containing intersection data.
     * @param bar An optional progress bar
     * @return An array of intersections
     */
    public static Intersection[] loadIntersections(Datafile file, IProgressBar bar) {        
        System.out.println("Loading intersection data...");
        long t1 = System.nanoTime();
        
        Intersection[] intersections = new Intersection[(int)file.lines];
        int i = 0;
        try (InputStream stream = Utils.getFileStream(file.filename);
            InputStreamReader is = new InputStreamReader(stream, Charset.forName(encoding));
            BufferedReader br = new BufferedReader(is)){

            String line;
            if (bar != null) { // Conditional stuff here as well
                while ((line = br.readLine()) != null) {
                    intersections[i++] = new Intersection(line);
                    bar.update(1);
                }
            } else {
                while ((line = br.readLine()) != null) {
                    intersections[i++] = new Intersection(line);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load intersection data from '" + file.filename + "'");
        } catch (LoadFileException ex) {
            System.out.println("Could not load the file. Error: "+ex);
            if (bar != null) {
                bar.setTarget("Intersection loading Error!", 0);
            }
            return new Intersection[0];
        }
        
        System.out.println("Intersection data loaded!");
        double elapsed = (System.nanoTime() - t1) / (1000000000.0);
        System.out.printf("Loaded the intersections in %.3f seconds\n", elapsed);
        return intersections;
    }
    
    public static Intersection[] loadIntersections(Datafile file) {
        return loadIntersections(file, null);
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
            bar.setTarget(insFile.progressDescription, insFile.lines);
            ins = loadIntersections(insFile, bar);
            bar.setTarget(roadFile.progressDescription, roadFile.lines);
            roads = loadRoads(roadFile, bar);
            model = new Model(ins, roads, bar);
            
        } else {
            ins = loadIntersections(insFile);
            roads = loadRoads(roadFile);
            model = new Model(ins, roads);
        }
        ins = null;
        roads = null;
        System.gc();
        MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
        System.out.printf("Heap memory usage: %d MB%n",
                mxbean.getHeapMemoryUsage().getUsed() / (1000000));
        return model;
    }

    @Override
    public Model loadData(Datafile... files) {
        return loadData(null, files);
    }
}
