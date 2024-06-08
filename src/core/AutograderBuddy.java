package core;

import tileengine.TETile;
import tileengine.Tileset;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        String seed = "";
        String movements = "";
        boolean loadGame = false;
        boolean seedFound = false;
        World w;
        for (char c : input.toCharArray()) { // Creates a string consisting of the seed and movements separately.
            if (c == 'l' || c == 'L') {
                loadGame = true;
                continue;
            }

            if (c == 's' || c == 'S') {
                seedFound = true;
                continue;
            }

            if (loadGame) {
                movements += c;
            } else if (seedFound) {
                movements += c;
            } else {
                seed += c;
            }

        }
        if (seedFound) {
            if (seed.equals("")) {
                w = new World();
                return w.getBoard();
            }
            w = new World(seed);
        } else {
            w = new World();
        }

        w.runGameFromString(movements);
        return w.getBoard();
    }

    public static TETile[][] getLoadWorld(String input) {
        World w = new World();
        return w.getBoard();
    }

    public static World getWorldFromString(String input) {
        return new World(input);
    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
