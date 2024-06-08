package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.io.File;
import java.util.*;

import static java.lang.Long.parseLong;
public class World {

    // Board generating vars:
    private final int BOARD_LENGTH = 75;
    private final int BOARD_HEIGHT = 50;

    private final int HUD_OFFSET = 1;

    private static final int HUD_X_OFFSET = 15;

    private static final int ALIEN_UPDATE_RATE = 500;

    private static final int ALIEN_VISIBILITY_RAD = 10;
    private final Random nextGen;
    private final int SMALLEST_ROOM_DIM = 3;
    private final int LARGEST_ROOM_DIM = BOARD_HEIGHT;

    private final int MIN_ROOMS = Integer.MAX_VALUE;

    private final int MAX_ROOMS = Integer.MAX_VALUE;

    private final int COUNT_LIMITER = 1000;
    private TETile[][] board;

    private int numRooms;
    private ArrayList<Room> rooms;
    private final Room boardRoom;

    private int width;
    private int height;
    private int kNearestConstant;
    // List of sprite used in the game:
    private TETile wall;

    // Game running vars:
    private long prevActionTimestamp;
    private long prevFrameTimestamp;

    // Sprites:
    private Avatar avatar;
    private static final int AVATAR_ROOM_LOCATION = 1;

    private ArrayList<Alien> aliens;
    private final int NUM_ALIENS = 3;
    private int numAliens;

    private final int NUMDABLOONS = 4;
    private ArrayList<Dabloon> dabloons;
    private int numDabloons;

    private SpeedPotion speedPotion;

    private InvisibilityPotion invisibilityPotion;
    private int spRoomLocation_FACTOR = 2;
    private int spRoomLocation;

    private int ipRoomLocation_FACTOR = 4;
    private int ipRoomLocation;
    private  LaserGun laserGun;
    private int laserRoomLocation_FACTOR = 3;
    private int laserRoomLocation;

    private ExitDoor exitDoor;

    // Game play:
    private int finalRoom;

    private final TERenderer ter = new TERenderer();

    private AlienSearch alienGraph;

    private ArrayList<Coord> blastTiles;

    private static final int EXPLOSIONFRAMECOUNT = 10;
    private int explosionCounter;
    private String endString;

    private boolean win;

    private static final String SAVE_FILE_PATH = "save.txt";
    private String numberSeed;


    // Vars for loading worlds:
    private TileMap tileMap;

    private static final int WHITE = 225;

    private static final int STARTINGY = 40;

    private static final int FONTSIZE = 50;


    public World(String s) {

        // Generate specs for World
        seed = parseLong(s.replaceAll("\\D", ""));
        nextGen = new Random(seed);
        numRooms = nextNum(MIN_ROOMS, MAX_ROOMS);

        // Other properties
        width = BOARD_LENGTH;
        height = BOARD_HEIGHT;

        ter.initialize(width, height + 3);
        board = new TETile[width][height + 3];
        wall = Tileset.WALL;

        // Used for testing if rooms are inside board
        rooms = new ArrayList<>();
        Coord boardCoords = new Coord(0, height);
        boardRoom = new Room(boardCoords, width, height);
        // Fill tiles with nothing
        fillWithNothing(board);
        // Generate Rooms
        generateRooms();
        // Draw all Rooms
        drawRooms();
        // Connect Rooms
        connectRooms();

        spRoomLocation = (Integer) numRooms / spRoomLocation_FACTOR;
        ipRoomLocation = (Integer) numRooms / ipRoomLocation_FACTOR;
        laserRoomLocation = (Integer) numRooms / laserRoomLocation_FACTOR;

        // Place aliens:
        if (NUM_ALIENS >= numRooms) {
            numAliens = numRooms - 1;
        } else {
            numAliens = NUMDABLOONS;
        }
        aliens = new ArrayList<>();
        placeAliens();

        // Place dabloons
        if (numDabloons >= numRooms) {
            numDabloons = numRooms - 1;
        } else {
            numDabloons = NUMDABLOONS;
        }

        dabloons = new ArrayList<>();
        placeDabloons();

        // Place speed potion:
        placeSpeedPotion();

        // Place speed potion:
        placeInvisibilityPotion();

        // Place laser gun
        placeLaserGun();
        blastTiles = new ArrayList<>();
        explosionCounter = EXPLOSIONFRAMECOUNT;

        // Place player:
        placePlayer();

        // Place exit door:
        placeExitDoor();

        getAlienGraph();

        endString = "";
        tileMap = new TileMap();
    }


