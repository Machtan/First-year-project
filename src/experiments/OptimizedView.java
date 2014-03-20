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
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * The OptimizedView class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class OptimizedView extends JPanel  {
    private BufferedImage image;
    private BufferedImage scaleSource;
    public static final Color clearColor = Color.WHITE;
    GraphicsConfiguration gfx_config = GraphicsEnvironment.
		getLocalGraphicsEnvironment().getDefaultScreenDevice().
		getDefaultConfiguration(); // Voodoo
    Rect markerRect = null;
    public final static double wperh = 450403.8604700001 / 352136.5527900001; // map ratio
    
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
     * Fills the given image with the set clear color
     * @param img
     */
    public void clear(BufferedImage img) {
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(clearColor);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
    }
    
    /**
     * Returns the width of the view's scale source
     * @return The width of the view's scale source
     */
    public int getSourceWidth() {
        return scaleSource.getWidth();
    }
    
    /**
     * Moves the current image based on the offset, then draws the given array
     * of lines. Used for panning.
     * @param x The Eastward offset 
     * @param y The Nortward offset 
     * @param newLines The new lines to patch up 
     * @param newSize The new dimension of the image
     */
    public void offsetImage(int x, int y, Line[] newLines, Dimension newSize) { // Takes roughly 0.0016 secs at worst
        long t1 = System.nanoTime();
        scaleSource = gfx_config.createCompatibleImage(newSize.width, newSize.height);
        clear(scaleSource); // Clear the whole image
        Graphics2D g2d = scaleSource.createGraphics();
        g2d.drawImage(image, x, -y, this); // Draw the old image offset
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        long t2 = System.nanoTime();
        for (Line line : newLines) {
            g2d.setColor(line.color);
            g2d.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
        g2d.dispose();
        /*
        System.out.println("Offsetting by "+x+", "+y+" ("+newLines.length+" lines)");
        long t3 = System.nanoTime();
        double nFac = 1000000000.0;
        double stampTime = (t2-t1)/nFac;
        double drawTime = (t3-t2)/nFac;
        double total = (t3-t1)/nFac;
        System.out.println("Offsetting took "+total+" secs (image: "+stampTime+" secs, lines: "+drawTime+" secs)");
        */
        image = scaleSource;
        repaint();
    }
    
    /**
     * Moves the current image based on the offset, then draws the given array
     * of lines. Used for panning.
     * @param x The Eastward offset 
     * @param y The Nortward offset 
     * @param newLines The new lines to patch up 
     */
    public void offsetImage(int x, int y, Line[] newLines) {
        offsetImage(x, y, newLines, getSize());
    }
        
    /**
     * Resizes the map somewhat naïvely
     * @param newSize 
     */
    public void resizeMap(Dimension newSize) {
        if (scaleSource != null) {
            // Calculate the dimensions of the new image
            double ratio = newSize.height / (double)scaleSource.getHeight();
            Dimension size = new Dimension((int)Math.round(scaleSource.getWidth()*ratio), newSize.height);
            System.out.println("Resizing to "+size);
            int width = size.width; int height = size.height;
            Image scaledImage = scaleSource.getScaledInstance(width, height, Image.SCALE_FAST);
            BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = imageBuff.createGraphics();
            g.drawImage(scaledImage, 0, 0, clearColor, null);
            image = imageBuff;
            g.dispose();
        }
    }
    
    /**
     * Creates a buffered image from the given array of lines
     * @param lineArr The lines to draw
     * @return A buffered image containing the drawn lines
     */
    private BufferedImage createImage(Line[] lineArr, Dimension dim) {
        System.out.println("Creating an image with the size "+dim);
        BufferedImage img = gfx_config.createCompatibleImage(dim.width, dim.height);
        clear(img);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        for (Line line : lineArr) {
            g2d.setColor(line.color);
            g2d.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
        g2d.dispose();
        return img;
    }
    
    public void renewImage(Line[] lines) {
        scaleSource = createImage(lines, getSize());
        image = scaleSource;
        repaint();
    }
    
    /**
     * Returns whether the view is initialized and can be used
     * @return True if the view is initialized
     */
    public boolean initialized() {
        return image != null;
    }
    
    /**
     * Set the marker rect to be drawn
     * @param rect The rect
     */
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
            g.setColor(clearColor);
            g.fillRect(0,0,getWidth(),getHeight());
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
        }
    } 
}
