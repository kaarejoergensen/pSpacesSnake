package org.team08.pspacessnake.pspacessnake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Snake_Client extends JPanel {

    private final static String REMOTE_URI = "tcp://127.0.0.1:9001/";
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        Space space = new RemoteSpace(REMOTE_URI + "aspace?keep");

        new Thread(new Reader1(new RemoteSpace(REMOTE_URI + name + "?keep"), name)).start();
        new Thread(new Writer1(new RemoteSpace(REMOTE_URI + name + "?keep"), scanner, name)).start();
    }
}

class Reader1 implements Runnable {
    private Space space;
    private String name;

    public Reader1(Space space, String name) {
        this.space = space;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            /*try {
                Object[] info = space.get(new ActualField("info" + name), new FormalField(Integer.class), new FormalField(Integer.class));
                if ((int) info[1] == 0) {
                	//YouDied();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }
}


class Writer1 extends JPanel implements KeyListener, Runnable{
    private Space space;
    private Scanner scanner;
    private String name;
	private boolean right = false;
	private boolean left = false;
	
	private final JPanel gui = new JPanel(new BorderLayout(3,3));
	
	
    public Writer1(Space space, Scanner scanner, String name) {
    	super();
        this.space = space;
        this.scanner = scanner;
        this.name = name;
        addKeyListener(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
            	if (right) {
            		System.out.println("You turned right");
            		space.put(name, left, right);
            	}
            	if (left) {
            		System.out.print("You turned left");
            		space.put(name,left,right);
            	}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            left = true;
            right = false;
        }

        if (key == KeyEvent.VK_RIGHT) {
            right = true;
            left = false;
        }
    }
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            left = false;
            right = false;
        }

        if (key == KeyEvent.VK_RIGHT) {
            right = false;
            left = false;
        }
    }
}

