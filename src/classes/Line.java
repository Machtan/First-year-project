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
public class Line {

    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;

    public Line(double x1, double y1, double x2, double y2) {
        this.x1 = (int)x1;
        this.x2 = (int)x2;
        this.y1 = (int)y1;
        this.y2 = (int)y2;
    }

}
