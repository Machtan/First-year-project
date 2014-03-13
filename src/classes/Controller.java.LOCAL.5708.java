/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import enums.RoadType;
import java.awt.Color;

/**
 *
 * @author Isabella
 */
public class Controller {
    private Model model;
    private View view;
    private Loader loader;
    

    public Controller() {
        Intersection[] intersecArr = Loader.loadIntersections("resources/intersections.txt");
            RoadPart[] roadPartArr = Loader.loadRoads("resources/roads.txt");
       model = new Model(intersecArr, roadPartArr);
       view = new View();
       
       
       
       view.setLines(model.getLines(View.colorStuff));
       
    }
       
    public static void main(String[] args){
        
        //Run program
        Controller controller = new Controller();
        
        
        
        
    }
    
    
}
