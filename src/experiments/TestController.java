package experiments;

import classes.Line;
import classes.Loader;
import classes.Model;
import classes.Rect;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * The TestController class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class TestController extends JFrame implements KeyListener, ActionListener {
    
    private final OptimizedView view;
    private final Model model;
    private final HashMap<Integer, Boolean> keyDown;
    private final double scrollPerFrame = 6;
    private final Timer timer;
    private final double fps = 30;
    private int vx = 0;
    private int vy = 0;
    private Rect activeArea;
    
    /**
     * Constructor for the TestController class
     * @param view The view to manage
     * @param model The model to manage
     */
    public TestController (OptimizedView view, Model model) {
        super();
        this.view = view;
        this.model = model;
        add(view);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        activeArea = model.getBoundingArea();
        view.addKeyListener(this);
        view.createImage(model.getLines(activeArea, view));
        
        keyDown = new HashMap<>();
        keyDown.put(KeyEvent.VK_LEFT, false);
        keyDown.put(KeyEvent.VK_RIGHT, false);
        keyDown.put(KeyEvent.VK_UP, false);
        keyDown.put(KeyEvent.VK_DOWN, false);
        timer = new Timer((int)(1000/fps), this);
        timer.start();
        
    }
    
    /**
     * Tells the model to redraw based on the activeArea
     */
    private void redraw() {
        view.createImage(model.getLines(activeArea, view));
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) { // Key Repeat => Fuck
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: {
                if (!keyDown.get(e.getKeyCode()))
                    vx += scrollPerFrame;
                break;
            }
            case KeyEvent.VK_RIGHT: {
                if (!keyDown.get(e.getKeyCode()))
                    vx -= scrollPerFrame;
                break;
            }
            case KeyEvent.VK_UP: {
                if (!keyDown.get(e.getKeyCode()))
                    vy -= scrollPerFrame;
                break;
            }

            case KeyEvent.VK_DOWN: {
                if (!keyDown.get(e.getKeyCode())) {
                    vy += scrollPerFrame;
                }
                    
                break;
            }
        }
        
        if (e.getKeyChar() == '+') {
            double zoomFactor = 0.9;
            double newWidth = activeArea.width*zoomFactor;
            double newHeight = activeArea.height*zoomFactor;
            double newX = activeArea.x + (activeArea.width-newWidth)/2;
            double newY = activeArea.y + (activeArea.height-newHeight)/2;
            System.out.println("Zooming...");
            activeArea = new Rect(newX, newY, newWidth, newHeight);
            redraw();
            System.out.println("Zoomed!");
        }
        
        // Track keypresses
        if (keyDown.containsKey(e.getKeyCode())) {
            keyDown.put(e.getKeyCode(), true);
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: {
                vx -= scrollPerFrame;
                break;
            }
            case KeyEvent.VK_RIGHT: {
                vx += scrollPerFrame;
                break;
            }
            case KeyEvent.VK_UP: {
                vy += scrollPerFrame;
                break;
            }

            case KeyEvent.VK_DOWN: {
                vy -= scrollPerFrame;
                break;
            }
        }
        // Track key releases
        if (keyDown.containsKey(e.getKeyCode())) {
            keyDown.put(e.getKeyCode(), false);
        }
    }
    
    /**
     * Called when the timer loop ticks :3
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (vx != 0 || vy != 0) {
            // Pixels per unit
            double ppu = view.getHeight()/activeArea.height;
            double upp = 1.0 / ppu;
            
            activeArea = new Rect(activeArea.x-vx*upp, activeArea.y-vy*upp, 
                    activeArea.width, activeArea.height);
            view.offsetImage(vx, vy, model.getLines(activeArea, view));
        }
    }
    
    public static void main(String[] args) {
        
        // Next level: Cached images from QuadTrees ?
        
        
        
        
        OptimizedView view = new OptimizedView(new Dimension(600,400));
        Model model = new Model(Loader.loadIntersections("resources/intersections.txt"),
            Loader.loadRoads("resources/roads.txt"));
        
        TestController controller = new TestController(view, model);
        
        controller.setVisible(true);
    }
    
}
