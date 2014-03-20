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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Observable;
import javax.swing.*;

/**
 *
 * @author Isabella
 */
public class View extends JPanel {
    
    private static Graphics g;
    private Rect markerRect;
    private JLabel statusLabel;
    private Line[] lines;
    private final JFrame frame;

    
    
    public View() {
        lines = View.makeLineArr();
        
        frame = new JFrame();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this, BorderLayout.CENTER);
        JLabel statusLabel = new JLabel("Address information here...");

        
        frame.add(GUI.GUI(Model.defaultInstructions), BorderLayout.NORTH);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }   
    

    
 
    /**
     * Tells the view that a new marker rect needs to be drawn
     * @param rect 
     */
    public void setMarkerRect(Rect rect) {
        markerRect = rect;
    }
        
    public void setLines(Line[] lines) {
        this.lines = lines;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(lines, g);
        // Draw the marker rect
        if (markerRect != null) {
            g.setColor(Color.MAGENTA);
            g.fillRect((int)markerRect.x, (int)(markerRect.y-markerRect.height), 
                    (int)markerRect.width, (int)markerRect.height);
        }
    }
    
    private static Line[] makeLineArr() {
        
        Line[] lineArr = new Line[10];
        int x = 1;
        
        for (int i = 0; i < lineArr.length; i++) {
            
            lineArr[i] = new Line(10, x+(10*x), 30 * x, x+(10*x), null);
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
