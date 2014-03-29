package experiments;

import classes.Intersection;
import classes.Loader;
import classes.Model;
import classes.ProgressBar;
import classes.QuadTree;
import classes.Rect;
import classes.RenderInstructions;
import classes.RoadPart;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The Optimizations class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 26-Mar-2014
 */
public class Optimizations {
    
    private static double avg(Collection<Double> col) {
        double sum = 0.0;
        for (Double d : col) {
            sum += d;
        }
        sum /= col.size();
        return sum;
    }
    
    private static void testTree(QuadTree tree, int reps, Rect area) {
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
        //testTree(tree, reps, area);
        testModel(model, reps, area, new Rect(0,0,500,500));
    }
    
}
