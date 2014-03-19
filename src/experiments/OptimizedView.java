package experiments;

import classes.Line;
import classes.Rect;
import java.awt.BasicStroke;
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
public class OptimizedView extends JPanel  {
    private BufferedImage image;
    public Color clearColor = Color.WHITE;
    GraphicsConfiguration gfx_config = GraphicsEnvironment.
		getLocalGraphicsEnvironment().getDefaultScreenDevice().
		getDefaultConfiguration(); // Voodoo
    Rect markerRect = null;
    
    /**
     * Constructor for the OptimizedView class
     * @param dimension
     */
    public OptimizedView (Dimension dimension) {
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
    public void offsetImage(int x, int y, Line[] newLines) { // Takes roughly 0.0016 secs at worst
        long t1 = System.nanoTime();
        BufferedImage newImage = gfx_config.createCompatibleImage(getWidth(), getHeight());
        Graphics2D g2d = newImage.createGraphics();
        clear(g2d); // Clear the whole image
        g2d.drawImage(image, x, -y, this); // Draw the old image offset
        long t2 = System.nanoTime();
        for (Line line : newLines) {
            g2d.setColor(line.color);
            g2d.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
        System.out.println("Offsetting by "+x+", "+y+" ("+newLines.length+" lines)");
        long t3 = System.nanoTime();
        double nFac = 1000000000.0;
        double stampTime = (t2-t1)/nFac;
        double drawTime = (t3-t2)/nFac;
        double total = (t3-t1)/nFac;
        System.out.println("Offsetting took "+total+" secs (image: "+stampTime+" secs, lines: "+drawTime+" secs)");
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
    
    public void setMarkerRect(Rect rect) {
        markerRect = rect;
    }
    
    /**
     * What happens at the default render (on resize etc.)
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            long t1 = System.nanoTime();
            g.drawImage(image, 0, 0, this);
            double delay = (System.nanoTime()-t1)/1000000000.0;
            System.out.println("Drawing the Optimized View took "+delay+" secs");
            if (markerRect != null) { // Draw the rect used for marking 
                BasicStroke str = new BasicStroke(2, BasicStroke.CAP_BUTT, 
                        BasicStroke.JOIN_BEVEL, 0, new float[] {3,2}, 0);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setColor(new Color(200,200,255,90));
                g2d.fillRect((int)Math.round(markerRect.x), (int)Math.round(markerRect.y-markerRect.height), 
                        (int)Math.round(markerRect.width), (int)Math.round(markerRect.height));
                g2d.setColor(Color.BLUE);
                g2d.setStroke(str);
                g2d.drawRect((int)Math.round(markerRect.x), (int)Math.round(markerRect.y-markerRect.height), 
                        (int)Math.round(markerRect.width), (int)Math.round(markerRect.height));
            }
            
        } else {
            System.out.println("No image set yet, so nothing to draw...");
            clear(g);
        }
    } 
}
