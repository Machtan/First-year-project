package classes;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Isabella
 */
public class ProgressBar {

    private final JFrame frame;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;
    //Maximum constants for the different loading options
    private static final int intersections = 675902;
    private static final int roads = 812301;
    private static final int quads = 812301;
    private static ProgressBar instance;

    private ProgressBar() {
        frame = new JFrame();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        statusLabel = new JLabel("Loading...");
        
        progressBar = new JProgressBar(0, intersections + roads + quads);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel labelPanel = new JPanel(new GridBagLayout());
        labelPanel.add(statusLabel);
        JPanel barPanel = new JPanel(new GridBagLayout());
        barPanel.add(progressBar);
        
        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(barPanel, BorderLayout.SOUTH);
        
        frame.add(panel);
        frame.pack();
        frame.setPreferredSize(panel.getPreferredSize());
        frame.setVisible(true);
    }
    
    public static void open() {
        instance = new ProgressBar();
    }
    
    //Check whether or not the loading is complete
    public static boolean done() {
        return (Loader.getIntersectionCnt() == intersections
                && Loader.getRoadCnt() == roads && Model.getQuadCnt() == quads);
    }
    
    private static void assertInstance() {
        if (instance == null ) {
            throw new RuntimeException("The ProgressBar hasn't been opened\n"
                    + "(use ProgressBar.open() before calling update)");
        }
    }

    //Update progress with the given counter
    public static Integer update(Integer count) {
        assertInstance();
        instance.progressBar.setValue(count);
        
        return count;
    }

    //Update label to inform the user
    public static void updateLabel(Integer count) {
        assertInstance();
        if (count < intersections) {
            instance.statusLabel.setText("Loading intersections");
        } else if (count >= intersections && count < roads + intersections) {
            instance.statusLabel.setText("Loading roads");
        } else {
            instance.statusLabel.setText("Initializing quad-tree");
        }
    }

    //Closes the frame if done
    public static void close() {
        assertInstance();
        if (done()) {
            instance.frame.setCursor(null);
            instance.frame.dispose();
            instance = null;
        }
    }
}
