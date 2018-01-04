package org.team08.pspacessnake.pspacessnake;

import org.jspace.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChatServer {
    final static String URI = "tcp://ec2-18-218-0-120.us-east-2.compute.amazonaws.com:9001/";
    private final static String GATE_URI = URI + "?kepp";

    public static void main(String[] args) throws InterruptedException {
        SpaceRepository repository = new SpaceRepository();
        repository.addGate(GATE_URI);
        repository.add("aspace", new SequentialSpace());
        Space space = repository.get("aspace");
        space.put("rooms", new ArrayList<>());
        System.out.println("Server started!");

        new Thread(new Create(repository, space)).start();
    }
}

class Create implements Runnable {
    private final static String ALPHABET = "ABCDEFZHIKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private SpaceRepository repository;
    private Space space;

    Create(SpaceRepository repository, Space space) {
        this.repository = repository;
        this.space = space;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] create = space.get(new ActualField("create"), new FormalField(String.class));
                StringBuilder UID = new StringBuilder();
                do {
                    Random r = new Random();
                    for (int i = 0; i < 5; i++) {
                        UID.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
                    }
                    UID.append('-');
                    for (int i = 0; i < 5; i++) {
                        UID.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
                    }
                } while (repository.get("UID") != null);
                repository.add(UID.toString(), new SequentialSpace());
                space.put("createResult", UID.toString(), create[1]);

                Object[] rooms = space.get(new ActualField("rooms"), new FormalField(Object.class));
                List<String>  rooms1 = (ArrayList<String>) rooms[1];
                rooms1.add(UID.toString());
                space.put("rooms", rooms1);

                new Thread(new EnterRoom(new RemoteSpace(ChatServer.URI + UID.toString() + "?keep"), UID.toString())).start();
                new Thread(new Room(new RemoteSpace(ChatServer.URI + UID.toString() + "?keep"))).start();
                System.out.println("New room with UID " + UID + " created!");
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class EnterRoom implements Runnable {
    private Space space;
    private String UID;

    public EnterRoom(Space space, String UID) throws InterruptedException {
        this.space = space;
        this.UID = UID;
        space.put("users", new ArrayList<String>());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] enter = space.get(new ActualField("enter"), new ActualField(UID), new FormalField(String.class));
                Object[] users = space.get(new ActualField("users"), new FormalField(Object.class));
                List<String> usersList = (ArrayList<String>) users[1];
                usersList.add((String) enter[2]);
                space.put("users", usersList);
                space.put("enterResult", enter[2]);
                space.put("message", "User '" + enter[2] + "' entered room!", "System");
                System.out.println("Added user " + enter[2] + " to room " + UID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Room implements Runnable {
    private Space space;

    public Room(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] message = space.get(new ActualField("message"), new FormalField(String.class), new FormalField(String.class));
                Object[] users = space.query(new ActualField("users"), new FormalField(Object.class));
                List<String> usersList = (List<String>) users[1];
                System.out.println("Got message " + message[1] + " from " + message[2] + ". Sending to " + (usersList.size() - 1) + " users.");
                usersList.stream().filter(u -> !u.equals(message[2])).forEach(u -> {
                    try {
                        space.put("message" + u, message[1], message[2]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}