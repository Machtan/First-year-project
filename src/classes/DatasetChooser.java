package classes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * The DatasetChooser class is a window that lets the user choose between
 * different data sets, in this case only two.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 12-Apr-2014
 */
public class DatasetChooser extends JFrame {
    
    private JPanel midPanel;
    private boolean initialized = false;
    private static final Font headerFont = new Font("Lucida", Font.PLAIN, 24);
    /**
     * Constructor for the DatasetChooser class
     */
    public DatasetChooser () {
        super();
        initComponents();
        addChoice("Krak", "<html>A set of data compiled by KRAK. "+
                "<br/>This data set is rather simple, and thus faster than the OSM set. "+
                "It contains all of Denmark, and most of Scania as well."+
                "<br/><br/>Note: Requires 1GB of RAM to load.</html>", NewLoader.krakdata);
        addChoice("OpenStreetMap", "<html>A huge crowd-collected data set. "+
                "<br/>The map is only of Denmark, but should be a bit more detailed than "+
                "the Krak data set. "+
                "<br/>For more information, please see <br/>www.openstreetmap.org."+
                "<br/><br/>Note: Requires 2GB of RAM to load.</html>", NewLoader.osmdata);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        if (initialized) { return; }
        setTitle("Choose a dataset");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        midPanel = new JPanel(new GridLayout(1,0));
        add(midPanel);
        setPreferredSize(new Dimension(500,300));
        //setUndecorated(true);
        initialized = true;
    }
    
    /**
     * Returns a string with the size of the given files
     * @param files The files whose size to find
     * @return A string with the combined size of the files as text, eg "5MiB"
     */
    private String getSizeString(Datafile[] files) {
        return "0MB <TESTING>";
    }
    
    private void addChoice(String name, String description, final Datafile file) {
        JPanel panel = new JPanel(new BorderLayout());
        // Create the border
        int pad = 5;
        Border bpad = BorderFactory.createEmptyBorder(pad,pad,pad,pad);
        panel.setBorder(bpad);
        
        // Set the panel to be focusable
        //panel.setFocusable(true);
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel(name, SwingConstants.CENTER);
        title.setFont(headerFont);
        topPanel.add(title, BorderLayout.NORTH);
        //topPanel.setMaximumSize(title.getPreferredSize());
        //topPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        JLabel descField = new JLabel();
        descField.setText(description);
        topPanel.add(descField, BorderLayout.SOUTH);
        //topPanel.setMaximumSize(new Dimension(250, 30));
        
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        
        JButton chooseButton = new JButton("Select");
        final JFrame chooser = this;
        
        final String actionName = name;
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("The action '"+actionName+"' was chosen!");
                startProgram(file);
            }
        });
        bottomPanel.add(chooseButton, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        midPanel.add(panel);
    }
    
    private void startProgram(final Datafile file) {
        setVisible(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dispose();
        setEnabled(false);
        new Thread(new Runnable() {
            public void run() {
                Model model = NewLoader.loadData(file);
               // System.out.println("Starting the program");
                Dimension viewSize = new Dimension(600,400);
                OptimizedView view = new OptimizedView(viewSize, Controller.defaultInstructions);
                Controller controller = new Controller(view, model); 
                controller.setMinimumSize(new Dimension(900,600));
                controller.setVisible(true);
               // controller.draw(controller.viewport.zoomTo(1));
            }
        }).start();
    }
    
    
    
    public static void main(String[] args) {
        //IProgressBar progbar = new ProgressBar();
        DatasetChooser chooser = new DatasetChooser();
    }
}
