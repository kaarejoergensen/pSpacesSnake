package org.team08.pspacessnake.Client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;
import org.team08.pspacessnake.Model.Player;
import org.team08.pspacessnake.Model.Room;
import org.team08.pspacessnake.Model.Token;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.team08.pspacessnake.GUI.*;
import org.team08.pspacessnake.GUI.logic.Point;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("restriction")
public class Client extends Application {
    private final static String REMOTE_URI = "tcp://127.0.0.1:9001/";
    private static Token token;
    private static Space space;
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        space = new RemoteSpace(REMOTE_URI + "space?keep");

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
        String UID;
        if (Integer.parseInt(choice) == 1) {
            System.out.print("Enter name: ");
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
        

        System.out.println("hjælp");
        new Thread(new ChatReader(new RemoteSpace(REMOTE_URI + UID + "?keep"), token)).start();
        new Thread(new ChatWriter(new RemoteSpace(REMOTE_URI + UID + "?keep"), scanner, token)).start();
        new Thread(new GameWriter(new RemoteSpace(REMOTE_URI + UID + "?keep"), token)).start();
 
        //GUI stuff

        space.put("Player moved","test", new Point(50,50));
////        Object[] startPoints = space.queryp(new ActualField("Starting position"), new FormalField(List.class));
////        gui.startingPositions((List<Point>) startPoints[1]);

        Application.launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
    	System.out.print("Hey");
		SpaceGui gui = new SpaceGui(token, primaryStage);
		new Thread(new GameReader(space, gui)).start();
    }
    
}
class GameReader implements Runnable {
	private Space space;
	private SpaceGui gui;

	public GameReader(Space space, SpaceGui gui) {
		this.space = space;
		this.gui = gui;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Object[] newPoint = space.get(new ActualField("Player moved"), new FormalField(String.class), 
						new FormalField(Point.class));

				gui.updateGui((Point) newPoint[2]); 

			} catch (InterruptedException e) {}
		}

	}
}
class GameWriter implements Runnable {
	private Space space;
	private Token token;
	private boolean left = false;
	private boolean right = false;
	
	public GameWriter(Space space, Token token) {
		this.space = space;
		this.token = token;
	}
	
	@Override
	public void run() {
		
		//KeyListener();
		while (true) {
			try {
				if (left || right) {
					space.put(token, left, right);
				}				
			} catch (InterruptedException e) {
				
			}
		}
	}
}

class ChatReader implements Runnable {
    private Space space;
    private Token token;

    public ChatReader(Space space, Token token) {
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


class ChatWriter implements Runnable {
    private Space space;
    private Scanner scanner;
    private Token token;

    public ChatWriter(Space space, Scanner scanner, Token token) {
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


