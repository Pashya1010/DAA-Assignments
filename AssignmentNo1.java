class CustomerOrder {
    long timestamp; // e.g., epoch milliseconds
    String orderId; // or any other relevant fields

    public CustomerOrder(long timestamp, String orderId) {
        this.timestamp = timestamp;
        this.orderId = orderId;
    }

    public String toString() {
        return "OrderID: " + orderId + ", Timestamp: " + timestamp;
    }
}
class OrderSorter {

    public static void mergeSort(CustomerOrder[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private static void merge(CustomerOrder[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        CustomerOrder[] leftArray = new CustomerOrder[n1];
        CustomerOrder[] rightArray = new CustomerOrder[n2];

        for (int i = 0; i < n1; i++)
            leftArray[i] = arr[left + i];
        for (int j = 0; j < n2; j++)
            rightArray[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (leftArray[i].timestamp <= rightArray[j].timestamp) {
                arr[k++] = leftArray[i++];
            } else {
                arr[k++] = rightArray[j++];
            }
        }

        while (i < n1)
            arr[k++] = leftArray[i++];
        while (j < n2)
            arr[k++] = rightArray[j++];
    }

    public static void printOrders(CustomerOrder[] arr) {
        for (CustomerOrder order : arr)
            System.out.println(order);
    }
}
public class AssignmentNo1 {
    public static void main(String[] args) {
        CustomerOrder[] orders = {
            new CustomerOrder(1633036800000L, "ORD001"),
            new CustomerOrder(1633123200000L, "ORD002"),
            new CustomerOrder(1633209600000L, "ORD003"),
            new CustomerOrder(1632950400000L, "ORD004")
        };

        OrderSorter.mergeSort(orders, 0, orders.length - 1);

        System.out.println("Sorted Orders by Timestamp:");
        OrderSorter.printOrders(orders);
    }
}
