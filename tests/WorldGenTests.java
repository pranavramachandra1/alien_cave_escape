import core.*;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        // TODO: write a test that uses an input like "n123swasdwasd"
    }

    @Test
    public void basicSaveTest() {
        // TODO: write a test that calls getWorldFromInput twice, with "n123swasd:q" and with "lwasd"
    }

    @Test
    // Test if board generates appropriately
    public void testVisualization1() {
        int w = 10;
        int h = 20;
        TETile[][] tiles = new TETile[w][h];

        World w1 = new World("123456789");
        w1.generateBoard();
    }

    @Test
    public void testMakeWorld() {

        // New board
        int boardWidth = 75;
        int boardHeight = 50;
        TETile[][] board = new TETile[boardWidth][boardHeight];

        String seed = "1234567890";

        // Make world
        World w1 = new World(seed);
    }

    @Test
    public void basidLoadSave() {
        String seed = "12345678987654";
//        World w = new World;
    }

    @Test
    public void testAutograderBuddy() {
       TETile[][] w = AutograderBuddy.getWorldFromInput("N1234543S:Q");
       TETile[][] w2 = AutograderBuddy.getWorldFromInput("L");
       assertThat(Arrays.deepEquals(w, w2)).isTrue();
    }

    @Test
    public void testAutograderBuddy3() {
        TETile[][] w2 = AutograderBuddy.getWorldFromInput("");
    }

    @Test
    public void testAutograderBuddy2() {
        TETile[][] b1 = AutograderBuddy.getWorldFromInput("N123456S:Q");
        TETile[][] b3 = AutograderBuddy.getWorldFromInput("LAAAAADDDDD:Q");
        assertThat(Arrays.deepEquals(b1, b3)).isTrue();
    }

}
