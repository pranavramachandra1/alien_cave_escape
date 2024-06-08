package core;
import java.util.*;

public class RoomGraph {


    HashMap<Room, Integer> indMap;
    ArrayList<Room> rooms;
    int numRooms;
    int kNear;

    MSTGraph g;
    int[] mstRef;
    public RoomGraph(ArrayList<Room> rs, int k) {
        numRooms = rs.size();
        rooms = rs;
        indMap = new HashMap<>();
        kNear = k;
        g = new MSTGraph(numRooms);

        createIndMap();

        addAllEdges();

        // Make MST:
        mstRef = g.createMST();
    }

    private void createIndMap() {
        int count = 0;
        for (Room r: rooms) {
            indMap.put(r, count);
            count++;
        }
    }

    private void addAllEdges() {
        for (Room r : rooms) {
            for (Room x : r.getKNearestRooms(rooms, kNear)) {
                g.addEdge(indMap.get(r), indMap.get(x), (int) Room.distance(r, x));
            }
        }
    }

    public int[] getPointers() {
        return mstRef;
    }

    public ArrayList<Integer> compareAllEdges() {
        return g.compareAllEdges();
    }
}
