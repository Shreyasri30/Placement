package day_3;
import java.util.Arrays;

public class arraymethods {
    public static void main(String[] args) {

        int[] arr = {5, 2, 8, 1, 9};

       
        System.out.println("1. toString(): " + Arrays.toString(arr));

       
        Arrays.sort(arr);
        System.out.println("2. sort(): " + Arrays.toString(arr));

       
        int[] copy1 = Arrays.copyOf(arr, 7);
        System.out.println("3. copyOf(): " + Arrays.toString(copy1));

       
        int[] copy2 = Arrays.copyOfRange(arr, 1, 4);
        System.out.println("4. copyOfRange(): " + Arrays.toString(copy2));

        
        int[] fillArr = new int[5];
        Arrays.fill(fillArr, 100);
        System.out.println("5. fill(): " + Arrays.toString(fillArr));

       
        int[] arr2 = {1, 2, 5, 8, 9};
        System.out.println("6. equals(): " + Arrays.equals(arr, arr2));

        
        int index = Arrays.binarySearch(arr, 8);
        System.out.println("7. binarySearch() for 8: index = " + index);

        
        int[][] twoD = {{1, 2}, {3, 4}};
        System.out.println("8. deepToString(): " + Arrays.deepToString(twoD));

      
        int[] pArr = {9, 4, 6, 2, 1};
        Arrays.parallelSort(pArr);
        System.out.println("9. parallelSort(): " + Arrays.toString(pArr));

       
        int mismatchIndex = Arrays.mismatch(arr, arr2);
        System.out.println("10. mismatch(): " + mismatchIndex);
    }
}
