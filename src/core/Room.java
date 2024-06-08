package core;

import java.util.*;
public class Room {
    private int width;
    private int height;
    private Coord topLeft;
    private Coord topRight;
    private Coord bottomLeft;
    private Coord bottomRight;

    public Room(Coord topLeftPoint, int w, int h) {
        width = w;
        height = h;

        topLeft = topLeftPoint;
        topRight = topLeft.shiftCoord(width, 0);
        bottomLeft = topLeft.shiftCoord(0, -height);
        bottomRight = topLeft.shiftCoord(width, -height);
    }

    /**
     * @param r1 Room 1
     * @param r2 Room 2
     * @return
     */
    public static boolean overLap(Room r1, Room r2) {

        // If room doesn't exist, then there is no overlap.
        if (r1 == null || r2 == null) {
            return false;
        }

        // If bottom left point of 1 rectangle is higher than the other, then one rectangle is on above another
        if (r1.topLeft.getY() <= r2.bottomRight.getY() || r2.topLeft.getY() <= r1.bottomRight.getY()) {
            return false;
        }

        // Check if one rectangle is to the left of the other
        if (r1.bottomRight.getX() <= r2.topLeft.getX() || r2.bottomRight.getX() <= r1.topLeft.getX()) {
            return false;
        }


        return true;
    }

    public static boolean insideBoard(Room board, Room r) {

        // Check if r is entirely within board horizontally
        boolean insideHorizontally = board.topLeft.compareX(r.topLeft) < 0
                && board.bottomRight.compareX(r.bottomRight) > 0;

        // Check if r is entirely within board vertically
        boolean insideVertically = board.topLeft.compareY(r.topLeft) > 0
                && board.bottomRight.compareY(r.bottomRight) < 0;

        return insideHorizontally && insideVertically;
    }

    public Location getCenterLoc() {
        double xLoc = (topLeft.getX() + topRight.getX()) / 2.0;
        double yLoc = (topLeft.getY() + bottomLeft.getY()) / 2.0;
        return new Location(xLoc, yLoc);
    }


    public static double distance(Room r1, Room r2) {
        return Location.distance(r1.getCenterLoc(), r2.getCenterLoc());
    }

    public Coord getTopLeft() {
        return topLeft;
    }

    public Coord getTopRight() {
        return topRight;
    }

    public Coord getBottomLeft() {
        return bottomLeft;
    }

    public Coord getBottomRight() {
        return bottomRight;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public ArrayList<Room> getKNearestRooms(ArrayList<Room> rooms, int k) {
        DistanceFromRoomComparator comparator = new DistanceFromRoomComparator(this);

        ArrayList<Room> sortedRooms = new ArrayList<>(rooms);
        sortedRooms.sort(comparator);

        ArrayList<Room> sublist = new ArrayList<>();

        int i = 0;
        while (sublist.size() < k) {
            if (!sortedRooms.get(i).equals(this)) {
                sublist.add(sortedRooms.get(i));
            }
            i++;
        }

        return sublist;
    }

    public ArrayList<Coord> getAllEdgePoints() {
        ArrayList<Coord> list = new ArrayList<>();
        list.add(this.bottomLeft);
        list.add(this.bottomRight);
        list.add(this.topLeft);
        list.add(this.topRight);
        return list;
    }


    // Used Chat-GPT to create a comparator for distance between rooms.
    public class DistanceFromRoomComparator implements Comparator<Room> {
        private Room referenceRoom;

        public DistanceFromRoomComparator(Room referenceRoom) {
            this.referenceRoom = referenceRoom;
        }

        @Override
        public int compare(Room room1, Room room2) {
            double distanceToRoom1 = Location.distance(referenceRoom.getCenterLoc(), room1.getCenterLoc());
            double distanceToRoom2 = Location.distance(referenceRoom.getCenterLoc(), room2.getCenterLoc());

            return Double.compare(distanceToRoom1, distanceToRoom2);
        }
    }

    public static boolean insideRoom(Room r, Coord c) {
        boolean insideHorizontally = c.getX() > r.getTopLeft().getX() && c.getX() < r.getTopRight().getX();
        boolean insideVertically = c.getY() < r.getTopLeft().getY() && c.getY() > r.getBottomLeft().getY();
        return insideHorizontally && insideVertically;
    }


    public String printCoords() {
        return "(" + topLeft.getX() + ", " + topLeft.getY() + ")";
    }

    public ArrayList<Coord> turnCornersToList() {
        ArrayList<Coord> corners = new ArrayList<Coord>();
        corners.add(this.topLeft);
        corners.add(this.topRight);
        corners.add(this.bottomLeft);
        corners.add(this.bottomRight);

        return corners;
    }

    public String printDims() {
        return " dims: [" + this.width + ", " + this.height + "]";
    }

    public String printCenter() {
        return "(" + getCenterLoc().getX() + ", " + getCenterLoc().getY() + ")";
    }

}
