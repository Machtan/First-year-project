/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import java.lang.Math;
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
    }
    
    private void initComponents() {
        descList = new JList<>(new String[]{"No route entered yet"});
        descList.setSize(150, 400);
        add(descList);
    }
    
    private void addPart(String road, double length, DefaultListModel<String> model) {
        String message = "Follow " + road + " for " + length + "m";
        model.add(model.size(), message);
    }
    
    public void setRoute(RoadPart[] route) {
        DefaultListModel<String> model = new DefaultListModel<>();
        if (route.length == 0) {
            model.addElement("Please check a valid route");
        } else {
            String wrongChar = "!"; // Any wrong char
            String last = wrongChar; 
            int length = 0;
            for (RoadPart road : route) {
                if (!road.name.equals(last)) {
                    if (!last.equals(wrongChar)) {
                        addPart(last, length, model);
                    }
                    length = road.length();
                    last = road.name;
                } else {
                    System.out.println("Adding the length of "+road.name+": "+road.length());
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
                RoadPart road = new RoadPart("0,0,0,"+name+",0,0,0,0,,,,,0,0,0,80,"+i*3+",0,,,");
                Intersection i2 = new Intersection("1,0,"+i*3+j);
                road.setPoints(i1, i2);
                testRoads.add(road);
                j++;
                System.out.println(road.driveTime * (road.speedLimit/3.6));
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
