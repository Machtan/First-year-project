package classes;

import external.SpringUtilities;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
    
    private JButton addButton(String tooltip, ActionListener listener) {
        JButton button = new JButton();
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(42,42));
        button.setToolTipText(tooltip);
        button.addActionListener(listener);
        numberOfButtons++;
        panel.add(button);
        return button;
    }
    
    private void addButton(String text, String tooltip, ActionListener listener) {
        addButton(tooltip, listener).setText(text);
    }

    private void addButton(ImageIcon image, String tooltip, ActionListener listener) {
        addButton(tooltip, listener).setIcon(image);
    }
    
    private ImageIcon loadImage(String filepath) {
        try {
            ImageIcon image = new ImageIcon(ImageIO.read(getClass().getResource(filepath)));
            return image;
        } catch (IOException ex) {
            //System.out.println("Couldn't load images from filepath: "+filepath);
        }
        return null;
    }

    public ZoomButtonsGUI(final Controller controller) {
        super();
        panel = new JPanel(new SpringLayout());
        
        String zoomTip = "Zooms in on the map";
        addButton(loadImage("/resources/images/zoomIn.png"), zoomTip, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.draw(controller.viewport.zoomBy(0.2f));
            }
        });
        String zoomOutTip = "Zooms out";
        addButton(loadImage("/resources/images/zoomOut.png"), zoomOutTip, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.draw(controller.viewport.zoomBy(-0.2f));
            }
            
        });
        String resetTip = "Zooms back out to the whole map";
        addButton(loadImage("/resources/images/reset.png"), resetTip, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.draw(controller.viewport.zoomTo(1));
            }
        });
        SpringUtilities.makeCompactGrid(panel, numberOfButtons, 1, 0, 0, 5, 5); // Prepare the grid :D
        this.add(panel);
        repaint();
    }
}
