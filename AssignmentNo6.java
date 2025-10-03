import java.util.*;

public class ReliefKnapsack {

    public static int knapsackDP(int[] weight, int[] utility, int W) {
        int N = weight.length;
        int[][] dp = new int[N+1][W+1];

        // Build DP table
        for (int i = 1; i <= N; i++) {
            for (int w = 0; w <= W; w++) {
                if (weight[i-1] <= w) {
                    dp[i][w] = Math.max(dp[i-1][w], dp[i-1][w - weight[i-1]] + utility[i-1]);
                } else {
                    dp[i][w] = dp[i-1][w];
                }
            }
        }

        // Optional: reconstruct items chosen
        List<Integer> chosenItems = new ArrayList<>();
        int w = W;
        for (int i = N; i > 0; i--) {
            if (dp[i][w] != dp[i-1][w]) {
                chosenItems.add(i-1); // item i-1 included
                w -= weight[i-1];
            }
        }

        System.out.println("Items selected: " + chosenItems);
        return dp[N][W];
    }

    public static void main(String[] args) {
        int[] weight = {10, 20, 30, 5};
        int[] utility = {60, 100, 120, 50};
        int W = 50;

        int maxUtility = knapsackDP(weight, utility, W);
        System.out.println("Maximum utility achievable: " + maxUtility);
    }
}
