/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import classes.RoadPart;
import classes.Intersection;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    private final int V; //Number of vertices/intersections
    private int E; //Number of edges/road parts
    private ArrayList[] adj;
    private RoadPart[] roads;
    private HashMap<Long, Integer> IDToIndex;
    private HashMap<Integer, Long> indexToID;
    private ArrayList<Intersection> inters;

    /**
     * Initializes an empty edge-weighted graph with <tt>V</tt> vertices and 0
     * edges. param V the number of vertices
     *
     * @throws java.lang.IllegalArgumentException if <tt>V</tt> < 0
     */
    public Graph(int intersections, RoadPart[] roads) {
        if (intersections < 0) {
            throw new IllegalArgumentException("Number of vertices must be nonnegative");
        }
        V = intersections;
        inters = new ArrayList<>(roads.length / 4);
        this.roads = roads;
        this.E = roads.length;
        adj = new ArrayList[V];
        IDToIndex = new HashMap<>();
        indexToID = new HashMap<>();

        for (int v = 0; v < V; v++) {
            adj[v] = new ArrayList<RoadPart>();
        }

        int i = 0;
        for (RoadPart road : roads) {
            if (!IDToIndex.containsKey(road.sourceID)) {
                System.out.println("Adding new intersection " + road.sourceID + " at index " + i);
                indexToID.put(i, road.sourceID);
                IDToIndex.put(road.sourceID, i++);
                inters.add(road.p1);
            }
            adj[IDToIndex.get(road.sourceID)].add(road);

            if (!IDToIndex.containsKey(road.targetID)) {
                System.out.println("Adding new intersection " + road.targetID + " at index " + i);
                indexToID.put(i, road.targetID);
                IDToIndex.put(road.targetID, i++);
                inters.add(road.p2);
            }
            adj[IDToIndex.get(road.targetID)].add(road);
        }
        int c = 0;
        for (ArrayList<RoadPart> arr : adj) {
            System.out.println((c++) + ": " + arr.size());
        }
    }

    /**
     * Returns the number of vertices in the edge-weighted graph.
     *
     * @return the number of vertices in the edge-weighted graph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in the edge-weighted graph.
     *
     * @return the number of edges in the edge-weighted graph
     */
    public int E() {
        return E;
    }

    /**
     * Returns the edges incident on vertex <tt>v</tt>.
     *
     * @return the edges incident on vertex <tt>v</tt> as an Iterable
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<RoadPart> adj(int v) {
        if (v < 0 || v >= V) {
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V - 1));
        }
        System.out.println("vertex " + v + " has " + adj[v].size() + " adjacent edges");
        return adj[v];
    }

    public int other(RoadPart part, int firstIDIndex) {
        System.out.println("Checking other index with indexes " + part.sourceID + " and " + part.targetID);
        if (IDToIndex.get(part.sourceID) == firstIDIndex) {
            return IDToIndex.get(part.targetID);
        } else {
            return IDToIndex.get(part.sourceID);
        }
    }

    public long getIntersectionID(int index) {
        return indexToID.get(index);
    }

    public int getIntersectionIndex(long ID) {
        System.out.println("Valid IDs:");
        for (long key : IDToIndex.keySet()) {
            System.out.println("- " + key);
        }

        System.out.println(ID + " -> " + IDToIndex.get(ID));
        return IDToIndex.get(ID);
    }

    public Intersection getIntersection(int index) {
        return inters.get(index);
    }

    /**
     * Returns all edges in the edge-weighted graph. To iterate over the edges
     * in the edge-weighted graph, use foreach notation:
     * <tt>for (Edge e : G.edges())</tt>.
     *
     * @return all edges in the edge-weighted graph as an Iterable.
     */
    public RoadPart[] edges() {
        return roads;
    }
}
