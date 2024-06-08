package core;

import tileengine.TETile;

public class Game {
    private final int BOARD_LENGTH = 75;
    private final int BOARD_HEIGHT = 50;

    private World w;
    private StartMenu startMenu;

    private World world;
    private TETile avatar;
    private String seed;

    public Game() {

    }

    public void runGame() {
        startMenu = new StartMenu();
        startMenu.runStartMenu();

        if (startMenu.isLoadGame()) {
            world = new World();
            world.runGame();
            System.out.println("No save file stored. Play a game to try again!");
        }
        if (startMenu.isNewGame()) {
            seed = startMenu.getSeed();
            world = new World(seed);
            world.runGame();
        }

        System.out.println("Finished game!");
        System.exit(0);
    }

    public World getWorld() {
        return world;
    }

    public static void main(String[] args) {
        Game g = new Game();
        g.runGame();
    }

}
