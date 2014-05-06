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

/**
 *
 * @author Daniel
 */
public class RouteDesc extends JPanel {

    private JList descList;
    private RoadPart[] roadDesc;
    private DefaultListModel listModel;
    double roadLength;

    public RouteDesc(RoadPart[] road) {
        listModel = new DefaultListModel();
        descList = new JList(listModel);
        descList.setSize(150, 400);
        roadDesc = road;
        descList.setVisible(true);
        calcRoadLength();
    }

    private void calcRoadLength() {
        if (roadDesc.length == 0) {
            listModel.addElement("Please enter valid stuff");
        }

        for (int i = 0; i < roadDesc.length; i++) {
            
            roadLength = Math.sqrt(Math.pow(roadDesc[i].area.height, 2) + Math.pow(roadDesc[i].area.width, 2));
            //System.out.println(roadDesc[i].area.height);
            
            for (int j = i; j < roadDesc.length; j++) {
                if(j == roadDesc.length-1) { break; }
                if (roadDesc[i].name.equals(roadDesc[i + 1].name)) {
                    roadLength += Math.sqrt(Math.pow(roadDesc[j + 1].area.height, 2) + Math.pow(roadDesc[j + 1].area.width, 2));
                    j++;
                } else if (!roadDesc[i].name.equals(roadDesc[i + 1].name)) {
                    i = j;
                    break;
                }
            }
            if (i < roadDesc.length - 1) {
                listModel.addElement("Follow " + roadDesc[i].name + " for " + roadLength + " meters and turn onto " + roadDesc[i + 1].name);
            } else if (i == roadDesc.length - 1) {
                listModel.addElement("You reached your destionation: " + roadDesc[i].name);
            }
        }
    }
    public static void main(String[] args) {
        ArrayList<RoadPart> testRoads = new ArrayList<>();
        Intersection i1 = new Intersection("0,0,0");
        for (int i = 0; i < 10; i++) {
            int j = 0;
            for (String name : new String[]{"Kildevej", "Rodevej", "Pærevej"}) {
                RoadPart road = new RoadPart("0,0,0,"+name+",0,0,0,0,,,,,0,0,0,"+80+","+i*3+",0,,,");
                Intersection i2 = new Intersection("1,0,"+i*3+j);
                road.setPoints(i1, i2);
                testRoads.add(road);
                j++;
                System.out.println(road.driveTime * (road.speedLimit/3.6));
            }
        }
        RoadPart[] roads = testRoads.toArray(new RoadPart[0]);
        RouteDesc stuff = new RouteDesc(roads);
    }
}
