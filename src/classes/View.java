/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.geom.Line2D;

/**
 *
 * @author Isabella
 */
public class View extends JPanel {

    //private Canvas canvas;
    private static Graphics g;

    private JLabel labelBot;

    public static void main(String[] args) {
        View view = new View();
        Line[] lineArr = new Line[10];
        JFrame frame = new JFrame();
        //canvas = new Canvas();
        //graphic = new Graphics();
        //canvas.setSize(400, 400);
        frame.setPreferredSize(new Dimension(400, 400));
        frame.add(new MainPanel(), BorderLayout.CENTER);
        JLabel labelBot = new JLabel("Address information here...");
        frame.add(labelBot, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
}

class MainPanel extends JPanel {
    
    private Line[] drawnLines;
    
    MainPanel() {
        drawnLines = makeLineArr();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        draw(drawnLines, g);
    }
    
    /**
     * Changes the lines that the view draws
     * @param lines 
     */
    public void setLines(Line[] lines) {
        drawnLines = lines;
    }

    private static Line[] makeLineArr() {
        Line[] lineArr = new Line[10];
        int x = 1;

        for (int i = 0; i < lineArr.length; i++) {
            lineArr[i] = new Line(10, x + (10 * x), 30 * x, x + (10 * x));
            x++;
        }
        return lineArr;
    }

    public void draw(Line[] lineArr, Graphics g) {
        for (Line lineArr1 : lineArr) {
            //System.out.println((int)lineArr[i].x1 + "+"+ (int)lineArr[i].y1+"+" +(int)lineArr[i].x2+"+"+ (int)lineArr[i].y2);
            g.drawLine((int) lineArr1.x1, (int) lineArr1.y1, (int) lineArr1.x2, (int) lineArr1.y2);
        }
        System.out.println("3");
    }

}
