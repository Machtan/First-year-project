package classes;

import interfaces.IProgressBar;
import interfaces.Receiver;
import interfaces.StreamedContainer;
import java.awt.geom.Point2D;

/**
 * The Finder class is a static class with methods to find roads and nodes near
 * a set of map coordinates.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 18-May-2014
 */
public class Finder {
    
    private static final int startWidth = 100; // initial invariable for the width of a new rectangle.
    private static final int startHeight = 100; // initial invariable for the heigth of a new rectangle.
    
    /**
     * Function to calculate the smallest distance from a point P(pX, pY) to the
     * a line with ends at point A(aX, aY) and point B(bX, bY).
     * @param a = The starting point of the line
     * @param b = The ending point of the line
     * @param source = The source position
     * @return The distance between the source position to the middle of the line
     */
    public static float pointToLineDistance(Point2D.Float a, Point2D.Float b, Point2D.Float source) {
        // Creates a vector from point a to point source.
        Point2D.Float c = new Point2D.Float(source.x - a.x, source.y - a.y);
        // Creates a vector from a to b.
        Point2D.Float ab = new Point2D.Float(b.x - a.x, b.y - a.y);
        // Calculate the length of the vector ab.
        float length = (float) Math.sqrt(ab.x*ab.x + ab.y*ab.y);
        // Create a unit vector of ab.
        Point2D.Float unit = new Point2D.Float(ab.x / length, ab.y / length);
        // calculate the length of the vector, moving from a towards b, and ending
        // where the closest distance to point source will be.
        float dot = unit.x * c.x + unit.y * c.y;
        // If source is further away from b than a is.
        if (dot < 0) {
            return (float) Math.sqrt((source.x - a.x) * (source.x - a.x) + (source.y - a.y) * (source.y - a.y));
            // if source is further away from a then b is.
        } else if (dot > length) {
            return (float) Math.sqrt((source.x - b.x) * (source.x - b.x) + (source.y - b.y) * (source.y - b.y));
        } else {
            // create a vector with the same degree as ab, that has the length 
            // of point a to the point that is closest to point source
            Point2D.Float unitDot = new Point2D.Float(unit.x * dot, unit.y * dot);
            // add the coordinates of point a to unitDot to get the coordinates
            // of the point on line ab, which is closest to source
            Point2D.Float point = new Point2D.Float(a.x + unitDot.x, a.y + unitDot.y);
            // Use Pythagoras to find distance from source to point
            return (float) Math.sqrt((source.x - point.x) * (source.x - point.x) + (source.y - point.y) * (source.y - point.y));
        }
    }
    
    /**
     * Calculates the distance from a position to a node
     * @param x The x-coodinate of the position
     * @param y The y-coordinate of the position
     * @param n2 The second node
     * @return The distance between the nodes
     */
    public static float distance(float x, float y, Road.Node n2) {
        return (float)Math.sqrt(Math.pow(n2.x - x, 2) + Math.pow(n2.y - y, 2));
    }
    
    /**
     * Finds the node within and closest to the center of the given rect
     * @param rect The rect to look in
     * @param recipient Whom to give the node to
     * @param con The controller to ask for roads
     */
    public static void findNearestNode(Rect rect, Receiver<Road.Node> recipient, Controller con) {
        con.streamRoads(rect, new NodeChecker(rect, recipient, con));
    }
    
    /**
     * Finds the node within and closest to the given position
     * @param x The x-coordinate of the position
     * @param y The y-coordinate of the position
     * @param recipient Whom to give the node to
     * @param con The controller to ask for roads
     */
    public static void findNearestNode(float x, float y, Receiver<Road.Node> recipient, Controller con) {
        Rect rect = new Rect(x - startWidth / 2, y - startHeight / 2, startWidth, startHeight);
        findNearestNode(rect, recipient, con);
    }
    
    /**
     * Finds the road overlapping the rectangle, that is closest to its center
     * @param rect The rect to look in
     * @param recipient The object that will receive the road once it is found
     * @param con The controller to ask for roads
     */
    public static void findNearestRoad(Rect rect, Receiver<Road> recipient, Controller con) {
        con.streamRoads(rect, new RoadChecker(rect, recipient, con));
    }
    
