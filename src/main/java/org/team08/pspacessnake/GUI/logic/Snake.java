package org.team08.pspacessnake.GUI.logic;

import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

public class Snake {
    public static final Color COLOR = Color.CORNSILK;
    public static final Color DEAD = Color.RED;
    public static final int SIZE = 2;
    private Grid grid;
    private int length;
    private boolean safe;
    private List<Point> points;
    private Point head;
    private int xVelocity;
    private int yVelocity;
    private double direction;
    private boolean keyLeft;
    private boolean keyRight;

    /**
     * The constructor the snake. It takes the initial point, for the head and the Grid
     * that it lives (and dies) in.
     *
     * @param initialPoint The {@link Point} to the put the snake's head on.
     */
    public Snake(Grid grid, Point initialPoint) {
        length = 1;
        points = new LinkedList<>();
        points.add(initialPoint);
        head = initialPoint;
        safe = true;
        this.grid = grid;
        xVelocity = 0;
        yVelocity = 0;
        direction = 0;
        keyLeft = false;
        keyRight = false;
    }

    /**
     * This method is called after food has been consumed. It increases the length of the
     * snake by one.
     *
     * @param point The Point where the food was and the new location for the head.
     */
    private void growTo(Point point) {
        length++;
        checkAndAdd(point);
    }

    /**
     * Called during every update. It gets rid of the oldest point and adds the given point.
     *
     * @param point The new Point to add.
     */
    private void shiftTo(Point point) {
        // The head goes to the new location
        checkAndAdd(point);
        // The last/oldest position is dropped
        points.remove(0);
    }

    /**
     * Checks for an intersection and marks the "safe" flag accordingly.
     *
     * @param point The new Point to move to.
     */
    private void checkAndAdd(Point point) {
        point = grid.wrap(point);
//        safe &= !points.contains(point);
        points.add(point);
        head = point;
    }

    /**
     * @return The points occupied by the snake.
     */
    public List<Point> getPoints() {
        return points;
    }

    /**
     * @return {@code true} if the Snake hasn't run into itself yet.
     */
    public boolean isSafe() {
        return safe || length == 1;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    /**
     * @return The location of the head of the Snake.
     */
    public Point getHead() {
        return head;
    }

    private boolean isStill() {
        return false;
    }

    /**
     * Make the snake move one square in it's current direction.
     */
    public void move() {
        if (!isStill()) {
            shiftTo(head.translate(xVelocity, yVelocity));
        }
    }

    /**
     * Make the snake extend/grow to the square where it's headed.
     */
    public void extend() {
        if (!isStill()) {
            growTo(head.translate(0.5* Math.cos(this.direction), -0.5* Math.sin(this.direction)));
        }
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public boolean isKeyLeft() {
        return keyLeft;
    }

    public void setKeyLeft(boolean keyLeft) {
        this.keyLeft = keyLeft;
    }

    public boolean isKeyRight() {
        return keyRight;
    }

    public void setKeyRight(boolean keyRight) {
        this.keyRight = keyRight;
    }
}
