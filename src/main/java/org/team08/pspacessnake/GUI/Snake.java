package org.team08.pspacessnake.GUI;

import javax.swing.*;
import java.awt.*;


public class Snake extends JFrame {

    public Snake() {

        this.add(new Board());

        this.setResizable(false);
        this.pack();

        this.setTitle("Snake");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame ex = new Snake();
                ex.setVisible(true);
            }
        });
    }
}