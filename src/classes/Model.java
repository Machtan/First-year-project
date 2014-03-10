/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import java.util.HashMap;
import javax.swing.JComponent;

/**
 *
 * @author Isabella
 */
public class Model {
    private Model model;
    private HashMap<Integer, Intersection> intersecMap;
    private RoadPart[] roadPartArr;
    private Line[] lineArr;

    public Model(Intersection[] intersecArr, RoadPart[] roadPartArr) {
        this.roadPartArr = roadPartArr;
        lineArr = new Line[roadPartArr.length];
        intersecMap = new HashMap<>();
        for (Intersection intersecArr1 : intersecArr) {
            intersecMap.put(intersecArr1.id, intersecArr1);
        }
    }
    
    public int getScreenX(double x, JComponent comp) {
        return (int)((x-442254.35659) * (comp.getHeight() / 352136.5527900001));
    }
        
    public int getScreenY(double y, JComponent comp) {
        return (int)(comp.getHeight() - ((y-6049914.43018) * (comp.getHeight() / 352136.5527900001)));
    }
    
    public Line[] getLines(JComponent view){
        for(int i = 0; i<roadPartArr.length; i++) {
            lineArr[i] = new Line(
                    getScreenX(intersecMap.get(roadPartArr[i].sourceID).x, view), 
                    getScreenY(intersecMap.get(roadPartArr[i].sourceID).y, view),
                    getScreenX(intersecMap.get(roadPartArr[i].targetID).x, view),
                    getScreenY(intersecMap.get(roadPartArr[i].targetID).y, view));
        }
        return lineArr;
    }
   
}
