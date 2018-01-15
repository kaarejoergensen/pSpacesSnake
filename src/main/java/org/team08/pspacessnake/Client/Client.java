package org.team08.pspacessnake.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;
import org.team08.pspacessnake.GUI.SpaceGui;
import org.team08.pspacessnake.Model.Player;
import org.team08.pspacessnake.Model.Room;
import org.team08.pspacessnake.Model.Token;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("restriction")
public class Client extends Application {
    private final static String REMOTE_URI = "tcp://127.0.0.1:9001/";
    private static Space space;
    private static Space roomSpace;

    public static void main(String[] args) throws IOException {
        space = new RemoteSpace(REMOTE_URI + "space?keep");
        Application.launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SpaceGui.fxml"));
        Parent root = loader.load();
        SpaceGui gui = loader.getController();
        gui.setClient(this);
        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Snake");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();
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

    public boolean enterRoom(String UID, Token token) throws InterruptedException, IOException {
        space.put("enter", UID, token);
        Object[] result = space.get(new ActualField("enterResult"), new FormalField(Boolean.class), new
                ActualField(token));
        boolean returnResult = (Boolean) result[1];
        if (returnResult) {
            roomSpace = new RemoteSpace(REMOTE_URI + UID + "?keep");
        }
        return returnResult;
    }

    public void turn(String direction, Token token) throws InterruptedException {
        roomSpace.put("Changed direction", direction, token);
    }

    public void startGame(SpaceGui gui, Token token, String RoomUID) throws IOException {
        new Thread(new GameReader(new RemoteSpace(REMOTE_URI + RoomUID+ "?keep"), gui, token)).start();
        new Thread(new ChatReader(new RemoteSpace(REMOTE_URI + RoomUID+ "?keep"), gui, token)).start();
        new Thread(new PlayersReader(new RemoteSpace(REMOTE_URI + RoomUID+ "?keep"), gui)).start();
    }

    public void sendMessage(String text, Token token) throws InterruptedException {
        roomSpace.put("message", text, token);
    }

    public void setReady(Token token) throws InterruptedException {
        Object[] playerGet = roomSpace.getp(new ActualField("player"), new ActualField(token), new FormalField(Player.class));
        if (playerGet != null) {
            Player player = (Player) playerGet[2];
            player.setReady(true);
            roomSpace.put("player", token, player);
        }
    }
}

class PlayersReader implements Runnable {
    private Space space;
    private SpaceGui spaceGui;

    public PlayersReader(Space space, SpaceGui spaceGui) {
        this.space = space;
        this.spaceGui = spaceGui;
    }

    @Override
    public void run() {
        boolean gameStarted = false;
        while (!gameStarted) {
            try {
                List<Object[]> playersQuery = space.queryAll(new ActualField("player"), new FormalField(Token.class),
                        new FormalField(Player.class));
                List<Player> players = playersQuery.stream().map(o -> (Player) o[2]).
                        sorted(Comparator.comparing(player -> player.getToken().getName())).collect(Collectors.toList());
                gameStarted = players.stream().allMatch(Player::isReady);
                if (!gameStarted) {
                    spaceGui.drawPlayers(players);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
		boolean firstRun = true;
        while (true) {
			try {
				Object[] newPoint = space.get(new ActualField("Player moved"), new FormalField(Player.class),
                        new ActualField(token));
				if (firstRun) {
				    gui.clear();
				    firstRun = false;
                }
                gui.updateGui((Player) newPoint[1]);
			} catch (InterruptedException e) {
			    e.printStackTrace();
            }
        }
    }
}

class ChatReader implements Runnable {
    private Space space;
    private SpaceGui gui;
    private Token token;

    public ChatReader(Space space, SpaceGui gui, Token token) {
        this.space = space;
        this.gui = gui;
        this.token = token;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] message = space.get(new ActualField("message" + token.getID()), new FormalField(String
                        .class), new FormalField(String.class));
                gui.addMessage(message[2] + ": " + message[1]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

