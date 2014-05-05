/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Daniel
 */
public class SearchStuff extends JPanel {

    private JTextField inputField; //Text Field for searching capabilities
    private static final int searchDelay = 200; //Milliseconds
    private Timer typeTimer;
    private RoadPart[] edges;
    //private ArrayList<String> roadList;
    private DefaultListModel listModel;
    private JList roadJList;
    private String selectedRoad;
    private ArrayList<String> roadListName;
    private int letterCount = 0; //Value to check if letters in textfield is written by us, and not a road taken from the list

    public SearchStuff(RoadPart[] roads) {
        edges = roads;
        listModel = new DefaultListModel();
        roadJList = new JList(listModel);
        inputField = new JTextField("Search Field");
        inputField.setPreferredSize(new Dimension(130, 50));
        roadListName = new ArrayList<>();
        this.add(inputField);
        this.add(roadJList);
        setTimer();
        addListeners();
    }

    //A
    private void setTimer() {
        typeTimer = new Timer(searchDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSearch();
            }
        });
        typeTimer.setRepeats(false);
    }

    private void addListeners() {
         roadJList.addListSelectionListener(new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (roadJList.getSelectedIndex() != -1) {
                    int i = roadJList.getSelectedIndex();
                    selectedRoad = listModel.get(i).toString();
                    roadListName.add(selectedRoad);
                    inputField.setText(listModel.get(i).toString());
                    letterCount = 0;
                } else if (inputField.getText().isEmpty()) {
                    inputField.setText("");
                    listModel.clear();
                    roadJList.clearSelection();
                } } });
        

        inputField.addFocusListener(new FocusListener() {
            
            @Override
            public void focusGained(FocusEvent e) {
                inputField.setText(""); //Sets the text to nothing if focus is gained
                listModel.clear();
                roadListName.clear();
            }

            @Override
            public void focusLost(FocusEvent e) {
            } });

        inputField.getDocument().addDocumentListener(new DocumentListener() { //Add a listener to check if something is written
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTyping();
                letterCount++;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTyping();
                letterCount--;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTyping();
            } });
    }

    public void checkTyping() {
        if (!typeTimer.isRunning()) {
            typeTimer.start(); //You sure you're finished typing?
        } else {
            typeTimer.restart(); //Typing again, better wait some more!
        }
        listModel.clear();
    }

    public void startSearch() {
        String searchText = inputField.getText().toLowerCase();
        boolean enoughInput = inputField.getText().length() >= 3;
        HashSet<Integer> usedZips = new HashSet<>();
        
        if (letterCount > 0) { //Making sure the search happens when the letters in the textfield are written by us
            for (int i = 0; i < edges.length; i++) {
                String edgeName = edges[i].name;
                int edgeZip = edges[i].leftZip;
                
                if (enoughInput && edgeName.toLowerCase().startsWith(searchText)) {
                    if (listModel.isEmpty()) {
                        listModel.addElement(edgeName + " - " + edgeZip);
                        usedZips.add(edgeZip);
                    } else if(!usedZips.contains(edgeZip)) {
                        listModel.addElement(edgeName + " - " + edgeZip);
                        usedZips.add(edgeZip);
                    } 
                    roadJList.setVisible(true);
                    
                    if(listModel.size()>= 3) { break; }
                }
            }
        }
        roadJList.setVisible(true);
    }
}
