/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

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

    public Controller() {
        
        model = new Model(Loader.loadIntersections("resources/intersections.txt"), 
        Loader.loadRoads("resources/roads.txt"));
        
        view = new View();
        view.paintComponent(view.getGraphics());

        System.out.println("View height: " + view.getHeight());

        view.addKeyListener(this);
        view.setFocusTraversalKeysEnabled(false);
        view.setFocusable(true);

        view.setLines(model.getLines(view));
        //activeArea = model.getBoundingArea();

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

                break;
            }
            case KeyEvent.VK_RIGHT: {

                break;
            }
            case KeyEvent.VK_UP: {

                break;
            }

            case KeyEvent.VK_DOWN: {

                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("Key released!");
    }

}
