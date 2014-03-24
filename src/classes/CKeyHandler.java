package classes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Timer;

/**
 * The CKeyHandler class handles key-presses for the controller. This involves
 * activating the basic zoom, and panning the image when the arrow keys are held
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 24-Mar-2014
 */
public class CKeyHandler implements KeyListener, ActionListener {
    private final Controller controller;
    private int vx = 0;
    private int vy = 0;
    private final HashMap<Integer, Boolean> keyDown;
    private final Timer timer;
    
    // Tweakable configuration values
    private final static double scrollPerFrame = 12;
    private final static double fps = 15;
    
    public CKeyHandler(Controller controller) {
        this.controller = controller;
        controller.getView().addKeyListener(this);
        
        // Prepare the key and update listening
        keyDown = new HashMap<>();
        keyDown.put(KeyEvent.VK_LEFT, false);
        keyDown.put(KeyEvent.VK_RIGHT, false);
        keyDown.put(KeyEvent.VK_UP, false);
        keyDown.put(KeyEvent.VK_DOWN, false);
        timer = new Timer((int)(1000/fps), this);
        timer.start();
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
            controller.zoomIn();
        }

        if (e.getKeyChar() == '-') {
            controller.zoomOut();
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

    private void shiftImage() {
       Rect activeRect = controller.getActiveRect();
       Model model = controller.getModel();
       OptimizedView view = controller.getView();
       
       // Pixels per unit
       double ppu = view.getHeight()/activeRect.height;
       double upp = 1.0 / ppu;

       int movx = vx;
       int movy = vy;
       System.out.println("Shifting by ("+movx+", "+movy+")");
       /*
       This doesn't work. Restriction is probably too cumbersome due to other
       implementations
       double activeRight = activeRect.x+(view.getWidth()*upp);
       if (vx < 0 && (activeRight+vx*upp > limitRect.right)) {
           System.out.println("Limiting RIGHT");
           System.out.println("dx = "+(activeRight-limitRect.right));
           movx = (int)Math.ceil((activeRight-limitRect.right)*ppu);
           System.out.println("Active rect: "+activeRect);
           System.out.println("Limit rect:  "+limitRect);

           System.out.println("movx "+vx+" -> "+movx);
       } else if (vx > 0 && (activeRect.left-vx*upp < limitRect.left)) {
           System.out.println("Limiting LEFT");
           movx = (int)Math.floor((activeRect.left-limitRect.left)*ppu);
           System.out.println("movx "+vx+" -> "+movx);
       }*/


       // Prepare the visual changes
       Rect verArea = null;
       Rect horArea = null;
       Rect a = activeRect;
       // Create the new active rect
       Rect na = new Rect(activeRect.x-movx*upp, activeRect.y-movy*upp, 
               activeRect.width, activeRect.height); 

       Rect verTarget = null;
       Rect horTarget = null;
       // Find out which parts of the map should be redrawn
       if (movx > 0) { // (render)Left pressed -> map goes right 
           verArea = new Rect(na.left, na.bottom, Math.abs(movx*upp), a.height); // <-- not working
           verTarget = new Rect(0, 0, view.getWidth(), view.getHeight());
       } else if (movx < 0) { // (render)Right pressed -> map goes left
           verArea = new Rect(a.right, na.bottom, Math.abs(movx*upp), a.height);
           verTarget = new Rect(view.getWidth()-abs(movx), 0, view.getWidth(), view.getHeight());
       }
       if (movy > 0) { // (render)Down -> map up
           horArea = new Rect(na.left, na.bottom, a.width, Math.abs(movy*upp)); // <-- not working
           horTarget = new Rect(0, 0, view.getWidth(), abs(movy)); // 
       } else if (movy < 0) { // (render)Up -> map down
           horArea = new Rect(na.left, a.top, a.width, Math.abs(movy*upp));
           horTarget = new Rect(0, view.getHeight()-abs(movy), view.getWidth(), abs(movy)); // 
       }

       // Request the lines for the areas to be redrawn
       ArrayList<Line> lines = new ArrayList<>();
       if (verArea != null && horArea != null) {
           lines = controller.getLines(verArea, verTarget);
           lines.addAll(controller.getLines(horArea, horTarget));
       } else if (verArea != null) {
           lines = controller.getLines(verArea, verTarget);
       } else if (horArea != null) {
           lines = controller.getLines(horArea, horTarget);
       }

       // Finalize the change to the active area
       controller.setActiveRect(na);

       // Update the view's image
       view.offsetImage(movx, movy, lines);
    }

    /**
    * Called when the timer loop ticks :3
    * @param e 
    */
   @Override
   public void actionPerformed(ActionEvent e) {
       if (vx != 0 || vy != 0) {
           shiftImage();
       }
   }
}
