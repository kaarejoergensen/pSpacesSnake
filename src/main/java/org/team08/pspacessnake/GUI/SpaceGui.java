package org.team08.pspacessnake.GUI;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.team08.pspacessnake.Client.Client;
import org.team08.pspacessnake.Model.Point;
import org.team08.pspacessnake.Model.Room;
import org.team08.pspacessnake.Model.Token;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


@SuppressWarnings("restriction")
public class SpaceGui {
    private final static int SIZE = 5;
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private static final int CELL_SIZE = 5;

    private Client client;
    private Token token;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private List<Circle> points;
    private static GraphicsContext context;
    private List<Room> rooms;
    private Room selectedRoom;

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
    private void onClickEnterGameButton() {
        try {
            enterGame(selectedRoom);
        } catch (InterruptedException e) {
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    private void enterGame(Room room) throws InterruptedException {
        if (client.enterRoom(room.getID(), token)) {
            System.out.println("JOINED");
        }
    }

    public SpaceGui() {
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void holes(Circle circle) {
		context.setFill(new Color(0.1, 0.1, 0.1, 1));
		context.fillRect(0, 0, WIDTH, HEIGHT);
		context.setFill(Color.CORNSILK);
		context.fillOval(circle.getCenterX() - SIZE / 2, circle.getCenterY() - SIZE / 2, SIZE, SIZE);
		
		for (Circle circle1 : points) {
			context.fillOval(circle1.getCenterX() - SIZE / 2, circle1.getCenterY() - SIZE / 2, SIZE, SIZE);
		}
	}

    public void updateGui(Point point, Boolean remember) {
        Circle circle = new Circle(point.getX() * SIZE * 2, point.getY() * SIZE * 2, SIZE / 2);
		if(remember) {
        	context.fillOval(circle.getCenterX() - SIZE / 2, circle.getCenterY() - SIZE / 2, SIZE, SIZE);
        	points.add(circle);
    	}
    	else {
    		holes(circle);
    	}
    }
}


