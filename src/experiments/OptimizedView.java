package experiments;

import classes.Line;
import classes.Rect;
import classes.View;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * The OptimizedView class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class OptimizedView extends JPanel {
    private BufferedImage image;
    public Color clearColor = Color.WHITE;
    GraphicsConfiguration gfx_config = GraphicsEnvironment.
		getLocalGraphicsEnvironment().getDefaultScreenDevice().
		getDefaultConfiguration(); // Voodoo
    
    /**
     * Constructor for the OptimizedView class
     * @param dimension
     */
    public OptimizedView (Dimension dimension) {
        View.initializeStaticVars();
        setPreferredSize(dimension);
        setFocusTraversalKeysEnabled(false);
        setFocusable(true);
    }
    
    /**
     * Fills the given graphics with the set clear color
     * @param g The graphics to fill
     */
    public void clear(Graphics g) {
        g.setColor(clearColor);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Moves the current image based on the offset, then draws the given array
     * of lines. Used for panning.
     * @param x The Eastward offset 
     * @param y The Nortward offset 
     * @param newLines The new lines to patch up 
     */
    public void offsetImage(int x, int y, Line[] newLines) {
        BufferedImage newImage = gfx_config.createCompatibleImage(getWidth(), getHeight());
        Graphics2D g2d = newImage.createGraphics();
        clear(g2d); // Clear the whole image
        g2d.drawImage(image, x, -y, this); // Draw the old image offset
        for (Line line : newLines) {
            g2d.setColor(line.color);
            g2d.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
        System.out.println("Offsetting by "+x+", "+y+" ("+newLines.length+" lines)");
        image = newImage;
        repaint();
    }
    
    /**
     * Creates the current 
     * @param lineArr 
     */
    public void createImage(Line[] lineArr) {
        image = gfx_config.createCompatibleImage(getWidth(), getHeight());
        Graphics2D g2d = image.createGraphics();
        clear(g2d);
        for (Line line : lineArr) {
            g2d.setColor(line.color);
            g2d.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
        repaint();
    }
    
    /**
     * What happens at the default render (on resize etc.)
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            //clear(g);
            g.drawImage(image, 0, 0, this);
        } else {
            System.out.println("No image set yet, so nothing to draw...");
            clear(g);
        }
    }
}
