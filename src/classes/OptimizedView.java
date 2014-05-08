package classes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * The OptimizedView class is a faster view using an underlying buffered image
 * to optimize its draw calls when the map is moved.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class OptimizedView extends JPanel  {
    private BufferedImage image;
    private BufferedImage backbuffer;
    private boolean scaled;
    public static final Color clearColor = Color.WHITE;
    GraphicsConfiguration gfx_config = GraphicsEnvironment.
		getLocalGraphicsEnvironment().getDefaultScreenDevice().
		getDefaultConfiguration(); // Voodoo
    Rect markerRect = null;
    
    /**
     * Constructor for the OptimizedView class
     * @param dimension
     */
    public OptimizedView (Dimension dimension) {
        super();
        scaled = false;
        setMinimumSize(dimension);
        setSize(dimension);
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
     * Swaps the buffer images
     */
    private void swapBuffers() {
        BufferedImage previous = image;
        image = backbuffer;
        backbuffer = previous;
    }
    
    /**
     * Moves the current image based on the offset, then draws the given array
     * of lines. Used for panning.
     * @param x The Eastward offset 
     * @param y The Nortward offset 
     * @param newLines The new lines to patch up 
     */
    public void offsetImage(int x, int y, Line[]... newLines) {
        swapBuffers();
        clear(image); // Clear the whole image
        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(backbuffer, x, -y, this); // Draw the old image offset
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        for (Line[] arr: newLines) {
            for (Line line : arr) {
                g2d.setColor(line.color);
                g2d.drawLine(line.x1, line.y1, line.x2, line.y2);
            }
        }
        g2d.dispose();
        repaint();
    }
    
    public void extend(Line[]...newLines) {
        backbuffer = gfx_config.createCompatibleImage(getWidth(), getHeight());
        Graphics2D g2d = backbuffer.createGraphics();
        g2d.setColor(clearColor);
        g2d.fillRect(image.getWidth(), 0, getWidth() - image.getWidth(), getHeight());
        g2d.drawImage(image, 0, 0, null);
        for (Line[] arr: newLines) {
            for (Line line : arr) {
                g2d.setColor(line.color);
                g2d.drawLine(line.x1, line.y1, line.x2, line.y2);
            }
        }
        g2d.dispose();
        image = gfx_config.createCompatibleImage(getWidth(), getHeight());
        swapBuffers();
        repaint();
    }
        
    /**
     * Resizes the map somewhat naïvely
     * @param newSize 
     */
    public void scaleMap(Dimension newSize) {
        if (!initialized()) { return; }
        if (!scaled) { // The scaling is starting
            swapBuffers(); // Ensure that the backbuffer holds the scaling source
        }
        // Calculate the dimensions of the new image
        Dimension size = Utils.clampDimension(newSize, 
                new Dimension(backbuffer.getWidth(), backbuffer.getHeight()), 
                true);
        image = gfx_config.createCompatibleImage(newSize.width, newSize.height);
        Graphics2D g2d = image.createGraphics();
        // Find out what to clear
        g2d.setColor(clearColor);
        if (size.width < newSize.width) { // different width
            g2d.fillRect(size.width, 0, newSize.width-size.width, size.height);
        } else {
            g2d.fillRect(0, size.height, size.width, newSize.height-size.height);
        }
        
        g2d.drawImage(backbuffer, 0, 0, size.width, size.height, null);
        g2d.dispose();
        scaled = true;
        repaint();
    }
    
    /**
     * Creates a buffered image from the given list of lines
     * @param lineArr The lines to draw
     * @return A buffered image containing the drawn lines
     */
    private BufferedImage createImage(Line[] lineArr, Dimension dim) {
        if (dim.height == 0 || dim.width == 0) { 
            dim = this.getMinimumSize(); 
        }
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
        image = createImage(lines, getSize());
        backbuffer = gfx_config.createCompatibleImage(getWidth(), getHeight());
        scaled = false;
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
