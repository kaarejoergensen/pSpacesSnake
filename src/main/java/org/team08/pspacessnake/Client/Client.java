package org.team08.pspacessnake.Client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;
import org.team08.pspacessnake.Model.Room;
import org.team08.pspacessnake.Model.Token;
import org.team08.pspacessnake.Model.GameSettings;

import javafx.application.Application;
import javafx.stage.Stage;

import org.team08.pspacessnake.GUI.*;
import org.team08.pspacessnake.Model.Point;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("restriction")
public class Client extends Application {
    private final static String REMOTE_URI = "tcp://127.0.0.1:9001/";
    private static Token token;
    private static String UID;
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        Space space = new RemoteSpace(REMOTE_URI + "space?keep");

        space.put("createClient", name);
        Object[] createClientResult = space.get(new ActualField("createClientResult"), new ActualField(name), new
                FormalField(Token.class));
        token = (Token) createClientResult[2];

        System.out.println("1: Create new room");
        List<Object[]> rooms = space.queryAll(new ActualField("room"), new FormalField(String.class), new FormalField
                (Room.class));
        if (rooms.size() > 0) {
            System.out.println("Enter existing room: ");
        }
        for (int i = 0; i < rooms.size(); i++) {
            Room room = (Room) rooms.get(i)[2];
            System.out.println((i + 2) + ": " + room.getName());
        }

        String choice = scanner.nextLine();
        if (Integer.parseInt(choice) == 1) {
            System.out.print("New room name: ");
            String roomName = scanner.nextLine();
            space.put("createRoom", roomName, token);
            Object[] room = space.get(new ActualField("createRoomResult"), new FormalField(String.class), new
                    ActualField(roomName), new ActualField(token));
            UID = (String) room[1];
        } else {
            UID = ((Room) rooms.get(Integer.parseInt(choice) - 2)[2]).getID();
        }

        space.put("enter", UID, token);
        Object[] result = space.get(new ActualField("enterResult"), new FormalField(Boolean.class), new
                ActualField(token));
        if (!(Boolean) result[1]) {
            System.out.println("Error!");
        }
        System.out.print("Entered " + UID + ". Current users: ");
        Room room = (Room) space.queryp(new ActualField("room"), new ActualField(UID), new FormalField(Room.class))[2];
        room.getTokens().forEach(t -> System.out.print(t.getName() + " "));
        System.out.println();

        new Thread(new Reader(new RemoteSpace(REMOTE_URI + UID + "?keep"), token)).start();
        new Thread(new Writer(new RemoteSpace(REMOTE_URI + UID + "?keep"), scanner, token)).start();

        Application.launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
		SpaceGui gui = new SpaceGui(new RemoteSpace(REMOTE_URI + UID + "?keep"), token, primaryStage, new GameSettings());
		new Thread(new GameReader(new RemoteSpace(REMOTE_URI + UID + "?keep"), gui, token)).start();
    }
}

class GameReader implements Runnable {
	private Space space;
	private SpaceGui gui;
	private Token token;

    public GameReader(Space space, SpaceGui gui, Token token) {
        this.space = space;
        this.gui = gui;
        this.token = token;
    }

    @Override
	public void run() {
		while (true) {
			try {
				List<Object[]> newPoint = space.getAll(new ActualField("Player moved"), new FormalField(Point.class),
                        new ActualField(token));
				for (Object[] point : newPoint) {
                    gui.updateGui((Point) point[1]);
                }
			} catch (InterruptedException e) {
			    e.printStackTrace();
            }
		}
	}
}

class Reader implements Runnable {
    private Space space;
    private Token token;

    public Reader(Space space, Token token) {
        this.space = space;
        this.token = token;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] message = space.get(new ActualField("message" + token.getID()), new FormalField(String
                        .class), new FormalField(String.class));
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
    private Token token;

    public Writer(Space space, Scanner scanner, Token token) {
        this.space = space;
        this.scanner = scanner;
        this.token = token;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String text = scanner.nextLine();
                space.put("message", text, token);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

