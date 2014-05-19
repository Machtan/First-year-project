package classes;

import java.util.HashMap;
import java.util.PriorityQueue;

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
        } else {
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
        }
    }

    private static void relax(Graph G, Road.Edge r, long s, long t) {
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

    public static void main(String[] args) { 
        
        /*
         Intersection first = new Intersection("100,10,2");
         Intersection second = new Intersection("10,15,3");
         Intersection third = new Intersection("20,27,14");
         Intersection fourth = new Intersection("30,12,6");
         Intersection fifth = new Intersection("40,4,1");
         Intersection sixth = new Intersection("1,15,4");
         Intersection seventh = new Intersection("18,25,7");

         Road.Edge rfirst = new RoadPart("100,30,0,rfirst,0,0,0,0,,,,,0,0,0,0,0,1,,,");
         Road.Edge rsecond = new RoadPart("30,20,0,rsecond,0,0,0,0,,,,,0,0,0,0,0,1,,,");
         Road.Edge rthird = new RoadPart("30,10,0,rthird,0,0,0,0,,,,,0,0,0,0,0,1,,,");
         RoadPart rfourth = new RoadPart("40,100,0,rfourth,0,0,0,0,,,,,0,0,0,0,0,1,,,");
         RoadPart rfith = new RoadPart("10,1,0,rfith,0,0,0,0,,,,,0,0,0,0,0,1,,,");
         RoadPart rsixth = new RoadPart("1,18,0,rsixth,0,0,0,0,,,,,0,0,0,0,0,1,,,");
         RoadPart rseventh = new RoadPart("18,20,0,rseventh,0,0,0,0,,,,,0,0,0,0,0,1,,,");

         rfirst.setPoints(first, fourth);
         rsecond.setPoints(fourth, third);
         rthird.setPoints(fourth, second);
         rfourth.setPoints(fifth, first);
         rfith.setPoints(second, sixth);
         rsixth.setPoints(sixth, seventh);
         rseventh.setPoints(seventh, third);

         RoadPart[] r = new RoadPart[]{rfirst, rsecond, rthird, rfourth, rfith, rsixth, rseventh};

         // for(RoadPart road : r) {
         //     System.out.println(road.name);
         // }
         Graph graph = new Graph(7, r);

         ShortestPath SP = new ShortestPath(graph);

         RoadPart[] RoadPartArray = SP.findPath(10, 30);
         System.out.println("The found path is:");
         for (RoadPart road : RoadPartArray) {
         System.out.println("- " + road.name);
         }
        ProgressBar progbar = new ProgressBar();
        Datafile krakRoads = new Datafile("resources/roads.txt", 812301,
                "Loading road data...");
        Datafile krakInters = new Datafile("resources/intersections.txt",
                675902, "Loading intersection data...");
        Model model = new Loader().loadData(progbar, krakInters, krakRoads);
        progbar.close();

        Road.Edge[] roadTemp = model.getRoads(model.getBoundingArea());
        for (Road.Edge r : roadTemp) {
            //System.out.println("Roads: " +r.name);
            r.setPoints(r.p1, r.p2);
        }

        Graph graph = new Graph(model.intersections, roadTemp, new HashSet<RoadType>());

        ShortestPath SP = new ShortestPath(graph);

        RoadPart[] RoadPartArray = SP.findPath(1, 100);
        for (RoadPart road : RoadPartArray) {
            System.out.println(road.name);
            
        
        ProgressBar progbar = new ProgressBar(); // Create the progress bar
        Dimension viewSize = new Dimension(600,400);
        OptimizedView view = new OptimizedView(viewSize, Controller.defaultInstructions);
        progbar.close();

        Model model = NewLoader.loadData(NewLoader.krakdata);
        Controller controller = new Controller(view, model); 
        controller.setMinimumSize(new Dimension(800,600));

        //controller.pack();
        System.out.println("View size previs:  " + view.getSize());
        controller.draw(controller.viewport.zoomTo(1));
        controller.setVisible(true);
        System.out.println("View size postvis: " + view.getSize());
        
        */
        
        }
    }

    /*
     System.out.println("The path found from " + RoadPartArray[0].name + " to " + RoadPartArray[RoadPartArray.length-1].name + " has been found. \n"
     + "Directions are: ");
     String prevRoadName = "";
     Boolean firstRoad = true;
     for (RoadPart road : RoadPartArray) {
     if (!road.name.equals(prevRoadName)) {
     if (road.name.length() == 0) {
     if (!firstRoad) {
     System.out.println("to Unknown Road Name ");
     } else {
     System.out.println("Starting from Unknown Road Name");
     }
     } else {
     prevRoadName = road.name;
     if (!firstRoad) {
     System.out.println("to " + road.name);
     } else {
     System.out.println("Starting from " + road.name);
     }
     }
     }
     firstRoad = false;
     }
     */

