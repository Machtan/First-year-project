package classes;

import enums.RoadType;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;

public class ShortestPath {

    private HashMap<Integer, Road.Edge> edgeTo;
    private HashMap<Integer, Double> distTo;
    private IndexMinPQ<Double> pq;
    private Graph G;

    private static double h(Road.Node source, Road.Node target) { //s = start, t = target
        // System.out.println("Heuristic analysis: \n"
        //         + "Intersection source: " + source + ". Intersection target: " + target);
        return Math.sqrt(Math.pow(source.x - target.x, 2) + Math.pow(source.y - target.y, 2)) / 1000 / 130;
    }

    public Road.Edge[] findPath(long sourceID, long targetID) {
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
                    relax(r, v, t);
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

    public ShortestPath(Graph G) {
        this.G = G;
    }

    public void relax(Road.Edge r, int s, int t) {
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

        Road.Node first = new Road.Node(1,5,2);
        Road.Node second = new Road.Node(2,6,7);
        Road.Node third = new Road.Node(3,8,6);
        Road.Node fourth = new Road.Node(4,9,3);
        Road.Node fifth = new Road.Node(5,10,5);
        Road.Node sixth = new Road.Node(6,12,1);
        Road.Node seventh = new Road.Node(7,13,5);
        Road.Node eighth = new Road.Node(8,16,11);
        Road.Node ninth = new Road.Node(9,16,7);
        
        Road.Node[] nodes = new Road.Node[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth};
        Rect rect = new Rect(100,100,100,100);
        
        Road.Node[] nodesForA = new Road.Node[]{first, second};
        float[] timeA = new float[]{NewLoader.getDriveTime(first, second, (short)80)};
        Road roadA = new Road("a", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForA, timeA, rect); 
        Road.Edge EdgeA = new Road.Edge(first, second);

        Road.Node[] nodesForB = new Road.Node[]{second, third};
        float[] timeB = new float[]{NewLoader.getDriveTime(second, third, (short)80)};
       // Road roadB = new Road("b", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForB, timeB, rect);
        
        Road.Node[] nodesForC = new Road.Node[]{second, eighth};
        float[] timeC = new float[]{NewLoader.getDriveTime(second, eighth, (short)80)};
       // Road roadC = new Road("c", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForC, timeC, rect);
        
        Road.Node[] nodesForD = new Road.Node[]{eighth, fifth};
        float[] timeD = new float[]{NewLoader.getDriveTime(eighth, fifth, (short)80)};
       // Road roadD = new Road("d", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForD, timeD, rect);
     
        Road.Node[] nodesForE = new Road.Node[]{fifth, seventh};
        float[] timeE = new float[]{NewLoader.getDriveTime(fifth, seventh, (short)80)};
       // Road roadE = new Road("e", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForE, timeE, rect);
       
        Road.Node[] nodesForF = new Road.Node[]{seventh, ninth};
        float[] timeF = new float[]{NewLoader.getDriveTime(seventh, ninth, (short)80)};
      //  Road roadF = new Road("f", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForF, timeF, rect);
        
        Road.Node[] nodesForG = new Road.Node[]{fifth, fourth};
        float[] timeG = new float[]{NewLoader.getDriveTime(fifth, fourth, (short)80)};
      //  Road roadG = new Road("g", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForG, timeG, rect);
       
        Road.Node[] nodesForH = new Road.Node[]{fourth, sixth};
        float[] timeH = new float[]{NewLoader.getDriveTime(fourth, sixth, (short)80)};
      //  Road roadH = new Road("h", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForH, timeH, rect);
       
        Road.Node[] nodesForI = new Road.Node[]{first, fourth};
        float[] timeI = new float[]{NewLoader.getDriveTime(first, fourth, (short)80)};
      //  Road roadI = new Road("i", RoadType.PrimeRoute, (short)2600, (short)80, false, nodesForI, timeI, rect);
           /*
        RoadPart a = new RoadPart("1,2,0,rfirst,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        Road.Edge b = new Road.Edge("2,3,0,rsecond,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        Road.Edge c = new Road.Edge("2,8,0,rthird,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        Road.Edge d = new Road.Edge("8,5,0,rfourth,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        Road.Edge e = new Road.Edge("5,7,0,rfith,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        Road.Edge f = new Road.Edge("7,9,0,rsixth,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        Road.Edge g = new Road.Edge("5,4,0,rseventh,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        Road.Edge h = new Road.Edge("4,6,0,rfirst,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        Road.Edge i = new Road.Edge("1,4,0,rfirst,0,0,0,0,,,,,0,0,0,0,0,1,,,");
        
        roads = new Edge[]{a, b, c, d, e ,f, g, h, i};
        * */
        
        Road[] r = new Road[]{roadA, roadB, roadC, roadD, roadE, roadF, roadG, roadH, roadI};
        /*
        a.setPoints(first, second);
        b.setPoints(second, third);
        c.setPoints(second, eighth);
        d.setPoints(eighth, fifth);
        e.setPoints(fifth, ninth);
        f.setPoints(seventh, ninth);
        g.setPoints(fifth, fourth);
        h.setPoints(fourth, sixth);
        i.setPoints(first, fourth);

         
        
        g1 = new Graph(9, r);
        */
        Model m = new Model(rect);
        Graph g = new Graph(m);
        ShortestPath SP = new ShortestPath(g);
        Road.Edge[] result = SP.findPath(1, 8);
        for(Road.Edge road : result) {
            System.out.println(road.parent().name);
        }
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
            Dimension viewSize = new Dimension(600, 400);
            OptimizedView view = new OptimizedView(viewSize, Controller.defaultInstructions);
            progbar.close();

            Model model = NewLoader.loadData(NewLoader.krakdata);
            Controller controller = new Controller(view, model);
            controller.setMinimumSize(new Dimension(800, 600));

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
