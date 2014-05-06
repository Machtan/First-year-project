package classes;

import enums.RoadType;
import java.awt.BorderLayout;
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
    private final CMouseHandler mouseHandler;
    /*private final CKeyHandler keyHandler;
    private final CResizeHandler resizeHandler;*/
    public final double wperh = 450403.8604700001 / 352136.5527900001; // map ratio
    private ArrayList<RoadType> prioritized;
    
    // Dynamic fields
    public Viewport viewport;
    private RenderInstructions ins;
    
    /**
     * Constructor for the TestController class
     * @param view The view to manage
     * @param model The model to manage
     */
    public Controller(OptimizedView view, Model model) {
        super();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.model = model;
        viewport = new Viewport(model.getBoundingArea(), 1, view);
        this.ins = Model.defaultInstructions;
        
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
        setFocusable(true);
        /*keyHandler = new CKeyHandler(this, view);
        resizeHandler = new CResizeHandler(this, view);*/
        mouseHandler = new CMouseHandler(this, view);
        
        contentPanel.add(new RenderPanel(ins, this), BorderLayout.NORTH);
        contentPanel.add(new ZoomButtonsGUI(this), BorderLayout.EAST);
        contentPanel.add(viewPanel);
        contentPanel.add(new FindRoadPanel(this, view), BorderLayout.SOUTH);
        
        setTitle("First-year Project - Visualization of Denmark");
        
        // Pack the window
        this.setContentPane(contentPanel);
    }
    
    /**
     * Sets the marker rect of the view to the given rect
     * @param markerRect The rect to mark
     */
    public void setMarkerRect(Rect markerRect) {
        view.setMarkerRect(markerRect);
    }
    
    public void draw(Viewport.Projection p) {
        view.renewImage(model.getLines(viewport.getProjection(), ins, prioritized));
    }
    
    public RoadPart[] getRoads(Rect area){
        return model.getRoads(area, ins);
    }
    
    /**
     * Tells the model to redraw based on the activeRect
     */
    public void redraw() {
        long t1 = System.nanoTime();
        System.out.println("Preparing the image...");
        view.renewImage(model.getLines(viewport.getProjection(), ins, prioritized));
        System.out.println("Finished! ("+(System.nanoTime()-t1)/1000000000.0+" sec)");
    }
    
    /**
     * Entry point
     * @param args 
     */
    public static void main(String[] args) throws InterruptedException {
        ProgressBar progbar = new ProgressBar(); // Create the progress bar
        OptimizedView view = new OptimizedView(new Dimension(600,400));
        
        // Load everything with the optional progressbar on :U
        Datafile krakRoads = new Datafile("resources/roads.txt", 812301, 
            "Loading road data...");
        Datafile krakInters = new Datafile("resources/intersections.txt", 
            675902, "Loading intersection data...");
        Model model = new Loader().loadData(progbar, krakInters, krakRoads);
        
        Controller controller = new Controller(view, model); 
        controller.setMinimumSize(new Dimension(600,500));
        controller.pack();
        controller.redraw();
        progbar.close();
        controller.setVisible(true);
    }
}
