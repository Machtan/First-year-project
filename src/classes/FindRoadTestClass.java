/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import experiments.Controller;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


/**
 *
 * @author Alekxander
 */
public class FindRoadTestClass implements MouseMotionListener {
    private final JPanel view;
    private final Controller controller;
    private static final int width = 100; // initial invariable for the width of a new rectangle.
    private static final int height = 100; // initial invariable for the heigth of a new rectangle.
    
    FindRoadTestClass(JPanel panel, Controller controller) {
        this.controller = controller;
        view = panel;
        panel.addMouseMotionListener(this);
        /*Intersection[] intersecArr = Loader.loadIntersections("resources/intersections.txt");
            RoadPart[] roadPartArr = Loader.loadRoads("resources/roads.txt");
        model = new Model(intersecArr, roadPartArr);*/
       
    }
    
    public Point getCoordinates(){
    return MouseInfo.getPointerInfo().getLocation();   
    }

    @Override
    public void mouseDragged(MouseEvent e) { }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        Point cPos = e.getLocationOnScreen();   
        System.out.println("position on screen : " + cPos);
        cPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        System.out.println("position on map : " + cPos);         
        
        // All values should be between 100 and 300
        Rect activeRect = controller.getActiveRect(); //temporarily changed for testing
        double x = cPos.x; //- view.getLocationOnScreen().x;
        double y = cPos.y; //- view.getLocationOnScreen().y;
        
        double modelX = activeRect.x + (x/view.getWidth())*activeRect.width;
        double modelY = activeRect.y + (1-(y/view.getHeight()))*activeRect.height;
        
        System.out.println("Mouse is now on position (" + x + ", " + y + ")");
        System.out.println("The model position is    ("+modelX+", "+modelY+")");
        
        
        Rect cursorRect = new Rect(x-width/2, y-height/2, width, height);
        
        Model model = controller.getModel();
        HashSet<RoadPart> roads = model.getRoads(cursorRect);
        while (roads.isEmpty()) {
            double rectX = x-cursorRect.width;
            double rectY = y-cursorRect.height;
            double rectWidth = cursorRect.width*2;
            double rectHeight = cursorRect.height*2;
            cursorRect = new Rect(rectX, rectY, rectWidth, rectHeight);
            roads = model.getRoads(cursorRect);
        }
        
        // Her skal den returne det nærmeste objekts navn!
        // Så en item.getName() fx
                
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(600,400));
        FindRoadTestClass test = new FindRoadTestClass(panel);
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
