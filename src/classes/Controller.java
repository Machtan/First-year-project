package classes;

import enums.RoadType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The Controller class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class Controller extends JFrame {
    private final OptimizedView view;
    private final Model model;
    private final CMouseHandler mouseHandler;
    private final CKeyHandler keyHandler;
    private final CResizeHandler resizeHandler;
    public final double wperh = 450403.8604700001 / 352136.5527900001; // map ratio
    private ArrayList<RoadType> prioritized;
    
    // Dynamic fields
    private Rect activeRect;
    private RenderInstructions ins;
    
    /**
     * Constructor for the TestController class
     * @param view The view to manage
     * @param model The model to manage
     */
    public Controller(OptimizedView view, Model model) {
        super();
        this.model = model;
        activeRect = model.getBoundingArea();
        this.ins = Model.defaultInstructions;
        
        prioritized = new ArrayList<>();
        prioritized.add(RoadType.Highway);
        prioritized.add(RoadType.HighwayExit);
        prioritized.add(RoadType.PrimeRoute);
        
        this.view = view;
        resizeHandler = new CResizeHandler(this);
        
        // Key handling (This might need fixing)
        setFocusTraversalKeysEnabled(false);
        setFocusable(true);
        keyHandler = new CKeyHandler(this);
        
        mouseHandler = new CMouseHandler(this);
        add(new RenderPanel(ins, this), BorderLayout.NORTH);
        add(new ZoomButtonsGUI(resizeHandler, this), BorderLayout.EAST);
        add(view);
        
        setTitle("Førsteårsprojekt - Danmarkskort");
        
        // Pack the window
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        resizeActiveArea(view.getSize());
        
        // Set the image of the view
        view.renewImage(model.getLines(activeRect, 
                new Rect(0, 0, view.getWidth(), view.getHeight()), ins, 
                prioritized));
        
    }
    
    /**
     * Returns the lines of the given area mapped to the target
     * @param area The area to get roads from
     * @param target The target to map them to as lines
     * @return A list of lines to render the area
     */
    public ArrayList<Line> getLines(Rect area, Rect target) {
        return model.getLines(area, target, view.getHeight(), ins, prioritized);
    }
    
    /**
     * Returns the controller's active rect
     * @return the controller's active rect
     */
    public Rect getActiveRect() {
        return activeRect;
    }
    
    /**
     * Refreshes the view
     */
    public void refresh() {
        view.repaint();
    }
    
    /**
     * Sets the active rect of the controller
     * @param rect The new active rect of the controller
     */
    public void setActiveRect(Rect rect) {
        activeRect = rect;
    }
    
    /**
     * Returns the controller's active model
     * @return the controller's active model
     */
    public Model getModel() {
        return model;
    }
    
    /**
     * Returns the controller's view instance
     * @return the controller's view instance
     */
    public OptimizedView getView() {
        return view;
    }
    
    public void resizeActiveArea(Dimension dim) {
        double height = activeRect.height;
        double width = (dim.width/(double)dim.height) * height;
        Rect newArea = new Rect(activeRect.x, activeRect.y, width, height);
        System.out.println("Resizing active area from "+activeRect+" to "+newArea);
        activeRect = newArea;
    }
    
    public void resetView() {
        setActiveRect(model.getBoundingArea());
        redraw();
    }
    
    /**
     * Zooms the view out
     */
    public void zoomOut() {
        System.out.println("Zoomin' out!");
        resizeHandler.zoomOut();
    }
    
    /**
     * Zoom the view in
     */
    public void zoomIn() {
        System.out.println("Zoomin' in!");
        resizeHandler.zoomIn();
    }
    
    /**
     * Tells the model to redraw based on the activeRect
     */
    public void redraw() {
        long t1 = System.nanoTime();
        
        // Change the active Rect so that it fits the screen
        resizeActiveArea(view.getSize());
        resizeHandler.setLastRect(activeRect);
        
        System.out.println("Preparing the image...");
        view.renewImage(model.getLines(activeRect, new Rect(0, 0, 
                view.getWidth(), view.getHeight()), ins, prioritized));
        System.out.println("Finished! ("+(System.nanoTime()-t1)/1000000000.0+" sec)");
    }
     
    /**
     * Entry point
     * @param args 
     */
    public static void main(String[] args) {
        ProgressBar.open(); // Create the progress bar
        OptimizedView view = new OptimizedView(new Dimension(600,400));
        
        Model model = new Model(Loader.loadIntersections("resources/intersections.txt"),
            Loader.loadRoads("resources/roads.txt"));
        
        Controller controller = new Controller(view, model); 
        ProgressBar.close();
        controller.setVisible(true);
    }
}
