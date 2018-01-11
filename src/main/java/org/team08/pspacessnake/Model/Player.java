package org.team08.pspacessnake.Model;

import java.awt.*;

public class Player {
    private Token token;
    private Point position;
    private Double speed;
    private Double angle;
    private Color color;
    private String direction;

    public Player(Token token) {
        this.token = token;
        this.position = new Point(1, 1);
        this.angle = 0d;
        this.direction = "none";
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
