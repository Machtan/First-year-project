/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import java.lang.Math;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JFrame;

/**
 *
 * @author Daniel
 */
public class RouteDescriptionPanel extends JPanel {

    private JList<String> descList;
    double roadLength;

    public RouteDescriptionPanel() {
        super();
        initComponents();
        this.setMaximumSize(new Dimension(180, 200));
        this.setPreferredSize(this.getMaximumSize());
    }

    //Reverse the resulting route
    private <T> T[] reverseArr(T[] a) {
        Collections.reverse(Arrays.asList(a));
        return a;
    }

    private void initComponents() {
        descList = new JList<>(new String[]{"No route entered yet"});
        descList.setSize(150, 400);
        descList.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
        add(descList);
    }

    private void addPart(String road, double length, DefaultListModel<String> model) {
        String message = road + " - " + length + "m";
        model.add(model.size(), message);
    }

    private double calAngle(RoadPart first, RoadPart second) {
        double angle1 = Math.atan2(first.y1() - first.y2(), first.x1() - first.x2());
        double angle2 = Math.atan2(second.y1() - second.y2(), second.x1() - second.x2());
        System.out.println("Angles " + Math.toDegrees(angle1 - angle2));
        return Math.toDegrees(angle1 - angle2);
    }

   /* if(Math.abs(angle)< Math.PI / 4)
            continue straight ahead
            else if( angle> 0)
            return turn right
    else    turn left
    */
    public void setRoute(RoadPart[] route) {
        DefaultListModel<String> model = new DefaultListModel<>();
        if (route.length == 0) {
            model.addElement("Please check a valid route");
        } else {
            model.add(model.size(), "Follow");
            String emptyS = ""; // Any wrong char
            RoadPart lastRoad = null;
            String last = emptyS;
            int length = 0;
            for (RoadPart road : route) {
                if (!road.name.equals(last)) {
                    if (!last.equals(emptyS)) {
                        addPart(last, length, model);
                        if (calAngle(lastRoad, road) < 90) {
                            model.add(model.getSize(), "Turn left onto");
                        }
                        else if(calAngle(lastRoad, road) == 90){
                            model.add(model.getSize(), "Follow the road onto");
                        }
                        else {
                            model.add(model.getSize(), "Turn right onto");
                        }
                    }
                    length = road.length();
                    last = road.name;
                    lastRoad = road;
                }
                else if(last.equals(emptyS)){
                    addPart("Unknown road", length, model);
                }
                else {
                    System.out.println("Adding the length of " + road.name + ": " + road.length());
                    length += road.length();
                }
            }
            addPart(last, length, model); // Add the last part
        }
        descList.setModel(model);
    }

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
    }
}
