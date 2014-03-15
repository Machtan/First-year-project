/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import enums.RoadType;
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
    public static RenderInstructions colorStuff;
    
    /*
    X ranges from 442254.35659 to 892658.21706 (450403.8604700001)
    Y ranges from 6049914.43018 to 6402050.98297 (352136.5527900001)
    */
    
    public View() {
        colorStuff = new RenderInstructions();
        View.setColors();
        lines = View.makeLineArr(colorStuff);
        
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
    }
    
    //Add colors to the map in RenderInstr.
    public static void setColors() {
        colorStuff.addMapping(Color.red, RoadType.TEMP);
        colorStuff.addMapping(Color.green, RoadType.TEEMP);
        colorStuff.addMapping(Color.blue, RoadType.TEEEMP);
        colorStuff.addMapping(Color.black, RoadType.TEEEEMP);
    }
    
    
    public void setLines(Line[] lines) {
        this.lines = lines;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(lines, g);
        
            
        
    }
    
    private static Line[] makeLineArr(RenderInstructions instr) {
        
        Line[] lineArr = new Line[10];
        int x = 1;
        
        for (int i = 0; i < lineArr.length; i++) {
            
            lineArr[i] = new Line(10, x+(10*x), 30 * x, x+(10*x), colorStuff.getColor(RoadType.TEMP));
            x++;
           
        }
        return lineArr;
    }
    
    public void draw(Line[] lineArr, Graphics g) {
        
        for (Line line : lineArr) {
            g.setColor(line.color);
            g.drawLine(line.x1, line.y1, line.x2, line.y2);
            
           
        }
    }
}
