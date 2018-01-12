package org.team08.pspacessnake.Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import java.util.stream.Collectors;

@SuppressWarnings("restriction")
public class Client extends Application {
    private final static String REMOTE_URI = "tcp://127.0.0.1:9001/";
    private static Space space;

    public static void main(String[] args) throws IOException, InterruptedException {
        space = new RemoteSpace(REMOTE_URI + "space?keep");
//
//        new Thread(new Reader(new RemoteSpace(REMOTE_URI + UID + "?keep"), token)).start();
//        new Thread(new Writer(new RemoteSpace(REMOTE_URI + UID + "?keep"), scanner, token)).start();

        Application.launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SpaceGui.fxml"));
        Parent root = loader.load();
        SpaceGui gui = loader.getController();
        gui.setClient(this);
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Snake");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();
//		new Thread(new GameReader(new RemoteSpace(REMOTE_URI + UID + "?keep"), gui, token)).start();
    }

    public Token enterName(String name) throws InterruptedException {
        space.put("createClient", name);
        Object[] createClientResult = space.get(new ActualField("createClientResult"), new ActualField(name), new
                FormalField(Token.class));
        return (Token) createClientResult[2];
    }

    public List<Room> getRooms(Token token) throws InterruptedException {
        List<Object[]> rooms = space.queryAll(new ActualField("room"), new FormalField(String.class),
                new FormalField(Room.class));
        return rooms.stream().map(objects -> (Room) objects[2]).collect(Collectors.toList());
    }

    public String createRoom(String name, Token token) throws InterruptedException {
        space.put("createRoom", name, token);
        Object[] room = space.get(new ActualField("createRoomResult"), new FormalField(String.class), new
                ActualField(name), new ActualField(token));
        return (String) room[1];
    }

    public boolean enterRoom(String UID, Token token) throws InterruptedException {
        space.put("enter", UID, token);
        Object[] result = space.get(new ActualField("enterResult"), new FormalField(Boolean.class), new
                ActualField(token));
        return (Boolean) result[1];
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
                Object[] newPoint = space.get(new ActualField("Player moved"), new FormalField(Point.class),
                        new ActualField(token), new FormalField(Boolean.class));
                gui.updateGui((Point) newPoint[1], (Boolean) newPoint[3]);
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

