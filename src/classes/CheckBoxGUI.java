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
public class CheckBoxGUI extends JPanel {

    private static JCheckBox HighwayCheck = new JCheckBox("Highways and exits");
    private static JCheckBox PrimeRouteCheck = new JCheckBox("Prime routes");
    private static JCheckBox PathCheck = new JCheckBox("Paths");
    private static JPanel mainPanel = new JPanel();

    public static JPanel makeGUI(final RenderInstructions instr, final Controller cont) {

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

                if (source == HighwayCheck) {
                    instr.setColor(RoadType.Highway, Color.red);
                    instr.setColor(RoadType.HighwayExit, Color.red);
                    cont.redraw();
                } else if (source == PrimeRouteCheck) {
                    instr.setColor(RoadType.PrimeRoute, new Color(255, 170, 100));
                    cont.redraw();
                } else if (source == PathCheck) {
                    instr.setColor(RoadType.Path, Color.green);
                    cont.redraw();
                }

                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    if (source == HighwayCheck) {
                        instr.setColor(RoadType.Highway, instr.getVoidColor());
                        instr.setColor(RoadType.HighwayExit, instr.getVoidColor());
                        cont.redraw();
                    } else if (source == PrimeRouteCheck) {
                        instr.setColor(RoadType.PrimeRoute, instr.getVoidColor());
                        cont.redraw();
                    } else if (source == PathCheck) {
                        instr.setColor(RoadType.Path, instr.getVoidColor());
                        cont.redraw();
                    }
                }
                //Sets the checkboxes unfocused, so the the Controller is focussed
                HighwayCheck.setFocusable(false);
                PrimeRouteCheck.setFocusable(false);
                PathCheck.setFocusable(false);                      
                
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