    // Used ChatGPT to help generate limited-range random number generation.
    private int nextNum(int lowerLimit, int upperLimit) {
        // Ensure a positive range is always returned, including upperLimit
        return lowerLimit + nextGen.nextInt(upperLimit - lowerLimit + 1);
    }

    /**
     * Generates the Rooms
     */
    private void generateRooms() {

        int count = 0;
        while (rooms.size() < numRooms) {

            // Potential room is initialized:
            Coord c = new Coord(nextNum(0, width), nextNum(0, height));
            Room r = new Room(c, nextNum(SMALLEST_ROOM_DIM, LARGEST_ROOM_DIM),
                    nextNum(SMALLEST_ROOM_DIM, LARGEST_ROOM_DIM));

            if (Room.insideBoard(boardRoom, r) && checkNoOverlap(r)) {
                rooms.add(r);
                fillRoom(r);
                count = 0;
            }

            if (count > COUNT_LIMITER) {
                numRooms = rooms.size();
                break;
            }
            count++;
        }
        kNearestConstant = numRooms - 2;
    }

    /**
     * Draws every room in generated rooms
     */
    private void drawRooms() {
        for (Room r : rooms) {
            drawRoom(board, r);
        }
    }

    private void fillRoom(Room r) {
        for (int x = r.getBottomLeft().getX() + 1; x < r.getTopRight().getX(); x++) {
            for (int y = r.getBottomLeft().getY() + 1; y < r.getTopRight().getY(); y++) {
                this.board[x][y] = Tileset.FLOOR;
            }
        }
    }

