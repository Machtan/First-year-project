package classes;

import enums.RoadType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * The Controller class faciliates everythign between parts of the program
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class Controller extends JFrame {
    private final OptimizedView view;
    private final Model model;
    public final double wperh = 450403.8604700001 / 352136.5527900001; // map ratio
    public ArrayList<RoadType> prioritized;
    
    // Dynamic fields
    public final Viewport viewport;
    public static final RenderInstructions defaultInstructions = new RenderInstructions();
    /**
     * Initializes the static variables
     */
    static {
        // Create the default render instructions :
        defaultInstructions.addMapping(Color.red, RoadType.Highway);
        defaultInstructions.addMapping(Color.red, RoadType.HighwayExit);
        defaultInstructions.addMapping(new Color(255,170,100), RoadType.PrimeRoute);
        defaultInstructions.addMapping(new Color(0,255,25,200), RoadType.Path);
        defaultInstructions.addMapping(Color.blue, RoadType.Ferry);
        defaultInstructions.addMapping(new Color(200,200,255), RoadType.Other);
    }
    /**
     * Constructor for the TestController class
     * @param view The view to manage
     * @param model The model to manage
     */
    public Controller(OptimizedView view, Model model) {
        super();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("First-year Project - Visualization of Denmark");
        
        this.model = model;
        viewport = new Viewport(model.bounds, 1, view);
        
        prioritized = new ArrayList<>();
        prioritized.add(RoadType.Highway);
        prioritized.add(RoadType.HighwayExit);
        prioritized.add(RoadType.PrimeRoute);
        
        this.view = view;
        
        // Set the insets / padding of the window
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        // Padding for the view (and unobstructing borders)
        JPanel viewPanel = new JPanel(new GridLayout(1,1));
        Border padding = BorderFactory.createEmptyBorder(10,0,10,10);
        Border bevel = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        viewPanel.setBorder(BorderFactory.createCompoundBorder(padding, bevel));
        viewPanel.add(view);
        
        // Key handling
        setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        new CResizeHandler(this, view);
        new CMouseHandler(this, view);
        
        contentPanel.add(viewPanel);
        contentPanel.add(new ZoomButtonsGUI(this), BorderLayout.EAST);
        /*
        contentPanel.add(new RenderPanel(ins, this), BorderLayout.NORTH);
        contentPanel.add(new FindRoadPanel(this, view), BorderLayout.SOUTH);
        */
        //contentPanel.add(new SearchStuff(), BorderLayout.WEST); //TODO compat
                
        //contentPanel.add(new RouteDescriptionPanel());
        
        // Pack the window
        this.setContentPane(contentPanel);
        this.pack();
    }
    
    /**
     * Sets the marker rect of the view to the given rect
     * @param markerRect The rect to mark
     */
    public void setMarkerRect(Rect markerRect) {
        view.setMarkerRect(markerRect);
    }
    
    /**
     * Tells the view to draw the lines of the given projection
     * @param p The projection to draw
     */
    public void draw(Viewport.Projection p) {
        view.renewImage(p);
        model.getRoads(view, p);
    }
    
    /**
     * Extends the view by the given width
     * @param deltaWidth
     */
    public void extend(int deltaWidth) {
        view.extend();
        model.getRoads(view, viewport.widen(deltaWidth));
    }
    
    /**
     * Moves the map by the given pixel values
     * @param dx The x-axis movement
     * @param dy The y-axis movement
     */
    public void moveMap(int dx, int dy) {
        view.offsetImage(dx, dy);
        for (Viewport.Projection p: viewport.movePixels(dx, dy)) {
            view.setProjection(p);
            model.getRoads(view, p);
        }
    }
    
    /**
     * Tells the model to redraw based on the activeRect
     */
    public void redraw() {
        long t1 = System.nanoTime();
        System.out.println("Executing a full redraw of the View");
        System.out.println("The projection is "+viewport.getProjection());
        view.renewImage(viewport.getProjection());
        model.getRoads(view, viewport.getProjection());
        System.out.println("- Finished! ("+(System.nanoTime()-t1)/1000000000.0+" sec) -");
    }
    
    /**
     * Entry point
     * @param args 
     */
    public static void main(String[] args) throws InterruptedException {
        ProgressBar progbar = new ProgressBar(); // Create the progress bar
        Dimension viewSize = new Dimension(600,400);
        OptimizedView view = new OptimizedView(viewSize, Controller.defaultInstructions);
        
        // Load everything with the optional progressbar on :U
        Datafile krakRoads = new Datafile("resources/roads.txt", 812301, 
            "Loading road data...");
        Datafile krakInters = new Datafile("resources/intersections.txt", 
            675902, "Loading intersection data...");
       // Model model = new Loader().loadData(progbar, krakInters, krakRoads);
        progbar.close();
        
        /*boolean loop = true;
        while (loop) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                loop = false;
            }
        }*/
        Model model = NewLoader.loadKrakData(NewLoader.krakdata);
        Controller controller = new Controller(view, model); 
        controller.setMinimumSize(new Dimension(800,600));
        controller.pack();
        System.out.println("View size previs:  "+view.getSize());
        controller.draw(controller.viewport.zoomTo(1));
        controller.setVisible(true);
        System.out.println("View size postvis: "+view.getSize());
    }
}
