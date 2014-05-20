
package classes;

import enums.RoadType;
import external.SpringUtilities;
import interfaces.StreamedContainer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * The Controller class faciliates everythign between parts of the program
 *
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class Controller extends JFrame {

    private static PathFinder SP;
    private final OptimizedView view;
    private final Model model;
    public final double wperh = 450403.8604700001 / 352136.5527900001; // map ratio
    public ArrayList<RoadType> prioritized;
    public final Graph graph;
    private FindAsYouSearchPanel searchStuff;
    private JTextField inputField;
    private JList adressList;
    private DefaultListModel listModel;
    public final RouteDescriptionPanel routePanel;
    
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
        defaultInstructions.addMapping(new Color(0,255,25), RoadType.Path);
        defaultInstructions.addMapping(Color.blue, RoadType.Ferry);
        defaultInstructions.addMapping(new Color(200,200,255), RoadType.Other);
    }

    /**
     * Constructor for the TestController class
     *
     * @param view The view to manage
     * @param model The model to manage
     */
    public Controller(OptimizedView view, Model model) {
        super();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("First-year Project - Visualization of Denmark");
        
        ProgressBar progbar = new ProgressBar();
        progbar.setTarget("Creating graph", model.roadCount);
        graph = new Graph(model, NewLoader.loaded, progbar);
        //graph = null;
        progbar.close();
        //System.out.println("Graph stats: V: "+graph.V()+", E: "+graph.E());
        this.model = model;
        viewport = new Viewport(model.bounds, 1, view);

        this.view = view;

        // Set the insets / padding of the window
        final JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Padding for the view (and unobstructing borders)
        JPanel viewPanel = new JPanel(new GridLayout(1, 1));
        Border padding = BorderFactory.createEmptyBorder(10, 0, 10, 10);
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
        

        
        JPanel westContent = new JPanel();
        westContent.setLayout(new SpringLayout());
        final RouteDescriptionPanel routeDesc = new RouteDescriptionPanel();
        final AutoCompleter fromField = new AutoCompleter(model);
        final AutoCompleter toField = new AutoCompleter(model);
        
        routePanel = new RouteDescriptionPanel();
        westContent.add(fromField);
        westContent.add(toField);
        westContent.add(new JButton(new AbstractAction("Search") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Make shortest path search
                String prevRoadName = "";
                if (toField.getRoad() == null || fromField.getRoad() == null) {
                    JOptionPane.showMessageDialog(contentPanel, "Please choose two roads", "Information", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Road.Edge[] result = SP.findPath(graph, fromField.getRoad().nodes[1].id, toField.getRoad().nodes[1].id);
                    routeDesc.setRoute(result);
                }
            }
        }));
        westContent.add(routeDesc);
        SpringUtilities.makeCompactGrid(westContent, 4, 1, 0, 0, 1, 1);
        
        westContent.add(routePanel);
        SpringUtilities.makeCompactGrid(westContent, 1, 1, 0, 0, 1, 1);
        //Panel for when we want to add both FindAsYouSearchPanel and RouteDesc
        contentPanel.add(westContent, BorderLayout.WEST); 
        
        contentPanel.add(new FindRoadPanel(this, view), BorderLayout.SOUTH);
        contentPanel.add(new RenderPanel(model.priorities, this), BorderLayout.NORTH);
        contentPanel.add(new ZoomButtonsGUI(this), BorderLayout.EAST);
        contentPanel.add(westContent, BorderLayout.WEST);
        contentPanel.add(viewPanel);
        

        setTitle("First-year Project - Visualization of Denmark");

        // Pack the window
        setContentPane(contentPanel);
        pack();
    }

    /**
     * Sets the marker rect of the view to the given rect
     *
     * @param markerRect The rect to mark
     */
    public void setMarkerRect(Rect markerRect) {
        view.setMarkerRect(markerRect);
    }

    /**
     * Tells the view to draw the lines of the given projection
     *
     * @param p The projection to draw
     */
    public void draw(Viewport.Projection p) {
        view.renewImage(p);
        model.getRoads(view, p);
    }

    /**
     * Extends the view by the given width
     *
     * @param deltaWidth
     */
    public void extend(int deltaWidth) {
        view.extend();
        model.getRoads(view, viewport.widen(deltaWidth));
    }

    /**
     * Moves the map by the given pixel values
     *
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
     * Streams the roads from the given area to the target
     * @param area The area to find roads in
     * @param target Where to send them
     */
    public void streamRoads(Rect area, StreamedContainer<Road> target) {
        // The projection target is irrelevant, since we're not drawing
        model.getRoads(target, new Viewport.Projection(area, area)); 
    }

    /**
     * Entry point
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        new DatasetChooser();
    }
}
