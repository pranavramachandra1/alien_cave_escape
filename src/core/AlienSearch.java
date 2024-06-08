package core;

import java.util.*;

/**
 * @source This code is borrowed and modified from the following source:
 * https://www.geeksforgeeks.org/a-search-algorithm/
 *
 * Changes made:
 * - Created a Node class which holds the heuristic values
 * - Implemented comparator for Nodes
 * - Uses a priority queue to determine next node instead of a hashmap
 * - Rewrote repetitive location updating
 * - Changed to create instances of the class
 * - Added nextBestMove() which returns the next best move for the alien.
 * - Comparmentalized the "finding" functions: grouped actions together to increase
 * readability.
 *
 *
 *
 * NOTE: The algorithm will ONLY run if the player is within DIST_CALC
 * of the alien. The alien will then continuously run A* until player
 * if out of range.
 *
 */

public class AlienSearch {

    class Cell {
        int parentI, parentJ;
        double f, g, h;

        Cell() {
            this.parentI = 0;
            this.parentJ = 0;
            this.f = 0;
            this.g = 0;
            this.h = 0;
        }
    }

    class Node {
        double weight;
        int i;
        int j;

        public Node(int x, int y, double w) {
            i = x;
            j = y;
            weight = w;
        }
    }

    class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return Double.compare(o1.weight, o2.weight);
        }
    }
    int[][] grid;

    List<int[]> bestPaths;
    int ROW;
    int COL;
    Cell[][] cellDetails;

    PriorityQueue<Node> queue;
    boolean[][] closedList;


    public AlienSearch(int[][] g) {
        grid = g;
        ROW = g.length;
        COL = g[0].length;
    }

    public void runAStar(int[] src, int[] dest) {
        aStarSearch(src, dest);
    }

    public int[] getNextMove(int[] src, int[] dest) {
        aStarSearch(src, dest);
        if (bestPaths != null) {
            return bestPaths.reversed().get(1);
        } else {
            return null;
        }
    }

    private boolean isValid(int row, int col) {
        return (row >= 0) && (row < grid.length) && (col >= 0) && (col < grid[0].length);
    }

    private boolean isUnBlocked(int row, int col) {
        return grid[row][col] == 1;
    }

    private boolean isDestination(int row, int col, int[] dest) {
        return row == dest[0] && col == dest[1];
    }

    private double calculateHValue(int row, int col, int[] dest) {
        return Math.sqrt((row - dest[0]) * (row - dest[0]) + (col - dest[1]) * (col - dest[1]));
    }

    private void tracePath(int[] dest) {
        int row = dest[0];
        int col = dest[1];

        Map<int[], Boolean> path = new LinkedHashMap<>();

        while (!(cellDetails[row][col].parentI == row && cellDetails[row][col].parentJ == col)) {
            path.put(new int[]{row, col}, true);
            int tempRow = cellDetails[row][col].parentI;
            int tempCol = cellDetails[row][col].parentJ;
            row = tempRow;
            col = tempCol;
        }

        path.put(new int[]{row, col}, true);
        List<int[]> pathList = new ArrayList<>(path.keySet());
        bestPaths = new ArrayList<>(path.keySet());
        Collections.reverse(pathList);
    }

    private void aStarSearch(int[] src, int[] dest) {
        if (!isValid(src[0], src[1]) || !isValid(dest[0], dest[1])) {
            System.out.println("Source or destination is invalid");
            return;
        }

        if (!isUnBlocked(src[0], src[1]) || !isUnBlocked(dest[0], dest[1])) {
            System.out.println("Source or the destination is blocked");
            return;
        }

        if (isDestination(src[0], src[1], dest)) {
            System.out.println("We are already at the destination");
            return;
        }

        closedList = new boolean[ROW][COL];
        cellDetails = new Cell[ROW][COL];

        // Set infinite weights on all points
        fillCellValues();

        // Set the starting point values
        int i = src[0], j = src[1];
        cellDetails[i][j].f = 0;
        cellDetails[i][j].g = 0;
        cellDetails[i][j].h = 0;
        cellDetails[i][j].parentI = i;
        cellDetails[i][j].parentJ = j;

        queue = new PriorityQueue<>(new NodeComparator());
        queue.add(new Node(i, j, 0));

        while (!queue.isEmpty()) {
            Node b = queue.poll();

            i = b.i;
            j = b.j;

            closedList[i][j] = true;

            double gNew = 0, hNew = 0, fNew = 0;

            // 1st Successor (North)
            if (isValid(i - 1, j)) {
                findNextStep(i, i - 1, j, j, dest, fNew, gNew, hNew);
            }

            // 2nd Successor (South)
            if (isValid(i + 1, j)) {
                findNextStep(i, i + 1, j, j, dest, fNew, gNew, hNew);
            }

            // 3rd Successor (East)
            if (isValid(i, j + 1)) {
                findNextStep(i, i, j, j + 1, dest, fNew, gNew, hNew);
            }

            // 4th Successor (West)
            if (isValid(i, j - 1)) {
                findNextStep(i, i, j, j - 1, dest, fNew, gNew, hNew);
            }

        }
    }

    public void fillCellValues() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                cellDetails[i][j] = new Cell();
                cellDetails[i][j].f = Double.POSITIVE_INFINITY;
                cellDetails[i][j].g = Double.POSITIVE_INFINITY;
                cellDetails[i][j].h = Double.POSITIVE_INFINITY;
                cellDetails[i][j].parentI = -1;
                cellDetails[i][j].parentJ = -1;
            }
        }
    }

    private void findNextStep(int i, int iNew, int j, int jNew, int[] dest, double fNew, double gNew, double hNew) {
        if (isDestination(iNew, jNew, dest)) {
            completePath(i, iNew, j, jNew, dest);
            return;
        } else if (!closedList[iNew][jNew] && isUnBlocked(iNew, jNew)) { //
            gNew = cellDetails[i][j].g + 1;
            hNew = calculateHValue(iNew, jNew, dest);
            fNew = gNew + hNew;

            if (cellDetails[iNew][jNew].f == Double.POSITIVE_INFINITY || cellDetails[iNew][jNew].f > fNew) {
                updateAdjValues(i, j, iNew, jNew, fNew, gNew, hNew);
            }
        }
    }

    private void completePath(int i, int iNew, int j, int jNew, int[] dest) {
        cellDetails[iNew][jNew].parentI = i;
        cellDetails[iNew][jNew].parentJ = j;
        tracePath(dest);
    }

    private void updateAdjValues(int i, int j, int newI, int newJ, double fNew, double gNew, double hNew) {
        queue.add(new Node(newI, newJ, fNew));
        cellDetails[newI][newJ].f = fNew;
        cellDetails[newI][newJ].g = gNew;
        cellDetails[newI][newJ].h = hNew;
        cellDetails[newI][newJ].parentI = i;
        cellDetails[newI][newJ].parentJ = j;
    }
    public static void main(String[] args) {
        // Description of the Grid-
        // 1--> The cell is not blocked
        // 0--> The cell is blocked
        int[][] grid = {
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 1, 1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1, 1, 0, 1, 0, 1},
                {0, 0, 1, 0, 1, 0, 0, 0, 0, 1},
                {1, 1, 1, 0, 1, 1, 1, 0, 1, 0},
                {1, 0, 1, 1, 1, 1, 0, 1, 0, 0},
                {1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 1, 0, 0, 1}
        };

        // Source is the left-most bottom-most corner
        int[] src = {0, 8};

        // Destination is the left-most top-most corner
        int[] dest = {0, 0};

        AlienSearch a1 = new AlienSearch(grid);
        a1.runAStar(src, dest);
    }
}
