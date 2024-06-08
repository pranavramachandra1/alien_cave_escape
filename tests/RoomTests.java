import core.*;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;
import static core.Room.overLap;

public class RoomTests {

    @Test
    public void testInit() {
        Coord c1 = new Coord(0, 4);
        Room r1 = new Room(c1, 4, 4);

        assertThat(r1.getBottomLeft().coordEquals(new Coord(0, 0))).isTrue();
        assertThat(r1.getTopLeft().coordEquals(new Coord(0, 4))).isTrue();
        assertThat(r1.getBottomRight().coordEquals(new Coord(4, 0))).isTrue();
        assertThat(r1.getTopRight().coordEquals(new Coord(4, 4))).isTrue();

    }

    @Test
    public void testOverLap() {

        Coord c1 = new Coord(0, 4);
        Coord c2 = new Coord(0, 5);
        Coord c3 = new Coord(10, 10);

        Room r1 = new Room(c1, 4, 4);
        Room r2 = new Room(c2, 4, 4);
        Room r3 = new Room(c3, 1, 1);

        assertThat(overLap(r1, r2)).isTrue();
        assertThat(overLap(r1, r3)).isFalse();
        assertThat(overLap(r2, r3)).isFalse();
    }

    @Test
    public void testInside() {
        Coord c1 = new Coord(0, 50);
        Coord c2 = new Coord(20, 5);
        Coord c3 = new Coord(49, 74);

        Room board = new Room(c1, 75, 50);
        Room r1 = new Room(c2, 5, 5);
        Room r2 = new Room(c3, 20, 20);

        assertThat(Room.insideBoard(board, r1)).isTrue();
        assertThat(Room.insideBoard(board, r2)).isFalse();
    }


    @Test
    public void testKNearest() {
        Coord c1 = new Coord(1, 5);
        Coord c2 = new Coord(1, 10);
        Coord c3 = new Coord(1, 15);
        Coord c4 = new Coord(100, 100);
        Coord c5 = new Coord(200, 200);
        Coord c6 = new Coord(300, 300);

        Room r1 = new Room(c1, 2, 2);
        Room r2 = new Room(c2, 2, 2);
        Room r3 = new Room(c3, 2, 2);
        Room r4 = new Room(c4, 2, 2);
        Room r5 = new Room(c5, 2, 2);
        Room r6 = new Room(c6, 2, 2);

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r3);
        rooms.add(r4);
        rooms.add(r5);
        rooms.add(r6);

        assertThat(r1.getKNearestRooms(rooms, 3).contains(r2)).isTrue();
        assertThat(r1.getKNearestRooms(rooms, 3).contains(r3)).isTrue();
        assertThat(r1.getKNearestRooms(rooms, 3).contains(r4)).isTrue();
        assertThat(r1.getKNearestRooms(rooms, 3).contains(r6)).isFalse();
        assertThat(r1.getKNearestRooms(rooms, 3).contains(r1)).isFalse();
    }

    // TODO: include more rigorous testing for overlap function.
}
