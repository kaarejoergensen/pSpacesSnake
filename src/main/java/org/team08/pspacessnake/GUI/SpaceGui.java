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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.team08.pspacessnake.Client.Client;
import org.team08.pspacessnake.Model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@SuppressWarnings("restriction")
public class SpaceGui {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;

    private Client client;
    private Token token;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private List<Point> points;
    private List<Powerups> powers = new ArrayList<>();
    private GraphicsContext context;
    private List<Room> rooms;
    private Room selectedRoom;
    private ObservableList<String> messages;
    private ObservableList<String> roomNames;
    private boolean roomEntered = false;

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
    public void onEnterName(ActionEvent ae) {
        onClickEnterNameButton();
    }

    @FXML
    private void onClickEnterNameButton() {
        String name = enterNameText.getText();
        if (name != null && !name.equals("")) {
            try {
                token = client.enterName(name);
                roomNames = FXCollections.observableArrayList(new ArrayList<>());
                new Thread(() -> {
                    while (!roomEntered) {
                        Platform.runLater(() -> {
                            try {
                                rooms = client.getRooms(token);
                                roomNames.clear();
                                roomNames.addAll(rooms.
                                        stream().map(r -> r.getName() + " " + r.getTokens().size() + "/6").collect(Collectors.toList()));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                roomsListView.setItems(roomNames);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            enterNameLayout.setVisible(false);
            roomsLayout.setVisible(true);
            roomsListView.setOnMouseClicked(event -> {
                String roomName = roomsListView.getSelectionModel().getSelectedItem();
                if (roomName != null) {
                    roomName = roomName.substring(0, roomName.length() - 4);
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
                selectedRoom = client.createRoom(s);
                enterGame(selectedRoom);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void enterGame(Room room) throws InterruptedException, IOException {
        if (client.enterRoom(room.getURL(), token)) {
            roomEntered = true;
            roomsLayout.setVisible(false);
            gameContainerLayout.setVisible(true);
            initGame(room.getURL());
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
        clear();
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
            context.fillText(player.getToken().getName() + "\t\t\t" + (player.isReady() ? "Ready!" : "Not ready"), WIDTH / 2, i);
            i += 50;
        }
    }

    public void clear() {
        context.setFill(new Color(0.1, 0.1, 0.1, 1));
        context.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void holes(Point point) {
        clear();
        context.setFill(point.getColor());
        drawPoint(point);
        if (!points.isEmpty()) {
            points.remove(points.size() - 1);
        }
        for (Point point1 : points) {
            context.setFill(point1.getColor());
            drawPoint(point1);
        }
        for (Powerups power1 : powers) {
            drawImage(power1);
        }
        points.add(point);
    }

    public void updateGui(Player player) {
        Point point = player.getPosition();
        if (player.getRemember()) {
            drawPoint(point);
            points.add(point);
        } else {
        	drawPoint(point);
            //holes(point);
        }
    }
    
    private void drawImage(Powerups power) {

    	switch (power.getPower()) {
    	case "Fast":
    		// Image image = new Image("SpaceLightning.png");
    		Image image = new Image("powerup.png");
    		context.drawImage(image, power.getPosition().getX(), power.getPosition().getY(), 20.0, 20.0);
    		
    	}
    }

    private void drawPoint(Point point) {
    	context.setFill(point.getColor());
        context.fillOval(point.getX() - point.getRadius(), point.getY() - point.getRadius(), 2*point.getRadius(), 2*point.getRadius());
    }

	public void addPowerup(Powerups power) {
		powers.add(power);
		System.out.println(power.getPower());
		drawImage(power);
	}
}


