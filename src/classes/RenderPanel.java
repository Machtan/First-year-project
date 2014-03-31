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
import java.util.Collection;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Daniel
 */
public class RenderPanel extends JPanel {

    private final RenderInstructions ins;
    private final Controller controller;
    private final ArrayList<RoadType> defined;
    
    private void addOption(String description, RoadType... types) {
        Color color = ins.getColor(types[0]);
        JCheckBox option = new JCheckBox(description);
        option.setMnemonic(MouseEvent.BUTTON1);
        option.setSelected(true);
        for (RoadType type : types) {
            defined.add(type);
        }
        new CheckListener(option, color, ins.getVoidColor(), types);
        option.setFocusable(false);
        add(option);
    }
    
    public RenderPanel(RenderInstructions ins, Controller cont) {
        super();
        this.ins = ins;
        controller = cont;
        defined = new ArrayList<>();
        
        addOption("Highways and exits", RoadType.Highway, RoadType.HighwayExit);
        addOption("Prime routes", RoadType.PrimeRoute);
        addOption("Paths", RoadType.Path);
        addOption("Ferry routes", RoadType.Ferry);
    }
    
    private class CheckListener implements ItemListener {
        
        private final Color selected;
        private final Color deselected;
        private final RoadType[] types;
        
        public CheckListener(JCheckBox parent, Color selected, Color deselected, RoadType... types) {
            this.selected = selected;
            this.deselected = deselected;
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
                ins.setColor(type, selected);
            }
            refresh();
        }
        
        private void onUnchecked(Object source) {
            for (RoadType type : types) {
                ins.setColor(type, deselected);
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
