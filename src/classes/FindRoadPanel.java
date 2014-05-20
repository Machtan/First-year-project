package classes;

import interfaces.Receiver;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
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
public class FindRoadPanel extends JPanel implements MouseMotionListener, Receiver<Road> {

    private final JPanel view;
    private final Controller controller;
    private JLabel roadLabel;
    private JLabel coordLabel;
    private String coordFString = "x/y: %10.1f, %10.1f";
    private Point mousePosition;
    private float mapX;
    private float mapY;
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
        coordLabel = new JLabel("x/y: undefined, undefined");
        add(coordLabel, BorderLayout.EAST);
    }

    /**
     * Set field roadLabel to Sring name. If given string is empty, set
     * roadLabel to UNKNOWN.
     * @param name of road
     * @param p Where the mouse is
     */
    public void setNearestRoad(String name) {
        String roadName = (name.equals(""))? "UKNOWN": name;
        roadLabel.setText(description + roadName);
        coordLabel.setText(String.format(coordFString, mapX, mapY));
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
        mousePosition = e.getLocationOnScreen();
        if (!view.isShowing()) {
            return;
        }
        if ((view.getHeight() < 20) || (view.getWidth() < 20)) {
            return; // Don't attempt to find a road if the view is too small
        }
        mousePosition.translate(-view.getLocationOnScreen().x, -view.getLocationOnScreen().y);
        
        // Retrieve active rect area
        Viewport port = controller.viewport;

        // Change position from screen coordinates to map coordinates.
        // Create a new small Rect with the mouseposition as midpoint with mapcoordinates.
        mapX = port.getMapX(mousePosition.x);
        mapY = port.getMapY(mousePosition.y);
        Finder.findNearestRoad(mapX, mapY, this, controller);
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void receive(Road road) {
        setNearestRoad(road.name);
    }
}
