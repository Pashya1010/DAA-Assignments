import java.util.*;
public class MultistageRouting {

    static class Edge {
        final int to;
        double weight;
        Edge(int to, double weight) { this.to = to; this.weight = weight; }
    }

    private final int V;                     // number of nodes
    private final int numStages;             // number of stages
    private final int[] stageOf;             // stageOf[node] = stage index
    private final List<List<Edge>> adj;      // adjacency list

    public MultistageRouting(int V, int numStages, int[] stageOf) {
        if (stageOf.length != V) throw new IllegalArgumentException("stageOf length must be V");
        this.V = V;
        this.numStages = numStages;
        this.stageOf = Arrays.copyOf(stageOf, stageOf.length);
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
    }

    // add directed edge u -> v with weight w
    public void addEdge(int u, int v, double w) {
        if (u < 0 || u >= V || v < 0 || v >= V) throw new IndexOutOfBoundsException();
        // disallow edges that go backward in stages (optional)
        if (stageOf[v] < stageOf[u]) {
            // allow if you want but note DP assumes forward edges (can handle skip > 1)
            // Here we still allow but will ignore backward edges in DP for the "must move forward" rule.
        }
        adj.get(u).add(new Edge(v, w));
    }

    // update an existing edge u->v weight if found (first edge matched)
    public boolean updateEdgeWeight(int u, int v, double newW) {
        for (Edge e : adj.get(u)) {
            if (e.to == v) {
                e.weight = newW;
                return true;
            }
        }
        return false;
    }

    /** Request data structure for batch processing */
    public static class Request {
        public final int source;    // starting node (should be in stage 0 or some stage)
        public final int destination; // destination node (should be in final stage or later)
        public Request(int s, int d) { this.source = s; this.destination = d; }
    }

    /** Result structure */
    public static class Result {
        public final double cost;
        public final List<Integer> path; // empty if unreachable
        public Result(double cost, List<Integer> path) { this.cost = cost; this.path = path; }
        public String toString() {
            return "cost=" + cost + ", path=" + path;
        }
    }

    /**
     * Core DP solver for one request.
     * Assumes we must move forward through stages; edges that go to a stage <= current stage are ignored.
     * Handles edges that jump more than one stage (skipping allowed).
     */
    public Result findOptimalPathDP(int source, int destination) {
        final double INF = Double.POSITIVE_INFINITY;
        double[] dist = new double[V];
        int[] parent = new int[V];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        dist[source] = 0.0;

        // Build nodes-by-stage lists to iterate stages in order
        List<List<Integer>> nodesInStage = new ArrayList<>(numStages);
        for (int i = 0; i < numStages; i++) nodesInStage.add(new ArrayList<>());
        for (int node = 0; node < V; node++) {
            int s = stageOf[node];
            if (s >= 0 && s < numStages) nodesInStage.get(s).add(node);
        }

        int sourceStage = stageOf[source];
        int destStage = stageOf[destination];

        // iterate stages from sourceStage to destStage (inclusive)
        for (int s = sourceStage; s <= destStage; s++) {
            for (int u : nodesInStage.get(s)) {
                if (dist[u] == INF) continue; // unreachable so far
                // relax all outgoing edges that go to a later stage (or same stage if allowed)
                for (Edge e : adj.get(u)) {
                    int v = e.to;
                    int vStage = stageOf[v];
                    // enforce stage progression: allow only vStage > s (strict) OR vStage >= s if same-stage moves allowed
                    if (vStage < s) continue; // skip backward moves
                    double nd = dist[u] + e.weight;
                    if (nd < dist[v]) {
                        dist[v] = nd;
                        parent[v] = u;
                    }
                }
            }
            // optional early exit: if destination reached and none of its predecessors from earlier stages can improve it further,
            // But simplest is to continue until destStage to ensure optimal
        }

        if (dist[destination] == INF) return new Result(INF, Collections.emptyList());

        // reconstruct path
        LinkedList<Integer> path = new LinkedList<>();
        for (int cur = destination; cur != -1; cur = parent[cur]) path.addFirst(cur);
        return new Result(dist[destination], path);
    }

    /**
     * Batch processing: process multiple requests and return results.
     * This simple implementation runs DP per request (works fine for thousands).
     * For many requests sharing the same source or same stage structure, consider reusing computations.
     */
    public List<Result> batchProcess(List<Request> requests) {
        List<Result> out = new ArrayList<>(requests.size());
        for (Request r : requests) {
            out.add(findOptimalPathDP(r.source, r.destination));
        }
        return out;
    }

    // --- Example usage and small demo ---
    public static void main(String[] args) {
        // Example: 8 nodes across 4 stages (0..3)
        // stageOf: node -> stage
        int V = 8;
        int numStages = 4;
        int[] stageOf = new int[] {
            0, // 0 in stage 0 (warehouses)
            0, // 1 in stage 0
            1, // 2 in stage 1 (transit hub)
            1, // 3 in stage 1
            2, // 4 in stage 2
            2, // 5 in stage 2
            3, // 6 in stage 3 (final delivery)
            3  // 7 in stage 3
        };

        MultistageRouting mr = new MultistageRouting(V, numStages, stageOf);

        // Add directed edges (u -> v, weight)
        mr.addEdge(0, 2, 5);
        mr.addEdge(0, 3, 2);
        mr.addEdge(1, 2, 6);
        mr.addEdge(1, 3, 3);
        mr.addEdge(2, 4, 4);
        mr.addEdge(3, 4, 10);
        mr.addEdge(3, 5, 2);
        mr.addEdge(4, 6, 3);
        mr.addEdge(5, 7, 5);
        mr.addEdge(4, 7, 12);

        // Request: from node 0 (stage0) to node 6 (stage3)
        Request r1 = new Request(0, 6);
        Result res1 = mr.findOptimalPathDP(r1.source, r1.destination);
        System.out.println("Result1: " + res1);

        // Batch requests
        List<Request> batch = Arrays.asList(new Request(1,6), new Request(0,7), new Request(1,7));
        List<Result> batchRes = mr.batchProcess(batch);
        System.out.println("Batch results:");
        for (Result r : batchRes) System.out.println("  " + r);

        // Simulate a real-time update: road 3->5 becomes heavy (increase cost)
        System.out.println("Updating edge 3->5 to weight 20 (traffic).");
        mr.updateEdgeWeight(3, 5, 20);
        Result resAfter = mr.findOptimalPathDP(0, 6);
        System.out.println("After update: " + resAfter);
    }
}
