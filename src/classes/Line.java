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

    public final short x1;
    public final short y1;
    public final short x2;
    public final short y2;
    public final Color color;

    public Line(float x1, float y1, float x2, float y2, Color color) {
        this.x1 = (short)x1;
        this.x2 = (short)x2;
        this.y1 = (short)y1;
        this.y2 = (short)y2;
        this.color = color;
        
    }

}
