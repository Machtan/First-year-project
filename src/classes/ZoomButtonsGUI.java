package classes;

import external.SpringUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 *
 * @author Isabella
 */
public class ZoomButtonsGUI extends JPanel {
    private final JPanel panel;
    private int numberOfButtons = 0;
    
    private void addButton(String text, String tooltip, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setToolTipText(tooltip);
        button.addActionListener(listener);
        numberOfButtons++;
        panel.add(button);
    }

    public ZoomButtonsGUI(final Controller controller) {
        super();
        panel = new JPanel(new SpringLayout());
        
        String zoomTip = "You can also zoom in by clicking '+' on your keyboard.";
        addButton("Zoom +", zoomTip, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.draw(controller.viewport.zoomBy(0.2f));
            }
        });
        String zoomOutTip = "You can also zoom out by clicking '-' on your keyboard";
        addButton("Zoom -", zoomOutTip, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.draw(controller.viewport.zoomBy(-0.2f));
            }
            
        });
        String resetTip = "Zooms back out to the whole map.";
        addButton("Reset", resetTip, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.draw(controller.viewport.zoomTo(1));
            }
        });
        // NOTE: change the 2nd parameter to the amount of buttons added!
        SpringUtilities.makeCompactGrid(panel, numberOfButtons, 1, 0, 0, 5, 5); // Prepare the grid :D
        this.add(panel);
        repaint();
    }
}
