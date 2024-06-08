package tileengine;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {

    public static TETile AVATAR = new TETile('@', Color.pink, Color.black, "you", "src/tileengine/sprites/donald_new.png", 0);
    public static final TETile ALIEN = new TETile('@', Color.red, Color.black, "alien", "src/tileengine/sprites/rock_troll.png", 0);

    public static final TETile DABLOON = new TETile('@', Color.yellow, Color.black, "dabloon", "src/tileengine/sprites/silver_star.png", 0);

    public static final TETile LASERGUN = new TETile('@', Color.cyan, Color.black, "lasergun", "src/tileengine/sprites/urand_firestarter.png", 0);


    public static final TETile EXITDOOR = new TETile('@', Color.orange, Color.black, "exitdoor","src/tileengine/sprites/closed_door.png", 0);

    public static final TETile EXITDOOR_OPENED = new TETile('@', Color.orange, Color.black, "exitdoorOpened","src/tileengine/sprites/open_door.png", 0);

    public static final TETile SPEEDPOTION = new TETile('@', Color.orange, Color.black, "speedPotion","src/tileengine/sprites/golden.png", 0);

    public static final TETile INVISPOTION = new TETile('@', Color.orange, Color.black, "invisPotion","src/tileengine/sprites/sky_blue.png", 0);

    public static final TETile EXPLOSION = new TETile('@', Color.orange, Color.black, "explosion","src/tileengine/sprites/magic_bolt_4.png", 0);
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray, "wall", "src/tileengine/sprites/cobble_blood_2_old.png", 1);
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black, "floor", "src/tileengine/sprites/black_cobalt_12.png", 2);
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.lightGray, "nothing", "src/tileengine/sprites/frozen_10.png", 3);

    public static final TETile BACKGROUND = new TETile(' ', Color.black, Color.black, "background", 4);
    public static final TETile FLOWER = new TETile('≈', Color.blue, Color.black, "water", 5);

    public static final TETile LOCKED_DOOR = new TETile('+', Color.blue, Color.black, "water", 5);

    public static final TETile UNLOCKED_DOOR = new TETile('-', Color.blue, Color.black, "water", 5);
    public void setAvatar(TETile avatar) {
        Tileset.AVATAR = avatar;
    }
}


