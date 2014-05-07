package classes;

import enums.RoadType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
    private SearchStuff searchStuff;
    private JTextField inputField;
    private JList adressList;
    private DefaultListModel listModel;
    
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
        contentPanel.add(new SearchStuff(model.getRoads(model.getBoundingArea())), 
                BorderLayout.WEST);
        
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
    
    /**
     * Tells the view to draw the lines of the given projection
     * @param p The projection to draw
     */
    public void draw(Viewport.Projection p) {
        view.renewImage(model.getLines(viewport.getProjection(), view.getHeight(), ins, prioritized));
    }
    
    /**
     * Moves the map by the given pixel values
     * @param dx The x-axis movement
     * @param dy The y-axis movement
     */
    public void moveMap(int dx, int dy) {
        ArrayList<Line[]> lines = new ArrayList<>();
        for (Viewport.Projection p: viewport.movePixels(dx, dy)) {
            lines.add(model.getLines(p, view.getHeight(), ins, prioritized));
        }
        view.offsetImage(dx, dy, lines.toArray(new Line[lines.size()][]));
    }
    
    /**
     * Returns the roads with the given area
     * @param area The area to look in
     * @return A list of roads in the area
     */
    public RoadPart[] getRoads(Rect area){
        return model.getRoads(area, ins);
    }
    
    /**
     * Tells the model to redraw based on the activeRect
     */
    public void redraw() {
        long t1 = System.nanoTime();
        System.out.println("Executing a full redraw of the View");
        view.renewImage(model.getLines(viewport.getProjection(), view.getHeight(), ins, prioritized));
        System.out.println("- Finished! ("+(System.nanoTime()-t1)/1000000000.0+" sec) -");
    }
    
    /**
     * Entry point
     * @param args 
     */
    public static void main(String[] args) throws InterruptedException {
        ProgressBar progbar = new ProgressBar(); // Create the progress bar
        Dimension viewSize = new Dimension(600,400);
        OptimizedView view = new OptimizedView(viewSize);
        
        // Load everything with the optional progressbar on :U
        Datafile krakRoads = new Datafile("resources/roads.txt", 812301, 
            "Loading road data...");
        Datafile krakInters = new Datafile("resources/intersections.txt", 
            675902, "Loading intersection data...");
        Model model = new Loader().loadData(progbar, krakInters, krakRoads);
        
        Controller controller = new Controller(view, model); 
        controller.setMinimumSize(new Dimension(800,600));
        controller.pack();
        progbar.close();
        
        controller.draw(controller.viewport.getProjection(viewSize));
        controller.setVisible(true);
    }
}
