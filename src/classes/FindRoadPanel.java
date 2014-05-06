package classes;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;


/**
 * The FindRoadPanel class works as a panel and makes it possible to show 
 * the road that is closest to the cursor on the map.
 * @author Daniel
 * @author Jakob
 * @author Isabella
 * @author Alekxander
 */
public class FindRoadPanel extends JPanel implements MouseMotionListener {
    private final JPanel view;
    private final Controller controller;
    private static final int width = 100; // initial invariable for the width of a new rectangle.
    private static final int height = 100; // initial invariable for the heigth of a new rectangle.
    private JLabel roadLabel;
    private final String description = "Nearest road: ";
    
    FindRoadPanel(Controller controller, JPanel target) {
        super(new BorderLayout());
        Border padding = BorderFactory.createEmptyBorder(5,5,5,5);
        Border bevel = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        setBorder(BorderFactory.createCompoundBorder(bevel, padding));
        roadLabel = new JLabel(description + "Undefined");
        add(roadLabel, BorderLayout.WEST);
        this.controller = controller;;
        target.addMouseMotionListener(this);
        view = target;
    }
    
    /**
     * Set field roadLabel to Sring name.
     * If given string is empty, set roadLabel to UNKNOWN.
     * @param name of road
     */
    public void setNearestRoad(String name) {
        if (!name.equals("")) {
            roadLabel.setText(description + name);
        } else {
            roadLabel.setText(description + "UNKNOWN");
        }
    }
    
    /**
    * private method to calculate the distance from a point P(pX, pY)
    * to the middle of a line with ends at point A(aX, aY) and point B(bX, bY).
    * @param double a = (aX, aY).
    * @param double b = (bX, bY).
    * @param double p = (pX, pY).
    * @return double distance.
    */
    private double pointToLineDistance( double aX, double aY, double bX, double bY, double pX, double pY) {
        //return Math.abs((bX-aX)*(aY-pY)-(aX-pX)*(bY-aY)) / Math.sqrt((bX-aX)*(bX-aX)+(bY-aY)*(bY-aY));
        
        double centerx, centery;
        centerx = aX+(bX-aX)/2;
        centery = aY+(bY-aY)/2;
        return Math.sqrt((pX-centerx)*(pX-centerx)+(pY-centery)*(pY-centery));
       }
    
    /**
     * Find the road closest to the mousecursor. Is called whenever
     * the cursor moves.
     * @param e mousemovement.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        // Mouse cursor on screen.
        Point cPos = e.getLocationOnScreen();  
        if (!view.isShowing()) { return; }
        cPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);      
        
        // Retrieve active rect area
        Viewport port = controller.viewport;
        
        // Change position from screen coordinates to map coordinates.
        // Create a new small Rect with the mouseposition as midpoint with mapcoordinates.
        double x = port.getMapX(cPos.x);
        double y = port.getMapY(cPos.y);
        Rect cursorRect = new Rect(x-width/2, y-height/2, width, height);
        
        // Get a HashSet containing RoadParts within the cursorRect.
        RoadPart[] roads = controller.getRoads(cursorRect);
  
        // If no RoadParts are found within the area, double size of cursorRect until 
        // at least one has been found.
        while (roads.length == 0) {

            double rectX = cursorRect.x-cursorRect.width/2;
            double rectY = cursorRect.y-cursorRect.height/2;
            double rectWidth = cursorRect.width*2;
            double rectHeight = cursorRect.height*2;
            
            cursorRect = new Rect(rectX, rectY, rectWidth, rectHeight);
            roads = controller.getRoads(cursorRect);
        }
        
        // Calculate distance from mouse coordinates to all the RoadParts found.
        double minDist = Integer.MAX_VALUE;
        RoadPart nearest = null;
        for(RoadPart road : roads) {
            Rect r = road.getRect();
            double areaX1 = r.x;
            double areaY1 = r.y;
            double areaX2 = r.x + r.width;
            double areaY2 = r.y + r.height;  
            
            double distance = pointToLineDistance(areaX1, areaY1, areaX2, areaY2, x, y); 
            if (distance < minDist) {
                nearest = road;
                minDist = distance;
            }
        }

        setNearestRoad(nearest.name);

        /* Can be used if needed for DEBUGGING purposes.
        Draw the rect containing the nearest RoadPart r.
        
        Rect roadRect = r.getRect();
        double mx = (roadRect.x - activeRect.x)/activeRect.width * view.getWidth();
        double my = view.getHeight()-((roadRect.y - activeRect.y)/activeRect.height * view.getHeight());
        double mw = (roadRect.width / activeRect.width) * view.getWidth();
        double mh = (roadRect.height / activeRect.height) * view.getHeight();
        Rect markerRect = new Rect(mx, my, mw, mh);
        controller.getView().setMarkerRect(markerRect);
        controller.refresh();
        */
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
            // Do nothing 
    }
}
