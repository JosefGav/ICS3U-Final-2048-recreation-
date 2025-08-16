/**
 * Class contains useful utilities for my game.
 * @author Josef Gavronskiy (2023-12-28 -> 2024-01-21) for ICS3U course
 * */

public class Utilities {
	/**
	 * Postcondition: Returns the number of digits in a number
	 * */
    public static int intLen(int num) {
        String numAsStr = num+"";

        int len = numAsStr.length();

        return len;
    }

    /**
     * Precondition: base and argument cannot violate the rules of exponent laws. ex: log2(-4) does not exist.
     * Postcondition: Takes the base and argument of the logarithm and returns the exponent. (base^exponent = argument)
     * */
    public static double log(double base, double arg) {
        return Math.log(arg) / Math.log(base);
    }

    /**
     * Precondition: arrays must be of the same length
     * Post-condition: returns true if the arrays are the same otherwise returns false
     * */
    public static boolean compareIntegerArrays(int[] arr1, int[] arr2) {
        if (arr1.length != arr2.length) return false;

        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i]!=arr2[i]) return false;
        }
        return true;
    }
    
    
    /**
     * Postcondition: Takes a column within a 2D array. And converts it to a 1D array representing the column
     * Ex:
     * 1 , 2 , 3 -> column 0 -> {1,4}
     * 4 , 5 , 6 
     * */
    public static int[] getColumn(int[][] arr2D,int col) {
        int[] out = new int[arr2D.length];

        for (int r = 0; r<arr2D.length;r++) {
            out[r]=arr2D[r][col];
        }

        return out;
    }
}
