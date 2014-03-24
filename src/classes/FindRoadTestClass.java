/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import experiments.OptimizedView;
import experiments.TestController;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JPanel;


/**
 *
 * @author Alekxander
 */
public class FindRoadTestClass implements MouseMotionListener{
    private final JPanel view;
    private final TestController controller;
    private static final double width = 0.01; // initial invariable for the width of a new rectangle.
    private static final double height = 0.01; // initial invariable for the heigth of a new rectangle.
    
    FindRoadTestClass(JPanel panel, TestController controller) {
        this.controller = controller;
        view = panel;
        view.addMouseMotionListener(this);
    }
    
    // DEBUGGING
    @Override
    public void mouseDragged(MouseEvent e) { 
        System.out.println("Dragged");}
    
    @Override
    public void mouseMoved(MouseEvent e) {
      //  System.out.println("Mouse Moved");
        Point cPos = e.getLocationOnScreen();   
       // System.out.println("position on screen : " + cPos);
        cPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
       // System.out.println("position on map : " + cPos);         
        
        Rect activeRect = controller.getActiveRect(); //temporarily changed for testing
        double x = cPos.x; //- view.getLocationOnScreen().x;
        double y = cPos.y; //- view.getLocationOnScreen().y;
        
        double modelX = activeRect.x + (x/view.getWidth())*activeRect.width;
        double modelY = activeRect.y + (1-(y/view.getHeight()))*activeRect.height;
        
      //  System.out.println("Mouse is now on position (" + x + ", " + y + ")");
       // System.out.println("The model position is    ("+modelX+", "+modelY+")");
        
        
        Rect cursorRect = new Rect(modelX-width/2, modelY-height/2, width, height);
       // System.out.println("Before while lopp");
        Model model = controller.getModel();
        HashSet<RoadPart> roads = model.getRoads(cursorRect);
        double rectCoordinate = width; // Could also have been height, doesnt matter.
        // Perhaps we should make the fields width and height into one, they do the same I think.
        double j = cursorRect.height-height; // default size of width and height for incoming loops' rect.
        double startWidth = cursorRect.width;
        double startHeight = cursorRect.height;
        while (roads.isEmpty()) {
            //System.out.println("While loop started !!!!!!!!!!!!!!!!!!!!!!");
            //System.out.println("x: " + cursorRect.x + "y: " + cursorRect.y);
            double rectX = cursorRect.x-cursorRect.width*2;
            double rectY = cursorRect.y-cursorRect.height*2;
            double rectWidth = cursorRect.x-j;
            double rectHeight = cursorRect.y-j;
            j=j*2;
            /*
            double rectX = cursorRect.x-cursorRect.width;
            double rectY = cursorRect.y-cursorRect.height;
            double rectWidth = cursorRect.width*2;
            double rectHeight = cursorRect.height*2;
            */
            cursorRect = new Rect(rectX, rectY, rectWidth, rectHeight);
            roads = model.getRoads(cursorRect);
        }
        
        //System.out.println("After while loop");
        //Incoming code is just an iteration. Its an experiment. 
        ArrayList<RoadPart> roadArray = new ArrayList<>();
        for(RoadPart r : roads) {
            roadArray.add(r);
        }
        
        
        // Now it has to check which road is cloasest to the cursorCoordinates
        double[] sizes = new double[roadArray.size()];
        
        for(int i = 0; i<roadArray.size(); i++) {
            double areaX1 = roadArray.get(i).getRect().x;
            double areaY1 = roadArray.get(i).getRect().y;
            double areaX2 = (roadArray.get(i).getRect().x)-(roadArray.get(0).getRect().width);
            double areaY2 = (roadArray.get(i).getRect().y)-(roadArray.get(0).getRect().height);  
            
            double distance = pointToLineDistance(areaX1, areaY1, areaX2, areaY2, modelX, modelY); 
            sizes[i] = distance;
        }
        
        int currentDis;
        int smallestDis = -1;
        
        for(int i = 0; i<roadArray.size(); i++) {
            if(smallestDis == -1) {
                smallestDis = i; }
            else {
                currentDis = i; 
                if(sizes[i] < smallestDis) {
                    smallestDis = i;
                }
            }     
        }
        
        RoadPart r = roadArray.get(smallestDis);
        
        System.out.println(r.name);
    }
    
    public double pointToLineDistance(
        double Ax, double Ay, double Bx, double By, double Px, double Py) {
        double normalLength = Math.sqrt((Bx-Ax)*(Bx-Ax)+(By-Ay)*(By-Ay));
        return Math.abs((Px-Ax)*(By-Ay)-(Py-Ay)*(Bx-Ax))/normalLength;
  }
    
    public static void main(String[] args) {
        OptimizedView view = new OptimizedView(new Dimension(600,400));
        Model model = new Model(Loader.loadIntersections("resources/intersections.txt"),
        Loader.loadRoads("resources/roads.txt"));
        TestController controller = new TestController(view, model);
        controller.setVisible(true);
        FindRoadTestClass test = new FindRoadTestClass(view, controller);
    }
}
