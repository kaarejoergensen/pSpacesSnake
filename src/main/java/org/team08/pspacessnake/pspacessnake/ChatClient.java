package org.team08.pspacessnake.pspacessnake;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ChatClient   {
    private final static String REMOTE_URI = "tcp://127.0.0.1:9001/";

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        Space space = new RemoteSpace(REMOTE_URI + "aspace?keep");

        System.out.println("1: Create new room");
        Object[] rooms = space.query(new ActualField("rooms"), new FormalField(Object.class));
        List<String> rooms1 = (ArrayList<String>) rooms[1];
        if (rooms1.size() > 0) {
            System.out.println("Enter existing room: ");
        }
        for (int i = 0; i < rooms1.size(); i++) {
            System.out.println((i + 2) + ": " + rooms1.get(i));
        }

        String choice = scanner.nextLine();
        String UID;
        if (Integer.parseInt(choice) == 1) {
            space.put("create", name);
            Object[] room = space.get(new ActualField("createResult"), new FormalField(String.class), new ActualField(name));
            UID = (String) room[1];
        } else {
            UID = rooms1.get(Integer.parseInt(choice) - 2);
        }

        Space roomSpace = new RemoteSpace(REMOTE_URI + UID + "?keep");
        roomSpace.put("enter", UID, name);
        roomSpace.get(new ActualField("enterResult"), new ActualField(name));

        Object[] users = roomSpace.query(new ActualField("users"), new FormalField(Object.class));
        List<String> usersList = (ArrayList<String>) users[1];
        System.out.print("Entered " + UID + ". Current users: ");
        usersList.forEach(u -> System.out.print(u + " "));
        System.out.println();

        new Thread(new Reader(new RemoteSpace(REMOTE_URI + UID + "?keep"), name)).start();
        new Thread(new Writer(new RemoteSpace(REMOTE_URI + UID + "?keep"), scanner, name)).start();
    }
}

class Reader implements Runnable {
    private Space space;
    private String name;

    public Reader(Space space, String name) {
        this.space = space;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] message = space.get(new ActualField("message" + name), new FormalField(String.class), new FormalField(String.class));
                System.out.println(message[2] + ": " + message[1]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


class Writer implements Runnable {
    private Space space;
    private Scanner scanner;
    private String name;

    public Writer(Space space, Scanner scanner, String name) {
        this.space = space;
        this.scanner = scanner;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String text = scanner.nextLine();
                space.put("message", text, name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}