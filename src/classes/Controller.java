/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import enums.RoadType;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;

/**
 *
 * @author Isabella
 */
public class Controller extends MouseAdapter implements KeyListener {

    private Model model;
    private View view;
    private Loader loader;
    private Rect activeArea;
    public final double wperh = 450403.8604700001 / 352136.5527900001; // map ratio

    
    /*
    x = 442254.35659
    y = 6049914.43018
    width = 352136.5527900001
    height = 352136.5527900001
    */
    
    
    public Controller() {
        Intersection[] intersecArr = Loader.loadIntersections("resources/intersections.txt");
            RoadPart[] roadPartArr = Loader.loadRoads("resources/roads.txt");
       model = new Model(intersecArr, roadPartArr);
        
        view = new View();
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
        view.addMouseWheelListener(this);
        view.addComponentListener(new ResizeHandler());

        System.out.println("View height: " + view.getHeight());

        view.addKeyListener(this);
        view.setFocusTraversalKeysEnabled(false);
        view.setFocusable(true);

        activeArea = model.getBoundingArea();
        refresh();
        
    }
    
    // Mouse handling 
    private Point startPos = null;
    private Point endPos = null;
    private Rect markerRect = null;
    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("CLICK");
        startPos = e.getLocationOnScreen();
        System.out.println("Standard startPos: "+startPos);
        startPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        System.out.println("Translated:        "+startPos);
        System.out.println("Event location:  "+e.getLocationOnScreen());
        System.out.println("Window location: "+view.getLocationOnScreen());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (startPos == null) { return; }
        System.out.println("DRAG");
        endPos = e.getLocationOnScreen();
        endPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        
        int width = Math.abs(startPos.x - endPos.x);
        int height = Math.abs(startPos.y - endPos.y);
        int x = Math.min(startPos.x, endPos.x);
        int y = Math.max(startPos.y, endPos.y);
        markerRect = new Rect(x, y, width, height);
        view.setMarkerRect(markerRect);
        view.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("RELEASE");
        /*
         ______
        |      | activeArea (kortkoordinater) 45000-88000x 350000-60000y
        |______|
         _
        |_| markerRect (skærmkoordinater) 0-1024x 0-800y
        */
        //activeArea = markerRect;
        double screenWidth = view.getHeight()*wperh;
        
        double unitsPerPixel = activeArea.width / screenWidth;
        
        Rect temp = new Rect( (markerRect.x)*unitsPerPixel , 
                ( markerRect.y) *unitsPerPixel, 
                ( markerRect.width) *unitsPerPixel, 
                (markerRect.height) *unitsPerPixel );
        activeArea = temp;
        view.setMarkerRect(null);
        refresh();
    }
    
    
    // End mouse handling

    private class ResizeHandler implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            refresh();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            
        }

        @Override
        public void componentShown(ComponentEvent e) {
            
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            
        }
        
    }
    
    /**
     * Refreshes the view according to the active area
     */
    public void refresh() {
        view.setLines(model.getLines(activeArea, 
                new Rect(0,0,view.getWidth(),view.getHeight()), View.defaultInstructions));
        view.paintComponent(view.getGraphics());
    }

    public static void main(String[] args) {
        
        //Run program
        Controller controller = new Controller();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: {
                // The focus area goes left, meaning the map will go right
                activeArea = new Rect(activeArea.x - activeArea.width/30,
                    activeArea.y, activeArea.width, activeArea.height);
                refresh();
                break;
            }
            case KeyEvent.VK_RIGHT: {
                // The focus area goes right, meaning the map will go left
                activeArea = new Rect(activeArea.x + activeArea.width/30,
                    activeArea.y, activeArea.width, activeArea.height);
                refresh();
                break;
            }
            case KeyEvent.VK_UP: {

                break;
            }

            case KeyEvent.VK_DOWN: {

                break;
            }
        }
        
        if (e.getKeyChar() == '+') {
            System.out.println("PLUS!");
            activeArea = new Rect(activeArea.x, activeArea.y, 
                    activeArea.width * 0.9, 
                    activeArea.height * 0.9);
            refresh();
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("Key released!");
    }

}
