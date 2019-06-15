package org.huynv.facepica;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Confirm extends JFrame {
    private JTextField topicanIdText;
    private JButton confirmButton;
    private JPanel rootPanel;

    public Confirm() {
        add(rootPanel);

        setTitle("Nhập thông tin của bạn!");
        setSize(400,500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(rootPanel, "Cliked");
            }
        });
    }
}
