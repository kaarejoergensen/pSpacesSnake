package org.team08.pspacessnake.Model;

@SuppressWarnings("restriction")
public class Point {
    private final double x;    // The X coordinate
    private final double y;    // The Y coordinate
    private double radius;
    private int color;


    public Point(final double x, final double y) {
    	this.x = x;
        this.y = y;
        this.setRadius(2.5d); // default value
        this.color = 1;
    }

    public Point(final double x, final double y, final int color) {
        this.x = x;
        this.y = y;
        this.setRadius(2.5d); // default value
        this.color = color;
    }

    public Point(final double x, final double y, final double radius) {
        this.x = x;
        this.y = y;
        this.setRadius(radius);
        this.color = 1;
    }

    public Point(final double x, final double y, final double radius, int color) {
        this.x = x;
        this.y = y;
        this.setRadius(radius);
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
 	
	public void setRadius(double radius) {
		this.radius = radius;
	}

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy, this.getRadius(), this.getColor());
    }
	
    public Point translate(double dx, double dy, double radius) {
        return new Point(x + dx, y + dy, radius, this.getColor());
    }

    public Point translate(double dx, double dy, int color) {
        return new Point(x + dx, y + dy, this.getRadius(), color);
    }
    
    public Point translate(double dx, double dy, double radius, int color) {
        return new Point(x + dx, y + dy, radius, color);
    }

    public Point translate(double dx, double dy, double radius, int color, int boardWidth, int boardHeight) {
        return new Point((x + dx + boardWidth) % boardWidth, (y + dy + boardHeight) % boardHeight, radius, color);
    }

    public double distance(Point point) {
    	double xDist = Math.abs(this.getX() - point.getX());
    	double yDist = Math.abs(this.getY() - point.getY());
    	return Math.sqrt(xDist*xDist + yDist*yDist);
    }
  
    public Double getAngleToPoint(double targetX, double targetY) {
    	double angle = Math.acos((targetX - this.getX()) / this.distance(new Point(targetX, targetY)));
    	if (this.getY() - targetY < 0.0)
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
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + getRadius() +
                ", color=" + color +
                '}';
    }
}
