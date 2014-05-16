/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
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
public class AutoCompleter extends JTextField {

    private JTextField inputField; //Text Field for searching capabilities
    private static final int searchDelay = 200; //Milliseconds
    private Timer typeTimer;
    private Road.Edge[] edges;
    private String selectedRoad;
    private int letterCount = 0; //Value to check if letters in textfield is written by us, and not a road taken from the list
    private JPopupMenu pop;
    private Road.Edge foundRoad;
    private HashSet<Integer> usedZips;

    public AutoCompleter(Road.Edge[] roads) {
        edges = roads;
        inputField = this;
        inputField.setMaximumSize(new Dimension(180, 22));
        inputField.setPreferredSize(inputField.getMaximumSize());
        pop = new JPopupMenu();
        pop.setVisible(false);
        pop.setFocusable(false);
        addListeners();
        setTimer();
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

    private MenuItem createMenuItem(Road.Edge r, String text) {
        Road.Edge tempR = r;
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
                foundRoad = item.Road.Edge;
                inputField.setText(item.getText());
                removeItems();
            }
        });
    }
    
    /*private void checkRNo(RoadPart r){
        r.
    }*/

    //Searches through the list
    private void startSearch() {
        if (inputField.getText().length() >= 3) {
            //removeItems();
            System.out.println("Starting search");
            
            usedZips = new HashSet<Integer>();
            String searchText = inputField.getText().toLowerCase();
            //Trying to start something with regex...
            //searchText = searchText.replace(' ',',');
            //searchText = searchText.replace(',', ':');
            //searchText.indexOf("\\d");
            //String search[] = searchText.split(",");

            //Starting linear search through all the roads
            for (int i = 0; i < edges.length; i++) {
                String edgeName = edges[i].Road.this.name;
                int edgeZip = edges[i].leftZip;
                int loLNo = edges[i].sLeftNum; //Lowest house no. on the left
                int hiLNo = edges[i].eLeftNum; //Highest house no. on the left
                int loRNo = edges[i].sRightNum; //Lowest house no. on the right
                int hiRNo = edges[i].eRightNum; //Highest house no. on the right

                if (edgeName.toLowerCase().startsWith(searchText)) {

                    //Just add it when there's no other elements
                    if (pop.getSubElements().length == 0) {
                        pop.add(createMenuItem(edges[i], edgeName + " - " + edgeZip));
                        usedZips.add(edgeZip);
                        
                    } else {
                        Boolean didItMatch = true;

                        //Check if the road already is added
                        for (MenuElement element : pop.getSubElements()) {
                            MenuItem item = (MenuItem) element;
                            if (edgeName.equalsIgnoreCase(item.roadPart.name) && usedZips.contains(edgeZip)) {
                                //The road is already added
                                didItMatch = true;
                                break;
                            } else {
                                didItMatch = false;
                            }
                        }
                        //Add the road
                        if (!didItMatch) {
                            pop.add(createMenuItem(edges[i], edgeName + " - " + edgeZip));
                            usedZips.add(edgeZip);
                        }
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
        //foundRoad = null;
        inputField.revalidate();
        inputField.repaint();
    }

    public RoadPart getRoad() {
        return foundRoad;
    }
}
