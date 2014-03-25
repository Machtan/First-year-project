/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.Dimension;
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
    private final Controller controller;
    private static final int width = 100; // initial invariable for the width of a new rectangle.
    private static final int height = 100; // initial invariable for the heigth of a new rectangle.
    
    FindRoadTestClass(JPanel panel, Controller controller) {
        this.controller = controller;
        view = panel;
        view.addMouseMotionListener(this);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {}
    
    @Override
    public void mouseMoved(MouseEvent e) {
        // Mouse cursor on screen.
        Point cPos = e.getLocationOnScreen();   
        cPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);      
        
        // Retrieve active rect area
        Rect activeRect = controller.getActiveRect();
        double x = cPos.x; 
        double y = cPos.y; 
        
        // Change position from screen coordinates to map coordinates.
        // Create a new small Rect with the mouseposition as midpoint with mapcoordinates.
        double modelX = activeRect.x + (x/view.getWidth())*activeRect.width;
        double modelY = activeRect.y + (1-(y/view.getHeight()))*activeRect.height;
        Rect cursorRect = new Rect(modelX-width/2, modelY-height/2, width, height);
        
        // Get a HashSet containing RoadParts within the cursorRect.
        Model model = controller.getModel();
        HashSet<RoadPart> roads = model.getRoads(cursorRect);
  
        // If no RoadParts are found within the area, double size of cursorRect until 
        // at least one has been found.
        while (roads.isEmpty()) {

            double rectX = cursorRect.x-cursorRect.width/2;
            double rectY = cursorRect.y-cursorRect.height/2;
            double rectWidth = cursorRect.width*2;
            double rectHeight = cursorRect.height*2;
            
            cursorRect = new Rect(rectX, rectY, rectWidth, rectHeight);
            roads = model.getRoads(cursorRect);
        }
        
        // Put RoadPart elements into ArrayList.
        ArrayList<RoadPart> roadArray = new ArrayList<>();
        for(RoadPart r : roads) {
            roadArray.add(r);
        }
        
        // Calculate distance from mouse coordinates to all the RoadParts found.
        double[] sizes = new double[roadArray.size()];
        for(int i = 0; i<roadArray.size(); i++) {
            double areaX1 = roadArray.get(i).getRect().x;
            double areaY1 = roadArray.get(i).getRect().y;
            double areaX2 = (roadArray.get(i).getRect().x)+(roadArray.get(i).getRect().width);//(roadArray.get(0).getRect().width);
            double areaY2 = (roadArray.get(i).getRect().y)+(roadArray.get(i).getRect().height);  
            
            double distance = pointToLineDistance(areaX1, areaY1, areaX2, areaY2, modelX, modelY); 
            sizes[i] = distance;
        }
        
        // Find out which RoadPart is closest to the mouse coordinates.
        int currentDis;
        int smallestDis = -1;
        for(int i = 0; i<roadArray.size(); i++) {
            if(smallestDis == -1) {
                smallestDis = i; }
            else {
                currentDis = i; 
                if(sizes[i] < sizes[smallestDis]) {
                    smallestDis = i;
                }
            }     
        }
        
        // Print out name of the RoadPart that is found to be closest to the mouse.
        RoadPart r = roadArray.get(smallestDis);
        System.out.println(r.name);
        
        // For DEBUGGING.
        // Get the rect that the RoadPart is contained within, and draw it
        // on the map to see which is selected. Refreshes when mouse is moved.
        // Rect roadRect = r.getRect();
        // double mx = (roadRect.x - activeRect.x)/activeRect.width * view.getWidth();
        // double my = view.getHeight()-((roadRect.y - activeRect.y)/activeRect.height * view.getHeight());
        // double mw = (roadRect.width / activeRect.width) * view.getWidth();
        // double mh = (roadRect.height / activeRect.height) * view.getHeight();
        //Rect markerRect = new Rect(mx, my, mw, mh);
        //controller.getView().setMarkerRect(markerRect);
        //controller.refresh();
    }
    
        /**
         * private method to calculate the distance from a point P(pX, pY)
         * to the middle of a line with ends at point A(aX, aY) and point B(bX, bY).
         * @param double a = (aX, aY).
         * @param double b = (bX, bY).
         * @param double p = (pX, pY).
         * @return double distance.
         */
    private double pointToLineDistance(
        double aX, double aY, double bX, double bY, double pX, double pY) {
        //return Math.abs((bX-aX)*(aY-pY)-(aX-pX)*(bY-aY)) / Math.sqrt((bX-aX)*(bX-aX)+(bY-aY)*(bY-aY));
        
        double centerx, centery;
        centerx = aX+(bX-aX)/2;
        centery = aY+(bY-aY)/2;
        return Math.sqrt((pX-centerx)*(pX-centerx)+(pY-centery)*(pY-centery));
       }
    
    public static void main(String[] args) {
        ProgressBar.open();
        OptimizedView view = new OptimizedView(new Dimension(600,400));
        Model model = new Model(Loader.loadIntersections("resources/intersections.txt"),
        Loader.loadRoads("resources/roads.txt"));
        Controller controller = new Controller(view, model);
        ProgressBar.close();
        FindRoadTestClass test = new FindRoadTestClass(view, controller);
        controller.setVisible(true);
    }
}
