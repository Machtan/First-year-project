/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Isabella
 */
public class ZoomButtonsGUI extends JPanel {

    //private JPanel zoomPanel = new JPanel();
    private JButton zoomResetBtn;
    private JButton zoomOutBtn;
    private JButton zoomInBtn;
    private CResizeHandler resizeHandler;
    private Controller controller;

    public ZoomButtonsGUI(CResizeHandler resizeHandler, Controller controller) {
        this.controller = controller;
        this.resizeHandler = resizeHandler;

        zoomInBtn = new JButton("+");
        zoomInBtn.setToolTipText("You can also zoom in by clicking '+' on your keyboard.");
        zoomInBtn.addActionListener(new ZoomListener());
        zoomInBtn.setBorder(BorderFactory.createBevelBorder(5, Color.lightGray, Color.yellow));
        zoomInBtn.setFont(new Font("Verdana", Font.BOLD, 18));

        zoomOutBtn = new JButton("-");
        zoomOutBtn.setToolTipText("You can also zoom out by clicking '-' on your keyboard");
        zoomOutBtn.addActionListener(new ZoomListener());
        zoomOutBtn.setBorder(BorderFactory.createBevelBorder(5, Color.lightGray, Color.yellow));
        zoomOutBtn.setFont(new Font("Verdana", Font.BOLD, 18));

        zoomResetBtn = new JButton("Reset");
        zoomResetBtn.setToolTipText("Zooms back out to the whole map.");
        zoomResetBtn.addActionListener(new ZoomListener());
        zoomResetBtn.setBorder(BorderFactory.createBevelBorder(5, Color.lightGray, Color.yellow));

        setLayout(new GridLayout(0, 1));
        //Fill panels
        JPanel fillPn1 = new JPanel();
        fillPn1.setBackground(Color.white);
        JPanel fillPn2 = new JPanel();
        fillPn2.setBackground(Color.white);
        JPanel fillPn3 = new JPanel();
        fillPn3.setBackground(Color.white);
        JPanel fillPn4 = new JPanel();
        fillPn4.setBackground(Color.white);
        JPanel fillPn5 = new JPanel();
        fillPn5.setBackground(Color.white);

        //setLayout(new BoxLayout(this,  BoxLayout.PAGE_AXIS));
        add(zoomInBtn);
        add(zoomOutBtn);
        add(zoomResetBtn);
        add(fillPn1);
        add(fillPn2);
        add(fillPn3);
        add(fillPn4);
        add(fillPn5);
    }

    private class ZoomListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == zoomInBtn) {
                System.out.println("Zooming in");
                resizeHandler.zoomIn();

            } else if (e.getSource() == zoomOutBtn) {
                System.out.println("Zooming out");
                resizeHandler.zoomOut();

            } else if (e.getSource() == zoomResetBtn) {
                System.out.println("Resetting zoom");
                controller.resetView();
            }
            zoomInBtn.setFocusable(false);
            zoomOutBtn.setFocusable(false);
            zoomResetBtn.setFocusable(false);
        }
    }
}
