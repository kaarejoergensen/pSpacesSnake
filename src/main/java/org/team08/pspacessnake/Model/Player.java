package org.team08.pspacessnake.Model;

import java.awt.*;

public class Player {
    private Token token;
    private Point position;
    private Double speed;
    private Double angle;		//current angler of movement
    private double dAngle;		//the change of angle per frame.
    private Color color;
    private String direction;	//change of angle

    public Player(Token token) {
        this.token = token;
        this.position = new Point(1, 1);
        this.speed = .5;
        this.angle = 0d;
        this.dAngle = .1;
        this.direction = "none";
    }
    
    public Point move() {
    	this.position = this.position.translate(speed * Math.cos(angle), speed * Math.sin(angle));
    	return this.position;
    }
    
    public double turn() {
    	if (!this.direction.equals("none")) {
            if (this.direction.equals("left")) {
                this.angle += this.dAngle;
            } else if (direction.equals("right")) {
                this.angle -= this.dAngle;
            }
        }
    	return this.angle;
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
    
    public double getDAngle() {
        return dAngle;
    }

    public void setDAngle(double dAngle) {
        this.dAngle = dAngle;
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