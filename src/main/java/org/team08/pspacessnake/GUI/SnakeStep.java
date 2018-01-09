package org.team08.pspacessnake.GUI;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

@SuppressWarnings("serial")
public class SnakeStep extends Line2D.Double {
	

	public SnakeStep() {
		super();
	}
	
	public SnakeStep(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}
	
	public SnakeStep(Point2D.Double p1, Point2D.Double p2) {
		super(p1, p2);
	}
	
	
	public SnakeStep getClosestSnakeWall(Point2D snakePosition, int snakeThickness) {
		double length = this.getLength();
		int dir = -this.relativeCCW(snakePosition);
		double scale = dir * (double)snakeThickness / 2.0 / this.getLength();
		Point2D P1 = this.getP1();
		Point2D P2 = this.getP2();
		Point2D.Double wallP1 = new Point2D.Double();
		Point2D.Double wallP2 = new Point2D.Double();
		wallP1.setLocation(P1.getX()-this.getDY()*scale, P1.getY()+this.getDX()*scale);
		wallP2.setLocation(P2.getX()-this.getDY()*scale, P2.getY()+this.getDX()*scale);
		
		SnakeStep closestWall = new SnakeStep(wallP1, wallP2);
		return closestWall;
	}
	
	public int getDir(Point2D snakePosition) {
		return -this.relativeCCW(snakePosition);
	}
	
	public double getScale(Point2D snakePosition, int snakeThickness) {
		int dir =  -this.relativeCCW(snakePosition);
		return dir * (double)snakeThickness / 2.0 / this.getLength();
	}
	
	public double getDX() {
		return this.getX2() - this.getX1();
	}
	
	public double getDY()  {
		return this.getY2() - this.getY1();
	}
	
	public double getLength() {
		//return Math.sqrt(this.getDX()*this.getDX() + this.getDY()*this.getDY());
		return this.getP1().distance(this.getP2());
	}
	
	@Override
	public String toString() {
		return "[" + this.getX1() + ", " + this.getY1() + "] -> [" + this.getX2() + ", " + this.getY2() + "]"; 
	}

}
