/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;


import interfaces.IProgressBar;
import interfaces.StreamedContainer;

import enums.RoadType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Graph implements StreamedContainer<Road>{

    private int V; //Number of vertices/intersections
    private int E; //Number of edges/road parts
    private ArrayList<ArrayList<Road.Edge>> adj;
    //private ArrayList<Road> roads;
    private ArrayList<Road.Node> nodes;
    private HashMap<Long, Integer> IDToIndex;
    private HashMap<Integer, Long> indexToID;

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
    public Graph(Model model) {
        model.getAllRoads(this);
    }

    /**
     * Returns the edges incident on vertex <tt>v</tt>.
     *
     * @return the edges incident on vertex <tt>v</tt> as an Iterable
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<Road.Edge> adj(int v) {
        if (v < 0 || v >= V) {
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V - 1));
        }
        //  System.out.println("vertex " + v + " has " + adj[v].size() + " adjacent edges");
        return adj.get(v);
    }

    public int other(Road.Edge part, int firstIndex) {
        // System.out.println("Checking other index with indexes " + part.sourceID + " and " + part.targetID);
        // System.out.println("Road:" + part);
        //System.out.println("RoadPart: " + part.name);
        //System.out.println("SourceID: " + part.sourceID + "\n"
        //      + "FirstIDIndex: " + firstIDIndex);
        if (part != null) {
            if (IDToIndex.get(part.p1.id) == firstIndex) {
                return IDToIndex.get(part.p2.id);
            } else {
                return IDToIndex.get(part.p1.id);
            }
        } else {
            throw new RuntimeException("Roadpart is null");
            //return firstIDIndex;
        }
    }

    public long getIntersectionID(int index) {
        return indexToID.get(index);
    }

    public int getIntersectionIndex(long ID) {
        return IDToIndex.get(ID);
    }

    public Road.Node getIntersection(int index) {
        return nodes.get(index);
    }

    @Override
    public void startStream() {
        V = 0;
        E = 0;
        nodes = new ArrayList<>();
        System.out.println("Starting the graph population...");
    }

    @Override
    public void startStream(IProgressBar bar) {
        throw new UnsupportedOperationException("Progressbar unsupported.");
    }

    @Override
    public void add(Road obj) {
        V += obj.nodes.length;
        //roads.add(obj);
        int i = nodes.size();
        Road.Edge last = null;
        for (Road.Edge edge: obj) {
            last = edge;
            Road.Node node = edge.p1;
            if (!IDToIndex.containsKey(node.id)) {
                indexToID.put(i, node.id);
                IDToIndex.put(node.id, i);
                nodes.add(node);
                adj.add(new ArrayList<Road.Edge>());
                i++;
            }
            
            adj.get(IDToIndex.get(edge.p1.id)).add(edge);
            if (!obj.oneway) { // Krak is mostly bidirectional
                adj.get(IDToIndex.get(edge.p2.id)).add(edge);
            }
            E += 1;
        }
        if (!IDToIndex.containsKey(last.p2.id)) {
            indexToID.put(i, last.p2.id);
            IDToIndex.put(last.p2.id, i);
            nodes.add(last.p2);
            i++;
        }
    }

    @Override
    public void endStream() {
        System.out.println("Graph populated!");
    }
}
