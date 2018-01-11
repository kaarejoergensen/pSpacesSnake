package org.team08.pspacessnake.Model;

public class Point {
    private final double x;    // The X coordinate
    private final double y;    // The Y coordinate

    public Point(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy);
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
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
