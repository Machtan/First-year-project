/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import interfaces.IProgressBar;
import interfaces.StreamedContainer;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Daniel
 */
public class AutoCompleter extends JTextField implements StreamedContainer<Road> {

    private JTextField inputField; //Text Field for searching capabilities
    private static final int searchDelay = 200; //Milliseconds
    private Timer typeTimer;
    private Road.Edge[] edges;
    private String selectedRoad;
    private int letterCount = 0; //Value to check if letters in textfield is written by us, and not a road taken from the list
    private JPopupMenu pop;
    private Road foundRoad;
    private String edgeToAdd;
    private HashSet<Integer> usedZips;
    private HashSet<String> addedRoads;
    private ArrayList<Road> edgesList = new ArrayList<>();
    //private HashMap<String, Integer> addrMap;
    //private HashSet<String> usedRoadNames;

    public AutoCompleter(Model model) {
        model.getAllRoads(this);
        //edges = roads;
        inputField = this;
        inputField.setMaximumSize(new Dimension(180, 22));
        inputField.setPreferredSize(inputField.getMaximumSize());
        pop = new JPopupMenu();
        pop.setVisible(false);
        pop.setFocusable(false);
        addListeners();
        setTimer();
        //startStream();
    }

    //Set what the timer does
    private void setTimer() {
        typeTimer = new Timer(searchDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSearch(edgesList);
            }
        });
        typeTimer.setRepeats(false);
    }

    //Add listeners
    private void addListeners() {

        //add a focus listener to the textfield
        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                inputField.setText(""); //Sets the text to nothing if focus is gained
                removeItems();
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("Im losing focus!!!");
            }
        });

        //Add a listener to check if something is written
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                removeItems();
                checkTyping();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                removeItems();
                checkTyping();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTyping();
            }
        });
    }

    private void checkTyping() {
        if (!typeTimer.isRunning()) {
            typeTimer.start(); //You sure you're finished typing?
        } else {
            typeTimer.restart(); //Typing again, better wait some more!
        }
    }

    private void setPopMenu() {
        inputField.setComponentPopupMenu(pop);
        pop.show(inputField, 0, 0 + inputField.getHeight());
        pop.setFocusable(false);
        pop.setVisible(false);
        resizePopMenu();
    }

    private void resizePopMenu() {
        pop.setPreferredSize(new Dimension(inputField.getWidth(), inputField.getHeight() * pop.getSubElements().length));
    }

    private MenuItem createMenuItem(Road r, String text) {
        Road tempR = r;
        
        MenuItem item = new MenuItem(r, text);
        item.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
        addMenuListener(item);
        item.setPreferredSize(new Dimension(inputField.getWidth(), inputField.getHeight()));
        return item;
    }

    private void addMenuListener(final MenuItem item) {
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                foundRoad = item.roadPart;
                inputField.setText(item.getText());
                removeItems();
            }
        });
    }
    
     private void startSearch(ArrayList<Road> edges2) {
        edges2 = edgesList;
        if (inputField.getText().length() >= 3) {
            //removeItems();
            System.out.println("Starting search");

            usedZips = new HashSet<Integer>();
            //usedRoadNames = new HashSet<String>();
            addedRoads = new HashSet<>();
            String searchText = inputField.getText().toLowerCase();

            //Starting linear search through all the roads
            for (Road edge : edges2) {
                String edgeName = edge.name;
                int edgeZip = edge.zipCode;

                if (edgeName.toLowerCase().startsWith(searchText)) {

                    //Just add it when there's no other elements
                    if (pop.getSubElements().length == 0) {
                        pop.add(createMenuItem(edge, edgeName + " - " + edgeZip));
                        usedZips.add(edgeZip);
                        
                        System.out.println(edgeToAdd);
                        //addedRoads.add(edgeName);
                        //usedRoadNames.add(edgeName);

                        //Only shows one option for each city
                    } else if (!usedZips.contains(edgeZip)) {
                        pop.add(createMenuItem(edge, edgeName + " - " + edgeZip));
                        usedZips.add(edgeZip);
                       
                        
                        //addedRoads.add(edgeToAdd);
                    }

                    setPopMenu();
                    //show up to 18 possible roads
                    if (pop.getSubElements().length >= 18) {
                        pop.setVisible(true);
                        return;

                    } else if (pop.getSubElements().length != 0) {
                        pop.setVisible(true);

                    }
                }
            }
        }
    }
/*
    //Searches through the list
    private void startSearch(ArrayList<Road.Edge> edge) {
        edge = edgesList;
        if (inputField.getText().length() >= 3) {
            //removeItems();
            System.out.println("Starting search");

            //usedZips = new HashSet<Integer>();
            //usedRoadNames = new HashSet<String>();
            addedRoads = new HashSet<>();
            String searchText = inputField.getText().toLowerCase();

            //Starting linear search through all the roads
            for (int i = 0; i < edges.length; i++) {
                String edgeName = edge.parent().name;
                int edgeZip = edge.parent().zipCode;

                if (edgeName.toLowerCase().startsWith(searchText)) {

                    //Just add it when there's no other elements
                    if (pop.getSubElements().length == 0) {
                        pop.add(createMenuItem(edge, edgeName + " - " + edgeZip));
                        //usedZips.add(edgeZip);
                        addedRoads.add(edges[i].parent());
                        //usedRoadNames.add(edgeName);

                        //Only shows one option for each city
                    } else if (!addedRoads.contains(edges[i].parent())) {
                        pop.add(createMenuItem(edges[i], edgeName + " - " + edgeZip));
                        //usedZips.add(edgeZip);
                        addedRoads.add(edges[i].parent());
                    }

                    setPopMenu();
                    //show up to 18 possible roads
                    if (pop.getSubElements().length >= 18) {
                        pop.setVisible(true);
                        return;

                    } else if (pop.getSubElements().length != 0) {
                        pop.setVisible(true);

                    }
                }
            }
        }
    }
*/
    private void removeItems() {
        pop.removeAll();
        pop.setVisible(false);
        //foundRoad = null;
        inputField.revalidate();
        inputField.repaint();
    }

    public Road getRoad() {
        return foundRoad;
    }

    @Override
    public void startStream() {
        
    }

    @Override
    public void startStream(IProgressBar bar) {
        //
    }

    @Override
    public void endStream() {

    }

    @Override
    public void add(Road obj) {
            edgesList.add(obj);
             
    }
}
