import java.util.*;

public class DirectedWeightedGraph<T> {
    private Map<T, Map<T, Double>> graph;

    public DirectedWeightedGraph() {
        this.graph = new HashMap<>();
    }

    public void addVertex(T vertex) {
        if (!graph.containsKey(vertex)) {
            graph.put(vertex, new HashMap<>());
        }
    }

    public void addEdge(T source, T target, double weight) {
        if (!graph.containsKey(source) || !graph.containsKey(target)) {
            throw new IllegalArgumentException("Vertex does not exist in the graph.");
        }

        graph.get(source).put(target, weight);
    }

    public double getEdgeWeight(T source, T target) {
        if (!graph.containsKey(source) || !graph.containsKey(target)) {
            throw new IllegalArgumentException("Vertex does not exist in the graph.");
        }

        Map<T, Double> outgoingEdges = graph.get(source);
        if (outgoingEdges.containsKey(target)) {
            return outgoingEdges.get(target);
        } else {
            throw new IllegalArgumentException("Edge does not exist in the graph.");
        }
    }

    public Set<T> getVertices() {
        return graph.keySet();
    }

    public Set<T> getNeighbors(T vertex) {
        if (!graph.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex does not exist in the graph.");
        }

        return graph.get(vertex).keySet();
    }

    public boolean hasVertex(T vertex) {
        return graph.containsKey(vertex);
    }

    public boolean hasEdge(T source, T target) {
        if (!graph.containsKey(source)) {
            return false;
        }

        Map<T, Double> outgoingEdges = graph.get(source);
        return outgoingEdges.containsKey(target);
    }

    public int getVertexCount() {
        return graph.size();
    }

    public int getEdgeCount() {
        int count = 0;
        for (Map<T, Double> outgoingEdges : graph.values()) {
            count += outgoingEdges.size();
        }
        return count;
    }
}
