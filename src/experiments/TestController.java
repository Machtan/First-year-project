package experiments;

import classes.Line;
import classes.Loader;
import classes.Model;
import classes.Rect;
import classes.RenderInstructions;
import classes.Utils;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import static java.lang.Math.abs;
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
public class TestController extends JFrame {
    
    private final OptimizedView view;
    private final Model model;
    private final HashMap<Integer, Boolean> keyDown;
    private final Timer timer;
    private final Timer resizeTimer;
    public final double wperh = 450403.8604700001 / 352136.5527900001; // map ratio
    private Point startPos = null;
    private Point endPos = null;
    private Rect markRect = null;
    
    
    // Tweakable configuration values
    private final static double scrollPerFrame = 12;
    private final static double fps = 15;
    private final static double zoomFactor = 0.7;
    private final static int resizeDelay = 400; // milliseconds
    
    // Dynamic fields
    private int vx = 0;
    private int vy = 0;
    private Rect activeArea;
    private RenderInstructions ins = Model.defaultInstructions;
    
    /**
     * Constructor for the TestController class
     * @param view The view to manage
     * @param model The model to manage
     */
    public TestController (OptimizedView view, Model model) {
        super();
        this.model = model;
        activeArea = model.getBoundingArea();
        
        this.view = view;
        ResizeHandler resizeHandler = new ResizeHandler();
        KeyHandler keyHandler = new KeyHandler();
        MouseHandler mouseHandler = new MouseHandler();
        view.addComponentListener(resizeHandler);
        view.addMouseListener(mouseHandler);
        view.addMouseMotionListener(mouseHandler);
        view.addKeyListener(keyHandler);
        add(view);
        
        // Prepare scaling for the view
        Dimension screenSize = Utils.convertDimension(Toolkit.getDefaultToolkit().getScreenSize());
        Rect rect = new Rect(0,0, screenSize.width, screenSize.height);
        view.createScaleSource(model.getLines(activeArea, rect, ins), screenSize);
        
        // Prepare resize handling :)
        resizeTimer = new Timer(resizeDelay, resizeHandler);
        resizeTimer.setRepeats(false);
        
        // Pack the window
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // Set the image of the view
        view.renewImage(model.getLines(activeArea, 
                new Rect(0, 0, view.getWidth(), view.getHeight()), ins));
        
        // Connect input
        keyDown = new HashMap<>();
        keyDown.put(KeyEvent.VK_LEFT, false);
        keyDown.put(KeyEvent.VK_RIGHT, false);
        keyDown.put(KeyEvent.VK_UP, false);
        keyDown.put(KeyEvent.VK_DOWN, false);
        timer = new Timer((int)(1000/fps), keyHandler);
        timer.start();
    }
    
    /**
     * Tells the model to redraw based on the activeArea
     */
    private void redraw() {
        long t1 = System.nanoTime();
        
        // 
        
        
        System.out.println("Preparing the image...");
        view.renewImage(model.getLines(activeArea, new Rect(0, 0, 
                view.getWidth(), view.getHeight()), ins));
        System.out.println("Finished! ("+(System.nanoTime()-t1)/1000000000.0+" sec)");
    }
    
    private class KeyHandler implements KeyListener, ActionListener {
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

            if (e.getKeyChar() == '-') {
                double zOutFactor = 1/zoomFactor;
                double newWidth = activeArea.width*zOutFactor;
                double newHeight = activeArea.height*zOutFactor;
                double newX = activeArea.x - (newWidth-activeArea.width)/2;
                double newY = activeArea.y - (newHeight-activeArea.height)/2;
                activeArea = new Rect(newX, newY, newWidth, newHeight);
                System.out.println("Zooming out!");
                redraw();
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
                   verArea = new Rect(na.left, na.bottom, Math.abs(vx*upp), a.height); // <-- not working
                   verTarget = new Rect(0, 0, screenWidth, view.getHeight());
               } else if (vx < 0) { // (render)Right pressed -> map goes left
                   verArea = new Rect(a.right, na.bottom, Math.abs(vx*upp), a.height);
                   verTarget = new Rect(screenWidth-abs(vx), 0, screenWidth, view.getHeight());
               }
               if (vy > 0) { // (render)Down -> map up
                   horArea = new Rect(na.left, na.bottom, a.width, Math.abs(vy*upp)); // <-- not working
                   horTarget = new Rect(0, 0, screenWidth, abs(vy)); // 
               } else if (vy < 0) { // (render)Up -> map down
                   horArea = new Rect(na.left, a.top, a.width, Math.abs(vy*upp));
                   horTarget = new Rect(0, view.getHeight()-abs(vy), screenWidth, abs(vy)); // 
               }

