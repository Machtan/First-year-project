package classes;

import interfaces.IProgressBar;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Isabella
 */
public class ProgressBar implements IProgressBar {

    private final JFrame frame;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;
    private final static double updateAmount = 0.004; 
    private int target;
    private int counter; // Current counter
    private int minAdd; // How much the counter needs to be before updating

    public ProgressBar() {
        frame = new JFrame();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        statusLabel = new JLabel("No target set :)");
        progressBar = new JProgressBar(0, 0);
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
        
        frame.setPreferredSize(new Dimension(200, panel.getPreferredSize().height));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
    
    //Check whether or not the loading is complete
    public boolean done() {
        return progressBar.getPercentComplete() == 1;
    }
    
    /**
     * Sets a new target for the progress bar
     * @param text The text to display for the target
     * @param target The target amount
     */
    public void setTarget(String text, int target) {
        minAdd = (int)Math.ceil(target*updateAmount);
        counter = 0;
        this.target = target;
        progressBar.setMaximum(target);
        progressBar.setValue(0);
        statusLabel.setText(text);
    }

    /**
     * Updates the progress bar by the given amount
     * @param addition The amount to increase by
     */
    public void update(int addition) {
        counter += addition;
        if (!done() || addition==0) {
            if (counter == minAdd || (counter+progressBar.getValue() == target)) {
                progressBar.setValue(progressBar.getValue() + counter);
                counter = 0;
            }
        } else {
            throw new RuntimeException("Update ("+addition+") attempted while already finished!");
        }
    }

    //Closes the frame if done
    public void close() {
        frame.dispose();
    }
}
