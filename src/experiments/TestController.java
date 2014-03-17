package experiments;

import classes.Line;
import classes.Loader;
import classes.Model;
import classes.Rect;
import classes.RenderInstructions;
import classes.View;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import static java.lang.Math.abs;
import static java.lang.Math.round;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * The TestController class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class TestController extends JFrame implements KeyListener, 
            ActionListener, MouseListener, MouseMotionListener {
    
    private final OptimizedView view;
    private final Model model;
    private final HashMap<Integer, Boolean> keyDown;
    private final Timer timer;
    public final double wperh = 450403.8604700001 / 352136.5527900001; // map ratio
    
    // Tweakable configuration values
    private final double scrollPerFrame = 6;
    private final double fps = 30;
    private final double zoomFactor = 0.9;
    
    // Dynamic fields
    private int vx = 0;
    private int vy = 0;
    private Rect activeArea;
    private RenderInstructions ins = View.defaultInstructions;
    
    /**
     * Constructor for the TestController class
     * @param view The view to manage
     * @param model The model to manage
     */
    public TestController (OptimizedView view, Model model) {
        super();
        this.view = view;
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
        this.model = model;
        add(view);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        activeArea = model.getBoundingArea();
        view.addKeyListener(this);
        view.createImage(model.getLines(activeArea, 
                new Rect(0, 0, view.getWidth(), view.getHeight()), ins));
        
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
        view.createImage(model.getLines(activeArea, new Rect(0, 0, 
                view.getWidth(), view.getHeight()), ins));
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
        
        // Handle simple zoom
        if (e.getKeyChar() == '+') {
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
            
            // Prepare the visual changes
            Rect verArea = null;
            Rect horArea = null;
            Rect a = activeArea;
            // Create the new active rect
            Rect na = new Rect(activeArea.x-vx*upp, activeArea.y-vy*upp, 
                    activeArea.width, activeArea.height); 
            
            // Calculate the 'actual' width of the map based on the ratio
            double screenWidth = view.getHeight()*wperh;
            
            Rect verTarget = null;
            Rect horTarget = null;
            // Find out which parts of the map should be redrawn
            if (vx > 0) { // (render)Left pressed -> map goes right 
                verArea = new Rect(na.left, na.bottom, a.width, a.height);
                verTarget = new Rect(0, 0, screenWidth, view.getHeight());
            } else if (vx < 0) { // (render)Right pressed -> map goes left
                verArea = new Rect(a.right, na.bottom, a.width, a.height);
                verTarget = new Rect(screenWidth-abs(vx), 0, screenWidth, view.getHeight());
            }
            if (vy > 0) { // (render)Down -> map up
                horArea = new Rect(na.left, na.bottom, a.width, a.height);
                horTarget = new Rect(0, 0, view.getWidth(), view.getHeight());
            } else if (vy < 0) { // (render)Up -> map down
                horArea = new Rect(na.left, a.top, a.width, a.height);
                horTarget = new Rect(0, view.getHeight()-abs(vy), view.getWidth(), view.getHeight());
            }
            
            // Request the lines for the areas to be redrawn
            Line[] lines = new Line[0];
            if (verArea != null && horArea != null) {
                Line[] verLines = model.getLines(verArea, verTarget, ins);
                Line[] horLines = model.getLines(horArea, horTarget, ins);
                lines = Arrays.copyOf(verLines, verLines.length + horLines.length);
                for (int i = 0; i < horLines.length; i++) {
                    lines[verLines.length+i] = horLines[i];
                }
            } else if (verArea != null) {
                lines = model.getLines(verArea, verTarget, ins);
            } else if (horArea != null) {
                lines = model.getLines(horArea, horTarget, ins);
            }
            
            // Finalize the change to the active area
            activeArea = na;
            
            // Update the view's image
            view.offsetImage(vx, vy, lines);
        }
    }
    
       // Mouse handling 
    private Point startPos = null;
    private Point endPos = null;
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
        Rect rect = new Rect(x, y, width, height);
        System.out.println("Rect Y: ("+y+"), Rect: ("+rect+")");
        view.setMarkerRect(rect);
        view.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("RELEASE");
        view.setMarkerRect(null);
        view.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
    
    public static void main(String[] args) {
        OptimizedView view = new OptimizedView(new Dimension(600,400));
        Model model = new Model(Loader.loadIntersections("resources/intersections.txt"),
            Loader.loadRoads("resources/roads.txt"));
        
        TestController controller = new TestController(view, model);
        controller.setVisible(true);
    }
    
}
