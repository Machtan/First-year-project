package classes;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * The FindRoadPanel class works as a panel and makes it possible to show the
 * road that is closest to the cursor on the map.
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

        this.controller = controller;
        target.addMouseMotionListener(this);
        view = target;

        Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border bevel = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        setBorder(BorderFactory.createCompoundBorder(bevel, padding));
        roadLabel = new JLabel(description + "Undefined");
        add(roadLabel, BorderLayout.WEST);
    }

    /**
     * Set field roadLabel to Sring name. If given string is empty, set
     * roadLabel to UNKNOWN.
     *
     * @param name of road
     */
    public void setNearestRoad(String name, float x, float y) {
        if (!name.equals("")) {
            roadLabel.setText(description + name + " x :" + x + " y : " + y);
        } else {
            roadLabel.setText(description + "UNKNOWN, x : " + x + " y : " + y);
        }
    }

    /**
     * private method to calculate the distance from a point P(pX, pY) to the
     * middle of a line with ends at point A(aX, aY) and point B(bX, bY).
     *
     * @param float a = (aX, aY).
     * @param float b = (bX, bY).
     * @param float p = (pX, pY).
     * @return float distance.
     */
    private float pointToLineDistance(Point2D.Float a, Point2D.Float b, Point2D.Float mouse) {
        Point2D.Float c = new Point2D.Float(mouse.x - a.x, mouse.y - a.y);
        Point2D.Float ab = new Point2D.Float(b.x - a.x, b.y - a.y);
        float length = (float) Math.sqrt(ab.x*ab.x + ab.y*ab.y);
        Point2D.Float unit = new Point2D.Float(ab.x / length, ab.y / length);
        float dot = unit.x * c.x + unit.y * c.y;
        
        if (dot < 0) {
            return (float) Math.sqrt((mouse.x - a.x) * (mouse.x - a.x) + (mouse.y - a.y) * (mouse.y - a.y));
        } else if (dot > length) {
            return (float) Math.sqrt((mouse.x - b.x) * (mouse.x - b.x) + (mouse.y - b.y) * (mouse.y - b.y));
        } else {
            Point2D.Float unitDot = new Point2D.Float(unit.x * dot, unit.y * dot);
            Point2D.Float point = new Point2D.Float(a.x + unitDot.x, a.y + unitDot.y);
            return (float) Math.sqrt((mouse.x - point.x) * (mouse.x - point.x) + (mouse.y - point.y) * (mouse.y - point.y));
        }
    }

    public Road.Edge closestRoad(float x, float y) {
        Rect cursorRect = new Rect(x - width / 2, y - height / 2, width, height);

        // Get a HashSet containing RoadParts within the cursorRect.
        RoadPart[] roads = controller.getRoads(cursorRect);

        // If no RoadParts are found within the area, float size of cursorRect until 
        // at least one has been found.
        while (roads.length == 0) {

            float rectX = cursorRect.x - cursorRect.width / 2;
            float rectY = cursorRect.y - cursorRect.height / 2;
            float rectWidth = cursorRect.width * 2;
            float rectHeight = cursorRect.height * 2;

            cursorRect = new Rect(rectX, rectY, rectWidth, rectHeight);
            roads = controller.getRoads(cursorRect);
        }

        // Calculate distance from mouse coordinates to all the RoadParts found.
        double minDist = Integer.MAX_VALUE;
        RoadPart nearest = null;
        for (RoadPart road : roads) {
            float distance = pointToLineDistance(
                    new Point2D.Float(road.x1(), road.y1()),
                    new Point2D.Float(road.x2(), road.y2()),
                    new Point2D.Float(x, y)
            );
            if (distance < minDist) {
                nearest = road;
                minDist = distance;
            }
        }
        return nearest;
    }

    /**
     * Find the road closest to the mousecursor. Is called whenever the cursor
     * moves.
     *
     * @param e mousemovement.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        // Mouse cursor on screen.
        Point cPos = e.getLocationOnScreen();
        if (!view.isShowing()) {
            return;
        }
        if ((view.getHeight() < 20) || (view.getWidth() < 20)) {
            return; // Don't attempt to find a road if the view is too small
        }
        cPos.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);

        // Retrieve active rect area
        Viewport port = controller.viewport;

        // Change position from screen coordinates to map coordinates.
        // Create a new small Rect with the mouseposition as midpoint with mapcoordinates.
        float x = port.getMapX(cPos.x);
        float y = port.getMapY(cPos.y);

        RoadPart closest = closestRoad(x, y);

        setNearestRoad(closest.name, x, y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Do nothing 
    }
}
