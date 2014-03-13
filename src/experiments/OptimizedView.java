package experiments;

import classes.Line;
import classes.Rect;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import static java.lang.Math.max;
import static java.lang.Math.min;
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
        setPreferredSize(dimension);
        setFocusTraversalKeysEnabled(false);
        setFocusable(true);
    }
    
    @Deprecated
    public static Rect getBoundingRect(Line[] lines) {
        
                double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        
        for (Line line : lines) {  
            minX = min(minX, line.x1);
            minX = min(minX, line.x2);
            maxX = max(maxX, line.x1);
            maxX = max(maxX, line.x2);
            minY = min(minY, line.y1);
            minY = min(minY, line.y2);
            maxY = max(maxY, line.y1);
            maxY = max(maxY, line.y2);
        }
        return new Rect(minX, minY, maxX-minX, maxY-minY);
        
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
        g2d.setColor(Color.BLACK);
        g2d.drawImage(image, x, -y, this); // Draw the old image offset
        for (Line line : newLines) {
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
        g2d.setColor(Color.BLACK);
        for (Line line : lineArr) {
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
