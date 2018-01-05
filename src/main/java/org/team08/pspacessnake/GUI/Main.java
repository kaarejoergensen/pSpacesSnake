package org.team08.pspacessnake.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.team08.pspacessnake.GUI.logic.GameLoop;
import org.team08.pspacessnake.GUI.logic.Grid;
import org.team08.pspacessnake.GUI.logic.Snake;

import java.util.ArrayList;

/**
 * This is the place where the threads are dispatched.
 *
 * @author Subhomoy Haldar
 * @version 2016.12.17
 */
public class Main extends Application {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;

    private GameLoop loop;
    private Grid grid;
    private GraphicsContext context;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        context = canvas.getGraphicsContext2D();

        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> {
            Snake snake = grid.getSnake();
            if (loop.isKeyPressed()) {
                return;
            }
            switch (e.getCode()) {
                case LEFT:
                    snake.setKeyLeft(true);
                    break;
                case RIGHT:
                    snake.setKeyRight(true);
                    break;
                case ENTER:
                    if (loop.isPaused()) {
                        reset();
                        (new Thread(loop)).start();
                    }
            }
        });
        canvas.setOnKeyReleased(e -> {
            Snake snake = grid.getSnake();
            if (loop.isKeyPressed()) {
                return;
            }
            switch (e.getCode()) {
                case LEFT:
                    snake.setKeyLeft(false);
                    break;
                case RIGHT:
                    snake.setKeyRight(false);
                    break;
                case ENTER:
                    if (loop.isPaused()) {
                        reset();
                        (new Thread(loop)).start();
                    }
            }
        });

        reset();

        root.getChildren().add(canvas);

        Scene scene = new Scene(root);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Snake");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();

        (new Thread(loop)).start();
    }

    private void reset() {
        grid = new Grid(WIDTH, HEIGHT);
        loop = new GameLoop(grid, context);
        Painter.paint(grid, context);
    }
}
