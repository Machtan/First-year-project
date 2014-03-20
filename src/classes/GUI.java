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
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Daniel
 */
public class GUI extends JPanel {
    
    private static JCheckBox HighwayCheck = new JCheckBox("Highways and exits");
    private static JCheckBox PrimeRouteCheck = new JCheckBox("Prime routes");
    private static JCheckBox PathCheck = new JCheckBox("Paths");
    private static JPanel mainPanel = new JPanel();
    
    public static JPanel GUI(final RenderInstructions instr) {
        
                
        HighwayCheck.setMnemonic(MouseEvent.BUTTON1);
        HighwayCheck.setSelected(true);
        
        
        PrimeRouteCheck.setMnemonic(MouseEvent.BUTTON1);
        PrimeRouteCheck.setSelected(true);
        
        
        PathCheck.setMnemonic(MouseEvent.BUTTON1);
        PathCheck.setSelected(true);
        
        
        ItemListener listener = new ItemListener() {

            @Override
             public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        
        if(source == HighwayCheck) {
            instr.setColor(RoadType.Highway, Color.red);
            instr.setColor(RoadType.HighwayExit, Color.red);
            
        } else if(source == PrimeRouteCheck) {
            instr.setColor(RoadType.PrimeRoute, Color.blue);
            
        } else if(source == PathCheck) {
            instr.setColor(RoadType.Path, Color.green);
            
        }
        
            if(e.getStateChange() == ItemEvent.DESELECTED) {
                if(source == HighwayCheck) {
                    instr.setColor(RoadType.Highway, Color.black);
                    instr.setColor(RoadType.HighwayExit, Color.black);
                    
                } else if(source == PrimeRouteCheck) {
                    instr.setColor(RoadType.PrimeRoute, Color.black);
                   
                } else if(source == PathCheck) {
                    instr.setColor(RoadType.Path, Color.black);
                }
            }
        }
    };
        
        HighwayCheck.addItemListener(listener);
        PrimeRouteCheck.addItemListener(listener);
        PathCheck.addItemListener(listener);
        
        mainPanel.add(HighwayCheck);
        mainPanel.add(PrimeRouteCheck);
        mainPanel.add(PathCheck);
        
        return mainPanel;
    }
}
