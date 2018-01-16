package org.team08.pspacessnake.Model;

import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.ListIterator;

public class Player {
    private Token token;
    private Point position;
    private LinkedList<Point> pointBuffer = new LinkedList<Point>();
    private Double speed;
    private double size;		// diameter of snake head. 
    private Double angle;		//current angler of movement
    private double dAngle;		//the change of angle per frame.
    private Color color;
    private String direction;    //change of angle
    private boolean isDead = false;
    private boolean remember = true;
    private boolean ready;
    private Powerups power;
    private boolean edgeJumper;


    public Player(Token token) {
        this.setToken(token);
        this.setSpeed(3d);
        this.setSize(5d);
        this.setAngle(0d);
        this.setDAngle(.17);
        this.setDirection("none");
        this.setEdgeJumper(false);
    }

    public Point move(int boardWidth, int boardHeight) {
    	if (this.isEdgeJumper())
    		this.position = this.position.translate(speed * Math.cos(angle), -speed * Math.sin(angle), this.getPosition().getRadius(), this.getColor(), boardWidth, boardHeight);
    	else
    		this.position = this.position.translate(speed * Math.cos(angle), -speed * Math.sin(angle), this.getPosition().getRadius(), this.getColor()); 
    	System.out.println(this.position);
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

    public LinkedList<Point> getPointBuffer() {
		return pointBuffer;
	}

	public Double getSpeed() {
        return speed;
    }

    /**
	 * @return the size
	 */
	public double getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(double size) {
		this.size = size;
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

    public boolean isDead() {
        return isDead;
    }

    public void kill() {
        this.isDead = true;
        System.out.printf("Spiller: %s er død\n", this.getToken().getName());
    }

    public void setRemember(boolean holes) {
        this.remember = holes;
    }

    public boolean getRemember() {
        return remember;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }


	public Powerups getPower() {
		return power;
	}

	public void setPower(Powerups power) {
		this.power = power;
	}

	public boolean isEdgeJumper() {
		return edgeJumper;
	}

	public void setEdgeJumper(boolean edgeJumper) {
		this.edgeJumper = edgeJumper;
	}
}
