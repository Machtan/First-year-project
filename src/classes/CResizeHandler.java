package classes;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The CResizeHandler class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 24-Mar-2014
 */
public class CResizeHandler implements ComponentListener, ActionListener, WindowStateListener, WindowListener {
    private final Timer resizeTimer;
    private Dimension prevSize;
    private Dimension startResizeSize; // The size when a resize is started
    private Controller controller;
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    // Tweakable values
    private final static int resizeDelay = 400; // milliseconds
    private final static int margin = 40; // The amount of pixels to load to the right when resizing
    private Rect lastRect;
    private OptimizedView view;
    
    
    public CResizeHandler(Controller controller, OptimizedView view) {
        // Prepare resize handling :)
        this.controller = controller;
        this.view = view;
        view.addComponentListener(this);
        controller.addWindowStateListener(this);
        controller.addWindowListener(this);
        resizeTimer = new Timer(resizeDelay, this);
        resizeTimer.setRepeats(false);
        prevSize = view.getSize(); // prepare for scaling
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
        if (prevSize == null) { return; } // You're too fast ;)
        
        Dimension newSize = view.getSize();
        if (newSize.height == 0 || newSize.width == 0) { return; } // This cannot be resized ;)
        if (newSize.height != prevSize.height) {
            if (!resizeTimer.isRunning()) { // Start the 'draw it prettily' timer
                startResizeSize = newSize;
                resizeTimer.start();
            } else {
                resizeTimer.restart(); // Interrupt the timer
            }
            view.scaleMap(newSize);
        } else if (newSize.width != prevSize.width) { // The windows is wider now
            controller.extend(newSize.width - prevSize.width);
        }
        prevSize = newSize;
    }

    @Override
    public void actionPerformed(ActionEvent e) { // Once the user has finished redrawing
        // Only redraw if it is needed, plx
        if (view.getHeight() != startResizeSize.height) {
            System.out.println("Redrawing after resizing...");
            controller.redraw();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {}
    @Override
    public void componentShown(ComponentEvent e) {}
    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void windowStateChanged(WindowEvent e) {
        if (e.getNewState() == 6 || e.getNewState() == 0) { // Maximize window on windows
            String msg = "Window "+ ((e.getNewState() == 6)? "maximized!": "restored!");
            System.out.println(msg);
            controller.redraw();
            prevSize = view.getSize();
        } 
    }

    @Override
    public void windowOpened(WindowEvent e) {
        controller.draw(controller.viewport.zoomTo(1));
    }

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) { }

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
