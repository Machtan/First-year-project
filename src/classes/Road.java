package classes;

import classes.Viewport.Projection;
import enums.RoadType;
import interfaces.QuadNode;
import java.util.Iterator;

/**
 * The Road class represents a road in a smarter way
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 13-May-2014
 */
public class Road implements QuadNode, Iterable<Road.Edge> {

    @Override
    public boolean collidesWith(Rect area) {
        return bounds.collidesWith(area);
    }
    public static class Node {
        public final long id;
        public final float x;
        public final float y;
        public Node(long id, float x, float y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
        public int mappedX(Projection p) {
            return Math.round(p.target.x + (x - p.source.x) * p.ppu);
        }
        public int mappedY(Projection p, int windowHeight) {
            return Math.round(windowHeight - (p.target.y + (y - p.source.y) * p.ppu));
        }
        @Override
        public String toString() {
            return "["+id+": ("+x+", "+y+")]";
        }
    }
    public class Edge {
        public final Node p1;
        public final Node p2;
        public final float driveTime;
        /**
         * An edge represents an edge on a graph
         * @param p1
         * @param p2
         */
        public Edge(Node p1, Node p2, float driveTime) {
            this.p1 = p1;
            this.p2 = p2;
            this.driveTime = driveTime;
        }
        public Edge(Node p1, Node p2) {
            this.p1 = p1;
            this.p2 = p2;
            this.driveTime = (float)length() * Road.this.speedLimit;
        }
        public Road parent() {
            return Road.this;
        }
        public Rect getRect() {
            return new Rect(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), 
                    Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
        }
        public double length() {
            return Math.sqrt(Math.pow(p2.x - p1.x, 2)+Math.pow(p2.y - p1.y, 2));
        }
    }
    public final String     name;
    public final RoadType   type; // Using int representation
    public final short      zipCode;
    public final short      speedLimit;
    public final boolean    oneway;
    public final Node[]     nodes;
    public final float[]    drivetimes;
    public final Rect       bounds;
    /**
     * Constructor for the Road class
     * @param name The name of the road
     * @param type The type of the road 
     * @param nodes The list of successive nodes in this road
     * @param zipCode The zipCode of the area this road is within
     * @param oneway Wether the road is one-way or bidirectional
     * @param speedLimit The speed limit for this road
     * @param drivetimes The time it takes to pass the edges in the road
     * @param bounds The bounding area of this road
     */
    public Road (String name, RoadType type, short zipCode, short speedLimit, 
            boolean oneway, Node[] nodes, float[] drivetimes, Rect bounds) {
        this.name = name;
        this.type = type;
        this.zipCode = zipCode;
        this.speedLimit = speedLimit;
        this.oneway = oneway;
        this.nodes = nodes;
        this.drivetimes = drivetimes;
        this.bounds = bounds;
    }
    
    private class EdgeIter implements Iterator<Edge> {
        int index;
        Edge nextEdge;
        public EdgeIter() {
            index = 0;
        }
        @Override
        public boolean hasNext() {
            return index < Road.this.nodes.length-1;
        }

        @Override
        public Edge next() {
            nextEdge = new Edge(Road.this.nodes[index], Road.this.nodes[index+1], 
                Road.this.drivetimes[index]);
            index++;
            return nextEdge;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removal not supported");
        }
        
    }
    
    public Iterator<Edge> iterator() {
        return new EdgeIter();
    }
}