               // Request the lines for the areas to be redrawn
               Line[] lines = new Line[0];
               if (verArea != null && horArea != null) {
                   Line[] verLines = model.getLines(verArea, verTarget, view.getHeight(), ins);
                   Line[] horLines = model.getLines(horArea, horTarget, view.getHeight(), ins);
                   lines = Arrays.copyOf(verLines, verLines.length + horLines.length);
                   for (int i = 0; i < horLines.length; i++) {
                       lines[verLines.length+i] = horLines[i];
                   }
               } else if (verArea != null) {
                   lines = model.getLines(verArea, verTarget, view.getHeight(), ins);
               } else if (horArea != null) {
                   lines = model.getLines(horArea, horTarget, view.getHeight(), ins);
               }

               // Finalize the change to the active area
               activeArea = na;

               // Update the view's image
               view.offsetImage(vx, vy, lines);
           }
       }
    }
    private class MouseHandler implements MouseListener, MouseMotionListener {
        @Override
        public void mousePressed(MouseEvent e) {
            startPos = e.getLocationOnScreen();
            startPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (startPos == null) { return; }
            endPos = e.getLocationOnScreen();
            endPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);

            double width = Math.abs(startPos.x - endPos.x);
            double height = Math.abs(startPos.y - endPos.y);

            // Restrict the ratio
            if (width < height*wperh) { // Height is larger
                height = width/wperh; // The smaller is used
                //width = height*wperh; // The bigger is used
            } else {
                width = height*wperh; // The smaller is used
                //height = width/wperh; // The bigger is used
            }

            double x;
            if (endPos.x < startPos.x) {
                x = startPos.x-width;
            } else {
                x = startPos.x;
            }
            double y;
            if (endPos.y < startPos.y) {
                y = startPos.y;
            } else {
                y = startPos.y + height;
            }

            markRect = new Rect(x, y, width, height);
            view.setMarkerRect(markRect);
            view.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (markRect == null) { return; }

            // create a new active rect from the marker rect
            double sHeight = view.getHeight();
            double sWidth = sHeight*wperh;
            double relx = markRect.x / sWidth;
            double rely = 1 - (markRect.y / sHeight); // Invert y
            double relh = markRect.height / sHeight;
            double relw = relh; // same aspect

            double x = activeArea.x + (relx * activeArea.width);
            double y = activeArea.y + (rely * activeArea.height);
            double width = relw * activeArea.width;
            double height = relh * activeArea.height;

            Rect newArea = new Rect(x, y, width, height);

            view.setMarkerRect(null);
            activeArea = newArea;
            redraw();
        }

        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}
    }
    private class ResizeHandler implements ComponentListener, ActionListener {

        @Override
        public void componentResized(ComponentEvent e) {
            Dimension newSize = view.getSize();
            if (newSize.height == 0 || newSize.width == 0) { return; } // This cannot be resized ;)
            System.out.println("Resizing view to "+newSize);
            view.resizeMap(newSize);
            if (!resizeTimer.isRunning()) {
                resizeTimer.start();
            } else {
                System.out.println("Interrupt!");
                resizeTimer.restart();
            }
        }
        
        @Override
        public void actionPerformed(ActionEvent e) { // Once the user has finished redrawing
            System.out.println("Redrawing after resizing...");
            redraw();
        }

        @Override
        public void componentMoved(ComponentEvent e) {}
        @Override
        public void componentShown(ComponentEvent e) {}
        @Override
        public void componentHidden(ComponentEvent e) {}
    }
     
    /**
     * Entry point
     * @param args 
     */
    public static void main(String[] args) {
        OptimizedView view = new OptimizedView(new Dimension(600,400));
        Model model = new Model(Loader.loadIntersections("resources/intersections.txt"),
            Loader.loadRoads("resources/roads.txt"));
        
        TestController controller = new TestController(view, model);
        controller.setVisible(true);
    }
     
}
