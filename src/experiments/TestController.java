package experiments;

import classes.Line;
import classes.Loader;
import classes.Model;
import classes.Rect;
import classes.RenderInstructions;
import enums.RoadType;
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
import java.util.ArrayList;
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
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public final double wperh = 450403.8604700001 / 352136.5527900001; // map ratio
    private Rect limitRect;
    private Point startPos = null;
    private Point endPos = null;
    private Rect markRect = null;
    private Dimension prevSize;
    private Dimension startResizeSize; // The size when a resize is started
    private ArrayList<RoadType> prioritized;
    
    // Tweakable configuration values
    private final static double scrollPerFrame = 12;
    private final static double fps = 15;
    private final static double zoomFactor = 0.7;
    private final static int resizeDelay = 400; // milliseconds
    private final static int margin = 40; // The amount of pixels to load to the right when resizing
    
    // Dynamic fields
    private int vx = 0;
    private int vy = 0;
    private Rect activeRect;
    private Rect lastArea;
    private RenderInstructions ins = Model.defaultInstructions;
    
    /**
     * Constructor for the TestController class
     * @param view The view to manage
     * @param model The model to manage
     */
    public TestController (OptimizedView view, Model model) {
        super();
        this.model = model;
        activeRect = model.getBoundingArea();
        
        double lw = activeRect.width * 1.2;
        double lx = activeRect.x - 0.1 * activeRect.width;
        double lh = activeRect.height * 1.2;
        double ly = activeRect.y - 0.1 * activeRect.height;
        limitRect = new Rect(lx, ly, lw, lh);
        System.out.println("Active area: "+activeRect);
        System.out.println("Limit rect:  "+limitRect);
        
        prioritized = new ArrayList<>();
        prioritized.add(RoadType.Highway);
        prioritized.add(RoadType.HighwayExit);
        prioritized.add(RoadType.PrimeRoute);
        
        this.view = view;
        ResizeHandler resizeHandler = new ResizeHandler();
        KeyHandler keyHandler = new KeyHandler();
        MouseHandler mouseHandler = new MouseHandler();
        view.addComponentListener(resizeHandler);
        view.addMouseListener(mouseHandler);
        view.addMouseMotionListener(mouseHandler);
        view.addKeyListener(keyHandler);
        add(view);
        
        // Prepare resize handling :)
        resizeTimer = new Timer(resizeDelay, resizeHandler);
        resizeTimer.setRepeats(false);
        
        // Pack the window
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        resizeActiveArea(view.getSize());
        prevSize = view.getSize(); // prepare for scaling
        
        // Set the image of the view
        view.renewImage(model.getLines(activeRect, 
                new Rect(0, 0, view.getWidth(), view.getHeight()), ins, 
                prioritized));
        
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
     * Returns the controller's active rect
     * @return the controller's active rect
     */
    public Rect getActiveRect() {
        return activeRect;
    }
    
    /**
     * Returns the controller's active model
     * @return the controller's active model
     */
    public Model getModel() {
        return model;
    }
    
    public void resizeActiveArea(Dimension dim) {
        double height = activeRect.height;
        double width = (dim.width/(double)dim.height) * height;
        Rect newArea = new Rect(activeRect.x, activeRect.y, width, height);
        System.out.println("Resizing active area from "+activeRect+" to "+newArea);
        activeRect = newArea;
    }
    
    /**
     * Tells the model to redraw based on the activeRect
     */
    private void redraw() {
        long t1 = System.nanoTime();
        
        // Change the active Rect so that it fits the screen
        resizeActiveArea(view.getSize());
        lastArea = activeRect;
        
        System.out.println("Preparing the image...");
        view.renewImage(model.getLines(activeRect, new Rect(0, 0, 
                view.getWidth(), view.getHeight()), ins, prioritized));
        System.out.println("Finished! ("+(System.nanoTime()-t1)/1000000000.0+" sec)");
    }
    
    public void zoomOut() {
        System.out.println("Zooming out!");
        double zOutFactor = 1/zoomFactor;
        double newWidth = activeRect.width*zOutFactor;
        double newHeight = activeRect.height*zOutFactor;
        double newX = activeRect.x - (newWidth-activeRect.width)/2;
        double newY = activeRect.y - (newHeight-activeRect.height)/2;
        if (newHeight > limitRect.height) {
            newHeight = limitRect.height;
            newWidth = limitRect.width;
            newX = limitRect.x;
            newY = limitRect.y;
            System.out.println("Restricted the zooming out");
        }
        
        if (activeRect.height != limitRect.height) {
            activeRect = new Rect(newX, newY, newWidth, newHeight);
            redraw();
        } else {
            System.out.println("No size change, ignoring...");
        }
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
                double newWidth = activeRect.width*zoomFactor;
                double newHeight = activeRect.height*zoomFactor;
                double newX = activeRect.x + (activeRect.width-newWidth)/2;
                double newY = activeRect.y + (activeRect.height-newHeight)/2;
                System.out.println("Zooming...");
                activeRect = new Rect(newX, newY, newWidth, newHeight);
                redraw();
                System.out.println("Zoomed!");
            }

            if (e.getKeyChar() == '-') {
                zoomOut();
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
           // Pixels per unit
           double ppu = view.getHeight()/activeRect.height;
           double upp = 1.0 / ppu;
           
           int movx = vx;
           int movy = vy;
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
               lines = model.getLines(verArea, verTarget, view.getHeight(), ins, prioritized);
               lines.addAll(model.getLines(horArea, horTarget, view.getHeight(), ins, prioritized));
           } else if (verArea != null) {
               lines = model.getLines(verArea, verTarget, view.getHeight(), ins, prioritized);
           } else if (horArea != null) {
               lines = model.getLines(horArea, horTarget, view.getHeight(), ins, prioritized);
           }

           // Finalize the change to the active area
           activeRect = na;

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
    private class MouseHandler implements MouseListener, MouseMotionListener {
        @Override
        public void mousePressed(MouseEvent e) {
            startPos = e.getLocationOnScreen();
            startPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
            markRect = new Rect(startPos.x, startPos.y, 0, 0);
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
            double rHeight = markRect.height;
            double rWidth = markRect.width;
            double rX = markRect.x;
            double rY = markRect.y;
            if (rHeight < 15) { // Default zoom-ish
                rHeight = 120;
                rWidth = rHeight*wperh;
                rX = markRect.x - (rWidth - (markRect.width))/2;
                rY = markRect.y + (rHeight - (markRect.height))/2;
            } 
            
            // create a new active rect from the marker rect
            double relx = rX      / view.getWidth();
            double rely = 1 - (rY / view.getHeight()); // Invert y
            double relh = rHeight / view.getHeight();
            double relw = rWidth  / view.getWidth(); // same aspect

            double x = activeRect.x + (relx * activeRect.width);
            double y = activeRect.y + (rely * activeRect.height);
            double width = relw * activeRect.width;
            double height = relh * activeRect.height;

            Rect newArea = new Rect(x, y, width, height);

            view.setMarkerRect(null);
            activeRect = newArea;
            lastArea = newArea;
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
            if (prevSize == null) { return; } // You're too fast ;)
            if (!view.initialized()) { return; } // You're still too fast ;)
            Dimension newSize = view.getSize();
            if (newSize.height == 0 || newSize.width == 0) { return; } // This cannot be resized ;)
            if (newSize.height != prevSize.height) {
                view.resizeMap(newSize);
                prevSize = newSize;
            } else if (newSize.width > Math.min(view.getSourceWidth()-margin, screenSize.width)) { // The windows is wider now
                int prevRightLimit = view.getSourceWidth();
                int newRightLimit = Math.max(prevRightLimit+margin, view.getWidth()+margin);

                System.out.println("Moving the right limit to "+newRightLimit);
                resizeActiveArea(newSize);

                double sx = lastArea.right;
                double sy = lastArea.y;
                double sw = (newRightLimit-prevRightLimit) * (activeRect.width / view.getWidth());
                double sh = lastArea.height;
                Rect source = new Rect(sx, sy, sw, sh);
                //System.out.println("-> Source: "+source);

                double tx = prevRightLimit;
                double ty = 0;
                double tw = newRightLimit;
                double th = newSize.height;
                Rect target = new Rect(tx, ty, tw, th);
                //System.out.println("-> Target: "+target);

                // Update the image to show the new content ;)
                view.offsetImage(0, 0, model.getLines(source, target, ins, prioritized), 
                        new Dimension(newRightLimit, view.getHeight()));
                lastArea = source;
            }
            if (!resizeTimer.isRunning()) {
                startResizeSize = view.getSize();
                resizeTimer.start();
            } else {
                resizeTimer.restart();
            }
        }
        
        @Override
        public void actionPerformed(ActionEvent e) { // Once the user has finished redrawing
            // Only redraw if it is needed, plx
            if (view.getHeight() != startResizeSize.height) {
                System.out.println("Redrawing after resizing...");
                redraw();
            }
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
