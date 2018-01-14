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
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("Snake");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	public void holes(Point point) {
		context.setFill(new Color(0.1, 0.1, 0.1, 1));
		context.fillRect(0, 0, WIDTH, HEIGHT);
		context.setFill(Color.CORNSILK);
		context.fillOval(point.getX() - SIZE / 2, point.getY() - SIZE / 2, SIZE, SIZE);
		
		for (Point thisPoint : points) {
			context.fillOval(thisPoint.getX() - SIZE / 2, thisPoint.getY() - SIZE / 2, SIZE, SIZE);
		}
	}

    public void updateGui(Point point, Boolean remember) {
		if(remember) {
        	drawPoint(point);
        	detectCollision(point);
        	points.add(point);
        	System.out.println(point);
    	}
    	else {
    		holes(point);
    	}
    }
    
    private void drawPoint(Point point) {
    	//System.out.printf("SIZE = %d\t RADIUS = %f\t DrawPointX = %f / %f \n", SIZE, point.getRadius(), point.getX() - point.getRadius(), point.getX() - SIZE / 2.0);
    	context.fillOval(point.getX() - point.getRadius(), point.getY() - point.getRadius(), 2*point.getRadius(), 2*point.getRadius());
    	//context.fillOval(point.getX() - SIZE / 2, point.getY() - SIZE / 2, SIZE, SIZE);
    }

    private boolean detectCollision(Point newPoint) {
        for (Point oldPoint : points) {
            if (newPoint.distance(oldPoint) < newPoint.getRadius() + oldPoint.getRadius()) {
            	System.out.printf("COLLISION: (%f, %f, R: %f) <-> (%f, %f, R:%f)\n", newPoint.getX(), newPoint.getY(), newPoint.getRadius(), oldPoint.getX(), newPoint.getY(), newPoint.getRadius());
                //System.out.println((newPoint.getX() + " " + (newPoint.getY());
                //System.out.println((oldPoint.getX() + " " + (oldPoint.getY());
                return true;
            }
        }
        return false;
    }
}


