package org.team08.pspacessnake.GUI;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import org.jspace.Space;

import org.team08.pspacessnake.Model.Point;
import org.team08.pspacessnake.Model.Token;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


@SuppressWarnings("restriction")
public class SpaceGui {
    private Space space;
    private final static int SIZE = 5;
    private final static String REMOTE_URI = "tcp://127.0.0.1:9001/";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private List<Circle> points;
    private static GraphicsContext context;

    public SpaceGui(Space space, Token token, Stage primaryStage) throws IOException {
        this.space = space;
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
                if (!leftKeyPressed && !rightKeyPressed) {
                    try {
                        space.put("Changed direction", "none", token);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Sent direction none");
                }
                leftKeyPressed = false;
                rightKeyPressed = false;
            }
        });


        root.getChildren().add(canvas);

        Scene scene = new Scene(root);

        reset();

        primaryStage.setResizable(false);
        primaryStage.setTitle("Snake");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void updateGui(Point point) {
        Circle circle = new Circle(point.getX() * SIZE, point.getY() * SIZE, SIZE / 2);

        context.fillOval(circle.getCenterX() - SIZE / 2, circle.getCenterY() - SIZE / 2, SIZE, SIZE);
//        if (this.collisionDetected(circle)) {
//            System.out.println("COLLISION");
//        }
        points.add(circle);
    }

    private boolean collisionDetected(Shape block) {
        for (Shape shape : points) {
            if (block.getBoundsInParent().intersects(shape.getBoundsInParent())) {
                System.out.println(((Circle) block).getCenterX() + " " + ((Circle) block).getCenterY());
                System.out.println(((Circle) shape).getCenterX() + " " + ((Circle) shape).getCenterY());
                return true;
            }
        }
        return false;
    }

    public void reset() {
        context.setFill(new Color(0.1, 0.1, 0.1, 1));
        context.fillRect(0, 0, WIDTH, HEIGHT);
        context.setFill(Color.CORNSILK);
        for (Circle circle : points) {
            context.fillOval(circle.getCenterX(), circle.getCenterY(), SIZE, SIZE);
        }
    }

    public void startingPositions(List<Point> startPosition) {
        for (int i = 0; i < startPosition.size(); i++) {
            Point point = startPosition.get(i);
            points.add(new Circle(point.getX(), point.getY(), SIZE));
        }
        reset();
    }
}


