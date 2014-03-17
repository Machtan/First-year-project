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
    private final JFrame frame;
    public static final RenderInstructions defaultInstructions = new RenderInstructions();
    
    /**
     * Initializes the static variables
     */
    static void InitDefRenderInstr() {
        // Create the default render instructions :
        defaultInstructions.addMapping(Color.red, RoadType.Motorvej);
        defaultInstructions.addMapping(Color.red, RoadType.Motorvejsafkorsel);
        defaultInstructions.addMapping(new Color(51,51,255), RoadType.PrimaerRute);
        defaultInstructions.addMapping(new Color(0,255,25), RoadType.Sti);
        defaultInstructions.addMapping(Color.black, RoadType.Other);
        
        System.out.println("Initialized the default render instructions!");
    }  
    
    
  
   
    
    public View() {
        lines = View.makeLineArr(defaultInstructions);
        View.InitDefRenderInstr();
        
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
            
            lineArr[i] = new Line(10, x+(10*x), 30 * x, x+(10*x), defaultInstructions.getColor(RoadType.fromValue(i%1)));
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
