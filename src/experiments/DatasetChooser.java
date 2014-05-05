package experiments;

import classes.Controller;
import classes.Datafile;
import classes.Loader;
import classes.Model;
import classes.OptimizedView;
import classes.ProgressBar;
import classes.UTFLoader;
import interfaces.ILoader;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * The DatasetChooser class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 12-Apr-2014
 */
public class DatasetChooser extends JFrame {
    
    private JPanel midPanel;
    private boolean initialized = false;
    
    /**
     * Constructor for the DatasetChooser class
     */
    public DatasetChooser () {
        super();
        initComponents();
        
        Datafile krakRoads = new Datafile("resources/roads.txt", 812301, 
            "Loading road data...");
        Datafile krakInters = new Datafile("resources/intersections.txt", 
            675902, "Loading intersection data...");
        Datafile osmRoads = new Datafile("resources/osm_roads.txt", 
            12355950, "Loading OSM roads...");
        Datafile osmInters = new Datafile("resources/osm_intersections.txt",
            14118721, "Loading OSM intersections...");
        
        addChoice("Krak data", "A set of data compiled by KRAK.", new Loader(), 
                krakInters, krakRoads);
        addChoice("Krak data II", "The sequel to the popular data set!", new Loader(), 
                krakInters, krakRoads);
        addChoice("Krak data III Shut Up And Jam Gaiden - The videogame", 
                "The epitome of the Krak® datasets!", new Loader(), 
                krakInters, krakRoads);
        addChoice("OSM data", "Slower but more accurate.", new Loader(), 
                osmInters, osmRoads);
        
        //"Loading road data...", 812301
        // ins: 675902
        
        //osm-ins: 14118721
        
        
        pack();
        setVisible(true);
    }
    
    private void initComponents() {
        if (initialized) { return; }
        setTitle("Choose a dataset");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        midPanel = new JPanel(new GridLayout(1,0));
        add(midPanel);
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
    
    private void addChoice(String name, String description, ILoader loader, Datafile... files) {
        JPanel panel = new JPanel(new BorderLayout());
        System.out.println("Files:");
        for (Datafile file : files) {
            System.out.println("- "+file);
        }
        // Create the border
        int pad = 5;
        Border bbev = BorderFactory.createLoweredBevelBorder();
        Border bpad = BorderFactory.createEmptyBorder(pad,pad,pad,pad);
        panel.setBorder(BorderFactory.createCompoundBorder(bpad, bbev));
        
        // Set the panel to be focusable
        //panel.setFocusable(true);
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel(name);
        JLabel sizeInfo = new JLabel("Size: "+getSizeString(files));
        
        //title.setFont(new Font());
        // Do some prettifying of the title here, please :)
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(sizeInfo, BorderLayout.WEST);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextField descField = new JTextField();
        descField.setText(description);
        descField.setEnabled(false);
        
        JButton chooseButton = new JButton("Choose");
        final JFrame chooser = this;
        
        final String actionName = name;
        final Datafile[] actionFiles = files;
        final ILoader actionLoader = loader;
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("The action '"+actionName+"' was chosen!");
                chooser.setVisible(false);
                setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
                chooser.dispose();
                ProgressBar bar = new ProgressBar();
                //bar.setEnabled(true);
                //bar.paintComponents(bar.getGraphics());
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                    
                }
                startProgram(actionLoader.loadData(bar, actionFiles));
                bar.close();
            }
        });
        bottomPanel.add(descField, BorderLayout.NORTH);
        bottomPanel.add(chooseButton, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        midPanel.add(panel);
    }
    
    private void startProgram(Model model) {
        System.out.println("Starting the program");
        Dimension minSize = new Dimension(600,500);
        OptimizedView view = new OptimizedView(minSize);
        Controller controller = new Controller(view, model); 
        controller.setMinimumSize(minSize);
        controller.pack();
        controller.redraw();
        controller.setVisible(true);
    }
    
    
    
    public static void main(String[] args) {
        //IProgressBar progbar = new ProgressBar();
        DatasetChooser chooser = new DatasetChooser();
    }
}
