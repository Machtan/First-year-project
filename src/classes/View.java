/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;

/**
 *
 * @author Isabella
 */
public class View extends JPanel {
    
    private static Graphics g;
    private JLabel statusLabel;
    private Line[] lines;
    private JFrame frame;
    
    /*
    X ranges from 442254.35659 to 892658.21706 (450403.8604700001)
    Y ranges from 6049914.43018 to 6402050.98297 (352136.5527900001)
    */
    
    public View() {
        lines = View.makeLineArr();
        
        frame = new JFrame();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this, BorderLayout.CENTER);
        JLabel statusLabel = new JLabel("Address information here...");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
        paintComponent(this.getGraphics());
    }
    
    public void setLines(Line[] lines) {
        this.lines = lines;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        draw(lines, g);
    }
    
    private static Line[] makeLineArr() {
        Line[] lineArr = new Line[10];
        int x = 1;
        
        for (int i = 0; i < lineArr.length; i++) {
            lineArr[i] = new Line(10, x+(10*x), 30 * x, x+(10*x));
            x++;
        }
        return lineArr;
    }
    
    public int getScreenX(double x) {
        return (int)((x-442254.35659) * (getHeight() / 352136.5527900001));
    }
    public int getScreenY(double y) {
        return (int)(getHeight() - ((y-6049914.43018) * (getHeight() / 352136.5527900001)));
    }
    
    public void draw(Line[] lineArr, Graphics g) {
        for (Line line : lineArr) {
            //System.out.println((int)lineArr[i].x1 + "+"+ (int)lineArr[i].y1+"+" +(int)lineArr[i].x2+"+"+ (int)lineArr[i].y2);
            g.drawLine(getScreenX(line.x1), 
                    getScreenY(line.y1), 
                    getScreenX(line.x2), 
                    getScreenY(line.y2));
        }
        System.out.println("3");
    }
}
