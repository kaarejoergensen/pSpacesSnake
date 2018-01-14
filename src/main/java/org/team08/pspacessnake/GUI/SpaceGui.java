package org.team08.pspacessnake.GUI;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.team08.pspacessnake.Client.Client;
import org.team08.pspacessnake.Model.Player;
import org.team08.pspacessnake.Model.Point;
import org.team08.pspacessnake.Model.Room;
import org.team08.pspacessnake.Model.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@SuppressWarnings("restriction")
public class SpaceGui {
    private final static int SIZE = 5;
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;
    private static final int CELL_SIZE = 5;

    private Client client;
    private Token token;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private List<Point> points; 
    private static GraphicsContext context;
    private List<Room> rooms;
    private Room selectedRoom;
    private ObservableList<String> messages;

    @FXML
    private VBox enterNameLayout;
    @FXML
    private TextField enterNameText;

    @FXML
    private StackPane roomsLayout;

    @FXML
    private ListView<String> roomsListView;

    @FXML
    private Button joinGameButton;

    @FXML
    private GridPane gameContainerLayout;

    @FXML
    private Canvas gameLayout;

    @FXML
    private ListView<String> chatListView;

    @FXML
    private TextField chatTextField;

    @FXML
    private Button readyButton;

    public SpaceGui() {
    }

    @FXML
    public void onEnterName(ActionEvent ae){
        onClickEnterNameButton();
    }

    @FXML
    private void onClickEnterNameButton() {
        String name = enterNameText.getText();
        if (name != null && !name.equals("")) {
            try {
                token = client.enterName(name);
                rooms = client.getRooms(token);
                ObservableList<String> roomNames = FXCollections.observableArrayList(rooms.stream().
                        map(Room::getName).collect(Collectors.toList()));
                roomsListView.setItems(roomNames);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            enterNameLayout.setVisible(false);
            roomsLayout.setVisible(true);
            roomsListView.setOnMouseClicked(event -> {
                String roomName = roomsListView.getSelectionModel().getSelectedItem();
                if (roomName != null ) {
                    joinGameButton.setDisable(false);
                    for (Room room : rooms) {
                        if (room.getName().equals(roomName)) {
                            selectedRoom = room;
                        }
                    }
                }
            });
        }
    }

    @FXML
    public void onEnterChat(ActionEvent ae) {
        onClickChatMessage();
    }

    @FXML
    private void onClickChatMessage() {
        String message = chatTextField.getText();
        if (message != null && !message.equals("")) {
            try {
                client.sendMessage(message, token);
                chatTextField.setText("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onClickReady() {
        try {
            client.setReady(token);
            readyButton.setVisible(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickEnterGameButton() {
        try {
            enterGame(selectedRoom);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickCreateGameButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New game");
        dialog.setContentText("Please enter game name:");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(newValue == null || newValue.equals(""));
        }));
        dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);


        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            try {
                String UID = client.createRoom(s, token);
                enterGame(new Room(UID, s));
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void enterGame(Room room) throws InterruptedException, IOException {
        if (client.enterRoom(room.getID(), token)) {
            roomsLayout.setVisible(false);
            gameContainerLayout.setVisible(true);
            initGame(room.getID());
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void initGame(String UID) throws IOException {
        points = new LinkedList<>();
        context = gameLayout.getGraphicsContext2D();
        gameLayout.setFocusTraversable(true);
        gameLayout.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> gameLayout.requestFocus());
        gameContainerLayout.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    if (leftKeyPressed) {
                        return;
                    }
                    try {
                        client.turn("left", token);
                        leftKeyPressed = true;
                    } catch (InterruptedException ignored) {
                    }
                    break;
                case RIGHT:
                    if (rightKeyPressed) {
                        return;
                    }
                    try {
                        client.turn("right", token);
                        rightKeyPressed = true;
                    } catch (InterruptedException ignored) {
                    }
                    break;
                default:
                    break;
            }
        });
        gameContainerLayout.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case LEFT:
                    leftKeyPressed = false;
                    break;
                case RIGHT:
                    rightKeyPressed = false;
                    break;
                default:
                    break;
            }
            if (e.getCode().equals(KeyCode.LEFT) || e.getCode().equals(KeyCode.RIGHT)) {
                try {
                    if (!leftKeyPressed && !rightKeyPressed) {
                        client.turn("none", token);
                    } else if (rightKeyPressed) {
                        client.turn("right", token);
                    } else {
                        client.turn("left", token);
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });

        context.setFill(new Color(0.1, 0.1, 0.1, 1));
        context.fillRect(0, 0, WIDTH, HEIGHT);
        context.setFill(Color.CORNSILK);
        context.setFont(new Font("Verdana", 18));
        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.CENTER);

        messages = FXCollections.observableArrayList(new ArrayList<>());

        chatListView.setItems(messages);

        client.startGame(this, token, UID);
    }

    public void addMessage(String message) {
        Platform.runLater(() -> messages.add(message));
    }

    public void drawPlayers(List<Player> players) {
        clear();
        int i = 100;
        for (Player player : players) {
            context.setFill(player.getColor());
            context.fillText(player.getToken().getName() + "\t\t\t" + (player.isReady() ? "Ready!" : "Not ready"), WIDTH/2, i);
            i += 50;
        }
    }

    public void clear() {
        context.setFill(new Color(0.1, 0.1, 0.1, 1));
        context.fillRect(0, 0, WIDTH, HEIGHT);
    }

	private void holes(Point point) {
		clear();
		drawPoint(point);
		for (Point thisPoint : points) {
			drawPoint(thisPoint);		}
	}


    public void updateGui(Player player) {
		// Vi skal da stadig ikke skalere positionen med SIZE, vel? /Simon
    	 Point point = new Point(player.getPosition().getX() * SIZE, player.getPosition().getY() * SIZE, player.getColor()); 
		//Point point = new Point(player.getPosition().getX(), player.getPosition().getY(), player.getColor());
        // context.setFill(point.getColor()); // is set for each point in drawPoint()
		if(player.getRemember()) {
        	drawPoint(point);
        	points.add(point);
    	}
    	else {
    		holes(point);
    	}
    }
    
    private void drawPoint(Point point) {
    	context.setFill(point.getColor());
    	//System.out.printf("SIZE = %d\t RADIUS = %f\t DrawPointX = %f / %f \n", SIZE, point.getRadius(), point.getX() - point.getRadius(), point.getX() - SIZE / 2.0);
    	context.fillOval(point.getX() - point.getRadius(), point.getY() - point.getRadius(), 2*point.getRadius(), 2*point.getRadius());
    }
}