    private boolean checkNoOverlap(Room r) {
        for (Room ref : rooms) {
            if (Room.overLap(r, ref)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Draws an individual room
     * @param tiles
     * @param r
     */
    public void drawRoom(TETile[][] tiles, Room r) {
        // Draw left side
        drawToStraight(tiles, wall, r.getTopLeft(), r.getBottomLeft());

        // Draw bottom side
        drawToStraight(tiles, wall, r.getBottomLeft(), r.getBottomRight());

        // Draw right side
        drawToStraight(tiles, wall, r.getBottomRight(), r.getTopRight());

        // Draw top side
        drawToStraight(tiles, wall, r.getTopRight(), r.getTopLeft());
    }

    private void drawToStraight(TETile[][] tiles, TETile t, Coord initCoord, Coord destCoord) {

        // If destCoord lies in the y-direction:
        if (initCoord.compareX(destCoord) == 0) {

            // Determine if destCoord is above or below:
            int increment = -1;
            if (initCoord.compareY(destCoord) < 0) {
                increment = 1;
            }

            // Draw path toward destCoord
            while (!initCoord.coordEquals(destCoord)) {
                draw(t, initCoord);
                initCoord = initCoord.shiftCoord(0, increment);
            }
        }

        // If destCoord lies in the x-direction:
        if (initCoord.compareY(destCoord) == 0) {

            // Determine if destCoord is left or right:
            int increment = -1;
            if (initCoord.compareX(destCoord) < 0) {
                increment = 1;
            }

            // Draw path toward destCoord
            while (!initCoord.coordEquals(destCoord)) {
                draw(t, initCoord);
                initCoord = initCoord.shiftCoord(increment, 0);
            }
        }
    }


    /**
     * Sets an individual "nothing" tile to a particular tile.
     * @param t
     * @param c
     */
    public void draw(TETile t, Coord c) {
        TETile[][] tiles = this.board;
        if (tiles[c.getX()][c.getY()] != Tileset.FLOOR) {
            tiles[c.getX()][c.getY()] = t;
        }
    }

    /**
     * Connects all the rooms
     */
    private void connectRooms() {
        RoomGraph rg = new RoomGraph(rooms, kNearestConstant);
        int[] pointers = rg.getPointers();
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == -1) {
                finalRoom = i;
                continue;
            }
            drawConnection(rooms.get(i), rooms.get(pointers[i]));
        }
    }

    private void drawConnection(Room r1, Room r2) {

        Coord start = r1.getCenterLoc().convertToCoord();
        Coord end = r2.getCenterLoc().convertToCoord();

        // Determine whether to draw horizontal or vertical line first
        boolean horizontalFirst = Math.abs(start.getX() - end.getX()) > Math.abs(start.getY() - end.getY());

        if (horizontalFirst) {
            drawHorizontalPath(start, new Coord(end.getX(), start.getY()), Tileset.FLOOR);
            drawVerticalPath(new Coord(end.getX(), start.getY()), end, Tileset.FLOOR);
        } else {
            drawVerticalPath(start, new Coord(start.getX(), end.getY()), Tileset.FLOOR);
            drawHorizontalPath(new Coord(start.getX(), end.getY()), end, Tileset.FLOOR);
        }

        if (horizontalFirst) {
            drawHorizontalWalls(start, new Coord(end.getX(), start.getY()), r1, r2);
            drawVerticalWalls(new Coord(end.getX(), start.getY()), end, r1, r2);
        } else {
            drawVerticalWalls(start, new Coord(start.getX(), end.getY()), r1, r2);
            drawHorizontalWalls(new Coord(start.getX(), end.getY()), end, r1, r2);
        }
    }


    private void drawHorizontalWalls(Coord start, Coord end, Room r1, Room r2) {
        int step = start.getX() < end.getX() ? 1 : -1;
        for (int x = start.getX(); x != end.getX(); x += step) {

            // Drawing the path and walls
            if (!Room.insideRoom(r1, new Coord(x, start.getY())) && !Room.insideRoom(r2, new Coord(x, start.getY()))) {
                draw(wall, new Coord(x, start.getY() - 1)); // Wall below
                draw(wall, new Coord(x, start.getY() + 1)); // Wall above
            }
        }
        // Draw at the end point
        if (!Room.insideRoom(r1, end) && !Room.insideRoom(r2, end)) {
            draw(wall, new Coord(end.getX(), end.getY() - 1)); // Wall below
            draw(wall, new Coord(end.getX(), end.getY() + 1)); // Wall above
        }
    }

    private void drawVerticalWalls(Coord start, Coord end, Room r1, Room r2) {
        int step = start.getY() < end.getY() ? 1 : -1;
        for (int y = start.getY(); y != end.getY(); y += step) {

            // Drawing the path and walls
            if (!Room.insideRoom(r1, new Coord(start.getX(), y)) && !Room.insideRoom(r2, new Coord(start.getX(), y))) {
                draw(wall, new Coord(start.getX() - 1, y)); // Wall to the left
                draw(wall, new Coord(start.getX() + 1, y)); // Wall to the right
            }

        }
        // Draw at the end point
        if (!Room.insideRoom(r1, end) && !Room.insideRoom(r2, end)) {
            draw(wall, new Coord(end.getX() - 1, end.getY())); // Wall to the left
            draw(wall, new Coord(end.getX() + 1, end.getY())); // Wall to the right
        }
    }

    private void drawHorizontalPath(Coord start, Coord end, TETile tile) {
        int step = start.getX() < end.getX() ? 1 : -1;
        for (int x = start.getX(); x != end.getX(); x += step) {
            // Drawing the path and walls
            draw(tile, new Coord(x, start.getY()));
        }
        // Draw at the end point
        draw(tile, end);
    }

    private void drawVerticalPath(Coord start, Coord end, TETile tile) {
        int step = start.getY() < end.getY() ? 1 : -1;
        for (int y = start.getY(); y != end.getY(); y += step) {
            // Drawing the path and walls
            draw(tile, new Coord(start.getX(), y));
        }
        // Draw at the end point
        draw(tile, end);
    }


    /**
     * Fills the 2D array of tiles with NOTHING tiles.
     * @param tiles
     */
    public void fillWithNothing(TETile[][] tiles) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height + 3; y++) { // FIX THIS HERE
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void generateBoard() {
        ter.initialize(this.width, this.height);
        ter.renderFrame(this.board);
    }

    public TETile[][] getBoard() {
        return this.board;
    }

    private TETile getTileFromCoord(Coord c) {
        if (Room.insideRoom(boardRoom, c)) {
            return board[c.getX()][c.getY()];
        }
        return null;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void drawSprite(TETile t, Coord c) {
        board[c.getX()][c.getY()] = t;
    }

    private void placePlayer() {
        Coord c = rooms.get(0).getCenterLoc().convertToCoord();
        avatar = new Avatar(Tileset.AVATAR, c, numDabloons, speedPotion, invisibilityPotion, laserGun);
        drawSprite(avatar.getImage(), avatar.getLocation());
    }

    private void placePlayer(Coord c) {
        avatar = new Avatar(Tileset.AVATAR, c, numDabloons, speedPotion, invisibilityPotion, laserGun);
        drawSprite(avatar.getImage(), avatar.getLocation());
    }

    private void placeAliens() {
        HashSet<Integer> roomsSelected = new HashSet<>();
        while (aliens.size() < numAliens) {
            int roomNumber = nextNum(AVATAR_ROOM_LOCATION, numRooms - 1);
            if (roomsSelected.contains(roomNumber)) { // Make sure aliens are not added to the same room.
                continue;
            }
            roomsSelected.add(roomNumber);

            Coord c = rooms.get(roomNumber).getCenterLoc().convertToCoord();
            Alien a = new Alien(Tileset.ALIEN, c);
            aliens.add(a);
            drawSprite(a.getImage(), a.getLocation());
        }
    }

    private void placeAliens(ArrayList<Coord> coords) {
        for (Coord c : coords) {
            Alien a = new Alien(Tileset.ALIEN, c);
            aliens.add(a);
            drawSprite(a.getImage(), a.getLocation());
        }
    }

    private void getAlienGraph() {
        int[][] grid = new int[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y > -1; y--) {
                if (board[x][y] == Tileset.FLOOR || board[x][y] == Tileset.AVATAR || board[x][y] == Tileset.ALIEN) {
                    grid[y][x] = 1;
                } else {
                    grid[y][x] = 0;
                }
            }
        }

        alienGraph = new AlienSearch(grid);
    }

    private void placeDabloons() {
        HashSet<Integer> roomsSelected = new HashSet<>();
        while (dabloons.size() < numDabloons) {
            int roomNumber = nextNum(AVATAR_ROOM_LOCATION, numRooms - 1);
            if (roomsSelected.contains(roomNumber)) { // Make sure dabloons are not added to the same room.
                continue;
            }
            roomsSelected.add(roomNumber);

            Coord c = rooms.get(roomNumber).getTopLeft().shiftCoord(1, -1);
            Dabloon d = new Dabloon(Tileset.DABLOON, c);
            dabloons.add(d);
            drawSprite(d.getImage(), d.getLocation());
        }
    }

    private void placeDabloons(ArrayList<Coord> coords) {
        for (Coord c : coords) {
            Dabloon d = new Dabloon(Tileset.DABLOON, c);
            dabloons.add(d);
            drawSprite(d.getImage(), d.getLocation());
        }
    }

    private void placeLaserGun() {
        Coord c = rooms.get(laserRoomLocation).getBottomLeft().shiftCoord(1, 1);
        laserGun = new LaserGun(Tileset.LASERGUN, c);
        drawSprite(laserGun.getImage(), laserGun.getLocation());
    }

    private void placeLaserGun(Coord c) {
        laserGun = new LaserGun(Tileset.LASERGUN, c);
        drawSprite(laserGun.getImage(), laserGun.getLocation());
    }

    private void placeSpeedPotion() {
        Coord c = rooms.get(spRoomLocation).getTopRight().shiftCoord(-1, -1);
        speedPotion = new SpeedPotion(Tileset.SPEEDPOTION, c);
        drawSprite(speedPotion.getImage(), speedPotion.getLocation());
    }

    private void placeSpeedPotion(Coord c) {
        speedPotion = new SpeedPotion(Tileset.SPEEDPOTION, c);
        drawSprite(speedPotion.getImage(), speedPotion.getLocation());
    }

    private void placeInvisibilityPotion() {
        Coord c = rooms.get(ipRoomLocation).getTopRight().shiftCoord(-1, -1);
        invisibilityPotion = new InvisibilityPotion(Tileset.INVISPOTION, c);
        drawSprite(invisibilityPotion.getImage(), invisibilityPotion.getLocation());
    }

    private void placeInvisibilityPotion(Coord c) {
        invisibilityPotion = new InvisibilityPotion(Tileset.INVISPOTION, c);
        drawSprite(invisibilityPotion.getImage(), invisibilityPotion.getLocation());
    }

    // Place exit as far from player is possible
    private void placeExitDoor() {
        ArrayList<Room> reordered = rooms.get(finalRoom).getKNearestRooms(rooms, numRooms - 1);
        Coord c = reordered.get(reordered.size() - 1).getBottomRight().shiftCoord(-1, 1);
        exitDoor = new ExitDoor(c);
        this.avatar.setExitDoor(c);
        drawSprite(exitDoor.getImage(), exitDoor.getLocation());
    }

    public ArrayList<Integer> getEdgeLengths() {
        RoomGraph rg = new RoomGraph(rooms, kNearestConstant);
        return rg.compareAllEdges();
    }

    // RUNNING THE GAME:

    // test run:
    public void rederImageOfBoard() {
        renderBoard();
    }

    public void runGame() {
        System.out.println("Alien Cave Escape has started!");
        resetActionTimer();
        resetFrameTimer();
        while (!endGame()) {
            updateMenu();
            updateBoard();
            renderBoard();
            if (checkAvatarAlienOverlap()) {
                avatar.reduceLive();
                Sprite.respawn(board, avatar);
                for (Alien a : aliens) {
                    Sprite.respawn(board, a);
                }
            }

            if (!blastTiles.isEmpty() && explosionCounter > 0) {
                explosionCounter--;
                if (explosionCounter == 0) {
                    resetBlastTiles();
                }
            }

            if (endString.equals(":Q") || endString.equals(":q")) {
                break;
            }
            StdDraw.pause(10);
        }
        determineWin();
        updateResult();
        saveBoard();
        System.out.println("Game finished.");
    }

    private void resetBlastTiles() {
        for (Coord c : blastTiles) {
            draw(Tileset.FLOOR, c);
        }
        blastTiles = new ArrayList<>();
        explosionCounter = EXPLOSIONFRAMECOUNT;
    }

    private boolean checkAvatarAlienOverlap() {
        for (Alien a : aliens) {
            if (a.getLocation().coordEquals(avatar.getLocation())) {
                return true;
            }
            // Check if adjacent:
            if (a.getLocation().shiftCoord(0, -1).coordEquals(avatar.getLocation())) {
                return true;
            }
            if (a.getLocation().shiftCoord(0, 1).coordEquals(avatar.getLocation())) {
                return true;
            }
            if (a.getLocation().shiftCoord(-1, 0).coordEquals(avatar.getLocation())) {
                return true;
            }
            if (a.getLocation().shiftCoord(1, 0).coordEquals(avatar.getLocation())) {
                return true;
            }
        }
        return false;
    }

    private void updateBoard() {
        if (actionDeltaTime() > ALIEN_UPDATE_RATE) {
            updateAliens();
            resetActionTimer();
            return;
        }
        String temp = "";
        if (StdDraw.hasNextKeyTyped()) {
            char nextKey = StdDraw.nextKeyTyped();
            updateBoardFromInput(nextKey);
        }
        if (avatar.getNumDabloons() == numDabloons) {
            ExitDoor.openExitDoor(board, exitDoor);
        }

    }

    public void updateBoardFromInput(char nextKey) {
        if (nextKey == 'w' || nextKey == 'W') {
            Avatar.move(board, avatar, 'w');
        }
        if (nextKey == 'a' || nextKey == 'A') {
            Avatar.move(board, avatar, 'a');
        }
        if (nextKey == 's' || nextKey == 'S') {
            Avatar.move(board, avatar, 's');
        }
        if (nextKey == 'd' || nextKey == 'D') {
            Avatar.move(board, avatar, 'd');
        }
        if (nextKey == 'p' || nextKey == 'P') {
            avatar.getSpeedPotion().togglePotionEffects();
        }
        if (nextKey == 'o' || nextKey == 'O') {
            avatar.getInvisibilityPotion().togglePotionEffects();
        }
        if (nextKey == ' ' || nextKey == 'W') {
            Avatar.blastLaser(board, avatar, aliens);
            drawLaserBlast();
        }
        if (nextKey == ':') {
            if (endString.isEmpty()) {
                endString += nextKey;
            }
        }
        if (nextKey == 'Q' || nextKey == 'q') {
            if (endString.equals(":")) {
                endString += nextKey;
            }
        }
    }

    public void runGameFromString(String input) {
        endString = "";

        for (char c : input.toCharArray()) {
            updateBoardFromInput(c);
            if (endString.equals(":Q") || endString.equals(":q")) {
                break;
            }
        }
        saveBoard();
    }

    private void drawLaserBlast() {
        int rad = avatar.blastRadius();
        int xLow = avatar.getLocation().getX() - rad / 2;
        int xHigh = avatar.getLocation().getX() + rad / 2;
        int yLow = avatar.getLocation().getY() - rad / 2;
        int yHigh = avatar.getLocation().getY() + rad / 2;
        for (int i = xLow; i < xHigh; i++) {
            for (int j = yLow; j < yHigh; j++) {

                Coord blastCoord = new Coord(i, j);

                if (Coord.distance(blastCoord, avatar.getLocation()) <= rad
                        && Coord.distance(new Coord(i, j), avatar.getLocation()) > 0
                        && getTileFromCoord(blastCoord) == Tileset.FLOOR) {
                    blastTiles.add(blastCoord);
                    drawSprite(Tileset.EXPLOSION, blastCoord);
                }
            }
        }
    }

    private void renderBoard() {
        StdDraw.clear(Color.BLACK);
        ter.drawTiles(board);
//        ter.renderFrame(board);
    }

    private void updateAliens() {
        for (Alien a : aliens) {
            if (Coord.distance(a.getLocation(), avatar.getLocation()) < ALIEN_VISIBILITY_RAD
                    && !avatar.atSpawn()
                    && !avatar.getInvisibilityPotion().hasEffect()) {
                int[] nextStep = alienGraph.getNextMove(coordToGrid(a.getLocation()),
                        coordToGrid(avatar.getLocation()));
                if (nextStep != null) {
                    Alien.move(board, gridToCoord(nextStep), a);
                }
            }
        }
    }

    private int[] coordToGrid(Coord location) {
        return new int[]{location.getY(), location.getX()};
    }

    private Coord gridToCoord(int[] nextStep) {
        return new Coord(nextStep[1], nextStep[0]);
    }

    private boolean endGame() {
        return (avatar.getNumDabloons() == numDabloons
                && avatar.getLocation().coordEquals(exitDoor.getLocation()))
                || avatar.getNumLives() <= 0;
    }

    private void determineWin() {
        if (avatar.getNumDabloons() == numDabloons && avatar.getLocation().coordEquals(exitDoor.getLocation())) {
            win = true;
        } else {
            win = false;
        }
    }

    private long actionDeltaTime() {
        return System.currentTimeMillis() - prevActionTimestamp;
    }

    private long frameDeltaTime() {
        return System.currentTimeMillis() - prevFrameTimestamp;
    }


    private void resetActionTimer() {
        prevActionTimestamp = System.currentTimeMillis();
    }

    private void resetFrameTimer() {
        prevFrameTimestamp = System.currentTimeMillis();
    }

    private ArrayList<String> getHUDDisplayText() {
        String sActive = "Inactive";
        if (avatar.getSpeedPotion().hasEffect()) {
            sActive = "Active";
        }

        String iActive = "Inactive";
        if (avatar.getInvisibilityPotion().hasEffect()) {
            iActive = "Active";
        }

        ArrayList<String> hudText = new ArrayList<>();
        hudText.add("# of dabloons: " + avatar.getNumDabloons());
        hudText.add("# of lives: " + avatar.getNumLives());
        hudText.add("Seed Potion: " + sActive);
        hudText.add("Invisibility Potion: " + iActive);
        return hudText;
    }
    private void drawText(String text, double x, double y) {
        StdDraw.setPenColor(WHITE, WHITE, WHITE);
        new Font("Monaco", Font.BOLD, FONTSIZE);
        StdDraw.text(x, y, text);
    }

    private void updateMenu() {
        ArrayList<String> hudText = getHUDDisplayText();

        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        int offset = 0;
        for (String s : hudText) {
            drawText(s, offset + 2, height + HUD_OFFSET);
            offset += HUD_X_OFFSET;
        }

        if (getTileFromCoord(new Coord(x, y)) != null) {
            StdDraw.text(width - HUD_X_OFFSET,
                    height + HUD_OFFSET, "Tile: " + getTileFromCoord(new Coord(x, y)).description());
        }
        StdDraw.show();
    }

    private void updateResult() {
        if (win) {
            drawText("You win!", width - HUD_OFFSET * 3, height);
        } else {
            drawText("You lose!", width - HUD_OFFSET * 3, height);
        }
        StdDraw.show();
    }


    public void saveBoard() {
        String entry = this.width + " " + this.height + "\n";
        for (int y = this.height - 1; y > -1; y--) {
            String line = "";
            for (int x = 0; x < this.width; x++) {
                line += tileMap.getChar(board[x][y]);
            }
            line += ("\n");
            entry += (line);
        }
        entry += this.seed + "\n";
        entry += this.avatar.getNumLives();
        FileUtils.writeFile(SAVE_FILE_PATH, entry);
    }

    public void eliminiateSprite(Sprite s) {
        board[s.getLocation().getX()][s.getLocation().getY()] = Tileset.FLOOR;
    }


    private boolean placeSpeed;
    private boolean placeInvis;
    private boolean placeLaser;

    private ArrayList<Coord> alienCoords;
    private ArrayList<Coord> dabloonCoords;

    private Coord avatarCoord;
    private long seed;

    private Coord speedCoord;
    private Coord invisCoord;

    private Coord laserCoord;

    private int avatarNumLives;

    public World() {
        width = BOARD_LENGTH;
        height = BOARD_HEIGHT;
        ter.initialize(width, height + 3);
        board = new TETile[width][height + 3];
        numDabloons = 0;
        numAliens = 0;
        aliens = new ArrayList<>();
        dabloons = new ArrayList<>();

        alienCoords = new ArrayList<>();
        dabloonCoords = new ArrayList<>();

        tileMap = new TileMap();

        placeAliens(alienCoords);
        placeDabloons(dabloonCoords);

        loadGame();

        nextGen = new Random(seed);
        numRooms = nextNum(MIN_ROOMS, MAX_ROOMS);

        ter.initialize(width, height + 3);

        board = new TETile[width][height + 3];

        wall = Tileset.WALL;

        rooms = new ArrayList<>();
        boardRoom = new Room(new Coord(0, height), width, height);
        fillWithNothing(board);
        generateRooms();
        drawRooms();
        connectRooms();

        placeAliens(alienCoords);
        placeDabloons(dabloonCoords);

        // Place speed potion:
        if (placeSpeed) {
            placeSpeedPotion(speedCoord);
        } else {
            placeSpeedPotion();
            speedPotion.usePotion();
            eliminiateSprite(speedPotion);
        }

        if (placeInvis) {
            placeInvisibilityPotion(invisCoord);
        } else {
            placeInvisibilityPotion();
            invisibilityPotion.usePotion();
            eliminiateSprite(invisibilityPotion);
        }

        // Place laser gun
        if (placeLaser) {
            placeLaserGun(laserCoord);
        } else {
            placeLaserGun();
            laserGun.acquireLaserGun();
            eliminiateSprite(laserGun);
        }

        blastTiles = new ArrayList<>();
        explosionCounter = EXPLOSIONFRAMECOUNT;

        placePlayer(avatarCoord);
        avatar.setNumLives(avatarNumLives);
        placeExitDoor();
        getAlienGraph();

        endString = "";
        tileMap = new TileMap();
    }

    public void loadGame() {
        placeSpeed = false;
        placeInvis = false;
        placeLaser = false;

        In i = new In(new File(SAVE_FILE_PATH));
        // Read dimensions and initialize board
        String dim = i.readLine();
        if (Objects.equals(dim, "") || dim == null) {
            throw new IllegalArgumentException("No dimensions listed");
        }
        String[] dims = dim.split(" ");
        int w = Integer.parseInt(dims[0]);
        int h = Integer.parseInt(dims[1]);
        this.width = w;
        this.height = h;
        board = new TETile[h][w];
        h--;
        // Process line by line
        while (i.hasNextLine() && h > -1) {
            // Read line
            String line = i.readLine();
            char[] chars = line.toCharArray();
            // read across hth row
            int count = 0;
            for (char c : chars) {
                if (c == 'd') {
                    numDabloons++;
                    dabloonCoords.add(new Coord(count, h));
                } else if (c == 'q') {
                    numAliens++;
                    alienCoords.add(new Coord(count, h));
                } else if (c == 'i') {
                    placeInvis = true;
                    invisCoord = new Coord(count, h);
                } else if (c == 's') {
                    placeSpeed = true;
                    speedCoord = new Coord(count, h);
                } else if (c == 'l') {
                    placeLaser = true;
                    laserCoord = new Coord(count, h);
                } else if (c == 'a') {
                    System.out.println(count + ", " + h);
                    avatarCoord = new Coord(count, h);
                }
                count++;
            }
            h--;
        }

        seed = parseLong(i.readLine());
        avatarNumLives = (int) parseLong(i.readLine());
    }
}
