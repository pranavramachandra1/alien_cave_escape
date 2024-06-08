import core.AutograderBuddy;
import core.Coord;
import core.Main;
import core.World;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

import static com.google.common.truth.Truth.assertThat;

public class CoordTests {

    @Test
    public void testShift() {
        Coord c1 = new Coord(5, 5);
        Coord c2 = c1.shiftCoord(1, 1);

        assertThat(c1.equals(c2)).isFalse();
        assertThat(c2.getX() == 6).isTrue();
        assertThat(c2.getY() == 6).isTrue();
    }

    @Test
    public void testEquals() {
        Coord c1 = new Coord(1, 1);
        Coord c2 = new Coord(1, 1);
        Coord c3 = new Coord(999, 999);

        assertThat(c1.coordEquals(c2)).isTrue();
        assertThat(c2.coordEquals(c3)).isFalse();
    }

}
