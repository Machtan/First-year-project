/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import java.util.HashMap;

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
    
    public Line[] getLines(){
        for(int i = 0; i<roadPartArr.length; i++) {
            lineArr[i] = new Line(
                    intersecMap.get(roadPartArr[i].sourceID).x, 
                    intersecMap.get(roadPartArr[i].sourceID).y,
                    intersecMap.get(roadPartArr[i].targetID).x, // Error here s5,t7
                    intersecMap.get(roadPartArr[i].targetID).y);
        }
        return lineArr;
    }
   
}
