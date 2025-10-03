import java.util.*;

public class SmartTraffic {
    static class Edge {
        int to, weight;
        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    static class Graph {
        int V; // number of intersections
        List<List<Edge>> adj;

        Graph(int V) {
            this.V = V;
            adj = new ArrayList<>();
            for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        }

        void addEdge(int u, int v, int w) {
            adj.get(u).add(new Edge(v, w));
            adj.get(v).add(new Edge(u, w)); // bidirectional road
        }

        // Update weight of a road (simulate traffic change)
        void updateEdge(int u, int v, int newW) {
            for (Edge e : adj.get(u)) if (e.to == v) e.weight = newW;
            for (Edge e : adj.get(v)) if (e.to == u) e.weight = newW;
        }

        // Dijkstra algorithm
        List<Integer> dijkstra(int src, Set<Integer> hospitals) {
            int[] dist = new int[V];
            int[] parent = new int[V];
            Arrays.fill(dist, Integer.MAX_VALUE);
            Arrays.fill(parent, -1);

            dist[src] = 0;
            PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
            pq.add(new int[]{src, 0});

            while (!pq.isEmpty()) {
                int[] top = pq.poll();
                int u = top[0];

                // If we reached a hospital, stop early
                if (hospitals.contains(u)) {
                    return reconstructPath(src, u, parent);
                }

                for (Edge e : adj.get(u)) {
                    int v = e.to, w = e.weight;
                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                        pq.add(new int[]{v, dist[v]});
                    }
                }
            }
            return new ArrayList<>(); // no path
        }

        private List<Integer> reconstructPath(int src, int dest, int[] parent) {
            LinkedList<Integer> path = new LinkedList<>();
            for (int v = dest; v != -1; v = parent[v]) path.addFirst(v);
            return path;
        }
    }

    public static void main(String[] args) {
        // Example city graph
        Graph g = new Graph(6);

        g.addEdge(0, 1, 2);
        g.addEdge(1, 2, 3);
        g.addEdge(0, 3, 6);
        g.addEdge(2, 3, 1);
        g.addEdge(2, 4, 5);
        g.addEdge(3, 5, 2);

        int ambulance = 0; // ambulance at intersection 0
        Set<Integer> hospitals = new HashSet<>(Arrays.asList(4, 5)); // hospitals at nodes 4, 5

        // First shortest path
        List<Integer> path = g.dijkstra(ambulance, hospitals);
        System.out.println("Shortest path to hospital: " + path);

        // Update traffic (increase weight of road 2-4)
        g.updateEdge(2, 4, 20);

        // Recalculate after traffic change
        List<Integer> newPath = g.dijkstra(ambulance, hospitals);
        System.out.println("New shortest path after traffic update: " + newPath);
    }
}
