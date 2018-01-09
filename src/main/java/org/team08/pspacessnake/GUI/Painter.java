package org.team08.pspacessnake.GUI;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.team08.pspacessnake.GUI.logic.Grid;
import org.team08.pspacessnake.GUI.logic.Point;
import org.team08.pspacessnake.GUI.logic.Snake;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Painter {
    public static void paint(Grid grid, GraphicsContext gc) {
        gc.setFill(Grid.COLOR);
        gc.fillRect(0, 0, grid.getWidth(), grid.getHeight());

        Snake snake = grid.getSnake();
        gc.setFill(Snake.COLOR);
        snake.getPoints().forEach(p -> paintPoint(p, gc));
        if (!snake.isSafe()) {
            gc.setFill(Snake.DEAD);
            paintPoint(snake.getHead(), gc);
        }

        gc.setFill(Color.BEIGE);
    }

    private static void paintPoint(Point point, GraphicsContext gc) {
        gc.fillOval(point.getX(), point.getY(), Grid.SIZE, Grid.SIZE);
    }

    public static void paintResetMessage(GraphicsContext gc) {
        gc.setFill(Color.AQUAMARINE);
        gc.fillText("Hit RETURN to reset.", 10, 10);
    }
}
