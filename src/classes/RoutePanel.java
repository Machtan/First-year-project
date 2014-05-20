/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import external.SpringUtilities;
import interfaces.Receiver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 *
 * @author Daniel
 */
public class RoutePanel extends JPanel implements Receiver<Road>{
    
    private final AutoCompleter fromField;
    private final AutoCompleter toField;
    private final RouteDescriptionPanel descriptionPanel;
    final Controller con;
    
    public RoutePanel(Model model, final OptimizedView view, final Graph graph, final Controller con) {
        super(new SpringLayout());
        this.con = con;
        descriptionPanel = new RouteDescriptionPanel();
        fromField = new AutoCompleter(model, view, true);
        toField = new AutoCompleter(model, view, false);
        add(fromField);
        add(toField);
        final JPanel panel = this;
        add(new JButton(new AbstractAction("Search") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Make shortest path search
                if (toField.getRoad() == null || fromField.getRoad() == null) {
                    JOptionPane.showMessageDialog(panel, "Please choose two roads", "Information", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Road.Edge[] result = PathFinder.findPath(graph, fromField.getRoad().nodes[1].id, toField.getRoad().nodes[1].id);
                    view.setPath(result);
                    descriptionPanel.setRoute(result);
                }
            }
        }));
        add(descriptionPanel);
        SpringUtilities.makeCompactGrid(this, 4, 1, 0, 0, 1, 1);
    }
    
    private boolean pathStart;
    public void setPathStart(Road.Node start) {
        pathStart = true;
        Finder.findNearestRoad(start.x, start.y, this, con);
    }
    
    public void setPathEnd(Road.Node end) {
        pathStart = false;
        Finder.findNearestRoad(end.x, end.y, this, con);
    }
    
    public void setRoute(Road.Edge[] route) {
        descriptionPanel.setRoute(route);
    }

    @Override
    public void receive(Road obj) {
        if (pathStart) {
            fromField.setRoad(obj);
        } else {
            toField.setRoad(obj);
        }
    }
    
}
