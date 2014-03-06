/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author Isabella
 */
public class Controller {

    private final Model model;
    private final View view;

    public Controller() {
        model = new Model(Loader.loadIntersections("resources/intersections.txt"),
                Loader.loadRoads("resources/roads.txt"));
        view = new View();
        view.setLines(model.getLines());
        

    }

    public static void main(String[] args) {
        //Run program
        Controller controller = new Controller();

    }

}
