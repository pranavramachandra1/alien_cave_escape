package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Alien extends Sprite {

    public Alien(TETile i, Coord loc) {

        super(i, loc);
    }

    public static void move(TETile[][] board, Coord coord, Alien a) {
        if (board[coord.getX()][coord.getY()] == Tileset.FLOOR || board[coord.getX()][coord.getY()] == Tileset.AVATAR) {
            board[a.getLocation().getX()][a.getLocation().getY()] = Tileset.FLOOR;
            board[coord.getX()][coord.getY()] = a.getImage();
            a.updateLocation(coord);
        }
    }
}
