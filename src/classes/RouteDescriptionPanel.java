/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

/**
 *
 * @author Daniel
 */
public class RouteDescriptionPanel extends JPanel {

    private JList<String> descList;
    double roadLength;
    private DefaultListModel<String> model;

    public RouteDescriptionPanel() {
        super();

        initComponents();
        model = new DefaultListModel<>();
        //this.setMaximumSize(new Dimension(180, 500));
        //this.setPreferredSize(new Dimension(180,400));
    }

    //Reverse the resulting route
    private <T> T[] reverseArr(T[] a) {
        Collections.reverse(Arrays.asList(a));
        return a;
    }

    private void initComponents() {
        descList = new JList<>(new String[]{"No route entered yet"});
        //descList.setSize(180, 500);
        descList.setFont(new Font("Lucida", Font.PLAIN, 12));
        descList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollP = new JScrollPane(
                descList, 
                VERTICAL_SCROLLBAR_AS_NEEDED, 
                HORIZONTAL_SCROLLBAR_NEVER
        );
        Dimension size = new Dimension(240, 400);
        //scrollP.setMaximumSize(new Dimension(180,400));
        scrollP.setPreferredSize(size);
        scrollP.setSize(size);
        add(scrollP);
        this.setSize(size);
    }

    private double calAngle(Road.Edge first, Road.Edge second) {
        double angle1 = Math.atan2(first.p1.y - first.p2.y, first.p1.x - first.p2.x);
        double angle2 = Math.atan2(second.p1.y - second.p2.y, second.p1.x - second.p2.x);
        System.out.println("Angles " + Math.toDegrees(angle1 - angle2));
        return Math.toDegrees(angle1 - angle2);
    }
    
    private String timeString(double totalTime) {
        totalTime = Math.ceil(totalTime);
        int hrs = (int)Math.floor(totalTime / 60);
        int mins = (int)Math.floor(totalTime - hrs*60);
        return ((hrs > 0)? hrs+" hrs ": " ") + ((mins > 0)? mins+" min": "");
    }
    
    private String lengthString(double length) {
        length = Math.round(length);
        return ((length > 1000)?(length/1000) + " km": ((int)length)+" m");
    }

    public void setRoute(Road.Edge[] route) {
        if (route.length == 0) {
            model.addElement("Please check a valid route");
        } else {
            model.clear();
            model.add(model.size(), "Start on "+route[0].parent().name);

            //double totalLength = 0;
            String wrongC = ""; // Any wrong char
            Road.Edge lastRoad = null;
            String last = wrongC;
            String lastDesc = "";
            double length = 0;
            double totalLength = 0;
            double totalTime = 0;
            for (Road.Edge road : route) {
                totalLength += road.length();
                totalTime += road.driveTime;
                if (!road.parent().name.equals(last)) {
                    if (!last.equals(wrongC)) {
                        model.addElement(lastDesc);
                        model.addElement("Drive for "+lengthString(length));
                        String turnDesc;
                        if (calAngle(lastRoad, road) < 90) {
                            turnDesc = "Turn left onto";
                        } else if (calAngle(lastRoad, road) > 90) {
                            turnDesc = "Turn right onto";
                        } else {
                            turnDesc = "Follow the road onto";
                        }
                        lastDesc = turnDesc+" "+road.parent().name;
                    }
                    length = road.length();
                    last = road.parent().name;
                    lastRoad = road;
                } else if (last.equals("")) {
                    model.addElement("Follow the unnamed road");
                    model.addElement("Drive for "+lengthString(road.length()));
                    last = road.parent().name;
                    lastRoad = road;
                } else {
                    System.out.println("Adding the length of " + road.parent().name + ": " + road.length());
                    length += road.length();
                }
            }
            model.addElement(lastDesc);
            model.addElement("Drive for "+lengthString(length)); // Add the last part
            model.addElement("Arrival");
            model.addElement("Length: "+lengthString(totalLength)+", "+timeString(totalTime));
            //String totL;
            //addPart("Total length ", totalLength - route[route.length-1].getLength());
        }
        descList.setModel(model);
        repaint();
    }
    /*
     public static void main(String[] args) {
     ArrayList<RoadPart> testRoads = new ArrayList<>();
     Intersection i1 = new Intersection("0,0,0");
     for (int i = 0; i < 10; i++) {
     int j = 0;
     for (String name : new String[]{"Kildevej", "Rodevej", "Pærevej"}) {
     RoadPart road = new RoadPart("0,0,0," + name + ",0,0,0,0,,,,,0,0,0,80," + i * 3 + ",0,,,");
     Intersection i2 = new Intersection("1,0," + i * 3 + j);
     road.setPoints(i1, i2);
     testRoads.add(road);
     j++;
     System.out.println(road.driveTime * (road.speedLimit / 3.6));
     }
     }
     RoadPart[] roads = testRoads.toArray(new RoadPart[0]);

     RouteDescriptionPanel routePanel = new RouteDescriptionPanel();
     routePanel.setRoute(roads);
     JFrame frame = new JFrame();
     frame.add(routePanel);
     frame.pack();
     frame.setVisible(true);
     }*/
}
