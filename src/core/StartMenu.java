package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static java.lang.Long.parseLong;

public class StartMenu {
    private final int BOARD_LENGTH = 75;
    private final int BOARD_HEIGHT = 50;
    private static final int SIZE = 50;
    private static final int PAUSE_LENGTH = 10;

    private static final int OFFSET = 2;
    private int length;
    private int height;

    private String seed;

    private final TERenderer ter = new TERenderer();

    private TETile[][] menu;
    private TETile[][] seedMenu;
    private TETile[][] avaterMenu;

    private TETile avatarTile;

    private boolean displayErrorMessage = false;

    private boolean loadGame;

    private boolean newGame;

    private ArrayList<String> titleText;

    private static final int WHITE = 225;

    private static final int STARTINGY = 40;

    private static final String SAVE_FILE_PATH = "save.txt";
    public StartMenu() {
        height = BOARD_HEIGHT;
        length = BOARD_LENGTH;
        menu = new TETile[height][length];
        seedMenu = new TETile[height][length];
        avaterMenu = new TETile[height][length];
        ter.initialize(length + 5, height);
        titleText = new ArrayList<>();
        fillWithNothing();

        // Return values:
        seed = "";
        avatarTile = Tileset.AVATAR;

        loadGame = false;
        buildTitleText();
    }

    public void fillWithNothing() {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < length; y++) {
                menu[x][y] = Tileset.BACKGROUND;
                seedMenu[x][y] = Tileset.BACKGROUND;
                avaterMenu[x][y] = Tileset.BACKGROUND;
            }
        }
    }

    public void runStartMenu() {
        ter.renderFrame(menu);
        int count = 0;
        for (String s : titleText) {
            drawText(s, (double) length / 2, STARTINGY - count);
            count += 1;
        }
        StdDraw.show();

        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.pause(PAUSE_LENGTH);
        }

        char nextKey = StdDraw.nextKeyTyped();
        if (nextKey == 'l' || nextKey == 'L' && isLoadGame()) {

            loadGame = true;
            seed = "-1";
            return;

        }
        if (nextKey == 'n' || nextKey == 'N') {

            seed = "";
            runSeedSelectMenu();
            while (!validSeed()) {
                seed = "";
                displayErrorMessage = true;
                runSeedSelectMenu();
            }
            newGame = true;
            return;
        }
        if (nextKey == 'q' || nextKey == 'Q') {
            return;
        }
        runStartMenu();
    }

    private void buildTitleText() {
        titleText.add("Welcome to Alien Cave Escape");
        titleText.add("Your goal: evade the aliens, collect the dabloons, and reach the exit");
        titleText.add("Use your speed potion to increase your speed (activate by pressing \"P\")");
        titleText.add("Use your invisibility potion evade the aliens (activate by pressing \"O\")");
        titleText.add("Collect and use your blaster to destroy the aliens");
        titleText.add("Press N for new game");
        titleText.add("Press Q to quit and save game");
        titleText.add("Step carefully. Good luck solider.");
    }

    private void drawText(String text, double x, double y) {
        StdDraw.setPenColor(WHITE, WHITE, WHITE);
        new Font("Monaco", Font.BOLD, SIZE);
        StdDraw.text(x, y, text);
    }


    private void runSeedSelectMenu() {

        StdDraw.clear();

        char next = ' ';
        while (next != 's') {

            seed += next;


            ter.renderFrame(seedMenu);

            drawText("Seed: (Press s when finished)", BOARD_LENGTH / 2, STARTINGY);
            drawText(seed, BOARD_LENGTH / 2, STARTINGY - OFFSET);

            if (displayErrorMessage) {
                drawText("Please enter a valid seed.", BOARD_LENGTH / 2, STARTINGY);

            }


            StdDraw.show();

            while (!StdDraw.hasNextKeyTyped()) {
                StdDraw.pause(PAUSE_LENGTH);
            }
            next = StdDraw.nextKeyTyped();
            System.out.println(next);
            System.out.println(next != 's');
        }
    }

    private boolean validSeed() {

        if (!containsNumbers(seed)) {
            return false;
        }

        long s = parseLong(seed.replaceAll("\\D", ""));
        System.out.println(s);

        return true;
    }

    private  boolean containsNumbers(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i))) {
                return true; // Return true immediately if a digit is found
            }
        }
        return false; // Return false if no digits are found
    }

    public String getSeed() {
        return seed;
    }

    public TETile getAvatar() {
        return this.avatarTile;
    }

    public boolean hasSaveFile() {
        In i = new In(new File(SAVE_FILE_PATH));
        // Read dimensions and initialize board
        String dim = i.readLine();

        if (i.hasNextLine()) {
            return true;
        }

        return false;
    }

    public boolean isLoadGame() {
        return loadGame && this.hasSaveFile();
    }

    public boolean isNewGame() {
        return newGame;
    }

    public static void main(String[] args) {
        StartMenu sm = new StartMenu();
        sm.runStartMenu();
        System.out.println("Finished");
        System.out.println(sm.getSeed());
    }
}
