package org.team08.pspacessnake.Model;
import javafx.scene.paint.Color;

@SuppressWarnings("restriction")
public class Point {
    private final double x;    // The X coordinate
    private final double y;    // The Y coordinate
    private final double radius;
    private Color color;

    public Point(final double x, final double y, Color color) {
        this.x = x;
        this.y = y;
        this.radius = 2.5d;		//default value
        this.setColor(color);
    }
    
    public Point(final double x, final double y, final double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.setColor(color);
    }
    
    public Point(final double x, final double y, final double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.setColor(color);
    }

    public Color getColor() {
        return this.color;
    }
    
	public void setColor(Color color) {
		this.color = color;
	}

	public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
 	public double getRadius() {
		return radius;
	}
    
    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy, getColor());
    }
	
    public Point translate(double dx, double dy, double radius) {
        return new Point(x + dx, y + dy, radius);
    }

    public Point translate(double dx, double dy, Color color) {
        return new Point(x + dx, y + dy, color);
    }
    
    public Point translate(double dx, double dy, double radius, Color color) {
        return new Point(x + dx, y + dy, radius, color);
    }

    public double distance(Point point) {
    	double xDist = Math.abs(this.getX() - point.getX());
    	double yDist = Math.abs(this.getY() - point.getY());
    	return Math.sqrt(xDist*xDist + yDist*yDist);
    }
    
    public Double getAngleToPoint(double targetX, double targetY) {
    	Double angle = Math.acos(targetX - this.getX());
    	if (Math.signum(targetY - this.getY()) < 0.0)
    		angle = 2 * Math.PI - angle;
    	return angle;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        double xx = point.x - x;
        double yy = point.y - y;
        return (Math.abs(xx-1.0) <= 2.5 && Math.abs(yy-1.0) <= 2.5  );
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
    	return "Point{" +                "x=" + x +
                ", y=" + y +
                ", radius=" + radius +
                ", color=" + getColor().toString() + 
                "}";
    }

}
