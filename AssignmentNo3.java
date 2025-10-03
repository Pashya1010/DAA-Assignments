import java.util.Arrays;

public class FractionalKnapsack {
    public static void main(String[] args) {
        int[] val = {60, 100, 120};
        int[] wt = {10, 20, 30};
        int W = 50;

        // Create an array for item indices and their value-to-weight ratio
        double[][] ratio = new double[val.length][2];
        for (int i = 0; i < val.length; i++) {
            ratio[i][0] = i; // store index
            ratio[i][1] = (double) val[i] / wt[i]; // store ratio
        }

        // Sort items by ratio in descending order
        Arrays.sort(ratio, (a, b) -> Double.compare(b[1], a[1]));

        double finalValue = 0.0;
        for (double[] r : ratio) {
            int idx = (int) r[0];
            if (W >= wt[idx]) {
                finalValue += val[idx];
                W -= wt[idx];
            } else {
                // Take the fractional part of the remaining capacity
                finalValue += r[1] * W;
                break; // no more capacity left
            }
        }

        System.out.printf("Maximum value: %.2f\n", finalValue);
    }
}
