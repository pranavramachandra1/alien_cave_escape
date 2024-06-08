package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Sprite {

    private Coord location;
    private final Coord spawnPoint;
    private TETile image;

    public Sprite(TETile i, Coord loc) {
        this.location = loc;
        this.spawnPoint = loc;
        this.image = i;
    }

    public TETile getImage() {
        return this.image;
    }

    public Coord getLocation() {
        return this.location;
    }

    public void updateLocation(Coord dest) {
        this.location = dest;
    }

    public static void respawn(TETile[][] board, Sprite s) {
        board[s.getLocation().getX()][s.getLocation().getY()] = Tileset.FLOOR;
        board[s.spawnPoint.getX()][s.spawnPoint.getY()] = s.image;
        s.updateLocation(s.spawnPoint);
    }

    public void setImage(TETile newImage) {
        this.image = newImage;
    }

    public static void setBoardImageToSprite(TETile[][] board, Sprite s) {
        board[s.getLocation().getX()][s.getLocation().getY()] = s.getImage();
    }

    public boolean atSpawn() {
        return location.coordEquals(spawnPoint);
    }
}
