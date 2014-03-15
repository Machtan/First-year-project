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
    public static RenderInstructions defaultInstructions = new RenderInstructions();
    private static boolean initialized = false;
    
    /**
     * Initializes the static variables
     */
    public static void initializeStaticVars() {
        if (initialized) { return; }
        // Create the default render instructions :p
        defaultInstructions.addMapping(Color.red, RoadType.TEMP);
        defaultInstructions.addMapping(Color.blue, RoadType.TEEMP);
        defaultInstructions.addMapping(Color.black, RoadType.TEEEMP);
        defaultInstructions.addMapping(Color.green, RoadType.TEEEEMP);
        System.out.println("Initialized the default render instructions!");
        initialized = true;
    }
    
    
    public View() {
        View.initializeStaticVars();
        lines = View.makeLineArr(defaultInstructions);
        
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
    
        colorStuff.addMapping(Color.green, RoadType.TEEMP);
        colorStuff.addMapping(Color.blue, RoadType.TEEEMP);
        colorStuff.addMapping(Color.black, RoadType.TEEEEMP);
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
            
            lineArr[i] = new Line(10, x+(10*x), 30 * x, x+(10*x), defaultInstructions.getColor(RoadType.TEMP));
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
