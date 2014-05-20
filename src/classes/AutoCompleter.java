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

    private static final int searchDelay = 200; //Milliseconds
    private Timer typeTimer;
    private Road.Edge[] edges;
    private String selectedRoad;
    private int letterCount = 0; //Value to check if letters in textfield is written by us, and not a road taken from the list
    private JPopupMenu pop;
    private Road foundRoad;
    private String edgeToAdd;
    private ArrayList<Road> edgesList = new ArrayList<>();
    private final boolean startPointField;
    private final OptimizedView view;
    //private HashMap<String, Integer> addrMap;
    private HashSet<String> usedRoadNames;

    public AutoCompleter(Model model, OptimizedView view, boolean startPointField) {
        model.getAllRoads(this);
        //edges = roads;
        this.startPointField = startPointField;
        this.view = view;
        setMaximumSize(new Dimension(180, 22));
        setPreferredSize(getMaximumSize());
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
                startSearch();
            }
        });
        typeTimer.setRepeats(false);
    }

    //Add listeners
    private void addListeners() {

        //add a focus listener to the textfield
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setText(""); //Sets the text to nothing if focus is gained
                removeItems();
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("Im losing focus!!!");
            }
        });

        //Add a listener to check if something is written
        getDocument().addDocumentListener(new DocumentListener() {
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
        setComponentPopupMenu(pop);
        pop.show(this, 0, 0 + getHeight());
        pop.setFocusable(false);
        pop.setVisible(false);
        resizePopMenu();
    }

    private void resizePopMenu() {
        pop.setPreferredSize(new Dimension(getWidth(), getHeight() * pop.getSubElements().length));
    }

    private MenuItem createMenuItem(Road r, String text) {
        Road tempR = r;
        
        MenuItem item = new MenuItem(r, text);
        item.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
        addMenuListener(item);
        item.setPreferredSize(new Dimension(getWidth(), getHeight()));
        return item;
    }
    
    private void addMenuListener(final MenuItem item) {
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                foundRoad = item.roadPart;
                if (startPointField) {
                    view.setPathStart(foundRoad.nodes[0]);
                } else {
                    view.setPathEnd(foundRoad.nodes[0]);
                }
                setText(item.getText());
                removeItems();
            }
        });
    }
    
     private void startSearch() {
        if (getText().length() >= 3) {
            
            HashSet<String> usedRoads = new HashSet<>();
            String searchText = getText().toLowerCase();
            
            //Starting linear search through all the roads
            for (Road edge : edgesList) {
                String edgeName = edge.name;
                int edgeZip = edge.zipCode;
                String toAdd = edgeName+edgeZip;

                if (edgeName.toLowerCase().startsWith(searchText)) {

                    //Just add it when there's no other elements
                    if (pop.getSubElements().length == 0) {
                        pop.add(createMenuItem(edge, edgeName + " - " + edgeZip));
                        usedRoads.add(toAdd);

                        //Only shows one option for each city
                    } else if (!usedRoads.contains(toAdd)) {
                        pop.add(createMenuItem(edge, edgeName + " - " + edgeZip));
                        usedRoads.add(toAdd);
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

    private void removeItems() {
        pop.removeAll();
        pop.setVisible(false);
        
        revalidate();
        repaint();
    }

    public Road getRoad() {
        return foundRoad;
    }
    
    public void setRoad(Road road) {
        foundRoad = road;
        setText(road.name+" - "+road.zipCode);
    }

    @Override
    public void startStream() { }

    @Override
    public void startStream(IProgressBar bar) { }

    @Override
    public void endStream() { }

    @Override
    public void add(Road obj) {
            edgesList.add(obj);  
    }
}
