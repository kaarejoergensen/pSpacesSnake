package org.team08.pspacessnake.Model;

import javafx.scene.paint.Color;

public class Point {
    private final double x;    // The X coordinate
    private final double y;    // The Y coordinate
    private final double radius;
    private final Color color;

    public Point(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.radius = 2.5d;		//default value
        this.color = new Color(1d, 0d, 0d, 1d);
    }
    
    public Point(final double x, final double y, final double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = new Color(1d, 0d, 0d, 1d);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public Color getColor() {
    	return this.color;
    }
    
	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy);
    }
    
    public Point translate(double dx, double dy, double radius) {
        return new Point(x + dx, y + dy, radius);
    }

    public double distance(Point point) {
    	double xDist = Math.abs(this.getX() - point.getX());
    	double yDist = Math.abs(this.getY() - point.getY());
    	return Math.sqrt(xDist*xDist + yDist*yDist);
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
                ", color=" + color.toString() + 
                "}";
    }
}
