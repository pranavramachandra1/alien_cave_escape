package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.HashSet;

public class Avatar extends Sprite {

    private static final int NUMLIVES = 2;
    private int numDabloons;
    private int numLives;

    private Coord exitDoorLocation;

    private SpeedPotion speedPotion;

    private InvisibilityPotion invisibilityPotion;

    private HashSet<TETile> badSquares;

    private LaserGun laserGun;

    private final int NUM_TILES_SPEED_POTION = 2;

    public Avatar(TETile i, Coord c, int td, SpeedPotion sp, InvisibilityPotion ip, LaserGun lg) {
        super(i, c);
        numDabloons = 0;
        numLives = NUMLIVES;
        speedPotion = sp;
        badSquares = new HashSet<>();
        setBadSquares();
        invisibilityPotion = ip;
        laserGun = lg;
    }

    private void setBadSquares() {
        badSquares.add(Tileset.NOTHING);
        badSquares.add(Tileset.WALL);
    }
    public void shootLaser() {

    }

    public void setExitDoor(Coord exitLoc) {
        this.exitDoorLocation = exitLoc;
    }
    public Coord getExitDoor() {
        return this.exitDoorLocation;
    }

    public void collectDabloon() {
        numDabloons += 1;
    }

    public int getNumDabloons() {
        return numDabloons;
    }

    public int getNumLives() {
        return numLives;
    }

    public static void move(TETile[][] board, Avatar avatar, char keyStroke) {

        Coord currentLoc = avatar.getLocation();
        Coord c;
        if (avatar.speedPotion.hasEffect()) {
            c = avatar.getMoveCoordSpeedPotion(keyStroke);
            if (tryMoveLocation(board, avatar.getMoveCoordSpeedPotion(keyStroke), avatar.badSquares)) {
                c = avatar.getMoveCoordSpeedPotion(keyStroke);
                avatar.getSpeedPotion().updateStepCounter();
            }
        } else {
            c = avatar.getMoveCoord(keyStroke);
        }

        if (avatar.invisibilityPotion.hasEffect()) {
            avatar.getInvisibilityPotion().updateStepCounter();
        }

        if (board[c.getX()][c.getY()] == Tileset.FLOOR || board[c.getX()][c.getY()] == Tileset.ALIEN
                || board[c.getX()][c.getY()] == Tileset.EXPLOSION) { // Maybe clean up code?
            board[currentLoc.getX()][currentLoc.getY()] = Tileset.FLOOR;
            board[c.getX()][c.getY()] = avatar.getImage();
            avatar.updateLocation(c);
        }

        if (board[c.getX()][c.getY()] == Tileset.DABLOON) {
            board[currentLoc.getX()][currentLoc.getY()] = Tileset.FLOOR;
            board[c.getX()][c.getY()] = avatar.getImage();
            avatar.updateLocation(c);
            avatar.addDabloon();

        }

        if (board[c.getX()][c.getY()] == Tileset.SPEEDPOTION) {
            board[currentLoc.getX()][currentLoc.getY()] = Tileset.FLOOR;
            board[c.getX()][c.getY()] = avatar.getImage();
            avatar.updateLocation(c);
            avatar.speedPotion.usePotion();
        }

        if (board[c.getX()][c.getY()] == Tileset.INVISPOTION) {
            board[currentLoc.getX()][currentLoc.getY()] = Tileset.FLOOR;
            board[c.getX()][c.getY()] = avatar.getImage();
            avatar.updateLocation(c);
            avatar.invisibilityPotion.usePotion();
        }

        if (board[c.getX()][c.getY()] == Tileset.LASERGUN) {
            board[currentLoc.getX()][currentLoc.getY()] = Tileset.FLOOR;
            board[c.getX()][c.getY()] = avatar.getImage();
            avatar.updateLocation(c);
            avatar.laserGun.acquireLaserGun();
        }

        if (board[c.getX()][c.getY()] == Tileset.EXITDOOR) {
            board[currentLoc.getX()][currentLoc.getY()] = Tileset.FLOOR;
            board[c.getX()][c.getY()] = avatar.getImage();
            avatar.updateLocation(c);
        }

        if (board[c.getX()][c.getY()] == Tileset.EXITDOOR_OPENED) {
            board[currentLoc.getX()][currentLoc.getY()] = Tileset.FLOOR;
            board[c.getX()][c.getY()] = avatar.getImage();
            avatar.updateLocation(c);
        }

        if (currentLoc.coordEquals(avatar.getExitDoor())) {
            board[currentLoc.getX()][currentLoc.getY()] = Tileset.EXITDOOR;
            board[c.getX()][c.getY()] = avatar.getImage();
            avatar.updateLocation(c);
        }
    }

    private Coord getMoveCoord(char keyStroke) {
        if (keyStroke == 'w') {
            return this.getLocation().shiftCoord(0, 1);
        }
        if (keyStroke == 'a') {
            return this.getLocation().shiftCoord(-1, 0);
        }
        if (keyStroke == 's') {
            return this.getLocation().shiftCoord(0, -1);
        }
        if (keyStroke == 'd') {
            return this.getLocation().shiftCoord(1, 0);
        }
        return this.getLocation();
    }

    private Coord getMoveCoordSpeedPotion(char keyStroke) {
        if (keyStroke == 'w') {
            return this.getLocation().shiftCoord(0, NUM_TILES_SPEED_POTION);
        }
        if (keyStroke == 'a') {
            return this.getLocation().shiftCoord(-NUM_TILES_SPEED_POTION, 0);
        }
        if (keyStroke == 's') {
            return this.getLocation().shiftCoord(0, -NUM_TILES_SPEED_POTION);
        }
        if (keyStroke == 'd') {
            return this.getLocation().shiftCoord(NUM_TILES_SPEED_POTION, 0);
        }
        return this.getLocation();
    }

    private static boolean tryMoveLocation(TETile[][] board, Coord dest, HashSet<TETile> badTiles) {
        return !badTiles.contains(board[dest.getX()][dest.getY()]);
    }

    public SpeedPotion getSpeedPotion() {
        return this.speedPotion;
    }


    public InvisibilityPotion getInvisibilityPotion() {
        return this.invisibilityPotion;
    }
    private void addDabloon() {
        numDabloons++;
    }

    public void reduceLive() {
        numLives--;
    }

    public LaserGun getLaserGun() {
        return this.laserGun;
    }

    public int blastRadius() {
        return this.laserGun.getBlastRadius();
    }

    public static void blastLaser(TETile[][] board, Avatar avatar, ArrayList<Alien> aliens) {
        for (Alien a : aliens) {
            if (Coord.distance(avatar.getLocation(), a.getLocation()) < avatar.blastRadius()) {
                Sprite.respawn(board, a);
            }
        }
    }

    public void setNumLives(int avatarNumLives) {
        this.numLives = avatarNumLives;
    }
}
