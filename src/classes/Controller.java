/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import enums.RoadType;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Isabella
 */
public class Controller implements KeyListener {

    private Model model;
    private View view;
    private Loader loader;
    
    private Rect activeArea;

    
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

        System.out.println("View height: " + view.getHeight());

        view.addKeyListener(this);
        view.setFocusTraversalKeysEnabled(false);
        view.setFocusable(true);

        activeArea = model.getBoundingArea();
        refresh();
        
    }
    
    /**
     * Refreshes the view according to the active area
     */
    public void refresh() {
        view.setLines(model.getLines(activeArea, view, View.colorStuff));
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
