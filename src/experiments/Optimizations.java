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
            tree.getIn(area);
            times.add((System.nanoTime()-t1)/1e9);
        }
        System.out.println("Average: "+avg(times)+"s");
    }
    
    private static void testModel(Model model, int reps, Rect area, Rect target) {
        RenderInstructions ins = Model.defaultInstructions;
        ArrayList<Double> times = new ArrayList<>();
        for (int i = 0; i < reps; i++) {
            long t1 = System.nanoTime();
            model.getLines(area, target, ins);
            times.add((System.nanoTime()-t1)/1e9);
        }
        System.out.println("Average: "+avg(times)+"s");
    }
    
    public static void arrayTest(int[] inArr) {
        inArr[inArr.length-1] = inArr[0];
    }
    
    public static void main(String[] args) {
        /* --- Arrays are apparently mutable :)
        int[] intArr = new int[]{1, 2, 3};
        System.out.println("intArr:");
        for (int i : intArr) { System.out.println("- "+i); }
        arrayTest(intArr);
        System.out.println("intArr:");
        for (int i : intArr) { System.out.println("- "+i); }*/
        
        
        ProgressBar prog = new ProgressBar();
        Intersection[] ins = Loader.loadIntersections("resources/intersections.txt", prog);
        RoadPart[] roads = Loader.loadRoads("resources/roads.txt", prog);
        Model model = new Model(ins, roads, prog);
        //NewModel newModel = new NewModel(ins, roads, prog);
        prog.close();
        
        Rect area = model.getBoundingArea();
        int reps = 10;
        
        // Roughly 0.15s on average
        QuadTree tree = model.getTree();
        testTree(tree, new ArrayList<RoadPart>(), reps, area);
        testTree(tree, new HashSet<RoadPart>(), reps, area);
        testTree(tree, new ArrayList<RoadPart>(100000), reps, area);
        testTree(tree, new HashSet<RoadPart>(), reps, area);
        
        testModel(model, reps, area, new Rect(0,0,500,500));
        //testModel(newModel, 10, area, new Rect(0,0,500,500));
        testModel(model, reps, area, new Rect(0,0,500,500));
        //testModel(newModel, 10, area, new Rect(0,0,500,500));*/
    }
    
}
