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
        for (Edge edge : this) {
            if (edge.getRect().collidesWith(area)) {
                return true;
            }
        }
        return false;
    }
    public class Node {
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
            return Math.round(windowHeight - (y - p.source.y) * p.ppu);
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
        public Rect getRect() {
            return new Rect(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), 
                    Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
        }
        public double length() {
            return Math.sqrt(Math.pow(p2.x - p1.x, 2)+Math.pow(p2.y - p1.y, 2));
        }
    }
    public final String name;
    public final char speedLimit;
    public final short zipCode;
    public final RoadType type; // Using int representation
    public final boolean oneway;
    public final Node[] nodes;
    /**
     * Constructor for the Road class
     * @param name The name of the road
     * @param type The type of the road 
     * @param nodes The list of successive nodes in this road
     * @param zipCode The zipCode of the area this road is within
     * @param oneway
     * @param speedLimit The speed limit for this road
     */
    public Road (String name, char type, short zipCode, char speedLimit, 
            boolean oneway, Node[] nodes) {
        this.name = name;
        this.type = RoadType.fromValue(type);
        this.zipCode = zipCode;
        this.speedLimit = speedLimit;
        this.oneway = oneway;
        this.nodes = nodes;
        
    }
    
    private class EdgeIter implements Iterator<Edge> {
        int index;
        public EdgeIter() {
            index = 0;
        }
        @Override
        public boolean hasNext() {
            return index < Road.this.nodes.length-1;
        }

        @Override
        public Edge next() {
            return new Edge(Road.this.nodes[index], Road.this.nodes[++index]);
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