    /**
     * Finds the road closest to the given map coordinates, and notifies the 
     * recipient once the nearest road is found
     * @param x The x-coordinate 
     * @param y The y-coordinate
     * @param recipient The object to receive the road
     * @param controller The controller to ask for roads in the area
     */
    public static void findNearestRoad(float x, float y, Receiver<Road> recipient, Controller controller) {
        Rect rect = new Rect(x - startWidth / 2, y - startHeight / 2, startWidth, startHeight);
        findNearestRoad(rect, recipient, controller);
    }
    
    private static class RoadChecker implements StreamedContainer<Road> {
        /**
         * A helper class to make the road finding static while still using
         * the streaming method.
         */
        private Rect rect;
        private Road nearest;
        private float minDist;
        private Receiver<Road> recipient;
        private Controller con;
 
        public RoadChecker(Rect rect, Receiver<Road> recipient, Controller con) {
            this.rect = rect;
            this.recipient = recipient;
            this.con = con;
        }
        
        @Override
        public void startStream() {
            //System.out.println("Starting road check stream...");
            nearest = null;
            minDist = Float.MAX_VALUE;
        }

        @Override
        public void startStream(IProgressBar bar) {
            throw new UnsupportedOperationException("ProgressBar unsupported");
        }

        @Override
        public void add(Road obj) {
            for (Road.Edge edge : obj) {
                float distance = pointToLineDistance(
                        new Point2D.Float(edge.p1.x, edge.p1.y),
                        new Point2D.Float(edge.p2.x, edge.p2.y),
                        rect.center()
                );
                if (distance < minDist) {
                    nearest = obj;
                    minDist = distance;
                }
            }
        }

        @Override
        public void endStream() {
            if (nearest == null) { // No roads were found this time
                float rectX = rect.x - rect.width / 2;
                float rectY = rect.y - rect.height / 2;
                float rectWidth = rect.width * 2;
                float rectHeight = rect.height * 2;
                rect = new Rect(rectX, rectY, rectWidth, rectHeight);
                //System.out.println("Found no roads, changing rect to "+cursorRect);
                Finder.findNearestRoad(rect, recipient, con);
            } else {
                //System.out.println("Found a road, ending...");
                recipient.receive(nearest);
            }
        }
    }
    private static class NodeChecker implements StreamedContainer<Road> {
        /**
         * A helper class to make the road finding static while still using
         * the streaming method.
         * (Sorry, I couldn't find a way to do this nicely without duplication)
         */
        private Rect rect;
        private Road.Node nearest;
        private float minDist;
        private Receiver<Road.Node> recipient;
        private Controller con;
        private float x;
        private float y;
 
        public NodeChecker(Rect rect, Receiver<Road.Node> recipient, Controller con) {
            this.rect = rect;
            this.recipient = recipient;
            this.con = con;
            x = rect.center().x;
            y = rect.center().y;
        }
        
        @Override
        public void startStream() {
            //System.out.println("Starting road check stream...");
            nearest = null;
            minDist = Float.MAX_VALUE;
        }

        @Override
        public void startStream(IProgressBar bar) {
            throw new UnsupportedOperationException("ProgressBar unsupported");
        }

        @Override
        public void add(Road obj) {
            for (Road.Node node : obj.nodes) {
                float distance = distance(x, y, node);
                if (distance < minDist) {
                    nearest = node;
                    minDist = distance;
                }
            }
        }

        @Override
        public void endStream() {
            if (nearest == null) { // No roads were found this time
                float rectX = rect.x - rect.width / 2;
                float rectY = rect.y - rect.height / 2;
                float rectWidth = rect.width * 2;
                float rectHeight = rect.height * 2;
                rect = new Rect(rectX, rectY, rectWidth, rectHeight);
                //System.out.println("Found no roads, changing rect to "+cursorRect);
                Finder.findNearestNode(rect, recipient, con);
            } else {
                //System.out.println("Found a road, ending...");
                recipient.receive(nearest);
            }
        }
    }
}
