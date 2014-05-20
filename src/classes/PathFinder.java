package classes;

import enums.RoadType;
import java.util.HashMap;

public class PathFinder {

    private static HashMap<Integer, Road.Edge> edgeTo;
    private static HashMap<Integer, Double> distTo;
    private static IndexMinPQ<Double> pq;

    private static double h(Road.Node source, Road.Node target) { //s = start, t = target
        // System.out.println("Heuristic analysis: \n"
        //         + "Intersection source: " + source + ". Intersection target: " + target);
        return Math.sqrt(Math.pow(source.x - target.x, 2) + Math.pow(source.y - target.y, 2)) / 1000 / 130;
    }

    public static Road.Edge[] findPath(Graph G, long sourceID, long targetID) {
        if (G == null) {
            throw new RuntimeException("Graph have not been instantitiated.");
        } else {
            //    System.out.println("Finding path");
            int t = G.getIntersectionIndex(targetID);
            edgeTo = new HashMap<>();
            distTo = new HashMap<>();
            int startIndex = G.getIntersectionIndex(sourceID);
            distTo.put(startIndex, 0.0);

            pq = new IndexMinPQ(G.V());
            pq.insert(startIndex, Double.POSITIVE_INFINITY);
            while (!pq.isEmpty()) {
                int v = pq.delMin();
                //System.out.println("Checking vertex "+v);
                if (v == t) {
                    break;
                }
                for (Road.Edge r : G.adj(v)) {
                    //System.out.println("- (" + v + ") Relaxing part " + r);
                    relax(G, r, v, t);
                }
            }

            //System.out.println("Checked edges:");
            //  for (int key : edgeTo.keySet()) {
            //System.out.println("- "+key+": "+edgeTo.get(key));
            //   }

            HashMap<Integer, Road.Edge> path = new HashMap<>();
            int current = G.getIntersectionIndex(targetID);
            int i = 0;
            // System.out.println("Building edgeTo");
            // System.out.println(edgeTo.toString());
            while (current != startIndex) {
                Road.Edge currentRoad = edgeTo.get(current);
                path.put(i++, currentRoad);
                //System.out.println("Checking the other intersection than "+current+" of "+currentRoad);
                //System.out.println("Calling other() with: "+currentRoad+ " and current: " +current);
                current = G.other(currentRoad, current);
            }
            Road.Edge[] result = new Road.Edge[path.size()];

            for (int j = 0; j < path.size(); j++) {
                result[j] = path.get(path.size() - j - 1);
            }
            return result;
        }
    }

    private static void relax(Graph G, Road.Edge r, int s, int t) {
        //   System.out.println("Relaxing");
        int v = s; // The starting vertice
        int w = G.other(r, v); // w is the other vertice
        //System.out.println("Relaxing edge between: "+v+ " and "+w);
        if (!distTo.containsKey(w)) {
            //    System.out.println("DistTo adds the new key with infinity:" + w);
            distTo.put(w, Double.POSITIVE_INFINITY);
        }
        if (distTo.get(w) > distTo.get(v) + r.driveTime) {
            distTo.put(w, distTo.get(v) + r.driveTime);
            edgeTo.put(w, r);
            //System.out.println("Updates the approximated weight and puts the road into edgeTo: "+r);
            if (pq.contains(w)) {
                //      System.out.println("Decreasing");
                pq.decreaseKey(w, distTo.get(w) + h(G.getIntersection(w), G.getIntersection(t)));
            } else {
                //     System.out.println("Inserting");
                pq.insert(w, distTo.get(w) + h(G.getIntersection(w), G.getIntersection(t)));
            }
        }
    }

    public static void main(String[] args) {
        
        
        Model m = NewLoader.loadData(NewLoader.krakdata);
        Graph g = new Graph(m);
        Road.Edge[] result = findPath(g, 1, 20);
        for(Road.Edge road : result) {
            System.out.println(road.parent().name);
        }
    }
}
