package core;

public class Location {

    private double x;
    private double y;

    public Location(double xCoor, double yCoor) {
        x = xCoor;
        y = yCoor;
    }

    public static double distance(Location c1, Location c2) {
        return Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
    }

    public Coord convertToCoord() {
        return new Coord((int) this.x, (int) this.y);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
