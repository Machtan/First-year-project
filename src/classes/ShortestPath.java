package classes;

import java.util.HashMap;

public class ShortestPath {

    private HashMap<Integer, RoadPart> edgeTo;
    private HashMap<Integer, Double> distTo;
    private IndexMinPQ<Double> pq;
    private Graph G;

    private static double h(Intersection source, Intersection target) { //s = start, t = target
        // System.out.println("Heuristic analysis: \n"
        //         + "Intersection source: " + source + ". Intersection target: " + target);
        return Math.sqrt(Math.pow(source.x - target.x, 2) + Math.pow(source.y - target.y, 2));
    }

    public RoadPart[] findPath(long sourceID, long targetID) {
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
                for (RoadPart r : G.adj(v)) {
                    //System.out.println("- (" + v + ") Relaxing part " + r);
                    relax(r, v, t);
                }
            }

            //System.out.println("Checked edges:");
            //  for (int key : edgeTo.keySet()) {
            //System.out.println("- "+key+": "+edgeTo.get(key));
            //   }

            HashMap<Integer, RoadPart> path = new HashMap<>();
            int current = G.getIntersectionIndex(targetID);
            int i = 0;
            // System.out.println("Building edgeTo");
            // System.out.println(edgeTo.toString());
            while (current != startIndex) {
                RoadPart currentRoad = edgeTo.get(current);
                path.put(i++, currentRoad);
                //System.out.println("Checking the other intersection than "+current+" of "+currentRoad);
                //System.out.println("Calling other() with: "+currentRoad+ " and current: " +current);
                current = G.other(currentRoad, current);
            }
            RoadPart[] result = new RoadPart[path.size()];
            for (int j = 0; j < path.size(); j++) {
                result[j] = path.get(path.size() - j - 1);
            }
            return result;
        }
    }

    public ShortestPath(Graph G) {
        this.G = G;
    }

    public void relax(RoadPart r, int s, int t) {
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
        /*   Intersection first = new Intersection("100,10,2");
         Intersection second = new Intersection("10,15,3");
         Intersection third = new Intersection("20,27,14");
         Intersection fourth = new Intersection("30,12,6");
         Intersection fifth = new Intersection("40,4,1");
         Intersection sixth = new Intersection("1,15,4");
         Intersection seventh = new Intersection("18,25,7");

         RoadPart rfirst = new RoadPart("100,30,0,rfirst,0,0,0,0,,,,,0,0,0,0,0,1,,,");
         RoadPart rsecond = new RoadPart("30,20,0,rsecond,0,0,0,0,,,,,0,0,0,0,0,1,,,");
         RoadPart rthird = new RoadPart("30,10,0,rthird,0,0,0,0,,,,,0,0,0,0,0,1,,,");
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
         }*/
        ProgressBar progbar = new ProgressBar();
        Datafile krakRoads = new Datafile("resources/roads.txt", 812301,
                "Loading road data...");
        Datafile krakInters = new Datafile("resources/intersections.txt",
                675902, "Loading intersection data...");
        Model model = new Loader().loadData(progbar, krakInters, krakRoads);
        progbar.close();


        RoadPart[] roadTemp = model.getRoads(model.getBoundingArea());
        for (RoadPart r : roadTemp) {
            //System.out.println("Roads: " +r.name);
            r.setPoints(r.p1, r.p2);
        }

        Graph graph = new Graph(model.intersectionCount(), roadTemp);

        ShortestPath SP = new ShortestPath(graph);

        RoadPart[] RoadPartArray = SP.findPath(603585, 659617);
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
    }
}
