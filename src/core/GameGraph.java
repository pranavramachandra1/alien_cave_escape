package core;

import java.util.ArrayList;


public class GameGraph {
    class Edge {
        int n1;
        int n2;
        int weight;

        private Edge(int node1, int node2, int w) {
            n1 = node1;
            n2 = node2;
            weight = w;
        }
    }

    private ArrayList<Edge> edges;
    private int numV;

    private int[] mst;

    public GameGraph(int v) {
        edges = new ArrayList<>();
        numV = v;
    }

    public void addEdge(int n1, int n2, int weight) {
        edges.add(new Edge(n1, n2, weight));
    }

    protected ArrayList<Edge> getEdges() {
        return edges;
    }

    protected int getNumV() {
        return numV;
    }

}
