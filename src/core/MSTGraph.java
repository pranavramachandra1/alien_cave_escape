package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class MSTGraph extends GameGraph {
    private int[] mst;
    public MSTGraph(int v) {
        super(v);
        mst = createMST();
    }
    public int[] createMST() {

        ArrayList<Edge> edges = this.getEdges();
        int numV = this.getNumV();

        int[] parent = new int[numV]; // Stores the MST
        boolean[] inMST = new boolean[numV]; // Tracks vertices included in the MST
        int[] key = new int[numV]; // Key values used to pick minimum weight edge in the cut

        // Initialize all keys as infinite, parent as -1, and inMST as false
        Arrays.fill(key, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        Arrays.fill(inMST, false);

        // Priority queue supports vertex index and key value
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(index -> key[index]));
        // Start with the first vertex
        key[0] = 0;
        pq.add(0); // Add vertex index 0 to the priority queue

        while (!pq.isEmpty()) {
            // Extract the vertex with the minimum key value
            int u = pq.poll(); // This vertex is now part of MST
            inMST[u] = true; // Mark vertex as included in MST

            // Iterate over all vertices adjacent to u and update their keys
            for (Edge edge : edges) {
                // Consider edge from u to v
                int v;
                if (edge.n1 == u) {
                    v = edge.n2;
                } else if (edge.n2 == u) {
                    v = edge.n1;
                } else {
                    v = -1; // Indicates the edge does not connect with vertex u
                }

                if (v != -1 && !inMST[v] && edge.weight < key[v]) {
                    // Update parent and key value
                    parent[v] = u;
                    key[v] = edge.weight;
                    pq.add(v);
                }
            }
        }
        return parent;
    }

    public ArrayList<Integer> compareAllEdges() {
        ArrayList<Integer> arr = new ArrayList<>();
        ArrayList<Edge> edges = this.getEdges();
        for (Edge e : edges) {
            arr.add(e.n1);
            arr.add(e.n2);
            arr.add(e.weight);
        }
        return arr;
    }
}
