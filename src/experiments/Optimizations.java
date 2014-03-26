package experiments;

import classes.Intersection;
import classes.Line;
import classes.Loader;
import classes.Model;
import classes.OptimizedView;
import classes.ProgressBar;
import classes.QuadTree;
import classes.Rect;
import classes.RenderInstructions;
import classes.RoadPart;
import enums.RoadType;
import interfaces.IProgressBar;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.JFrame;

/**
 * The Optimizations class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 26-Mar-2014
 */
public class Optimizations {
    private static class NewRoadPart extends RoadPart {
        private Intersection p1;
        private Intersection p2;
        public NewRoadPart(String line) {
            super(line);
        }
        
        public void setPoints(Intersection p1, Intersection p2) {
            this.p1 = p1;
            this.p2 = p2;
            double x = Math.min(p1.x, p2.x);
            double y = Math.min(p1.y, p2.y);
            double width = Math.abs(p1.x - p2.x);
            double height = Math.abs(p1.y - p2.y);
            area = new Rect(x,y,width,height);
        }
        
        public Line asLine(double x1, double y1, Rect target, double ppu, RenderInstructions ins) {
            Line line = new Line(
                    target.x+(p1.x-x1)*ppu, 
                    target.height-(target.y+(p1.y-y1)*ppu), 
                    target.x+(p2.x-x1)*ppu, 
                    target.height-(target.y+(p2.y-y1)*ppu), 
                    ins.getColor(type)
            );
            return line;
        }
    }
    
    public static Rect getRect(Intersection one, Intersection two) {
        double x = Math.min(one.x, two.x);
        double y = Math.min(one.y, two.y);
        double width = Math.abs(one.x - two.x);
        double height = Math.abs(one.y - two.y);
        return new Rect(x,y,width,height);
    }
    
    private static class NewModel {
        private QuadTree<NewRoadPart> tree;
        private HashMap<Integer, Intersection> inMap;
        private Rect bounds;
        public NewModel(Collection<Intersection> ins, Collection<RoadPart> roads, IProgressBar... bar) {
            IProgressBar progbar = null; // Optional progress bar
            if (bar.length != 0) { 
                progbar = bar[0]; 
                progbar.setTarget("Populating Quad Tree...", 812301);
            }
                // Find the bounding area of the intersections
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;

            inMap = new HashMap<>();
            for (Intersection i : ins) {
                inMap.put(i.id, i); // Add intersections to the map
                minX = Math.min(i.x, minX);
                maxX = Math.max(i.x, maxX);
                minY = Math.min(i.y, minY);
                maxY = Math.max(i.y, maxY);
            }

            // Create the quad tree
            bounds = new Rect(minX, minY, maxX-minX, maxY-minY);
            tree = new QuadTree<>(bounds, 400, 15);

            // Fill the quad tree
            long t1 = System.nanoTime();
            if (progbar != null) {
                for (RoadPart part : roads) {
                    NewRoadPart road = new NewRoadPart(part.toString());
                    road.setPoints(inMap.get(part.sourceID), inMap.get(part.targetID));
                    tree.add(road);
                    progbar.update(1);
                }
            } else {
                for (RoadPart part : roads) {
                    NewRoadPart road = new NewRoadPart(part.toString());
                    road.setPoints(inMap.get(part.sourceID), inMap.get(part.targetID));
                    tree.add(road);
                }
            }

            double secs = (System.nanoTime()-t1)/1000000000.0;
            System.out.println("Finished!");
            System.out.println("Populating the tree took "+secs+" seconds");
        }
        
