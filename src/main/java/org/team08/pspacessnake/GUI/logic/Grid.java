package org.team08.pspacessnake.GUI.logic;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * The positional system for the game. This grid will be rendered in the Canvas.
 *
 * @author Subhomoy Haldar
 * @version 2016.12.17
 */
public class Grid {
    /**
     * The side length of each square point in the grid.
     */
    public static final int SIZE = 5;
    public static final Color COLOR = new Color(0.1, 0.1, 0.1, 1);

    private final int cols;     // The number of columns
    private final int rows;     // The number of rows

    private Snake snake;

    public Grid(final double width, final double height) {
        rows = (int) width / SIZE;
        cols = (int) height / SIZE;

        // initialize the snake at the centre of the screen
        snake = new Snake(this, new Point(rows / 2, cols / 2));

        // put the food at a random location
    }

    public Point wrap(Point point) {
return point;
        //        double x = point.getX();
//        double y = point.getY();
//        if (x >= rows) x = 0;
//        if (y >= cols) y = 0;
//        if (x < 0) x = rows - 1;
//        if (y < 0) y = cols - 1;
//        return new Point(x, y);
    }

    private Point getRandomPoint() {
        Random random = new Random();
        Point point;
        do {
            point = new Point(random.nextInt(rows), random.nextInt(cols));
        } while (point.equals(snake.getHead()));
        return point;
    }

    /**
     * This method is called in every cycle of execution.
     */
    public void update() {
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public double getWidth() {
        return rows * SIZE;
    }

    public double getHeight() {
        return cols * SIZE;
    }

    public Snake getSnake() {
        return snake;
    }
}
