package classes;

import classes.Viewport.Projection;
import enums.RoadType;
import interfaces.IProgressBar;
import interfaces.StreamedContainer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * The OptimizedView class is a faster view using an underlying buffered image
 * to optimize its draw calls when the map is moved.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 10-Mar-2014
 */
public class OptimizedView extends JPanel implements StreamedContainer<Road> {
    GraphicsConfiguration gfx_config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); // Voodoo
    
    public final RenderInstructions ins;
    public static Color clearColor = Color.WHITE;
    
    private BufferedImage image;
    private BufferedImage backbuffer;
    private boolean scaled      = false;
    private Rect markerRect     = null;
    private Road.Edge[] path    = new Road.Edge[0];
    private Road.Node fromNode  = null;
    private Road.Node toNode    = null;
    
    // Strokes
    private final BasicStroke dotStroke = new BasicStroke(2, 
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,2}, 0);
    private final BasicStroke ferryStroke = new BasicStroke(1, 
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5,4}, 0);
    private final BasicStroke pathStroke = new BasicStroke(2, 
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    
    //private HashMap<RoadType, BufferedImage> layers = new HashMap<>();
    
    // Values used for the streamed image drawing
    private Graphics2D activeGraphics;
    private Projection activeProjection;
    
    /**
     * Constructor for the OptimizedView class
     * @param dimension
     */
    public OptimizedView (Dimension dimension, RenderInstructions ins) {
        super();
        this.ins = ins;
        setMinimumSize(dimension);
        setSize(dimension);
        renewImage(Viewport.Projection.Empty);
    }
    
    /**
     * Sets the shortest path to be drawn
     * @param path The path to be drawn
     */
    public void setPath(Road.Edge[] path) {
        if (path == null) {
            throw new RuntimeException("Please use a Road.Edge[0] for clearing!");
        }
        this.path = path;
        repaint();
    }
    
    /**
     * Tells the view to draw the given node as the starting point of a path
     * Use 'null' to clear.
     * @param start The node to start at
     */
    public void setPathStart(Road.Node start) {
        System.out.println("Set the path starting point to "+start);
        fromNode = start;
        repaint();
    }
    
    /**
     * Tells the view to draw the given node as the ending point of a path
     * Use 'null' to clear.
     * @param end The node to end at
     */
    public void setPathEnd(Road.Node end) {
        System.out.println("Set the path ending point to "+end);
        toNode = end;
        repaint();
    }
    
    /**
     * Sets the projection of p
     * @param p 
     */
    public void setProjection(Projection p) {
        System.out.println("Setting the projection to "+p);
        activeProjection = p;
    }
    
    /**
     * Set the marker rect to be drawn
     * @param rect The rect
     */
    public void setMarkerRect(Rect rect) {
        markerRect = rect;
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
     */
    public void offsetImage(int x, int y) {
        swapBuffers();
        clear(image); // Clear the whole image
        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(backbuffer, x, -y, this); // Draw the old image offset
        g2d.dispose();
        repaint();
    }
    
    public void extend() {
        backbuffer = createImage(getSize(), false);
        Graphics2D g2d = backbuffer.createGraphics();
        g2d.setColor(clearColor);
        g2d.fillRect(image.getWidth(), 0, getWidth() - image.getWidth(), getHeight());
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        image = createImage(getSize(), false);
        swapBuffers();
        repaint();
    }
        
    /**
     * Resizes the map somewhat naïvely
     * @param newSize 
     */
    public void scaleMap(Dimension newSize) {
        if (image == null) { return; }
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
     * Creates a new compatible BufferedImage with the given size and optionally
     * clears it
     * @param dim The dimension of the image
     * @param clear Whether it should be filled with the clearColor
     * @return A potentially cleared buffered image of the given size
     */
    public BufferedImage createImage(Dimension dim, boolean clear) {
        BufferedImage img = gfx_config.createCompatibleImage(dim.width, dim.height);
        if (clear) {
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(clearColor);
            g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
            g2d.dispose();
        }
        return img;
    }
    
    public void renewImage(Projection p) {
        activeProjection = p;
        image = createImage(getSize(), true);
        backbuffer = createImage(getSize(), false);
        scaled = false;
        repaint();
    }
        
    /**
     * Draws all the extra overlays of the view
     * @param g2d The graphics2D object to draw it unto
     */
    private void drawOverlays(Graphics2D g2d) {
        Graphics2D g2 = (Graphics2D)g2d.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (markerRect != null) {
            Graphics2D g2c = (Graphics2D)g2.create();
            g2c.setColor(new Color(200,200,255,90));
            g2c.fillRect((int)Math.round(markerRect.x), (int)Math.round(markerRect.y-markerRect.height), 
                    (int)Math.round(markerRect.width), (int)Math.round(markerRect.height));
            g2c.setColor(Color.BLUE);
            g2c.setStroke(dotStroke);
            g2c.drawRect((int)Math.round(markerRect.x), (int)Math.round(markerRect.y-markerRect.height), 
                    (int)Math.round(markerRect.width), (int)Math.round(markerRect.height));
        }
        int h = getHeight();
        if (path.length != 0) {
            Graphics2D g2e = (Graphics2D)g2d.create();
            
            g2e.setStroke(pathStroke);
            g2e.setColor(Color.GREEN);
            for (Road.Edge edge : path) {
                g2e.drawLine(
                        edge.p1.mappedX(activeProjection), 
                        edge.p1.mappedY(activeProjection, h), 
                        edge.p2.mappedX(activeProjection), 
                        edge.p2.mappedY(activeProjection, h));
            }
        }
        if (fromNode != null) {
            int x = fromNode.mappedX(activeProjection);
            int y = fromNode.mappedY(activeProjection, h);
            g2.setColor(Color.MAGENTA);
            g2.fillOval(x, y, 10, 10);
        }
        if (toNode != null) {
            int x = toNode.mappedX(activeProjection);
            int y = toNode.mappedY(activeProjection, h);
            g2.setColor(new Color(100,255,100));
            g2.fillOval(x, y, 10, 10);
        }
    }
    
    /**
     * What happens at the default render (on resize etc.)
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            g.setColor(clearColor);
            g.fillRect(0,0,getWidth(),getHeight());
            g.drawImage(image, 0, 0, this);
            drawOverlays((Graphics2D)g);
            g.dispose();
            
        } else {
            System.out.println("No image set yet, so nothing to draw...");
        }
    } 
    
    private IProgressBar progbar = null;
    @Override
    public void startStream() {
        System.out.println("Starting View paint routine...");
        activeGraphics = image.createGraphics();
        activeGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    }

    @Override
    public void startStream(IProgressBar bar) {
        progbar = bar;
        startStream();
    }
    
    /**
     * Draws all edges of a road as a poly line
     * (This is a rather inefficient alternative to drawLines)
     * @param road The road to draw
     */
    private void drawPolyLine(Road road) {
        activeGraphics.setColor(ins.getColor(road.type));
        int l = road.nodes.length;
        int h = getHeight();
        int[] xVals = new int[l];
        int[] yVals = new int[l];
        for (int i = 0; i < l; i++) {
            Road.Node n = road.nodes[i];
            xVals[i] = n.mappedX(activeProjection);
            yVals[i] = n.mappedY(activeProjection, h);
        }
        activeGraphics.drawPolyline(xVals, yVals, l);
    }
    
    /**
     * Draws each edge of a road as a separate line
     * @param road The road to draw
     */
    private void drawLines(Road road) {
        activeGraphics.setColor(ins.getColor(road.type));
        Graphics2D g2d = activeGraphics;
        int h = getHeight();
        if (road.type == RoadType.Ferry) {
            g2d = (Graphics2D)activeGraphics.create();
            g2d.setStroke(ferryStroke);
        }
        for (Road.Edge edge: road) {
            //System.out.println("Drawing a line from ("+edge.p1+") to ("+edge.p2+") => "+edge.p1.mappedX(activeProjection)+" to "+edge.p2.mappedX(activeProjection));
            g2d.drawLine(
                    edge.p1.mappedX(activeProjection), 
                    edge.p1.mappedY(activeProjection, h), 
                    edge.p2.mappedX(activeProjection), 
                    edge.p2.mappedY(activeProjection, h));
        }
    }

    @Override
    public void add(Road obj) {
        drawLines(obj);
        if (progbar != null) {
            progbar.update(1);
        }
    }

    @Override
    public void endStream() {
        System.out.println("Painting finished");
        repaint();
        progbar = null;
    }
}
