import java.util.Comparator;

public class MovieQuickSort {

    public static void quickSort(Movie[] arr, int low, int high, Comparator<Movie> comparator) {
        if (low < high) {
            int pi = partition(arr, low, high, comparator);
            quickSort(arr, low, pi - 1, comparator);
            quickSort(arr, pi + 1, high, comparator);
        }
    }

    private static int partition(Movie[] arr, int low, int high, Comparator<Movie> comparator) {
        Movie pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparator.compare(arr[j], pivot) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(Movie[] arr, int i, int j) {
        Movie temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        Movie[] movies = {
            new Movie("Inception", 8.8, 2010, 10000),
            new Movie("The Matrix", 8.7, 1999, 12000),
            new Movie("Interstellar", 8.6, 2014, 15000),
            new Movie("Avengers", 8.0, 2012, 20000),
            new Movie("Parasite", 8.6, 2019, 8000)
        };

        // Example: sort by IMDB rating descending
        Comparator<Movie> byRatingDesc = (m1, m2) -> Double.compare(m2.imdbRating, m1.imdbRating);

        quickSort(movies, 0, movies.length - 1, byRatingDesc);

        System.out.println("Sorted by IMDB Rating (desc):");
        for (Movie m : movies) System.out.println(m);

        Comparator<Movie> byYearAsc = Comparator.comparingInt(m -> m.releaseYear);
        quickSort(movies, 0, movies.length - 1, byYearAsc);

        System.out.println("\nSorted by Release Year (asc):");
        for (Movie m : movies) System.out.println(m);

        Comparator<Movie> byPopularityDesc = (m1, m2) -> Integer.compare(m2.watchTimePopularity, m1.watchTimePopularity);
        quickSort(movies, 0, movies.length - 1, byPopularityDesc);

        System.out.println("\nSorted by Popularity (desc):");
        for (Movie m : movies) System.out.println(m);
    }
}
