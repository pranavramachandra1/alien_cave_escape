package core;

public class Coord {
    private int x;
    private int y;

    public Coord(int xCoord, int yCoord) {
        x = xCoord;
        y = yCoord;
    }

    public Coord shiftCoord(int xShift, int yShift) {
        Coord newCoord = new Coord(this.x, this.y);
        newCoord.x += xShift;
        newCoord.y += yShift;

        return newCoord;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int compareX(Coord o) {
        return this.x - o.x;
    }

    public int compareY(Coord o) {
        return this.y - o.y;
    }

    public boolean coordEquals(Coord c) {
        return this.x == c.x && this.y == c.y;
    }

    public static double distance(Coord c1, Coord c2) {
        return Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
    }

    public String printCoord() {
        return "(" + this.x + ", " + this.y + ")";
    }

}
