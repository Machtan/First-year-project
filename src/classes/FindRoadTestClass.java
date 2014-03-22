/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 *
 * @author Alekxander
 */
public class FindRoadTestClass extends Frame implements MouseMotionListener {
    
    private double mouseX = 0, mouseY = 0;
    private Point startPos = null;
    private Point endPos = null;
    private Rect markerRect = null;
    private View view;
    private Model model = null;
    
    FindRoadTestClass() {
        addMouseMotionListener(this);
        reshape(100,100,100,100);
        Intersection[] intersecArr = Loader.loadIntersections("resources/intersections.txt");
            RoadPart[] roadPartArr = Loader.loadRoads("resources/roads.txt");
       model = new Model(intersecArr, roadPartArr);
       
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
                
        double modelX = (view.getLocationOnScreen().x/view.getWidth())*(activeArea.width/view.getWidth());
        double modelY =(1-(view.getLocationOnScreen().y)/view.getHeight());
        
        int mouseX = e.getX();
        int mouseY = e.getY();
        System.out.println("Mouse is now on position (" + mouseX + ", " + mouseY + ")");
        int sizeX = 0; // initial invariable for the width of a new rectangle.
        int sizeY = 0; // initial invariable for the heigth of a new rectangle.
        markerRect = new Rect(mouseX, mouseY, sizeX, sizeY);
        
        // KIG KIG KIG  -Kand et passe at den skal bruge en 0,0.view.getwidth, når 
        // det starter i 0,0 som er i toppen?
        
       while (model.getLines(markerRect, new Rect(0,0,view.getWidth(),view.getHeight()), model.defaultInstructions).length == 0)
            {              
                if(!(mouseX == 0 && mouseY == 0))
                    markerRect = new Rect(--mouseX, ++mouseY, (sizeX = sizeX+2), (sizeY = sizeY+2));
                
                else if(!(mouseX == 0) && mouseY == 0) 
                    markerRect = new Rect(--mouseX, ++mouseY, (sizeX = sizeX+2), ++sizeY);
                
                else if(mouseX == 0 && !(mouseY == 0))
                    markerRect = new Rect(mouseX, ++mouseY, ++sizeX, (sizeY = sizeY+2));
                
                else if(mouseX == 0 && mouseY == 0)
                    markerRect = new Rect(mouseX, ++mouseY, ++sizeX, ++sizeY);
                
            }
        
        // Her skal den returne det nærmeste objekts navn!
        // Så en item.getName() fx
                
    }
    
    public static void main(String[] args) {
        FindRoadTestClass test = new FindRoadTestClass();
    }
    

}
