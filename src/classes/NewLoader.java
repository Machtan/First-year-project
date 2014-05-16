package classes;

import enums.RoadType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/* OSM
x: [52.691433 : 62.0079024]
y: [-20.071433 : 28.0741667]
*/

/**
 * The NewLoader class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 15-May-2014
 */
public class NewLoader {
    
    public static Datafile krakdata = new Datafile("resources/new_krak_roads.txt", 812301, 
            "Loading new Krak roads...");
    public static Rect krakBounds = new Rect(
            442254.35659f, 
            6049914.43018f, 
            892658.21706f - 442254.35659f, 
            6402050.98297f - 6049914.43018f);
   
    public static Datafile osmdata = new Datafile("resources/new_osm_roads.txt", 
            0, "Loading new OSM roads...");
    
    public static Rect OSMBounds = new Rect(
            52.691433f, 
            -20.071433f,
            62.0079024f - 52.691433f, 
            28.0741667f - (-20.071433f)
    );
    
    /**
     * Returns how fast once could ideally traverse between the given points
     * @param p1 The point of origin
     * @param p2 The point of destination
     * @param speedLimit How fast you can move
     * @return How many hours (in decimal) it takes to traverse between the points
     */
    public static float getDriveTime(Road.Node p1, Road.Node p2, short speedLimit) {
        float dist = (float)Math.sqrt(Math.pow(p2.x-p1.x, 2)+Math.pow(p2.y-p1.y, 2));
        return dist / ((float)speedLimit * 1000f);
    }
    
    public static final char sepchar = '@';
    public static Road loadRoad(String line) {
        //System.out.println("Parsing road line: '"+line+"'");
        // Split the road into metadata, nodes and drive times
        int firstSplit = line.indexOf(sepchar);
        int secondSplit = line.indexOf(sepchar, firstSplit+1);
        if ((firstSplit == -1) || (secondSplit == -1)) {
            throw new RuntimeException("Could not split at '"+sepchar+"' in the line:\n"+line);
        }
        String meta = line.substring(0, firstSplit);
        String nodestring = line.substring(firstSplit+1, secondSplit);
        String drivetimestring = line.substring(secondSplit+1);
                
        Utils.Tokenizer.setLine(meta);
        String      name        = Utils.Tokenizer.getString();
        RoadType    type        = RoadType.fromValue(Utils.Tokenizer.getInt());
        short       zip         = Utils.Tokenizer.getShort();
        short       speedLimit  = Utils.Tokenizer.getShort();
        boolean     oneway      = Utils.Tokenizer.getBool();
        
        Utils.Tokenizer.setLine(nodestring);
        ArrayList<Road.Node> nodes = new ArrayList<>();
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = minX;
        float maxY = maxX;
        while (Utils.Tokenizer.hasNext()) {
            long id = Utils.Tokenizer.getLong();
            float x = Utils.Tokenizer.getFloat();
            float y = Utils.Tokenizer.getFloat();
            minX = (x < minX)? x: minX;
            maxX = (x > maxX)? x: maxX;
            minY = (y < minY)? y: minY;
            maxY = (y > maxY)? y: maxY;
            nodes.add(new Road.Node(id, x, y));
        }
        Rect bounds = new Rect(minX, minY, maxX-minX, maxY-minY);
        
        float[] driveTimes = new float[nodes.size()-1];
        if (drivetimestring.length() != 0) {
            Utils.Tokenizer.setLine(drivetimestring);
            for (int i = 0; i < driveTimes.length; i++) {
                driveTimes[i] = Utils.Tokenizer.getFloat();
            }
        } else {
            for (int i = 0; i < driveTimes.length; i++) {
                driveTimes[i] = getDriveTime(nodes.get(i), nodes.get(i+1), speedLimit);
            }
        }
        
        return new Road(name, type, zip, speedLimit, oneway, 
                nodes.toArray(new Road.Node[nodes.size()]), driveTimes, bounds);
    }
    
    public static Model loadData(Datafile file) {
        Rect bounds = new Rect(0,0,0,0);
        if (file.equals(krakdata)) {
            bounds = krakBounds;
        } else if (file.equals(osmdata)) {
            bounds = OSMBounds;
        }
        Model model = new Model(bounds);
        
        model.startStream();
        try (InputStream stream = Utils.getFileStream(file.filename);
            InputStreamReader is = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(is)) {
            String line;
            while ((line = br.readLine()) != null) {
                model.add(loadRoad(line));
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load road data from '" + file.filename + "'");
        } catch (Utils.LoadFileException ex) {
            System.out.println("Could not load the file. Error: "+ex);
        }
        model.endStream();
        return model;
    }
    
    public static void main(String[] args) {
        System.out.println("Loading model...");
        long t1 = System.nanoTime();
        Model model = loadData(krakdata);
        double delta = (System.nanoTime()-t1)/1e9;
        System.out.println("Loaded!");
        System.out.println("The loading took "+delta+" seconds!");
    }
    
}
