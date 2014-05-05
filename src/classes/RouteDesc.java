/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

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
        doStuff();
    }

    private void doStuff() {
        if (roadDesc.length == 0) {
            listModel.addElement("Please enter valid stuff");
        }

        for (int i = 0; i < roadDesc.length; i++) {
            roadLength = Math.sqrt(Math.pow(roadDesc[i].area.height, 2) + Math.pow(roadDesc[i].area.width, 2));
            for (int j = i; j < roadDesc.length; j++) {
                if (roadDesc[i].name.equals(roadDesc[i + 1].name)) {
                    roadLength += Math.sqrt(Math.pow(roadDesc[j + 1].area.height, 2) + Math.pow(roadDesc[j + 1].area.width, 2));
                    j++;
                } else if (!roadDesc[i].name.equals(roadDesc[i + 1].name)) {
                    i = j;
                    break;
                }
            }
            if (i < roadDesc.length - 1) {
                listModel.addElement(roadDesc[i].name + " For " + roadLength + " meters and turn onto " + roadDesc[i + 1].name);
            } else if (i == roadDesc.length - 1) {
                listModel.addElement("You reached your destionation: " + roadDesc[i].name);
            }
        }
    }
}
