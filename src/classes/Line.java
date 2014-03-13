/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.Color;

/**
 *
 * @author Isabella
 */
public class Line {

    public final double x1;
    public final double y1;
    public final double x2;
    public final double y2;
    public final Color color;

    public Line(double x1, double y1, double x2, double y2, Color color) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.color = color;
        
    }

}