        public Collection<Line> getLines(Rect area, Rect target, double windowHeight, 
                RenderInstructions instructions, List<RoadType> prioritized) {

            HashSet<RoadType> types = instructions.getRenderedTypes();
            ArrayList<NewRoadPart> roads = new ArrayList<>();
            tree.fillSelectedFromRect(area, roads, types);
            ArrayList<Line> lines = new ArrayList<>(roads.size());

            // Prepare the prioritized lists
            HashMap<RoadType, ArrayList<Line>> prioLines = new HashMap<>();
            for (RoadType type : prioritized) {
                prioLines.put(type, new ArrayList<Line>());
            }
            
            double ppu = target.height / area.height;
            double x1 = area.x;
            double y1 = area.y;
            for(NewRoadPart road: roads) {
                if (instructions.getColor(road.type) == instructions.getVoidColor()) {
                    continue; // Ignore undrawn roads
                }

                // Create the line to be drawn
                Line line = road.asLine(x1, y1, target, ppu, instructions);

                // Prioritize if needed
                if (prioritized.contains(road.type)) {
                    prioLines.get(road.type).add(line);
                } else {
                    lines.add(line);
                }
            }

            // Add the prioritized lines in order
            for (int i = prioLines.size()-1; i >= 0; i--) {
                lines.addAll(prioLines.get(prioritized.get(i)));
            }

            return lines;
        }
    }

    
    
    private static void test(int a, int... is) {
        System.out.println("a: "+a);
        System.out.println("is:"+is);
    }
    
    private static double avg(Collection<Double> col) {
        double sum = 0.0;
        for (Double d : col) {
            sum += d;
        }
        sum /= col.size();
        return sum;
    }
    
    private static void testTree(QuadTree tree, Collection col, int reps, Rect area) {
        System.out.println("Testing "+col.getClass()+" "+reps+" times");
        ArrayList<Double> times = new ArrayList<>();
        for (int i = 0; i < reps; i++) {
            long t1 = System.nanoTime();
            tree.fillFromRect(area, new ArrayList<RoadPart>());
            times.add((System.nanoTime()-t1)/1e9);
        }
        System.out.println("Average: "+avg(times)+"s");
    }
    
    private static void testModel(Object model, int reps, Rect area, Rect target) {
        NewModel nm = null;
        Model m = null;
        if (NewModel.class.isInstance(model)) {
            nm = (NewModel)model;
        } else {
            m = (Model)model;
        }
        RenderInstructions ins = Model.defaultInstructions;
        ArrayList<Double> times = new ArrayList<>();
        for (int i = 0; i < reps; i++) {
            long t1 = System.nanoTime();
            if (nm != null) {
                nm.getLines(area, target, target.height, ins, new ArrayList<RoadType>());
            } else {
                m.getLines(area, target, ins);
            }
            times.add((System.nanoTime()-t1)/1e9);
        }
        System.out.println("Average: "+avg(times)+"s");
    }
    
    public static void main(String[] args) {
        ProgressBar prog = new ProgressBar();
        Collection<Intersection> ins = Loader.loadIntersections("resources/intersections.txt", prog);
        Collection<RoadPart> roads = Loader.loadRoads("resources/roads.txt", prog);
        Model model = new Model(ins, roads, prog);
        NewModel newModel = new NewModel(ins, roads, prog);
        prog.close();
        QuadTree tree = model.getTree();
        Rect area = model.getBoundingArea();
        int reps = 10;
        
        testTree(tree, new ArrayList<RoadPart>(), reps, area);
        testTree(tree, new HashSet<RoadPart>(), reps, area);
        testTree(tree, new ArrayList<RoadPart>(100000), reps, area);
        
        OptimizedView view = new OptimizedView(new Dimension(600,500));
        JFrame frame = new JFrame();
        frame.add(view);
        frame.setMinimumSize(new Dimension(600,500));
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        RenderInstructions instr = Model.defaultInstructions;
        Rect target = new Rect(0,0,view.getWidth(), view.getHeight());
        view.renewImage(newModel.getLines(area, target, reps, instr, new ArrayList<RoadType>()));
        //testModel(model, 10, area, new Rect(0,0,500,500));
        //testModel(newModel, 10, area, new Rect(0,0,500,500));
        //testModel(model, 10, area, new Rect(0,0,500,500));
        //testModel(newModel, 10, area, new Rect(0,0,500,500));
    }
    
}
