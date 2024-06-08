package core;

import tileengine.TETile;
import tileengine.Tileset;

public class ExitDoor extends Sprite {
    public ExitDoor(Coord loc) {
        super(Tileset.EXITDOOR, loc);
    }

    public static void openExitDoor(TETile[][] board, ExitDoor ed) {
        ed.setImage(Tileset.EXITDOOR_OPENED);
        Sprite.setBoardImageToSprite(board, ed);
    }
}
