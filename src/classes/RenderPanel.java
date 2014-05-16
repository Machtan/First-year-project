/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import enums.RoadType;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Daniel
 */
public class RenderPanel extends JPanel {

    private final Controller controller;
    private final ArrayList<RoadType> defined;
    private final ArrayList<RoadType> drawnTypes;
    
    public RenderPanel(ArrayList<RoadType> drawnTypes, Controller cont) {
        super();
        controller = cont;
        defined = new ArrayList<>();
        this.drawnTypes = drawnTypes;
        
        addOption("Highways and exits", RoadType.Highway, RoadType.HighwayExit);
        addOption("Prime routes", RoadType.PrimeRoute);
        addOption("Paths", RoadType.Path);
        addOption("Ferry routes", RoadType.Ferry);
    }
    
    private void addOption(String description, RoadType... types) {
        JCheckBox option = new JCheckBox(description);
        option.setMnemonic(MouseEvent.BUTTON1);
        option.setSelected(true);
        for (RoadType type : types) {
            defined.add(type);
        }
        new CheckListener(option, types);
        option.setFocusable(false);
        add(option);
    }
    
    private class CheckListener implements ItemListener {
       
        private final RoadType[] types;
        
        public CheckListener(JCheckBox parent, RoadType... types) {
            this.types = types;
            parent.addItemListener(this);
        }
        
        /**
         * Changes the color of the given road type and renders the changes in 
         * the view
         */
        private void refresh() {
            System.out.println("Changing the color from the Render panel");
            controller.redraw();
        }
        
        private void onChecked(Object source) {
            for (RoadType type : types) {
                //ins.addMapping(selected, type);
                if (!drawnTypes.contains(type)) {
                    drawnTypes.add(type);
                }
            }
            refresh();
        }
        
        private void onUnchecked(Object source) {
            for (RoadType type : types) {
                //ins.addMapping(deselected, type);
                drawnTypes.remove(type);
            }
            refresh();
        }
        
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                onChecked(e.getItemSelectable());
            } else {
                onUnchecked(e.getItemSelectable());
            }                   
        }
    }
}
