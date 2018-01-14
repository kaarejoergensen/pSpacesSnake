package org.team08.pspacessnake.GUI;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import org.jspace.Space;
import org.team08.pspacessnake.Model.Player;
import org.team08.pspacessnake.Model.Point;
import org.team08.pspacessnake.Model.Token;
import org.team08.pspacessnake.Model.GameSettings;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


@SuppressWarnings("restriction")
public class SpaceGui {
    private final static int SIZE = 5;
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private static final int CELL_SIZE = 5;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private List<Point> points;
    private static GraphicsContext context;

    public SpaceGui(Space space, Token token, Stage primaryStage, GameSettings settings) {
        points = new LinkedList<>();
        StackPane root = new StackPane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        context = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    if (leftKeyPressed) {
                        return;
                    }
                    try {
                        space.put("Changed direction", "left", token);
                        leftKeyPressed = true;
                        System.out.println("Sent left");
                    } catch (InterruptedException ignored) {
                    }
                    break;
                case RIGHT:
                    if (rightKeyPressed) {
                        return;
                    }
                    try {
                        space.put("Changed direction", "right", token);
                        rightKeyPressed = true;
                        System.out.println("Sent right");
                    } catch (InterruptedException ignored) {
                    }
                    break;
                default:
                    break;
            }
        });
        canvas.setOnKeyReleased(e -> {
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
                        space.put("Changed direction", "none", token);
                        System.out.println("Sent direction none");
                    } else if (rightKeyPressed) {
                        space.put("Changed direction", "right", token);
                    } else {
                        space.put("Changed direction", "left", token);
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });


        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        context.setFill(new Color(0.1, 0.1, 0.1, 1));
        context.fillRect(0, 0, WIDTH, HEIGHT);
        context.setFill(Color.CORNSILK);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Snake");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	public void holes(Point point) {
		context.setFill(new Color(0.1, 0.1, 0.1, 1));
		context.fillRect(0, 0, WIDTH, HEIGHT);
        context.setFill(point.getColor());
        context.fillOval(point.getX(), point.getY(), SIZE, SIZE);
		
		for (Point point1 : points) {
            context.setFill(point1.getColor());

            context.fillOval(point1.getX(), point1.getY(), SIZE, SIZE);
		}
	}

    public void updateGui(Player player) {
    	Point point = new Point(player.getPosition().getX() * SIZE * 2, player.getPosition().getY() * SIZE * 2, player.getColor());
        context.setFill(point.getColor());
		if(player.getRemember()) {
        	context.fillOval(point.getX(), point.getY(), SIZE, SIZE);
        	points.add(point);
    	}
    	else {
    		holes(point);
    	}
    }


}


