package classes;

import classes.Graph.NoPathException;
import java.util.HashMap;
import java.util.PriorityQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class PathFinder {

    private static HashMap<Long, Road.Edge> edgeTo;
    private static HashMap<Long, Double> distTo;
    private static PriorityQueue<QueueElement> pq;

    private static double h(Road.Node source, Road.Node target) { //s = start, t = target
        // System.out.println("Heuristic analysis: \n"
        //         + "Intersection source: " + source + ". Intersection target: " + target);
        return Math.sqrt(Math.pow(source.x - target.x, 2) + Math.pow(source.y - target.y, 2))/1000/130;
    }
    
    private static class QueueElement implements Comparable<QueueElement> {
        public final long id;
        public double val;
        public QueueElement(long id, double val) {
            this.val = val;
            this.id = id;
        }

        @Override
        public int compareTo(QueueElement o) {
            return Double.compare(val, o.val);
        }
        
        public boolean equals(QueueElement other) {
            return id == other.id;
        }
    }

    public static Road.Edge[] findPath(Graph G, long sourceID, long targetID) {
        if (G == null) {
            throw new RuntimeException("Graph have not been instantitiated.");
        }
        try {
            //    System.out.println("Finding path");
            edgeTo = new HashMap<>();
            distTo = new HashMap<>();
            distTo.put(sourceID, 0.0);

            pq = new PriorityQueue<>();
            pq.offer(new QueueElement(sourceID, Double.POSITIVE_INFINITY));
            while (!pq.isEmpty()) {
                long v = pq.poll().id;
                //System.out.println("Checking vertex "+v);
                if (v == targetID) {
                    break;
                }
                for (Road.Edge r : G.adj(v)) {
                    //System.out.println("- (" + v + ") Relaxing part " + r);
                    relax(G, r, v, targetID);
                }
            }

            //System.out.println("Checked edges:");
            //  for (int key : edgeTo.keySet()) {
            //System.out.println("- "+key+": "+edgeTo.get(key));
            //   }

            HashMap<Integer, Road.Edge> path = new HashMap<>();
            long current = targetID;
            int i = 0;
            // System.out.println("Building edgeTo");
            // System.out.println(edgeTo.toString());
            while (current != sourceID) {
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
        } catch (NoPathException ex) {
            JOptionPane.showMessageDialog(new JFrame(), 
                    "No path could be found between the given addresses", 
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            return new Road.Edge[0];
        }
    }

    private static void relax(Graph G, Road.Edge r, long s, long t) throws NoPathException {
        //   System.out.println("Relaxing");
        long v = s; // The starting vertice
        long w = G.other(r, v); // w is the other vertice
        //System.out.println("Relaxing edge between: "+v+ " and "+w);
        if (!distTo.containsKey(w)) {
            //    System.out.println("DistTo adds the new key with infinity:" + w);
            distTo.put(w, Double.POSITIVE_INFINITY);
        }
        if (distTo.get(w) > distTo.get(v) + r.driveTime) {
            distTo.put(w, distTo.get(v) + r.driveTime);
            edgeTo.put(w, r);
            //System.out.println("Updates the approximated weight and puts the road into edgeTo: "+r);
            QueueElement q = new QueueElement(w, 
                    distTo.get(w) + h(G.getIntersection(w), G.getIntersection(t)));
            if (pq.contains(q)) {
                //      System.out.println("Decreasing");
                pq.remove(q);
            } 
            pq.offer(q);
        }
    }
}

