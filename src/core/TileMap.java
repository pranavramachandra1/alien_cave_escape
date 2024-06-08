package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.HashMap;

public class TileMap {

    private HashMap<TETile, Character> tileMap;
    private HashMap<Character, TETile> charMap;

    public TileMap() {

        tileMap = new HashMap<>();
        charMap = new HashMap<>();
        buildSaveDictionary();

    }

    // Building the dictionary to create save file
    public void buildSaveDictionary() {
        tileMap.put(Tileset.AVATAR, 'a');
        tileMap.put(Tileset.ALIEN, 'q');
        tileMap.put(Tileset.FLOOR, 'f');
        tileMap.put(Tileset.DABLOON, 'd');
        tileMap.put(Tileset.LASERGUN, 'l');
        tileMap.put(Tileset.EXITDOOR, 'e');
        tileMap.put(Tileset.EXITDOOR_OPENED, 'o');
        tileMap.put(Tileset.SPEEDPOTION, 's');
        tileMap.put(Tileset.INVISPOTION, 'i');
        tileMap.put(Tileset.EXPLOSION, 'p');
        tileMap.put(Tileset.WALL, 'w');
        tileMap.put(Tileset.NOTHING, 'n');

        charMap.put('a', Tileset.FLOOR);
        charMap.put('q', Tileset.FLOOR);
        charMap.put('f', Tileset.FLOOR);
        charMap.put('d', Tileset.FLOOR);
        charMap.put('l', Tileset.FLOOR);
        charMap.put('e', Tileset.FLOOR);
        charMap.put('o', Tileset.FLOOR);
        charMap.put('s', Tileset.FLOOR);
        charMap.put('i', Tileset.FLOOR);
        charMap.put('p', Tileset.FLOOR);
        charMap.put('w', Tileset.WALL);
        charMap.put('n', Tileset.NOTHING);
    }

    public TETile getTile(char c) {
        return charMap.get(c);
    }

    public char getChar(TETile t) {
        return tileMap.get(t);
    }
}
