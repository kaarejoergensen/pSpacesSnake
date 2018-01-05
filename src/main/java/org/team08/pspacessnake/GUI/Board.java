package org.team08.pspacessnake.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;



public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 800;
    private final int B_HEIGHT = 800;
    private final int DOT_SIZE = 2;
    private final int ALL_DOTS = B_WIDTH*B_HEIGHT;
    private final int RAND_POS = 29;
    private final int DELAY = 35;
    private final int INIT_X = 500;
    private final int INIT_Y = 50;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;

    private boolean leftDirection = false;
    private boolean rightDirection = false;
    private Double direction = 0d;
    private boolean inGame = true;

    private Timer timer;
    
    private Point2D.Double myPos = new Point2D.Double(INIT_X, INIT_Y);
     
    
    

    public Board() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        initGame();
    }


    private void initGame() {

        dots = 0;

        x[0] = 500;
        y[0] = 50;

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        if (inGame) {

            for (int z = dots; z > 0; z--) {
                g.fillOval(x[z], y[z], 5,5);
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {

        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void move() {

//        for (int z = dots; z > 0; z--) {
//            x[z] = x[(z - 1)];
//            y[z] = y[(z - 1)];
//        }
        if (leftDirection) {
            direction -= 10 + 360;
        }

        if (rightDirection) {
            direction += 10;
        }
        direction %= 360;

        double angle = direction * (Math.PI / 180); //convert to radians
        //double newX = myPos.getX() + DOT_SIZE * Math.cos(angle);
        //double newY = myPos.getY() + DOT_SIZE * Math.sin(angle);
        myPos.setLocation(myPos.getX() + DOT_SIZE * Math.cos(angle), myPos.getY() + DOT_SIZE * Math.sin(angle));
        x[dots + 1] = Math.round((float)myPos.getX());
        y[dots + 1] = Math.round((float)myPos.getY());
        //System.out.println(direction + " " + + DOT_SIZE * Math.cos(direction*Math.PI/180) + " " + DOT_SIZE * Math.sin(direction*Math.PI/180));
        System.out.printf("Retning: %f\t X: %f\t Y: %f \n", direction, myPos.getX(), myPos.getY());

        dots++;
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }

        if(!inGame) {
            timer.stop();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

//            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {
                leftDirection = true;
                rightDirection = false;
            }

            if (key == KeyEvent.VK_RIGHT) {
                rightDirection = true;
                leftDirection = false;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {
                leftDirection = false;
                rightDirection = false;
            }

            if (key == KeyEvent.VK_RIGHT) {
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
