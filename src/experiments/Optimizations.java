package experiments;

import classes.Loader;
import classes.Model;
import classes.ProgressBar;
import classes.QuadTree;
import classes.Rect;
import classes.RoadPart;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * The Optimizations class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 26-Mar-2014
 */
public class Optimizations {
    
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
            times.add((System.nanoTime()-t1)/1000000000.0);
        }
        System.out.println("Average: "+avg(times)+"s");
    }
    
    public static void main(String[] args) {
        ProgressBar prog = new ProgressBar();
        Model model = new Model(Loader.loadIntersections("resources/intersections.txt", prog),
            Loader.loadRoads("resources/roads.txt", prog), prog);
        prog.close();
        QuadTree tree = model.getTree();
        Rect area = model.getBoundingArea();
        int reps = 10;
        
        testTree(tree, new HashSet<RoadPart>(), reps, area);
        testTree(tree, new ArrayList<RoadPart>(), reps, area);
        testTree(tree, new ArrayList<RoadPart>(100000), reps, area);
    }
    
}
