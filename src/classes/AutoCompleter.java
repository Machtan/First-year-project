/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
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
    private RoadPart[] edges;
    private String selectedRoad;
    private int letterCount = 0; //Value to check if letters in textfield is written by us, and not a road taken from the list
    private JPopupMenu pop;

    public AutoCompleter(RoadPart[] roads) {
        edges = roads;
        inputField = this;
        inputField.setMaximumSize(new Dimension(130,22));
        inputField.setPreferredSize(new Dimension(100, inputField.getPreferredSize().height));
        System.out.println("Fieldsize height: " +inputField.getPreferredSize().height + " width: "+
                inputField.getPreferredSize().width);
        //inputField.setHorizontalAlignment(JTextField.CENTER);
        pop = new JPopupMenu();
        pop.setVisible(false);
        pop.setFocusable(false);
        //this.setLayout(new GridLayout(1, 2));
        //is.add(inputField);
        addListeners();
        //this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.red, Color.yellow));
        setTimer();
    }

    //Set what the timer does
    private void setTimer() {
        typeTimer = new Timer(searchDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //setPopMenu();
                startSearch();
                if (pop != null) {
                    System.out.println("Antal items: " + pop.getSubElements().length);
                }
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

    public void checkTyping() {
        if (!typeTimer.isRunning()) {
            typeTimer.start(); //You sure you're finished typing?
        } else {
            typeTimer.restart(); //Typing again, better wait some more!
        }
    }

    private void setPopMenu() {
        inputField.setComponentPopupMenu(pop);
        pop.show(inputField, inputField.getX(), inputField.getY() + inputField.getHeight());
        pop.setFocusable(false);
        pop.setVisible(false);
        resizePopMenu();
    }

    private void resizePopMenu() {
        pop.setPreferredSize(new Dimension(inputField.getWidth(), inputField.getHeight() * pop.getSubElements().length / 2));
    }

    private MenuItem createMenuItem(RoadPart r, String text) {
        RoadPart tempR = r;
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
                //DisplayRoute.setAddress(item.roadPart);
                inputField.setText(item.getText());
                removeItems();
            }
        });
    }

    //Searches through the list
    private void startSearch() {
        if (inputField.getText().length() == 0) {
            removeItems();
        }
        if (inputField.getText().length() >= 3) {

            String searchText = inputField.getText().toLowerCase();

            for (int i = 0; i < edges.length; i++) {
                String edgeName = edges[i].name;
                int edgeZip = edges[i].leftZip;

                if (edgeName.toLowerCase().startsWith(searchText)) {

                    if (pop.getSubElements().length == 0) {
                        pop.add(createMenuItem(edges[i], edgeName + " - " + edgeZip));
                    } else {
                        for (MenuElement element : pop.getSubElements()) {
                            MenuItem item = (MenuItem) element;
                            if (!edgeName.equals(item.roadPart.name) && edgeZip != item.roadPart.leftZip
                                    || edgeName.equals(item.roadPart.name) && edgeZip != item.roadPart.leftZip) {
                                pop.add(createMenuItem(edges[i], edgeName + " - " + edgeZip));
                            }
                        }
                    }
                    setPopMenu();
                    pop.setVisible(true);

                    if (pop.getSubElements().length >= 3) {
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
        inputField.revalidate();
        inputField.repaint();
    }
}
