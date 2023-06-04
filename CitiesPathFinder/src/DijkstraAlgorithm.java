import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DijkstraAlgorithm {

    final static String DIR = System.getProperty("user.dir");
    final static String FILE_PATH = DIR + "\\src\\cities.txt";

    public static void main(String[] args) {
//        DirectedWeightedGraph<Character> graph = readGraphFromFile(FILE_PATH);
//
//        Set<Character> vertexSet = graph.getVertices();
//        for (char location : vertexSet) {
//            System.out.println("The starting point location is " + location);
//            System.out.println("The routes from location " + location + " to the rest of the locations are:");
//
//            Map<Character, Double> shortestPaths = calculateShortestPaths(graph, location);
//
//            for (char destination : vertexSet) {
//                if (shortestPaths.isEmpty())
//                    break;
//                List<Character> path = getPath(graph, location, destination, shortestPaths);
//                double distance = shortestPaths.get(destination);
//                System.out.println("loc. " + location + " to loc. " + destination +
//                        ": " + pathToString(path) + " --- route length: " + distance);
//            }
//
//            System.out.println();

        int[] valuesOfN = {5000, 10000, 15000, 20000, 25000, 30000};
        Map<Integer, Long> times = new HashMap<>();
        Map<Integer, Double> theoreticalTimes = new HashMap<>();

        for (int n : valuesOfN) {
            int m = 2 * n;

            DirectedWeightedGraph<Character> graph = makeGraph(n, m);

            long startTime = System.currentTimeMillis();

            calculateShortestPaths(graph, 'A');

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            double expectedTimeEfficiency = (n + m) * Math.log(n) * 1000;

            times.put(n, elapsedTime);
            theoreticalTimes.put(n, expectedTimeEfficiency);

            System.out.printf("n = %d, Running time = %dms, Expected theoretical time efficiency = %.2fms\n", n, elapsedTime, expectedTimeEfficiency);
        }

        System.out.println("\nRatio of running time\tRatio of expected time efficiency");
        for (int i = 1; i < valuesOfN.length; i++) {
            int prevN = valuesOfN[i - 1];
            int currN = valuesOfN[i];

            long prevTime = times.get(prevN);
            long currTime = times.get(currN);

            double prevTheoreticalTime = theoreticalTimes.get(prevN);
            double currTheoreticalTime = theoreticalTimes.get(currN);

            double runningTimeRatio = (double) currTime / prevTime;
            double expectedTimeEfficiencyRatio = currTheoreticalTime / prevTheoreticalTime;

            System.out.printf("Time of %d / Time of %d: %.2f\n", currN, prevN, runningTimeRatio);
            System.out.printf("Time of %d / Time of %d: %.2f\n", currN, prevN, expectedTimeEfficiencyRatio);
        }


//        n = 5000, Running time = 44ms, Expected theoretical time efficiency = 127757897.87ms
//        n = 10000, Running time = 33ms, Expected theoretical time efficiency = 276310211.16ms
//        n = 15000, Running time = 5ms, Expected theoretical time efficiency = 432711246.60ms
//        n = 20000, Running time = 33ms, Expected theoretical time efficiency = 594209253.15ms
//        n = 25000, Running time = 43ms, Expected theoretical time efficiency = 759497332.79ms
//        n = 30000, Running time = 6ms, Expected theoretical time efficiency = 927805739.46ms

    }

    public static Map<Character, Double> calculateShortestPaths(DirectedWeightedGraph<Character> graph, char startVertex) {
        Map<Character, Double> distances = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Set<Character> visited = new HashSet<>();

        for (char vertex : graph.getVertices()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.put(startVertex, 0.0);
        pq.offer(new Node(startVertex, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            char currVertex = current.getVertex();

            if (visited.contains(currVertex)) {
                continue; // Skip if already visited
            }

            visited.add(currVertex);

            for (char neighbor : graph.getNeighbors(currVertex)) {
                double weight = graph.getEdgeWeight(currVertex, neighbor);
                double newDistance = distances.get(currVertex) + weight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    pq.offer(new Node(neighbor, newDistance));
                }
            }
        }

        return distances;
    }

    public static List<Character> getPath(DirectedWeightedGraph<Character> graph, char startVertex, char endVertex, Map<Character, Double> distances) {
        List<Character> path = new ArrayList<>();
        char currentVertex = endVertex;

        while (currentVertex != startVertex) {
            path.add(currentVertex);
            Set<Character> outgoingEdges = graph.getNeighbors(currentVertex);
            double shortestDistance = distances.get(currentVertex);
            boolean pathFound = false;

            for (char target : outgoingEdges) {
                double weight = graph.getEdgeWeight(currentVertex, target);
                double prevDistance = distances.get(target);

                if (Math.abs(prevDistance + weight - shortestDistance) < 1e-6) {
                    currentVertex = target;
                    pathFound = true;
                    break;
                }
            }

            if (!pathFound) {
                // No valid path exists, return an empty path
                return Collections.emptyList();
            }

            if (distances.get(currentVertex) > shortestDistance) {
                // Distance to currentVertex is greater than the shortest distance, terminate
                return Collections.emptyList();
            }
        }

        Collections.reverse(path);
        return path;
    }


    public static DirectedWeightedGraph<Character> readGraphFromFile(String filename) {
        DirectedWeightedGraph<Character> graph = new DirectedWeightedGraph<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                char source = parts[0].charAt(0);
                char destination = parts[1].charAt(0);
                double weight = Double.parseDouble(parts[2]);

                graph.addVertex(source);
                graph.addVertex(destination);
                graph.addEdge(source, destination, weight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    static class Node implements Comparable<Node> {
        private final char vertex;
        private final double distance;

        public Node(char vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        public char getVertex() {
            return vertex;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    public static String pathToString(List<Character> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append("city ").append(path.get(i));
            if (i < path.size() - 1) {
                sb.append(" – ");
            }
        }
        return sb.toString();
    }

    public static DirectedWeightedGraph<Character> makeGraph(int n, int m) {
        DirectedWeightedGraph<Character> graph = new DirectedWeightedGraph<>();
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            char vertex = (char) ('A' + i); // Generate vertex labels from A to n
            graph.addVertex(vertex);
        }

        int count = 0;
        while (count < m) {
            char source = (char) ('A' + random.nextInt(n)); // Random vertex as source
            char target = (char) ('A' + random.nextInt(n)); // Random vertex as target
            double weight = random.nextInt(100) + 1; // Random weight between 1 and 100

            if (!graph.hasEdge(source, target)) {
                graph.addEdge(source, target, weight);
                count++;
            }
        }

        return graph;
    }

}
